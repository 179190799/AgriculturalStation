package com.rifeng.agriculturalstation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.TaskBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 搜索界面
 * <p>
 * Created by chw on 2017/1/18.
 */
public class SearchActivity extends BaseActivity {

    @BindView(R.id.back_img)
    ImageView getBack;
    @BindView(R.id.auto_text)
    AutoCompleteTextView autoTextView;
    @BindView(R.id.del_autotext)
    ImageView clearAutoText;
    @BindView(R.id.search_text)
    TextView searchTV;
    @BindView(R.id.search_recycler)
    RecyclerView mRecyclerView;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private ArrayAdapter<String> arrayAdapter;
    private CustomProgressDialog dialog;
    private ArrayList<TaskBean> taskBeenList = new ArrayList<>();
    private CommonAdapter<TaskBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_search;
    }

    @OnTextChanged(value = R.id.auto_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s) {
        if (autoTextView.getText().toString().trim().length() > 0) {
            clearAutoText.setVisibility(View.VISIBLE);
        } else {
            clearAutoText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(this, "正在搜索...");

        String[] arr = {"aa", "gfdasdf", "gfdwrwer", "gfdgd", "gfdgdg", "gfdlkjl", "gfdsdfdsfsfdsfdsfdsfsfwsewrewrwrwerwer"};
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        autoTextView.setAdapter(arrayAdapter);

        initialize();
    }

    private void initialize() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<TaskBean>(this, R.layout.item_taskcenter, taskBeenList) {

            @Override
            protected void convert(ViewHolder holder, TaskBean taskBean, int position) {
                if (!TextUtils.isEmpty(taskBean.picfilepath)) {
                    imageLoader.displayImage(Urls.BASE_IMGURL + taskBean.picfilepath, (ImageView) holder.getView(R.id.taskcenter_item_img));
                }
                holder.setText(R.id.taskcenter_item_title, taskBean.name);
                holder.setText(R.id.taskcenter_item_crops, String.valueOf(Html.fromHtml(taskBean.content)));
                holder.setText(R.id.taskcenter_item_area, "作业面积：" + taskBean.operatingarea + "亩");
                holder.setText(R.id.taskcenter_item_totalPrice, "项目款：￥" + taskBean.totalprice);
                holder.setText(R.id.taskcenter_item_undertakeType, taskBean.meetuser);
                holder.setText(R.id.taskcenter_item_endtime, "竞标截止日期：" + taskBean.endtime);
            }
        };

        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);
        // 设置适配器数据
        mRecyclerView.setAdapter(mLoadMoreWrapper);
        // 设置item点击事件
        mAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                Intent intent = new Intent(SearchActivity.this, TaskCenterDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("TaskBean", taskBeenList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);

                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    private void searchData() {
        if (TextUtils.isEmpty(autoTextView.getText().toString().trim())) {
            ToastUtil.showShort(this, "请输入搜索内容");
            return;
        }
        dialog.show();
        // 拼接参数
        OkGo.post(Urls.URL_TASK_SEARCH)
                .tag(this)
                .params("name", autoTextView.getText().toString().trim())
                .execute(new JsonCallback<List<TaskBean>>() {
                    @Override
                    public void onSuccess(List<TaskBean> taskBeanList, Call call, Response response) {
                        dialog.dismiss();
                        if (taskBeanList.size() > 0) {
                            taskBeenList.addAll(taskBeanList);
                        }
                        mLoadMoreWrapper.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "查询失败");
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
    }

    @OnClick({R.id.back_img, R.id.del_autotext, R.id.search_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_img: // 返回
                finish();
                break;

            case R.id.del_autotext: // 清空
                autoTextView.setText(null);
                break;

            case R.id.search_text: // 搜索
                searchData();
                break;
        }
    }
}
