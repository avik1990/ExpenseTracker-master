package com.app.exptracker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.exptracker.daywiseexpenses.DayWiseTransAdapter;
import com.app.exptracker.adapter.MonthAdapter;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.Utils;
import com.github.mikephil.charting.charts.PieChart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ActivityDayWiseData extends BaseActivity implements View.OnClickListener,
        DayWiseTransAdapter.GetMonthFromAdapter {

    private static final String TAG = "ExpenseDetails";
    Context context;
    DatabaseHelper db;
    ImageView iv_dropdown;
    TextView sp_month;
    ImageView btn_back, btn_menu;
    TextView tv_currentyear;
    RecyclerView rl_recy_avg;
    ImageView iv_logo;
    private PieChart chart;
    String st_year = "";
    String current_year = "";
    //for calender
    RelativeLayout calenderview;
    ImageView tv_year_back;
    ImageView tv_year_forward;
    String current_month = "";
    LinearLayout ll_monthView;
    MonthAdapter monthAdapter;
    RelativeLayout rl_calender_mainview;
    ProgressDialog p;
    List<ExcelDataModel> excelDataModel = new ArrayList<>();
    double st_expense = 0;
    ImageView top_iv_left;
    ImageView top_iv_right;
    RadioGroup rgFilter;
    private static final int RESULT_UPDATED = 9999;
    String filterType = "";
    TextView tv_payment_mode_exp, tv_payment_mode_inc;
    double st_expense_paymentMode = 0;
    double st_income_paymentMode = 0;
    LinearLayout rl_paymentmodecal;
    DayWiseTransAdapter adapter;

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);

        current_month = getIntent().getExtras().getString("date");
        sp_month = findViewById(R.id.sp_month);

        if (!current_month.isEmpty()) {
            String a[] = current_month.split("-");
            sp_month.setText(Utils.getFormattedDate(current_month));
        }

        chart = findViewById(R.id.pieChart);
        calenderview = findViewById(R.id.calenderview);
        tv_payment_mode_exp = findViewById(R.id.tv_payment_mode_exp);
        tv_payment_mode_inc = findViewById(R.id.tv_payment_mode_inc);
        ll_monthView = findViewById(R.id.ll_monthView);
        rgFilter = findViewById(R.id.rgFilter);
        rgFilter.setVisibility(View.VISIBLE);

        st_year = Utils.getCurrentYear();
        rl_recy_avg = findViewById(R.id.rl_recy_avg);
        tv_currentyear = findViewById(R.id.tv_currentyear);
        tv_year_back = findViewById(R.id.tv_year_back);
        tv_year_forward = findViewById(R.id.tv_year_forward);

        rl_paymentmodecal = findViewById(R.id.rl_paymentmodecal);
        rl_paymentmodecal.setVisibility(View.VISIBLE);

        rl_calender_mainview = findViewById(R.id.rl_calender_mainview);
        rl_calender_mainview.setOnClickListener(this);

        //chart.setOnValueTouchListener(new ValueTouchListener());
        tv_currentyear.setText(st_year);
        tv_year_back.setOnClickListener(this);
        tv_year_forward.setOnClickListener(this);
        ll_monthView.setOnClickListener(this);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        new AsyncTaskFetchAllDataDayWise().execute();
    }


    private void setAdapter() {
        if (excelDataModel.size() > 0) {
            rl_recy_avg.setVisibility(View.VISIBLE);
            adapter = new DayWiseTransAdapter(context, excelDataModel, this);
            rl_recy_avg.setAdapter(adapter);
            filterType = "";
            new AsyncTaskSummationExpensesModeWise().execute();

            if (adapter != null) {
                rgFilter.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                    switch (checkedId) {
                        case R.id.rbAll:
                            filterType = "";
                            adapter.getFilter().filter("");
                            new AsyncTaskSummationExpensesModeWise().execute();
                            break;
                        case R.id.rbCash:
                            filterType = "Cash";
                            adapter.getFilter().filter("Cash");
                            new AsyncTaskSummationExpensesModeWise().execute();
                            break;
                        case R.id.rbDebit:
                            filterType = "Credit Card";
                            adapter.getFilter().filter("Credit Card");
                            new AsyncTaskSummationExpensesModeWise().execute();
                            break;
                        case R.id.rbCheque:
                            filterType = "Cheque/Debit Card";
                            adapter.getFilter().filter("Cheque/Debit Card");
                            new AsyncTaskSummationExpensesModeWise().execute();
                            break;
                    }
                });

            }
        } else {
            Utils.ShowToast(context, "No Data Found");
            rl_recy_avg.setVisibility(View.GONE);
        }
    }


    @Override
    protected void InitResources() {
        top_iv_left = findViewById(R.id.top_iv_left);
        top_iv_right = findViewById(R.id.top_iv_right);

        top_iv_left.setVisibility(View.GONE);
        top_iv_right.setVisibility(View.GONE);
        sp_month = findViewById(R.id.sp_month);
        rl_recy_avg = findViewById(R.id.rl_recy_avg);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        btn_back = findViewById(R.id.btn_back);
        iv_logo = findViewById(R.id.iv_logo);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        iv_dropdown.setVisibility(View.GONE);
        iv_logo.setVisibility(View.GONE);
        sp_month.setText(current_month);
    }

    @Override
    protected void InitPermission() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_chart;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        } else if (v == tv_year_back) {
            int year = Integer.parseInt(st_year);
            year--;
            st_year = String.valueOf(year);
            tv_currentyear.setText(st_year);
            current_year = st_year;
        } else if (v == tv_year_forward) {
            int year = Integer.parseInt(st_year);
            year++;
            st_year = String.valueOf(year);
            tv_currentyear.setText(st_year);
            current_year = st_year;
        } /*else if (v == ll_monthView) {
            calenderview.setVisibility(View.VISIBLE);
            monthAdapter = new MonthAdapter(context, Utils.getAllMonths(), this, current_month);
            rl_recyclerview.setAdapter(monthAdapter);
        }*/ else if (v == rl_calender_mainview) {
            calenderview.setVisibility(View.GONE);
        }
    }

    @Override
    public void returnedMonth(String month, String sort) {
    }

    @Override
    public void returnedID(String id) {
        Intent i = new Intent(context, ExpenseDetails.class);
        i.putExtra("trans_id", String.valueOf(id));
        startActivityForResult(i, RESULT_UPDATED);
    }


    private class AsyncTaskFetchAllDataDayWise extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            excelDataModel = db.GetTransactionDatafsfsdfdsfs(current_month);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            excelDataModel.clear();
            p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (excelDataModel.size() > 0) {
                setAdapter();
            } else {
                Utils.ShowToast(context, "No Data Found");
                rl_recy_avg.setVisibility(View.GONE);
                tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
                tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
            }
            p.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_UPDATED:
                new AsyncTaskFetchAllDataDayWise().execute();
                break;
        }
    }


    private class AsyncTaskSummationExpensesModeWise extends AsyncTask<Void, Void, Void> {

        ExcelDataModel excelDataModel1 = new ExcelDataModel();

        @Override
        protected Void doInBackground(Void... voids) {
            excelDataModel1 = db.GetExpensesDataModeDayWise("Expenses", current_month, filterType);
            st_expense_paymentMode = 0;
            //double st_income_paymentMode = 0, st_expense_paymentMode = 0;
            st_expense_paymentMode = excelDataModel1.getAmount_();
            //count_ = excelDataModel.getCount_();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (st_expense_paymentMode <= 0) {
                tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
            } else {
                tv_payment_mode_exp.setText("Expenses: " + String.valueOf(new DecimalFormat("#,##,###.00").format(st_expense_paymentMode)));
            }
            new AsyncTaskSummationExpensesModeWiseIncome().execute();
        }
    }


    private class AsyncTaskSummationExpensesModeWiseIncome extends AsyncTask<Void, Void, Void> {

        ExcelDataModel excelDataModel1 = new ExcelDataModel();

        @Override
        protected Void doInBackground(Void... voids) {
            excelDataModel1 = db.GetExpensesDataModeDayWise("Income", current_month, filterType);
            st_income_paymentMode = 0;
            //double st_income_paymentMode = 0, st_expense_paymentMode = 0;
            st_income_paymentMode = excelDataModel1.getAmount_();
            //count_ = excelDataModel.getCount_();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (st_income_paymentMode <= 0) {
                tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
            } else {
                tv_payment_mode_inc.setText("Income: " + String.valueOf(new DecimalFormat("#,##,###.00").format(st_income_paymentMode)));
            }
        }
    }

}
