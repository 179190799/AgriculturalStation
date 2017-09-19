package com.rifeng.agriculturalstation.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.EeventBusBean;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 *
 * 设置分期金额
 *
 */
public class FenQiPriceActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.save_fenqi_price)
    Button saveFenqiPrice;

    private CommonAdapter<Integer> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;
    private List<Integer> numList = new ArrayList<>();

    private List<Integer> mPriceList = new ArrayList<>();
    private Map<Integer, String> priceMap = new HashMap<>();

    private int taskId;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_fen_qi_price;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("设置进度款");
        taskId = this.getIntent().getExtras().getInt("taskid");
        getFenQiNum(this.getIntent().getExtras().getInt("num"));
        initialize();
    }

    private void getFenQiNum(int num) {
        for (int i = 1; i <= num; i++) {
            numList.add(i);
        }
    }

    private void initialize() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonAdapter<Integer>(this, R.layout.item_fen_qi_price, numList) {

            @Override
            protected void convert(ViewHolder holder, final Integer integer, final int position) {
                holder.setText(R.id.item_fenqi_num, "第" + (integer.intValue()) + "期");
                final EditText priceET = holder.getView(R.id.item_fenqi_price);

                priceET.setText(null);
                priceET.setTag(position);
                priceET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!TextUtils.isEmpty(s.toString().trim())){
                            priceMap.put((int) priceET.getTag(), s.toString().trim());
                        }
                    }
                });
                priceET.setText(priceMap.get(position));
            }
        };

        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);
        // 设置适配器数据
        recyclerView.setAdapter(mLoadMoreWrapper);
    }

    @OnClick({R.id.id_title_left, R.id.save_fenqi_price})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left:
                finish();
                break;

            case R.id.save_fenqi_price: // 保存
                    if(priceMap.size() < numList.size()){
                        ToastUtil.showShort(FenQiPriceActivity.this, "请先设置完分期数");
                    }else {
                        for(int i = 0; i < priceMap.size(); i++){
                            mPriceList.add(Integer.valueOf(priceMap.get(i)));
                        }
//                        for(int k = 0; k < mPriceList.size(); k++){
//                            LogUtil.i("TAG", "Value ---> " + mPriceList.get(k));
//                        }
                        stagesSubmit();
                    }
                break;
        }
    }

    private void stagesSubmit() {
        Gson gson = new Gson();
        String toJson = gson.toJson(mPriceList);
        Log.e(TAG, "taskId: "+taskId);
        Log.e(TAG, "toJson: "+toJson);

        // 拼接参数
        OkGo.post(Urls.URL_TASK_STAGES)
            .tag(this)
            .params("taskid", taskId)
            .params("stages", toJson)
//            .addUrlParams("stages", mPriceList)
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    ToastUtil.showShort(FenQiPriceActivity.this, serverResult.msg);
                    if (serverResult.code!=-1) {
                        EventBus.getDefault().post(new EeventBusBean("支付成功"));
                        finish();
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "网络错误");
                }
            });
    }
}
