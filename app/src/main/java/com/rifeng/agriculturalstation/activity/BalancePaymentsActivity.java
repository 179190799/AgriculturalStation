package com.rifeng.agriculturalstation.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人中心-收支明细
 * <p/>
 * Created by chw on 2016/10/25.
 */
public class BalancePaymentsActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.bp_income)
    TextView bpIncome;
    @BindView(R.id.bp_cost)
    TextView bpCost;
    @BindView(R.id.bp_balance)
    TextView bpBalance;
    @BindView(R.id.bp_records_btn)
    Button bpRecordsBtn;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_balancepayments;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("收支明细");
        // 获取数据
        obtainData();
    }

    /**
     * 获取数据
     */
    private void obtainData() {
        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0));

        AsyncHttpUtil.post(Urls.URL_INCOME, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setDatas(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    /**
     * 显示数据
     *
     * @param response
     */
    private void setDatas(JSONObject response) {
        try {
            if (response.getDouble("income") != 0) {
                bpIncome.setText("￥" + response.getDouble("income"));
            }
            if (response.getDouble("spending") != 0) {
                bpCost.setText("￥" + response.getDouble("spending"));
            }
            if (response.getDouble("balance") != 0) {
                bpBalance.setText("￥" + response.getDouble("balance"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.id_title_left, R.id.bp_records_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.bp_records_btn: // 交易记录
                startActivity(TradingRecordActivity.class);
                break;
        }
    }
}
