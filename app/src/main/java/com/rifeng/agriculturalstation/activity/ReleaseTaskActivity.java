package com.rifeng.agriculturalstation.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
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
import com.rifeng.agriculturalstation.bean.ServerReleaseTaskBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.ChangeAddressPopwindow;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.DateUtil;
import com.rifeng.agriculturalstation.utils.ImageUtils;
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
 * 发布任务
 * <p/>
 * Created by chw on 2016/10/26.
 */
public class ReleaseTaskActivity extends BaseActivity implements View.OnTouchListener {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight; // 发布
    @BindView(R.id.rt_task_name)
    EditText rtTaskName; // 任务名称
    @BindView(R.id.rt_task_describe)
    EditText rtTaskDescribe; // 任务描述
    @BindView(R.id.rt_work_area)
    EditText rtWorkArea; // 作业面积
    @BindView(R.id.rt_schedule_et)
    EditText rtScheduleEt; // 工期要求
    @BindView(R.id.rt_bidding_et)
    EditText rtBiddingEt; // 竞标最高限价
    @BindView(R.id.rt_totalprice_et)
    EditText rtTotalpriceEt; // 总价
    @BindView(R.id.rt_worktime_et)
    TextView rtWorktimeEt; // 进场开工时间
    @BindView(R.id.rt_place_tv)
    TextView rtPlaceTv; // 所在区域
    @BindView(R.id.rt_address)
    EditText rtAddress; // 具体位置
    @BindView(R.id.release_spinner)
    Spinner mSpinner;
    @BindView(R.id.recycler_view)
    MultiPickResultView recyclerView;
    @BindView(R.id.rt_end_time_et)
    TextView rtEndTime;//竞标截止日期

