package com.app.exptracker;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.exptracker.adapter.SyncStatusAdapter;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.model.ExcelDataModelDateCompDesc;
import com.app.exptracker.model.Sync;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.CheckForSDCard;
import com.app.exptracker.utility.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.itextpdf.text.pdf.BaseFont;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ActivitySettings extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "ExpenseDetails";
    Context context;
    TextView sp_month;
    ImageView btn_back, btn_menu;
    private BaseFont bfBold;
    String st_year = "";
    String current_year = "";
    //for calender
    // Button mSignOut,mRevokeAccess;
    SignInButton mSignIn;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    DatabaseHelper db;
    public static final int RC_SIGN_IN = 1;
    ProgressDialog p;
    SimpleDateFormat dateFormat;
    Calendar cal;
    File apkStorage = null;
    SwitchCompat btnSwitch, btnSwitchTimer;
    ImageView iv_dropdown;
    SyncStatusAdapter syncStatusAdapter;
    List<Sync> listSync = new ArrayList<>();
    Button btn_viewSynchistory, btn_sync;
    GoogleAccountCredential mCredential = null;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    AsyncTaskExportDriveSync asyncTaskFetchAllData = null;
    MakeDriveRequestTask2 makeDriveRequestTask2 = null;
    com.google.api.services.drive.model.File file;
    List<ExcelDataModel> export_excel = new ArrayList<>();
    String syncFileName = "";
    File outputFile = null;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String[] SCOPES = {DriveScopes.DRIVE};
    Button btn_changeAccount, btn_localdrive, btn_localHistory;
    TextView tv_accountName;

    @Override
    protected void InitListner() {
        context = this;
        sp_month = findViewById(R.id.sp_month);
        st_year = Utils.getCurrentYear();

        dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
        cal = Calendar.getInstance();

        if (new CheckForSDCard().isSDCardPresent()) {
            apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + Utils.BACKUP_FOLDER_PATH);
        } else {
            Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();
        }

        if (!apkStorage.exists()) {
            apkStorage.mkdir();
        }


        mSignIn = findViewById(R.id.sign_in_btn);
        mSignIn.setSize(SignInButton.SIZE_STANDARD);

        db = new DatabaseHelper(context);

        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);

        if (!Utils.getIsDateShow(context)) {
            btnSwitch.setChecked(false);
        } else {
            btnSwitch.setChecked(true);
        }

        if (!Utils.getIsCaptureTime(context)) {
            btnSwitchTimer.setChecked(false);
        } else {
            btnSwitchTimer.setChecked(true);
        }

        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utils.setIsDateShow(context, true);
                } else {
                    Utils.setIsDateShow(context, false);
                }
            }
        });

        btnSwitchTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utils.setIsCaptureTimeON(context, true);
                    Utils.setIsCaptureTime(context, true);
                } else {
                    Utils.setIsCaptureTimeON(context, false);
                    Utils.setIsCaptureTime(context, false);
                }
            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signIn();
            }
        });

       /*mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();

            }});*/


        // If already signed in with the app it can be obtained here

        /*if (account == null) {
         *//*Toast.makeText(getApplicationContext(),
                    "You Need To Sign In First", Toast.LENGTH_SHORT).show();*//*
        }
        if (account != null) {
            Intent intent = new Intent(this, DriveActivity.class);
            intent.putExtra("ACCOUNT", account);
            startActivity(intent);
        }*/

        buildGoogleSignInClient();
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        try {
            tv_accountName.setVisibility(View.VISIBLE);
            // Utils.ShowToast(context,account);
            tv_accountName.setText("Logged in as: " + getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null));
        } catch (Exception e) {
            tv_accountName.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    private void buildGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        /*In order to access files in drive the scope of the permission has to be specified.
        More info on scope is available in Google Drive Api Documentation*/

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        account = GoogleSignIn.getLastSignedInAccount(this);
    }


    @Override
    protected void InitResources() {
        btn_localdrive = findViewById(R.id.btn_localdrive);
        btn_localHistory = findViewById(R.id.btn_localHistory);
        tv_accountName = findViewById(R.id.tv_accountName);
        btn_changeAccount = findViewById(R.id.btn_changeAccount);
        btn_sync = findViewById(R.id.btn_sync);
        btn_viewSynchistory = findViewById(R.id.btn_viewSynchistory);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        iv_dropdown.setVisibility(View.GONE);
        btnSwitchTimer = findViewById(R.id.btnSwitchTimer);
        btnSwitch = findViewById(R.id.btnSwitch);
        sp_month = findViewById(R.id.sp_month);
        sp_month.setText("Settings");
        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        btn_viewSynchistory.setOnClickListener(this);
        btn_sync.setOnClickListener(this);
        btn_changeAccount.setOnClickListener(this);
        btn_localdrive.setOnClickListener(this);
        btn_localHistory.setOnClickListener(this);

    }

    @Override
    protected void InitPermission() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_settings;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        } else if (v == btn_viewSynchistory) {
            Intent i = new Intent(context, ActivitySyncHistory.class);
            startActivity(i);
        } else if (v == btn_sync) {

            ///Google Drive Sync
            if (mGoogleSignInClient == null) {
                signIn();
            } else {
                getResultsFromApi();
            }
        } else if (v == btn_changeAccount) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Do you want to Change your Account?.You Will be Logged out from your current Account");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            signOut();
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
        } else if (v == btn_localdrive) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Do you want to Backup to your Local Drive?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            new AsyncTaskSaveToLocal().execute();
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
        } else if (v == btn_localHistory) {
            Intent i = new Intent(context, ActivityLocalBackupfiles.class);
            startActivity(i);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            Intent intent = new Intent(this, DriveActivity.class);
            intent.putExtra("ACCOUNT", account);
            startActivity(intent);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        try {
                            //Utils.ShowToast(context,account);
                            tv_accountName.setVisibility(View.VISIBLE);
                            tv_accountName.setText("Logged in as: " + accountName);
                        } catch (Exception e) {
                            tv_accountName.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.e("Check", account.getEmail() + account.getGivenName() + account.getFamilyName());
            Toast.makeText(getApplicationContext(),
                    account.getEmail() + " " + account.getGivenName(), Toast.LENGTH_LONG).show();
            try {
                //Utils.ShowToast(context,account);
                SharedPreferences settings =
                        getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(PREF_ACCOUNT_NAME, account.getEmail());
                editor.apply();
                tv_accountName.setText("Logged in as:" + account.getEmail());
            } catch (Exception e) {
                tv_accountName.setVisibility(View.GONE);
                e.printStackTrace();
            }
            // If successful you can obtain the account info using the getter methods
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("Sign-In", "signInResult:failed code=" + e.getStatusCode() + e);
            Toast.makeText(getApplicationContext(),
                    "Sign In Failed.Try again Later", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void signOut() {
        mGoogleSignInClient.signOut()
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
                        signIn();
                    }
                });
    }

    public void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(getApplicationContext(),
                    "No Network Connection Available", Toast.LENGTH_SHORT).show();
            Log.e(this.toString(), "No network connection available.");
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
                makeDriveRequestTask2 = new MakeDriveRequestTask2(mCredential, ActivitySettings.this);
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
            } else {
                Utils.ShowToast(context, "Failed");
            }
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
                this,
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
            // Request the GET_ACCOUNTS permission via a user dialog
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
                            REQUEST_AUTHORIZATION);
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
                            REQUEST_AUTHORIZATION);
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
            data.put(1, new Object[]{
                    "Date",
                    "Income/Expenses",
                    "Category",
                    "Memo",
                    "Amount",
                    "Payment Mode",
                    "User Name",
                    "DateTime",
                    "File Name",
                    "Tags"});

            if (export_excel.size() > 0) {
                for (int i = 0; i < export_excel.size(); i++) {
                    data.put((i + 2), new Object[]{
                            export_excel.get(i).getDate(),
                            export_excel.get(i).getIncome_Expenses(),
                            export_excel.get(i).getCategory(),
                            export_excel.get(i).getMemo(),
                            String.valueOf(export_excel.get(i).getAmount_()),
                            export_excel.get(i).getPayment_mode(),
                            export_excel.get(i).getUserName(),
                            export_excel.get(i).getTime(),
                            export_excel.get(i).getFileName(),
                            export_excel.get(i).getTagName()});
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





}
