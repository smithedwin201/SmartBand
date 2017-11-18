package com.test.smartband.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.smartband.R;
import com.test.smartband.net.HttpUtil;
import com.test.smartband.other.LocalUserInfo;
import com.test.smartband.tools.Config;
import com.test.smartband.tools.SDCardUtils;
import com.test.smartband.view.BottomPushPopupWindow;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.util.Calendar.SHORT;

/**
 * 用户信息界面
 */
public class UsersActivity extends Activity {

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;//拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;//从相册选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final String IMAGE_FILE_NAME = "head_image.jpg";// 头像文件
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;

    private String gender;
    private String height;
    private String weight;
    private String birth;
    private String avatarPath;
    private String name;
    private RelativeLayout re_avatar;
    private RelativeLayout re_name;
    private RelativeLayout re_height;
    private RelativeLayout re_gender;
    private RelativeLayout re_weight;
    private RelativeLayout re_birth;
    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_height;
    private TextView tv_gender;
    private TextView tv_weight;
    private TextView tv_birth;
    private Button btn_logout;
    private BottomPopAvatar mPop;
    private DatePicker datePicker;
    private int year;
    private int month;
    private int day;
    private RadioButton manRadioButton;
    private RadioButton womanRadioButton;
    private Uri imageUri;
    private Uri uritempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        findView();
        refreshView();
    }

    private void findView() {
        re_avatar = (RelativeLayout) this.findViewById(R.id.re_avatar);
        re_name = (RelativeLayout) this.findViewById(R.id.re_name);
        re_height = (RelativeLayout) this.findViewById(R.id.re_height);
        re_gender = (RelativeLayout) this.findViewById(R.id.re_gender);
        re_weight = (RelativeLayout) this.findViewById(R.id.re_weight);
        re_birth = (RelativeLayout) this.findViewById(R.id.re_birth);

        re_avatar.setOnClickListener(new MyListener());
        re_name.setOnClickListener(new MyListener());
        re_height.setOnClickListener(new MyListener());
        re_gender.setOnClickListener(new MyListener());
        re_weight.setOnClickListener(new MyListener());
        re_birth.setOnClickListener(new MyListener());

        // 头像
        iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_height = (TextView) this.findViewById(R.id.tv_height);
        tv_gender = (TextView) this.findViewById(R.id.tv_sex);
        tv_weight = (TextView) this.findViewById(R.id.tv_weight);
        tv_birth = (TextView) this.findViewById(R.id.tv_birth);
    }

    private void refreshView() {

        name = LocalUserInfo.getInstance(UsersActivity.this).getUserInfo(
                "name");
        gender = LocalUserInfo.getInstance(UsersActivity.this).getUserInfo(
                "gender");
        height = LocalUserInfo.getInstance(UsersActivity.this).getUserInfo(
                "height");
        weight = LocalUserInfo.getInstance(UsersActivity.this).getUserInfo(
                "weight");
        birth = LocalUserInfo.getInstance(UsersActivity.this).getUserInfo(
                "birth");
        //头像
        avatarPath = LocalUserInfo.getInstance(UsersActivity.this)
                .getUserInfo("avatarPath");

        if (!name.equals("")) {
            tv_name.setText(name);
        }
        if (gender.equals("1")) {
            tv_gender.setText("男");
        } else if (gender.equals("2")) {
            tv_gender.setText("女");
        } else {
            tv_gender.setText("男");
        }
        if (!height.equals("")) {
            tv_height.setText(height + " cm");
        }

        if (!weight.equals("")) {
            tv_weight.setText(weight + " Kg");
        }
        if (!birth.equals("")) {
            tv_birth.setText(birth);
        }

        //从图片的路径获取头像图片
        Bitmap photo = getUserAvatar(avatarPath);
        if (photo == null) {//不为小
            iv_avatar.setImageBitmap(photo);
        } else {
            //从服务器下载头像
            HttpUtil.downloadAvatar(Config.getUser(), Config.getPassword(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            BitmapDrawable bd = (BitmapDrawable) iv_avatar.getDrawable();
//                            Bitmap photo = bd.getBitmap();
//                            File f = PhotosPath();
//                            //保存到文件夹中
//                            savePhoto(photo, f);
                            Toast.makeText(UsersActivity.this, "获取头像失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    Log.e("woshishei", "adfgaeg");
                    byte[] respon = response.body().bytes();//获得返回的二进制字节数组
                    ShowResponse(respon);
                }
            });
        }
    }

    private void ShowResponse(final byte[] respon) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UsersActivity.this, "获取头像成功11", Toast.LENGTH_SHORT).show();
                Bitmap photo = BitmapFactory.decodeByteArray(respon, 0, respon.length);
                //byte[] data： 是要进行decode的资源数据
                //int offset：decode的位移量，一般为0
                //int length：decode的数据长度一般为data数组的长度
                //Options opts：设置显示图片的参数，压缩，比例等


