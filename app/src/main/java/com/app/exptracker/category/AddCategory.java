package com.app.exptracker.category;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.adapter.SortCategoryAdapter;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.OnStartDragListener;
import com.app.exptracker.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class AddCategory extends BaseActivity implements View.OnClickListener, OnStartDragListener, SortCategoryAdapter.GetSortedList {

    private static final String TAG = "AddCategory";
    FloatingActionButton fab_add;
    Context context;
    DatabaseHelper db;
    List<ExcelDataModel> list_exceldata_swap = new ArrayList<>();
    ProgressDialog p;
    TextView tv_expenses, tv_income;
    TextView sp_month, tv_currentyear;
    ItemTouchHelper mItemTouchHelper;
    ImageView iv_dropdown, btn_menu, btn_back;
    EditText et_category;
    Spinner sp_transtype;
    String st_sp_transtype = "";
    long returnedId;

    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        fab_add.setOnClickListener(this);

        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);


        sp_transtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                st_sp_transtype = sp_transtype.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    @Override
    protected void InitResources() {
        sp_transtype = findViewById(R.id.sp_transtype);
        et_category = findViewById(R.id.et_category);
        tv_currentyear = findViewById(R.id.tv_currentyear);
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
        //sp_month.setVisibility(View.GONE);
        sp_month.setText("Add Categories");
    }

    @Override
    protected void InitPermission() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_addcategory;
    }

    @Override
    public void onClick(View v) {
        if (v == fab_add) {
            if (et_category.getText().toString().trim().isEmpty()) {
                Utils.ShowToast(context, "Add Category Name");
                return;
            }

            if (st_sp_transtype.isEmpty()) {
                Utils.ShowToast(context, "Select Transaction Type");
                return;
            }

            ExcelDataModel excelDataModel = new ExcelDataModel();
            excelDataModel.setCategory(et_category.getText().toString().trim());
            excelDataModel.setIncome_Expenses(st_sp_transtype);
            excelDataModel.setCatIsAdded("1");
            returnedId = db.addCategory(excelDataModel);
            //Utils.ShowToast(context, "" + returnedId);
            if (returnedId < 0) {
                Utils.ShowToast(context, "Category Already Exists");
            } else {
                finish();
                Utils.ShowToast(context, "Category Added");
            }

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
    public void sortedList(List<ExcelDataModel> mPersonList) {
        list_exceldata_swap = mPersonList;
        for (int i = 0; i < mPersonList.size(); i++) {
            Log.d("SwapCat", mPersonList.get(i).getCategory());
        }
    }

    private class AsyncSwap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            db.UpdateSortFieldSwap(list_exceldata_swap);
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
            Utils.ShowToast(context, "Sorted Successfully");
            finish();
        }
    }


}