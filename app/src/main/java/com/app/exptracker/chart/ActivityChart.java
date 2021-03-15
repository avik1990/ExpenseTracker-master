package com.app.exptracker.chart;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.exptracker.R;
import com.app.exptracker.adapter.AverageAdapter;
import com.app.exptracker.adapter.MonthAdapter;
import com.app.exptracker.adapter.MonthlyExpenseAdapter;
import com.app.exptracker.adapter.YearlyExpenseAdapter;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.model.Months;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.Utils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ActivityChart extends BaseActivity implements View.OnClickListener,
        OnChartValueSelectedListener, MonthAdapter.GetMonthFromAdapter, MonthlyExpenseAdapter.GetYearFromAdapter, AverageAdapter.GetMonthFromAdapter, YearlyExpenseAdapter.GetYearFromreturnedYearView {

    Context context;
    DatabaseHelper db;
    ImageView iv_dropdown;
    TextView sp_month;
    ImageView btn_back, btn_menu;
    List<ExcelDataModel> list_transtype = new ArrayList<>();
    List<ExcelDataModel> list_avg = new ArrayList<>();
    TextView tv_currentyear;
    RecyclerView rl_recyclerview, rl_recy_avg;
    Dialog dialogMonthView;
    private PieChart chart;
    ImageView top_iv_left;
    ImageView top_iv_right;
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
    RecyclerView rl_Yearview;
    RelativeLayout rl_calender_mainview;
    AverageAdapter averageAdapter;
    ProgressDialog p;
    ExcelDataModel excelDataModel;
    double st_expense = 0;
    TextView tv_datanotfound;
    int month_count = 0;
    Button btnYearview;
    MonthlyExpenseAdapter.GetYearFromAdapter getYearFromAdapter;
    MonthlyExpenseAdapter.GetYearFromAdapter getMonthlyFromAdapter;
    MonthlyExpenseAdapter monthlyExpenseAdapter;
    RecyclerView rl_view;
    String from = "";
    String incexpflag = "";
    String popupYear;
    Dialog dialogYearView;
    YearlyExpenseAdapter yearlyExpenseAdapter;
    YearlyExpenseAdapter.GetYearFromreturnedYearView getYearlyFromAdapter;
    private static final int RESULT_UPDATED = 9999;
    String stFlag = "1";

    @Override
    protected void InitListner() {
        context = this;
        getYearlyFromAdapter = this;
        getMonthlyFromAdapter = this;
        db = new DatabaseHelper(context);
        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        getYearFromAdapter = this;
        from = getIntent().getExtras().getString("from");
        //fill chartData
        list_transtype.clear();
        list_transtype = db.GetAllFinalTransactionDataGroup();

        chart = findViewById(R.id.pieChart);
        calenderview = findViewById(R.id.calenderview);
        sp_month = findViewById(R.id.sp_month);
        ll_monthView = findViewById(R.id.ll_monthView);

        rl_recyclerview = findViewById(R.id.rl_recyclerview);
        rl_recyclerview.setLayoutManager(new GridLayoutManager(context, 6));
        rl_recy_avg = findViewById(R.id.rl_recy_avg);
        tv_currentyear = findViewById(R.id.tv_currentyear);
        tv_year_back = findViewById(R.id.tv_year_back);
        tv_year_forward = findViewById(R.id.tv_year_forward);

        rl_calender_mainview = findViewById(R.id.rl_calender_mainview);

        rl_calender_mainview.setOnClickListener(this);
        tv_year_back.setOnClickListener(this);
        tv_year_forward.setOnClickListener(this);
        ll_monthView.setOnClickListener(this);

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);

        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(63f);
        chart.setTransparentCircleRadius(20f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        // add a selection listener
        chart.setOnChartValueSelectedListener(this);

        chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setTextSize(14f);
        l.setFormSize(10f);
        l.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
        l.setYEntrySpace(5f);
        l.setWordWrapEnabled(true);
        l.setYOffset(0f);

        handleFlags();
    }

    private void handleFlags() {
        if (from.equalsIgnoreCase("sidemenu")) {
            incexpflag = "Expenses";
            st_year = Utils.getCurrentYear();
            tv_currentyear.setText(st_year);
            current_year = Utils.getCurrentYear();
            current_month = current_year + "-" + Utils.getCurrentMonthsIndex(Utils.getCurrentMonths());
            month_count = Integer.parseInt(Utils.getCurrentMonthsIndex(Utils.getCurrentMonths())) - 1;
            sp_month.setText(Utils.getCurrentMonths() + " " + Utils.getCurrentYear());

            chart.setDrawEntryLabels(false);
            list_avg.clear();

            new AsyncTaskSummationExpenses().execute();
        } else if (from.equalsIgnoreCase("Expenses")) {
            incexpflag = "Expenses";

            current_month = getIntent().getExtras().getString("current_date");
            //Utils.ShowToast(context, current_month);
            if (!current_month.isEmpty()) {
                String a[] = current_month.split("-");
                st_year = a[0];
                // st_year = Utils.getCurrentYear();
                tv_currentyear.setText(st_year);

                current_year = a[0];
                //current_month = current_year + "-" + Utils.getCurrentMonthsIndex(Utils.getCurrentMonths());
                month_count = Integer.parseInt(Utils.getCurrentMonthsIndex(Utils.getCurrentMonths(a[1]))) - 1;
                sp_month.setText(Utils.getCurrentMonths(a[1]) + " " + st_year);
                //Utils.ShowToast(context, "" + month_count);

                chart.setDrawEntryLabels(false);
                list_avg.clear();
            }
            new AsyncTaskSummationExpenses().execute();

        } else if (from.equalsIgnoreCase("Income")) {
            incexpflag = "Income";
            current_month = getIntent().getExtras().getString("current_date");
            if (!current_month.isEmpty()) {
                String a[] = current_month.split("-");
                st_year = a[0];
                //st_year = Utils.getCurrentYear();
                tv_currentyear.setText(st_year);

                current_year = a[0];
                //current_month = current_year + "-" + Utils.getCurrentMonthsIndex(Utils.getCurrentMonths());
                month_count = Integer.parseInt(Utils.getCurrentMonthsIndex(Utils.getCurrentMonths(a[1]))) - 1;
                sp_month.setText(Utils.getCurrentMonths(a[1]) + " " + st_year);
                chart.setDrawEntryLabels(false);
                list_avg.clear();
            }

            new AsyncTaskSummationExpenses().execute();
        }
    }


    private void setAdapter() {
        if (list_avg.size() > 0) {
            tv_datanotfound.setVisibility(View.GONE);
            rl_recy_avg.setVisibility(View.VISIBLE);
            averageAdapter = new AverageAdapter(context, list_avg, this, st_expense);
            rl_recy_avg.setAdapter(averageAdapter);
            //setChartData();
        } else {
            tv_datanotfound.setVisibility(View.VISIBLE);
            rl_recy_avg.setVisibility(View.GONE);
        }


    }

    private void setChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (list_avg.size() > 0) {
            if (list_avg.size() > 5) {
                for (int i = 0; i < 5; i++) {
                    double percentage = 0;
                    if (list_avg.get(i).getAmount_() != 0) {
                        percentage = (list_avg.get(i).getAmount_() / st_expense) * 100;
                    }
                    //Log.d("CategoryData", list_avg.get(i).getCategory());
                    // if (i < 5) {
                    entries.add(new PieEntry((int) percentage, list_avg.get(i).getCategory() + " " + new DecimalFormat("0.0").format(percentage) + "%", getResources().getDrawable(R.drawable.ic_about)));
                   /* } else {
                        percentage = +percentage;
                        Log.d("Chart",)
                        //entries.add(new PieEntry((int) percentage, "Others" + " " + new DecimalFormat("0.0").format(percentage) + "%", getResources().getDrawable(R.drawable.ic_about)));
                    }*/

                   /* if (i <= 4) {
                        entries.add(new PieEntry((int)percentage, list_avg.get(i).getMemo() + " " + percentage, getResources().getDrawable(R.drawable.ic_about)));
                    }else{

                        entries.add(new PieEntry((int)percentage, list_avg.get(i).getMemo() + " " + percentage, getResources().getDrawable(R.drawable.ic_about)));
                    }*/
                }
            } else {
                for (int i = 0; i < list_avg.size(); i++) {
                    double percentage = 0;
                    if (list_avg.get(i).getAmount_() != 0) {
                        percentage = (list_avg.get(i).getAmount_() / st_expense) * 100;
                    }
                    Log.d("CategoryData", list_avg.get(i).getCategory());
                    entries.add(new PieEntry((int) percentage, list_avg.get(i).getCategory() + " " + new DecimalFormat("0.0").format(percentage) + "%", getResources().getDrawable(R.drawable.ic_about)));
                }
            }
        }

        /*ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < list_transtype.size(); i++) {
            String[] parties = new String[list_transtype.size()];
            parties[i] = list_transtype.get(i).getCategory();
            float randomval = (float) (Math.random() * list_transtype.size()) + list_transtype.size() / 5;
            Log.d("Values", "" + randomval);
            entries.add(new PieEntry(randomval, "Hello" + " " + randomval, getResources().getDrawable(R.drawable.ic_about)));
        }*/
       /* if(entries.size()<=0){
            return;
        }*/


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setColors(new int[]{R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark, R.color.bg_screen2, R.color.bg_screen3}, context);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        //hide text inside the PIE graph
        dataSet.setDrawValues(false);
        //add a lot of colors

       /* ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);*/
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();
    }


    @Override
    protected void InitResources() {
        btnYearview = findViewById(R.id.btnYearview);
        tv_datanotfound = findViewById(R.id.tv_datanotfound);
        top_iv_left = findViewById(R.id.top_iv_left);
        top_iv_right = findViewById(R.id.top_iv_right);
        top_iv_left.setOnClickListener(this);
        top_iv_right.setOnClickListener(this);
        top_iv_left.setVisibility(View.VISIBLE);
        top_iv_right.setVisibility(View.VISIBLE);
        sp_month = findViewById(R.id.sp_month);
        rl_recy_avg = findViewById(R.id.rl_recy_avg);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        iv_dropdown.setVisibility(View.VISIBLE);
        btnYearview.setVisibility(View.VISIBLE);
        btnYearview.setOnClickListener(this);
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
        } else if (v == ll_monthView) {
            calenderview.setVisibility(View.VISIBLE);
            monthAdapter = new MonthAdapter(context, Utils.getAllMonths(), this, current_month);
            rl_recyclerview.setAdapter(monthAdapter);
        } else if (v == rl_calender_mainview) {
            calenderview.setVisibility(View.GONE);
        } else if (v == top_iv_left) {
            if (month_count == 0) {
                return;
            } else {
                month_count--;
                current_month = tv_currentyear.getText().toString() + "-" + Utils.getCurrentMonthsIndex(Utils.getAllMonths().get(month_count).getMonthname());
                sp_month.setText(Utils.getAllMonths().get(month_count).getMonthname() + " " + tv_currentyear.getText().toString());
                Log.d("current_month", current_month);
                calenderview.setVisibility(View.GONE);
                new AsyncTaskSummationExpenses().execute();
            }
        } else if (v == top_iv_right) {
            if (month_count < 11) {
                month_count++;
                current_month = tv_currentyear.getText().toString() + "-" + Utils.getCurrentMonthsIndex(Utils.getAllMonths().get(month_count).getMonthname());
                sp_month.setText(Utils.getAllMonths().get(month_count).getMonthname() + " " + tv_currentyear.getText().toString());
                Log.d("current_month", current_month);
                calenderview.setVisibility(View.GONE);
                new AsyncTaskSummationExpenses().execute();
            } else {
                return;
            }
        } else if (v == btnYearview) {
            if (!sp_month.getText().toString().trim().isEmpty()) {
                String a[] = sp_month.getText().toString().trim().split(" ");
                current_month = a[1] + "-" + Utils.getCurrentMonthsIndex(a[0]);
                current_year = a[1];
            }
            new AsyncDialogView().execute();
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
        current_month = tv_currentyear.getText().toString() + "-" + month;
        month_count = Integer.parseInt(Utils.getCurrentMonthsIndex(Utils.getCurrentMonths(month))) - 1;
        sp_month.setText(Utils.getCurrentMonths(month) + " " + tv_currentyear.getText().toString());
        calenderview.setVisibility(View.GONE);
        new AsyncTaskSummationExpenses().execute();
        return month;
    }

    @Override
    public void returnedMonth(String month, String sort) {
    }

    @Override
    public void returnedID(String id) {
        Intent i = new Intent(context, ActivityCategorywiseChart.class);
        i.putExtra("selecteddate", current_month);
        i.putExtra("incexpflag", incexpflag);
        i.putExtra("_id", id);
        startActivityForResult(i, RESULT_UPDATED);
    }

    @Override
    public String returnedYear(String month) {
        current_month = current_year + "-" + month;
        month_count = Integer.parseInt(Utils.getCurrentMonthsIndex(Utils.getCurrentMonths(month))) - 1;
        sp_month.setText(Utils.getCurrentMonths(month) + " " + current_year);
        calenderview.setVisibility(View.GONE);
        if (dialogYearView != null) {
            dialogYearView.dismiss();
        }

        if (dialogMonthView != null) {
            dialogMonthView.dismiss();
        }

        new AsyncTaskSummationExpenses().execute();
        return null;
    }

    @Override
    public String returnedYearView(String year) {
        if (dialogYearView != null) {
            dialogYearView.dismiss();
        }
        new AsyncDialogFromYearView(year).execute();
        return null;
    }


    private class AsyncTaskSummationExpenses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            excelDataModel = new ExcelDataModel();
            excelDataModel = db.GetExpensesData(incexpflag, current_month);
            st_expense = 0;
            st_expense = excelDataModel.getAmount_();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.show();
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
            Log.d("current_month", current_month);
            list_avg = db.getAverageData(current_month, incexpflag);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(stFlag.equalsIgnoreCase("0")){
                if(averageAdapter!=null){
                    averageAdapter.updateAdapterData(list_avg);
                }
            }else{
                setAdapter();
            }

            p.dismiss();
        }
    }


    private class AsyncDialogView extends AsyncTask<Void, Void, Void> {

        List<ExcelDataModel> listPopup = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            listPopup.clear();
            String year = "";
            if (!current_month.isEmpty()) {
                String y[] = current_month.split("-");
                year = y[0];
            }
            listPopup = db.getExpenseDetailsMonthWise(year);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            p.dismiss();
            dialogMonthView = new Dialog(context);
            dialogMonthView.setContentView(R.layout.year_popup);
            final Window window = dialogMonthView.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogMonthView.setCancelable(true);
            dialogMonthView.setCanceledOnTouchOutside(false);

            ImageView iv_cross = dialogMonthView.findViewById(R.id.iv_cross);
            rl_view = dialogMonthView.findViewById(R.id.rl_view);

            ImageView tv_year_back = dialogMonthView.findViewById(R.id.tv_year_back);
            ImageView tv_year_forward = dialogMonthView.findViewById(R.id.tv_year_forward);
            TextView tv_currentyear = dialogMonthView.findViewById(R.id.tv_currentyear);
            tv_currentyear.setText(current_year);

            tv_currentyear.setOnClickListener(view -> {
                popupYear = tv_currentyear.getText().toString().trim();
                dialogMonthView.dismiss();
                new AsyncDialogYearView().execute();
            });

            iv_cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogMonthView.dismiss();
                }
            });

            tv_year_back.setOnClickListener(view -> {
                int year = Integer.parseInt(st_year);
                year--;
                st_year = String.valueOf(year);
                tv_currentyear.setText(st_year);
                current_year = tv_currentyear.getText().toString();
                new AsyncUpdateDialogData().execute();
            });

            tv_year_forward.setOnClickListener(view -> {
                int year = Integer.parseInt(st_year);
                year++;
                st_year = String.valueOf(year);
                tv_currentyear.setText(st_year);
                current_year = tv_currentyear.getText().toString();
                new AsyncUpdateDialogData().execute();
            });

            rl_view.setLayoutManager(new GridLayoutManager(context, 3));
            dialogMonthView.show();
            monthlyExpenseAdapter = new MonthlyExpenseAdapter(context, Utils.getAllMonths(), getYearFromAdapter, current_month, listPopup);
            rl_view.setAdapter(monthlyExpenseAdapter);
        }
    }


    private class AsyncUpdateDialogData extends AsyncTask<Void, Void, Void> {

        List<ExcelDataModel> listPopup = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            listPopup.clear();
            listPopup = db.getExpenseDetailsMonthWise(current_year);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //monthlyExpenseAdapter.updateReceiptsList(listPopup);
            monthlyExpenseAdapter = new MonthlyExpenseAdapter(context, Utils.getAllMonths(), getYearFromAdapter, current_month, listPopup);
            rl_view.setAdapter(monthlyExpenseAdapter);
        }
    }

    private class AsyncDialogYearView extends AsyncTask<Void, Void, Void> {

        List<ExcelDataModel> listPopup = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            listPopup.clear();
            listPopup = db.getExpenseDetailsYearWise();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            p.dismiss();
            openYearViewCalender(popupYear, listPopup);
        }
    }

    private void openYearViewCalender(String year1, List<ExcelDataModel> listPopup) {
        dialogYearView = new Dialog(context);
        dialogYearView.setContentView(R.layout.year_popup1);
        final Window window = dialogYearView.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogYearView.setCancelable(true);
        dialogYearView.setCanceledOnTouchOutside(false);

        ImageView iv_cross = dialogYearView.findViewById(R.id.iv_cross);
        rl_Yearview = dialogYearView.findViewById(R.id.rl_view);

        ImageView tv_year_back = dialogYearView.findViewById(R.id.tv_year_back);
        ImageView tv_year_forward = dialogYearView.findViewById(R.id.tv_year_forward);
        TextView tv_currentyear = dialogYearView.findViewById(R.id.tv_currentyear);

        int yearsPrev = Integer.parseInt(year1) - 5;
        int yearsNext = Integer.parseInt(year1) + 6;

        tv_currentyear.setText(yearsPrev + " - " + yearsNext);

        String dateRange1 = yearsPrev + " - " + yearsNext;
        generateCalender(dateRange1, "b", listPopup);

        iv_cross.setOnClickListener(view -> dialogYearView.dismiss());

        tv_year_back.setOnClickListener(view -> {
            if (!tv_currentyear.getText().toString().trim().isEmpty()) {
                String p[] = tv_currentyear.getText().toString().split(" - ");
                int yearP = Integer.parseInt(p[0].trim()) - 12;
                int yearF = Integer.parseInt(p[1].trim()) - 12;
                String dateRange = yearP + " - " + yearF;
                tv_currentyear.setText(yearP + " - " + yearF);
                generateCalender(dateRange, "b", listPopup);
            }

           /* year--;
            st_year = String.valueOf(year);
            tv_currentyear.setText(st_year);
            current_year = tv_currentyear.getText().toString();*/
            //new AsyncUpdateDialogData().execute();
        });

        tv_year_forward.setOnClickListener(view -> {
            /*int year = Integer.parseInt(st_year);
            year++;
            st_year = String.valueOf(year);
            tv_currentyear.setText(st_year);
            current_year = tv_currentyear.getText().toString();
            //  new AsyncUpdateDialogData().execute();*/
            if (!tv_currentyear.getText().toString().trim().isEmpty()) {
                String p[] = tv_currentyear.getText().toString().trim().split(" - ");
                int yearP = Integer.parseInt(p[0].trim()) + 12;
                int yearF = Integer.parseInt(p[1].trim()) + 12;
                tv_currentyear.setText(yearP + " - " + yearF);
                String dateRange = yearP + " - " + yearF;
                generateCalender(dateRange, "b", listPopup);
            }
        });

        /*rl_view.setLayoutManager(new GridLayoutManager(context, 3));
        dialogYearView.show();
        monthlyExpenseAdapter = new MonthlyExpenseAdapter(context, Utils.getAllMonths(), getMonthlyFromAdapter, current_month, listPopup);
        rl_view.setAdapter(monthlyExpenseAdapter);*/

        dialogYearView.show();
    }

    private void generateCalender(String dateRange, String b, List<ExcelDataModel> listPopup) {
        int lowerdate = 0;
        int upperdate = 0;
        List<Months> listMonth = new ArrayList<>();
        listMonth.clear();
        if (!dateRange.isEmpty()) {
            String a[] = dateRange.split(" - ");
            lowerdate = Integer.parseInt(a[0]);
            upperdate = Integer.parseInt(a[1]);
            for (int i = lowerdate; i <= upperdate; i++) {
                Log.d("YearRange", "" + i);
                listMonth.add(new Months(String.valueOf(i), String.valueOf(i), ""));
            }

        }

        if (listMonth.size() > 0) {
            rl_Yearview.setLayoutManager(new GridLayoutManager(context, 3));
            yearlyExpenseAdapter = new YearlyExpenseAdapter(context, listMonth, getYearlyFromAdapter, current_month, listPopup);
            rl_Yearview.setAdapter(yearlyExpenseAdapter);
        }

    }

    private class AsyncDialogFromYearView extends AsyncTask<Void, Void, Void> {

        List<ExcelDataModel> listPopup = new ArrayList<>();
        String year = "";

        public AsyncDialogFromYearView(String year) {
            this.year = year;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            listPopup.clear();
            listPopup = db.getExpenseDetailsMonthWise(year);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialogMonthView = new Dialog(context);
            dialogMonthView.setContentView(R.layout.year_popup);
            final Window window = dialogMonthView.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogMonthView.setCancelable(true);
            dialogMonthView.setCanceledOnTouchOutside(false);

            ImageView iv_cross = dialogMonthView.findViewById(R.id.iv_cross);
            rl_view = dialogMonthView.findViewById(R.id.rl_view);

            ImageView tv_year_back = dialogMonthView.findViewById(R.id.tv_year_back);
            ImageView tv_year_forward = dialogMonthView.findViewById(R.id.tv_year_forward);
            TextView tv_currentyear = dialogMonthView.findViewById(R.id.tv_currentyear);
            tv_currentyear.setText(year);
            current_year = year;

            tv_currentyear.setOnClickListener(view -> {
                popupYear = tv_currentyear.getText().toString().trim();
                dialogMonthView.dismiss();
                new AsyncDialogYearView().execute();
            });


            iv_cross.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    dialogMonthView.dismiss();
                }
            });

            tv_year_back.setOnClickListener(view -> {
                int year = Integer.parseInt(st_year);
                year--;
                st_year = String.valueOf(year);
                tv_currentyear.setText(st_year);
                current_year = tv_currentyear.getText().toString();
                new AsyncUpdateDialogData().execute();
            });

            tv_year_forward.setOnClickListener(view -> {
                int year = Integer.parseInt(st_year);
                year++;
                st_year = String.valueOf(year);
                tv_currentyear.setText(st_year);
                current_year = tv_currentyear.getText().toString();
                new AsyncUpdateDialogData().execute();
            });
            rl_view.setLayoutManager(new GridLayoutManager(context, 3));
            dialogMonthView.show();
            monthlyExpenseAdapter = new MonthlyExpenseAdapter(context, Utils.getAllMonths(), getMonthlyFromAdapter, current_month, listPopup);
            rl_view.setAdapter(monthlyExpenseAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_UPDATED:
                stFlag = "0";
                new AsyncTaskSummationExpenses().execute();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stFlag = "1";
    }
}
