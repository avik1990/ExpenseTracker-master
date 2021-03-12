package com.app.exptracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.exptracker.adapter.SearchTransExpAdapter;
import com.app.exptracker.adapter.SelectTagAdapter;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.CheckForSDCard;
import com.app.exptracker.utility.Utils;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivitySearch extends BaseActivity implements View.OnClickListener, SelectTagAdapter.GetTagFromAdpater,
        SearchTransExpAdapter.ChildClicked {

    private static final String TAG = "ActivitySearch";
    Context context;
    DatabaseHelper db;
    String tagId = "";
    LinearLayout ll_monthView;
    TextView sp_month;
    ImageView btn_back, btn_menu;
    SelectTagAdapter.GetTagFromAdpater tagAdapter;
    EditText tv_search_text;
    Spinner sp_category, sp_transtype;
    Button btn_fromdata, btn_todata, btnSearch;
    List<ExcelDataModel> list_categoryData = new ArrayList<>();
    List<ExcelDataModel> list_users = new ArrayList<>();
    Calendar myCalendar = Calendar.getInstance();
    String startDate = "", endDate = "";
    String search_text = "", st_sp_cat = "", st_sp_transtype = "All", st_sp_cat_id = "", st_sp_user = "", st_sp_user_id = "";
    String from_date = "", to_date = "";
    List<ExcelDataModel> list_search_data = new ArrayList<>();
    RecyclerView cat_recyclerview;
    ImageView iv_dropdown;
    TextView tv_search;
    ProgressDialog progressDialog;
    ExpandableListView expandableListView;
    SearchTransExpAdapter expandableListAdapter;
    ImageView img_search;
    private static final int RESULT_UPDATED = 9999;
    double st_income = 0, st_expense = 0;
    int totalCounts = 0, count_ = 0;
    TextView tv_expenses, tv_income;
    Spinner sp_users;
    RecyclerView recy_tags;
    List<ExcelDataModel> listTags = new ArrayList<>();
    SelectTagAdapter selectTagAdapter;
    Splitter splitter;
    Joiner joiner;
    ImageView img_export;
    File apkStorage1 = null;
    File outputFile = null;
    List<ExcelDataModel> export_excel = new ArrayList<>();
    private BaseFont bfBold;
    FloatingActionButton btn_export;
    AlertDialog.Builder builder;

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        builder = new AlertDialog.Builder(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Looking...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        expandableListView = findViewById(R.id.expandableListView);
        splitter = Splitter.on(',').omitEmptyStrings().trimResults();
        joiner = Joiner.on(',').skipNulls();
        tagAdapter = this;
        list_categoryData.clear();
        list_users.clear();


        if (new CheckForSDCard().isSDCardPresent()) {
            apkStorage1 = new File(Environment.getExternalStorageDirectory() + "/" + Utils.SHARED_FOLDER_PATH);
        } else {
            Utils.ShowToast(context, "Oops!! There is no SD Card.");
        }

        if (!apkStorage1.exists()) {
            apkStorage1.mkdir();
        }


        list_categoryData = db.GetAllCAtegoryDataByName();
        list_users = db.GetAllUsers();

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

        if (list_users.size() > 0) {
            String user_arr[] = new String[list_users.size() + 1];
            String user_arr_ids[] = new String[list_users.size() + 1];
            user_arr[0] = "Select User";

            for (int i = 0; i < list_users.size(); i++) {
                user_arr[i + 1] = list_users.get(i).getUserName();
                user_arr_ids[i + 1] = list_users.get(i).getUserId();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, user_arr); //Specify the layout to use when the list of choices appear
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_users.setAdapter(adapter);

            sp_users.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (position != 0) {
                        st_sp_user = sp_users.getSelectedItem().toString();
                        st_sp_user_id = user_arr_ids[position];
                    } else {
                        st_sp_user = "";
                        st_sp_user_id = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
        }


        sp_transtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    st_sp_transtype = sp_transtype.getSelectedItem().toString();
                } else {
                    st_sp_transtype = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        try {
            String currentDate = Utils.getCurrentDate();
            btn_todata.setText("TO : " + currentDate);
            endDate = currentDate;

            //SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

            String st_date = btn_todata.getText().toString().replace("TO :", "").trim();
            try {
                String a[] = st_date.split("-");
                //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    //Date date = format.parse(st_fromdate);
                    //long millis = date.getTime();
                    if (Integer.parseInt(a[1]) > 4) {
                        int month = (Integer.parseInt(a[1]) - 4);
                        String month1 = "";
                        if (month < 10) {
                            month1 = "0" + month;
                        } else {
                            month1 = String.valueOf(month);
                        }
                        startDate = Utils.getCurrentYear() + "-" + month1 + "-" + "01";
                        btn_fromdata.setText("FROM : " + startDate);
                    } else {
                        DateTime date = new DateTime().minusMonths(4).toDateTime();
                        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
                        String todayAsString = fmt.print(date);
                        String todayAsString1[] = todayAsString.split("-");
                        //String strDate = dateFormat.format(date);
                        Log.e("strDate", todayAsString);
                        int month = Integer.parseInt(todayAsString1[1]);
                        String month1 = "";
                        if (month < 10) {
                            month1 = "0" + month;
                        } else {
                            month1 = String.valueOf(month);
                        }
                        startDate = (Integer.parseInt(Utils.getCurrentYear()) - 1) + "-" + month1.replaceAll("-", "").trim() + "-" + "01";
                        btn_fromdata.setText("FROM : " + startDate);
                    }


                    //dpd.getDatePicker().updateDate(Integer.parseInt(a[0]), (Integer.parseInt(a[1]) - 1), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {

            }

            /*Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MONTH, -4);
            c.add(Calendar.DAY_OF_MONTH, 1);

            Date d = c.getTime();
            String res = dateformat.format(d);
            startDate = res;

            btn_fromdata.setText("FROM : " + res);*/
        } catch (Exception e) {

        }
        new AsyncFetchTags().execute();
    }

    public static Date getDateMonthsAgo(int numOfMonthsAgo) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1 * numOfMonthsAgo);
        return c.getTime();
    }

    @Override
    protected void InitResources() {
        btn_export = findViewById(R.id.fab_export);
        recy_tags = findViewById(R.id.recy_tags);
        recy_tags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        img_export = findViewById(R.id.img_export);
        img_export.setVisibility(View.GONE);
        tv_expenses = findViewById(R.id.tv_expenses);
        tv_income = findViewById(R.id.tv_income);
        sp_users = findViewById(R.id.sp_users);
        img_search = findViewById(R.id.img_search);
        img_search.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_black_24dp));
        cat_recyclerview = findViewById(R.id.cat_recyclerview);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        iv_dropdown.setVisibility(View.GONE);
        tv_search = findViewById(R.id.tv_search);
        cat_recyclerview.setLayoutManager(new LinearLayoutManager(context));
        ll_monthView = findViewById(R.id.ll_monthView);
        sp_month = findViewById(R.id.sp_month);
        btn_export.setOnClickListener(this);
        sp_month.setText("Search");
        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        img_search.setVisibility(View.VISIBLE);
        img_search.setOnClickListener(this);
        tv_search_text = findViewById(R.id.tv_search_text);
        tv_search_text.clearFocus();
        sp_category = findViewById(R.id.sp_category);
        sp_transtype = findViewById(R.id.sp_transtype);
        btn_fromdata = findViewById(R.id.btn_fromdata);
        btn_todata = findViewById(R.id.btn_todata);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setVisibility(View.GONE);
        btnSearch.setOnClickListener(this);
        btn_fromdata.setOnClickListener(this);
        btn_todata.setOnClickListener(this);
        img_export.setOnClickListener(this);
       /* tv_search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if  ((actionId == EditorInfo.IME_ACTION_DONE)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    search_text = tv_search_text.getText().toString().trim();
                    Log.d("CatData", search_text + " " + st_sp_cat + " " + st_sp_transtype + " " + startDate + " " + endDate);
                    list_search_data.clear();
                    tv_search_text.clearFocus();
                    new AsyncTaskSearch().execute();
                    return true;
                }
                return false;
            }
        });*/
    }

    @Override
    public void returnTags(String tagId, String tags) {

    }

    @Override
    public void returnTags(List<ExcelDataModel> excelDataModel) {
        StringBuilder text = new StringBuilder();
        for (ExcelDataModel model : excelDataModel) {
            if (model.isSelected()) {
                text.append(model.getTagId());
                text.append(",");
            }
        }
        tagId = cleanUpCommas(text.toString());
        Log.d("TAG", "Output : " + cleanUpCommas(text.toString()));
    }

    @Override
    public void returnTagsPosition(int position) {

    }

    private class AsyncFetchTags extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            listTags = db.getALlTags();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listTags.size() > 0) {
                selectTagAdapter = new SelectTagAdapter(context, listTags, tagAdapter, tagId);
                recy_tags.setAdapter(selectTagAdapter);
            }
        }
    }

    public String cleanUpCommas(String string) {
        return joiner.join(splitter.split(string));
    }


    @Override
    protected void InitPermission() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_search;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == ll_monthView) {
        } else if (v == btn_back) {
            finish();
        } else if (v == btn_todata) {
            DatePickerDialog todateDialog = new DatePickerDialog(context, todate,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            try {
                String st_fromdate = btn_fromdata.getText().toString().replace("From : ", "").trim();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = format.parse(st_fromdate);
                    long millis = date.getTime();
                    todateDialog.getDatePicker().setMinDate(millis);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            todateDialog.show();

        } else if (v == btn_fromdata) {
            DatePickerDialog dpd = new DatePickerDialog(context, fromdate,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            try {
                String st_date = btn_fromdata.getText().toString().replace("FROM :", "").trim();

                try {
                    String a[] = st_date.split("-");
                    try {
                        dpd.getDatePicker().updateDate(Integer.parseInt(a[0]), (Integer.parseInt(a[1]) - 1), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd.show();
            } catch (Exception e) {
            }

        } else if (v == img_search) {
            if (startDate.isEmpty()) {
                Utils.ShowToast(context, "Select From Date");
                return;
            }

            if (endDate.isEmpty()) {
                Utils.ShowToast(context, "Select To Date");
                return;
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            search_text = tv_search_text.getText().toString().trim();
            list_search_data.clear();
            tv_search_text.clearFocus();

            new AsyncTaskSearch().execute();
        } /*else if (v == img_export) {
            OpenExportDialogExcel();
        }*/ else if (v == btn_export) {
            if (list_search_data.size() > 0) {
                //builder.setMessage("Export Data as Excel/PDF").setTitle("Export");
                //Setting message manually and performing action on button click
                builder.setMessage("Choose the format below to export the Data")
                        .setCancelable(true)
                        .setPositiveButton("Excel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                OpenExportDialogExcel();
                            }
                        })
                        .setNegativeButton("PDF", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                OpenExportDialogPDF();
                            }
                        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Export Data...");
                alert.show();
            } else {
                Utils.ShowToast(context, "No Data to Export");
            }
        }
    }


    private void OpenExportDialogExcel() {
        final Dialog dialogExport = new Dialog(context);
        dialogExport.setContentView(R.layout.custom_export_dialog_search);
        dialogExport.setTitle("");

        TextView tv_open = dialogExport.findViewById(R.id.tv_open);
        TextView tv_cancel = dialogExport.findViewById(R.id.tv_cancel);
        TextView tv_share = dialogExport.findViewById(R.id.tv_share);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogExport.dismiss();
            }
        });

        tv_share.setOnClickListener(v -> {
            new AsyncTaskExportAfterSearch("1").execute();
            dialogExport.dismiss();
        });

        tv_open.setOnClickListener(view -> {
            new AsyncTaskExportAfterSearch("2").execute();
            dialogExport.dismiss();
        });

        dialogExport.show();
    }

    private void OpenExportDialogPDF() {
        final Dialog dialogExport = new Dialog(context);
        dialogExport.setContentView(R.layout.custom_export_dialog_search);
        dialogExport.setTitle("");

        TextView tv_open = dialogExport.findViewById(R.id.tv_open);
        TextView tv_cancel = dialogExport.findViewById(R.id.tv_cancel);
        TextView tv_share = dialogExport.findViewById(R.id.tv_share);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogExport.dismiss();
            }
        });

        tv_share.setOnClickListener(v -> {
            new AsyncTaskGeneratePdf("1").execute();
            dialogExport.dismiss();
        });

        tv_open.setOnClickListener(view -> {
            new AsyncTaskGeneratePdf("2").execute();
            dialogExport.dismiss();
        });

        dialogExport.show();
    }


    private class AsyncTaskExportData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            export_excel.clear();
            export_excel = db.GetAllFinalTransactionDataSearch(search_text, st_sp_cat.replace("All", ""), st_sp_transtype.replace("All", ""), startDate, endDate, st_sp_cat_id, st_sp_user_id, tagId);
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
            //Collections.sort(export_excel, new ExcelDataModelDateCompDesc());
            //1 for share
            Utils.ExportToExcelSheet(export_excel, startDate, endDate, apkStorage1, outputFile, context, "1");
            progressDialog.dismiss();
        }
    }


    private class AsyncTaskOpenFIle extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            export_excel.clear();
            export_excel = db.GetAllFinalTransactionDataSearch(search_text, st_sp_cat.replace("All", ""), st_sp_transtype.replace("All", ""), startDate, endDate, st_sp_cat_id, st_sp_user_id, tagId);
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
            //Collections.sort(export_excel, new ExcelDataModelDateCompDesc());
            //2 for open
            Utils.ExportToExcelSheet(export_excel, startDate, endDate, apkStorage1, outputFile, context, "2");
            progressDialog.dismiss();
        }
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

    @Override
    public void childClicked(int id) {
        Intent i = new Intent(context, ExpenseDetails.class);
        i.putExtra("trans_id", String.valueOf(id));
        startActivityForResult(i, RESULT_UPDATED);
    }


    private class AsyncTaskSearch extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_search_data.clear();
            list_search_data = db.getSearchDataFormatted(search_text, st_sp_cat.replace("All", ""), st_sp_transtype.replace("All", ""), startDate, endDate, st_sp_cat_id, st_sp_user_id, tagId);
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            tv_search_text.clearFocus();
            if (st_sp_transtype.equalsIgnoreCase("Expenses")) {
                tv_income.setText("Income: " + String.valueOf(0.0));
            } else if (st_sp_transtype.equalsIgnoreCase("Income")) {
                tv_expenses.setText("Expenses: " + String.valueOf(0.0));
            }

            if (list_search_data.size() > 0) {

                setAdapter();

                if (st_sp_transtype.isEmpty()) {
                    new AsyncTaskSummationExpenses().execute();
                } else if (st_sp_transtype.equalsIgnoreCase("Income")) {
                    new AsyncTaskSummationOnlyIncome().execute();
                } else if (st_sp_transtype.equalsIgnoreCase("Expenses")) {
                    new AsyncTaskSummationOnlyExpenses().execute();
                }
            } else {
                list_search_data.clear();
                progressDialog.dismiss();
                tv_search.setVisibility(View.GONE);
                expandableListView.setVisibility(View.GONE);
                cat_recyclerview.setVisibility(View.GONE);
                tv_expenses.setText("Expenses: " + String.valueOf(0.0));
                tv_income.setText("Income: " + String.valueOf(0.0));
                Utils.ShowToast(context, "No data available");
            }
        }
    }

    private class AsyncTaskExportAfterSearch extends AsyncTask<Void, Void, Void> {
        String flag = "";

        public AsyncTaskExportAfterSearch(String s) {
            this.flag = s;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            list_search_data.clear();
            list_search_data = db.getSearchDataFormatted(search_text, st_sp_cat.replace("All", ""), st_sp_transtype.replace("All", ""), startDate, endDate, st_sp_cat_id, st_sp_user_id, tagId);
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            tv_search_text.clearFocus();
            if (st_sp_transtype.equalsIgnoreCase("Expenses")) {
                tv_income.setText("Income: " + String.valueOf(0.0));
            } else if (st_sp_transtype.equalsIgnoreCase("Income")) {
                tv_expenses.setText("Expenses: " + String.valueOf(0.0));
            }

            if (list_search_data.size() > 0) {
                setAdapter();
                if (st_sp_transtype.isEmpty()) {
                    new AsyncTaskSummationExpenses().execute();
                } else if (st_sp_transtype.equalsIgnoreCase("Income")) {
                    new AsyncTaskSummationOnlyIncome().execute();
                } else if (st_sp_transtype.equalsIgnoreCase("Expenses")) {
                    new AsyncTaskSummationOnlyExpenses().execute();
                }
                if (flag.equalsIgnoreCase("1")) {
                    new AsyncTaskExportData().execute();
                } else if (flag.equalsIgnoreCase("2")) {
                    new AsyncTaskOpenFIle().execute();
                }

            } else {
                list_search_data.clear();
                progressDialog.dismiss();
                tv_search.setVisibility(View.GONE);
                expandableListView.setVisibility(View.GONE);
                cat_recyclerview.setVisibility(View.GONE);
                tv_expenses.setText("Expenses: " + String.valueOf(0.0));
                tv_income.setText("Income: " + String.valueOf(0.0));
                Utils.ShowToast(context, "No data available");
            }
        }
    }

    private void setAdapter() {
        expandableListView.setVisibility(View.VISIBLE);
        tv_search.setVisibility(View.GONE);
        cat_recyclerview.setVisibility(View.GONE);
        expandableListAdapter = new SearchTransExpAdapter(context, list_search_data, this);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setVisibility(View.VISIBLE);
        expandableListView.setOnTouchListener((view, event) -> {
            int eventaction = event.getAction();

            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
                    btn_export.hide();
                    //Toast.makeText(context, "ACTIO", Toast.LENGTH_SHORT).show();
                    break;

                case MotionEvent.ACTION_MOVE:
                    //Toast.makeText(context, "ACTIOMove", Toast.LENGTH_SHORT).show();
                    btn_export.hide();
                    break;

                case MotionEvent.ACTION_UP:
                    //Toast.makeText(context, "ACTIOUp", Toast.LENGTH_SHORT).show();
                    btn_export.show();
                    break;
            }
            return false;
        });
        progressDialog.dismiss();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_UPDATED:
                //if (resultCode == RESULT_OK) {
                new AsyncTaskUpdateList().execute();
                //}
                break;
        }
    }

    private class AsyncTaskUpdateList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_search_data.clear();
            list_search_data = db.getSearchDataFormatted(search_text, st_sp_cat.replace("All", ""), st_sp_transtype.replace("All", ""), startDate, endDate, st_sp_cat_id, st_sp_user_id, tagId);
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
            if (list_search_data.size() > 0) {

                expandableListAdapter.updateReceiptsList(list_search_data);

                if (st_sp_transtype.isEmpty()) {
                    new AsyncTaskSummationExpenses().execute();
                } else if (st_sp_transtype.equalsIgnoreCase("Income")) {
                    new AsyncTaskSummationOnlyIncome().execute();
                } else if (st_sp_transtype.equalsIgnoreCase("Expenses")) {
                    new AsyncTaskSummationOnlyExpenses().execute();
                }
            } else {
                expandableListView.setVisibility(View.GONE);
            }

            progressDialog.dismiss();
        }
    }


    private class AsyncTaskSummationExpenses extends AsyncTask<Void, Void, Void> {
        String stMemo = tv_search_text.getText().toString().trim();

        @Override
        protected Void doInBackground(Void... voids) {
            ExcelDataModel excelDataModel = new ExcelDataModel();
            excelDataModel = db.GetExpensesDataDateRange("Expenses", startDate, endDate, stMemo, st_sp_cat_id, st_sp_user_id, tagId);
            st_expense = 0;
            st_expense = excelDataModel.getAmount_();
            count_ = excelDataModel.getCount_();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (st_expense <= 0) {
                tv_expenses.setText("Expenses: " + String.valueOf(0.0));
            } else {
                tv_expenses.setText("Expenses: " + String.valueOf(new DecimalFormat("#,##,###.00").format(st_expense)));
            }

            new AsyncTaskSummationIncome().execute();
        }
    }

    private class AsyncTaskSummationIncome extends AsyncTask<Void, Void, Void> {
        String stMemo = tv_search_text.getText().toString().trim();

        @Override
        protected Void doInBackground(Void... voids) {
            /*st_income = 0;
            st_income = db.GetExpensesData("Income", current_month);*/
            ExcelDataModel excelDataModel = new ExcelDataModel();
            excelDataModel = db.GetExpensesDataDateRange("Income", startDate, endDate, stMemo, st_sp_cat_id, st_sp_user_id, tagId);
            st_income = 0;
            st_income = excelDataModel.getAmount_();
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
            if (st_income <= 0) {
                tv_income.setText("Income: " + String.valueOf(0.0));
            } else {
                tv_income.setText("Income: " + String.valueOf(new DecimalFormat("#,##,###.00").format(st_income)));
            }
        }
    }


    //transtype Expense
    private class AsyncTaskSummationOnlyExpenses extends AsyncTask<Void, Void, Void> {
        String stMemo = tv_search_text.getText().toString().trim();

        @Override
        protected Void doInBackground(Void... voids) {
            ExcelDataModel excelDataModel = new ExcelDataModel();
            excelDataModel = db.GetExpensesDataDateRange("Expenses", startDate, endDate, stMemo, st_sp_cat_id, st_sp_user_id, tagId);
            st_expense = 0;
            st_expense = excelDataModel.getAmount_();
            count_ = excelDataModel.getCount_();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (st_expense <= 0) {
                tv_expenses.setText("Expenses: " + String.valueOf(0.0));
            } else {
                tv_expenses.setText("Expenses: " + String.valueOf(new DecimalFormat("#,##,###.00").format(st_expense)));
            }
        }
    }

    //transtype Income
    private class AsyncTaskSummationOnlyIncome extends AsyncTask<Void, Void, Void> {
        String stMemo = tv_search_text.getText().toString().trim();

        @Override
        protected Void doInBackground(Void... voids) {
            /*st_income = 0;
            st_income = db.GetExpensesData("Income", current_month);*/
            ExcelDataModel excelDataModel = new ExcelDataModel();
            excelDataModel = db.GetExpensesDataDateRange(st_sp_transtype, startDate, endDate, stMemo, st_sp_cat_id, st_sp_user_id, tagId);
            st_income = 0;
            st_income = excelDataModel.getAmount_();
            count_ = excelDataModel.getCount_();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (st_income <= 0) {
                tv_income.setText("Income: " + String.valueOf(0.0));
            } else {
                tv_income.setText("Income: " + String.valueOf(new DecimalFormat("#,##,###.00").format(st_income)));
            }
        }
    }

    private void generatePDF() {
        outputFile = new File(apkStorage1, "ETracker_" + startDate + "-" + endDate + ".pdf");
        //create a new document
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            //PdfContentByte cb = docWriter.getDirectContent();
            //initialize fonts for text printing
            initializeFonts();
            //the company logo is stored in the assets which is read only
            //get the logo and print on the document
            try {
                //document.open();
                Drawable d = getResources().getDrawable(R.mipmap.ic_launcher_round);
                BitmapDrawable bitDw = ((BitmapDrawable) d);
                Bitmap bmp = bitDw.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.scaleToFit(150, 150);
                image.scaleAbsolute(70f, 70f);
                image.setAlignment(Element.ALIGN_CENTER);
                document.add(image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.add(new Paragraph("\n"));
            Font font1 = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, BaseColor.BLACK);
            Chunk createDate = new Chunk("Created Date: " + Utils.getCurrentDate(), font1);
            Chunk copyrightBy = new Chunk("Created Using ExpenseTracker ", font1);
            Paragraph p1 = new Paragraph();
            Paragraph p2 = new Paragraph();
            p1.add(createDate);
            p2.add(copyrightBy);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p2.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p1);
            document.add(p2);
            document.add(new Paragraph("\n"));
            //creating a sample invoice with some customer data
            //createHeadings(cb, 400, 780, "Date");
            //createHeadings(cb, 400, 765, "Income/Expenses");
            /*createHeadings(cb, 400, 750, "Category");
            createHeadings(cb, 400, 735, "Memo");
            createHeadings(cb, 400, 720, "Amount");
            createHeadings(cb, 400, 720, "Payment Mode");
            createHeadings(cb, 400, 720, "User Name");
            createHeadings(cb, 400, 720, "DateTime");
            createHeadings(cb, 400, 720, "File Name");
            createHeadings(cb, 400, 720, "Tags");*/

            //list all the products sold to the customer
            // float[] columnWidths = {1f, 1f, 1f};
            //create PDF table with the given widths
            // PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            // table.setTotalWidth(500f);

           /* PdfPCell cell = new PdfPCell(new Phrase("Date"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Income/Expenses"));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Category"));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Memo"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Amount"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Payment Mode"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("User Name"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("DateTime"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Tags"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);*/

            for (int j = 0; j < list_search_data.size(); j++) {
                Font red = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
                Chunk redText = new Chunk(Utils.getFormattedDate(list_search_data.get(j).getDate()) + "  Income: " + String.format("%.2f", list_search_data.get(j).getInc_amt_()) + " Expense: " + String.format("%.2f", list_search_data.get(j).getExp_amt_()), red);
                Paragraph p = new Paragraph(redText);
                p.setAlignment(Paragraph.ALIGN_CENTER);
                document.add(p);
                document.add(new Paragraph("\n"));
                for (int i = 0; i < list_search_data.get(j).getGroupFocAll().size(); i++) {
                    PdfPTable table = new PdfPTable(6);
                    Font fontCOlor = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
                    table.addCell(new Phrase(list_search_data.get(j).getGroupFocAll().get(i).getMemo(), fontCOlor));
                    table.addCell(new Phrase(list_search_data.get(j).getGroupFocAll().get(i).getCategory(), fontCOlor));
                    if (list_search_data.get(j).getGroupFocAll().get(i).getIncome_Expenses().equalsIgnoreCase("Income")) {
                        Font incColor = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GREEN);
                        /*table.addCell(new Phrase(String.valueOf(list_search_data.get(j).getGroupFocAll().get(i).getAmount_()), incColor));*/
                        table.addCell(new Phrase(String.format("%.2f", list_search_data.get(j).getGroupFocAll().get(i).getAmount_()), incColor));
                    } else {
                        Font expColor = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);
                        table.addCell(new Phrase(String.format("%.2f", list_search_data.get(j).getGroupFocAll().get(i).getAmount_()), expColor));
                    }
                    table.addCell(new Phrase(list_search_data.get(j).getGroupFocAll().get(i).getIncome_Expenses(), fontCOlor));
                    table.addCell(new Phrase(list_search_data.get(j).getGroupFocAll().get(i).getPayment_mode(), fontCOlor));
                    table.addCell(new Phrase(list_search_data.get(j).getGroupFocAll().get(i).getTagName(), fontCOlor));
                    table.setHorizontalAlignment(Element.ALIGN_CENTER);
                    document.add(table);
                }
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //PDF file is now ready to be sent to the bluetooth printer using PrintShare
       /* Intent i = new Intent(Intent.ACTION_VIEW);
        i.setPackage("com.dynamixsoftware.printershare");
        i.setDataAndType(Uri.fromFile(apkStorage1), "application/pdf");
        startActivity(i);*/

    }

    private void createHeadings(PdfContentByte cb, float x, float y, String text) {

        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.setTextMatrix(x, y);
        cb.showText(text.trim());
        cb.endText();

    }


    private void initializeFonts() {
        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class AsyncTaskGeneratePdf extends AsyncTask<Void, Void, Void> {
        String flag = "";

        public AsyncTaskGeneratePdf(String s) {
            this.flag = s;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //export_excel.clear();
            //export_excel = db.GetAllFinalTransactionDataSearch(search_text, st_sp_cat.replace("All", ""), st_sp_transtype.replace("All", ""), startDate, endDate, st_sp_cat_id, st_sp_user_id, tagId);
            generatePDF();
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
            if (flag.equalsIgnoreCase("1")) {
                try {
                    Utils.shareAll(context, outputFile);
                } catch (Exception e) {

                }
            }
            if (flag.equalsIgnoreCase("2")) {
                try {
                    Utils.openFile(context, outputFile);
                } catch (Exception e) {

                }
            }
            progressDialog.dismiss();
        }
    }

}
