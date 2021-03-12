package com.app.exptracker.category;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.CircularTextView;
import com.app.exptracker.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class ExpCategoryAdapter extends BaseExpandableListAdapter implements Filterable {

    private Context context;
    List<ExcelDataModel> expenseListFilter;
    List<ExcelDataModel> expenseList;
    ChildClicked childClicked;
    DatabaseHelper db;
    String m_Text = "";

    public ExpCategoryAdapter(Context context, List<ExcelDataModel> expenseList, ChildClicked childClicked) {
        this.context = context;
        this.expenseListFilter = expenseList;
        this.childClicked = childClicked;
        this.expenseList = expenseList;
        db = new DatabaseHelper(context);
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
        View cview = layoutInflater.inflate(R.layout.row_categories, null);

        TextView tv_category;
        ImageView iv_cat_icon, iv_delete;
        RelativeLayout child_container;
        CircularTextView cv_text;
        ImageView iv_edit;
        tv_category = cview.findViewById(R.id.tv_category);
        iv_cat_icon = cview.findViewById(R.id.iv_icon);
        child_container = cview.findViewById(R.id.child_container);
        cv_text = cview.findViewById(R.id.cv_text);
        iv_delete = cview.findViewById(R.id.iv_delete);
        cv_text.setSolidColor("#D3D3D3");
        iv_edit = cview.findViewById(R.id.iv_edit);

        try {
            if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCatIsAdded().equalsIgnoreCase("1")) {
                iv_delete.setVisibility(View.VISIBLE);
                iv_edit.setVisibility(View.VISIBLE);
            } else {
                iv_delete.setVisibility(View.GONE);
                iv_edit.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }

        try {
            if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCatIsAdded().equalsIgnoreCase("1")) {
                iv_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Edit Category");

                        final EditText input = new EditText(context);
                        input.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        input.setSelection(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory().length());
                        builder.setView(input);

                        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_Text = input.getText().toString();
                                if (db.updateCategory(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getId(), m_Text) > 0) {
                                    ((CategoryActivity) context).inflateData();
                                    Utils.ShowToast(context, "Data Updated Successfully");
                                } else {
                                    Utils.ShowToast(context, "Please try again.Data may already exists");
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });
            } else {
            }
        } catch (Exception e) {
        }


        tv_category.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory());

        try {
            if (expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getIcon_name() != null &&
                    !expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getIcon_name().isEmpty()) {
                cv_text.setVisibility(View.GONE);
                iv_cat_icon.setVisibility(View.VISIBLE);
                int imageid = context.getResources().getIdentifier(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getIcon_name(), "drawable", context.getPackageName());
                iv_cat_icon.setImageResource(imageid);
            } else {
                cv_text.setVisibility(View.VISIBLE);
                iv_cat_icon.setVisibility(View.GONE);
                cv_text.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory().substring(0, 2).toUpperCase());
            }
        } catch (Exception e) {
            cv_text.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory().substring(0, 2).toUpperCase());
        }

        child_container.setOnClickListener(v -> {
            if (!expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getId().isEmpty()) {
                childClicked.childClicked(Integer.parseInt(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getId()));
            }
        });

        if (!expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getMemo().isEmpty()) {
            tv_category.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getMemo());
        } else {
            tv_category.setText(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getCategory());
        }

        if (expandedListPosition == expenseListFilter.get(listPosition).getGroupFocAll().size() - 1) {
            child_container.setBackgroundResource(R.drawable.card_bottom_round);
        }

        iv_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (db.CheckCatIdCount(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getId()) <= 0) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete category")
                            .setMessage("Are you sure you want to delete this category?")

                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                db.deleteCategoryRow(expenseListFilter.get(listPosition).getGroupFocAll().get(expandedListPosition).getId());
                                ((CategoryActivity) context).inflateData();
                            })

                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Utils.ShowToast(context, "Data is already associated with this Category. This can't be deleted");
                }
            }
        });

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
        View view = layoutInflater.inflate(R.layout.list_parent_cat, null);
        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(listPosition);
        TextView listTitleTextView = view.findViewById(R.id.listTitle);

        TextView tv_expenditures = view.findViewById(R.id.tv_expenditures);
        // tv_expenditures.setVisibility(View.GONE);
        listTitleTextView.setText(expenseListFilter.get(listPosition).getIncome_Expenses());

        //tv_expenditures.setText("Expenses: " + expenseListFilter.get(listPosition).getExp_amt_() + " Income: " + expenseListFilter.get(listPosition).getInc_amt_());


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
