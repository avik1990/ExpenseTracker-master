package com.app.exptracker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.exptracker.adapter.FileBackupAdapter;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.FileModels;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityLocalBackupfiles extends BaseActivity implements View.OnClickListener, FileBackupAdapter.GetFileNames {
    private static final String TAG = "ActivityLocalBackupfiles";
    Context context;
    TextView sp_month;
    ImageView btn_back, btn_menu;
    String st_year = "";
    DatabaseHelper db;
    ProgressDialog p;
    ImageView iv_dropdown;
    RecyclerView rv_RecyclerView;
    FileModels fileModels;
    FileBackupAdapter fileBackupAdapter;
    List<File> fileArrayList = new ArrayList<>();
    List<File> fList = new ArrayList<>();
    File parentDirectory;

    @Override
    protected void InitListner() {
        context = this;
        sp_month = findViewById(R.id.sp_month);
        st_year = Utils.getCurrentYear();
        parentDirectory = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + Utils.BACKUP_FOLDER_PATH);
        p = new ProgressDialog(context);
        p.setMessage("Please Wait...");
        p.setCancelable(false);
        p.setCanceledOnTouchOutside(false);

        new AsyncGetBackUpFileList().execute();
    }

    private void inflateAdapter() {
      /*  for (int i = 0; i < fileArrayList.size(); i++) {
            Log.e("FIleName", fileArrayList.get(i).getName());
        }*/
        fileBackupAdapter = new FileBackupAdapter(context, fileArrayList, this);
        rv_RecyclerView.setAdapter(fileBackupAdapter);
    }


    @Override
    protected void InitResources() {
        rv_RecyclerView = findViewById(R.id.rv_RecyclerView);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        iv_dropdown.setVisibility(View.GONE);
        sp_month = findViewById(R.id.sp_month);
        sp_month.setText("Backup Files");
        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
    }

    @Override
    protected void InitPermission() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_localbackup;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        }
    }

    @Override
    public void returnedFileName(String month, String sort) {
    }

    private class AsyncGetBackUpFileList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getListFiles(parentDirectory);
            } catch (Exception e) {
                e.printStackTrace();
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
            inflateAdapter();
            p.dismiss();
        }
    }

    List<File> getListFiles(File parentDir) {
        fileArrayList = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                fileArrayList.addAll(getListFiles(file));
            } else {
                fileArrayList.add(file);
            }
        }
        return fileArrayList;
    }
}
