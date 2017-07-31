package com.rifeng.agriculturalstation.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ForumBean;
import com.rifeng.agriculturalstation.bean.ForumCommentBean;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.DateUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 帖子详情
 * <p/>
 * Created by chw on 2016/11/7.
 */
public class ForumDetailsActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.forum_title)
    TextView forumTitle;
    @BindView(R.id.forum_time)
    TextView forumTime;
    @BindView(R.id.forum_content)
    TextView forumContent;
    @BindView(R.id.comments_iv)
    ImageView commentsIv;
    @BindView(R.id.forum_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.hide_down)
    TextView hideDown;
    @BindView(R.id.comment_content)
    EditText commentContent;
    @BindView(R.id.comment_send)
    Button commentSend;
    @BindView(R.id.rl_comment)
    RelativeLayout rlComment;

    private ForumBean forumBean;
    private ArrayList<ForumCommentBean> commentBeenList = new ArrayList<>();
    private CommonAdapter<ForumCommentBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;
    private CustomProgressDialog dialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_forumdetails;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("帖子详情");
        dialog = new CustomProgressDialog(this, "正在加载...");

        forumBean = (ForumBean) getIntent().getSerializableExtra("ForumBean");
        setForumData();
        // 获取评论数据
        getCommentsData();
        initialize();
    }

    private void initialize() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<ForumCommentBean>(this, R.layout.item_forumcomment, commentBeenList) {

            @Override
            protected void convert(ViewHolder holder, ForumCommentBean forumCommentBean, int position) {
                holder.setText(R.id.item_comment_name, forumCommentBean.getUsername() + "：");
                holder.setText(R.id.item_comment_content, forumCommentBean.getContent());
            }
        };

        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);
        // 设置适配器数据
        mRecyclerView.setAdapter(mLoadMoreWrapper);
    }

    /**
     * 获取评论数据
     */
    private void getCommentsData() {
        dialog.show();

        RequestParams params = new RequestParams();
        params.put("id", forumBean.id);

        AsyncHttpUtil.post(Urls.URL_FORUM_COMMENTS, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                Gson gson = new Gson();
                List<ForumCommentBean> vol = gson.fromJson(response.toString(), new TypeToken<List<ForumCommentBean>>() {
                }.getType());
                if (vol != null && vol.size() > 0) {
                    commentBeenList.addAll(vol);
                }
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mLoadMoreWrapper.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    /**
     * 设置数据
     */
    private void setForumData() {
        // 标题
        forumTitle.setText(forumBean.name);
        // 发布时间
        forumTime.setText("发布时间：" + DateUtil.getTime(forumBean.dateline + "", "yyyy-MM-dd"));
        // 内容
        forumContent.setText(Html.fromHtml(forumBean.content));
    }

    /**
     * 发送评论
     */
    private void sendComment() {
        if (TextUtils.isEmpty(commentContent.getText().toString().trim())) {
            ToastUtil.showShort(this, "请输入内容");
        } else {
            RequestParams params = new RequestParams();
            params.put("uid", SharedPreferencesUtil.get(this, Consts.USER_UID, 0));
            params.put("articleid", forumBean.id);
            params.put("content", commentContent.getText().toString().trim());

            AsyncHttpUtil.post(Urls.URL_SEND_COMMENTS, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        ToastUtil.showShort(ForumDetailsActivity.this, response.getString("msg"));
                        if (response.getInt("code") == 200) { // 评论成功
                            ForumCommentBean commentBean = new ForumCommentBean();
                            commentBean.setId(response.getInt("id"));
                            commentBean.setUsername(response.getString("username"));
                            commentBean.setContent(response.getString("content"));
                            commentBean.setDateline(DateUtil.getTime(response.getInt("dateline") + "", "yyyy-MM-dd"));
                            commentBeenList.add(0, commentBean);
                            mLoadMoreWrapper.notifyDataSetChanged();

                            // 关闭软键盘
                            InputMethodManager im = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            im.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
                            // 清空输入框内容
                            commentContent.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    ToastUtil.showShort(ForumDetailsActivity.this, "评论失败");
                }
            });
        }
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

    @OnClick({R.id.id_title_left, R.id.comments_iv, R.id.hide_down, R.id.comment_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.comments_iv: // 评论
                // 弹出输入法
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                // 显示评论输入框
                rlComment.setVisibility(View.VISIBLE);
                commentContent.setFocusable(true);
                commentContent.setFocusableInTouchMode(true);
                commentContent.requestFocus();
                break;

            case R.id.hide_down: // 隐藏
                // 隐藏评论输入框
                rlComment.setVisibility(View.GONE);
                // 隐藏输入法，然后暂存当前输入框的内容，方便下次使用
                InputMethodManager im = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
                break;

            case R.id.comment_send: // 发送
                sendComment();
                break;
        }
    }
}
