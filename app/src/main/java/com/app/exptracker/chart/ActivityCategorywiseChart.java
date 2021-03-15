package com.app.exptracker.chart;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.exptracker.ExpenseDetails;
import com.app.exptracker.R;
import com.app.exptracker.adapter.CatwiseAverageAdapter;
import com.app.exptracker.adapter.MonthAdapter;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ActivityCategorywiseChart extends BaseActivity implements View.OnClickListener,
        OnChartValueSelectedListener, MonthAdapter.GetMonthFromAdapter, CatwiseAverageAdapter.GetMonthFromAdapter {

    private static final String TAG = "ExpenseDetails";
    Context context;
    DatabaseHelper db;
    ImageView iv_dropdown;
    TextView sp_month;
    ImageView btn_back, btn_menu;
    List<ExcelDataModel> list_transtype = new ArrayList<>();
    List<ExcelDataModel> list_avg = new ArrayList<>();
    TextView tv_currentyear;
    RecyclerView rl_recyclerview, rl_recy_avg;
    ImageView iv_logo;
    private PieChart chart;
    private boolean hasLabels = false;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;
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
    CatwiseAverageAdapter CatwiseAverageAdapter;
    ProgressDialog p;
    ExcelDataModel excelDataModel;
    double st_expense = 0;
    String id = "";
    ImageView top_iv_left;
    ImageView top_iv_right;
    String incexpflag = "";
    RadioGroup rgFilter;
    private static final int RESULT_UPDATED = 9999;
    String filterType = "";
    TextView tv_payment_mode_exp;
    double st_expense_paymentMode = 0;
    LinearLayout rl_paymentmodecal;
    String stFlag = "1";

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);
       // Utils.ShowToast(context,"Hello");
        id = getIntent().getExtras().getString("_id");
        current_month = getIntent().getExtras().getString("selecteddate");
        incexpflag = getIntent().getExtras().getString("incexpflag");
        list_transtype.clear();
        //list_transtype = db.GetAllFinalTransactionDataGroup();
        sp_month = findViewById(R.id.sp_month);
        //Utils.ShowToast(context,"Hello");
        if (!current_month.isEmpty()) {
            String a[] = current_month.split("-");
            sp_month.setText(Utils.getCurrentMonths(a[1]) + " " + a[0]);
        }

        chart = findViewById(R.id.pieChart);
        calenderview = findViewById(R.id.calenderview);
        tv_payment_mode_exp = findViewById(R.id.tv_payment_mode_exp);
        ll_monthView = findViewById(R.id.ll_monthView);
        rgFilter = findViewById(R.id.rgFilter);
        rgFilter.setVisibility(View.VISIBLE);

        st_year = Utils.getCurrentYear();
        rl_recyclerview = findViewById(R.id.rl_recyclerview);
        rl_recyclerview.setLayoutManager(new GridLayoutManager(context, 6));
        rl_recy_avg = findViewById(R.id.rl_recy_avg);
        tv_currentyear = findViewById(R.id.tv_currentyear);
        tv_year_back = findViewById(R.id.tv_year_back);
        tv_year_forward = findViewById(R.id.tv_year_forward);

        rl_paymentmodecal= findViewById(R.id.rl_paymentmodecal);
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

        new AsyncTaskSummationExpenses().execute();
    }


    private void setAdapter() {
        if (list_avg.size() > 0) {
            rl_recy_avg.setVisibility(View.VISIBLE);
            CatwiseAverageAdapter = new CatwiseAverageAdapter(context, list_avg, this, st_expense);
            rl_recy_avg.setAdapter(CatwiseAverageAdapter);
            filterType = "";
            new AsyncTaskSummationExpensesModeWise().execute();

            if (CatwiseAverageAdapter != null) {
                rgFilter.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                    switch (checkedId) {
                        case R.id.rbAll:
                            filterType = "";
                            CatwiseAverageAdapter.getFilter().filter("");
                            new AsyncTaskSummationExpensesModeWise().execute();
                            break;
                        case R.id.rbCash:
                            filterType = "Cash";
                            CatwiseAverageAdapter.getFilter().filter("Cash");
                            new AsyncTaskSummationExpensesModeWise().execute();
                            break;
                        case R.id.rbDebit:
                            filterType = "Credit Card";
                            CatwiseAverageAdapter.getFilter().filter("Credit Card");
                            new AsyncTaskSummationExpensesModeWise().execute();
                            break;
                        case R.id.rbCheque:
                            filterType = "Cheque/Debit Card";
                            CatwiseAverageAdapter.getFilter().filter("Cheque/Debit Card");
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
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public String returnedMonth(String month) {
        /*current_month = current_year + "-" + month;
        sp_month.setText(Utils.getCurrentMonths(month) + " " + tv_currentyear.getText().toString());
        calenderview.setVisibility(View.GONE);
        //new Dashboard.AsyncTaskGetTransactionData().execute();
        new AsyncTaskSummationExpenses().execute();*/
        return month;
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


    private class AsyncTaskSummationExpenses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            excelDataModel = new ExcelDataModel();
            excelDataModel = db.GetExpensesDataIdWise(incexpflag, current_month, id);
            st_expense = 0;
            st_expense = excelDataModel.getAmount_();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new AsyncTaskFetchData().execute();
        }
    }


    class AsyncTaskFetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_avg.clear();
            list_avg = db.getAverageDataCategoryWise(current_month, incexpflag, id);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(stFlag.equalsIgnoreCase("0")){
                if(CatwiseAverageAdapter!=null){
                    CatwiseAverageAdapter.updateReceiptsList(list_avg);
                }
            }else{
                setAdapter();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_UPDATED:
                stFlag = "0";
                new AsyncTaskUpdateList().execute();
                break;
        }
    }

    private class AsyncTaskUpdateList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_avg.clear();
            list_avg = db.getAverageDataCategoryWise(current_month, incexpflag, id);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_avg.size() > 0) {
                try {
                    new AsyncTaskSummationExpenses().execute();
                } catch (Exception e) {
                }
            } else {
                rl_recyclerview.setVisibility(View.GONE);
            }
        }
    }


    private class AsyncTaskSummationExpensesModeWise extends AsyncTask<Void, Void, Void> {

        ExcelDataModel excelDataModel1 = new ExcelDataModel();

        @Override
        protected Void doInBackground(Void... voids) {
            excelDataModel1 = db.GetExpensesDataModeWiseWithId(incexpflag, current_month, filterType, id);
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
                tv_payment_mode_exp.setText("Total: " + String.valueOf(0.0));
            } else {
                tv_payment_mode_exp.setText("Total: " + String.valueOf(new DecimalFormat("#,##,###.00").format(st_expense_paymentMode)));
            }

            //new Dashboard.AsyncTaskIncomeExpensesModeWise().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stFlag = "1";
    }

}
