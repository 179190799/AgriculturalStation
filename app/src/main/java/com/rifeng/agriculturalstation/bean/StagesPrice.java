package com.rifeng.agriculturalstation.bean;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by chw on 2017/3/14.
 */
public class StagesPrice implements TextWatcher {

    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
