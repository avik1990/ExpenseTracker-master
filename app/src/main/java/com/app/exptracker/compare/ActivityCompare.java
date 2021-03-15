package com.app.exptracker.compare;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.exptracker.R;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.model.TransactionType;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityCompare extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ActivitySearch";
    Context context;
    DatabaseHelper db;

    RecyclerView rl_view1, rl_view2;
    Button btnCompare;
    Button btn_fromdata, btn_todata;
    Button btn_fromdata2, btn_todata2;
    Spinner sp_category;
    List<ExcelDataModel> list_categoryData = new ArrayList<>();

    List<ExcelDataModel> list_categoryDataSec1 = new ArrayList<>();
    List<ExcelDataModel> list_categoryDataSec2 = new ArrayList<>();
    List<TransactionType> list_transtype = new ArrayList<>();
    String startDate = "", endDate = "";
    String startDate2 = "", endDate2 = "";

    Calendar myCalendar = Calendar.getInstance();

    CompAdapter1 compAdapter1;
    CompAdapter2 compAdapter2;

    String st_sp_cat = "", st_sp_cat_id = "";

    double total = 0;
    double total1 = 0;
    TextView tv_total, tv_total2;
    ProgressDialog progressDialog;
    TextView sp_month;
    String transaction_type = "";
    LinearLayout ll_monthView;
    ImageView btn_back, btn_menu;

    boolean isFrom1 = false;
    boolean isFrom2 = false;

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        list_categoryData.clear();
        list_categoryData = db.GetAllCAtegoryDataByName();


        list_transtype.clear();
        list_transtype = db.GetAllTransactionType();

        if (list_transtype.size() > 0) {
            sp_month.setText(list_transtype.get(0).getTrans_type());
            transaction_type = list_transtype.get(0).getTrans_type();
            //loadCategories(list_transtype.get(0).getTrans_type());
        }


        if (list_categoryData.size() > 0) {
            String category_arr[] = new String[list_categoryData.size() + 1];
            String category_arr_ids[] = new String[list_categoryData.size() + 1];
            category_arr[0] = "Select Category";
            for (int i = 0; i < list_categoryData.size(); i++) {
                category_arr[i + 1] = list_categoryData.get(i).getCategory() + " (" + list_categoryData.get(i).getIncome_Expenses().substring(0, 2) + ")";
                category_arr_ids[i + 1] = list_categoryData.get(i).getId();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category_arr);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_category.setAdapter(adapter);

            sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (position != 0) {
                        st_sp_cat = sp_category.getSelectedItem().toString();
                        st_sp_cat_id = category_arr_ids[position];
                    } else {
                        st_sp_cat = "";
                        st_sp_cat_id = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
        }
    }


    @Override
    protected void InitResources() {

        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);


        ll_monthView = findViewById(R.id.ll_monthView);
        tv_total = findViewById(R.id.tv_total);
        tv_total2 = findViewById(R.id.tv_total2);
        sp_month = findViewById(R.id.sp_month);
        rl_view1 = findViewById(R.id.rl_view1);
        rl_view2 = findViewById(R.id.rl_view2);
        btnCompare = findViewById(R.id.btnCompare);
        btn_fromdata = findViewById(R.id.btn_fromdata);
        btn_todata = findViewById(R.id.btn_todata);
        btn_fromdata2 = findViewById(R.id.btn_fromdata2);
        btn_todata2 = findViewById(R.id.btn_todata2);
        sp_category = findViewById(R.id.sp_category);

        btn_fromdata.setOnClickListener(this);
        btn_todata.setOnClickListener(this);
        btn_fromdata2.setOnClickListener(this);
        btn_todata2.setOnClickListener(this);
        btnCompare.setOnClickListener(this);
        ll_monthView.setOnClickListener(this);
    }

    @Override
    protected void InitPermission() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_compare;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == btn_fromdata) {
            DatePickerDialog dpd = new DatePickerDialog(context, fromdate,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            try {
                //String a[] = st_date.split("-");
                try {
                    // dpd.getDatePicker().updateDate(Integer.parseInt(a[0]), (Integer.parseInt(a[1]) - 1), Integer.parseInt(a[2]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dpd.show();
        } else if (v == btn_todata) {
            if (startDate.isEmpty()) {
                Utils.ShowToast(context, "Choose From Date First for Comp1");
                return;
            }
            openToDate();
        } else if (v == btn_fromdata2) {
            DatePickerDialog dpd = new DatePickerDialog(context, fromdate2,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            try {
                //String a[] = st_date.split("-");
                try {
                    // dpd.getDatePicker().updateDate(Integer.parseInt(a[0]), (Integer.parseInt(a[1]) - 1), Integer.parseInt(a[2]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dpd.show();
        } else if (v == btn_todata2) {
            if (startDate2.isEmpty()) {
                Utils.ShowToast(context, "Choose From Date First for Comp2");
                return;
            }
            openToDate1();
        } else if (v == btnCompare) {
            list_categoryDataSec1 = new ArrayList<>();
            list_categoryDataSec2 = new ArrayList<>();
            list_categoryDataSec1.clear();
            list_categoryDataSec2.clear();

            if (startDate.isEmpty() || startDate2.isEmpty() || endDate.isEmpty() || endDate2.isEmpty()) {
                Utils.ShowToast(context, "Select dates to compare");
                return;
            }

            progressDialog.show();
            new AsyncComp1().execute();
        } else if (v == ll_monthView) {
            if (list_transtype.size() > 0) {
                PopupMenu popup = new PopupMenu(context, ll_monthView);
                for (int i = 0; i < list_transtype.size(); i++) {
                    popup.getMenu().add(list_transtype.get(i).getTrans_type());
                }

                popup.setOnMenuItemClickListener(item -> {
                    transaction_type = item.getTitle().toString();
                    sp_month.setText(transaction_type);
                    return true;
                });
                popup.show();//showing popup menu
            }
        } else if (v == btn_back) {
            finish();
        }


    }

    private void openToDate1() {
        DatePickerDialog dpd = new DatePickerDialog(context, todate2,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        try {
            String st_fromdate = startDate2;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(st_fromdate);
                long millis = date.getTime();
                dpd.getDatePicker().setMinDate(millis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dpd.show();
    }

    private void openToDate() {
        DatePickerDialog dpd = new DatePickerDialog(context, todate,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        try {
            String st_fromdate = startDate;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(st_fromdate);
                long millis = date.getTime();
                dpd.getDatePicker().setMinDate(millis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dpd.show();
    }

    DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String years;
            String month1 = "";
            String dayOfMonth1 = "";
            years = "" + year;
            int month = monthOfYear;
            month = month + 1;
            if (month < 10) {
                month1 = "0" + month;
            } else {
                month1 = String.valueOf(month);
            }

            if (dayOfMonth < 10) {
                dayOfMonth1 = "0" + dayOfMonth;
            } else {
                dayOfMonth1 = String.valueOf(dayOfMonth);
            }

            startDate = years + "-" + month1 + "-" + dayOfMonth1;
            btn_fromdata.setText("From : " + startDate);

            openToDate();
        }
    };

    DatePickerDialog.OnDateSetListener todate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String years;
            String month1 = "";
            String dayOfMonth1 = "";
            years = "" + year;
            int month = monthOfYear;
            month = month + 1;
            if (month < 10) {
                month1 = "0" + month;
            } else {
                month1 = String.valueOf(month);
            }

            if (dayOfMonth < 10) {
                dayOfMonth1 = "0" + dayOfMonth;
            } else {
                dayOfMonth1 = String.valueOf(dayOfMonth);
            }


            endDate = years + "-" + month1 + "-" + dayOfMonth1;
            btn_todata.setText("To : " + endDate);
        }
    };


    DatePickerDialog.OnDateSetListener fromdate2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String years;
            String month1 = "";
            String dayOfMonth1 = "";
            years = "" + year;
            int month = monthOfYear;
            month = month + 1;
            if (month < 10) {
                month1 = "0" + month;
            } else {
                month1 = String.valueOf(month);
            }

            if (dayOfMonth < 10) {
                dayOfMonth1 = "0" + dayOfMonth;
            } else {
                dayOfMonth1 = String.valueOf(dayOfMonth);
            }

            startDate2 = years + "-" + month1 + "-" + dayOfMonth1;
            btn_fromdata2.setText("From : " + startDate2);
            openToDate1();
        }
    };

    DatePickerDialog.OnDateSetListener todate2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String years;
            String month1 = "";
            String dayOfMonth1 = "";
            years = "" + year;
            int month = monthOfYear;
            month = month + 1;
            if (month < 10) {
                month1 = "0" + month;
            } else {
                month1 = String.valueOf(month);
            }

            if (dayOfMonth < 10) {
                dayOfMonth1 = "0" + dayOfMonth;
            } else {
                dayOfMonth1 = String.valueOf(dayOfMonth);
            }

            endDate2 = years + "-" + month1 + "-" + dayOfMonth1;
            btn_todata2.setText("To : " + endDate2);
        }
    };


    private class AsyncComp1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_categoryDataSec1 = db.GetAllFinalTransactionDataComparision(startDate, endDate, st_sp_cat_id, transaction_type);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            inflateAdapter1();
            new AsyncComp2().execute();
        }
    }

    private void inflateAdapter1() {
        if (list_categoryDataSec1.size() > 0) {
            compAdapter1 = new CompAdapter1(context, list_categoryDataSec1);
            rl_view1.setAdapter(compAdapter1);
        } else {
            list_categoryDataSec1.clear();
            rl_view1.setAdapter(null);
        }
    }


    private class AsyncComp2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_categoryDataSec2 = db.GetAllFinalTransactionDataComparision(startDate2, endDate2, st_sp_cat_id, transaction_type);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            inflateAdapter2();
            new AsyncCompTotal().execute();
        }
    }

    private void inflateAdapter2() {
        if (list_categoryDataSec2.size() > 0) {
            compAdapter2 = new CompAdapter2(context, list_categoryDataSec2);
            rl_view2.setAdapter(compAdapter2);
        } else {
            list_categoryDataSec2.clear();
            rl_view2.setAdapter(null);
        }
    }


    private class AsyncCompTotal extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            total = 0.0;
            total = db.GetTotalCompCount(startDate, endDate, st_sp_cat_id, transaction_type);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (total < 1) {
                tv_total.setText(String.valueOf(0.0));
            } else {
                tv_total.setText(String.valueOf(new DecimalFormat("#,##,###.00").format(total)));
            }
            new AsyncCompTotal2().execute();
        }
    }


    private class AsyncCompTotal2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            total1 = 0.0;
            total1 = db.GetTotalCompCount(startDate2, endDate2, st_sp_cat_id, transaction_type);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (total1 < 1) {
                tv_total2.setText(String.valueOf(0.0));
            } else {
                tv_total2.setText(String.valueOf(new DecimalFormat("#,##,###.00").format(total1)));
            }
            progressDialog.dismiss();
        }
    }
}
