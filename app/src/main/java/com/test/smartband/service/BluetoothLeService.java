package com.test.smartband.service;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.test.smartband.activity.LocationData;
import com.test.smartband.activity.MyLocation;
import com.test.smartband.model.BandData;
import com.test.smartband.model.BandDataDB;
import com.test.smartband.other.LocalUserInfo;
import com.test.smartband.tools.VibratorUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 蓝牙服务
 */
public class BluetoothLeService extends Service  {

    private int i = 0;


    public static final String AIRCONDITION_NAME = "AirAirAir";
    public static final String BAND_NAME = "DA14583";

    public static final String ACTION_GATT_SCANNING =
            "com.smartband.ble.ACTION_GATT_SCANNING";
    public final static String ACTION_GATT_CONNECTED =
            "com.smartband.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.smartband.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.smartband.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.smartband.ble.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.smartband.ble.EXTRA_DATA";
    public final static String ACTION_BANDDATA =
            "com.smartband.ble.BANDDATA";



    //通知消息
    public static final String TAG = "*==BleService==*";


    //连接状态
    private int mConnectionState = STATE_DISCONNECTED;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static boolean ISRECONNECT = false;

    //读不同数据
    public static final int POWER = 1;
    public static final int CALORIE = 2;
    public static final int STEP = 3;
    public static final int TEMP = 4;
    public static final int TERMINAL = 5;

    //不同空调控制指令
    public static final int MINUS = 1;
    public static final int PLUS = 2;
    public static final int SWITCH = 3;
    public static final int MODE = 4;
    public static final int SPEED = 5;
    public static final int DIRECTION = 6;
    public static final int COMFORT = 7;

    public static final UUID CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID KEY_SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_LEVEL_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    public static final UUID KEY_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public static final UUID PASSWORD_UUID = UUID.fromString("00002a08-0000-1000-8000-00805f9b34fb");

    public static final UUID DATA_SERVICE_UUID = UUID.fromString("0000FFF0-0000-1000-8000-00805f9b34fb");
    public static final UUID LEVEL_UUID = UUID.fromString("0000FFF3-0000-1000-8000-00805f9b34fb");
    public static final UUID TEMP_UUID = UUID.fromString("0000FFF4-0000-1000-8000-00805f9b34fb");
    public static final UUID HUMIDITY_TEMP_UUID = UUID.fromString("0000FFF6-0000-1000-8000-00805f9b34fb");
    public static final UUID CALORIE_UUID = UUID.fromString("0000FFF8-0000-1000-8000-00805f9b34fb");
    public static final UUID STEP_UUID = UUID.fromString("0000FFF7-0000-1000-8000-00805f9b34fb");
    public static final UUID AIR_CONTROL_UUID = UUID.fromString("0000FFF1-0000-1000-8000-00805f9b34fb");

    //防丢器UUID
    public static final UUID LINK_LOSS_UUID = UUID.fromString("00001803-0000-1000-8000-00805f9b34fb");
    public static final UUID ALERT_LEVEL_UUID = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_POWER_UUID = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_POWER_LEVEL_UUID = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb");
    public static final UUID IMMEDIATE_ALERT_UUID = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");

    //键值
    public static final String KEY_LINKEDADDR = "KEY_LINKEDADDR";

    //创建该队列是为了在写数据时防止一个数据还没写完，又开始写下一个数据，这样无法向远程端写数据
    private static final Queue<Object> sWriteQueue = new ConcurrentLinkedQueue<Object>();
    //所以将写命令一个个写入队列中一个个执行
    private static boolean sIsWriting = false;

    private int action = 0;

    //蓝牙通信动作控制
    final static int ACT_TIMEOUT = 100;
    final static int ACT_SCAN = 101;
    final static int ACT_DIS = 102;

    private Messenger mMessenger;
    private List<Messenger> mClientsMsger = new LinkedList<Messenger>();
    private Map<String, BluetoothDevice> mDevices = new HashMap<String, BluetoothDevice>();

    //定位相关
    public MyLocation myLocation;//定位类
    public LocationData handleData;//处理定位数据的类
    private SharedPreferences preference;//用来存储定位数据和记录地址数。
    Editor editor;

    int rssiOld = -100;//用于判断
    static int SearchCount = 0;
    static int airCount = 0;
    static boolean isFirst = true;
    private BluetoothDevice mDevice;
    private final IBinder mBinder = new LocalBinder();
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;

