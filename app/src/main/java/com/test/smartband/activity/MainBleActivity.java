package com.test.smartband.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.test.smartband.R;
import com.test.smartband.controlsubfrags.FragmentSubAirCon;
import com.test.smartband.fragment.FragmentControl;
import com.test.smartband.fragment.FragmentHome;
import com.test.smartband.fragment.FragmentMine;
import com.test.smartband.fragment.FragmentSport;
import com.test.smartband.model.BandData;
import com.test.smartband.other.BandNotification;
import com.test.smartband.service.BluetoothLeService;
import com.test.smartband.tools.Config;
import com.test.smartband.view.BatteryView;
import com.test.smartband.view.MainTitleLayout;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;

public class MainBleActivity extends BaseActivity implements FragmentSubAirCon.airControl {


//    private static final String TAG = "*==MainActivity==*";
    public static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 5000L;

    //用来启动service
    private Intent mServiceIntent;

    //原名：mService作为messenger传过来Service对象的引用，以便进行相关处理
    private Messenger mServiceMessenger = null;
    //状态机标志，控制UI界面的更新以及相关蓝牙操作（自定义服务BleService，处理蓝牙扫描工作及结果）

    //代表一台蓝牙设备（手环或终端）
    private static BluetoothDevice Device;

    //简单数据存储操作
    private SharedPreferences preference;
    private Editor editor;
    //新数据标志
    private static boolean newData = true;
    //Provides access to information about the telephony services on the device
    //Applications can use the methods in this class to determine telephony services and states
    public TelephonyManager telManager;
    //通话状态监听器，自定义继承自extends PhoneStateListener
    public TelListner listener;
    //音频管理器
    public AudioManager audioManager;

    private FragmentHome homeFragment;
    private FragmentSport sportFragment;
    private FragmentControl controlFragment;
    private FragmentMine mineFragment;
    private Fragment[] fragments;
    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private int index;
    private int currentTabIndex;
    private Context context;
    private FragmentManager fragmentManager;

    //主页显示控件
    public static TextView body_t, room_t, humi, cal, step, connect, air_condition;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private BatteryView batteryview;
    private LinearLayout linearlayout;
    private TitlePageIndicator titlePageIndicator;
    private ImageView aircondition_iv;
    public static int progressForStep;
    private TextView pmv_t;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    public static boolean mScanning = false;
    private ArrayList<BluetoothDevice> mLeDevices = null;
    private BluetoothLeService mBluetoothLeService = null;
    private String mDeviceAddress;
    private BluetoothDevice mDevice;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    private int connectState = BluetoothLeService.STATE_DISCONNECTED;//当前状态为未连接
    private boolean connectResult = false;

    private MainTitleLayout mTitleLayout;
    private String[] fragmentTitle;
    private boolean[] isShowIcon;

