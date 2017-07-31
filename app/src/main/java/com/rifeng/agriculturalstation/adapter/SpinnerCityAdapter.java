package com.rifeng.agriculturalstation.adapter;

import android.content.Context;

import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.utils.CommonAdapter;
import com.rifeng.agriculturalstation.utils.ViewHolder;

import java.util.List;

/**
 * Created by chw on 2016/11/16.
 */
public class SpinnerCityAdapter extends CommonAdapter<String> {

    public SpinnerCityAdapter(Context context, List<String> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, String item) {
        helper.setText(R.id.spinner_item_text, item);
    }
}