//                Log.e("woshishei", "adfgaeg" + respon.toString());
//                Log.e("woshishei", "sssss" + photo.toString());

                iv_avatar.setImageBitmap(photo);
                //把路径存下
                File f = PhotosPath();
                if (f != null && photo != null)
                    //保存到文件夹中
                    savePhoto(photo, f);
            }
        });


    }

    /**
     * 保存头像图片
     */
    private void savePhoto(final Bitmap photo, File f) {
        final FileOutputStream out;

        try {
            //打开输出流 将图片数据填入文件中
            out = new FileOutputStream(f);
//            Log.e("woshishei","zheshi"+f  );
//            Log.e("woshishei","shi"+out  );
            photo.compress(Bitmap.CompressFormat.PNG, 80, out);  //80 是压缩率，表示压缩80%; 如果不压缩是100，表示压缩率为0
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private File PhotosPath() {
        //新建文件夹 先选好路径 再调用mkdir函数 现在是根目录下面的SmartBand文件夹
        File nf = new File(Environment.getExternalStorageDirectory() + "/SmartBand");
        nf.mkdirs();
        //在根目录下面的SmartBand文件夹下 创建head_image.jpg文件
        File f = new File(Environment.getExternalStorageDirectory() + "/SmartBand", IMAGE_FILE_NAME);
        //防止文件锁的安全创建
        try {

            if (f.exists()) {
                f.delete();
            }

            f.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //上面出文件，下面保存路径

        String path = f.getPath();
        //将路径保存到缓存中
        LocalUserInfo.getInstance(UsersActivity.this).setUserInfo("avatarPath", path);

        Log.e("woshishei", "PhotosPath: " + f.toString());
        return f;
    }

    //从图片的路径获取头像图片
    private Bitmap getUserAvatar(String path) {
        if (!path.equals("")) {
            try {
                FileInputStream fis = new FileInputStream(path);
                Bitmap photo = BitmapFactory.decodeStream(fis);
                return photo;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 头像弹出框：拍照、相册、取消
     */
    private class BottomPopAvatar extends BottomPushPopupWindow<Void> {

        public BottomPopAvatar(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void data) {
            View root = View.inflate(context, R.layout.layout_menu_popup_window, null);
            TextView menuBtn1 = (TextView) root.findViewById(R.id.menuBtn1);
            TextView menuBtn2 = (TextView) root.findViewById(R.id.menuBtn2);
            menuBtn1.setText("拍照");
            menuBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    choseHeadImageFromCameraCapture();
                }
            });
            menuBtn2.setText("从相册选取");
            menuBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    choseHeadImageFromGallery();
                }
            });
            View cancelView = root.findViewById(R.id.cancel);
            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return root;
        }
    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
//        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (SDCardUtils.isSDCardEnable()) {
//            // 判断存储卡是否可用，存储照片文件
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.TITLE, IMAGE_FILE_NAME);
//            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
//                    Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/SmartBand", IMAGE_FILE_NAME)));
//        }
        File outputImage = PhotosPath();
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(UsersActivity.this, "com.example.cameraalbumtest.fileprovider", outputImage);
        }
        // 启动相机程序

        //这里待加上当拍照权限被拒绝后的反应
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);

    }

    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        /**
         * 此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
         * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
         */
        //intent.putExtra("return-data", true);
        //uritempFile为Uri类变量，实例化uritempFile
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                if (resultCode == RESULT_OK) {
                    try {
//                        if (SDCardUtils.isSDCardEnable()) {
//
//                            File tempFile = new File(Environment.getExternalStorageDirectory() + "/SmartBand", IMAGE_FILE_NAME);
//                            cropRawPhoto(Uri.fromFile(tempFile));
//                        } else {
//                            Toast.makeText(UsersActivity.this, "没有SDCard!", Toast.LENGTH_LONG).show();
//                        }
////                         将拍摄的照片显示出来
//                        Log.e("woshishei", "onActivityResult: wodaole");
//                        Toast.makeText(this, "shezhi", Toast.LENGTH_SHORT).show();
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        iv_avatar.setImageBitmap(bitmap);
                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                        cropRawPhoto(uri);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    //获取图片路径
                    String imagePath = c.getString(columnIndex);

                    File picture = new File(imagePath);
                    cropRawPhoto(Uri.fromFile(picture));
                    c.close();
                }
                break;
            case PHOTO_REQUEST_CUT:
                //参考网址：http://blog.csdn.net/eclothy/article/details/42719217
                //裁剪后，将裁剪的图片保存在Uri中，在onActivityResult()方法中，再提取对应的Uri图片转换为Bitmap使用。
                // Toast.makeText(this, "wplaile ", Toast.LENGTH_SHORT).show();
                try {
                    //提取uri文件
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                    iv_avatar.setImageBitmap(bitmap);
                    //把路径存下
                    File f = PhotosPath();

                    //保存到文件夹中
                    savePhoto(bitmap, f);

                    HttpUtil.uploadAvatar(f.getPath());  //把网络访问的代码放在这里
                    Log.e("uploadFile", f.getPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("uploadFile", "uploadAvatar: 上传失败");
                }
                break;
            default:
                break;
        }
    }

