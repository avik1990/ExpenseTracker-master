package com.app.exptracker.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.model.Months;
import com.app.exptracker.utility.CircularTextView;
import com.app.exptracker.utility.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MonthlyExpenseAdapter extends RecyclerView.Adapter<MonthlyExpenseAdapter.MyViewHolder> {

    private List<Months> countryList;
    Context mContext;
    GetYearFromAdapter getMonthFromAdapter;
    String currentmonth = "";
    List<ExcelDataModel> listPopup = new ArrayList<>();
    List<ExcelDataModel> listPopupTemp = new ArrayList<>();

    List<String> listmonth = new ArrayList<>();
    List<Double> listExpense = new ArrayList<>();
    List<Double> listIncome = new ArrayList<>();
    String cmonth = "";

    public interface GetYearFromAdapter {
        String returnedYear(String month);
    }


    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircularTextView tv_checfname;
        TextView tv_expense, tv_income;
        RelativeLayout rl_mainview;


        public MyViewHolder(View view) {
            super(view);
            tv_checfname = view.findViewById(R.id.tv_month);
            tv_expense = view.findViewById(R.id.tv_expense);
            tv_income = view.findViewById(R.id.tv_income);
            rl_mainview = view.findViewById(R.id.rl_mainview);
        }
    }

    public void updateReceiptsList(List<ExcelDataModel> listPopup) {
        this.listPopup = listPopup;
        listmonth.clear();
        listExpense.clear();
        listIncome.clear();

        if (listPopup.size() > 0) {
            for (int i = 0; i < listPopup.size(); i++) {
                for (int j = 0; j < listPopup.get(i).getGroupFocAll().size(); j++) {
                    if (!listPopup.get(i).getGroupFocAll().get(j).getDate().isEmpty()) {
                        String m[] = listPopup.get(i).getGroupFocAll().get(j).getDate().split("-");
                        String month = Utils.getCurrentMonths(m[1]);
                        listmonth.add(month);
                        listExpense.add(listPopup.get(i).getGroupFocAll().get(j).getExp_amt_());
                        listIncome.add(listPopup.get(i).getGroupFocAll().get(j).getInc_amt_());
                    }
                }
            }
        }

        this.notifyDataSetChanged();
    }

    public MonthlyExpenseAdapter(Context mContext, List<Months> countryList, GetYearFromAdapter getMonthFromAdapter, String currentmonth, List<ExcelDataModel> listPopup) {
        this.mContext = mContext;
        this.getMonthFromAdapter = getMonthFromAdapter;
        this.countryList = countryList;
        this.listPopup = listPopup;
        this.cmonth = currentmonth;

        listmonth.clear();
        listExpense.clear();
        listIncome.clear();

        if (listPopup.size() > 0) {
            for (int i = 0; i < listPopup.size(); i++) {
                for (int j = 0; j < listPopup.get(i).getGroupFocAll().size(); j++) {
                    if (!listPopup.get(i).getGroupFocAll().get(j).getDate().isEmpty()) {
                        String m[] = listPopup.get(i).getGroupFocAll().get(j).getDate().split("-");
                        String month = Utils.getCurrentMonths(m[1]);
                        listmonth.add(month);
                        listExpense.add(listPopup.get(i).getGroupFocAll().get(j).getExp_amt_());
                        listIncome.add(listPopup.get(i).getGroupFocAll().get(j).getInc_amt_());
                    }
                }
            }
        }

        if (!currentmonth.isEmpty()) {
            String mon[] = currentmonth.split("-");
            currentmonth = mon[1];
            this.currentmonth = currentmonth;
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Months c = countryList.get(position);
        holder.tv_checfname.setText(c.getMonthname());


        if (listmonth.size() > 0) {
            if (listmonth.contains(c.getMonthname())) {
                int pos = listmonth.indexOf(c.getMonthname());
                if (listIncome.get(pos) <= 0) {
                    holder.tv_income.setText("\u20B9" + "0.00");

                } else {
                    holder.tv_income.setText("\u20B9" + new DecimalFormat("#,##,###.00").format(listIncome.get(pos)));
                }
                holder.tv_expense.setText("\u20B9" + new DecimalFormat("#,##,###.00").format(listExpense.get(pos)));
            } else {
                holder.tv_income.setText("\u20B9" + "0.00");
                holder.tv_expense.setText("\u20B9" + "0.00");
            }
        }else {
            holder.tv_income.setText("\u20B9" + "0.00");
            holder.tv_expense.setText("\u20B9" + "0.00");
        }


        holder.rl_mainview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getMonthFromAdapter.returnedYear(Utils.getCurrentMonthsIndex(holder.tv_checfname.getText().toString()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_years, parent, false);
        return new MyViewHolder(v);
    }
}