    private BluetoothGatt mGatt = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * 与GATT服务器建立连接后，当BLE设备发生连接状态改变、发现服务、读取特征和特征更改等事件时，
     * 会回调以下相应的函数。
     * @创锋
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        //状态改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                /**
                 * 连接成功，立即扫描设备的GattService
                 */
                Log.e(TAG, "--->GattCallback.onConnectionStateChange(): " +
                        "设备GATT连接成功，扫描服务 discoverServices()" );
                /**
                 * 调用服务发现，这是一个异步操作，当该函数运行结束后，无论是否成功发现服务，
                 * 都会回调onServicesDiscovered()函数，若发现服务成功，则可用getService()获取
                 * @创锋
                 */
                mBluetoothGatt.discoverServices();//进行服务发现
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mConnectionState != STATE_CONNECTING ) {
                    intentAction = ACTION_GATT_DISCONNECTED;
                    mConnectionState = STATE_DISCONNECTED;
                    Log.e(TAG, "连接断开，重新搜索.");
                    broadcastUpdate(intentAction);
                }
            }
        }
        //发现服务
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //服务发现后回调
            Log.e(TAG, "--->onServicesDiscovered(发现GATT服务): " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "连接成功");
                //连接成功后改变标志位
                mConnectionState = STATE_CONNECTED;
                //成功发现服务,到这里表示已经完全连接了
                //开启服务特征值使能通知
                enableNotification();
                //设置为已连接
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //写特性值完成后调用

        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //写描述符完成后调用
            Log.e( TAG,"onDescriptorWrite： "+ ++i);
            Log.e(TAG, "onDescriptorWrite: " + status);
            sIsWriting = false;
            nextWrite();

        }
        //特征改变
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //特性值发生变化后调用
            Log.e(TAG, "onCharacteristicChanged: " + characteristic.getUuid());

            mBluetoothGatt.readRemoteRssi();
            /**
             * 2016.8.27新增
             * 蓝牙数据处理
             */
            ParseData(characteristic);

        }
        //读取特征
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {//成功读取
                /**
                 * 2016.8.27新增
                 * 数据处理
                 */
                ParseData(characteristic);
                Toast.makeText(getApplicationContext(),
                        "onCharacteristicRead(),characteristic数据读取成功", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "--->onCharacteristicRead(),characteristic数据读取成功");
            } else{
                Toast.makeText(getApplicationContext(),
                        "onCharacteristicRead(),characteristic数据读取失败", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "--->onCharacteristicRead(),characteristic数据读取失败");
            }

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {//成功读取rssi信号强度
                //进行防丢监测
                lossAlert(rssi);
                Log.e(TAG, "--->onReadRemoteRssi(),rssi信号读取成功:"+rssi);
            }else {
                Log.e(TAG, "--->onReadRemoteRssi(),rssi信号读取失败");
            }
        }
    };




    @Override
    public void onCreate() {
        //服务绑定成功后
        //存储定位数据的sharepreference
        preference = getSharedPreferences("LOCATIONDATA", MODE_PRIVATE);
        editor = preference.edit();
        //定位
        handleData = new LocationData(getApplicationContext(), preference);
        myLocation = new MyLocation(getApplicationContext(), handleData);

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        super.onCreate();
    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    /**
     * 活动绑定的时候回调用，返回结果是onServiceConnected()
     * 中的IBinder service类型
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "服务销毁", Toast.LENGTH_SHORT).show();
        close();
        super.onDestroy();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        if (mConnectionState == STATE_CONNECTED) {
            mBluetoothGatt.disconnect();
            Toast.makeText(getApplicationContext(),
                    "断开连接", Toast.LENGTH_SHORT)
                    .show();
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        Toast.makeText(getApplicationContext(),
                "释放资源", Toast.LENGTH_SHORT)
                .show();
    }



        /**
         * 此处要用新的方法去启动搜索：
         * BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
         * bluetoothLeScanner.startScan((ScanCallback) mLeScanCallback);
         * 建议者@创锋
         */

    //2016.10.20
    public boolean connectDevice(final String address) {

        Log.e(TAG,"连接设备connectDevice");
        if (mBluetoothAdapter == null || address == null) {
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        //如果已经连接过了，则重新连接蓝牙设备
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.e(TAG,"重新连接蓝牙设备");
            broadcastUpdate(ACTION_GATT_CONNECTED);
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        //通过蓝牙地址可以获得相应的蓝牙设备
        mDevice = mBluetoothAdapter.getRemoteDevice(address);
        if (mDevice == null) {
            Log.e(TAG, "没发现该设备，不能连接");
            return false;
        }
        //获得蓝牙设备Gatt
        mBluetoothGatt = mDevice.connectGatt(this, true, mGattCallback);
        Log.e(TAG,"第一次连接设备");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * 2016.10.22
     * 开启相关服务特征值使能通知
     */
    private void enableNotification() {
        if (mDevice.getName().equals(BAND_NAME)) {
            Log.e(TAG, "开启手环通知");

            List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
            String uuid = null;
            for (BluetoothGattService gattService : gattServices) {
                uuid = gattService.getUuid().toString();
                Log.e(TAG, uuid);

                if (uuid.equals(DATA_SERVICE_UUID.toString())) {
                    //开启手环服务特征通知
                    enableCharNotification(DATA_SERVICE_UUID, TEMP_UUID);//开启温度的传输使能
                } else if (uuid.equals(LINK_LOSS_UUID.toString())) {
                    enableCharNotification(LINK_LOSS_UUID, ALERT_LEVEL_UUID);//开启防丢器使能
                } else if (uuid.equals(IMMEDIATE_ALERT_UUID.toString())) {
                    enableCharNotification(IMMEDIATE_ALERT_UUID, ALERT_LEVEL_UUID);//开启防丢器使能
                    Log.e(TAG, "已开启防丢器功能");
                    // 新启动一个子线程
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // tvMessage.setText("...");
                            // 以上操作会报错，无法再子线程中访问UI组件，UI组件的属性必须在UI线程中访问
                            // 使用post方式修改UI组件tvMessage的Text属性
                            while (true)
                                mBluetoothGatt.readRemoteRssi();
                        }}).start();
                }

            }

        } else if (mDevice.getName().equals(AIRCONDITION_NAME)) {
            Log.e(TAG, "开启空调通知");
            //开启空调服务特征通知
            enableCharNotification(DATA_SERVICE_UUID, HUMIDITY_TEMP_UUID);
        }
    }

    private void enableCharNotification(final UUID serverUUID, final UUID charUUID) {

        BluetoothGattService bleGattService = mBluetoothGatt.getService(serverUUID);
        if (bleGattService == null) {
            Log.e(TAG, "Gatt中没有相关服务");
            return;
        }
        BluetoothGattCharacteristic bleGattChar = bleGattService.getCharacteristic(charUUID);
        if (bleGattChar == null) {
            Log.e(TAG, "没有char特征");
            return;
        }
        //若开启通知成功则回调onCharacteristicChanged()
        boolean result = mBluetoothGatt.setCharacteristicNotification(bleGattChar, true);
        if (!result) {
            Log.e(TAG, "开启char通知失败");
            return;
        }

        if (mDevice.getName().equals(BAND_NAME)) {
            periodDisconnect(60*1000);
        }

        BluetoothGattDescriptor clientConfig = bleGattChar.getDescriptor(CCC);//获得它的客户端描述符
        if (clientConfig == null) {
            Toast.makeText(getApplicationContext(),
                    "无客户端配置", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);//设置客户端描述符的值
        write(clientConfig);

    }

    /**
     * 防丢器监测
     * d = 10^((abs(RSSI) - A) / (10 * n))
     *
     * @param rssi
     */
    private void lossAlert(int rssi) {

        int iRssi = Math.abs(rssi);
        float power = (float) ((iRssi - 59) / (10 * 2.0));
        double distance = Math.pow(10, power);//计算距离
        Log.e("与手环距离为：", distance + "");
        if (distance >= 2.0) {
            BluetoothGattService bleGattService = mBluetoothGatt.getService(IMMEDIATE_ALERT_UUID);
            if (bleGattService == null) {
                Log.e(TAG, "Gatt中没有相关服务");
                return;
            }
            BluetoothGattCharacteristic bleGattChar = bleGattService.getCharacteristic(ALERT_LEVEL_UUID);
            if (bleGattChar == null) {
                Log.e(TAG, "没有char特征");
                return;
            }

            byte[] alertByte = {0x02};
            bleGattChar.setValue(alertByte);
            write(bleGattChar);

            long[] myp = { 2000, 2000, 2000, 2000 };
            VibratorUtil.Vibrate((Activity) getApplicationContext(), myp, false); // 震动100ms
        }

    }

    //写数据。创建一个队列顺序写数据
    private synchronized boolean write(Object o) {
        Log.e(TAG,"write开始写数据");
        if (sWriteQueue.isEmpty() && !sIsWriting) {
            if (doWrite(o))
                return true;
            else
                return false;
        } else {
            sWriteQueue.add(o);
        }

        return false;
    }

    private synchronized boolean nextWrite() {
        if (!sWriteQueue.isEmpty() && !sIsWriting) {
            if (doWrite(sWriteQueue.poll()))
                return true;
            else
                return false;
        }
        return false;
    }

    private synchronized boolean doWrite(Object o) {
        Log.e(TAG, "给设备写数据dowrite: "+ ++i);
        if (o instanceof BluetoothGattCharacteristic) {
            sIsWriting = true;
            if (mBluetoothGatt.writeCharacteristic((BluetoothGattCharacteristic) o))
                return true;
            else
                return false;
        } else if (o instanceof BluetoothGattDescriptor) {
            sIsWriting = true;
            if (mBluetoothGatt.writeDescriptor((BluetoothGattDescriptor) o))
                return true;
            else
                return false;
        } else {
            nextWrite();
        }
        return false;
    }


    /**
     * 2016.8.27新增
     * 解析从蓝牙接受到的数据
     * 解析完数据之后保存数据和广播发送数据
     * @param characteristic
     */
    private void ParseData(BluetoothGattCharacteristic characteristic) {
        byte[] value;
        value = characteristic.getValue();
        /**
         * 当连接手环时
         */
        if (mDevice.getName().equals(BAND_NAME)) {

            if (characteristic.getUuid().equals(TEMP_UUID)) {
                byte body_temp_value[] = {value[1], value[0]};//体温
                byte battery_value[] = {value[2]};//电量
                //处理电量，在手环发过来的一个字节中，第一位是1表示正在充电，0表示补充
                boolean isCharge = false;//是否正在充电
                if ((battery_value[0] & 128) == 128) {
                    isCharge = true;
                }else{
                    isCharge = false;
                }
                battery_value[0] = (byte) (battery_value[0] & 127);//将第一位置0

                byte step_value[] = {value[4], value[3]};//步数

                float body_temp = (float) ((Integer.parseInt(HEXtoString(body_temp_value), 16)) / 100.0);
                int battery = Integer.parseInt(HEXtoString(battery_value), 16);
                int step = Integer.parseInt(HEXtoString(step_value), 16);
                float calorie = calculateCalorieValue(step);
                Log.e(TAG, "读取体温数据" + body_temp);
                Log.e(TAG, "读取步数数据" + step);
                Log.e(TAG, "读取电量数据" + battery);
                operateData(body_temp, battery, isCharge, step, 0, 0, 0, "- -", calorie);
            } else if (characteristic.getUuid().equals(ALERT_LEVEL_UUID)) {
                //报警的时候做的处理

            }
        } else if (mDevice.getName().equals(AIRCONDITION_NAME)){
            //连接终端
            byte body_temp_value[] = {value[1], value[0]};//体温
            byte battery_value[] = {value[2]};//电量
            //处理电量，在手环发过来的一个字节中，第一位是1表示正在充电，0表示补充
            boolean isCharge = false;//是否正在充电
            if ((battery_value[0] & 128) == 128) {
                isCharge = true;
            }else{
                isCharge = false;
            }
            battery_value[0] = (byte) (battery_value[0] & 127);//将第一位置0

            byte step_value[] = {value[4], value[3]};//步数
            byte humidity_value[] = {value[6], value[5]};//湿度
            byte room_temp_value[] = {value[8], value[7]};//室温
            byte air_temp_value[]= {value[10], value[9]};//空调温度
            byte PMV_level_value[] = {value[12], value[11]};//人体舒适度等级

            float body_temp = (float) ((Integer.parseInt(HEXtoString(body_temp_value), 16))/100.0);//体温
            int battery = Integer.parseInt(HEXtoString(battery_value), 16);//电量
            int step = Integer.parseInt(HEXtoString(step_value), 16);//步数
            float humidity = (float) ((Integer.parseInt(HEXtoString(humidity_value),16))/10.0);//湿度
            float room_temp = (float) ((Integer.parseInt(HEXtoString(room_temp_value), 16))/10.0);//室温
            int aircondition_temp = Integer.parseInt(HEXtoString(air_temp_value), 16);//空调温度
            int pmv_level = Integer.parseInt(HEXtoString(PMV_level_value), 16);
            String pmv = calculatePMV(pmv_level);//人体舒适度等级
            float calorie = calculateCalorieValue(step);//卡路里

            Log.e(TAG, "从终端读取体温数据" + body_temp);
            Log.e(TAG, "从终端读取步数数据" + step);
            Log.e(TAG, "从终端读取电量数据" + battery);
            Log.e(TAG, "从终端读取湿度数据" + humidity);
            Log.e(TAG, "从终端读取舒适度等级数据" + pmv);
            Log.e(TAG, "从终端读取温度数据" + aircondition_temp);

            operateData(body_temp, battery, isCharge, step, humidity, room_temp, aircondition_temp, pmv, calorie);
        }

    }
    /**
     * 计算人体舒适度
     */
    private String calculatePMV(int pmv_level_value) {
        String pmv = "";
        if (pmv_level_value >= 32767) {//传过来的值是负数
            int pmv_int = 65536- pmv_level_value;
            if (pmv_int <= 5) {
                pmv = "舒适";
            } else if (pmv_int <= 15) {
                pmv = "较冷";
            } else {
                pmv ="冷";
            }
        } else {
            if (pmv_level_value <= 5) {
                pmv = "舒适";
            } else if (pmv_level_value <= 15) {
                pmv = "较热";
            } else {
                pmv = "热";
            }
        }
        return pmv;
    }

    /**
     * 计算卡路里
     */
    private float calculateCalorieValue(int stepValue) {
        String weight = LocalUserInfo.getInstance(this).getUserInfo(
                "weight");
        if (weight.equals("")) {
            weight = "60";
        }
        float weightValue = Float.valueOf(weight);
        return (float) (stepValue * weightValue / 50.0);
    }

    /**
     * 2016.8.27新增
     * 存储从蓝牙接受到的数据
     * 广播发送数据更新UI显示
     */
    private void operateData(float body_temp, int battery, boolean isCharge, int step, float humidity,
                             float room_temp, int aircondition_temp, String pmv, float calorie) {
        BandData bandData = new BandData();
        bandData.setBodyTemp(body_temp);
        bandData.setBattery(battery);
        bandData.setStep(step);
        bandData.setHumidity(humidity);
        bandData.setRoomTemp(room_temp);
        bandData.setAirconditionTemp(aircondition_temp);
        bandData.setPmvLevel(pmv);
        bandData.setCalorie(calorie);
        bandData.setCharge(isCharge);
        /**
         * 将接受到蓝牙的数据存储到数据库
         */
        saveData(bandData);

        /**
         * 通过广播更新UI上面的数据
         */
        broadcastData(bandData);
    }

    /**2016.8.27
     * 通过广播更新UI上面的数据
     */
    private void broadcastData(BandData bandData) {
        Intent intent = new Intent(ACTION_BANDDATA);
        intent.putExtra("band_data", bandData);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action) {
        Log.e(TAG, "发送广播" + action);
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**2016.8.27
     * 将接受到蓝牙的数据存储到数据库
     */
    private void saveData(BandData bandData) {
        BandDataDB bandDataDB = BandDataDB.gerInstance(this);
        bandDataDB.saveBandData(bandData);
    }

    /**2016.8.27新增
     * 将字节数组转变成字符串
     * 字节变16进制字符，一个字节8位，一个16进制字符2个字节
     */
    private String HEXtoString(byte[] bytes) {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }

        return sb.toString();
    }

    //延时断开连接函数,time(ms)是延迟时间
    public void periodDisconnect(final int time) {
        if (mConnectionState == STATE_CONNECTED && mBluetoothGatt != null && !(mDevice.getName()).equals(AIRCONDITION_NAME)) {
            //连接的是空调就不用断开，超出通信范围会自动断开
            ISRECONNECT = true;
            Log.e(TAG, "人为设置断开");
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(time);
                        mBluetoothGatt.disconnect();
                        Log.e(TAG, "断开连接");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


}
