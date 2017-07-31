package com.rifeng.agriculturalstation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.MyMessageBean;
import com.rifeng.agriculturalstation.bean.TradeAlertsBean;
import com.rifeng.agriculturalstation.utils.DateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 行业快讯--详情
 * <p>
 * Created by chw on 2016/11/7.
 */
public class TradeAlertsDetailsActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.tad_title)
    TextView tadTitle;
    @BindView(R.id.tad_author)
    TextView tadAuthor;
    @BindView(R.id.tad_time)
    TextView tadTime;
    @BindView(R.id.tad_content)
    TextView tadContent;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_tradealertsdetails;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.getString("flag").equals("Trade")) { // 行业快讯
            idTitleMiddle.setText("快讯详情");
            TradeAlertsBean tradeAlertsBean = (TradeAlertsBean) bundle.getSerializable("TradeAlertsBean");
            setDatas(tradeAlertsBean);
        } else if (bundle.getString("flag").equals("Msg")) { // 我的消息
            idTitleMiddle.setText("消息详情");
            MyMessageBean msgBean = (MyMessageBean) bundle.getSerializable("msgBean");
            setMsgData(msgBean);
        }
    }

    /**
     * 我的消息
     * <p>
     * 显示数据
     *
     * @param msgBean
     */
    private void setMsgData(MyMessageBean msgBean) {
        tadTitle.setText(msgBean.getTitle());
        tadAuthor.setVisibility(View.GONE);
        tadTime.setText(DateUtil.getTime(msgBean.getDateline() + "", "yyyy-MM-dd"));
        tadContent.setText(msgBean.getContent());
    }

    /**
     * 行业快讯
     * <p>
     * 显示数据
     *
     * @param tradeAlertsBean
     */
    private void setDatas(TradeAlertsBean tradeAlertsBean) {
        tadTitle.setText(tradeAlertsBean.name);
//        tad_author.setText("发布者：" + tradeAlertsBean.getAuthor());
        tadTime.setText(DateUtil.getTime(tradeAlertsBean.dateline + "", "yyyy-MM-dd"));
        tadContent.setText(Html.fromHtml(tradeAlertsBean.content));
    }

    @OnClick({R.id.id_title_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;
        }
    }
}
