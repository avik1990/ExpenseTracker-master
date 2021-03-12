package com.app.exptracker.compare;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.Utils;

import java.util.List;

public class CompAdapter1 extends RecyclerView.Adapter<CompAdapter1.MyViewHolder> {

    private List<ExcelDataModel> countryList;
    Context mContext;

    public interface GetMonthFromAdapter {
        public void returnedMonth(String month, String sort);

        public void returnedCategoryName(String month);

        public void returnPosition(int pos);
    }

    /**
     * View holder class
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category, tv_expense, tv_time;
        ImageView iv_cat_icon;
        TextView cv_text;
        ImageView iv_payment;

        public MyViewHolder(View view) {
            super(view);
            tv_category = view.findViewById(R.id.tv_category);
            iv_cat_icon = view.findViewById(R.id.iv_icon);
            cv_text = view.findViewById(R.id.cv_text);
            iv_payment = view.findViewById(R.id.iv_payment);
            tv_expense = view.findViewById(R.id.tv_expense);
            tv_time = view.findViewById(R.id.tv_time);
        }
    }

    public CompAdapter1(Context mContext, List<ExcelDataModel> countryList) {
        this.mContext = mContext;
        this.countryList = countryList;

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
        holder.tv_category.setText(c.getMemo());

        try {
            if (c.getIcon_name() != null && !c.getIcon_name().isEmpty()) {
                holder.cv_text.setVisibility(View.GONE);
                holder.iv_cat_icon.setVisibility(View.VISIBLE);
                int imageid = mContext.getResources().getIdentifier(c.getIcon_name(), "drawable", mContext.getPackageName());
                holder.iv_cat_icon.setImageResource(imageid);
            } else {
                holder.cv_text.setVisibility(View.VISIBLE);
                holder.iv_cat_icon.setVisibility(View.GONE);
                holder.cv_text.setText(c.getCategory().substring(0, 2).toUpperCase());
            }
        } catch (Exception e) {
            holder.cv_text.setVisibility(View.VISIBLE);
            holder.iv_cat_icon.setVisibility(View.GONE);
            holder.cv_text.setText(c.getCategory().substring(0, 2).toUpperCase());
        }

        try {
            if (c.getTime() != null && !c.getTime().isEmpty()) {
                holder.tv_time.setVisibility(View.VISIBLE);
                holder.tv_time.setText(Utils.getFormattedDateTime(c.getTime()));
            } else {
                holder.tv_time.setText(c.getDate());
            }
        } catch (Exception e) {
            holder.tv_time.setText(c.getDate());
        }

        try {
            if (c.getPayment_mode().equalsIgnoreCase("Cash")) {
                //tv_payment_mode.setText("ca");
                holder.iv_payment.setImageResource(R.drawable.ic_rupee_icon);
            } else if (c.getPayment_mode().equalsIgnoreCase("Credit Card")) {
                //tv_payment_mode.setText("cc");
                holder.iv_payment.setImageResource(R.drawable.ic_visa_icon);
            } else if (c.getPayment_mode().equalsIgnoreCase("Cheque/Debit Card")) {
                // tv_payment_mode.setText("ch");
                holder.iv_payment.setImageResource(R.drawable.ic_cheque_icon);
            }

        } catch (Exception e) {

        }

        try {
            if (c.getIncome_Expenses().equalsIgnoreCase("Expenses")) {
                holder.tv_expense.setTextColor(Color.parseColor("#ff0000"));
                holder.tv_expense.setText("-" + c.getAmount());
            } else {
                holder.tv_expense.setTextColor(Color.parseColor("#008640"));
                holder.tv_expense.setText(c.getAmount());
            }
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comp_data, parent, false);
        return new MyViewHolder(v);
    }
}
