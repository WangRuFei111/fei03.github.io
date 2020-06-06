package com.example.read.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.read.R;
import com.example.read.model.bean.FruitImage;
import com.example.read.utils.OnItemClickListener;

import java.util.List;

public class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.ViewHolder> {
    private List<FruitImage> mFruitListImage;
    private OnItemClickListener onItemClickListener;

    //绑定控件
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView fruitImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fruitImage = itemView.findViewById(R.id.circle_img_item);
        }
    }

    public CircleAdapter(List<FruitImage> fruitImages){
        mFruitListImage = fruitImages;
    }



    @Override
    public CircleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_circle_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CircleAdapter.ViewHolder holder, final int position) {
        FruitImage fruitImages = mFruitListImage.get(position);
        holder.fruitImage.setImageResource(fruitImages.getImageId());
        if (onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFruitListImage.size();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListeners){
        this.onItemClickListener = onItemClickListeners;
    }
}
