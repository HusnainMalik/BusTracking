package com.example.hp.bustracking;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Category_Adapter extends RecyclerView.Adapter<Category_Adapter.CategoryHolder>{
    ArrayList<Category>dataset;
    AdapterView.OnItemClickListener onItemClickListener;

    public Category_Adapter(ArrayList<Category> dataset, AdapterView.OnItemClickListener onItemClickListener) {
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.activity_categorytem_layout, parent,false);
        return new CategoryHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, final int position) {
        Picasso.with(holder.itemView.getContext())
                .load(dataset.get(position).Catimage)
                .into(holder.imageView);
        holder.tvcatname.setText(dataset.get(position).Catname);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(null, holder.itemView, position, 0);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public  class CategoryHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tvcatname;
        public CategoryHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_cat_image);
            tvcatname =  itemView.findViewById(R.id.tv_cat_name);


        }
    }
}
