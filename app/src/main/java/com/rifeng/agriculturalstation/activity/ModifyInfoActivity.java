package com.rifeng.agriculturalstation.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 信息修改
 * <p>
 * Created by chw on 2016/10/21.
 */
public class ModifyInfoActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight; // 保存
    @BindView(R.id.modify_name)
    TextView modifyName;
    @BindView(R.id.modify_content)
    EditText modifyContent;
    @BindView(R.id.modify_delete)
    ImageView modifyDelete;

    private String content;
    private String title;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_modifyinfo;
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        content = bundle.getString("content");

        idTitleLeft.setText(title);
        idTitleLeft.setCompoundDrawablePadding(30);
        idTitleLeft.setTextColor(Color.WHITE);

        idTitleMiddle.setVisibility(View.GONE);
        idTitleRight.setText("保存");
        idTitleRight.setTextColor(Color.WHITE);

        modifyName.setText(title);
        modifyContent.setText(content);
        // 光标置于末尾
        modifyContent.setSelection(content.length());
    }

    @OnClick({R.id.id_title_left, R.id.id_title_right, R.id.modify_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.id_title_right: // 保存
                Intent intent = new Intent();
                intent.putExtra("data", modifyContent.getText().toString().trim());
                this.setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.modify_delete:
                modifyContent.setText("");
                break;
        }
    }
}