//    private void showImage(String TUKUimagePath) {
//
//        File picture = new File(TUKUimagePath);
//        cropRawPhoto(Uri.fromFile(picture));
//
//        Toast.makeText(UsersActivity.this, "获取头像成功1", Toast.LENGTH_SHORT).show();
//        Bitmap photo = BitmapFactory.decodeFile(TUKUimagePath);
//
////                Log.e("woshishei", "adfgaeg" + respon.toString());
////                Log.e("woshishei", "sssss" + photo.toString());
//
//        iv_avatar.setImageBitmap(photo);
//
//
//        //把路径存下
//        File f = PhotosPath();
//
//        //保存到文件夹中
//        savePhoto(photo, f);
//
//
//    }


    class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(UsersActivity.this,
                    ChangeUserParaActivity.class);
            switch (v.getId()) {
                case R.id.re_avatar:
                    mPop = new BottomPopAvatar(UsersActivity.this);
                    mPop.show(UsersActivity.this);
                    break;
                case R.id.re_name:
                    intent.putExtra("content", "name");
                    startActivity(intent);
                    break;
                case R.id.re_height:
                    intent.putExtra("content", "height");
                    startActivity(intent);
                    break;
                case R.id.re_gender:
//                    showSexSelectDialog();
                    new BottomPopSex(UsersActivity.this).show(UsersActivity.this);
                    break;
                case R.id.re_weight:
                    intent.putExtra("content", "weight");
                    startActivity(intent);
                    break;
                case R.id.re_birth:
                    new BottomPopBirthday(UsersActivity.this).show(UsersActivity.this);
                    break;
            }
            refreshView();
        }

    }

    /**
     * 性别选择弹出框
     */
    private class BottomPopSex extends BottomPushPopupWindow<Void> {

        public BottomPopSex(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(final Void data) {
            View root = View.inflate(context, R.layout.popup_sex_select, null);
            Button cancelButton = (Button) root.findViewById(R.id.cancel_step);
            Button okButton = (Button) root.findViewById(R.id.ok_step);
            RelativeLayout menRelativeLayout = (RelativeLayout) root.findViewById(R.id.male_relativeLayout);
            RelativeLayout femenRelativeLayout = (RelativeLayout) root.findViewById(R.id.female_relativeLayout);
            final RadioButton maleRadioButton = (RadioButton) root.findViewById(R.id.man_radioButton);
            final RadioButton femaleRadioButton = (RadioButton) root.findViewById(R.id.woman_radioButton);
            if (tv_gender.getText().equals("男")) {
                maleRadioButton.setChecked(true);
                femaleRadioButton.setChecked(false);
            } else {
                maleRadioButton.setChecked(false);
                femaleRadioButton.setChecked(true);
            }

            menRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    maleRadioButton.setChecked(true);
                    femaleRadioButton.setChecked(false);
                }
            });

            femenRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    maleRadioButton.setChecked(false);
                    femaleRadioButton.setChecked(true);
                }
            });
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (maleRadioButton.isChecked()) {
                        tv_gender.setText("男");
                        updateSex("1");
                    } else {
                        tv_gender.setText("女");
                        updateSex("2");
                    }

                    dismiss();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return root;
        }
    }

    /**
     * 更新数据库中的性别信息, 1表示男，2表示女，可以自定义
     */
    public void updateSex(final String gendernum) {
        LocalUserInfo.getInstance(UsersActivity.this)
                .setUserInfo("gender", gendernum);
    }

    /**
     * 生日日期选择弹出框
     */
    private class BottomPopBirthday extends BottomPushPopupWindow<Void> {

        public BottomPopBirthday(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(final Void data) {
            View root = View.inflate(context, R.layout.popup_data_picker, null);
            Button cancelButton = (Button) root.findViewById(R.id.cancel_birthday);
            Button okButton = (Button) root.findViewById(R.id.ok_birthday);
            datePicker = (DatePicker) root.findViewById(R.id.datePicker);
            Calendar maxCalendar = Calendar.getInstance();
            datePicker.setMaxDate(maxCalendar.getTimeInMillis());
            Calendar c = Calendar.getInstance();
            datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                    new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            UsersActivity.this.year = year;
                            UsersActivity.this.month = monthOfYear;
                            UsersActivity.this.day = dayOfMonth;
                        }
                    });

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBirthdayData(year, month, day);
                    dismiss();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return root;
        }
    }

    private void showBirthdayData(int year, int monthOfYear, int dayOfMonth) {
        String textString = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
        tv_birth.setText(textString);
        LocalUserInfo.getInstance(UsersActivity.this).setUserInfo("birth", textString);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        refreshView();
    }

    public void back(View view) {
        finish();
    }
}
