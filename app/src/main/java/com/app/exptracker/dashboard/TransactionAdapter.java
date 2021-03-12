package com.app.exptracker.dashboard;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.CircularTextView;
import com.app.exptracker.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Avik on 2/4/2019.
 */

public class TransactionAdapter extends BaseExpandableListAdapter implements Filterable {

    private Context context;
    List<ExcelDataModel> expenseListFilter;
    List<ExcelDataModel> expenseList;
    ChildClicked childClicked;

    public TransactionAdapter(Context context, List<ExcelDataModel> expenseList, ChildClicked childClicked) {
        this.context = context;
        this.expenseListFilter = expenseList;
        this.childClicked = childClicked;
        this.expenseList = expenseList;
    }

    public void updateReceiptsList(List<ExcelDataModel> expenseListFilter) {
        this.expenseListFilter = expenseListFilter;
        this.notifyDataSetChanged();
    }

    public interface ChildClicked {
        void childClicked(int id);
    }


    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expenseListFilter.get(listPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(final int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cview = layoutInflater.inflate(R.layout.list_child_cat, null);

        RelativeLayout child_container = cview.findViewById(R.id.child_container);
        TextView tv_category = cview.findViewById(R.id.tv_category);
        TextView tv_expense = cview.findViewById(R.id.tv_expense);
        TextView tv_time = cview.findViewById(R.id.tv_time);

        ImageView iv_icon = cview.findViewById(R.id.iv_icon);
        ImageView iv_payment = cview.findViewById(R.id.iv_payment);
        CircularTextView cv_text = cview.findViewById(R.id.cv_text);
        CircleImageView iv_user = cview.findViewById(R.id.iv_user);
        ImageView iv_dot = cview.findViewById(R.id.iv_dot);
        cv_text.setSolidColor("#D3D3D3");
        CircularTextView tv_payment_mode = cview.findViewById(R.id.tv_payment_mode);
        tv_payment_mode.setSolidColor("#FF0000");

        /*try {
            if (!expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getUser_relation().isEmpty() || expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getUser_relation() != null) {
               *//* byte[] decodedString = Base64.decode(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getUser_relation(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                iv_user.setImageBitmap(decodedByte);*//*
                Glide.with(context)
                        .load(Base64.decode(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getUser_relation(), Base64.DEFAULT))
                        .into(iv_user);
            }
        } catch (Exception e) {
        }*/

        try {
            if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getFileName() != null &&
                    !expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getFileName().isEmpty()) {
                iv_dot.setVisibility(View.VISIBLE);
            } else {
                iv_dot.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            iv_dot.setVisibility(View.GONE);
        }

        if (Utils.getIsDateShow(context)) {
            try {
                if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getTime() != null &&
                        !expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getTime().isEmpty()) {
                    tv_time.setVisibility(View.VISIBLE);
                    tv_time.setText(Utils.getFormattedTime(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getTime()));
                } else {
                    tv_time.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }
        } else {
            tv_time.setVisibility(View.GONE);
        }


        try {
            if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getIcon_name() != null &&
                    !expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getIcon_name().isEmpty()) {
                cv_text.setVisibility(View.GONE);
                iv_icon.setVisibility(View.VISIBLE);
                int imageid = context.getResources().getIdentifier(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getIcon_name(), "drawable", context.getPackageName());
                iv_icon.setImageResource(imageid);
            } else {
                cv_text.setVisibility(View.VISIBLE);
                iv_icon.setVisibility(View.GONE);
                cv_text.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory().substring(0, 2).toUpperCase());
            }
        } catch (Exception e) {
            // cv_text.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory().substring(0, 2).toUpperCase());
        }

        try {
            if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getIncome_Expenses().equalsIgnoreCase("Expenses")) {
                tv_expense.setTextColor(Color.parseColor("#ff0000"));
                tv_expense.setText("-" + expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount_());
            } else {
                tv_expense.setTextColor(Color.parseColor("#008640"));
                tv_expense.setText(""+expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount_());
            }
        } catch (Exception e) {

        }

        try {
            if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getPayment_mode().equalsIgnoreCase("Cash")) {
                //tv_payment_mode.setText("ca");
                iv_payment.setImageResource(R.drawable.ic_rupee_icon);
            } else if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getPayment_mode().equalsIgnoreCase("Credit Card")) {
                //tv_payment_mode.setText("cc");
                iv_payment.setImageResource(R.drawable.ic_visa_icon);
            } else if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getPayment_mode().equalsIgnoreCase("Cheque/Debit Card")) {
                // tv_payment_mode.setText("ch");
                iv_payment.setImageResource(R.drawable.ic_cheque_icon);
            }

        } catch (Exception e) {

        }


        child_container.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                if (!expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getId().isEmpty()) {
                    childClicked.childClicked(Integer.parseInt(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getId()));
                }
            }
        });

        try {
            if (!expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getMemo().isEmpty()) {
                tv_category.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getMemo());
            } else {
                tv_category.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory());
            }

            if (expandedListPosition == expenseListFilter.get(listPosition).getGroupFocAll().size() - 1) {
                child_container.setBackgroundResource(R.drawable.card_bottom_round);
            }
        } catch (Exception e) {

        }

        return cview;
    }


    @Override
    public int getChildrenCount(int listPosition) {
        return expenseListFilter.get(listPosition).getGroupFocAll().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return expenseListFilter.get(listPosition).getGroupFocAll();
    }

    @Override
    public int getGroupCount() {
        return expenseListFilter.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_parent_sos, null);
        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(listPosition);
        TextView listTitleTextView = view.findViewById(R.id.listTitle);
        try {
            TextView tv_expenditures = view.findViewById(R.id.tv_expenditures);
            listTitleTextView.setText(Utils.getFormattedDate(expenseListFilter.get(listPosition).getDate()));
            tv_expenditures.setText("Expenses: " + expenseListFilter.get(listPosition).getExp_amt_() + " Income: " + expenseListFilter.get(listPosition).getInc_amt_());
        } catch (Exception e) {

        }
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                Log.d("charString", charString);
                if (charString.isEmpty()) {
                    expenseListFilter = expenseList;
                } else {
                    List<ExcelDataModel> filteredList = new ArrayList<>();
                    for (ExcelDataModel row : expenseList) {
                        List<ExcelDataModel> faqQuestionList = row.getGroupFocAll();

                        for (ExcelDataModel country : faqQuestionList) {
                            if (country.getPayment_mode().contains(charSequence)) {
                                filteredList.add(country);
                            }
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