    private String defaultProvince = "广西"; // 省份，默认值
    private String defaultCity = "南宁"; // 城市，默认值
    private int mYear; // 年
    private int mMonth; // 月
    private int mDay; // 日
    private String[] mItems;
    private int needstar; // 可接用户
    private int startactivity = 2; //表示本页面启动的支付保证金页面
    private CustomProgressDialog dialog;
    private List<String> selectImgs;
    private List<File> fileList = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_releasetask;
    }

    @OnItemSelected(value = R.id.release_spinner, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        needstar = position;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(mContext, "正在发布...");
        idTitleMiddle.setText("发布任务");
        idTitleRight.setText("发布");
        idTitleRight.setTextColor(Color.WHITE);
        // 建立数据源
        mItems = getResources().getStringArray(R.array.permit_user);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 绑定Adapter到控件
        mSpinner.setAdapter(spinnerAdapter);
        recyclerView.init(this, MultiPickResultView.ACTION_SELECT, null);
        initDate();
    }

    /**
     * 获取当前的年月日
     */
    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        // 得到年份
        mYear = calendar.get(Calendar.YEAR);
        // 得到月份，由于月份是从0开始的，所以加1
        mMonth = calendar.get(Calendar.MONTH);
        // 得到日
        mDay = calendar.get(Calendar.DATE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 解决EditText跟ScrollView滑动冲突
        v.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    /**
     * 日期对话框
     */
    private void showDateDialog() {
        new DatePickerDialog(ReleaseTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selMonth = "";
                String selDay = "";
                if ((monthOfYear + 1) < 10) {
                    selMonth = "0" + (monthOfYear + 1);
                } else {
                    selMonth = "" + (monthOfYear + 1);
                }
                if (dayOfMonth < 10) {
                    selDay = "0" + dayOfMonth;
                } else {
                    selDay = "" + dayOfMonth;
                }
                rtWorktimeEt.setText(year + "-" + selMonth + "-" + selDay);
            }
        }, mYear, mMonth, mDay).show();
    }

    /**
     * 选择所在区域
     */
    private void selectNativePlace() {
        // 实例化对象
        ChangeAddressPopwindow mChangeAddressPopwindow = new ChangeAddressPopwindow(ReleaseTaskActivity.this, defaultProvince, defaultCity, "");
        // 设置显示的位置
        mChangeAddressPopwindow.showAtLocation(idTitleLeft, Gravity.BOTTOM, 0, 0);
        mChangeAddressPopwindow.setAddresskListener(new ChangeAddressPopwindow.OnAddressCListener() {
            @Override
            public void onClick(String province, String city, String areas) {
                rtPlaceTv.setText(province + "-" + city);
                defaultProvince = province;
                defaultCity = city;
            }
        });
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(rtTaskName.getText().toString().trim()) || TextUtils.isEmpty(rtTaskDescribe.getText().toString().trim())
                || TextUtils.isEmpty(rtWorkArea.getText().toString().trim()) || TextUtils.isEmpty(rtScheduleEt.getText().toString().trim())
                || TextUtils.isEmpty(rtBiddingEt.getText().toString().trim()) || TextUtils.isEmpty(rtTotalpriceEt.getText().toString().trim())
                || TextUtils.isEmpty(rtWorktimeEt.getText().toString().trim()) || TextUtils.isEmpty(rtAddress.getText().toString().trim())) {
            ToastUtil.showShort(mContext, "请填写完整信息");
            return false;
        }
        return true;
    }

    /**
     * 发布任务
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
                            fileList.add(ImageUtils.bitmap2File(selectImgs.get(i), 480, 800, ReleaseTaskActivity.this));
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
        OkGo.post(Urls.URL_RELEASE_TASK)
                .tag(this)
                .params(params)
                .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
                .params("name", rtTaskName.getText().toString().trim())
                .params("content", rtTaskDescribe.getText().toString().trim())
                .params("operatingarea", rtWorkArea.getText().toString().trim())
                .params("timelimit", rtScheduleEt.getText().toString().trim())
                .params("limitedprice", rtBiddingEt.getText().toString().trim())
                .params("totalprice", rtTotalpriceEt.getText().toString().trim())
                .params("starttime", DateUtil.getTimeStamp(rtWorktimeEt.getText().toString().trim(), "yyyy-MM-dd"))
                .params("enddate", DateUtil.getTimeStamp(rtEndTime.getText().toString().trim(), "yyyy-MM-dd"))
                .params("detailaddress", rtAddress.getText().toString().trim())
                .params("needstar", needstar)
                .params("provinces", defaultProvince)
                .params("city", defaultCity)
                .execute(new JsonCallback<ServerReleaseTaskBean>() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onSuccess(ServerReleaseTaskBean serverReleaseTaskBean, Call call, Response response) {
                        if (serverReleaseTaskBean.getCode() == 200) {
                            ToastUtil.showShort(mContext, serverReleaseTaskBean.msg);

                            int taskid = serverReleaseTaskBean.getTaskid();//发布的项目id
                            double taskmoney = serverReleaseTaskBean.getTaskmoney();//发布项目需要付的项目保证金
                            Bundle bundle = new Bundle();
                            bundle.putDouble("taskmoney", taskmoney);
                            bundle.putInt("taskid", taskid);
                            bundle.putInt("startactivity",startactivity);
                            startActivity(PayProjectBond.class, bundle);
                            dialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "提交失败");
                        dialog.dismiss();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerView.onActivityResult(requestCode, resultCode, data);

        selectImgs = recyclerView.getPhotos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.cancel();
            }
            dialog = null;
        }
    }

    @OnClick({R.id.id_title_left, R.id.id_title_right, R.id.rt_task_describe, R.id.rt_worktime_et, R.id.rt_place_tv, R.id.rt_address, R.id.rt_end_time_et})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.id_title_right: // 发布
                if (checkParams()) {
                    dialog.show();
                    farmUpload();
                }
                break;

            case R.id.rt_worktime_et: // 进场开工时间
                showDateDialog();
                break;

            case R.id.rt_end_time_et: // 进场开工时间

                new DatePickerDialog(ReleaseTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selMonth = "";
                        String selDay = "";
                        if ((monthOfYear + 1) < 10) {
                            selMonth = "0" + (monthOfYear + 1);
                        } else {
                            selMonth = "" + (monthOfYear + 1);
                        }
                        if (dayOfMonth < 10) {
                            selDay = "0" + dayOfMonth;
                        } else {
                            selDay = "" + dayOfMonth;
                        }
                        rtEndTime.setText(year + "-" + selMonth + "-" + selDay);
                    }
                }, mYear, mMonth, mDay).show();

                break;

            case R.id.rt_place_tv: // 所在区域
                selectNativePlace();
                break;

        }
    }
}
