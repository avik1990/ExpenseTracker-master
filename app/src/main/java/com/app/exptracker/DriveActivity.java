package com.app.exptracker;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class DriveActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    GoogleAccountCredential mCredential = null;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String[] SCOPES = {DriveScopes.DRIVE};
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private ProgressBar mProgressBar;
    private TextView mTextView;

    java.io.File file2;
    static String path;

    GoogleSignInAccount account;
    Button uploadFileBtn, createFolderBtn;
    private int calledFrom = 0;

    private static String DB_PATH = "";
    private static String DB_NAME = "expenses.db";// Database name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + getPackageName() + "/databases/";
        }

        account = getIntent().getParcelableExtra("ACCOUNT");
        mTextView = findViewById(R.id.drive_status);
        mProgressBar = findViewById(R.id.progress_bar_drive);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        path = getFilesDir().getAbsolutePath() + "/Template";
        //path of the file that is to be uploaded

        uploadFileBtn = (Button) findViewById(R.id.upload_file_btn);
        createFolderBtn = (Button) findViewById(R.id.create_folder_btn);
        createFolderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calledFrom = 1;
                getResultsFromApi();
                new MakeDriveRequestTask(mCredential, DriveActivity.this).execute();//create app folder in drive

            }
        });

        uploadFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calledFrom = 2;
                getResultsFromApi();
                new MakeDriveRequestTask2(mCredential, DriveActivity.this).execute();
            }
        });

        getResultsFromApi();
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
            if (calledFrom == 2) {
                new MakeDriveRequestTask2(mCredential, DriveActivity.this).execute();//upload q and responses xlsx files
            }
            if (calledFrom == 1) {
                new MakeDriveRequestTask(mCredential, DriveActivity.this).execute();//create app folder in drive
            }
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account1 (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.e(this.toString(), "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");

                } else {
                    getResultsFromApi();
                }
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
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }


    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.e(this.toString(), "Checking if device");
        return (networkInfo != null && networkInfo.isConnected());

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
                DriveActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
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
        }

        @Override
        protected List<String> doInBackground(Void... params) {

            try {
                uploadFile();
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
                            DriveActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.e(this.toString(), "The following error occurred:\n" + mLastError.getMessage());
                }
                Log.e(this.toString(), e + "");
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            mTextView.setText("");
            createFolderBtn.setVisibility(View.INVISIBLE);
            uploadFileBtn.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgressBar.setVisibility(View.GONE);
            uploadFileBtn.setVisibility(View.VISIBLE);
            createFolderBtn.setVisibility(View.VISIBLE);
            mTextView.setText("Task Completed.");

        }

        @Override
        protected void onCancelled() {
            mProgressBar.setVisibility(View.GONE);
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {

                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            DriveActivity.REQUEST_AUTHORIZATION);
                } else {
                    mTextView.setText("The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                mTextView.setText("Request cancelled.");
            }
        }


        private void uploadFile() throws IOException {
            File fileMetadata = new File();
            fileMetadata.setName("Expense Data");
            fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");

            // For mime type of specific file visit Drive Doucumentation
            file2 = new java.io.File(path);
            InputStream inputStream = getResources().openRawResource(R.raw.expense_data);
            try {
                FileUtils.copyInputStreamToFile(inputStream, file2);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileContent mediaContent = new FileContent("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", file2);


            File file = mService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();


            Log.e(this.toString(), "File Created with ID:" + file.getId());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "File created:" + file.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private class MakeDriveRequestTask extends AsyncTask<Void, Void, List<String>> {
        private Drive mService = null;
        private Exception mLastError = null;
        private Context mContext;


        MakeDriveRequestTask(GoogleAccountCredential credential, Context context) {

            mContext = context;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("UsingDriveJavaApi")
                    .build();
            // TODO change the application name to the name of your applicaiton
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                createFolderInDrive();
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
                            DriveActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.e(this.toString(), "The following error occurred:\n" + mLastError.getMessage());
                }
                Log.e(this.toString(), e + "");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            mTextView.setText("");
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgressBar.setVisibility(View.GONE);
            mTextView.setText("Task Completed.");

        }

        @Override
        protected void onCancelled() {
            mProgressBar.setVisibility(View.GONE);
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            DriveActivity.REQUEST_AUTHORIZATION);
                } else {
                    mTextView.setText("The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                mTextView.setText("Request cancelled.");
            }
        }

        private void createFolderInDrive() throws IOException {
            File fileMetadata = new File();
            fileMetadata.setName("Expense Tracker");
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            Log.e("FolderCreated", "Created");
            File file = mService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            System.out.println("Folder ID: " + file.getId());

            Log.e(this.toString(), "Folder Created with ID:" + file.getId());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Folder created:" + file.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
