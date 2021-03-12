package com.app.exptracker.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.ExcelDataModel;

import java.util.ArrayList;
import java.util.List;

public class SelectTagAdapter extends RecyclerView.Adapter<SelectTagAdapter.MyViewHolder> {

    private List<ExcelDataModel> countryList;
    Context mContext;
    GetTagFromAdpater getMonthFromAdapter;
    String id = "";
    int selectedPosition = -1;
    List<String> listIds = new ArrayList<>();

    public interface GetTagFromAdpater {
        public void returnTags(String tagId, String tags);

        public void returnTags(List<ExcelDataModel> countryList);

        public void returnTagsPosition(int position);
    }

    /**
     * View holder class
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category;


        public MyViewHolder(View view) {
            super(view);
            tv_category = view.findViewById(R.id.tv_category);

        }
    }

    public SelectTagAdapter(Context mContext, List<ExcelDataModel> countryList, GetTagFromAdpater getMonthFromAdapter, String id) {
        this.mContext = mContext;
        this.getMonthFromAdapter = getMonthFromAdapter;
        this.countryList = countryList;
        this.id = id;
        String a[] = id.split(",");

        for (int i = 0; i < a.length; i++) {
            listIds.add(a[i]);
        }

        try {
            for (int j = 0; j < countryList.size(); j++) {
                if (listIds.contains(countryList.get(j).getTagId())) {
                    countryList.get(j).setSelected(true);
                } else {
                    countryList.get(j).setSelected(false);
                }
            }
        } catch (Exception e) {
        }

        if (this.id.isEmpty()) {
            selectedPosition = 0;
        }
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
        final ExcelDataModel c = countryList.get(position);
        holder.tv_category.setText(c.getTagName());

        if (c.isSelected()) {
            holder.tv_category.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corner));
        } else {
            holder.tv_category.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corner_unselected));
        }

        holder.tv_category.setOnClickListener(v -> {
            c.setSelected(!c.isSelected());
            holder.tv_category.setBackground(c.isSelected() ? ContextCompat.getDrawable(mContext, R.drawable.rounded_corner) : ContextCompat.getDrawable(mContext, R.drawable.rounded_corner_unselected));
            getMonthFromAdapter.returnTags(countryList);
            id = "";
            selectedPosition = position;
            notifyDataSetChanged();
            getMonthFromAdapter.returnTags(c.getTagId(), c.getTagName());
            getMonthFromAdapter.returnTagsPosition(position);
        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tags_select, parent, false);
        return new MyViewHolder(v);
    }
}