    private NotificationManager mNotificationManager = null;
    private Notification.Builder notificationBuilder;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (mDeviceAddress != null) {
                mBluetoothLeService.connectDevice(mDeviceAddress);
//                Log.e(TAG, "连接设备connect");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
//            Log.i(TAG, "--->onServiceDisconnected()连接失败");
        }
    };

    //启动服务并绑定到服务
    //连接设备
    public void bindService() {
//        Log.e(TAG, "绑定服务");
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        //因为蓝牙也是通过服务进行使用的，所以要取得蓝牙服务需要绑定服务，绑定服务到蓝牙服务中。
        //绑定成功时得到OnBind()中的mBinder.
        bindService(gattServiceIntent, mConnection, BIND_AUTO_CREATE);
        //连接设备
        connectDevice();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (device != null && !mLeDevices.contains(device)) {
                        mLeDevices.add(device);
//                        Log.e(TAG, "扫描出设备" + device.getName());
                    } else {
//                        Log.e(TAG, "扫描出Nothing");
                    }
                }
            };


    private void initView() {
        //标题栏
        mTitleLayout = (MainTitleLayout) findViewById(R.id.toolbar);

        //加载三个主界面
        homeFragment = new FragmentHome();
        sportFragment = new FragmentSport();
        controlFragment = new FragmentControl();
        mineFragment = new FragmentMine();

        fragments = new Fragment[]{
                homeFragment, sportFragment, controlFragment, mineFragment
        };

        fragmentTitle = new String[]{"空调手环", "运动", "空调控制", "我的"};
        isShowIcon = new boolean[]{true, false, false, false};

        imagebuttons = new ImageView[4];
        imagebuttons[0] = (ImageView) findViewById(R.id.iv_home);
        imagebuttons[1] = (ImageView) findViewById(R.id.iv_sport);
        imagebuttons[2] = (ImageView) findViewById(R.id.iv_control);
        imagebuttons[3] = (ImageView) findViewById(R.id.iv_mine);
        imagebuttons[0].setSelected(true);

        textviews = new TextView[4];
        textviews[0] = (TextView) findViewById(R.id.tv_home);
        textviews[1] = (TextView) findViewById(R.id.tv_sport);
        textviews[2] = (TextView) findViewById(R.id.tv_control);
        textviews[3] = (TextView) findViewById(R.id.tv_mine);
        textviews[0].setTextColor(0xffea8010);
        // 开启Fragment事务，添加三个主的Fragment，并显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment, "home")
                .add(R.id.fragment_container, sportFragment, "sport")
                .add(R.id.fragment_container, controlFragment, "control")
                .add(R.id.fragment_container, mineFragment, "mine")
                .hide(sportFragment).hide(controlFragment).hide(mineFragment)
                .show(homeFragment)
                .commit();

        //显示通知栏
        BandNotification.getInstance(this).showNotification();

    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.re_home:
                index = 0;
                break;
            case R.id.re_sport:
                index = 1;
                break;
            case R.id.re_control:
                index = 2;
                break;
            case R.id.re_mine:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                fts.add(R.id.fragment_container, fragments[index]);
            }
            fts.show(fragments[index]).commit();
        }
        //设置标题栏
        mTitleLayout.setTitle(fragmentTitle[index]);
        mTitleLayout.setRunImageViewVisible(isShowIcon[index]);
        mTitleLayout.setMenuImageViewVisible(isShowIcon[index]);

        //变更底部Tab图标
        imagebuttons[currentTabIndex].setSelected(false);
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFFea8010);
        currentTabIndex = index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //全局初始化百度SDK
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Log.e("MainBleActivity", "onCreate");
        initView();//初始化ui控件

        mHandler = new Handler();
        mLeDevices = new ArrayList<BluetoothDevice>();
        //初始化蓝牙适配器，要通过蓝牙管理器获取
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bleVerify();//检查手机是否用于蓝牙模块

    }

    @Override
    protected void onResume() {
        Log.e("MainBleActivity", "onResume");
        findHomeFragmentView();
        registerLocalReceiver();

        if (connectState == BluetoothLeService.STATE_DISCONNECTED && mBluetoothAdapter.isEnabled()) {
            sendBroadcast(new Intent(BluetoothLeService.ACTION_GATT_SCANNING));
            scanLeDevice(true);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e("MainBleActivity", "onDestroy");
        mBluetoothAdapter.disable();//关关蓝牙
        stopService(mServiceIntent);
        unbindService(mConnection);
        mBluetoothLeService = null;
        localBroadcastManager.unregisterReceiver(localReceiver);

        super.onDestroy();
    }

    //蓝牙
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Intent intent = new Intent(Config.BLUETOOTH_OFF_BROADCAST);
            sendBroadcast(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    Log.e(TAG, "停止扫描");
                    //如果能够获得空调或手环的地址，就绑定服务并连接，否则继续扫描；
                    mDeviceAddress = getDeviceAddress();

                    if (mDeviceAddress != null) {
                        //扫描到手环了，停止搜索
                        bindService();
                        mHandler.removeCallbacks(this);//停止该线程：停止每两秒执行一次run里面的代码
                    } else {
                        //没有扫描到手环，继续搜索
                        mLeDevices.clear();
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                        mScanning = true;
//                        Log.e(TAG, "继续扫描");
                    }
                    if (mScanning) {
                        mHandler.postDelayed(this, 2000);//线程：每两秒执行run里面的代码一次
                    }
                }
            });

            mLeDevices.clear();
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
//            Log.e(TAG, "正在扫描");
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    private String getDeviceAddress() {
        boolean isConnectBand = false;
        String bleAddress = null;
        //当空调和手环都存在时，优先连接空调
        if (!mLeDevices.isEmpty()) {
            for (BluetoothDevice device : mLeDevices) {
                if (device.getName() != null) {
                    //是否扫描到空调
                    if (device.getName().equals(BluetoothLeService.AIRCONDITION_NAME)) {
                        bleAddress = device.getAddress();
                        mDevice = device;
//                        Log.e(TAG, "扫描得到将要连接的地址" + device.getAddress());
                        return bleAddress;
                    } else if (device.getName().equals(BluetoothLeService.BAND_NAME)) {
                        //是否扫描到手环
                        bleAddress = device.getAddress();
                        mDevice = device;
                        isConnectBand = true;
                    }
                }
            }
            if (isConnectBand) {
                return bleAddress;
            }
        }
        return null;
    }

    private void connectDevice() {
        //如果绑定服务失败则mBluetoothLeService == null
        if (mBluetoothLeService != null) {
            //连接蓝牙设备，通过蓝牙地址来连接
            connectResult = mBluetoothLeService.connectDevice(mDeviceAddress);
        }
    }


    //获取主页控件id
    private void findHomeFragmentView() {
        linearlayout = (LinearLayout) findViewById(R.id.Main_linearlayout);
        titlePageIndicator = (TitlePageIndicator) findViewById(R.id.id_titlePage_control);
        aircondition_iv = (ImageView) findViewById(R.id.iv_air_condition);
    }

    private void registerLocalReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothLeService.ACTION_BANDDATA);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);
    }

    private class LocalReceiver extends BroadcastReceiver {

        private int mMStep;

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            //监听蓝牙关闭或开启的状态
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {//蓝牙状态监听
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF: //手机蓝牙关闭时
                        scanLeDevice(false);
                        break;
                    case BluetoothAdapter.STATE_ON: //手机蓝牙开启时
                        scanLeDevice(true);
                        break;
                }
            }

            if (BluetoothLeService.ACTION_GATT_SCANNING.equals(action)) {
                //"正在搜索"

            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //"正在连接"
                if (mScanning) {
                    scanLeDevice(false);
                }

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //"连接断开"
                scanLeDevice(true);
                connectState = BluetoothLeService.STATE_DISCONNECTED;
                if (BluetoothLeService.ISRECONNECT) {
                    BluetoothLeService.ISRECONNECT = false;
//                    scanLeDevice(true);
                }

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //"已连接"
                connectState = BluetoothLeService.STATE_CONNECTED;//标记当前连接状态为已连接
                if (mDevice.getName().equals(BluetoothLeService.AIRCONDITION_NAME)) {
                    aircondition_iv.setVisibility(View.VISIBLE);
                }
//                setMainThemeColor();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

            } else if (BluetoothLeService.ACTION_BANDDATA.equals(action)) {
                BandData bandData = (BandData) intent.getSerializableExtra("band_data");
                mMStep = bandData.getStep();
                //连接上手环之后，在通知栏上面显示步数信息
                BandNotification.getInstance(MainBleActivity.this).showNotification(true, mMStep);
            }
        }
    }

    /**
     * 当app连接上手环或空调时，主题颜色从灰色变成橙色
     */
