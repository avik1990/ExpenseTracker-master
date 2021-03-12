package com.app.exptracker.daywiseexpenses;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.app.exptracker.ActivityDayWiseData;
import com.app.exptracker.R;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityCalender extends BaseActivity implements View.OnClickListener, DayAdapter.GetDateFromAdapter {

    private static final String TAG = "ActivitySearch";
    Context context;
    DatabaseHelper db;

    List<ExcelDataModel> listDayWiseDate = new ArrayList<>();
    List<String> list_years = new ArrayList<>();

    ProgressDialog progressDialog;
    TextView sp_month;
    LinearLayout ll_monthView;
    ImageView btn_back, btn_menu;
    TextView tvMonthView;
    List<String> listDates = new ArrayList<>();
    private static final int RESULT_UPDATED = 9999;
    private static final String[] ENG_MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};

    ImageView ivPrevious, ivNext;
    DayAdapter adapter;

    String currentMonth = "";
    int currentMonthIndex = 0;
    String currentYear;
    RecyclerView rlRecycler;
    ImageView iv_dropdown;
    TextView sp_years;
    LinearLayout ll_yearView;
    String from = "", fromRecievedDate;

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        listDates.clear();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        from = getIntent().getExtras().getString("from");

        if (from.equalsIgnoreCase("sidemenu")) {
            currentYear = Utils.getCurrentYear();
            currentMonth = Utils.getCurrentMonths();
            currentMonthIndex = Integer.parseInt(Utils.getCurrentMonthsIndex(currentMonth));
            tvMonthView.setText(ENG_MONTH_NAMES[currentMonthIndex-1]);
            sp_years.setText(currentYear);
           // Utils.ShowToast(context,"s "+currentMonthIndex);
        } else if (from.equalsIgnoreCase("yeardialog")) {
            fromRecievedDate = getIntent().getExtras().getString("fromRecievedDate");
            if (!fromRecievedDate.isEmpty()) {
                String t[] = fromRecievedDate.split("-");
                currentYear = t[0];
                currentMonth = t[1];
                currentMonthIndex = Integer.parseInt(t[1]);
                //Utils.ShowToast(context, "" + currentMonthIndex);
                tvMonthView.setText(ENG_MONTH_NAMES[currentMonthIndex-1]);
                sp_years.setText(currentYear);
            }
           // Utils.ShowToast(context,"Y "+currentMonthIndex);
        }
        ////////////
        int yearsPrev = Integer.parseInt(Utils.getCurrentYear()) - 20;
        int yearsNext = Integer.parseInt(Utils.getCurrentYear()) + 20;

        list_years.clear();
        for (int i = yearsPrev; i < yearsNext; i++) {
            list_years.add(String.valueOf(i));
        }

        new AsyncGenerateCalender().execute();
    }


    @Override
    protected void InitResources() {
        ll_yearView = findViewById(R.id.ll_yearView);
        ll_yearView.setVisibility(View.VISIBLE);
        sp_years = findViewById(R.id.sp_years);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        rlRecycler = findViewById(R.id.rlRecycler);
        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        ivPrevious = findViewById(R.id.ivPrevious);
        ivNext = findViewById(R.id.ivNext);
        tvMonthView = findViewById(R.id.tvMonthView);

        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        iv_dropdown.setVisibility(View.GONE);
        ll_monthView = findViewById(R.id.ll_monthView);
        sp_month = findViewById(R.id.sp_month);
        sp_month.setText("Day Wise View");
        rlRecycler.setLayoutManager(new GridLayoutManager(this, 4));

        ivPrevious.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ll_monthView.setOnClickListener(this);
        ll_yearView.setOnClickListener(this);
    }

    @Override
    protected void InitPermission() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_calender;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == ll_yearView) {
            if (list_years.size() > 0) {
                PopupMenu popup = new PopupMenu(context, ll_yearView);
                for (int i = 0; i < list_years.size(); i++) {
                    popup.getMenu().add(list_years.get(i));
                }

                popup.setOnMenuItemClickListener(item -> {
                    currentYear = item.getTitle().toString();
                    sp_years.setText(currentYear);
                    new AsyncGenerateCalender().execute();
                    return true;
                });
                popup.show();
            }
        } else if (v == btn_back) {
            finish();
        } else if (v == ivPrevious) {
            listDates.clear();
            if (currentMonthIndex <= 0) {
                return;
            }
            currentMonthIndex--;
            tvMonthView.setText(ENG_MONTH_NAMES[currentMonthIndex-1]);
            generateMonthCalender(Integer.parseInt(currentYear), currentMonthIndex);
            new AsyncGenerateCalender().execute();
        } else if (v == ivNext) {
            listDates.clear();
            if (currentMonthIndex >= 12) {
                return;
            }
            currentMonthIndex++;
            tvMonthView.setText(ENG_MONTH_NAMES[currentMonthIndex-1]);
            generateMonthCalender(Integer.parseInt(currentYear), currentMonthIndex);
            new AsyncGenerateCalender().execute();
        }
    }

    private void generateMonthCalender(int year, int month) {
        listDates.clear();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.clear();

        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, year);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < daysInMonth; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i + 1);
            Log.e("Dates", fmt.format(cal.getTime()));
            listDates.add(fmt.format(cal.getTime()));
        }
    }

    private void inflateAdapter() {
        if (listDates.size() > 0) {
            adapter = new DayAdapter(context, listDates, this, listDayWiseDate);
            rlRecycler.setAdapter(adapter);
        }
    }

    private void inflateAdapter1() {
        if (listDayWiseDate.size() > 0) {
            adapter = new DayAdapter(context, listDates, this, listDayWiseDate);
            rlRecycler.setAdapter(adapter);
        }
    }

    @Override
    public void returneDate(String date) {
        Intent i = new Intent(context, ActivityDayWiseData.class);
        i.putExtra("date", date);
        startActivityForResult(i, RESULT_UPDATED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_UPDATED:
                new AsyncGenerateCalender().execute();
                break;
        }
    }

    private class AsyncGetSummationMonthWise extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            listDayWiseDate.clear();
            listDayWiseDate = db.getMonthWiseData(currentYear, String.valueOf((currentMonthIndex)));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //if (listDayWiseDate.size() > 0) {
            // Utils.ShowToast(context, "Hello");
            inflateAdapter1();
            //}
            progressDialog.dismiss();
        }
    }


    private class AsyncGenerateCalender extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            generateMonthCalender(Integer.parseInt(currentYear), currentMonthIndex);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            inflateAdapter();
            new AsyncGetSummationMonthWise().execute();
        }
    }


}
