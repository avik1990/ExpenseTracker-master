package com.app.exptracker.dashboard;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import android.util.Base64;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.exptracker.ActivityLocalBackupfiles;
import com.app.exptracker.ActivitySearch;
import com.app.exptracker.ActivitySettings;
import com.app.exptracker.AddExpenseIncome;
import com.app.exptracker.ExpenseDetails;
import com.app.exptracker.GraphActivity;
import com.app.exptracker.R;
import com.app.exptracker.adapter.MonthAdapter;
import com.app.exptracker.adapter.MonthlyExpenseAdapter;
import com.app.exptracker.adapter.YearlyExpenseAdapter;
import com.app.exptracker.daywiseexpenses.ActivityCalender;
import com.app.exptracker.category.CategoryActivity;
import com.app.exptracker.chart.ActivityChart;
import com.app.exptracker.compare.ActivityCompare;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.model.ExcelDataModelDateCompDesc;
import com.app.exptracker.model.Months;
import com.app.exptracker.model.Sync;
import com.app.exptracker.tags.AddtagsActivity;
import com.app.exptracker.user.AddUserActivity;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.CheckForSDCard;
import com.app.exptracker.utility.FileUtils;
import com.app.exptracker.utility.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.toptas.fancyshowcase.FancyShowCaseView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class Dashboard extends BaseActivity implements TransactionAdapter.ChildClicked, MonthlyExpenseAdapter.GetYearFromAdapter,
        YearlyExpenseAdapter.GetYearFromreturnedYearView, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, MonthAdapter.GetMonthFromAdapter, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "Dashboard";
    private static final int RESULT_UPDATED = 9999;
    FloatingActionButton fab_add;
    LinearLayout ll_monthView;
    boolean doubleBackToExitPressedOnce = false;
    Context context;
    MonthAdapter monthAdapter;
    RecyclerView rl_recyclerview;
    RelativeLayout calenderview;
    DatabaseHelper db;
    List<ExcelDataModel> list_exceldata = new ArrayList<>();
    List<ExcelDataModel> list_exceldataCat = new ArrayList<>();
    List<ExcelDataModel> list_exceldataUser = new ArrayList<>();
    List<ExcelDataModel> list_categoryData = new ArrayList<>();
    List<ExcelDataModel> list_temp_categoryData_withID = new ArrayList<>();
    List<ExcelDataModel> list_final_transaction_data = new ArrayList<>();
    List<ExcelDataModel> list_final_transaction_data_resume = new ArrayList<>();
    ProgressDialog p;
    ProgressDialog pImport;
    TextView tv_expenses, tv_income;
    NavigationView navigationView;
    TransactionAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    RelativeLayout rl_calender_mainview;
    TextView sp_month, tv_currentyear;
    String current_month = "";
    String current_year = "";
    ImageView btn_menu;
    DrawerLayout drawer;
    ImageView tv_year_back;
    ImageView tv_year_forward;
    String st_year = "";
    SimpleDateFormat dateFormat;
    Calendar cal;
    // public final String downloadDirectory = "ETrackerBackup";
    File apkStorage = null;
    File apkStorage1 = null;
    File outputFile = null;
    Dialog dialogMonthView;
    Dialog dialogYearView;
    ImageView img_search;
    TextView tv_balance;
    double st_income = 0, st_expense = 0;
    double st_income_paymentMode = 0, st_expense_paymentMode = 0;
    Splitter splitter;
    Joiner joiner;
    File originalFile;
    RecyclerView rl_view;
    RecyclerView rl_Yearview;
    Calendar myCalendar = Calendar.getInstance();
    String startDate = "", endDate = "";
    MonthlyExpenseAdapter.GetYearFromAdapter getMonthlyFromAdapter;
    YearlyExpenseAdapter.GetYearFromreturnedYearView getYearlyFromAdapter;
    MonthlyExpenseAdapter monthlyExpenseAdapter;
    YearlyExpenseAdapter yearlyExpenseAdapter;
    TextView btn_fromdata;
    TextView btn_todata;
    List<ExcelDataModel> export_excel = new ArrayList<>();
    private ProgressDialog pDialogProgress;
    int totalCounts = 0, count_ = 0;
    TextView tv_inc_count, tv_exp_count, tv_currentyear_mdialog;
    ImageView iv_logo;
    LinearLayout ll_expenses, ll_income;
    Button btnYearview;
    String popupYear;
    RadioGroup rgFilter;
    String filterType = "";
    RadioButton rbAll;
    TextView tv_payment_mode_inc, tv_payment_mode_exp;
    SwipeRefreshLayout swipeToRefresh;
    CircleImageView iv_drawer_Userphoto;
    List<ExcelDataModel> list_user = new ArrayList<>();
    String flagYearViewCLicked = "1"; //// 1 when clicked from topbarview /// 2 when clicked from sidemenu
    Button btnDayView;
    ImageView ivSync;
    public int RC_GALLERY = 7;
    ////////////////// Google Sign In Variables///////////////////
    SignInButton mSignIn;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    public static final int RC_SIGN_IN = 1;
    List<String> listTags = new ArrayList<>();
    GoogleAccountCredential mCredential = null;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String[] SCOPES = {DriveScopes.DRIVE};
    private static final String PREF_ACCOUNT_NAME = "accountName";
    ////////////////////
    AsyncTaskExportDriveSync asyncTaskFetchAllData = null;
    MakeDriveRequestTask2 makeDriveRequestTask2 = null;
    com.google.api.services.drive.model.File file;
    String syncFileName = "";
    RadioButton rbCash, rbDebit, rbCheque;

    Multimap<String, String> tagsMap = ArrayListMultimap.create();
    StringBuilder val = new StringBuilder();

    @Override
    protected void InitListner() {
        context = this;
        splitter = Splitter.on(',').omitEmptyStrings().trimResults();
        joiner = Joiner.on(',').skipNulls();
        getMonthlyFromAdapter = this;
        getYearlyFromAdapter = this;
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
        cal = Calendar.getInstance();

        if (new CheckForSDCard().isSDCardPresent()) {
            apkStorage1 = new File(Environment.getExternalStorageDirectory() + "/" + Utils.SHARED_FOLDER_PATH);
            apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + Utils.BACKUP_FOLDER_PATH);
        } else {
            Utils.ShowToast(context, "Oops!! There is no SD Card.");
        }

        if (!apkStorage.exists()) {
            apkStorage.mkdir();
        }

        if (!apkStorage1.exists()) {
            apkStorage1.mkdir();
        }

        db = new DatabaseHelper(context);

        fab_add.setOnClickListener(this);
        ll_monthView.setOnClickListener(this);

        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);

        pImport = new ProgressDialog(context);
        pImport.setMessage("Please wait...");
        pImport.setIndeterminate(false);
        pImport.setCancelable(false);

        current_year = Utils.getCurrentYear();
        current_month = current_year + "-" + Utils.getCurrentMonthsIndex(Utils.getCurrentMonths());


        try {
            db.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //Read Data from Assets Folder
    private void readdatafromExcelData() {
        try {
            InputStream myInput;
            // initialize asset manager
            AssetManager assetManager = getAssets();
            //  open excel sheet
            myInput = assetManager.open("expense_data_sir.xls");
            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno = 0;
            //textView.append("\n");
            while (rowIter.hasNext()) {
                //Log.e(TAG, " row no " + rowno);
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0) {
                            date = myCell.toString();
                        } else if (colno == 1) {
                            Income_Expenses = myCell.toString();
                        } else if (colno == 2) {
                            Category = myCell.toString();
                        } else if (colno == 3) {
                            Memo = myCell.toString();
                        } else if (colno == 4) {
                            Amount = myCell.toString();
                        }
                        colno++;
                        //  Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount));
                    //Log.d("ExcelData", date + " " + Income_Expenses + " " + Category + " " + Memo + " " + Amount + "\n");
                    //textView.append(sno + " -- " + date + "  -- " + det + "\n");
                }
                rowno++;
            }
        } catch (
                Exception e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    protected void InitResources() {
        rbCash = findViewById(R.id.rbCash);
        rbDebit = findViewById(R.id.rbDebit);
        rbCheque = findViewById(R.id.rbCheque);
        ivSync = findViewById(R.id.ivSync);
        tv_payment_mode_inc = findViewById(R.id.tv_payment_mode_inc);
        tv_payment_mode_exp = findViewById(R.id.tv_payment_mode_exp);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        rbAll = findViewById(R.id.rbAll);
        rgFilter = findViewById(R.id.rgFilter);
        btnYearview = findViewById(R.id.btnYearview);
        btnDayView = findViewById(R.id.btnDayView);
        btnDayView.setVisibility(View.VISIBLE);
        btnYearview.setVisibility(View.VISIBLE);
        btnYearview.setOnClickListener(this);
        btnDayView.setOnClickListener(this);
        ll_expenses = findViewById(R.id.ll_expenses);
        ll_income = findViewById(R.id.ll_income);

        ll_expenses.setOnClickListener(this);
        ll_income.setOnClickListener(this);

        drawer = findViewById(R.id.drawer_layout);
        btn_menu = findViewById(R.id.btn_menu);
        tv_balance = findViewById(R.id.tv_balance);
        tv_inc_count = findViewById(R.id.tv_inc_count);
        tv_exp_count = findViewById(R.id.tv_exp_count);
        iv_logo = findViewById(R.id.iv_logo);
        iv_logo.setVisibility(View.VISIBLE);
        ///year
        tv_currentyear = findViewById(R.id.tv_currentyear);
        tv_year_back = findViewById(R.id.tv_year_back);
        tv_year_forward = findViewById(R.id.tv_year_forward);
        img_search = findViewById(R.id.img_search);
        img_search.setVisibility(View.VISIBLE);
        st_year = Utils.getCurrentYear();
        tv_currentyear.setText(st_year);
        tv_year_back.setOnClickListener(this);
        tv_year_forward.setOnClickListener(this);
        ////
        img_search.setOnClickListener(this);
        rl_calender_mainview = findViewById(R.id.rl_calender_mainview);
        rl_calender_mainview.setOnClickListener(this);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        iv_drawer_Userphoto = headerLayout.findViewById(R.id.iv_drawer_Userphoto);


        expandableListView = findViewById(R.id.expandableListView);
        rl_recyclerview = findViewById(R.id.rl_recyclerview);
        tv_expenses = findViewById(R.id.tv_expenses);
        tv_income = findViewById(R.id.tv_income);
        fab_add = findViewById(R.id.fab_add);
        ll_monthView = findViewById(R.id.ll_monthView);
        rl_recyclerview.setLayoutManager(new GridLayoutManager(context, 6));
        calenderview = findViewById(R.id.calenderview);
        sp_month = findViewById(R.id.sp_month);

        ll_expenses.setOnClickListener(this);
        ll_income.setOnClickListener(this);
        ivSync.setOnClickListener(this);
        ivSync.setVisibility(View.VISIBLE);

        sp_month.setText(Utils.getCurrentMonths() + " " + Utils.getCurrentYear());
        tv_currentyear.setText(Utils.getCurrentYear());
        btn_menu.setOnClickListener(this);

        swipeToRefresh.setOnTouchListener((view, event) -> {
            int eventaction = event.getAction();

            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
                    fab_add.show();
                    break;

                case MotionEvent.ACTION_MOVE:
                    fab_add.show();
                    break;

                case MotionEvent.ACTION_UP:
                    fab_add.show();
                    break;
            }
            return false;
        });


        expandableListView.setOnTouchListener((view, event) -> {
            int eventaction = event.getAction();

            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
                    fab_add.hide();
                    //Toast.makeText(context, "ACTIO", Toast.LENGTH_SHORT).show();
                    break;

                case MotionEvent.ACTION_MOVE:
                    //Toast.makeText(context, "ACTIOMove", Toast.LENGTH_SHORT).show();
                    fab_add.hide();
                    break;

                case MotionEvent.ACTION_UP:
                    //Toast.makeText(context, "ACTIOUp", Toast.LENGTH_SHORT).show();
                    fab_add.show();
                    break;
            }
            return false;
        });

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    filterType = "";
                    rbAll.setChecked(true);
                    new AsyncTaskGetTransactionDataonResume().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                swipeToRefresh.setRefreshing(false);
            }
        });

        buildGoogleSignInClient();
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        signOut();


        addtutorials();

    }

    private void addtutorials() {
        new FancyShowCaseView.Builder(this)
                .focusOn(ll_monthView)
                .title("Change month year to view Expenses")
                .showOnce("id0")
                .build()
                .show();
    }


    @Override
    protected void InitPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ).withListener(new MultiplePermissionsListener() {

            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    //ASSETS IMPORT TEST
                    // new AsyncTaskImportReadAssets().execute();
                    try {
                        new AsyncTaskGetTransactionDataonResume().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        })
                .onSameThread()
                .check();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main_inc;
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    @Override
    public void onClick(View v) {
        if (v == fab_add) {
            Intent i = new Intent(context, AddExpenseIncome.class);
            i.putExtra("from", TAG);
            startActivityForResult(i, RESULT_UPDATED);
        } else if (v == ll_monthView) {
            fab_add.setVisibility(View.GONE);
            calenderview.setVisibility(View.VISIBLE);
            monthAdapter = new MonthAdapter(context, Utils.getAllMonths(), this, current_month);
            rl_recyclerview.setAdapter(monthAdapter);
        } else if (v == rl_calender_mainview) {
            fab_add.setVisibility(View.VISIBLE);
            calenderview.setVisibility(View.GONE);
        } else if (v == btn_menu) {
            drawer.openDrawer(Gravity.START); //Edit Gravity.START need API 14
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
        } else if (v == img_search) {
            Intent i = new Intent(context, ActivitySearch.class);
            startActivityForResult(i, RESULT_UPDATED);
        } else if (v == ll_expenses) {
            Intent i = new Intent(context, ActivityChart.class);
            i.putExtra("from", "Expenses");
            i.putExtra("current_date", current_month);
            startActivityForResult(i, RESULT_UPDATED);
        } else if (v == ll_income) {
            Intent i = new Intent(context, ActivityChart.class);
            i.putExtra("from", "Income");
            i.putExtra("current_date", current_month);
            startActivityForResult(i, RESULT_UPDATED);
        } else if (v == btnYearview) {
            flagYearViewCLicked = "1";
            if (!sp_month.getText().toString().trim().isEmpty()) {
                String a[] = sp_month.getText().toString().trim().split(" ");
                current_month = a[1] + "-" + Utils.getCurrentMonthsIndex(a[0]);
                current_year = a[1];
            }
            new AsyncDialogView().execute();
        } else if (v == btnDayView) {
            flagYearViewCLicked = "2";
            if (!sp_month.getText().toString().trim().isEmpty()) {
                String a[] = sp_month.getText().toString().trim().split(" ");
                current_month = a[1] + "-" + Utils.getCurrentMonthsIndex(a[0]);
                current_year = a[1];
            }
            new AsyncDialogView().execute();
        } else if (v == ivSync) {
            final Dialog dialogExport = new Dialog(context);
            dialogExport.setContentView(R.layout.custom_sync_dialog);
            dialogExport.setTitle("");

            TextView tv_local = dialogExport.findViewById(R.id.tv_local);
            TextView tv_cancel = dialogExport.findViewById(R.id.tv_cancel);
            TextView tv_drive = dialogExport.findViewById(R.id.tv_drive);

          /*  if (mGoogleSignInClient == null) {
                signIn();
            } else {
                getResultsFromApi();
            }*/

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogExport.dismiss();
                }
            });

            tv_drive.setOnClickListener(view -> {
                dialogExport.dismiss();
                if (mGoogleSignInClient == null) {
                    signIn();
                } else {
                    getResultsFromApi();
                }
            });

            tv_local.setOnClickListener(view -> {
                dialogExport.dismiss();
                new AsyncTaskSaveToLocal().execute();
            });

            dialogExport.show();


        }
    }

    @Override
    public void childClicked(int id) {
        Intent i = new Intent(context, ExpenseDetails.class);
        i.putExtra("trans_id", String.valueOf(id));
        startActivityForResult(i, RESULT_UPDATED);
    }

    @Override
    public String returnedYear(String month) {
        ////1 when clicked from topbarview then stay on dashboard/// 2 when clicked from sidemenu then stay on calenderActivity
        if (flagYearViewCLicked.equalsIgnoreCase("1")) {
            current_month = current_year + "-" + month;
            sp_month.setText(Utils.getCurrentMonths(month) + " " + current_year);
            calenderview.setVisibility(View.GONE);

            if (dialogYearView != null) {
                dialogYearView.dismiss();
            }

            if (dialogMonthView != null) {
                dialogMonthView.dismiss();
            }

            new AsyncTaskGetTransactionData().execute();
        } else if (flagYearViewCLicked.equalsIgnoreCase("2")) {
            current_month = current_year + "-" + month;
            sp_month.setText(Utils.getCurrentMonths(month) + " " + current_year);
            calenderview.setVisibility(View.GONE);

            if (dialogYearView != null) {
                dialogYearView.dismiss();
            }

            if (dialogMonthView != null) {
                dialogMonthView.dismiss();
            }

            Intent i = new Intent(context, ActivityCalender.class);
            i.putExtra("fromRecievedDate", current_month);
            i.putExtra("from", "yeardialog");
            startActivityForResult(i, RESULT_UPDATED);

            //Utils.ShowToast(context,"c "+current_month);
        }
        return null;
    }

    @Override
    public String returnedYearView(String year) {
        if (dialogYearView != null) {
            dialogYearView.dismiss();
        }

        new AsyncDialogFromYearView(year).execute();
        //Utils.ShowToast(context, year);
        return null;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


    ////Import Assets folder data
    ////Read Data from assets folder
    private class AsyncTaskImportReadAssets extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_exceldata.clear();
            //importExcelData(originalFile);
            try {
                InputStream myInput;
                // initialize asset manager
                AssetManager assetManager = getAssets();
                //  open excel sheet
                myInput = assetManager.open("hdata.xls");
                // Create a POI File System object
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

                // Create a workbook using the File System
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                // Get the first sheet from workbook
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                int noOfColumns = mySheet.getRow(0).getLastCellNum();
                Log.d("NoCOlumns", "" + noOfColumns);
                // We now need something to iterate through the cells.
                Iterator<Row> rowIter = mySheet.rowIterator();
                int rowno = 0;
                listTags.clear();
                //textView.append("\n");
                while (rowIter.hasNext()) {
                    //Log.e(TAG, " row no " + rowno);
                    HSSFRow myRow = (HSSFRow) rowIter.next();
                    if (rowno != 0) {

                        Iterator<Cell> cellIter = myRow.cellIterator();
                        int colno = 0;
                        String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "", PaymentMode = "", UserName = "", Time = "", FileName = "", Tags = "";
                        while (cellIter.hasNext()) {
                            HSSFCell myCell = (HSSFCell) cellIter.next();
                            if (colno == 0) {
                                date = myCell.toString();
                            } else if (colno == 1) {
                                Income_Expenses = myCell.toString();
                            } else if (colno == 2) {
                                Category = myCell.toString();
                            } else if (colno == 3) {
                                Memo = myCell.toString();
                            } else if (colno == 4) {
                                Amount = myCell.toString();
                            } else if (colno == 5) {
                                PaymentMode = myCell.toString();
                            } else if (colno == 6) {
                                UserName = myCell.toString();
                            } else if (colno == 7) {
                                Time = myCell.toString();
                            } else if (colno == 8) {
                                FileName = myCell.toString();
                            } else if (colno == 9) {
                                Tags = myCell.toString();
                                listTags.add(myCell.toString());
                            }
                            Log.e("Tagssss", Tags);
                            colno++;
                        }

                        list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount, PaymentMode, UserName, Time, FileName, Tags));
                    }
                    rowno++;
                }
            } catch (
                    Exception e) {
                Log.e(TAG, "error " + e.toString());
            }

            if (list_exceldata.size() > 0) {
                if (listTags.size() > 0) {
                    String listTags1 = listTags.toString().replace("[", "").replace("]", "");
                    Set<String> listtag = new HashSet<String>(Arrays.asList(listTags1.split(",")));
                    listTags = Arrays.asList(cleanUpCommas(listtag.toString().replace("[", "").replace("]", "")).split("\\s*,\\s*"))
                    ;
                    if (listTags.size() > 0) {
                        db.InsertExcelDataTO_TagTable(listTags);
                    }
                }

                Log.e("asdadad", listTags.toString());
                db.truncateTempTable();
                db.InsertExcelDataTO_TempTable(list_exceldata);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // progressdialog.setProgress(values[0]);
            //Log.e(TAG, " Publish :" + values[0]);
            // pDialog.setProgress(values[0]);
            // pDialog.setMax(totalCounts);
            /*if (this.bar != null) {
                bar.setProgress(values[0]);
            }*/
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new AsyncTaskInsertToCategoryTbl().execute();
        }
    }

    public String cleanUpCommas(String string) {
        return joiner.join(splitter.split(string));
    }

    private class AsyncTaskImport extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_exceldata.clear();
            //importExcelData(originalFile);
            try {
                InputStream inputStream = new FileInputStream(originalFile);
                POIFSFileSystem myFileSystem = new POIFSFileSystem(inputStream);
                // Create a workbook using the File System
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                // Get the first sheet from workbook
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                int noOfColumns = mySheet.getRow(0).getLastCellNum();
                Log.d("SheetCount", "" + noOfColumns);
                // We now need something to iterate through the cells.
                Iterator<Row> rowIter = mySheet.rowIterator();
                //Log.d("TotalNoOfRows", "" + rowTotal);
                if (noOfColumns == 5) {
                    int rowno = 0;
                    //textView.append("\n");
                    while (rowIter.hasNext()) {
                        //Log.e(TAG, " row no " + rowno);
                        HSSFRow myRow = (HSSFRow) rowIter.next();
                        if (rowno != 0) {
                            Iterator<Cell> cellIter = myRow.cellIterator();
                            int colno = 0;
                            String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "";
                            while (cellIter.hasNext()) {
                                HSSFCell myCell = (HSSFCell) cellIter.next();
                                if (colno == 0) {
                                    date = myCell.toString();
                                } else if (colno == 1) {
                                    Income_Expenses = myCell.toString();
                                } else if (colno == 2) {
                                    Category = myCell.toString();
                                } else if (colno == 3) {
                                    Memo = myCell.toString();
                                } else if (colno == 4) {
                                    Amount = myCell.toString();
                                }
                                colno++;
                                // Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                            }

                            list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount));
                        }
                        rowno++;
                        publishProgress(rowno);

                    }
                } else if (noOfColumns == 6) {
                    int rowno = 0;
                    //textView.append("\n");
                    while (rowIter.hasNext()) {
                        //Log.e(TAG, " row no " + rowno);
                        HSSFRow myRow = (HSSFRow) rowIter.next();
                        if (rowno != 0) {
                            Iterator<Cell> cellIter = myRow.cellIterator();
                            int colno = 0;
                            String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "", PaymentMode = "";
                            while (cellIter.hasNext()) {
                                HSSFCell myCell = (HSSFCell) cellIter.next();
                                if (colno == 0) {
                                    date = myCell.toString();
                                } else if (colno == 1) {
                                    Income_Expenses = myCell.toString();
                                } else if (colno == 2) {
                                    Category = myCell.toString();
                                } else if (colno == 3) {
                                    Memo = myCell.toString();
                                } else if (colno == 4) {
                                    Amount = myCell.toString();
                                } else if (colno == 5) {
                                    PaymentMode = myCell.toString();
                                }
                                colno++;
                                //Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                            }
                            list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount, PaymentMode));
                        }
                        rowno++;
                        //Log.e(TAG, " Rowno :" + rowno);
                        publishProgress(rowno);
                    }
                } else if (noOfColumns == 7) {
                    int rowno = 0;
                    //textView.append("\n");
                    while (rowIter.hasNext()) {
                        //Log.e(TAG, " row no " + rowno);
                        HSSFRow myRow = (HSSFRow) rowIter.next();
                        if (rowno != 0) {
                            Iterator<Cell> cellIter = myRow.cellIterator();
                            int colno = 0;
                            String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "", PaymentMode = "", UserName = "";
                            while (cellIter.hasNext()) {
                                HSSFCell myCell = (HSSFCell) cellIter.next();
                                if (colno == 0) {
                                    date = myCell.toString();
                                } else if (colno == 1) {
                                    Income_Expenses = myCell.toString();
                                } else if (colno == 2) {
                                    Category = myCell.toString();
                                } else if (colno == 3) {
                                    Memo = myCell.toString();
                                } else if (colno == 4) {
                                    Amount = myCell.toString();
                                } else if (colno == 5) {
                                    PaymentMode = myCell.toString();
                                } else if (colno == 6) {
                                    UserName = myCell.toString();
                                }
                                colno++;
                                //Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                            }
                            list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount, PaymentMode, UserName));
                        }
                        rowno++;
                        //Log.e(TAG, " Rowno :" + rowno);
                        publishProgress(rowno);
                    }
                } else if (noOfColumns == 8) {
                    int rowno = 0;
                    //textView.append("\n");
                    while (rowIter.hasNext()) {
                        //Log.e(TAG, " row no " + rowno);
                        HSSFRow myRow = (HSSFRow) rowIter.next();
                        if (rowno != 0) {
                            Iterator<Cell> cellIter = myRow.cellIterator();
                            int colno = 0;
                            String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "", PaymentMode = "", UserName = "", Time = "";
                            while (cellIter.hasNext()) {
                                HSSFCell myCell = (HSSFCell) cellIter.next();
                                if (colno == 0) {
                                    date = myCell.toString();
                                } else if (colno == 1) {
                                    Income_Expenses = myCell.toString();
                                } else if (colno == 2) {
                                    Category = myCell.toString();
                                } else if (colno == 3) {
                                    Memo = myCell.toString();
                                } else if (colno == 4) {
                                    Amount = myCell.toString();
                                } else if (colno == 5) {
                                    PaymentMode = myCell.toString();
                                } else if (colno == 6) {
                                    UserName = myCell.toString();
                                } else if (colno == 7) {
                                    Time = myCell.toString();
                                }
                                colno++;
                                //Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                            }
                            list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount, PaymentMode, UserName, Time));
                        }
                        rowno++;
                        //Log.e(TAG, " Rowno :" + rowno);
                        publishProgress(rowno);
                    }
                } else if (noOfColumns == 9) {
                    int rowno = 0;
                    //textView.append("\n");
                    while (rowIter.hasNext()) {
                        //Log.e(TAG, " row no " + rowno);
                        HSSFRow myRow = (HSSFRow) rowIter.next();
                        if (rowno != 0) {
                            Iterator<Cell> cellIter = myRow.cellIterator();
                            int colno = 0;
                            String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "", PaymentMode = "", UserName = "", Time = "", FileName = "";
                            while (cellIter.hasNext()) {
                                HSSFCell myCell = (HSSFCell) cellIter.next();
                                if (colno == 0) {
                                    date = myCell.toString();
                                } else if (colno == 1) {
                                    Income_Expenses = myCell.toString();
                                } else if (colno == 2) {
                                    Category = myCell.toString();
                                } else if (colno == 3) {
                                    Memo = myCell.toString();
                                } else if (colno == 4) {
                                    Amount = myCell.toString();
                                } else if (colno == 5) {
                                    PaymentMode = myCell.toString();
                                } else if (colno == 6) {
                                    UserName = myCell.toString();
                                } else if (colno == 7) {
                                    Time = myCell.toString();
                                } else if (colno == 8) {
                                    FileName = myCell.toString();
                                }
                                colno++;
                                //Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                            }
                            list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount, PaymentMode, UserName, Time, FileName));
                        }
                        rowno++;
                        //Log.e(TAG, " Rowno :" + rowno);
                        publishProgress(rowno);
                    }
                } else if (noOfColumns == 10) {
                    int rowno = 0;
                    //listTags.clear();
                    //textView.append("\n");
                    while (rowIter.hasNext()) {
                        //Log.e(TAG, " row no " + rowno);
                        HSSFRow myRow = (HSSFRow) rowIter.next();
                        if (rowno != 0) {
                            Iterator<Cell> cellIter = myRow.cellIterator();
                            int colno = 0;
                            String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "", PaymentMode = "", UserName = "", Time = "", FileName = "", Tags = "";
                            while (cellIter.hasNext()) {
                                HSSFCell myCell = (HSSFCell) cellIter.next();
                                if (colno == 0) {
                                    date = myCell.toString();
                                } else if (colno == 1) {
                                    Income_Expenses = myCell.toString();
                                } else if (colno == 2) {
                                    Category = myCell.toString();
                                } else if (colno == 3) {
                                    Memo = myCell.toString();
                                } else if (colno == 4) {
                                    Amount = myCell.toString();
                                } else if (colno == 5) {
                                    PaymentMode = myCell.toString();
                                } else if (colno == 6) {
                                    UserName = myCell.toString();
                                    //Log.e("userName", myCell.toString());
                                } else if (colno == 7) {
                                    Time = myCell.toString();
                                } else if (colno == 8) {
                                    FileName = myCell.toString();
                                } else if (colno == 9) {
                                    Tags = myCell.toString();
                                    listTags.add(myCell.toString());
                                }
                                colno++;
                                // Log.e("colcounter", ""+colno);
                                Log.e(TAG, " Index :" + colno + "--" + myCell.getColumnIndex() + " -- " + myCell.toString());
                            }
                            list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount, PaymentMode, UserName, Time, FileName, Tags));
                        }
                        rowno++;
                        //Log.e(TAG, " Rowno :" + rowno);
                        publishProgress(rowno);
                    }
                }
            } catch (
                    Exception e) {
                Log.e(TAG, "error " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialogProgress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //Log.e(TAG, " Publish :" + values[0]);
            pDialogProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pDialogProgress.dismiss();
            new AsyncTaskInsertTemp().execute();
        }
    }

    private class AsyncTaskInsertTemp extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (list_exceldata.size() > 0) {
                if (listTags.size() > 0) {
                    String listTags1 = listTags.toString().replace("[", "").replace("]", "");
                    Set<String> listtag = new HashSet<String>(Arrays.asList(listTags1.split(",")));
                    listTags = Arrays.asList(cleanUpCommas(listtag.toString().replace("[", "").replace("]", "")).split("\\s*,\\s*"));
                    if (listTags.size() > 0) {
                        db.InsertExcelDataTO_TagTable(listTags);
                    }
                }
                db.truncateTempTable();
                ///inserting data into temp table///
                db.InsertExcelDataTO_TempTable(list_exceldata);
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
            pDialogProgress.dismiss();
            new AsyncTaskInsertToCategoryTbl().execute();

        }
    }


    private class AsyncTaskInsertToCategoryTbl extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_exceldataCat.clear();
            list_exceldataCat = db.getDistinctCategory();
            if (list_exceldataCat.size() > 0) {
                db.InsertCategoryData(list_exceldataCat);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pImport.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new AsyncTaskInsertUserToUserTbl().execute();

        }
    }

    private class AsyncUpdateCategoryIdTempTable extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_categoryData = db.GetAllCAtegoryData();
            db.UpdateTempCatDataAfterDataImport();
            db.UpdateTempUserDataAfterDataImport();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("TagBeautifier", "Beautifier122wqwq");
            if (list_categoryData.size() > 0) {
                Log.e("TagBeautifier", "Beautifier122");
                new AsyncTaskInsertDataToTransactionTable().execute();
            } else {
                pImport.dismiss();
            }
        }
    }

    ///insert user to user table
    private class AsyncTaskInsertUserToUserTbl extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_exceldataUser.clear();
            list_exceldataUser = db.getDistinctUsers();
            if (list_exceldataUser.size() > 0) {
                db.InsertUserData(list_exceldataUser);
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
            new AsyncUpdateCategoryIdTempTable().execute();
        }
    }


    private class AsyncTaskInsertDataToTransactionTable extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_temp_categoryData_withID = db.getAllTempDataWIthID();
            if (list_temp_categoryData_withID.size() > 0) {
                db.InsertDataTO_TransactionTable(list_temp_categoryData_withID);
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
            new AsyncTaskTagsBeautifier().execute();
        }
    }

    private class AsyncTaskTagsBeautifier extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("TagBeautifier", "Beautifier");
            tagsMap = db.getTagsInMap();
            if (tagsMap.size() > 0) {
                db.DeleteTagstfromDummyTable();
                for (Map.Entry<String, String> p : tagsMap.entries()) {
                    db.AddTagstToDummyTable(p.getKey(), p.getValue());
                }
                db.UpdateTempTagsWithIdAfterDataImport();
                db.insertDataintoTagTables();
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
            if (list_temp_categoryData_withID.size() > 0) {
                new AsyncTaskGetTransactionData().execute();
            } else {
                pImport.dismiss();
            }
        }
    }

    private class AsyncTaskGetTransactionData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_final_transaction_data = db.getTransactionDetails(current_month);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pImport.dismiss();
            p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_final_transaction_data.size() > 0) {
                setUpAdatpter();
            } else {
                tv_income.setText("0.0");
                tv_inc_count.setText("(" + 0 + " records)");
                tv_exp_count.setText("(" + 0 + " records)");
                tv_expenses.setText("0.0");
                tv_balance.setText("0.0");
                tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
                tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
                expandableListView.setVisibility(View.GONE);
            }
            p.dismiss();
        }
    }

    private void setUpAdatpter() {
        expandableListView.setVisibility(View.VISIBLE);
        expandableListAdapter = new TransactionAdapter(context, list_final_transaction_data, this);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setVisibility(View.VISIBLE);
        setFilter();
        new AsyncTaskSummationExpenses().execute();
    }

    private class AsyncTaskSummationExpenses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ExcelDataModel excelDataModel = new ExcelDataModel();
            excelDataModel = db.GetExpensesData("Expenses", current_month);
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
                tv_expenses.setText(String.valueOf(0.0));
                tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
            } else {
                tv_expenses.setText(String.valueOf(new DecimalFormat("#,##,###.00").format(st_expense)));
                tv_payment_mode_exp.setText("Expenses: " + String.valueOf(new DecimalFormat("#,##,###.00").format(st_expense)));
            }

            tv_exp_count.setText("(" + String.valueOf(count_) + " records)");
            new AsyncTaskIncomeExpenses().execute();
        }
    }

    private class AsyncTaskIncomeExpenses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ExcelDataModel excelDataModel = new ExcelDataModel();
            excelDataModel = db.GetExpensesData("Income", current_month);
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
                tv_income.setText(String.valueOf(0.0));
                tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
            } else {
                tv_income.setText(String.valueOf(new DecimalFormat("#,##,###.00").format(st_income)));
                tv_payment_mode_inc.setText("Income: " + String.valueOf(new DecimalFormat("#,##,###.00").format(st_income)));
            }

            double bal = 0.0;
            tv_inc_count.setText("(" + String.valueOf(count_) + " records)");
            if (st_income < st_expense) {
                bal = st_income - st_expense;
                if (bal == -0.0 || bal == 0.0) {
                    tv_balance.setText(String.valueOf(0.0));
                } else {
                    tv_balance.setText(String.valueOf(new DecimalFormat("#,##,###.00").format(bal)));
                }
            } else {
                bal = st_income - st_expense;
                if (bal == -0.0 || bal == 0.0) {
                    tv_balance.setText(String.valueOf(0.0));
                } else {
                    tv_balance.setText(String.valueOf(new DecimalFormat("#,##,###.00").format(bal)));
                }
            }
            p.dismiss();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        openNavDrawer(id, context);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openNavDrawer(int id, final Context mContext) {
        if (id == R.id.nav_import) {
            /*Intent intent = new Intent()
                    .setType("file/*")
                    .setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 7);*/
            if (Build.VERSION.SDK_INT == 25) {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), RC_GALLERY);
            } else {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_OPEN_DOCUMENT);
                //intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), RC_GALLERY);
            }
        } else if (id == R.id.nav_export) {
            OpenExportDialog();
        } else if (id == R.id.nav_chart) {
            Intent i = new Intent(context, ActivityChart.class);
            i.putExtra("from", "sidemenu");
            startActivityForResult(i, RESULT_UPDATED);
        } else if (id == R.id.nav_categories) {
            Intent i = new Intent(context, CategoryActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_about) {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_about);
            dialog.setTitle("");
            TextView txtName = (TextView) dialog.findViewById(R.id.text2);
            TextView txtNo = (TextView) dialog.findViewById(R.id.text3);
            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
            dialogButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(context, ActivitySettings.class);
            startActivityForResult(i, RESULT_UPDATED);
        } else if (id == R.id.nav_exportDB) {
            Utils.copyDatabase();
        } else if (id == R.id.nav_users) {
            Intent i = new Intent(context, AddUserActivity.class);
            startActivityForResult(i, RESULT_UPDATED);
        } else if (id == R.id.nav_graph) {
            Intent i = new Intent(context, GraphActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_compare) {
            Intent i = new Intent(context, ActivityCompare.class);
            startActivity(i);
        } else if (id == R.id.nav_calender) {
            Intent i = new Intent(context, ActivityCalender.class);
            i.putExtra("from", "sidemenu");
            startActivityForResult(i, RESULT_UPDATED);
        } else if (id == R.id.nav_yearwise) {
            flagYearViewCLicked = "2";
            if (!sp_month.getText().toString().trim().isEmpty()) {
                String a[] = sp_month.getText().toString().trim().split(" ");
                current_month = a[1] + "-" + Utils.getCurrentMonthsIndex(a[0]);
                current_year = a[1];
            }
            new AsyncDialogView().execute();
        } else if (id == R.id.nav_backfiles) {
            Intent i = new Intent(context, ActivityLocalBackupfiles.class);
            startActivityForResult(i, RESULT_UPDATED);
        } else if (id == R.id.nav_labels) {
            Intent i = new Intent(context, AddtagsActivity.class);
            i.putExtra("from", "sidemenu");
            startActivityForResult(i, RESULT_UPDATED);
        }
    }

    private void OpenExportDialog() {
        final Dialog dialogExport = new Dialog(context);
        dialogExport.setContentView(R.layout.custom_export_dialog);
        dialogExport.setTitle("");

        btn_fromdata = dialogExport.findViewById(R.id.btn_fromdata);
        btn_todata = dialogExport.findViewById(R.id.btn_todata);
        TextView tv_open = dialogExport.findViewById(R.id.tv_open);
        TextView tv_cancel = dialogExport.findViewById(R.id.tv_cancel);
        TextView tv_ok = dialogExport.findViewById(R.id.tv_ok);

        btn_fromdata.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(context, fromdate,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btn_todata.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog todateDialog = new DatePickerDialog(context, todate,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                todateDialog.show();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogExport.dismiss();
            }
        });

        tv_ok.setOnClickListener(v -> {
            if (!startDate.isEmpty() && !endDate.isEmpty()) {
                new AsyncTaskExportData().execute();
                dialogExport.dismiss();
            } else {
                Utils.ShowToast(context, "Select Date");
            }
        });

        tv_open.setOnClickListener(view -> {
            if (!startDate.isEmpty() && !endDate.isEmpty()) {
                new AsyncTaskOpenFIle().execute();
                dialogExport.dismiss();
            } else {
                Utils.ShowToast(context, "Select Date");
            }
        });

        dialogExport.show();
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
            btn_fromdata.setText(startDate);
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
            btn_todata.setText(endDate);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 7:
                if (resultCode == RESULT_OK) {
                    try {
                        //listAllTransaction = db.GetAllFinalTransactionDataUsingDate();
                        final Uri uri = data.getData();
                        Log.d("FilePATHS", uri.toString());
                        originalFile = new File(FileUtils.getRealPath(this, uri));
                        InputStream inputStream = new FileInputStream(originalFile);
                        POIFSFileSystem myFileSystem = new POIFSFileSystem(inputStream);
                        // Create a workbook using the File System
                        HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                        // Get the first sheet from workbook
                        HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                        // We now need something to iterate through the cells.
                        totalCounts = mySheet.getLastRowNum();
                        //Log.d("totalCountsHeader", "" + totalCounts);

                        pDialogProgress = new ProgressDialog(this);
                        pDialogProgress.setMessage("Importing Data Please Wait...");
                        pDialogProgress.setIndeterminate(false);
                        pDialogProgress.setMax(totalCounts);
                        pDialogProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pDialogProgress.setCancelable(false);
                        new AsyncTaskImport().execute();
                        //openFile(context, originalFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case RESULT_UPDATED:
                if (filterType.isEmpty()) {
                    rbAll.setChecked(true);
                    new AsyncTaskUpdateList().execute();
                } else {
                    if (filterType.equalsIgnoreCase("Cash")) {
                        rbCash.setChecked(true);
                    } else if (filterType.equalsIgnoreCase("Credit Card")) {
                        rbDebit.setChecked(true);
                    } else if (filterType.equalsIgnoreCase("Cheque/Debit Card")) {
                        rbCheque.setChecked(true);
                    } else {
                        filterType = "";
                        rbAll.setChecked(true);
                    }
                    new AsyncFilter().execute();
                }
                break;
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.e(this.toString(), "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
        }
    }


    private class AsyncTaskUpdateList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_final_transaction_data.clear();
            list_user.clear();
            list_user = db.getAllMyDetails();
            list_final_transaction_data = db.getTransactionDetails(current_month);
            return null;
        }

        @Override
        protected void onPreExecute() {
            p.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (list_user.size() > 0) {
                    if (!list_user.get(0).getUser_relation().isEmpty() || list_user.get(0).getUser_relation() != null) {
                        byte[] decodedString = Base64.decode(list_user.get(0).getUser_relation(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        iv_drawer_Userphoto.setImageBitmap(decodedByte);
                    } else {
                        iv_drawer_Userphoto.setImageResource(R.drawable.ic_avatar);
                    }
                }
            } catch (Exception e) {

            }

            if (list_final_transaction_data.size() > 0) {
                try {
                    expandableListView.setVisibility(View.VISIBLE);
                    expandableListAdapter.updateReceiptsList(list_final_transaction_data);
                    new AsyncTaskSummationExpenses().execute();
                } catch (Exception e) {
                    new AsyncTaskGetTransactionData().execute();
                }
            } else {
                p.dismiss();
                list_final_transaction_data.clear();
                expandableListView.setVisibility(View.GONE);
                tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
                tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
                tv_income.setText("0.0");
                tv_inc_count.setText("(" + 0 + " records)");
                tv_exp_count.setText("(" + 0 + " records)");
                tv_expenses.setText("0.0");
                tv_balance.setText("0.0");

            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public String returnedMonth(String month) {
        filterType = "";
        rbAll.setChecked(true);
        tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
        tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
        current_month = tv_currentyear.getText().toString() + "-" + month;
        sp_month.setText(Utils.getCurrentMonths(month) + " " + tv_currentyear.getText().toString());
        fab_add.setVisibility(View.VISIBLE);
        calenderview.setVisibility(View.GONE);
        new AsyncTaskGetTransactionData().execute();
        return month;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.getIsCaptureTimeON(context)) {
            Utils.setIsCaptureTime(context, true);
        } else {
            Utils.setIsCaptureTime(context, false);
        }
    }


    private class AsyncTaskGetTransactionDataonResume extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                list_user = db.getAllMyDetails();
                list_final_transaction_data_resume = db.getTransactionDetails(current_month);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_final_transaction_data_resume.clear();
            list_user.clear();
            p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (list_user.size() > 0) {
                    if (!list_user.get(0).getUser_relation().isEmpty() || list_user.get(0).getUser_relation() != null) {
                        byte[] decodedString = Base64.decode(list_user.get(0).getUser_relation(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        iv_drawer_Userphoto.setImageBitmap(decodedByte);
                    } else {
                        iv_drawer_Userphoto.setImageResource(R.drawable.ic_avatar);
                    }
                }
            } catch (Exception e) {
            }

            if (list_final_transaction_data_resume.size() > 0) {
                setUpAdatpter1();
            } else {
                expandableListView.setVisibility(View.GONE);
            }
            new AsyncTaskSummationExpenses().execute();
            p.dismiss();
        }
    }


    private void setUpAdatpter1() {
        expandableListView.setVisibility(View.VISIBLE);
        expandableListAdapter = new TransactionAdapter(context, list_final_transaction_data_resume, this);
        expandableListView.setAdapter(expandableListAdapter);
        setFilter();
    }


    private class AsyncFilter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                list_final_transaction_data_resume = db.getTransactionFilter(current_month, filterType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_final_transaction_data_resume.clear();
            p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_final_transaction_data_resume.size() > 0) {
                expandableListView.setVisibility(View.VISIBLE);
                //setUpAdatpter1();
                expandableListAdapter.updateReceiptsList(list_final_transaction_data_resume);
            } else {
                expandableListView.setVisibility(View.GONE);
            }
            new AsyncTaskSummationExpensesModeWise().execute();
        }
    }


    private class AsyncTaskSummationExpensesModeWise extends AsyncTask<Void, Void, Void> {

        ExcelDataModel excelDataModel1 = new ExcelDataModel();

        @Override
        protected Void doInBackground(Void... voids) {
            excelDataModel1 = db.GetExpensesDataModeWise("Expenses", current_month, filterType);
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

            new AsyncTaskIncomeExpensesModeWise().execute();
        }
    }

    private class AsyncTaskIncomeExpensesModeWise extends AsyncTask<Void, Void, Void> {

        ExcelDataModel excelDataModel1 = new ExcelDataModel();

        @Override
        protected Void doInBackground(Void... voids) {

            excelDataModel1 = db.GetExpensesDataModeWise("Income", current_month, filterType);
            st_income_paymentMode = 0;
            st_income_paymentMode = excelDataModel1.getAmount_();
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

            p.dismiss();
        }
    }


    private void setFilter() {
        rgFilter.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.rbAll:
                    filterType = "";
                    st_expense_paymentMode = 0.0;
                    st_income_paymentMode = 0.0;
                    tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
                    tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
                    new AsyncFilter().execute();
                    //expandableListAdapter.getFilter().filter("");
                    break;
                case R.id.rbCash:
                    st_expense_paymentMode = 0.0;
                    st_income_paymentMode = 0.0;
                    tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
                    tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
                    //expandableListAdapter.getFilter().filter("Cash");
                    filterType = "Cash";
                    new AsyncFilter().execute();
                    break;
                case R.id.rbDebit:
                    //expandableListAdapter.getFilter().filter("Credit Card");
                    filterType = "Credit Card";
                    st_expense_paymentMode = 0.0;
                    st_income_paymentMode = 0.0;
                    tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
                    tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
                    new AsyncFilter().execute();
                    break;
                case R.id.rbCheque:
                    st_expense_paymentMode = 0.0;
                    st_income_paymentMode = 0.0;
                    tv_payment_mode_exp.setText("Expenses: " + String.valueOf(0.0));
                    tv_payment_mode_inc.setText("Income: " + String.valueOf(0.0));
                    filterType = "Cheque/Debit Card";
                    new AsyncFilter().execute();
                    //expandableListAdapter.getFilter().filter("Cheque/Debit Card");
                    break;
            }
        });
    }


    public void writeCountryListToFile(String fileName, List<ExcelDataModel> countryList) throws Exception {
        Workbook workbook = null;

        if (fileName.endsWith("xlsx")) {
        } else if (fileName.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new Exception("invalid file name, should be xls or xlsx");
        }

        Sheet sheet = workbook.createSheet("Expense_tracker_export");

        Iterator<ExcelDataModel> iterator = countryList.iterator();

        int rowIndex = 0;
        while (iterator.hasNext()) {
            ExcelDataModel country = iterator.next();
            Row row = sheet.createRow(rowIndex++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(country.getCategory());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(country.getIncome_Expenses());
        }
        String filePath = getFilesDir().getPath().toString() + "/Expense_tracker_export.xls";
        File f = new File(filePath);
        //lets write the excel data to file now
        FileOutputStream fos = new FileOutputStream(f);
        workbook.write(fos);
        fos.close();
        System.out.println(fileName + " written successfully");
    }


    private void importExcelData(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            POIFSFileSystem myFileSystem = new POIFSFileSystem(inputStream);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowTotal = mySheet.getLastRowNum();
            Log.d("TotalNoOfRows", "" + rowTotal);
            int rowno = 0;
            while (rowIter.hasNext()) {
                //Log.e(TAG, " row no " + rowno);
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0) {
                            date = myCell.toString();
                        } else if (colno == 1) {
                            Income_Expenses = myCell.toString();
                        } else if (colno == 2) {
                            Category = myCell.toString();
                        } else if (colno == 3) {
                            Memo = myCell.toString();
                        } else if (colno == 4) {
                            Amount = myCell.toString();
                        }
                        colno++;
                        //Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    list_exceldata.add(new ExcelDataModel(date, Income_Expenses, Category, Memo, Amount));
                }
                rowno++;
            }
        } catch (
                Exception e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    private class AsyncTaskExportData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                export_excel.clear();
                export_excel = db.GetAllFinalTransactionData(startDate, endDate);
            } catch (Exception e) {

            }
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
            //Collections.sort(export_excel, new ExcelDataModelDateCompDesc());
            //1 for share
            Utils.ExportToExcelSheet(export_excel, startDate, endDate, apkStorage1, outputFile, context, "1");
            startDate = "";
            endDate = "";
            p.dismiss();
        }
    }

    private class AsyncTaskSaveToLocal extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                export_excel.clear();
                export_excel = db.GetAllTransaction();
            } catch (Exception e) {

            }
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
            Utils.ExportAndSaveToLocal(export_excel, apkStorage, outputFile, context);
            Utils.ShowToast(context, "Backup Created Successfully");
            p.dismiss();
        }
    }


    private class AsyncTaskOpenFIle extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                export_excel.clear();
                export_excel = db.GetAllFinalTransactionData(startDate, endDate);
            } catch (Exception e) {

            }
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
            //2 for open
            //Collections.sort(export_excel, new ExcelDataModelDateCompDesc());
            Utils.ExportToExcelSheet(export_excel, startDate, endDate, apkStorage1, outputFile, context, "2");
            startDate = "";
            endDate = "";
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
            tv_currentyear_mdialog = dialogMonthView.findViewById(R.id.tv_currentyear);
            tv_currentyear_mdialog.setText(current_year);

            tv_currentyear_mdialog.setOnClickListener(view -> {
                popupYear = tv_currentyear_mdialog.getText().toString().trim();
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
                int year = Integer.parseInt(tv_currentyear_mdialog.getText().toString());
                year--;
                st_year = String.valueOf(year);
                tv_currentyear_mdialog.setText(st_year);
                current_year = tv_currentyear_mdialog.getText().toString();
                new AsyncUpdateDialogData().execute();
            });

            tv_year_forward.setOnClickListener(view -> {
                int year = Integer.parseInt(tv_currentyear_mdialog.getText().toString());
                year++;
                st_year = String.valueOf(year);
                tv_currentyear_mdialog.setText(st_year);
                current_year = tv_currentyear_mdialog.getText().toString();
                new AsyncUpdateDialogData().execute();
            });

            rl_view.setLayoutManager(new GridLayoutManager(context, 3));
            dialogMonthView.show();
            monthlyExpenseAdapter = new MonthlyExpenseAdapter(context, Utils.getAllMonths(), getMonthlyFromAdapter, current_month, listPopup);
            rl_view.setAdapter(monthlyExpenseAdapter);
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

        });

        tv_year_forward.setOnClickListener(view -> {

            if (!tv_currentyear.getText().toString().trim().isEmpty()) {
                String p[] = tv_currentyear.getText().toString().trim().split(" - ");
                int yearP = Integer.parseInt(p[0].trim()) + 12;
                int yearF = Integer.parseInt(p[1].trim()) + 12;
                tv_currentyear.setText(yearP + " - " + yearF);
                String dateRange = yearP + " - " + yearF;
                generateCalender(dateRange, "b", listPopup);
            }
        });
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
                listMonth.add(new Months(String.valueOf(i), String.valueOf(i), ""));
            }

        }

        if (listMonth.size() > 0) {
            rl_Yearview.setLayoutManager(new GridLayoutManager(context, 3));
            yearlyExpenseAdapter = new YearlyExpenseAdapter(context, listMonth, getYearlyFromAdapter, current_month, listPopup);
            rl_Yearview.setAdapter(yearlyExpenseAdapter);
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
            monthlyExpenseAdapter = new MonthlyExpenseAdapter(context, Utils.getAllMonths(), getMonthlyFromAdapter, current_month, listPopup);
            rl_view.setAdapter(monthlyExpenseAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Utils.ShowToast(context, "Double press to exit");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.e("Check", account.getEmail() + account.getGivenName() + account.getFamilyName());
            Toast.makeText(getApplicationContext(), account.getEmail() + " " + account.getGivenName(), Toast.LENGTH_LONG).show();
            // If successful you can obtain the account info using the getter methods
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("Sign-In", "signInResult:failed code=" + e.getStatusCode() + e);
            Toast.makeText(getApplicationContext(),
                    "Sign In Failed.Try again Later", Toast.LENGTH_LONG).show();
        }
    }

    private void buildGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);
    }

    public void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(getApplicationContext(),
                    "No Network Connection Available", Toast.LENGTH_SHORT).show();
            //Log.e(this.toString(), "No network connection available.");
        } else {
            //if everything is Ok
            //if (calledFrom == 2) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Do you want to Sync the Transaction Data to Drive?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            asyncTaskFetchAllData = new AsyncTaskExportDriveSync();
                            asyncTaskFetchAllData.execute();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();


            //}
            /*if (calledFrom == 1) {
                new DriveActivity.MakeDriveRequestTask(mCredential, DriveActivity.this).execute();//create app folder in drive
            }*/
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                Dashboard.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.e(this.toString(), "Checking if device");
        return (networkInfo != null && networkInfo.isConnected());
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            //Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    private class MakeDriveRequestTask2 extends AsyncTask<Void, Void, List<String>> {
        private Drive mService = null;
        private Exception mLastError = null;
        private Context mContext;

        MakeDriveRequestTask2(GoogleAccountCredential credential, Context context) {
            mContext = context;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("UsingDriveJavaApi")
                    .build();
            p.setMessage("Uploading data to Drive....");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                }
            });
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                uploadFile(outputFile);
            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Dashboard.REQUEST_AUTHORIZATION);
                } else {
                    Log.e(this.toString(), "The following error occurred:\n" + mLastError.getMessage());
                }
                Log.e(this.toString(), e + "");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            p.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            p.dismiss();
            new AsyncTaskAddSyncStatus().execute();
        }

        @Override
        protected void onCancelled() {
            //Utils.ShowToast(context, "Upload Cancelled");
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Dashboard.REQUEST_AUTHORIZATION);
                } else {
                    Log.e("DriveError", mLastError.getMessage());
                }
            } else {
                Log.e("DriveError", "Request cancelled.");
            }
        }

        private void uploadFile(File outputFile) throws IOException {
            syncFileName = "ETracker_" + Utils.getCurrentDateTime() + ".xls";
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(syncFileName);
            fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");

            Collections.sort(export_excel, new ExcelDataModelDateCompDesc());
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("sheet1");
            Map<Integer, Object[]> data = new TreeMap<>();
            data.put(1, new Object[]{"Date", "Income/Expenses", "Category", "Memo", "Amount", "Payment Mode", "User Name", "DateTime", "File Name"});


            if (export_excel.size() > 0) {
                for (int i = 0; i < export_excel.size(); i++) {
                    data.put((i + 2), new Object[]{export_excel.get(i).getDate(), export_excel.get(i).getIncome_Expenses(), export_excel.get(i).getCategory(), export_excel.get(i).getMemo(), export_excel.get(i).getAmount(), export_excel.get(i).getPayment_mode(), export_excel.get(i).getUserName(), export_excel.get(i).getTime(), export_excel.get(i).getFileName()});
                }

                Set<Integer> keyset = data.keySet();
                int rownum = 0;
                for (Integer key : keyset) {
                    Row row = sheet.createRow(rownum++);
                    Object[] objArr = data.get(key);
                    //Log.e("KYES", Arrays.toString(data.get(key)));
                    int cellnum = 0;

                    for (Object obj : objArr) {
                        Cell cell = row.createCell(cellnum++);
                        if (obj instanceof String)
                            cell.setCellValue((String) obj);
                        else if (obj instanceof Integer)
                            cell.setCellValue((Integer) obj);
                    }
                }

                outputFile = new File(apkStorage, syncFileName);

                try {
                    FileOutputStream out = new FileOutputStream(outputFile);
                    workbook.write(out);
                    out.close();
                } catch (Exception e) {

                }

                FileContent mediaContent = new FileContent("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", outputFile);
                file = mService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();

            }
        }
    }

    private class AsyncTaskExportDriveSync extends AsyncTask<Void, Void, Void> {
        public AsyncTaskExportDriveSync() {
            p = new ProgressDialog(context);
            p.setMessage("Fetching Data....");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                }
            });

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!isCancelled()) {
                try {
                    export_excel.clear();
                    export_excel = db.GetAllTransaction();
                    if (isCancelled()) {
                        return null;
                    }
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.ShowToast(context, "Fetching Cancelled");
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
            if (export_excel.size() > 0) {
                makeDriveRequestTask2 = new MakeDriveRequestTask2(mCredential, Dashboard.this);
                makeDriveRequestTask2.execute();
            } else {
                Utils.ShowToast(context, "No Data To Upload");
                p.dismiss();
                return;
            }
        }
    }

    //////add data to the synctable
    private class AsyncTaskAddSyncStatus extends AsyncTask<Void, Void, Void> {
        Sync sync = new Sync();

        @Override
        protected Void doInBackground(Void... voids) {

            sync.setSyncDate(Utils.getCurrentDateTimeSync());

            try {
                sync.setFileId(file.getId());
            } catch (Exception e) {
                sync.setFileId("");
            }

            sync.setFileName(syncFileName);
            sync.setAccountName(mCredential.getSelectedAccountName());

            try {
                if (file.getId().isEmpty() || file.getId() == null) {
                    sync.setSyncStatus("Failed");
                } else {
                    sync.setSyncStatus("Success");
                }
            } catch (Exception e) {
                sync.setSyncStatus("Failed");
            }

            db.AddSyncData(sync);

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (sync.getSyncStatus().equalsIgnoreCase("Success")) {
                Utils.ShowToast(context, "Backup Created to Drive");
                signOut();
            } else {
                Utils.ShowToast(context, "Failed");
            }
        }
    }

    public void signOut() {
       /* mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, null);
                        editor.apply();
                        mCredential.setSelectedAccountName("");
                        mCredential.setSelectedAccountName(null);
                    }
                });*/
    }

}