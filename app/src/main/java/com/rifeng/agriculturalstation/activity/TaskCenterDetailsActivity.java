package com.rifeng.agriculturalstation.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.bean.StagesPayBean;
import com.rifeng.agriculturalstation.bean.TaskBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.DateUtil;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;
import com.rifeng.agriculturalstation.utils.ViewPagerImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 任务详情
 * <p>
 * Created by chw on 2016/11/10.
 */
public class TaskCenterDetailsActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.alltask_details_title)
    TextView title; // 标题
    @BindView(R.id.alltask_details_crops)
    TextView crops; // 主要作物
    @BindView(R.id.alltask_details_area)
    TextView area; // 作业面积
    @BindView(R.id.alltask_details_totalprice)
    TextView totalprice; // 项目款
    @BindView(R.id.alltask_details_undertakeType)
    TextView undertakeType; // 可接类型
    @BindView(R.id.alltask_details_schedule)
    TextView schedule; // 预计工期
    @BindView(R.id.alltask_details_participationnum)
    TextView participationnum; // 参与人数
    @BindView(R.id.alltask_details_endTime)
    TextView endTime; // 截止时间
    @BindView(R.id.alltask_details_address)
    TextView address; // 详细地址
    @BindView(R.id.finish_area)
    TextView finish_area; // 完成面积
    @BindView(R.id.contact_admin)
    TextView contact_admin; // 联系管理员
    @BindView(R.id.current_progress)
    TextView current_progress; // 完成进度
    @BindView(R.id.satellite_img)
    ImageView satellite_img; // 任务卫星图
    @BindView(R.id.ongoing_task_content)
    LinearLayout ongoing_task_content;
    @BindView(R.id.alltask_details_joinbtn)
    Button joinbtn; // 参与投标
    @BindView(R.id.banner)
    Banner banner; // 轮播图

    @BindView(R.id.ll_pay_status)
    LinearLayout ll_pay_status;//支付状态父布局
    @BindView(R.id.ll_pay_pass)
    LinearLayout ll_pay_pass;//审核状态父布局
    @BindView(R.id.pay_status)
    TextView pay_status;//支付状态
    @BindView(R.id.pay_pass)
    TextView pay_pass;//审核状态


    private TaskBean taskBean;
    private int accountlevel;  //客户星级
    private String payCost; // 项目款
    private float joinmoney; //任务投标所需交付的保证金
    private int regType; // 发布的任务（农场主 1）/ 参与的任务（农机手 2）
    private int taskid;
