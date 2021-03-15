package com.app.exptracker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.exptracker.adapter.SyncStatusAdapter;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.Sync;
import com.app.exptracker.utility.BaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class ActivitySyncHistory extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    Context context;
    TextView sp_month;
    ImageView btn_back, btn_menu;

    DatabaseHelper db;
    ProgressDialog p;
    ImageView iv_dropdown;
    RecyclerView rvSync;
    SyncStatusAdapter syncStatusAdapter;
    List<Sync> listSync = new ArrayList<>();
    TextView tv_syncHeader;

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);

        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);


        fetchSyncData();

    }

    private void fetchSyncData() {
        new AsyncgetSyncData().execute();
    }


    @Override
    protected void InitResources() {
        rvSync = findViewById(R.id.rvSync);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        tv_syncHeader = findViewById(R.id.tv_syncHeader);
        iv_dropdown.setVisibility(View.GONE);
        sp_month = findViewById(R.id.sp_month);
        sp_month.setText("Sync History");
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
        return R.layout.activity_synchistory;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private class AsyncgetSyncData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            listSync = db.getAllSyncData();
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
            if (listSync.size() > 0) {
                tv_syncHeader.setVisibility(View.GONE);
                syncStatusAdapter = new SyncStatusAdapter(context, listSync);
                rvSync.setAdapter(syncStatusAdapter);
            } else {
                tv_syncHeader.setVisibility(View.GONE);
            }
        }
    }
}
