package com.rifeng.agriculturalstation.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.rifeng.agriculturalstation.R;

/**
 * 加载进度条
 *
 * Created by chw on 2016/10/15.
 */
public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context, String message) {
        this(context, R.style.CustomProgressDialog, message);
    }

    public CustomProgressDialog(Context context, int themeResId, String message) {
        super(context, themeResId);
        this.setContentView(R.layout.progressdialog);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        TextView tvMsg = (TextView) this.findViewById(R.id.loading_tv);
        if(tvMsg != null){
            tvMsg.setText(message);
        }
        this.setCanceledOnTouchOutside(false);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        if(!hasFocus){
//            dismiss();
//        }
//    }
}






















































