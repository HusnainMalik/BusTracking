package com.example.hp.bustracking;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.stopsHolder> {
    ArrayList<StopInfo> dataset;
    AdapterView.OnItemClickListener onItemClickListener;

    public StopsAdapter(ArrayList<StopInfo> dataset, AdapterView.OnItemClickListener onItemClickListener) {

        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;

    }


    @NonNull
    @Override
    public StopsAdapter.stopsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.stop_item_layout, parent, false);
        return new StopsAdapter.stopsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final StopsAdapter.stopsHolder holder, final int position) {
        holder.tvStopInfoName.setText(dataset.get(position).stopInfoName);
        holder.tvStopTime.setText(dataset.get(position).stopTime);
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
    public class stopsHolder extends RecyclerView.ViewHolder{
        TextView tvStopInfoName;
        TextView tvStopTime;
        public stopsHolder(View itemView) {
            super(itemView);
            tvStopInfoName = itemView.findViewById(R.id.tv_stop_info_name);
            tvStopTime = itemView.findViewById(R.id.tv_stop_time);

        }
    }
}
