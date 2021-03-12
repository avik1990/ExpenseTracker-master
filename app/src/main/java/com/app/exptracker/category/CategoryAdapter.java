package com.app.exptracker.category;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.CategoryModel;
import com.app.exptracker.utility.CircularTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategoryModel> countryList;
    Context mContext;
    GetMonthFromAdapter getMonthFromAdapter;
    String id = "";
    int selectedPosition = -1;

    public interface GetMonthFromAdapter {
        public void returnedMonth(String month, String sort);

        public void returnedCategoryName(String month);

        public void returnPosition(int pos);
    }

    /**
     * View holder class
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category;
        CircleImageView iv_cat_icon;
        LinearLayout ll_background;
        RelativeLayout rl_imgview;
        CircularTextView cv_text;

        public MyViewHolder(View view) {
            super(view);
            tv_category = view.findViewById(R.id.tv_category);
            iv_cat_icon = view.findViewById(R.id.iv_cat_icon);
            ll_background = view.findViewById(R.id.ll_background);
            rl_imgview = view.findViewById(R.id.rl_imgview);
            cv_text = view.findViewById(R.id.cv_text);
        }
    }

    public CategoryAdapter(Context mContext, List<CategoryModel> countryList, GetMonthFromAdapter getMonthFromAdapter, String id) {
        this.mContext = mContext;
        this.getMonthFromAdapter = getMonthFromAdapter;
        this.countryList = countryList;
        this.id = id;

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
        final CategoryModel c = countryList.get(position);
        holder.tv_category.setText(c.getCat_name());

        if (selectedPosition == position) {
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
        }

        try {
            if (c.getCat_iconname() != null && !c.getCat_iconname().isEmpty()) {
                holder.cv_text.setVisibility(View.GONE);
                holder.iv_cat_icon.setVisibility(View.VISIBLE);
                int imageid = mContext.getResources().getIdentifier(c.getCat_iconname(), "drawable", mContext.getPackageName());
                holder.iv_cat_icon.setImageResource(imageid);
            } else {
                holder.cv_text.setVisibility(View.VISIBLE);
                holder.iv_cat_icon.setVisibility(View.GONE);
                holder.cv_text.setText(c.getCat_name().substring(0, 2).toUpperCase());
            }
        } catch (Exception e) {
            holder.cv_text.setVisibility(View.VISIBLE);
            holder.iv_cat_icon.setVisibility(View.GONE);
            holder.cv_text.setText(c.getCat_name().substring(0, 2).toUpperCase());
        }

        if (id.equalsIgnoreCase(c.getCat_id())) {
            getMonthFromAdapter.returnPosition(position);
            selectedPosition = position;
            getMonthFromAdapter.returnedCategoryName(c.getCat_name());
            getMonthFromAdapter.returnedMonth(c.getCat_id(), c.getSortOrder());
            holder.rl_imgview.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circularview));
        }

        holder.iv_cat_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                id = "";
                selectedPosition = position;
                notifyDataSetChanged();
                getMonthFromAdapter.returnedCategoryName(c.getCat_name());
                getMonthFromAdapter.returnedMonth(c.getCat_id(), c.getSortOrder());
                getMonthFromAdapter.returnPosition(position);
            }
        });

        holder.cv_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                id = "";
                selectedPosition = position;
                notifyDataSetChanged();
                getMonthFromAdapter.returnedCategoryName(c.getCat_name());
                getMonthFromAdapter.returnedMonth(c.getCat_id(), c.getSortOrder());
                getMonthFromAdapter.returnPosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_avg_categories, parent, false);
        return new MyViewHolder(v);
    }
}
