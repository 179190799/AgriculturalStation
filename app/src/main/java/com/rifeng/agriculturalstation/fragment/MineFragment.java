package com.rifeng.agriculturalstation.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseFragment;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.activity.AccountInfoActivity;
import com.rifeng.agriculturalstation.activity.AuthenticationActivity;
import com.rifeng.agriculturalstation.activity.BalancePaymentsActivity;
import com.rifeng.agriculturalstation.activity.BasePersonalInfoActivity;
import com.rifeng.agriculturalstation.activity.LoginActivity;
import com.rifeng.agriculturalstation.activity.MessageListActivity;
import com.rifeng.agriculturalstation.activity.MyReleaseTaskActivity;
import com.rifeng.agriculturalstation.activity.PasswordManagerActivity;
import com.rifeng.agriculturalstation.activity.PersonalInfoActivity;
import com.rifeng.agriculturalstation.activity.PunishmentActivity;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;
import com.rifeng.agriculturalstation.view.CircleImageView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 我的
 * <p>
 * Created by chw on 2016/10/18.
 */
public class MineFragment extends BaseFragment {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight;
    @BindView(R.id.mine_go_login)
    TextView mineGoLogin;
    @BindView(R.id.mine_avatar)
    CircleImageView mineAvatar;
    @BindView(R.id.mine_username)
    TextView mineUsername;
    @BindView(R.id.mine_phone)
    TextView minePhone;
    @BindView(R.id.mine_username_rl)
    RelativeLayout mineUsernameRl;
    @BindView(R.id.mine_personal_info)
    TextView minePersonalInfo;
    @BindView(R.id.mine_release_task)
    TextView mineReleaseTask;
    @BindView(R.id.mine_account_info)
    TextView mineAccountInfo;
    @BindView(R.id.mine_identity_certification)
    TextView mineIdentityCertification;
    @BindView(R.id.mine_income_spending)
    TextView mineIncomeSpending;
    @BindView(R.id.mine_password_manager)
    TextView minePasswordManager;
    @BindView(R.id.mine_my_message)
    TextView mineMyMessage;
    @BindView(R.id.mine_reward_punish)
    TextView mineRewardPunish;
    @BindView(R.id.mine_safe_exit)
    TextView mineSafeExit;
    @BindView(R.id.safe_exit_view)
    View safeExitView;
    @BindView(R.id.ll_account_starts)
    LinearLayout starts;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private CustomProgressDialog dialog;
    private boolean falg = false;
    private int regType; // 发布的任务（农场主 1）/ 参与的任务（农机手 2）

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(getActivity(), "请稍候...");
        setLevel((Integer) SharedPreferencesUtil.get(getActivity(),Consts.USER_STAR,0));
        Drawable drawableLeft = getActivity().getResources().getDrawable(R.mipmap.common_title_right);
        // 必须设置图片大小，否则不显示
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
        idTitleRight.setCompoundDrawables(drawableLeft, null, null, null);
        idTitleMiddle.setText(R.string.MineFragPersonalCenter);
        idTitleLeft.setVisibility(View.GONE);
        // 发布的任务（农场主 1）/ 参与的任务（农机手 2）
        regType = (int) SharedPreferencesUtil.get(getActivity(), Consts.USER_REGTYPE, 0);
        if (regType == 1) {
            mineReleaseTask.setText("发布的任务");
        } else if (regType == 2) {
            mineReleaseTask.setText("参与的任务");
        }
        // 判断用户是否有登录
        isLogin();
    }

