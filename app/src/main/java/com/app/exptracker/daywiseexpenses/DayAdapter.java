package com.app.exptracker.daywiseexpenses;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.model.ExcelDataModelDateCompAsc;
import com.app.exptracker.utility.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.MyViewHolder> {

    private List<String> monthList;
    private List<String> expMonthList = new ArrayList<>();
    List<ExcelDataModel> expesneDetals;
    List<ExcelDataModel> tempData;

    Context mContext;
    GetDateFromAdapter getMonthFromAdapter;
    String id = "";

    public interface GetDateFromAdapter {
        public void returneDate(String date);
    }

    /**
     * View holder class
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDates, tvExpenses, tvIncome, tvHiddenField;
        ImageView ivDot;
        FrameLayout fmLayout;

        public MyViewHolder(View view) {
            super(view);
            tvDates = view.findViewById(R.id.tvDates);
            tvExpenses = view.findViewById(R.id.tvExpenses);
            tvIncome = view.findViewById(R.id.tvIncome);
            ivDot = view.findViewById(R.id.ivDot);
            fmLayout = view.findViewById(R.id.fmLayout);
            tvHiddenField = view.findViewById(R.id.tvHiddenField);
        }
    }


    public DayAdapter(Context mContext, List<String> monthList, GetDateFromAdapter getMonthFromAdapter, List<ExcelDataModel> expesneDetals) {
        this.mContext = mContext;
        this.getMonthFromAdapter = getMonthFromAdapter;
        this.monthList = monthList;
        this.expesneDetals = expesneDetals;
        if (expesneDetals.size() > 0) {
            for (int i = 0; i < expesneDetals.size(); i++) {
                expMonthList.add(expesneDetals.get(i).getDate());
            }
            for (int i = 0; i < monthList.size(); i++) {
                if (expMonthList.contains(monthList.get(i))) {
                } else {
                    expesneDetals.add(new ExcelDataModel(monthList.get(i), 0, 0));
                }
            }
            Collections.sort(expesneDetals, new ExcelDataModelDateCompAsc());
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
        holder.tvDates.setText(Utils.getFormattedDateCalender(monthList.get(position)));
        holder.tvHiddenField.setText(monthList.get(position));

        if (expesneDetals.size() > 0) {
            if (expesneDetals.get(position).getExp_amt_() <= 0.0) {
                holder.tvExpenses.setText("\u20B9" + "0.00");
            } else {
                holder.tvExpenses.setText("\u20B9" + new DecimalFormat("#,##,###.00").format(expesneDetals.get(position).getExp_amt_()));
            }

            if (expesneDetals.get(position).getInc_amt_() <= 0.0) {
                holder.tvIncome.setText("\u20B9" + "0.00");
            } else {
                holder.tvIncome.setText("\u20B9" + new DecimalFormat("#,##,###.00").format(expesneDetals.get(position).getInc_amt_()));
            }

            if (expesneDetals.get(position).getExp_amt_() > 0 || expesneDetals.get(position).getInc_amt_() > 0) {
                holder.ivDot.setVisibility(View.VISIBLE);
                holder.fmLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        getMonthFromAdapter.returneDate(holder.tvHiddenField.getText().toString().trim());
                    }
                });
            } else {
                holder.ivDot.setVisibility(View.GONE);
            }
        } else {
            holder.tvExpenses.setText("\u20B9" + "0.00");
            holder.tvIncome.setText("\u20B9" + "0.00");
        }



    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_calender, parent, false);
        return new MyViewHolder(v);
    }
}
