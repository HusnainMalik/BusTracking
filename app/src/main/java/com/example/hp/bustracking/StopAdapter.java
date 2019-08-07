package com.example.hp.bustracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopHolder> {
    ArrayList<Stop> dataset;
    Context context;
    AdapterView.OnItemClickListener onItemClickListener;

    public StopAdapter(Context context,ArrayList<Stop> dataset, AdapterView.OnItemClickListener onItemClickListener) {
        this.context=context;
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public StopAdapter.StopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.stop_design_layout, parent, false);
        return new StopHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final StopAdapter.StopHolder holder, final int position) {

        holder.tv_routename.setText(dataset.get(position).Routename);
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

    public class StopHolder extends RecyclerView.ViewHolder {
        TextView tv_routename;
        ImageView ivBlackBus;
       ImageView ivGreenBus;

        public StopHolder(View itemView) {

            super(itemView);
            tv_routename = itemView.findViewById(R.id.tv_routename);
            ivBlackBus = itemView.findViewById(R.id.iv_black_bus);
            ivGreenBus = itemView.findViewById(R.id.iv_green_bus);


        }
    }
}
