package com.rifeng.agriculturalstation.utils;

/**
 * Created by chw on 2017/1/25.
 */
public class Urls {
    // 数据请求路径
    public static final String SERVER = "http://www.gxrfny.com/";
//    public static final String SERVER = "http://192.168.2.7/";

    // 图片请求路径
    public static final String BASE_IMGURL = SERVER + "attachment/image/";
    // 个人中心-账户信息
    public static final String URL_ACCOUNT_INFO = SERVER + "app-personalinfo-op-balance.html";
    // 添加农场
    public static final String URL_ADD_FARM = SERVER + "app-myfarm-op-addfarm.html";
    // 添加农机
    public static final String URL_ADD_LOCOMOTIVE = SERVER + "app-locomotive-op-addloco.html";
    // 个人中心-身份认证
    public static final String URL_UPLOAD_IDCARD = SERVER + "app-personalinfo-op-idcardpic.html"; // 上传身份证证件
    public static final String URL_GET_IDCARD = SERVER + "app-personalinfo-op-idcard.html"; // 获取数据
    public static final String URL_POST_IDCARD_DATA = SERVER + "app-personalinfo-op-idcarddata.html"; // 提交数据到后台
    public static final String URL_SIGN_IN = SERVER + "app-personalinfo-op-sign.html"; // 签到
    // 个人中心-收支明细
    public static final String URL_INCOME = SERVER + "app-personalinfo-op-income.html";
    // 个人中心-充值
    public static final String URL_RECHARGE = SERVER + "app-personalinfo-op-recharge.html";
    // 个人信息--基础信息
    public static final String URL_UPLOAD_CERTIFICATE = SERVER + "app-personalinfo-op-certificate.html"; // 上传相关证件
    public static final String URL_GET_PERSONAL_INFO = SERVER + "app-personalinfo-op-get.html"; // 获取用户数据
    public static final String URL_CHANGE_USER_INFO = SERVER + "app-personalinfo-op-change.html"; // 修改个人信息

    // 选标列表
    public static final String URL_JOINOWNER_SELECTION = SERVER + "app-joinowner-op-selection.html"; // 选标
    public static final String URL_GET_JOINOWNER_LIST = SERVER + "app-joinowner-op-list.html"; // 获取投标人列表
    // 评价
    public static final String URL_GET_EVALUATION = SERVER + "app-task-op-getevaluation.html"; // 获取评价的内容
    public static final String URL_POST_EVALUATION = SERVER + "app-task-op-evaluation.html";// 提交评价内容
    // 忘记密码
    public static final String URL_GET_AUTH_CODE = SERVER + "app-mynews-op-sms.html"; // 获取验证码
    public static final String URL_FIND_PASSWORD = SERVER + "app-register-op-change.html";// 找回密码
    // 帖子详情
    public static final String URL_FORUM_COMMENTS = SERVER + "app-forum-op-comments.html"; // 获取评论数据
    public static final String URL_SEND_COMMENTS = SERVER + "app-forum-op-releasecomments.html";// 发送评论
    // 农机手列表
    public static final String URL_ALL_LOCOMOTIVE_LIST = SERVER + "app-locomotive-op-all.html"; // 所有的农机主
    public static final String URL_OWNER_LIST = SERVER + "app-locomotive-op-ownerdetails.html"; // 农机的详情
    // 登录
    public static final String URL_USER_LOGIN = SERVER + "app-login-op-login.html";
    // 个人中心-我的消息
    public static final String URL_MYNEWS_LIST = SERVER + "app-mynews-op-list.html"; // 消息列表
    public static final String URL_MYNEWS_DELETE = SERVER + "app-mynews-op-delete.html"; // 删除选中的记录
    // 个人信息--农场信息
    public static final String URL_ALL_FARM_LIST = SERVER + "app-myfarm-op-all.html"; // 所有的农场
    public static final String URL_FARM_DETAILS = SERVER + "app-myfarm-op-farmdetails.html"; // 农场详情
    public static final String URL_FARM_COMMENT = SERVER + "app-myfarm-op-ownercomment.html"; // 农场最新评论
    // 个人中心-发布的任务
    public static final String URL_OWNER_FINISH_TASK = SERVER + "app-task-op-finish.html"; // 农机手--完成项目
    public static final String URL_OWNER_ACCEPT_TASK = SERVER + "app-task-op-accept.html"; // 农机手--接下项目
    public static final String URL_MYRELEASE_TASK = SERVER + "app-task-op-mytask.html"; // 农场主--我发布的任务
    public static final String URL_OWNER_JOIN_TASK = SERVER + "app-task-op-jointask.html"; // 农机手--我接下的任务
    public static final String URL_TASK_STAGES = SERVER + "app-task-op-stages.html"; // 设置分期金额
    public static final String URL_TASK_PAY_STAGES = SERVER + "app-task-op-paystagesrecord.html"; // 获取进度款记录
    public static final String URL_TASK_BALANCE_PAY_STAGES = SERVER + "app-task-op-stagespay.html"; // 余额支付进度款
    // 个人中心-密码管理
    public static final String URL_MODIFY_PASSWORD = SERVER + "app-personalinfo-op-password.html";
    // 个人中心-奖罚制度
    public static final String URL_REGIME = SERVER + "app-personalinfo-op-regime.html";
    // 注册
    public static final String URL_USER_REGISTER = SERVER + "app-register-op-register.html";
    // 发布任务
    public static final String URL_RELEASE_TASK = SERVER + "app-task-op-release.html";
    // 搜索界面
    public static final String URL_TASK_SEARCH = SERVER + "app-task-op-search.html";
    // 投标支付
    public static final String URL_ACCOUNT_BALANCE = SERVER + "app-task-op-balance.html"; // 获取账户余额
    public static final String URL_BALANCE_TBPAY = SERVER + "app-task-op-tbpay.html";// 余额支付项目款
    // 行业快讯
    public static final String URL_TRADEALERTS_LIST = SERVER + "app-tradealerts-op-list.html";
    // 交易记录
    public static final String URL_TRADING_LIST = SERVER + "app-personalinfo-op-trading.html";
    // 帖子列表
    public static final String URL_FORUM_LIST = SERVER + "app-forum-op-list.html";
    // 抢单中心，任务中心
    public static final String URL_TASK_LIST = SERVER + "app-task-op-all.html";
    // 主页
    public static final String URL_HOME_INFO = SERVER + "app-homeinfo-op-home.html";

    //    删除农机手机车信息
    public static final String URL_FARMER_LOMOTIVE_INFO = SERVER + "app-locomotive-op-delowner.html";
    //支付项目保证金
    public static final String URL_PAY_PROJECT_BOND= SERVER + "app-task-op-paytask.html";
    //提现
    public static final String URL_WITH_DRAWAL= SERVER + "app-personalinfo-op-extract.html";

}

