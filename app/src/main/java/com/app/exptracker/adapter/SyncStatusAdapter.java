package com.app.exptracker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.Sync;
import com.app.exptracker.utility.Utils;

import java.util.List;

public class SyncStatusAdapter extends RecyclerView.Adapter<SyncStatusAdapter.MyViewHolder> {

    private List<Sync> sync;

    Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_syncDate, tv_fileId, tv_fileName, tv_syncStatus, tv_accountName;

        public MyViewHolder(View view) {
            super(view);
            tv_syncDate = view.findViewById(R.id.tv_syncDate);
            tv_fileId = view.findViewById(R.id.tv_fileId);
            tv_fileName = view.findViewById(R.id.tv_fileName);
            tv_syncStatus = view.findViewById(R.id.tv_syncStatus);
            tv_accountName = view.findViewById(R.id.tv_accountName);
        }
    }

    public SyncStatusAdapter(Context mContext, List<Sync> expenseList) {
        this.mContext = mContext;
        this.sync = expenseList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Sync c = sync.get(position);
        holder.tv_syncDate.setText(Utils.getFormattedDateTime1(c.getSyncDate()));

        if (!c.getFileId().isEmpty()) {
            holder.tv_fileId.setVisibility(View.VISIBLE);
            holder.tv_fileId.setText("File Id: " + c.getFileId());
        } else {
            holder.tv_fileId.setVisibility(View.GONE);
        }

        try {
            if (!c.getAccountName().isEmpty()) {
                holder.tv_accountName.setVisibility(View.VISIBLE);
                holder.tv_accountName.setText("Account: " + c.getAccountName());
            } else {
                holder.tv_accountName.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.tv_accountName.setVisibility(View.GONE);
        }

        holder.tv_fileName.setText("File Name: " + c.getFileName());

        if (c.getSyncStatus().equalsIgnoreCase("Success")) {
            holder.tv_syncStatus.setTextColor(Color.parseColor("#008640"));
        } else {
            holder.tv_syncStatus.setTextColor(Color.parseColor("#ff0000"));
        }
        holder.tv_syncStatus.setText(c.getSyncStatus());

    }

    @Override
    public int getItemCount() {
        return sync.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sync, parent, false);
        return new MyViewHolder(v);
    }


}
