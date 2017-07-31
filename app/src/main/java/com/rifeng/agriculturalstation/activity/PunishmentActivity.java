package com.rifeng.agriculturalstation.activity;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人中心-奖罚制度
 * <p>
 * Created by chw on 2016/10/25.
 */
public class PunishmentActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.punishment_title)
    TextView punishmentTitle;
    @BindView(R.id.punishment_content)
    TextView punishmentContent;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_punishment;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("奖罚制度");
        // 获取数据
        obtainData();
    }

    private void obtainData() {
        RequestParams params = new RequestParams();

        AsyncHttpUtil.post(Urls.URL_REGIME, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    punishmentTitle.setText(response.getString("name"));
                    punishmentContent.setText(Html.fromHtml(response.getString("content")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @OnClick({R.id.id_title_left, R.id.id_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;
        }
    }
}
