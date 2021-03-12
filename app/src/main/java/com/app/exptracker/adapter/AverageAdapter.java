package com.app.exptracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.CircularTextView;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AverageAdapter extends RecyclerView.Adapter<AverageAdapter.MyViewHolder> {

    private List<ExcelDataModel> expenseList;

    Context mContext;
    GetMonthFromAdapter getMonthFromAdapter;
    double st_expense = 0;


    public interface GetMonthFromAdapter {
        void returnedMonth(String month, String sort);

        void returnedID(String id);
    }

    public void updateAdapterData(List<ExcelDataModel> expenseListFilter) {
        this.expenseList = expenseListFilter;
        this.notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category, tv_expense, tv_percentage;
        CircleImageView iv_icon;
        LinearLayout ll_background;
        RelativeLayout rl_imgview, child_container;
        ProgressBar progress;
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
            cv_text = view.findViewById(R.id.cv_text);
            cv_text.setSolidColor("#D3D3D3");
        }
    }

    public AverageAdapter(Context mContext,
                          List<ExcelDataModel> expenseList,
                          GetMonthFromAdapter getMonthFromAdapter,
                          double st_expense) {
        this.mContext = mContext;
        this.getMonthFromAdapter = getMonthFromAdapter;
        this.expenseList = expenseList;
        this.st_expense = st_expense;
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
        final ExcelDataModel c = expenseList.get(position);
        holder.tv_category.setText(c.getCategory());
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
                getMonthFromAdapter.returnedID(c.getId());

            }
        });

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
        return expenseList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_average, parent, false);
        return new MyViewHolder(v);
    }


}
