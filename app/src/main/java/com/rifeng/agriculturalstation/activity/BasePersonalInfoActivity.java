package com.rifeng.agriculturalstation.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.BaseApplication;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.bean.UserBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.ChangeAddressPopwindow;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.ImageUtils;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;
import com.rifeng.agriculturalstation.view.CircleImageView;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 个人信息--基础信息
 * <p/>
 * Created by chw on 2016/10/31.
 */
public class BasePersonalInfoActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.pi_avatar_rl)
    RelativeLayout piAvatarRl;
    @BindView(R.id.pi_avatar)
    CircleImageView piAvatar;
    @BindView(R.id.pi_username_tip)
    TextView piUsernameTip;
    @BindView(R.id.pi_username_tv)
    TextView piUsernameTv;
    @BindView(R.id.pi_username_rl)
    RelativeLayout piUsernameRl;
    @BindView(R.id.pi_usertype_tv)
    TextView piUsertypeTv;
    @BindView(R.id.pi_usertype_rl)
    RelativeLayout piUsertypeRl;
    @BindView(R.id.pi_phone_tip)
    TextView piPhoneTip;
    @BindView(R.id.pi_phone_tv)
    TextView piPhoneTv;
    @BindView(R.id.pi_phone_rl)
    RelativeLayout piPhoneRl;
    @BindView(R.id.pi_realname_tip)
    TextView piRealnameTip;
    @BindView(R.id.pi_realname_tv)
    TextView piRealnameTv;
    @BindView(R.id.pi_realname_rl)
    RelativeLayout piRealnameRl;
    @BindView(R.id.pi_idcard_tip)
    TextView piIdcardTip;
    @BindView(R.id.pi_idcard_tv)
    TextView piIdcardTv;
    @BindView(R.id.pi_idcard_rl)
    RelativeLayout piIdcardRl;
    @BindView(R.id.pi_nativeplace_tv)
    TextView piNativeplaceTv;
    @BindView(R.id.pi_nativeplace_rl)
    RelativeLayout piNativeplaceRl;
    @BindView(R.id.pi_address_tip)
    TextView piAddressTip;
    @BindView(R.id.pi_address_tv)
    TextView piAddressTv;
    @BindView(R.id.pi_address_rl)
    RelativeLayout piAddressRl;
    @BindView(R.id.pi_certificate_1)
    ImageView piCertificate1;
    @BindView(R.id.pi_certificate_2)
    ImageView piCertificate2;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    public static final int REQUEST_PHOTO_ALBUM = 0x123; // 调用系统相册
    public static final int REQUEST_CODE_CAMERA = 0x110; // 拍照操作
    public static final int REQUEST_CODE_CROP = 0x111; // 裁剪操作
    public static final int CAMERA_ONE = 0x112; // 相关证件一拍照
    public static final int CAMERA_TWO = 0x113; // 相关证件二拍照
    public static final int USER_AVATAR = 0x114; // 用户头像
    private int curCamera = 0;

    private int requestFlag; // 请求码标识
    private int userTypeFlag = 0; // 用户类型标识

    private String defaultProvince = "广西"; // 省份，默认值
    private String defaultCity = "南宁"; // 城市，默认值

    private PopupWindow popupWindow = null;
    private View popu_parentView;
    private TextView popuCamera;
    private TextView popuPhoto;
    private TextView popuCancel;
    private LinearLayout llPopup;
    private RelativeLayout popuParent;

    private Bitmap bitmap;
    private Uri imgUri;
    private int screenW = 0;
    private CustomProgressDialog dialog;
    private UserBean userBean;
    private boolean isUserName = false;
    private boolean isPhone = false;
    private boolean isAvatar = false;

    private int uid;//打开的用户的id
    private int UId;//登录中的用户的id

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CAMERA_ONE) {
                farmUpload("certificate");
            } else if (msg.what == CAMERA_TWO) {
                farmUpload("certificate1");
            } else if(msg.what == USER_AVATAR){
                isAvatar = true;
                farmUpload("avatar");
            }
        }
    };

    /**
     * 上传相关证件
     *
     * @param key
     */
    private void farmUpload(String key) {
        File file = null;
        try {
            file = ImageUtils.bitmap2File(bitmap, BasePersonalInfoActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 拼接参数
        OkGo.post(Urls.URL_UPLOAD_CERTIFICATE)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("key", key)
            .params("uploadfile", file)
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    dialog.show();
                }

                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    if(isAvatar){
                        isAvatar = false;
                        SharedPreferencesUtil.put(mContext, Consts.USER_AVATAR, serverResult.msg);
                    }else {
                        ToastUtil.showShort(mContext, serverResult.msg);
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "上传失败");
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_basepersonalinfo;
    }



    @Override
    protected void initData() {
        initView();
        initEvent();
        UId = (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0);
        idTitleMiddle.setText("基础信息");
        // 获取屏幕高度和宽度
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screenW = wm.getDefaultDisplay().getWidth();
//        int height = wm.getDefaultDisplay().getHeight();
        piCertificate1.setMaxHeight(screenW);
        piCertificate1.setAdjustViewBounds(true);

        piCertificate2.setMaxHeight(screenW);
        // 如果想设置ImageView的最大宽高，须设置为true
        piCertificate2.setAdjustViewBounds(true);
        userBean = (UserBean) this.getIntent().getSerializableExtra("userbean");
        if (userBean != null) {
            setUserBean();
            uid = userBean.getUid();
        } else {
            getUserDatas();
        }
    }
    private void initView() {
        // 实例化弹出窗
        initPop();
        dialog = new CustomProgressDialog(mContext, "请稍候...");
    }

    private void initPop() {
        popu_parentView = LayoutInflater.from(this).inflate(R.layout.activity_basepersonalinfo, null);
        // 实例化弹出窗
        popupWindow = new PopupWindow(this);
        // 获取弹出窗对象
        View viewWindow = LayoutInflater.from(this).inflate(R.layout.avater_popuwindows, null);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(viewWindow);

        popuCamera = (TextView) viewWindow.findViewById(R.id.popu_camera);
        popuPhoto = (TextView) viewWindow.findViewById(R.id.popu_photo);
        popuCancel = (TextView) viewWindow.findViewById(R.id.popu_cancel);
        llPopup = (LinearLayout) viewWindow.findViewById(R.id.ll_popup);
        popuParent = (RelativeLayout) viewWindow.findViewById(R.id.popu_parent);
    }

    private void setUserBean() {
        switch (userBean.getUsertype()) {
            case 1:
                piUsertypeTv.setText("个人");
                break;

            case 2:
                piUsertypeTv.setText("公司");
                break;

            case 3:
                piUsertypeTv.setText("企业");
                break;
        }
        piUsernameTv.setText(userBean.getUsername());
        piPhoneTv.setText(userBean.getPhone());
        piRealnameTv.setText(userBean.getRealname());
        piIdcardTv.setText(userBean.getIdcard());
        piNativeplaceTv.setText(userBean.getResideprovince() + "-" + userBean.getResidecity());
        piAddressTv.setText(userBean.getResideaddress());
        if (userBean.getCertificate() != null && !userBean.getCertificate().equals("")) {
            imageLoader.displayImage(Urls.BASE_IMGURL + userBean.getCertificate(), piCertificate1);
        }
        if (userBean.getCertificate1() != null && !userBean.getCertificate1().equals("")) {
            imageLoader.displayImage(Urls.BASE_IMGURL + userBean.getCertificate1(), piCertificate2);
        }
    }

    // 获取用户数据
    private void getUserDatas() {
        OkGo.post(Urls.URL_GET_PERSONAL_INFO)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .execute(new JsonCallback<UserBean>() {
                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    dialog.show();
                }

                @Override
                public void onSuccess(UserBean userBean, Call call, Response response) {
                    uid = userBean.getUid();
                    setUserData(userBean);
                    dialog.dismiss();
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "加载失败");
                    dialog.dismiss();
                }
            });
    }

    private void setUserData(UserBean userBean) {
        userTypeFlag = userBean.getUsertype() - 1;
        switch (userBean.getUsertype()) {
            case 1:
                piUsertypeTv.setText("个人");
                break;

            case 2:
                piUsertypeTv.setText("公司");
                break;

            case 3:
                piUsertypeTv.setText("企业");
                break;
        }

        if(!TextUtils.isEmpty(userBean.getAvatar())){ // 头像
            imageLoader.displayImage(Urls.BASE_IMGURL + userBean.getAvatar(), piAvatar);
        }
        if(!TextUtils.isEmpty(userBean.getUsername())){ // 用户名
            piUsernameTv.setText(userBean.getUsername());
        }
        if(!TextUtils.isEmpty(userBean.getPhone())){ // 手机号
            piPhoneTv.setText(userBean.getPhone());
        }
        if(!TextUtils.isEmpty(userBean.getRealname())){ // 真实姓名
            piRealnameTv.setText(userBean.getRealname());
        }
        if(!TextUtils.isEmpty(userBean.getIdcard())){ // 身份证号
            piIdcardTv.setText(userBean.getIdcard());
        }
        if(!TextUtils.isEmpty(userBean.getResideprovince()) && !TextUtils.isEmpty(userBean.getResidecity())){ // 籍贯
            piNativeplaceTv.setText(userBean.getResideprovince() + "-" + userBean.getResidecity());
        }
        if(!TextUtils.isEmpty(userBean.getResideaddress())){ // 详细地址
            piAddressTv.setText(userBean.getResideaddress());
        }
        if(!TextUtils.isEmpty(userBean.getCertificate())){ // 相关证件1
            imageLoader.displayImage(Urls.BASE_IMGURL + userBean.getCertificate(), piCertificate1);
        }
        if(!TextUtils.isEmpty(userBean.getCertificate1())){ // 相关证件2
            imageLoader.displayImage(Urls.BASE_IMGURL + userBean.getCertificate1(), piCertificate2);
        }

    }

    private void initEvent() {
        idTitleLeft.setOnClickListener(this);

        if (userBean == null) {
            piAvatarRl.setOnClickListener(this);
            piUsernameRl.setOnClickListener(this);
            piUsertypeRl.setOnClickListener(this);
            piPhoneRl.setOnClickListener(this);
            piRealnameRl.setOnClickListener(this);
            piIdcardRl.setOnClickListener(this);
            piNativeplaceRl.setOnClickListener(this);
            piAddressRl.setOnClickListener(this);

            piCertificate1.setOnClickListener(this);
            piCertificate2.setOnClickListener(this);
        }

        popuParent.setOnClickListener(this);
        popuCamera.setOnClickListener(this);
        popuPhoto.setOnClickListener(this);
        popuCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.pi_avatar_rl: // 头像
                if (UId == uid) {
                    curCamera = USER_AVATAR;
                    requestFlag = 0;
                    openPopupWindow();
                }
                break;
            case R.id.pi_username_rl: // 用户名
                if (UId == uid) {
                    bundle.putString("title", piUsernameTip.getText().toString().trim());
                    bundle.putString("content", piUsernameTv.getText().toString().trim());
                    requestFlag = 1;
                }

                break;

            case R.id.pi_usertype_rl: // 用户类型
                if (UId == uid) {
                    requestFlag = 0;
                    changeUserType();
                }

                break;

            case R.id.pi_phone_rl: // 联系电话
                if (UId == uid) {
                    bundle.putString("title", piPhoneTip.getText().toString().trim());
                    bundle.putString("content", piPhoneTv.getText().toString().trim());
                    requestFlag = 2;
                }

                break;

            case R.id.pi_realname_rl: // 真实姓名
                if (UId == uid) {
                    bundle.putString("title", piRealnameTip.getText().toString().trim());
                    bundle.putString("content", piRealnameTv.getText().toString().trim());
                    requestFlag = 3;
                }

                break;

            case R.id.pi_idcard_rl: // 身份证号
                if (UId == uid) {
                    bundle.putString("title", piIdcardTip.getText().toString().trim());
                    bundle.putString("content", piIdcardTv.getText().toString().trim());
                    requestFlag = 4;
                }

                break;

            case R.id.pi_nativeplace_rl: // 籍贯
                if (UId == uid) {
                    requestFlag = 0;
                    // 选择籍贯
                    selectNativePlace();
                }

                break;

            case R.id.pi_address_rl: // 详细地址
                if (UId == uid) {
                    bundle.putString("title", piAddressTip.getText().toString().trim());
                    bundle.putString("content", piAddressTv.getText().toString().trim());
                    requestFlag = 5;
                }

                break;

            case R.id.pi_certificate_1: // 相关证件一
                if (UId == uid) {
                    requestFlag = 0;
                    curCamera = CAMERA_ONE;
                    openPopupWindow();
                } else {
                    ToastUtil.showShort(mContext,"您不可以修改他人信息哦！");
                }
                break;

            case R.id.pi_certificate_2: // 相关证件二
                if (UId == uid) {
                    requestFlag = 0;
                    curCamera = CAMERA_TWO;
                    openPopupWindow();
                }else {
                    ToastUtil.showShort(mContext,"您不可以修改他人信息哦！");
                }
                break;

            case R.id.popu_parent: //
                requestFlag = 0;
                closePopupWindow();
                break;

            case R.id.popu_camera: // 拍照
                requestFlag = 0;
                // 调起系统的拍照功能
                camera();
                closePopupWindow();
                break;

            case R.id.popu_photo: // 从相册中选择
                requestFlag = 0;
                //调起系统中的相册
                photoAlbum();
                closePopupWindow();
                break;

            case R.id.popu_cancel: // 取消
                requestFlag = 0;
                closePopupWindow();
                break;
        }
        if (requestFlag != 0) {
            startActivityForResult(ModifyInfoActivity.class, bundle, requestFlag);
        }
    }

    private void openPopupWindow(){
        llPopup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
        popupWindow.showAtLocation(popu_parentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 调用系统相册
     */
    private void photoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        intent.setDataAndType(imgUri, "image/*");
        startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
    }

    /**
     * 调用系统相机拍照
     */
    private void camera() {
        String fileName = System.currentTimeMillis() + ".jpg";
        imgUri = Uri.fromFile(new File(BaseApplication.cacheFile + "/" + fileName));
        // MediaStore 媒体提供者
        // ACTION_IMAGE_CAPTURE 调起相机应用程序捕获图片并返回它
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 存储卡可用，将照片存储在sdcard
        /**
         * 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。
         * 如果想访问原始图片，可以通过data extra能够得到原始图片位置
         *
         * 如果指定了目标uri，data就没有数据返回；如果没有指定uri，则data就返回有数据
         */
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 关闭PopupWindow
     */
    private void closePopupWindow() {
        popupWindow.dismiss();
        // 调用View中的clearAnimation()方法，即可消除View上正在运行的动画效果
        llPopup.clearAnimation();
    }

    /**
     * 选择籍贯
     */
    private void selectNativePlace() {
        // 实例化对象
        ChangeAddressPopwindow mChangeAddressPopwindow = new ChangeAddressPopwindow(BasePersonalInfoActivity.this, defaultProvince, defaultCity, "");
        // 设置显示的位置
        mChangeAddressPopwindow.showAtLocation(idTitleLeft, Gravity.BOTTOM, 0, 0);
        mChangeAddressPopwindow.setAddresskListener(new ChangeAddressPopwindow.OnAddressCListener() {
            @Override
            public void onClick(String province, String city, String area) {
                piNativeplaceTv.setText(province + "-" + city);
                defaultProvince = province;
                defaultCity = city;
                changeUserInfo("area", "");
            }
        });
    }

    /**
     * 用户类型选择
     */
    private void changeUserType() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setSingleChoiceItems(R.array.select_user_type, userTypeFlag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                piUsertypeTv.setText(getResources().getStringArray(R.array.select_user_type)[which]);
                userTypeFlag = which;
                changeUserInfo("usertype", (userTypeFlag + 1) + "");
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

//            if(data != null){ // 可能尚未指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                // 返回有缩略图
//                if(data.hasExtra("data")){
//                    Bitmap bm = data.getParcelableExtra("data");
//                    // 得到bitmap后的操作
//                }
//            }else {
//                // 由于指定了目标uri，存储在目标uri，intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                // 通过目标uri，找到图片
//                // 对图片的缩放处理
//                // 操作
//            }
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (imgUri != null && !imgUri.equals("")) {
                    // TODO 将拍照得到的图片拿去裁剪
                    cropPicture(imgUri);
                }
            }


            if (data != null) {
                String result = "";
                switch (requestCode) {
                    case 1: // 用户名
                        result = data.getStringExtra("data");
                        piUsernameTv.setText(result);
                        isUserName = true;
                        changeUserInfo("username", result);
                        break;

                    case 2: // 联系电话
                        result = data.getStringExtra("data");
                        piPhoneTv.setText(result);
                        isPhone = true;
                        changeUserInfo("phone", result);
                        break;

                    case 3: // 真实姓名
                        result = data.getStringExtra("data");
                        piRealnameTv.setText(result);
                        changeUserInfo("realname", result);
                        break;

                    case 4: // 身份证号
                        result = data.getStringExtra("data");
                        piIdcardTv.setText(result);
                        changeUserInfo("idcard", result);
                        break;

                    case 5: // 详细地址
                        result = data.getStringExtra("data");
                        piAddressTv.setText(result);
                        changeUserInfo("resideaddress", result);
                        break;

                    case REQUEST_CODE_CROP:
                        try {
                            if (imgUri != null && !imgUri.equals("")) {
                                Message msg = new Message();
                                bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imgUri));
                                // TODO 将图片显示，保存到本地并发送消息到Handler中执行上传操作
                                if (curCamera == CAMERA_ONE) {
                                    // 相关证件一
                                    piCertificate1.setImageBitmap(bitmap);
                                    msg.what = CAMERA_ONE;
                                } else if (curCamera == CAMERA_TWO) {
                                    // 相关证件二
                                    piCertificate2.setImageBitmap(bitmap);
                                    msg.what = CAMERA_TWO;
                                } else if(curCamera == USER_AVATAR){
                                    piAvatar.setImageBitmap(bitmap);
                                    msg.what = USER_AVATAR;
                                }
                                msg.obj = bitmap;
                                mHandler.sendMessage(msg);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;

                    case REQUEST_PHOTO_ALBUM: // 调用系统相册后返回
                        imgUri = data.getData();
                        System.out.println("---imgUri = " + imgUri);
                        if (imgUri != null && !imgUri.equals("")) {
                            // TODO 将拍照得到的图片拿去裁剪
                            cropPicture(imgUri);
                        }
                        break;
                }
            }
        }
    }

    /**
     * 调用系统的裁剪功能裁剪图片
     *
     * @param uri
     */
    private void cropPicture(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
        // 图片输出大小
        intent.putExtra("outputX", screenW);
        intent.putExtra("outputY", screenW);
        // 为false则表示不返回数据
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 当图片裁剪后过小时，做填充拉伸
//        intent.putExtra("scale", true);
//        intent.putExtra("scaleUpIfNeeded", true);
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    private void changeUserInfo(String key, final String value) {
        dialog.show();
        HttpParams params = new HttpParams();
        if (key.equals("area")) { // 修改省市
            params.put("province", defaultProvince);
            params.put("city", defaultCity);
        } else {
            params.put("value", value);
        }
        // 拼接参数
        OkGo.post(Urls.URL_CHANGE_USER_INFO)
            .tag(this)
            .params(params)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("key", key)
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    ToastUtil.showShort(mContext, serverResult.msg);
                    if(serverResult.code == 200){
                        if(isUserName){
                            SharedPreferencesUtil.put(mContext, Consts.USER_USERNAME, value);
                            isUserName = false;
                        }
                        if(isPhone){
                            SharedPreferencesUtil.put(mContext, Consts.USER_PHONE, value);
                            isPhone = false;
                        }
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "网络错误");
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            });
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        super.onDestroy();
    }
}
