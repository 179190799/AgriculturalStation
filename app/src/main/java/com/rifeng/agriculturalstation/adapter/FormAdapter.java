package com.rifeng.agriculturalstation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.BidBean;
import com.rifeng.agriculturalstation.bean.FormBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/20.
 */

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<BidBean> list = new ArrayList<>();

    public interface SaveEditListener{

        void SaveEditDay(int position, String string);
        void SaveEditMoney(int position, String string);
    }

    public FormAdapter(Context context, List<BidBean> list) {
        this.mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_select, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName() + "/" + list.get(position).getUnit());
        holder.day.addTextChangedListener(new TextSwitcher(holder));
        holder.money.addTextChangedListener(new TextSwitcher2(holder));
        holder.day.setTag(position);
        holder.money.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        EditText money;
        EditText day;

        public ViewHolder(View itemView) {
            super(itemView);
            day = (EditText) itemView.findViewById(R.id.offerday_1);
            money = (EditText) itemView.findViewById(R.id.offermoney_1);
            name = (TextView) itemView.findViewById(R.id.offermoney_1_tv);
        }
    }

    private class TextSwitcher implements TextWatcher {

        private FormAdapter.ViewHolder mHolder;

        public TextSwitcher(FormAdapter.ViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            FormAdapter.SaveEditListener listener= (FormAdapter.SaveEditListener) mContext;
            if(s!=null){
                listener.SaveEditDay(Integer.parseInt(mHolder.day.getTag().toString()),s.toString());
//                listener.SaveEditMoney(Integer.parseInt(mHolder.money.getTag().toString()),s.toString());
            }
        }
    }

    private class TextSwitcher2 implements TextWatcher {
        private ViewHolder mHolder;
        public TextSwitcher2(ViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            FormAdapter.SaveEditListener listener= (FormAdapter.SaveEditListener) mContext;
            if(s!=null){
//                listener.SaveEditDay(Integer.parseInt(mHolder.day.getTag().toString()),s.toString());
                listener.SaveEditMoney(Integer.parseInt(mHolder.money.getTag().toString()),s.toString());
            }
        }
    }
}