//    private void obtainData() {
//        RequestParams params = new RequestParams();
//        params.put("uid", SharedPreferencesUtil.get(getActivity(), Consts.USER_UID, 0));
//        AsyncHttpUtil.post(Urls.URL_ACCOUNT_INFO, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                try {
////                    int integral = response.getInt("integral");
//
//                    if (!getActivity().isFinishing()) {
////                        accountBalanceTv.setText("￥" + tempBalance);
////                        accountIntegralTv.setText(integral + "");
//                        setLevel(response.getInt("accountlevel"));
//
////                        Log.e(TAG, "tempBalance: " + "￥" + tempBalance);
////                        Log.e(TAG, "integral: " + integral + "");
////                        Log.e(TAG, "accountlevel: " + response.getInt("accountlevel"));
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//
//            }
//        });
//    }

    /**
     * 设置我的星级
     *
     * @param accountlevel
     */
    private void setLevel(int accountlevel) {
        if (accountlevel == 0) {
            ImageView imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            params.leftMargin = 5;
            params.topMargin = 6;
            params.gravity = Gravity.CENTER ;
            imageView.setBackgroundResource(R.mipmap.ic_stars);
            starts.addView(imageView, params);
        } else {

            // 星级数
            for (int i = 0; i < accountlevel; i++) {
                ImageView imageView = new ImageView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                params.leftMargin = 5;
                params.topMargin = 6;
                params.gravity = Gravity.CENTER ;
                imageView.setBackgroundResource(R.mipmap.ic_stars);
                starts.addView(imageView, params);
            }
        }
    }

    private void isLogin() {
        if ((int) SharedPreferencesUtil.get(getActivity(), Consts.USER_UID, 0) == 0) {
            // 用户没有登录
            mineUsernameRl.setVisibility(View.GONE);
            mineGoLogin.setVisibility(View.VISIBLE);
            mineGoLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            // 隐藏安全退出
            mineSafeExit.setVisibility(View.GONE);
            safeExitView.setVisibility(View.GONE);

            falg = false;
        } else {
            // 用户已经登录
            mineUsernameRl.setVisibility(View.VISIBLE);
            mineGoLogin.setVisibility(View.GONE);
            // 显示安全退出
            mineSafeExit.setVisibility(View.VISIBLE);
            safeExitView.setVisibility(View.VISIBLE);
            falg = true;
        }
    }

    @Override
    public void onResume() {
        LogUtil.i("TAG", "MineFragment --- onResume");
        // 检查用户是否登录
        isLogin();
        if (falg) {
            setLoginInfo();
        }
        super.onResume();
    }

    private void setLoginInfo() {
        mineUsername.setText("用户名：" + SharedPreferencesUtil.get(getActivity(), Consts.USER_USERNAME, ""));
        minePhone.setText(SharedPreferencesUtil.get(getActivity(), Consts.USER_PHONE, "") + "");
        if (SharedPreferencesUtil.get(getActivity(), Consts.USER_AVATAR, "") != "") {
            imageLoader.displayImage(Urls.SERVER + SharedPreferencesUtil.get(getActivity(), Consts.USER_AVATAR, ""), mineAvatar);
        }

        // 发布的任务（农场主 1）/ 参与的任务（农机手 2）
        regType = (int) SharedPreferencesUtil.get(getActivity(), Consts.USER_REGTYPE, 0);
        if (regType == 1) {
            mineReleaseTask.setText("发布的任务");
        } else if (regType == 2) {
            mineReleaseTask.setText("参与的任务");
        }
    }

    /**
     * 当Fragment对用户可见（即用户切换到此Fragment时）执行此方法
     */
    @Override
    protected void lazyLoad() {
        if (!isVisible) {
            return;
        } else {

            LogUtil.i("TAG", "MineFragment --- UI布局可见");
        }
    }

    @OnClick({R.id.id_title_right, R.id.mine_avatar, R.id.mine_go_login, R.id.mine_personal_info, R.id.mine_release_task, R.id.mine_account_info, R.id.mine_identity_certification, R.id.mine_income_spending, R.id.mine_password_manager, R.id.mine_my_message, R.id.mine_reward_punish, R.id.mine_safe_exit})
    public void onClick(View view) {
        if (!falg) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }

        switch (view.getId()) {
            case R.id.id_title_right: // 签到
                signIn();
                break;

            case R.id.mine_go_login: // 登陆
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;

            case R.id.mine_avatar: // 头像
                startActivity(new Intent(getActivity(), BasePersonalInfoActivity.class));
                break;

            case R.id.mine_personal_info: // 个人信息
                startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
                break;

            case R.id.mine_release_task: // 发布的任务（农场主 1）/ 参与的任务（农机手 2）
                startActivity(new Intent(getActivity(), MyReleaseTaskActivity.class));
                break;

            case R.id.mine_account_info: // 账户信息
                startActivity(new Intent(getActivity(), AccountInfoActivity.class));
                break;

            case R.id.mine_identity_certification: // 身份认证
                startActivity(new Intent(getActivity(), AuthenticationActivity.class));
                break;

            case R.id.mine_income_spending: // 收支明细
                startActivity(new Intent(getActivity(), BalancePaymentsActivity.class));
                break;

            case R.id.mine_password_manager: // 密码管理
                startActivity(new Intent(getActivity(), PasswordManagerActivity.class));
                break;

            case R.id.mine_my_message: // 我的消息
                startActivity(new Intent(getActivity(), MessageListActivity.class));
                break;

            case R.id.mine_reward_punish: // 奖罚制度
                startActivity(new Intent(getActivity(), PunishmentActivity.class));
                break;

            case R.id.mine_safe_exit: // 安全退出
                // 清空用户数据
                SharedPreferencesUtil.remove(getActivity(), Consts.USER_UID);
                SharedPreferencesUtil.remove(getActivity(), Consts.USER_REGTYPE);
                SharedPreferencesUtil.remove(getActivity(), Consts.USER_AVATAR);
                SharedPreferencesUtil.remove(getActivity(), Consts.USER_PHONE);
                SharedPreferencesUtil.remove(getActivity(), Consts.USER_USERNAME);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                ToastUtil.showLong(getContext(), "您已安全退出");

        }
    }

    private void signIn() {
        dialog.show();
        // 拼接参数
        OkGo.post(Urls.URL_SIGN_IN)
                .tag(this)
                .params("uid", (int) SharedPreferencesUtil.get(getActivity(), Consts.USER_UID, 0))
                .params("username", (String) SharedPreferencesUtil.get(getActivity(), Consts.USER_USERNAME, ""))
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        ToastUtil.showShort(getActivity(), serverResult.msg);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(getActivity(), "网络错误");
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }
}
