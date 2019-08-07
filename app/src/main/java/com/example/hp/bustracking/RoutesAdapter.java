package com.example.hp.bustracking;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RoutesAdapter  extends RecyclerView.Adapter<RoutesAdapter.RoutesHolder> {
    ArrayList<Stop> dataset;
    AdapterView.OnItemClickListener onItemClickListener;

    public RoutesAdapter(RoutesActivity routesActivity, ArrayList<Stop> dataset, AdapterView.OnItemClickListener onItemClickListener) {
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public RoutesAdapter.RoutesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.routes_item_layout, parent, false);
        return new RoutesHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final RoutesAdapter.RoutesHolder holder, final int position) {
        int routes_status = dataset.get(position).driverStatus;

        holder.tv_routename.setText(dataset.get(position).Routename);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(null, holder.itemView, position, 0);
            }
        });
if(dataset.get(position).busstatus == 1){
    holder.ivGreenBus.setVisibility(View.VISIBLE);
    holder.ivBlackBus.setVisibility(View.GONE);
}

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class RoutesHolder extends RecyclerView.ViewHolder {
        TextView tv_routename;
        ImageView ivBlackBus;
        ImageView ivGreenBus;

        public RoutesHolder(View itemView) {

            super(itemView);
            tv_routename = itemView.findViewById(R.id.tv_routename);
            ivBlackBus = itemView.findViewById(R.id.iv_black_bus);
            ivGreenBus = itemView.findViewById(R.id.iv_green_bus);


        }
    }

}

