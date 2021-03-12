package com.app.exptracker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.CircularTextView;
import com.app.exptracker.utility.Utils;

import java.util.List;

/**
 * Created by Avik on 2/4/2019.
 */

public class SearchTransExpAdapter extends BaseExpandableListAdapter {

    private Context context;
    List<ExcelDataModel> list_shareofshelf;
    ChildClicked childClicked;

    public SearchTransExpAdapter(Context context, List<ExcelDataModel> list_shareofshelf, ChildClicked childClicked) {
        this.context = context;
        this.list_shareofshelf = list_shareofshelf;
        this.childClicked = childClicked;
    }

    public void updateReceiptsList(List<ExcelDataModel> list_shareofshelf) {
        this.list_shareofshelf = list_shareofshelf;
        this.notifyDataSetChanged();
    }

    public interface ChildClicked {
        void childClicked(int id);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.list_shareofshelf.get(listPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(final int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cview = layoutInflater.inflate(R.layout.list_child_sos, null);

        RelativeLayout child_container = cview.findViewById(R.id.child_container);
        TextView tv_category = cview.findViewById(R.id.tv_category);
        TextView tv_expense = cview.findViewById(R.id.tv_expense);
        ImageView iv_icon = cview.findViewById(R.id.iv_icon);
        ImageView iv_payment = cview.findViewById(R.id.iv_payment);
        CircularTextView cv_text = cview.findViewById(R.id.cv_text);
        TextView tv_time = cview.findViewById(R.id.tv_time);
        cv_text.setSolidColor("#D3D3D3");
        CircularTextView tv_payment_mode = cview.findViewById(R.id.tv_payment_mode);
        tv_payment_mode.setSolidColor("#FF0000");

        if (Utils.getIsDateShow(context)) {
            try {
                if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getTime() != null &&
                        !list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getTime().isEmpty()) {
                    tv_time.setVisibility(View.VISIBLE);
                    tv_time.setText(Utils.getFormattedTime(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getTime()));
                } else {
                    tv_time.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }
        } else {
            tv_time.setVisibility(View.GONE);
        }



        /*try {
            if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getTime() != null &&
                    !list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getTime().isEmpty()) {
                tv_time.setVisibility(View.VISIBLE);
                tv_time.setText(Utils.getFormattedTime(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getTime()));
            } else {
                tv_time.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }*/


        try {
            if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getIcon_name() != null && !list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getIcon_name().isEmpty()) {
                cv_text.setVisibility(View.GONE);
                iv_icon.setVisibility(View.VISIBLE);
                int imageid = context.getResources().getIdentifier(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getIcon_name(), "drawable", context.getPackageName());
                iv_icon.setImageResource(imageid);
            } else {
                cv_text.setVisibility(View.VISIBLE);
                iv_icon.setVisibility(View.GONE);
                cv_text.setText(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory().substring(0, 2).toUpperCase());
            }
        } catch (Exception e) {

        }

        try {
            if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getIncome_Expenses().equalsIgnoreCase("Expenses")) {
                tv_expense.setText("-" + list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount_());
                tv_expense.setTextColor(Color.parseColor("#ff0000"));
            } else {
                tv_expense.setTextColor(Color.parseColor("#008640"));
                tv_expense.setText("" + list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getAmount_());
            }
        } catch (Exception e) {
        }

        try {
            if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getPayment_mode().equalsIgnoreCase("Cash")) {
                tv_payment_mode.setText("ca");
                iv_payment.setImageResource(R.drawable.ic_rupee_icon);
            } else if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getPayment_mode().equalsIgnoreCase("Credit Card")) {
                tv_payment_mode.setText("cc");
                iv_payment.setImageResource(R.drawable.ic_visa_icon);
            } else if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getPayment_mode().equalsIgnoreCase("Cheque/Debit Card")) {
                tv_payment_mode.setText("ch");
                iv_payment.setImageResource(R.drawable.ic_cheque_icon);
            }
        } catch (Exception e) {

        }

        child_container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getId().isEmpty()) {
                    childClicked.childClicked(Integer.parseInt(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getId()));
                    //Utils.ShowToast(context, list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getId());

                }
            }
        });

        /*if (list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getId().isEmpty()) {
            iv_icon.setVisibility(View.GONE);
            iv_icon.setImageDrawable(null);

            if (st_income != 0.0) {
                tv_category.setText("Expenses: " + st_expenses);
            } else {
                tv_category.setText("Expenses: " + st_expenses + "    Income:    " + st_income);
            }

            Log.d("st_income", "" + st_income);

            tv_expense.setTextColor(Color.parseColor("#000000"));
            tv_expense.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        } else {*/

        try {
            if (!list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getMemo().isEmpty()) {
                tv_category.setText(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getMemo());
            } else {
                tv_category.setText(list_shareofshelf.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory());
            }
        } catch (Exception e) {

        }
        //}

        try {
            Log.d("listPosition", String.valueOf(expandedListPosition + " " + (list_shareofshelf.get(listPosition).getGroupFocAll().size() - 1)));

            if (expandedListPosition == list_shareofshelf.get(listPosition).getGroupFocAll().size() - 1) {
                child_container.setBackgroundResource(R.drawable.card_bottom_round);
            }
        } catch (Exception e) {

        }

        return cview;
    }


    @Override
    public int getChildrenCount(int listPosition) {
        return list_shareofshelf.get(listPosition).getGroupFocAll().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return list_shareofshelf.get(listPosition).getGroupFocAll();
    }

    @Override
    public int getGroupCount() {
        return list_shareofshelf.size();
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
        LinearLayout parent_container = view.findViewById(R.id.parent_container);
        ImageView actionIcon = view.findViewById(R.id.actionIcon);

        try {
            listTitleTextView.setText(Utils.getFormattedDate(list_shareofshelf.get(listPosition).getDate()));
        } catch (Exception e) {
        }

        TextView tv_expenditures = view.findViewById(R.id.tv_expenditures);

        try {
            tv_expenditures.setText("Expenses: " + list_shareofshelf.get(listPosition).getExp_amt_() + " Income: " + list_shareofshelf.get(listPosition).getInc_amt_());
        } catch (Exception e) {

        }

        /*if (isExpanded) {
            actionIcon.setImageResource(R.drawable.ic_collapse);
        } else {
            actionIcon.setImageResource(R.drawable.ic_expand);
        }
*/
       /* if (isExpanded) {
            view.setPadding(0, 0, 0, 0);
            int[] attrs = new int[]{R.drawable.card_top_round};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            parent_container.setBackgroundResource(backgroundResource);
        } else {
            view.setPadding(0, 0, 0, 10);
            int[] attrs = new int[]{R.drawable.card_full_round};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            parent_container.setBackgroundResource(backgroundResource);
        }*/
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


}
