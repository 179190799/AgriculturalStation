package com.rifeng.agriculturalstation.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.rifeng.agriculturalstation.utils.DateUtil;
import com.rifeng.agriculturalstation.utils.ImageUtils;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import me.iwf.photopicker.widget.MultiPickResultView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 添加农机信息
 * <p/>
 * Created by chw on 2016/11/17.
 */
public class AddLocomotiveActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight;
    @BindView(R.id.addloco_locomotive)
    EditText addlocoLocomotive; // 机车名称
    @BindView(R.id.addloco_name)
    EditText addlocoName; // 车主
    @BindView(R.id.addloco_buytime)
    TextView addlocoBuytime; // 购买时间
    @BindView(R.id.addloco_worktime)
    EditText addlocoWorktime; // 运营时间
    @BindView(R.id.addloco_area)
    TextView addlocoArea; // 所在区域
    @BindView(R.id.addloco_spinner)
    Spinner addlocoSpinner; // 工作性质
    @BindView(R.id.addloco_photopicker)
    MultiPickResultView addlocoPhotopicker;

    private String defaultProvince = "广西"; // 省份，默认值
    private String defaultCity = "南宁"; // 城市，默认值
    private int mYear; // 年
    private int mMonth; // 月
    private int mDay; // 日
    private int natureItem = 0;
    private CustomProgressDialog dialog;
    private List<String> selectImgs = new ArrayList<>();
    private List<File> fileList = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_addlocomotive;
    }

    // 工作性质
    @OnItemSelected(value = R.id.addloco_spinner, callback = OnItemSelected.Callback.ITEM_SELECTED)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        natureItem = position + 1;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(mContext, "请稍候...");

        idTitleMiddle.setText("添加农机");
        idTitleRight.setText("提交");
        addlocoPhotopicker.init(this, MultiPickResultView.ACTION_SELECT, null);

        initDate();
        // 工作性质
        spinnerNature();
    }

    /**
     * 获取当前的年月日
     */
    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        // 得到年份
        mYear = calendar.get(Calendar.YEAR);
        // 得到月份，由于月份是从0开始的，所以加1
        mMonth = calendar.get(Calendar.MONTH) + 1;
        // 得到日
        mDay = calendar.get(Calendar.DATE);
    }

    private void spinnerNature() {
        // 建立数据源
        String[] strNature = getResources().getStringArray(R.array.loco_nature);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strNature);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 绑定Adapter到控件
        addlocoSpinner.setAdapter(spinnerAdapter);
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(addlocoLocomotive.getText().toString().trim()) || TextUtils.isEmpty(addlocoName.getText().toString().trim())
                || TextUtils.isEmpty(addlocoBuytime.getText().toString().trim()) || TextUtils.isEmpty(addlocoWorktime.getText().toString().trim())
                || TextUtils.isEmpty(addlocoArea.getText().toString().trim())) {
            ToastUtil.showShort(mContext, "请填写完整信息");
            return false;
        }
        return true;
    }

    private void showDateDialog() {
        new DatePickerDialog(AddLocomotiveActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                addlocoBuytime.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        }, mYear, mMonth, mDay).show();
    }

    /**
     * 选择所在区域
     */
    private void selectNativePlace() {
        // 实例化对象
        ChangeAddressPopwindow mChangeAddressPopwindow = new ChangeAddressPopwindow(AddLocomotiveActivity.this, defaultProvince, defaultCity, "");
        // 设置显示的位置
        mChangeAddressPopwindow.showAtLocation(idTitleLeft, Gravity.BOTTOM, 0, 0);
        mChangeAddressPopwindow.setAddresskListener(new ChangeAddressPopwindow.OnAddressCListener() {
            @Override
            public void onClick(String province, String city, String areas) {
                addlocoArea.setText(province + "-" + city);
                defaultProvince = province;
                defaultCity = city;
            }
        });
    }

    /**
     * 提交数据到后台中
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
                            fileList.add(ImageUtils.bitmap2File(selectImgs.get(i), 480, 800, AddLocomotiveActivity.this));
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
        OkGo.post(Urls.URL_ADD_LOCOMOTIVE)
            .tag(this)
            .params(params)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("locomotive", addlocoLocomotive.getText().toString().trim())
            .params("locomaster", addlocoName.getText().toString().trim())
            .params("locobuytime", DateUtil.getTimeStamp(addlocoBuytime.getText().toString().trim(), "yyyy-MM-dd"))
            .params("operatingtime", addlocoWorktime.getText().toString().trim())
            .params("provinces", defaultProvince)
            .params("city", defaultCity)
            .params("naturework", natureItem, true)
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
                    finish();
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
        addlocoPhotopicker.onActivityResult(requestCode, resultCode, data);

        selectImgs = addlocoPhotopicker.getPhotos();
    }

    @OnClick({R.id.id_title_left, R.id.id_title_right, R.id.addloco_buytime, R.id.addloco_area})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left:
                finish();
                break;

            case R.id.id_title_right: // 提交
                if (checkParams()) {
                    dialog.show();
                    farmUpload();
//                    submitLocomotive();
                }
                break;

            case R.id.addloco_buytime: // 购买时间
                showDateDialog();
                break;

            case R.id.addloco_area: // 所在区域
                selectNativePlace();
                break;
        }
    }
}
