package com.example.hp.bustracking;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class timedetailAdapter extends RecyclerView.Adapter<timedetailAdapter.timedetailHolder> {

   ArrayList<time> dataset;
   AdapterView.OnItemClickListener onItemClickListener;

    public timedetailAdapter(ArrayList<time> dataset, AdapterView.OnItemClickListener onItemClickListener) {
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public timedetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.time_info_layout,parent,false);

        return new timedetailHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull timedetailHolder holder, int position) {
        holder.tvtitle.setText(dataset.get(position).timetitle);
        holder.tvname.setText(dataset.get(position).timename);
        holder.tvtimeofdepart.setText(dataset.get(position).timeofdepart);
        holder.tvtimeofarrival.setText(dataset.get(position).timeofarrival);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class timedetailHolder extends RecyclerView.ViewHolder {
        TextView tvtitle;
        TextView tvname;
        TextView tvtimeofdepart;
        TextView tvtimeofarrival;

        public timedetailHolder(View itemView) {
            super(itemView);
            tvtitle =itemView.findViewById(R.id.tv_title);
            tvname =itemView.findViewById(R.id.tv_timename);
            tvtimeofdepart =itemView.findViewById(R.id.tv_timeofdepart);
            tvtimeofarrival=itemView.findViewById(R.id.tv_timeofarrival);


        }
    }

}
