package com.rifeng.agriculturalstation.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的银行卡
 * <p>
 * Created by chw on 2016/11/23.
 */
public class MyBankCardActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight;
    @BindView(R.id.bankcard_recyclerview)
    RecyclerView bankcardRecyclerview;

    private ObjectAnimator anim; // 属性动画

    @Override
    protected int getContentViewId() {
        return R.layout.activity_mybankcard;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("我的银行卡");
        Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.add_farm_right);
        // 必须设置图片大小，否则不显示
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
        idTitleRight.setCompoundDrawables(drawableLeft, null, null, null);
        /**
         * rotation 表明要执行旋转
         * 属性动画
         */
        anim = ObjectAnimator.ofFloat(idTitleRight, "rotation", 0f, 45f);
        anim.setDuration(200); // 设置动画持续时长
        // 如果只需要知道一种状态，那么可以使用AnimatorListenerAdapter
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                startActivity(AddFarmActivity.class); // 添加农场
            }
        });

        obtainData();
    }

    private void obtainData() {
        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0));

        AsyncHttpUtil.post("", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
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

            case R.id.id_title_right: // 添加银行卡
                anim.start();
                break;
        }
    }
}
