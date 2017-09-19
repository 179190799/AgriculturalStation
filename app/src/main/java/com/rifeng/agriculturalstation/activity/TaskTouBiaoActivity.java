package com.rifeng.agriculturalstation.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * Created by Administrator on 2017/9/18.
 */

public class TaskTouBiaoActivity extends BaseActivity {
    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight;
    @BindView(R.id.total_money)
    TextView totalMoney;
    @BindView(R.id.total_number)
    TextView totalNumber;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.offermoney_1)
    EditText offermoney1;
    @BindView(R.id.offerday_1)
    EditText offerday1;
    @BindView(R.id.offermoney_2)
    EditText offermoney2;
    @BindView(R.id.offerday_2)
    EditText offerday2;
    @BindView(R.id.offermoney_3)
    EditText offermoney3;
    @BindView(R.id.offerday_3)
    EditText offerday3;
    @BindView(R.id.task_finish)
    TextView taskFinish;

    private List<Integer> offermoneyList = new ArrayList<>();
    private List<Integer> offerdayList = new ArrayList<>();
    private CustomProgressDialog mDialog;

    private int taskid;
    private float joinmoney;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task_select;
    }

    @Override
    protected void initData() {
        Bundle bundle = this.getIntent().getExtras();
        taskid = bundle.getInt("taskid");
        joinmoney = bundle.getFloat("joinmoney");
        mDialog = new CustomProgressDialog(mContext,"正在加载中...");
        idTitleMiddle.setText("参与投标");

    }

    @OnClick({R.id.id_title_left,R.id.task_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left:
                finish();
                break;
            case R.id.task_finish:
                if (checkParams()) {
                    startPayActivity();
                }
                break;
        }

    }

    private void startPayActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("taskid", taskid);
        bundle.putFloat("joinmoney", joinmoney);
        bundle.putIntegerArrayList("offermoneyList", (ArrayList<Integer>) offermoneyList);
        bundle.putIntegerArrayList("offerdayList", (ArrayList<Integer>) offerdayList);
        startActivity(TouBiaoPayActivity.class,bundle);
        finish();
    }


    private boolean checkParams() {
        String money1 = offermoney1.getText().toString().trim();
        String money2 = offermoney2.getText().toString().trim();
        String money3 = offermoney3.getText().toString().trim();
        String day1 = offermoney1.getText().toString().trim();
        String day2 = offermoney2.getText().toString().trim();
        String day3 = offermoney3.getText().toString().trim();
        if (TextUtils.isEmpty(money1)) {
            ToastUtil.showShort(mContext,"请输入完整！");
            return false;
        }
        if (TextUtils.isEmpty(day1)) {
            ToastUtil.showShort(mContext,"请输入完整！");
            return false;
        }
        if (TextUtils.isEmpty(money2)) {
            ToastUtil.showShort(mContext,"请输入完整！");
            return false;
        }
        if (TextUtils.isEmpty(day2)) {
            ToastUtil.showShort(mContext,"请输入完整！");
            return false;
        }
        if (TextUtils.isEmpty(money3)) {
            ToastUtil.showShort(mContext,"请输入完整！");
            return false;
        }
        if (TextUtils.isEmpty(day3)) {
            ToastUtil.showShort(mContext,"请输入完整！");
            return false;
        }
        offerdayList.add(Integer.valueOf(day1));
        offerdayList.add(Integer.valueOf(day2));
        offerdayList.add(Integer.valueOf(day3));
        offermoneyList.add(Integer.valueOf(money1));
        offermoneyList.add(Integer.valueOf(money2));
        offermoneyList.add(Integer.valueOf(money3));
        return true;
    }

}