//    private void setMainThemeColor() {
//        int apiVersion = Build.VERSION.SDK_INT;
//        if (apiVersion >= 21) {
//            Window window = this.getWindow();
//            window.setStatusBarColor(getResources().getColor(R.color.baseBackground));
//        }
//        toolbar.setBackgroundColor(getResources().getColor(R.color.baseBackground));
//        linearlayout.setBackgroundColor(getResources().getColor(R.color.baseBackground));
//        titlePageIndicator.setBackgroundColor(getResources().getColor(R.color.baseBackground));
//        if (mDevice.getName().equals(BluetoothLeService.AIRCONDITION_NAME)) {
//            aircondition_iv.setVisibility(View.VISIBLE);
//        }
//    }

    //蓝牙兼容判断
    public void bleVerify() {
        //一开始判断手机是否支持蓝牙BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "手机不支持蓝牙Ble", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "手机支持蓝牙Ble", Toast.LENGTH_SHORT).show();
            //检查蓝牙是否已启用，若不就弹出系统对话框询问用户得到使用权限
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("退出提示");
        dialog.setMessage("是否要退出手环程序？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("woshishei", "onba ");
//                mBluetoothAdapter.disable();//关闭蓝牙
//                finish();
                System.exit(0);
                //调用这个函数才能释放蓝牙资源
                /*finish是Activity的类，仅仅针对Activity，当调用finish()时，
                只是将活动推向后台，并没有立即释放内存，活动的资源并没有被清理；
                当调用System.exit(0)时，杀死了整个进程，这时候活动所占的资源也会被释放。*/
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.create();
        dialog.show();
    }

    @Override
    public void airCommand(View view, Bundle data) {

    }
}

