package com.app.exptracker.category;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.OnStartDragListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends BaseActivity implements View.OnClickListener, OnStartDragListener, ExpCategoryAdapter.ChildClicked {

    private static final String TAG = "CategoryActivity";
    FloatingActionButton fab_add;
    Context context;
    ExpandableListView expandableListView;
    DatabaseHelper db;
    List<ExcelDataModel> list_exceldata = new ArrayList<>();
    ProgressDialog p;
    TextView tv_expenses, tv_income;
    TextView sp_month, tv_currentyear;
    ItemTouchHelper mItemTouchHelper;
    ImageView iv_dropdown, btn_menu, btn_back;
    ExpCategoryAdapter expandableListAdapter;
    private static final int RESULT_UPDATED = 999;


    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        fab_add.setOnClickListener(this);

        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);

        inflateData();

    }

    public void inflateData() {
        new FetchData().execute();
    }


    @Override
    protected void InitResources() {
        tv_currentyear = findViewById(R.id.tv_currentyear);
        expandableListView = findViewById(R.id.expandableListView);
        tv_expenses = findViewById(R.id.tv_expenses);
        tv_income = findViewById(R.id.tv_income);
        fab_add = findViewById(R.id.fab_add);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        iv_dropdown.setVisibility(View.GONE);
        sp_month = findViewById(R.id.sp_month);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        sp_month.setText("Categories");
    }

    @Override
    protected void InitPermission() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_category;
    }

    @Override
    public void onClick(View v) {
        if (v == fab_add) {
            Intent i = new Intent(context, AddCategory.class);
            startActivityForResult(i, RESULT_UPDATED);
        } else if (v == btn_back) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }



    @Override
    public void childClicked(int id) {

    }

    public class FetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_exceldata = db.GetAllCategoryDataByGrouping();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.show();

        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_exceldata.size() > 0) {

                setUpAdapter();
                expandableListView.setOnTouchListener((view, event) -> {
                    int eventaction = event.getAction();

                    switch (eventaction) {
                        case MotionEvent.ACTION_DOWN:
                            fab_add.hide();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            fab_add.hide();
                            break;

                        case MotionEvent.ACTION_UP:
                            fab_add.show();
                            break;
                    }
                    return false;
                });
            }
            p.dismiss();
        }
    }

    private void setUpAdapter() {
        if (list_exceldata.size() > 0) {
            expandableListView.setVisibility(View.VISIBLE);
            expandableListAdapter = new ExpCategoryAdapter(context, list_exceldata, this);
            expandableListView.setAdapter(expandableListAdapter);
            expandableListView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_UPDATED:
                new AsyncTaskUpdateList().execute();
                break;
        }
    }


    private class AsyncTaskUpdateList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_exceldata = db.GetAllCategoryDataByGrouping();
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
            if (expandableListAdapter != null) {
                expandableListAdapter.updateReceiptsList(list_exceldata);
            }
            p.dismiss();
        }
    }
}