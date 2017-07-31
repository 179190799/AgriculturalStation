package com.rifeng.agriculturalstation.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.rifeng.agriculturalstation.R;

/**
 * 对话框管理类
 *   包括进度条、PopupWindow、自定义View、自定义样式的对话框
 *
 * Created by chw on 2016/11/3.
 */
public class DialogManager {

    private Context mContext;
    private AlertDialog.Builder builder;

    public DialogManager(Context context){
        this.mContext = context;
        builder = new AlertDialog.Builder(mContext);
    }

    /**
     * 设置对话框的标题+图标+按钮
     *
     * @param title
     */
    private void setButton(String title){
        builder.setTitle(title).setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("好", new PositiveListener())
                .setNeutralButton("中", new NeutralListener())
                .setNegativeButton("差", new NegativeListener());
        //.setCancelable(false); // 设置点击空白处，不能消除该对话框
    }

    /**
     * 监听器
     */
    private class PositiveListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 设置对话框强制退出
//            dialog.dismiss();

//            showToast();
        }
    }

    /**
     * 监听器
     */
    private class NeutralListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 设置对话框强制退出
//            dialog.dismiss();

//            showToast();
        }
    }

    /**
     * 监听器
     */
    private class NegativeListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 设置对话框强制退出
//            dialog.dismiss();

//            showToast();
        }
    }
/**---------------------------------------------------------------无情的分隔线------------------------------------------------------------------------*/
    /**
     * 简易对话框
     *
     * @param title
     * @param msg
     */
    public void simpleDialog(String title, String msg){
        setButton(title);
        builder.setMessage(msg).create().show();
    }

    /**
     * 列表对话框
     *
     * @param title
     * @param str
     */
    public void listDialog(String title, final String[] str){
        setButton(title);

        // 设置了列表就不能设置内容了，否则就会出问题
        builder.setItems(str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContext, "你选中了：" + str[which], Toast.LENGTH_SHORT).show();
            }
        }).create().show();
    }

    /**
     * 单选对话框
     *
     * @param title
     * @param str
     */
    public void singleChoiceDialog(String title, final String[] str){
        setButton(title);
        // 默认选择第一项
        builder.setSingleChoiceItems(str, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContext, "你选中了：" + str[which], Toast.LENGTH_SHORT).show();
            }
        }).create().show();
    }

    /**
     * 多选对话框
     *
     * @param title
     * @param str
     */
    public void MultiChoiceDialog(String title, final String[] str){
        setButton(title);
        // 默认选中几项
        builder.setMultiChoiceItems(str, new boolean[]{true, false, true}, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                Toast.makeText(mContext, "你选择的id为：" + which + "选中了：" + str[which], Toast.LENGTH_SHORT).show();
            }
        }).create().show();
    }

    /**
     * 适配器对话框，可以用各种适配器，比如SimpleAdapter
     *
     * @param title
     * @param str
     */
    public void adapterDialog(String title, final String[] str){
        setButton(title);
        builder.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_multiple_choice, str),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "你选中了：" + str[which], Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
    }

    /**
     * 自定义视图对话框
     *
     * @param title
     */
    public void viewDialog(String title){
        // LayoutInflater是用来找layout文件夹下xml布局文件，并且实例化
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(0, null);
        //
    }
}





















































