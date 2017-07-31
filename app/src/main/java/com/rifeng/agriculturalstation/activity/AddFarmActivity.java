package com.rifeng.agriculturalstation.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResultModel;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.ChangeAddressPopwindow;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.ImageUtils;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.iwf.photopicker.widget.MultiPickResultView;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 添加农场
 * <p/>
 * Created by chw on 2016/10/31.
 */
public class AddFarmActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight;
    @BindView(R.id.addfarm_name)
    EditText addfarmName; // 农场名称
    @BindView(R.id.addfarm_farmer)
    EditText addfarmFarmer; // 农场所有者
    @BindView(R.id.addfarm_area)
    EditText addfarmArea; // 农场面积
    @BindView(R.id.addfarm_crops)
    EditText addfarmCrops; // 主要农作物
    @BindView(R.id.addfarm_pro_city)
    TextView addfarmProCity; // 所在区域
    @BindView(R.id.addfarm_address)
    EditText addfarmAddress; // 农场所在地
    @BindView(R.id.addfarm_photopicker)
    MultiPickResultView addfarmPhotopicker;

    private String defaultProvince = "广西"; // 省份，默认值
    private String defaultCity = "南宁"; // 城市，默认值

    private CustomProgressDialog dialog;
    private List<String> selectImgs = new ArrayList<>();
    private List<File> fileList = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_addfarm;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(mContext, "请稍候...");

        idTitleMiddle.setText("添加农场");
        idTitleRight.setText("提交");

        addfarmPhotopicker.init(this, MultiPickResultView.ACTION_SELECT, null);
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(addfarmName.getText().toString().trim()) || TextUtils.isEmpty(addfarmFarmer.getText().toString().trim())
                || TextUtils.isEmpty(addfarmArea.getText().toString().trim()) || TextUtils.isEmpty(addfarmCrops.getText().toString().trim())
                || TextUtils.isEmpty(addfarmAddress.getText().toString().trim()) || TextUtils.isEmpty(addfarmProCity.getText().toString().toString())) {
            ToastUtil.showShort(mContext, "请填写完整信息");
            return false;
        }
        return true;
    }

    /**
     * 选择所在区域
     */
    private void selectNativePlace() {
        // 实例化对象
        ChangeAddressPopwindow mChangeAddressPopwindow = new ChangeAddressPopwindow(AddFarmActivity.this, defaultProvince, defaultCity, "");
        // 设置显示的位置
        mChangeAddressPopwindow.showAtLocation(idTitleLeft, Gravity.BOTTOM, 0, 0);
        mChangeAddressPopwindow.setAddresskListener(new ChangeAddressPopwindow.OnAddressCListener() {
            @Override
            public void onClick(String province, String city, String areas) {
                addfarmProCity.setText(province + "-" + city);
                defaultProvince = province;
                defaultCity = city;
            }
        });
    }

    /**
     * 提交农场信息到后台
     */
    private void farmUpload() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (selectImgs != null && selectImgs.size() > 0) {
                    if (fileList != null) {
                        fileList.clear();
                    }
                    try {
                        for (int i = 0; i < selectImgs.size(); i++) {
                            fileList.add(ImageUtils.bitmap2File(selectImgs.get(i), 480, 800, AddFarmActivity.this));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HttpParams params = new HttpParams();
        for (int i = 0; i < fileList.size(); i++) {
            params.put("uploadfile" + i, fileList.get(i));
        }
        params.put("count", fileList.size());

        // 拼接参数
        OkGo.post(Urls.URL_ADD_FARM)
            .tag(this)
            .params(params)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("name", addfarmName.getText().toString().trim())
            .params("farmer", addfarmFarmer.getText().toString().trim())
            .params("floorspace", addfarmArea.getText().toString().trim())
            .params("mainproduct", addfarmCrops.getText().toString().trim())
            .params("provinces", defaultProvince)
            .params("city", defaultCity)
            .params("farmaddress", addfarmAddress.getText().toString().trim())
            .execute(new JsonCallback<ServerResultModel>() {
                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    LogUtil.i("CHW", "开始上传");
                }

                @Override
                public void onSuccess(ServerResultModel serverResultModel, Call call, Response response) {
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    ToastUtil.showShort(mContext, serverResultModel.msg);
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "提交失败");
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }

                @Override
                public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                    super.upProgress(currentSize, totalSize, progress, networkSpeed);

                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addfarmPhotopicker.onActivityResult(requestCode, resultCode, data);

        selectImgs = addfarmPhotopicker.getPhotos();
    }

    @OnClick({R.id.id_title_left, R.id.id_title_right, R.id.addfarm_pro_city})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.id_title_right: // 提交
                if (checkParams()) {
                    dialog.show();
                    farmUpload();
//                    submitFarmInfo();
                }
                break;

            case R.id.addfarm_pro_city: // 所在区域
                selectNativePlace();
                break;
        }
    }
}
