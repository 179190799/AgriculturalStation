package com.rifeng.agriculturalstation.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.BaseApplication;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 个人中心-身份认证
 * <p/>
 * Created by chw on 2016/10/24.
 */
public class AuthenticationActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.identity_name_tv)
    TextView identityNameTv;
    @BindView(R.id.identity_name_et)
    EditText identityNameEt;
    @BindView(R.id.identity_name_delete)
    ImageView identityNameDelete;
    @BindView(R.id.identity_number_tv)
    TextView identityNumberTv;
    @BindView(R.id.identity_number_et)
    EditText identityNumberEt;
    @BindView(R.id.identity_number_delete)
    ImageView identityNumberDelete;
    @BindView(R.id.identity_idcard_positive)//zhge ?
    ImageView identityIdcardPositive;
    @BindView(R.id.identity_idcard_negative)
    ImageView identityIdcardNegative;
    @BindView(R.id.identity_btn)
    Button identityBtn;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PopupWindow popupWindow = null;
    private View popu_parentView;
    private TextView popuCamera;
    private TextView popuPhoto;
    private TextView popuCancel;
    private LinearLayout llPopup;
    private RelativeLayout popuParent;


    private Bitmap bitmap;
    private Uri imgUri;
    private int curCamera = 0;
    private int screenW;
    private File posFile; // 存储身份证正面照
    private File nagFile; // 存储身份证背面照
    private static final int POS = 0x001;
    private static final int NAG = 0x002;
    private boolean isAlbum = false;
    private CustomProgressDialog dialog = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case POS: // 身份证正面
                    changePictures("positive", (File) msg.obj);
                    break;

                case NAG: // 身份证背面
                    changePictures("negative", (File) msg.obj);
                    break;
            }
        }
    };

    /**
     * 上传身份证证件
     *
     * @param type
     */
    private void changePictures(String type, File filePath) {
        // 拼接参数
        OkGo.post(Urls.URL_UPLOAD_IDCARD)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("flag", type)
            .params("uploadfile", filePath)
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    dialog.show();
                }

                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    ToastUtil.showShort(mContext, serverResult.msg);
                    dialog.dismiss();
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "上传失败");
                    dialog.dismiss();
                }
            });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_authentication;
    }

    @Override
    protected void initData() {
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

        popuParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePopupWindow();
            }
        });
        popuCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
                closePopupWindow();
            }
        });
        popuPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoAlbum();
                closePopupWindow();
            }
        });
        popuCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePopupWindow();
            }
        });

        idTitleMiddle.setText("身份认证");
        dialog = new CustomProgressDialog(mContext, "");
        // 获取屏幕高度和宽度
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screenW = wm.getDefaultDisplay().getWidth();
        // 获取用户数据
        obtainData();
    }

    /**
     * 获取数据
     */
    private void obtainData() {
        dialog.show();

        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUtil.get(mContext, "uid", 0));
        params.put("token", SharedPreferencesUtil.get(mContext, "token", ""));

        AsyncHttpUtil.post(Urls.URL_GET_IDCARD, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setIdCardData(response);
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示数据
     *
     * @param response
     */
    private void setIdCardData(JSONObject response) {
        try {
            if (!response.getString("realname").equals("")) {
                identityNameEt.setText(response.getString("realname"));
            }
            if (!response.getString("idcard").equals("")) {
                identityNumberEt.setText(response.getString("idcard"));
            }
            if (!response.getString("idcardpositive").equals("")) {
                imageLoader.displayImage(Urls.BASE_IMGURL + response.getString("idcardpositive"), identityIdcardPositive);
            }
            if (!response.getString("idcardnegative").equals("")) {
                imageLoader.displayImage(Urls.BASE_IMGURL + response.getString("idcardnegative"), identityIdcardNegative);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交数据到后台
     */
    private void submitData() {
        // 拼接参数
        OkGo.post(Urls.URL_POST_IDCARD_DATA)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("realname", identityNameEt.getText().toString().trim())
            .params("idcard", identityNumberEt.getText().toString().trim())
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    dialog.show();
                }

                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    ToastUtil.showShort(mContext, serverResult.msg);
                    dialog.dismiss();
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "保存失败");
                    dialog.dismiss();
                }
            });
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(identityNameEt.getText().toString().trim()) || TextUtils.isEmpty(identityNumberEt.getText().toString().trim())) {
            ToastUtil.showShort(mContext, "请填写完信息");
            return false;
        }
        return true;
    }

    /**
     * 调用系统相册
     */
    private void photoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        intent.setDataAndType(imgUri, "image/*");
        startActivityForResult(intent, BasePersonalInfoActivity.REQUEST_PHOTO_ALBUM);
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
        startActivityForResult(intent, BasePersonalInfoActivity.REQUEST_CODE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BasePersonalInfoActivity.REQUEST_CODE_CAMERA:
                    if (imgUri != null && !imgUri.equals("")) {
                        // TODO 将拍照得到的图片拿去裁剪
                        cropPicture(imgUri);
                    }
                    break;

                case BasePersonalInfoActivity.REQUEST_PHOTO_ALBUM: // 调用系统相册后返回
                    isAlbum = true;
                    imgUri = data.getData();
                    System.out.println("我是从系统相册中的imgUri = " + imgUri);
                    if (imgUri != null && !imgUri.equals("")) {
                        // TODO 将拍照得到的图片拿去裁剪
                        cropPicture(imgUri);
                    }
                    break;

                case BasePersonalInfoActivity.REQUEST_CODE_CROP: // 调用系统裁剪后返回
                    System.out.println("我是拍照后的imgUri.getPath() = " + imgUri.getPath());
                    String path = "";
                    try {
                        if (imgUri != null && !imgUri.equals("")) {
                            if (isAlbum) {
                                // 获取图片的路径
                                Cursor cursor = getContentResolver().query(imgUri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                                    System.out.println("我是从系统相册中的path = " + path);
                                }
                                isAlbum = !isAlbum;
                            }

                            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imgUri));
                            // TODO 将图片显示，并发送消息到Handler中执行上传操作
                            if (curCamera == BasePersonalInfoActivity.CAMERA_ONE) {
                                // 身份证正面照
                                identityIdcardPositive.setImageBitmap(bitmap);

                                if (!path.equals("")) {
                                    posFile = new File(path);
                                } else {
                                    posFile = new File(imgUri.getPath());
                                }
                                // 发送到Handler中
                                Message msg = new Message();
                                msg.what = POS;
                                msg.obj = posFile;
                                mHandler.sendMessage(msg);
                            } else if (curCamera == BasePersonalInfoActivity.CAMERA_TWO) {
                                // 身份证反面照
                                identityIdcardNegative.setImageBitmap(bitmap);

                                if (!path.equals("")) {
                                    nagFile = new File(path);
                                } else {
                                    nagFile = new File(imgUri.getPath());
                                }
                                // 发送到Handler中
                                Message msg = new Message();
                                msg.what = NAG;
                                msg.obj = nagFile;
                                mHandler.sendMessage(msg);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
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
        intent.putExtra("outputX", (screenW - 40));
        intent.putExtra("outputY", 450);
        // 为false则表示不返回数据
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 当图片裁剪后过小时，做填充拉伸
//        intent.putExtra("scale", true);
//        intent.putExtra("scaleUpIfNeeded", true);
        startActivityForResult(intent, BasePersonalInfoActivity.REQUEST_CODE_CROP);
    }

    /**
     * 关闭PopupWindow
     */
    private void closePopupWindow() {
        popupWindow.dismiss();
        // 调用View中的clearAnimation()方法，即可消除View上正在运行的动画效果
        llPopup.clearAnimation();
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.cancel();
            }
            dialog = null;
        }
        super.onDestroy();
    }

    @OnClick({R.id.id_title_left, R.id.identity_name_delete, R.id.identity_number_delete, R.id.identity_idcard_positive, R.id.identity_idcard_negative, R.id.identity_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.identity_name_delete: // 真实姓名-删除按钮
                identityNameEt.setText("");
                break;

            case R.id.identity_number_delete: // 证件号码-删除按钮
                identityNumberEt.setText("");
                break;

            case R.id.identity_idcard_positive: // 身份证正面照
                curCamera = BasePersonalInfoActivity.CAMERA_ONE;
                llPopup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
                popupWindow.showAtLocation(popu_parentView, Gravity.BOTTOM, 0, 0);
                break;

            case R.id.identity_idcard_negative: // 身份证反面照
                curCamera = BasePersonalInfoActivity.CAMERA_TWO;
                llPopup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
                popupWindow.showAtLocation(popu_parentView, Gravity.BOTTOM, 0, 0);
                break;

            case R.id.identity_btn: // 提交
                if (checkParams()) {
                    submitData();
                }
                break;
        }
    }
}