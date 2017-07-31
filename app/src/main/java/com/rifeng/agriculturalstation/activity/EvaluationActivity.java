package com.rifeng.agriculturalstation.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.EvaluationBean;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 评价
 * <p/>
 * Created by chw on 2016/11/1.
 */
public class EvaluationActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight; // 提交
    @BindView(R.id.evaluat_content)
    EditText evaluatContent; // 内容
    @BindView(R.id.good_rb)
    RadioButton goodRb; // 好评
    @BindView(R.id.medium_rb)
    RadioButton mediumRb; // 中评
    @BindView(R.id.bad_rb)
    RadioButton badRb; // 差评
    @BindView(R.id.evaluat_rg)
    RadioGroup evaluatRg;
    @BindView(R.id.other_comment_tv)
    TextView otherCommentTv;
    @BindView(R.id.tip_no_comment)
    TextView tipNoComment;
    @BindView(R.id.farmer_rating_tv)
    TextView farmerRatingTv;
    @BindView(R.id.farmer_comment_tv)
    TextView farmerCommentTv;

    private int taskId; // 任务的id
    private CustomProgressDialog dialog;
    private int rating = 1; // 评价等级：1 好评（默认） 0 中评  -1 差评
    private int regtype;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x110) {
                EvaluationBean evaluationBean = (EvaluationBean) msg.obj;
                if (regtype == 1) { // 当前用户是农场主
                    setFarmerComment(evaluationBean);
                    Log.e(TAG, "-------------regtype:"+regtype );
                } else if (regtype == 2) { // 当前用户是农机主
                    setOwnerComment(evaluationBean);
                }
            }
        }
    };

    private void setOwnerComment(EvaluationBean evaluationBean) {
        otherCommentTv.setText("农场主的评论");
        if (!TextUtils.isEmpty(evaluationBean.ownercomment)) {
            evaluatContent.setText(evaluationBean.ownercomment); // 我的评论
            evaluatContent.setFocusable(false);
            evaluatContent.setKeyListener(null);
            idTitleRight.setVisibility(View.GONE); // 隐藏提交按钮

            switch (evaluationBean.oc_level) {
                case 1: // 好评
                    goodRb.setChecked(true);
                    break;

                case 0: // 中评
                    mediumRb.setChecked(true);
                    break;

                case -1: // 差评
                    badRb.setChecked(true);
                    break;
            }
            goodRb.setKeyListener(null);
            mediumRb.setKeyListener(null);
            badRb.setKeyListener(null);
        }
        if(!TextUtils.isEmpty(evaluationBean.farmercomment)){ // 如果农机主的评论不为空，则说明农机主已经评论了
            switch (evaluationBean.fc_level) {
                case 1: // 好评
                    farmerRatingTv.setText("好评");
                    break;

                case 0: // 中评
                    farmerRatingTv.setText("中评");
                    break;

                case -1: // 差评
                    farmerRatingTv.setText("差评");
                    break;
            }
            farmerCommentTv.setText(evaluationBean.farmercomment);
        } else {
            tipNoComment.setVisibility(View.VISIBLE); // 否则农机主还没有评论
        }
    }

    private void setFarmerComment(EvaluationBean evaluationBean) {
        otherCommentTv.setText("农机主的评论");
        if (!TextUtils.isEmpty(evaluationBean.farmercomment)) {
            evaluatContent.setText(evaluationBean.farmercomment); // 我的评论
            evaluatContent.setFocusable(false);
            evaluatContent.setKeyListener(null);
            idTitleRight.setVisibility(View.GONE); // 隐藏提交按钮

            switch (evaluationBean.fc_level) {
                case 1: // 好评
                    goodRb.setChecked(true);
                    break;

                case 0: // 中评
                    mediumRb.setChecked(true);
                    break;

                case -1: // 差评
                    badRb.setChecked(true);
                    break;
            }
            goodRb.setKeyListener(null);
            mediumRb.setKeyListener(null);
            badRb.setKeyListener(null);
        }
        if(!TextUtils.isEmpty(evaluationBean.ownercomment)){ // 如果农机主的评论不为空，则说明农机主已经评论了
            switch (evaluationBean.oc_level) {
                case 1: // 好评
                    farmerRatingTv.setText("好评");
                    break;

                case 0: // 中评
                    farmerRatingTv.setText("中评");
                    break;

                case -1: // 差评
                    farmerRatingTv.setText("差评");
                    break;
            }
            farmerCommentTv.setText(evaluationBean.ownercomment);
        } else {
            tipNoComment.setVisibility(View.VISIBLE); // 否则农机主还没有评论
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_evaluation;
    }

    @Override
    protected void initData() {
        regtype = (int) SharedPreferencesUtil.get(this, Consts.USER_REGTYPE, 0);
        dialog = new CustomProgressDialog(mContext, "正在提交...");
        idTitleMiddle.setText("评价");
        taskId = this.getIntent().getExtras().getInt("taskid", 0);
        idTitleRight.setText("提交");

        //获取评价的内容
        getEvaluationData();
    }

    /**
     * 获取评价的内容
     */
    private void getEvaluationData() {
        dialog.show();

        // 拼接参数
        OkGo.post(Urls.URL_GET_EVALUATION)
            .tag(this)
            .params("taskid", taskId)
            .params("regtype", regtype)
            .execute(new JsonCallback<EvaluationBean>() {
                @Override
                public void onSuccess(EvaluationBean evaluationBean, Call call, Response response) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (evaluationBean != null) {
                        Message msg = new Message();
                        msg.what = 0x110;
                        msg.obj = evaluationBean;
                        mHandler.sendMessage(msg);
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "网络错误");
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(evaluatContent.getText().toString().trim())) {
            ToastUtil.showShort(mContext, "请填写完整信息");
            return false;
        }
        return true;
    }

    /**
     * 提交评价内容
     */
    private void submmitEvaluation() {
        dialog.show();

        // 拼接参数
        OkGo.post(Urls.URL_POST_EVALUATION)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0))
            .params("taskid", taskId)
            .params("rating", rating)
            .params("regtype", (int) SharedPreferencesUtil.get(mContext, Consts.USER_REGTYPE, 0))
            .params("comment", evaluatContent.getText().toString().trim())
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (serverResult != null) {
                        ToastUtil.showShort(mContext, serverResult.msg);
                        finish();
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "网络错误");
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
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

    @OnClick({R.id.id_title_left, R.id.id_title_right, R.id.id_title_middle, R.id.good_rb, R.id.medium_rb, R.id.bad_rb})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.id_title_right: // 提交评价
                if (checkParams()) {
                    submmitEvaluation();
                }
                break;

            case R.id.good_rb: // 好评
                rating = 1;
                break;

            case R.id.medium_rb: // 中评
                rating = 0;
                break;

            case R.id.bad_rb: // 差评
                rating = -1;
                break;
        }
    }
}