//    private CustomProgressDialog dialog;
    private ArrayList<StagesPayBean> stagesPayList = new ArrayList<>();

    private int status;//支付状态，判断农场主是否已经支付发布任务的项目保证金，0未支付，1已支付
    private int pass;//审核状态，审核农场主发布的任务，1表示已审核，不用判断status。否则进一步判断status
    private double taskmoney; //发布项目需要付的项目保证金
    private int taskUId;//发布该任务的农场主ID
    private int uid;//本次登录的账号id
    private int startactivity=1;//表示本页面启动的


    @Override
    protected int getContentViewId() {
        return R.layout.activity_taskcenterdetails;
    }

    @Override
    protected void initData() {
//        dialog = new CustomProgressDialog(this, "正在加载...");
        // 获取注册类型，农场主/农机手
        regType = (int) SharedPreferencesUtil.get(mContext, Consts.USER_REGTYPE, 0);
        idTitleMiddle.setText("任务详情");
        taskBean = (TaskBean) getIntent().getExtras().getSerializable("TaskBean");
        joinmoney = taskBean.joinmoney;
        taskid = taskBean.id;
        status = taskBean.status;
        pass = taskBean.pass;
        taskmoney = taskBean.taskmoney;
        taskUId = taskBean.uid;
        uid = (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0);
        if (taskBean.picarr != null && taskBean.picarr.size() > 0) {
            banner.setImages(taskBean.picarr).setImageLoader(new ViewPagerImageLoader()).start();
            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "multiple");
                    bundle.putStringArrayList("select", (ArrayList<String>) taskBean.picarr);
                    startActivity(PhotoViewActivity.class, bundle);
                }
            });
        } else {
            banner.setBackgroundResource(R.mipmap.no_picture);
        }
        Log.e(TAG, "------ taskBean.id : ------ " + taskBean.id);
        setDatas();
        getAccountLevel();
    }

    /**
     * 初始化控件，并设置数据
     */
    private void setDatas() {
        LogUtil.e("status:"+status);
        LogUtil.e("pass:"+pass);

        ll_pay_pass.setVisibility(View.VISIBLE);
        ll_pay_status.setVisibility(View.VISIBLE);
            if (pass != 1) {//未审核
                if (status != 1) {//未支付
                    pay_status.setEnabled(true);
                } else {
                    pay_status.setText("已支付");
                    pay_status.setEnabled(false);
                }
            } else {
                pay_pass.setText("已审核");
                ll_pay_status.setVisibility(View.GONE);
            }




        switch (taskBean.curstatu) {
            case 1: // 竞标中
                joinbtn.setText("参与投标");
                break;

            case 2: // 作业中
                if (regType == 1) {
                    joinbtn.setText("支付进度款");
                } else {
                    joinbtn.setText("确认支付进度款");
                }
                break;

            case 3: // 已结束
                joinbtn.setVisibility(View.GONE);
                break;
        }

        // 标题
        title.setText("项目标题：" + taskBean.name);
        // 主要作物
        crops.setText(Html.fromHtml(taskBean.content));
        // 作业面积
        area.setText("作业面积：" + taskBean.operatingarea + "亩");
        // 项目款
        payCost = taskBean.totalprice + "";
        totalprice.setText("项目款：￥" + payCost);
        // 可接类型
        undertakeType.setText(taskBean.needstar + "星以上用户可接");
        // 预计工期
        schedule.setText("要求工期：" + taskBean.timelimit + "天");
        // 已参与人数
        participationnum.setText(Html.fromHtml("目前已有<font color='#228793'>" +
                taskBean.participationnum + "</font>人参与投标"));
        // 截止时间
        endTime.setText("截止日期：" + DateUtil.getTime(taskBean.enddate + "", "yyyy-MM-dd"));
        // 详细地址
        address.setText("具体地址：" + taskBean.detailaddress);

    }

    private void alertDialogFenQi() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        View view = getLayoutInflater().inflate(R.layout.dialog_fenqi, null);
        final EditText fenqiET = (EditText) view.findViewById(R.id.fenqi_num);
        Button nextBtn = (Button) view.findViewById(R.id.fenqi_next);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(view);
        dialog.setTitle("支付期数");
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.height = (int) (d.getHeight() * 0.5);
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        // 下一步
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(fenqiET.getText().toString().trim()) || Integer.valueOf(fenqiET.getText().toString().trim()) <= 0) {
                    ToastUtil.showShort(TaskCenterDetailsActivity.this, "请先输入分期次数");
                } else {
                    // 下一步
                    dialog.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putInt("num", Integer.valueOf(fenqiET.getText().toString().trim()));
                    bundle.putInt("taskid", taskid);
                    startActivity(FenQiPriceActivity.class, bundle);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("TAG", "TaskCenterDetailsActivity ---> onResume");
        getStagesPay();
        setDatas();
    }


    // 获取进度款记录
    private void getStagesPay() {
        if (stagesPayList != null && stagesPayList.size() > 0) {
            stagesPayList.clear();
        }
//        dialog.show();
        // 拼接参数
        OkGo.post(Urls.URL_TASK_PAY_STAGES)
                .tag(this)
                .params("taskid", taskid)
                .execute(new JsonCallback<List<StagesPayBean>>() {
                    @Override
                    public void onSuccess(List<StagesPayBean> stagesPayBeens, Call call, Response response) {
//                        if (dialog != null && dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
                        if (stagesPayBeens.size() > 0) {
                            stagesPayList.addAll(stagesPayBeens);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "网络错误");
//                        if (dialog != null && dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
                    }
                });
    }

