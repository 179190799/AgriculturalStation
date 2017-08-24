package com.rifeng.agriculturalstation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rifeng.agriculturalstation.R;

import java.util.List;

/**
 * Created by Administrator on 2017/8/24.
 */

public class ReleaseTaskAdapter extends RecyclerView.Adapter<ReleaseTaskAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<String> imgs;
    private Context mContext;

    public ReleaseTaskAdapter(Context context, List<String> imgs) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.imgs = imgs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_add_image_in_release_task, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(imgs.get(position))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.show_image);
        }
    }
}
