package com.app.exptracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.exptracker.R;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.CircularTextView;
import com.app.exptracker.utility.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CatwiseAverageAdapter extends RecyclerView.Adapter<CatwiseAverageAdapter.MyViewHolder> implements Filterable {

    private List<ExcelDataModel> expenseList;
    private List<ExcelDataModel> expenseListFilter;
    Context mContext;
    GetMonthFromAdapter getMonthFromAdapter;
    double st_expense = 0;

    public interface GetMonthFromAdapter {
        void returnedMonth(String month, String sort);

        void returnedID(String id);
    }

    public void updateReceiptsList(List<ExcelDataModel> expenseListFilter) {
        this.expenseListFilter = expenseListFilter;
        this.notifyDataSetChanged();
    }

    /**
     * View holder class
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category, tv_expense, tv_percentage, tv_date;
        CircleImageView iv_icon;
        LinearLayout ll_background;
        RelativeLayout rl_imgview, child_container;
        ProgressBar progress;
        ImageView iv_payment;
        CircularTextView cv_text;

        public MyViewHolder(View view) {
            super(view);
            tv_category = view.findViewById(R.id.tv_category);
            iv_icon = view.findViewById(R.id.iv_icon);
            ll_background = view.findViewById(R.id.ll_background);
            rl_imgview = view.findViewById(R.id.rl_imgview);
            tv_expense = view.findViewById(R.id.tv_expense);
            progress = view.findViewById(R.id.progress);
            tv_percentage = view.findViewById(R.id.tv_percentage);
            child_container = view.findViewById(R.id.child_container);
            tv_date = view.findViewById(R.id.tv_date);
            iv_payment = view.findViewById(R.id.iv_payment);
            iv_payment.setVisibility(View.VISIBLE);
            cv_text = view.findViewById(R.id.cv_text);
            cv_text.setSolidColor("#D3D3D3");
        }
    }

    public CatwiseAverageAdapter(Context mContext, List<ExcelDataModel> expenseList, GetMonthFromAdapter getMonthFromAdapter, double st_expense) {
        this.mContext = mContext;
        this.getMonthFromAdapter = getMonthFromAdapter;
        this.expenseList = expenseList;
        this.st_expense = st_expense;
        this.expenseListFilter = expenseList;
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
        final ExcelDataModel c = expenseListFilter.get(position);
        holder.tv_category.setText(c.getMemo());
        //holder.tv_expense.setText("\u20B9 " + new DecimalFormat("#,##,###.00").format(c.getAmount_()));
        holder.tv_date.setVisibility(View.VISIBLE);
        holder.tv_date.setText(Utils.getFormattedDate(c.getDate()));

        if (c.getAmount_() <= 0) {
            holder.tv_expense.setText("\u20B9 " + "0.00");
        } else {
            holder.tv_expense.setText("\u20B9 " + new DecimalFormat("#,##,###.00").format(c.getAmount_()));
        }

        double percentage = 0;

        if (c.getAmount_() != 0) {
            percentage = (c.getAmount_() / st_expense) * 100;
            holder.progress.setProgress((int) percentage);
        } else {
            holder.progress.setProgress(0);
        }

        holder.tv_percentage.setText(new DecimalFormat("0.0").format(percentage) + "%");

        holder.child_container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Utils.ShowToast(mContext, c.getId());
                getMonthFromAdapter.returnedID(c.getId());
            }
        });


        if (c.getPayment_mode().equalsIgnoreCase("Cash")) {
            holder.iv_payment.setImageResource(R.drawable.ic_rupee_icon);
        } else if (c.getPayment_mode().equalsIgnoreCase("Credit Card")) {
            holder.iv_payment.setImageResource(R.drawable.ic_visa_icon);
        } else if (c.getPayment_mode().equalsIgnoreCase("Cheque/Debit Card")) {
            holder.iv_payment.setImageResource(R.drawable.ic_cheque_icon);
        }

        //holder.tv_expense.setText("\u20B9 " + String.valueOf(c.getAmount_()));
        /*if (selectedPosition == position) {
            if (selectedPosition == 0) {
                id = "";
                selectedPosition = position;
                getMonthFromAdapter.returnPosition(position);
                getMonthFromAdapter.returnedCategoryName(c.getCat_name());
                getMonthFromAdapter.returnedMonth(c.getCat_id(), c.getSortOrder());
            }
            holder.rl_imgview.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circularview));
        } else {
            holder.rl_imgview.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circularview_trans));
        }*/
        try {
            if (c.getIcon_name() != null && !c.getIcon_name().isEmpty()) {
                holder.cv_text.setVisibility(View.GONE);
                int imageid = mContext.getResources().getIdentifier(c.getIcon_name(), "drawable", mContext.getPackageName());
                holder.iv_icon.setImageResource(imageid);
            } else {
                holder.cv_text.setVisibility(View.VISIBLE);
                holder.iv_icon.setVisibility(View.GONE);
                holder.cv_text.setText(c.getCategory().substring(0, 2).toUpperCase());
            }
        } catch (Exception e) {
            //holder.iv_icon.setImageResource(R.drawable.ic_gray_no_img);
        }
    }

    @Override
    public int getItemCount() {
        return expenseListFilter.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_average, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    expenseListFilter = expenseList;
                } else {
                    List<ExcelDataModel> filteredList = new ArrayList<>();
                    for (ExcelDataModel row : expenseList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getPayment_mode().equalsIgnoreCase(charString)) {
                            filteredList.add(row);
                        }
                    }

                    expenseListFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = expenseListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                expenseListFilter = (List<ExcelDataModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
