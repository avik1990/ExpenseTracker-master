package com.app.exptracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.exptracker.adapter.SelectTagAdapter;
import com.app.exptracker.timewheel.TimePickerPopWin;
import com.app.exptracker.category.CategoryAdapter;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.CategoryModel;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.model.TransactionType;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.CircularTextView;
import com.app.exptracker.utility.DecimalDigitsInputFilter;
import com.app.exptracker.utility.ImagePickerActivity;
import com.app.exptracker.utility.KeyboardHeightObserver;
import com.app.exptracker.utility.KeyboardHeightProvider;
import com.app.exptracker.utility.Utils;
import com.bumptech.glide.Glide;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.jsibbold.zoomage.ZoomageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddExpenseIncome extends BaseActivity implements View.OnClickListener, SelectTagAdapter.GetTagFromAdpater, CategoryAdapter.GetMonthFromAdapter, KeyboardHeightObserver {

    private static final String TAG = "AddExpenseIncome";
    Context context;
    DatabaseHelper db;
    List<CategoryModel> listcat = new ArrayList<>();
    List<TransactionType> list_transtype = new ArrayList<>();
    List<ExcelDataModel> list_users = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    RecyclerView cat_recyclerview;
    Calendar myCalendar = Calendar.getInstance();
    String cat_id = "", cat_name = "";
    String[] users;
    String[] usersID;
    LinearLayout ll_monthView;
    TextView sp_month;
    String transaction_type = "";
    ImageView btn_back, btn_menu;
    private KeyboardHeightProvider keyboardHeightProvider;

    /////calculator view
    EditText et_memo;
    TextView et_sum;
    TextView tv_7;
    TextView tv_8;
    TextView tv_9;
    TextView tv_date;
    TextView tv_4;
    TextView tv_5;
    TextView tv_6;
    TextView tv_plus;
    TextView tv_1;
    TextView tv_2;
    TextView tv_3;
    TextView tv_minus;
    TextView tv_decimal;
    TextView tv_0;
    ImageView tv_clr;
    ImageView iv_equals;
    LinearLayout ll_submit;
    String current_date = "";
    CircleImageView iv_calc_cat;
    CircularTextView cv_text;
    //////////////////////////////
    String userID = "";
    List<ExcelDataModel> list_from_transtype = new ArrayList<>();
    DisplayMetrics displaymetrics;

    String trans_id = "";
    String from = "";
    int mOriginalScreenHeight = 0;
    View keyboard;
    float value;
    RelativeLayout mainrl_keyboard;
    LinearLayout ll_keyboard;
    float KeyBoardHeight = 0;
    LinearLayout ll_memo;
    ImageView iv_timer;
    String aTime;
    private int hr;
    private int min;
    static final int TIME_DIALOG_ID = 1111;
    Calendar c;
    Date date = null;
    SimpleDateFormat displayFormat;
    Bitmap bitmap;
    ImageView img_camera;
    public static final int REQUEST_IMAGE = 100;
    String fileName = "", qualityType = "High";
    ProgressDialog pImageDialog;
    SingleDateAndTimePickerDialog.Builder singleBuilder;
    SimpleDateFormat simpleTimeFormat;
    String timeSet = "";
    Set<String> listFiles = new HashSet<>();
    Splitter splitter;
    Joiner joiner;
    SelectTagAdapter selectTagAdapter;
    RecyclerView recy_tags;
    SelectTagAdapter.GetTagFromAdpater tagAdapter;
    String tagId = "";
    List<ExcelDataModel> listTags = new ArrayList<>();
    List<ExcelDataModel> listTids = new ArrayList<>();

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        pImageDialog = new ProgressDialog(context);
        pImageDialog.setMessage("Processing Image...");
        pImageDialog.setCancelable(false);
        pImageDialog.setCanceledOnTouchOutside(false);
        tagAdapter = this;
        ll_monthView.setOnClickListener(this);
        keyboardHeightProvider = new KeyboardHeightProvider(this);
        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        value = getResources().getDisplayMetrics().density;
        splitter = Splitter.on(',').omitEmptyStrings().trimResults();
        joiner = Joiner.on(',').skipNulls();

        View view = findViewById(R.id.activitylayout);
        view.post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });

        new AsyncFetchTags().execute();
        c = Calendar.getInstance();
        hr = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        if (hr > 12) {
            hr = hr - 12;
        }

        int d = c.get(Calendar.AM_PM);
        Log.d("dddd", "" + d);
        if (d == Calendar.AM) {
            timeSet = "AM";
        } else {
            timeSet = "PM";
        }
        //Log.e("AM_PM", timeSet);

        updateTime(hr, min);
        this.simpleTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        if (from.equalsIgnoreCase("Dashboard")) {

            new AsyncFetchTeansType().execute();
            listcat.clear();

            tv_minus.setText("MySelf");
            userID = "1";
        } else if (from.equalsIgnoreCase("ExpenseDetails")) {
            trans_id = getIntent().getStringExtra("trans_id");
            new AsyncFetchTeansTypeEdit().execute();
        }

        calculator();
    }

    private void setCalculatorData() {
        et_memo.setText(list_from_transtype.get(0).getMemo());
        et_sum.setText(String.valueOf(list_from_transtype.get(0).getAmount_()));
        tv_date.setText(list_from_transtype.get(0).getDate());
        tv_minus.setText(list_from_transtype.get(0).getUserName());
        userID = list_from_transtype.get(0).getUserId();
        //aTime = list_from_transtype.get(0).getTime();

        try {
            if (list_from_transtype.get(0).getPayment_mode().isEmpty()) {
                tv_plus.setText("Cash");
            } else {
                tv_plus.setText(list_from_transtype.get(0).getPayment_mode());
            }
        } catch (Exception e) {
        }
    }

    private void loadCategories(String trans_type) {
        new AsyncFetchCategories(trans_type).execute();
    }

    private class AsyncFetchTeansType extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_transtype.clear();
            list_transtype = db.GetAllTransactionType();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_transtype.size() > 0) {
                sp_month.setText(list_transtype.get(0).getTrans_type());
                transaction_type = list_transtype.get(0).getTrans_type();
                loadCategories(list_transtype.get(0).getTrans_type());
            }
        }
    }

    private class AsyncFetchTeansTypeEdit extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            listFiles.clear();
            list_from_transtype.clear();
            list_from_transtype = db.GetTransactionById(trans_id);
            listcat.clear();
            list_transtype.clear();
            list_transtype = db.GetAllTransactionType();
            cat_id = list_from_transtype.get(0).getCat_id();
            current_date = list_from_transtype.get(0).getDate();
            listTids = db.GetTagsByTransId(trans_id);

            if (listTids.size() > 0) {
                if (listTids.size() > 0) {
                    StringBuilder text = new StringBuilder();
                    for (int i = 0; i < listTids.size(); i++) {
                        text.append(listTids.get(i).getTagId());
                        text.append(",");
                    }
                    tagId = cleanUpCommas(text.toString());
                }

            }

            if (!list_from_transtype.get(0).getFileName().isEmpty()) {
                String list_transtype_img[] = list_from_transtype.get(0).getFileName().split(",");
                for (int i = 0; i < list_transtype_img.length; i++) {
                    listFiles.add(list_transtype_img[i]);
                }
            }

            try {
                if (!list_from_transtype.get(0).getTime().isEmpty()) {
                    aTime = Utils.getFormattedTime(list_from_transtype.get(0).getTime());
                } else {
                    aTime = list_from_transtype.get(0).getTime();
                    hr = c.get(Calendar.HOUR_OF_DAY);
                    min = c.get(Calendar.MINUTE);
                    int f = c.get(Calendar.AM_PM);
                    if (f == Calendar.AM) {
                        timeSet = "AM";
                    } else {
                        timeSet = "PM";
                    }
                    updateTime(hr, min);
                }
            } catch (Exception e) {
                aTime = list_from_transtype.get(0).getTime();
                hr = c.get(Calendar.HOUR_OF_DAY);
                min = c.get(Calendar.MINUTE);
                int f = c.get(Calendar.AM_PM);
                if (f == Calendar.AM) {
                    timeSet = "AM";
                } else {
                    timeSet = "PM";
                }
                updateTime(hr, min);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_transtype.size() > 0) {
                sp_month.setText(list_from_transtype.get(0).getIncome_Expenses());
                transaction_type = list_from_transtype.get(0).getIncome_Expenses();
                loadCategories(list_from_transtype.get(0).getIncome_Expenses());
            }
            setCalculatorData();
        }
    }


    private class AsyncFetchCategories extends AsyncTask<Void, Void, Void> {

        String trans_type = "";

        public AsyncFetchCategories(String trans_type) {
            this.trans_type = trans_type;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            listcat.clear();
            listcat = db.GetAllCatgories(trans_type);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            inflateCategoryAdapter();
        }
    }

    private void inflateCategoryAdapter() {
        if (listcat.size() > 0) {
            categoryAdapter = new CategoryAdapter(context, listcat, this, cat_id);
            cat_recyclerview.setAdapter(categoryAdapter);
        }
    }


    @Override
    protected void InitResources() {
        ImagePickerActivity.INTENT_SHOW_SHADE = 0;
        img_camera = findViewById(R.id.img_camera);
        img_camera.setVisibility(View.VISIBLE);
        iv_timer = findViewById(R.id.iv_timer);
        iv_timer.setVisibility(View.VISIBLE);
        ll_memo = findViewById(R.id.ll_memo);
        mainrl_keyboard = findViewById(R.id.rl_keyboard);
        ll_keyboard = findViewById(R.id.ll_keyboard);
        keyboard = findViewById(R.id.keyboard);
        from = getIntent().getStringExtra("from");
        cv_text = findViewById(R.id.cv_text);
        cv_text.setSolidColor("#FED852");
        current_date = Utils.getCurrentDate();
        cat_recyclerview = findViewById(R.id.cat_recyclerview);
        cat_recyclerview.setLayoutManager(new GridLayoutManager(context, 4));

        recy_tags = findViewById(R.id.recy_tags);
        recy_tags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        iv_calc_cat = findViewById(R.id.iv_calc_cat);
        ll_monthView = findViewById(R.id.ll_monthView);
        sp_month = findViewById(R.id.sp_month);

        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        //calculator
        et_memo = findViewById(R.id.et_memo);
        et_sum = findViewById(R.id.et_sum);
        ll_submit = findViewById(R.id.ll_submit);
        tv_7 = findViewById(R.id.tv_7);
        tv_8 = findViewById(R.id.tv_8);
        tv_9 = findViewById(R.id.tv_9);
        tv_date = findViewById(R.id.tv_date);
        tv_4 = findViewById(R.id.tv_4);
        tv_5 = findViewById(R.id.tv_5);
        tv_6 = findViewById(R.id.tv_6);
        tv_plus = findViewById(R.id.tv_plus);
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_3 = findViewById(R.id.tv_3);
        tv_0 = findViewById(R.id.tv_0);

        tv_minus = findViewById(R.id.tv_minus);

        tv_minus.setEnabled(true);
        tv_decimal = findViewById(R.id.tv_decimal);
        tv_clr = findViewById(R.id.tv_clr);
        iv_equals = findViewById(R.id.iv_equals);

        //storeScreenHeightForKeyboardHeightCalculations();
        img_camera.setOnClickListener(this);
        img_camera.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                Utils.ShowToast(context, "Capture Image");
                return true;
            }
        });

        iv_timer.setOnClickListener(this);
    }


    @Override
    protected void InitPermission() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_addexpense;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == ll_monthView) {
            if (list_transtype.size() > 0) {
                PopupMenu popup = new PopupMenu(context, ll_monthView);
                for (int i = 0; i < list_transtype.size(); i++) {
                    popup.getMenu().add(list_transtype.get(i).getTrans_type());
                }

                popup.setOnMenuItemClickListener(item -> {
                    cat_id = "";
                    transaction_type = item.getTitle().toString();
                    loadCategories(transaction_type);
                    sp_month.setText(transaction_type);
                    return true;
                });
                popup.show();
            }
        } else if (v == btn_back) {
            finish();
        } else if (v == iv_timer) {
            hideKeyboard(AddExpenseIncome.this);
            if (!Utils.getIsCaptureTimeON(context)) {
                Utils.setIsCaptureTime(context, true);
            }

            if (from.equalsIgnoreCase("ExpenseDetails")) {
                try {
                    if (!list_from_transtype.get(0).getTime().isEmpty()) {
                        String a[] = list_from_transtype.get(0).getTime().split(" ");
                        String timeSS = a[1];
                        String time = timeSS.substring(0, timeSS.length() - 3);
                        String b[] = time.split(":");
                        hr = Integer.parseInt(b[0]);
                        min = Integer.parseInt(b[1]);
                        aTime = hr + ":" + min + ":00";
                    }
                } catch (Exception e) {
                    aTime = list_from_transtype.get(0).getTime();
                    hr = c.get(Calendar.HOUR_OF_DAY);
                    min = c.get(Calendar.MINUTE);
                    updateTime(hr, min);
                }
                //simpleTimeClicked(aTime);
                openTimePickerDialog(aTime);
            } else {
                openTimePickerDialog(aTime);
                //simpleTimeClicked(aTime);
            }
        } else if (v == img_camera) {
            Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                showImagePickerOptions();
                            }

                            if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void returnedMonth(String cat_id, String sortOrder) {
        this.cat_id = cat_id;
    }

    @Override
    public void returnedCategoryName(String catname) {
        this.cat_name = catname;
    }

    @Override
    public void returnPosition(int pos) {
        try {
            if (listcat.get(pos).getCat_iconname() != null && !listcat.get(pos).getCat_iconname().isEmpty()) {
                cv_text.setVisibility(View.GONE);
                iv_calc_cat.setVisibility(View.VISIBLE);
                int imageid = getResources().getIdentifier(listcat.get(pos).getCat_iconname(), "drawable", getPackageName());
                iv_calc_cat.setImageResource(imageid);
            } else {
                cv_text.setVisibility(View.VISIBLE);
                iv_calc_cat.setVisibility(View.GONE);
                cv_text.setText(listcat.get(pos).getCat_name().substring(0, 2).toUpperCase());
            }
        } catch (Exception e) {
            cv_text.setVisibility(View.VISIBLE);
            iv_calc_cat.setVisibility(View.GONE);
            cv_text.setText(listcat.get(pos).getCat_name().substring(0, 2).toUpperCase());
        }
    }

    public void calculator() {
        tv_date.setText(current_date);
        et_sum = findViewById(R.id.et_sum);
        et_sum.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        tv_7.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }
                et_sum.append("7");
            }
        });
        tv_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("8");
            }
        });
        tv_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("9");
            }
        });

        tv_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String st_date = tv_date.getText().toString().trim();
                DatePickerDialog dpd = new DatePickerDialog(context, fromdate,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                try {
                    String a[] = st_date.split("-");
                    try {
                        dpd.getDatePicker().updateDate(Integer.parseInt(a[0]), (Integer.parseInt(a[1]) - 1), Integer.parseInt(a[2]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd.show();
            }
        });

        tv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("4");
            }
        });

        tv_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("5");
            }
        });
        tv_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("6");
            }
        });
        tv_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               /* if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                if (et_sum.getText().toString().equalsIgnoreCase("-")) {
                    et_sum.setText("+");
                }

                if (et_sum.getText().length() > 0) {
                    Character value = et_sum.getText().toString().charAt(et_sum.getText().toString().length() - 1);

                    if (value.toString().equalsIgnoreCase("+")) {
                        return;
                    }
                    if (value.toString().equalsIgnoreCase("-")) {
                        return;
                    }

                    try {
                        if (et_sum.getText().toString().contains("+")) {
                            String val[] = et_sum.getText().toString().split("\\+");
                            String a = val[0];
                            String b = val[1];
                            Log.d("TWOVal", a + " " + b);
                            double sum = Double.parseDouble(a) + Double.parseDouble(b);
                            et_sum.setText(new DecimalFormat("##.##").format(sum));
                        } else if (et_sum.getText().toString().contains("-")) {
                            String val[] = et_sum.getText().toString().split("\\-");
                            String a = val[0];
                            String b = val[1];
                            Log.d("TWOVal", a + " " + b);
                            double sum = Double.parseDouble(a) - Double.parseDouble(b);
                            et_sum.setText(new DecimalFormat("##.##").format(sum));
                        }
                    } catch (Exception e) {
                    }
                    et_sum.append("+");
                }*/
                String[] payment_mode = {"Cash", "Credit Card", "Cheque/Debit Card"};
                PopupMenu popup = new PopupMenu(context, tv_plus);
                for (int i = 0; i < payment_mode.length; i++) {
                    popup.getMenu().add(payment_mode[i]);
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        tv_plus.setText(item.getTitle().toString());
                        return true;
                    }
                });
                popup.show();
            }
        });

        tv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list_users.size() > 0) {
                    users = new String[list_users.size()];
                    usersID = new String[list_users.size()];

                    for (int i = 0; i < list_users.size(); i++) {
                        users[i] = list_users.get(i).getUserName();
                        usersID[i] = list_users.get(i).getUserId();
                    }

                    PopupMenu popup = new PopupMenu(context, tv_minus);
                    for (int i = 0; i < users.length; i++) {
                        popup.getMenu().add(users[i]);
                    }

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            tv_minus.setText(item.getTitle().toString());
                            userID = list_users.get(Arrays.asList(users).indexOf(item.getTitle().toString())).getUserId();
                            return true;
                        }
                    });
                    popup.show();
                }

                /*if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                if (et_sum.getText().toString().equalsIgnoreCase("+")) {
                    et_sum.setText("-");
                }

                if (et_sum.getText().toString().length() > 0) {
                    Character value = et_sum.getText().toString().charAt(et_sum.getText().toString().length() - 1);
                    if (value.toString().equalsIgnoreCase("+")) {
                        return;
                    }
                    if (value.toString().equalsIgnoreCase("-")) {
                        return;
                    }

                    try {
                        if (et_sum.getText().toString().contains("-")) {
                            String val[] = et_sum.getText().toString().split("\\-");
                            String a = val[0];
                            String b = val[1];
                            Log.d("TWOVal", a + " " + b);
                            double sum = Double.parseDouble(a) - Double.parseDouble(b);
                            et_sum.setText(new DecimalFormat("##.##").format(sum));
                        } else if (et_sum.getText().toString().contains("+")) {
                            String val[] = et_sum.getText().toString().split("\\+");
                            String a = val[0];
                            String b = val[1];
                            Log.d("TWOVal", a + " " + b);
                            double sum = Double.parseDouble(a) + Double.parseDouble(b);
                            et_sum.setText(new DecimalFormat("##.##").format(sum));
                        }

                    } catch (Exception e) {
                    }
                    et_sum.append("-");
                }*/
            }
        });
        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }
                et_sum.append("1");
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }

                et_sum.append("2");
            }
        });
        tv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_sum.getText().toString().equalsIgnoreCase("0")) {
                    et_sum.setText("");
                }
                et_sum.append("3");
            }
        });

        tv_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_sum.append("0");
            }
        });
        tv_decimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_sum.getText().toString().contains(".")) {
                    et_sum.append(".");
                }
            }
        });

        tv_clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = et_sum.getText().toString();
                if (!value.isEmpty()) {
                    //if (!value.equalsIgnoreCase("0")) {
                    value = value.substring(0, value.length() - 1);
                    if (!value.isEmpty()) {
                        et_sum.setText(value);
                    } else {
                        et_sum.setText("0");
                    }
                } else {
                    et_sum.setText("0");
                }
            }
        });

        ll_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cat_id.isEmpty()) {
                    Utils.ShowToast(context, "Select a category");
                    return;
                }
               /* if (et_memo.getText().toString().isEmpty()) {
                    Utils.ShowToast(context, "Enter Memo");
                    return;
                }*/
                if (et_sum.getText().toString().isEmpty()) {
                    Utils.ShowToast(context, "Enter Expense/Income Amount");
                    return;
                }

                try {
                    if (et_sum.getText().toString().contains("+")) {
                        String val[] = et_sum.getText().toString().split("\\+");
                        String a = val[0];
                        String b = val[1];
                        double sum = Double.parseDouble(a) + Double.parseDouble(b);
                        et_sum.setText(String.valueOf(sum));
                    } else if (et_sum.getText().toString().contains("-")) {
                        String val[] = et_sum.getText().toString().split("\\-");
                        String a = val[0];
                        String b = val[1];
                        double sum = Double.parseDouble(a) - Double.parseDouble(b);
                        et_sum.setText(String.valueOf(sum));
                    }
                } catch (Exception e) {
                }

                if (et_sum.getText().toString().trim().contains("+") || et_sum.getText().toString().trim().contains("-")) {
                    Utils.ShowToast(context, "Enter Amount is not correct");
                    return;
                }

                if (listFiles.size() > 0) {
                    String s = listFiles.toString();
                    fileName = cleanUpCommas(s.replace("[", "").replaceAll("]", ""));
                } else {
                    fileName = "";
                }

                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setDate(tv_date.getText().toString().trim());
                excelDataModel.setIncome_Expenses(transaction_type);
                excelDataModel.setCat_id(cat_id);
                excelDataModel.setPayment_mode(tv_plus.getText().toString());
                excelDataModel.setUserId(userID);
                excelDataModel.setFileName(fileName);


                if (Utils.getIsCaptureTime(context)) {
                    try {
                        if (!aTime.isEmpty()) {
                            String t[] = aTime.split(":");
                            String h = t[0];
                            if (Integer.parseInt(h) < 10) {
                                h = "0" + h;
                                aTime = h + ":" + t[1];
                            }
                        }

                        SimpleDateFormat h_mm_a = new SimpleDateFormat("hh:mm a");
                        SimpleDateFormat hh_mm_ss = new SimpleDateFormat("HH:mm:ss");
                        Date d1 = h_mm_a.parse(aTime);
                        excelDataModel.setTime(tv_date.getText().toString().trim() + " " + hh_mm_ss.format(d1));
                    } catch (Exception e) {
                        excelDataModel.setTime(tv_date.getText().toString().trim() + " " + aTime);
                    }
                } else {
                    excelDataModel.setTime("");
                }


                excelDataModel.setCaptureDate(Utils.getCurrentDateTime());

                if (!et_memo.getText().toString().trim().isEmpty()) {
                    excelDataModel.setMemo(et_memo.getText().toString().trim());
                } else {
                    excelDataModel.setMemo(cat_name);
                }

                if (!et_sum.getText().toString().trim().equalsIgnoreCase("0")) {
                    excelDataModel.setAmount(trimLeadingZeros(et_sum.getText().toString().trim()));
                } else {
                    excelDataModel.setAmount(et_sum.getText().toString().trim());
                }

                if (!current_date.isEmpty()) {
                    String d[] = current_date.split("-");
                    String mon = d[1];
                    excelDataModel.setMonth(mon);
                }

                if (from.equalsIgnoreCase("Dashboard")) {
                    Utils.setIsUpdated(context, true);
                    long transId = db.AddTransactionDataTO_TransactionTable(excelDataModel);
                    //excelDataModel.setTagId(tagId);
                    db.AddTagsInTempWithId(transId, tagId);
                } else if (from.equalsIgnoreCase("ExpenseDetails")) {
                    Utils.setIsUpdated(context, true);
                    db.UpdateTransactionDataTO_TransactionTable(excelDataModel, trans_id);
                    db.AddTagsInTempWithId(Long.parseLong(trans_id), tagId);
                }
                if (!Utils.getIsCaptureTimeON(context)) {
                    Utils.setIsCaptureTime(context, false);
                }
                finish();
            }
        });
    }


    public static String trimLeadingZeros(String source) {
        for (int i = 0; i < source.length(); ++i) {
            char c = source.charAt(i);
            if (c != '0') {
                return source.substring(i);
            }
        }
        return "";
    }


    DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String years;
            String month1 = "";
            String dayofMonth = "";
            years = "" + year;
            int month = monthOfYear;
            month = month + 1;
            if (month < 10) {
                month1 = "0" + month;
            } else {
                month1 = String.valueOf(month);
            }
            //String startDate = dayOfMonth + "/" + month1 + "/" + years;
            if (dayOfMonth < 10) {
                dayofMonth = "0" + dayOfMonth;
            } else {
                dayofMonth = String.valueOf(dayOfMonth);
            }

            String startDate = years + "-" + month1 + "-" + dayofMonth;
            tv_date.setText(startDate);
        }
    };

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        KeyBoardHeight = height;
        if (height > 0) {
            View view = findViewById(R.id.keyboard);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            params.height = height;
            view.setLayoutParams(params);
            setheightDynamically("1");
        } else {
            setheightDynamically("0");
        }
    }


    private void setheightDynamically(String flag) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams params;
        if (flag.equalsIgnoreCase("1")) {
            params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    height
            );
            params.addRule(RelativeLayout.ABOVE, R.id.keyboard);
            ll_memo.setLayoutParams(params);
        } else {
            params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    height
            );
            params.addRule(RelativeLayout.ABOVE, R.id.rl_keyboard);
            ll_memo.setLayoutParams(params);
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }

    private void updateTime(int hours, int mins) {
        /*String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";*/
        String minutes = "";

        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString().toUpperCase();
        //Toast.makeText(AddExpenseIncome.this, aTime, Toast.LENGTH_SHORT).show();
        Log.e("TIMEFORMATTED", aTime);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    fileName = Utils.getCurrentDateTime() + ".jpg";
                    qualityType = "High";
                    new AsyncSaveAndShowImage().execute();
                } catch (IOException e) {
                    bitmap = null;
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void returnTags(String tagId, String tagName) {
        // this.tagId = tagId;
        // Utils.ShowToast(context, tagId);
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
            list_users.clear();
            listTags.clear();
            list_users = db.getAllUsers();
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
                recy_tags.setVisibility(View.VISIBLE);
                selectTagAdapter = new SelectTagAdapter(context, listTags, tagAdapter, tagId);
                recy_tags.setAdapter(selectTagAdapter);
            }else{
                recy_tags.setVisibility(View.GONE);
            }
        }
    }


    private class AsyncSaveAndShowImage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            storeImage(bitmap, fileName);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pImageDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pImageDialog.dismiss();
            showImageInDialog();
        }
    }


    private void showImageInDialog() {
        final Dialog dialog = new Dialog(context, R.style.Theme_FullScreen);
        dialog.setContentView(R.layout.fullscreen_dialog_with_radios);

        try {
            File image = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + Utils.FOLDER_PATH, fileName);
            final ZoomageView iv_image = dialog.findViewById(R.id.iv_image);
            ImageView iv_cross = dialog.findViewById(R.id.iv_cross);
            Button dialog_close = dialog.findViewById(R.id.dialog_close);
            RadioGroup rgQuality = dialog.findViewById(R.id.rgQuality);
            Button dialog_save = dialog.findViewById(R.id.dialog_save);


            Glide.with(context)
                    .load(image)
                    .into(iv_image);


            dialog_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeImage(bitmap, fileName);
                    dialog.cancel();
                }
            });


            rgQuality.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                switch (checkedId) {
                    case R.id.rbHighQuality:
                        qualityType = "High";
                        break;
                    case R.id.rbMediumQuality:
                        qualityType = "Medium";
                        break;
                    case R.id.rbLowQuality:
                        qualityType = "Low";
                        break;
                }
            });


            dialog_close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            iv_cross.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {

        }

        dialog.show();
    }

    public String cleanUpCommas(String string) {
        return joiner.join(splitter.split(string));
    }

    public boolean storeImage(Bitmap imageData, String filename) {
        File sdIconStorageDir = null;

        sdIconStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Utils.FOLDER_PATH);
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }
        try {
            String filePath = sdIconStorageDir.toString() + File.separator + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath, false);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            if (qualityType.equalsIgnoreCase("High")) {
                imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            } else if (qualityType.equalsIgnoreCase("Medium")) {
                imageData.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            } else if (qualityType.equalsIgnoreCase("Low")) {
                imageData.compress(Bitmap.CompressFormat.JPEG, 10, bos);
            }
            bos.flush();
            bos.close();
            listFiles.add(filename);
        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }
        return true;
    }


    private void launchCameraIntent() {
        Intent intent = new Intent(AddExpenseIncome.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 0); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 0);
        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(AddExpenseIncome.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 0); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 0);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddExpenseIncome.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    public void openTimePickerDialog(String aTime) {
        int hr1 = 0, min1 = 0, ampm = 0;
        String stAmPm = "";
        if (from.equalsIgnoreCase("Dashboard")) {
            c = Calendar.getInstance();
            hr1 = c.get(Calendar.HOUR_OF_DAY);
            min1 = c.get(Calendar.MINUTE);
            ampm = c.get(Calendar.AM_PM);
            if (hr1 > 12) {
                hr1 = hr1 - 12;
            }
            if (ampm == 0) {
                stAmPm = "am";
            } else {
                stAmPm = "pm";
            }
        } else {
            String aTime1[] = Utils.getFormattedTime(list_from_transtype.get(0).getTime()).replace("AM", "").replace("PM", "").replace("am", "").replace("pm", "").trim().split(":");
            hr1 = Integer.parseInt(aTime1[0]);
            min1 = Integer.parseInt(aTime1[1]);

            if (Utils.getFormattedTime(list_from_transtype.get(0).getTime()).contains("AM") || Utils.getFormattedTime(list_from_transtype.get(0).getTime()).contains("am")) {
                ampm = 0;
                stAmPm = "am";
            } else {
                ampm = 1;
                stAmPm = "pm";
            }
        }

        String timer = (hr1 - 1) + ":" + min1 + ":00" + " " + stAmPm;

        TimePickerPopWin pickerPopWin = new TimePickerPopWin.Builder(AddExpenseIncome.this, new TimePickerPopWin.OnTimePickedListener() {
            @Override
            public void onTimePickCompleted(int hour, int min, int sec, String meridium, String timeDesc) {
                String date1 = hour + ":" + min + " " + meridium;
                String d[] = date1.split(" ");
                String time[] = d[0].split(":");
                hr = Integer.parseInt(time[0]);
                min = Integer.parseInt(time[1]);
                timeSet = d[1];
                updateTime(hr, min);

            }
        }).textConfirm("CONFIRM") //text of confirm button
                .textCancel("CANCEL") //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .timeChose(timer)
                .colorCancel(Color.parseColor("#D81B60")) //color of cancel button
                .colorConfirm(Color.parseColor("#008577"))//color of confirm button
                .build();

        pickerPopWin.showPopWin(this);
    }

}