//    //参与投标
//    private void accpetTask() {
//        OkGo.post(Urls.URL_ACCEPT_TASK)
//                .tag(this)
//                .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0))
//                .params("taskid", taskid)
//                .execute(new JsonCallback<ServerResult>() {
//                    @Override
//                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
//
//                        if (serverResult.code == 101) {
//                            ToastUtil.showShort(mContext, serverResult.msg);
//                        } else {
//                            Bundle bundle = new Bundle();
//                            bundle.putString("payCost", payCost);
//                            bundle.putInt("taskid", taskid);
//                            startActivity(TouBiaoPayActivity.class, bundle);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        ToastUtil.showShort(mContext, "网络错误");
//                    }
//                });
//    }

    @OnClick({R.id.id_title_left, R.id.alltask_details_joinbtn, R.id.pay_status})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;
            case R.id.pay_status://支付保证金

                if (regType == 1) {

                    if (taskUId == uid) {//证明发布该任务的农场主和登录账号的农场主为一个人可以继续操作
                        Bundle bundle = new Bundle();
                        bundle.putDouble("taskmoney", taskmoney);
                        bundle.putInt("taskid", taskid);
                        bundle.putInt("startactivity", startactivity);
                        startActivity(PayProjectBond.class, bundle);
                    } else {
                        ToastUtil.showShort(mContext, "该任务不是您发布的！");
                    }

                } else {
                    ToastUtil.showShort(mContext,"农机主不能进行该操作！");
                }
                break;

            case R.id.alltask_details_joinbtn: // 参与投标
                switch (taskBean.curstatu) {
                    case 1: // 竞标中，参与投标
                        if (regType == 2) { // 农机手，能参与投标，去到支付页面

                            if (accountlevel >= taskBean.needstar) {
//                                accpetTask();
                                Bundle bundle = new Bundle();
                                bundle.putString("payCost", payCost);
                                bundle.putInt("taskid", taskid);
                                bundle.putFloat("joinmoney", joinmoney);
                                startActivity(TouBiaoPayActivity.class, bundle);

                            } else {
                                ToastUtil.showLong(this, "您的等级不足！");
                            }


                        } else if (regType == 1) {
                            ToastUtil.showShort(mContext, "农场主不能投标!");
                        }
                        break;

                    case 2: // 作业中
                        if (regType == 1) {
//                            joinbtn.setText("支付进度款");
                            if (stagesPayList.size() > 0) { // 已经设置过了分期次数
                                Bundle bundle = new Bundle();
                                bundle.putInt("taskid", taskid);
                                bundle.putParcelableArrayList("stagesPayList", stagesPayList);
                                startActivity(PayStagesActivity.class, bundle);
                            } else {
                                alertDialogFenQi();
                            }
                        } else {
//                            joinbtn.setText("确认支付进度款");
                            if (stagesPayList.size() > 0) { // 已经设置过了分期次数
                                Bundle bundle = new Bundle();
                                bundle.putInt("taskid", taskid);
                                bundle.putParcelableArrayList("stagesPayList", stagesPayList);
                                startActivity(PayStagesActivity.class, bundle);
                            }
                        }
                        break;

                    case 3: // 已结束
                        joinbtn.setVisibility(View.GONE);
                        break;
                }

                break;
        }
    }

    /**
     * 开启异步线程获取用户星级
     */
    private void getAccountLevel() {
        Log.e(TAG, "------------obtainData -------------");
        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0));

        AsyncHttpUtil.post(Urls.URL_ACCOUNT_INFO, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
//                    请求数据得到登录的账户的星级
                    accountlevel = response.getInt("accountlevel");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ToastUtil.showShort(TaskCenterDetailsActivity.this, "网络出错");
            }
        });
    }


}
