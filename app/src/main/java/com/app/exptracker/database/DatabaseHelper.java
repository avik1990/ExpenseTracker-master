package com.app.exptracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.exptracker.model.CategoryModel;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.model.Sync;
import com.app.exptracker.model.TransactionType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = "DatabaseHelper"; // Tag just for the LogCat window
    //destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String DB_NAME = "expenses.db";// Database name
    private SQLiteDatabase mDataBase;
    private final Context mContext;

    private static final String DATABASE_ALTER_TEAM = "ALTER TABLE " + ExcelDataModel.TABLE_TRANSACTION + " ADD COLUMN " + ExcelDataModel.COLUMN_MONTH + " string;";
    private static final String DATABASE_ALTER_Category = "alter table tbl_category ADD COLUMN sort varchar";
    private static final String DATABASE_ALTER_tb_trans = "alter table tbl_transactions ADD COLUMN payment_mode varchar";
    private static final String DATABASE_ALTER_temp = "alter table tbl_transaction_temp ADD COLUMN paymentMode varchar";

    private static final String DATABASE_TABLE_TRANSACTION = "ALTER TABLE " + ExcelDataModel.TABLE_TRANSACTION + " ADD COLUMN " + ExcelDataModel.COLUMN_CAPTURE_DATE + " string;";
    private static final String DATABASE_TABLE_TRANSACTION1 = "ALTER TABLE " + ExcelDataModel.TABLE_TRANSACTION + " ADD COLUMN " + ExcelDataModel.COLUMN_MODIFIED_DATE + " string;";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 18);// 1? Its database Version
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    public void createDataBase() throws IOException {
        //If the database does not exist, copy it from the assets.
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    public boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    //copy the database from assets
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;

        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }

        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String syncTable = "CREATE TABLE IF NOT EXISTS tbl_syncstatus (sync_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sync_date TEXT," +
                "sync_fileid TEXT," +
                "sync_filename TEXT," +
                "sync_status TEXT," +
                "colA TEXT," +
                "colB TEXT," +
                "colC TEXT," +
                "colD TEXT)";

        String dummy1 = "CREATE TABLE IF NOT EXISTS tbl_dummy(dummy_id1 INTEGER PRIMARY KEY AUTOINCREMENT," +
                "colA TEXT unique COLLATE NOCASE," +
                "colB TEXT," +
                "colC TEXT," +
                "colD TEXT," +
                "colE TEXT," +
                "colF TEXT," +
                "colG TEXT," +
                "colH TEXT," +
                "colI TEXT," +
                "colJ TEXT)";

        String dummy2 = "CREATE TABLE IF NOT EXISTS tbl_dummy2(dummy_id2 INTEGER PRIMARY KEY AUTOINCREMENT," +
                "colA TEXT," +
                "colB TEXT," +
                "colC TEXT," +
                "colD TEXT," +
                "colE TEXT," +
                "colF TEXT," +
                "colG TEXT," +
                "colH TEXT," +
                "colI TEXT," +
                "colJ TEXT)";

        String dummy3 = "CREATE TABLE IF NOT EXISTS tbl_dummy3(dummy_id3 INTEGER PRIMARY KEY AUTOINCREMENT," +
                "colA TEXT," +
                "colB TEXT," +
                "colC TEXT," +
                "colD TEXT," +
                "colE TEXT," +
                "colF TEXT," +
                "colG TEXT," +
                "colH TEXT," +
                "colI TEXT," +
                "colJ TEXT)";

        String dummy4 = "CREATE TABLE IF NOT EXISTS tbl_dummy4(dummy_id4 INTEGER PRIMARY KEY AUTOINCREMENT," +
                "colA TEXT," +
                "colB TEXT," +
                "colC TEXT," +
                "colD TEXT," +
                "colE TEXT," +
                "colF TEXT," +
                "colG TEXT," +
                "colH TEXT," +
                "colI TEXT," +
                "colJ TEXT)";

        try {
            db.execSQL(syncTable);
        } catch (Exception e) {

        }

        try {
            db.execSQL(dummy1);
        } catch (Exception e) {

        }

        try {
            db.execSQL(dummy2);
            Log.e("DataBaseChanged", "Changed1");
        } catch (Exception e) {

        }

        try {
            db.execSQL(dummy3);
        } catch (Exception e) {
        }

        try {
            db.execSQL(dummy4);
        } catch (Exception e) {
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DATABASE_ALTER_TEAM);
            db.execSQL(DATABASE_ALTER_Category);
            db.execSQL(DATABASE_ALTER_tb_trans);
            db.execSQL(DATABASE_ALTER_temp);
            db.execSQL(DATABASE_TABLE_TRANSACTION);
            db.execSQL(DATABASE_TABLE_TRANSACTION1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("DataBaseChanged", "Changed");
        onCreate(db);
    }

    public void InsertExcelDataTO_TempTable(List<ExcelDataModel> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < listexceldata.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("tdate", listexceldata.get(i).getDate());
            values.put("ttrans_type", listexceldata.get(i).getIncome_Expenses());
            values.put("tcategory", listexceldata.get(i).getCategory());
            values.put("tmemo", listexceldata.get(i).getMemo());
            values.put("tamount", listexceldata.get(i).getAmount());
            values.put("paymentMode", listexceldata.get(i).getPayment_mode());
            values.put("time", listexceldata.get(i).getTime());
            values.put("tfileName", listexceldata.get(i).getFileName());
            values.put("tcolA", listexceldata.get(i).getTagName());
            if (!listexceldata.get(i).getUserName().isEmpty()) {
                values.put("user_name", listexceldata.get(i).getUserName());
            } else {
                values.put("user_name", "Myself");
            }
            db.insert(ExcelDataModel.TABLE_TEMP_NAME, null, values);
        }
        db.close();
    }

    public void InsertExcelDataTO_TagTable(List<String> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < listexceldata.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("ColA", listexceldata.get(i));
            db.insert("tbl_dummy", null, values);
        }
        db.close();
    }

    public void truncateTempTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_TEMP_NAME);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        db.close();
    }

    public void truncateTagTransaction() {
        SQLiteDatabase db = this.getWritableDatabase();
        String del = "DELETE FROM tbl_dummy2";
        Log.e("DeleteQuery", del);
        db.execSQL(del);
        db.close();
    }


    public List<ExcelDataModel> getDistinctCategory() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + ExcelDataModel.TABLE_TEMP_NAME + " GROUP by " + ExcelDataModel.category;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setCategory(cursor.getString(cursor.getColumnIndex("tcategory")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("ttrans_type")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }


    public List<ExcelDataModel> getDistinctUsers() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "SELECT  distinct user_name FROM " + ExcelDataModel.TABLE_TEMP_NAME;
        Log.d("UserQuery", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                //note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("ttrans_type")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }


    public void InsertCategoryData(List<ExcelDataModel> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (int i = 0; i < listexceldata.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("cat_name", listexceldata.get(i).getCategory());
                values.put("trans_type", listexceldata.get(i).getIncome_Expenses());
                values.put("isAdded", "1");
                db.insert(ExcelDataModel.TABLE_CATEGORY, null, values);
            }
        } catch (Exception e) {
        }
        db.close();
    }

    public void InsertUserData(List<ExcelDataModel> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (int i = 0; i < listexceldata.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("user_name", listexceldata.get(i).getUserName());
                db.insert(ExcelDataModel.TABLE_USER, null, values);
            }
        } catch (Exception e) {
        }

        db.close();
    }

    public List<ExcelDataModel> getAllToDosByTag(String tag_name) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "SELECT  trans_type_id from tbl_transaction_type where trans_type='" + tag_name + "'";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setId(cursor.getString(cursor.getColumnIndex("trans_type_id")));
                excelDataModel.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }


    public List<ExcelDataModel> GetAllCAtegoryData() {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "select *, caty.cat_id, caty.cat_name, count(trans.cat_id) as total from tbl_category caty left join tbl_transactions trans " +
                "on caty.cat_id = trans.cat_id  group by cat_name order by total DESC";
        Log.e("CategoryQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                excelDataModel.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                excelDataModel.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                excelDataModel.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }


    public List<ExcelDataModel> GetAllCAtegoryDataByName() {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "select * from tbl_category order by trans_type asc,cat_name asc";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                excelDataModel.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                excelDataModel.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                excelDataModel.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }


    public List<ExcelDataModel> GetAllUsers() {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "select * from tbl_users order by user_name asc";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                excelDataModel.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }


    public void UpdateTempTransactionTable(String cat, String cat_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cat_id", Integer.parseInt(cat_id));
        //db.update(ExcelDataModel.TABLE_TEMP_NAME, values, "ttrans_type="'" + tag_name + "'", null);
        db.update(ExcelDataModel.TABLE_TEMP_NAME, values, "tcategory='" + cat + "'", null);
        db.close();
    }

    public void UpdateTempCatDataAfterDataImport() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "UPDATE tbl_transaction_temp " +
                "SET cat_id = (SELECT cat_id" +
                "              FROM tbl_category" +
                "              WHERE (tbl_transaction_temp.tcategory = tbl_category.cat_name)" +
                "              AND (tbl_transaction_temp.ttrans_type = tbl_category.trans_type))" +
                "WHERE EXISTS (SELECT *" +
                "              FROM tbl_category" +
                "              WHERE (tbl_transaction_temp.tcategory = tbl_category.cat_name)" +
                "              AND (tbl_transaction_temp.ttrans_type = tbl_category.trans_type))";

        Log.d("QueryCatAndroid", selectQuery);
        db.execSQL(selectQuery);
        db.close();
    }


    public void UpdateTempTagsWithIdAfterDataImport() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "UPDATE tbl_dummy3 " +
                "SET colC = (SELECT dummy_id1" +
                "              FROM tbl_dummy" +
                "              WHERE (tbl_dummy3.colB = tbl_dummy.colA))" +
                "WHERE EXISTS (SELECT *" +
                "              FROM tbl_dummy" +
                "              WHERE (tbl_dummy3.colB = tbl_dummy.colA))";

        Log.d("QueryCatAndroid", selectQuery);
        db.execSQL(selectQuery);
        db.close();
    }

    public void insertDataintoTagTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "INSERT INTO tbl_dummy2 " +
                "(colA" +
                ",colB) " +
                "SELECT " +
                "colA,colC " +
                "FROM " +
                "tbl_dummy3 ";
        Log.d("QueryCatAndroid", selectQuery);
        db.execSQL(selectQuery);
        db.close();
    }

    public void UpdateTempUserDataAfterDataImport() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "UPDATE tbl_transaction_temp " +
                "SET user_id = (SELECT user_id" +
                "              FROM tbl_users" +
                "              WHERE (tbl_transaction_temp.user_name = tbl_users.user_name))" +
                "WHERE EXISTS (SELECT *" +
                "              FROM tbl_users" +
                "               WHERE (tbl_transaction_temp.user_name = tbl_users.user_name))";

        Log.d("QueryUserAndroid", selectQuery);
        db.execSQL(selectQuery);
        db.close();
    }


    public List<ExcelDataModel> getAllTempDataWIthID() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_transaction_temp";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("tdate")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("ttrans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("tcategory")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("tmemo")));
                note.setAmount(cursor.getString(cursor.getColumnIndex("tamount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                note.setPayment_mode(cursor.getString(cursor.getColumnIndex("paymentMode")));
                note.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
                note.setTime(cursor.getString(cursor.getColumnIndex("time")));
                note.setFileName(cursor.getString(cursor.getColumnIndex("tfileName")));
                note.setTagName(cursor.getString(cursor.getColumnIndex("tcolA")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }


    public void InsertDataTO_TransactionTable(List<ExcelDataModel> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < listexceldata.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("date", listexceldata.get(i).getDate());
            values.put("trans_type", listexceldata.get(i).getIncome_Expenses());
            values.put("cat_id", listexceldata.get(i).getId());
            values.put("memo", listexceldata.get(i).getMemo());
            values.put("time", listexceldata.get(i).getTime());
            values.put("fileName", listexceldata.get(i).getFileName());
            values.put("column_2", listexceldata.get(i).getTagName());
            //Log.d("FIleNameTrans", listexceldata.get(i).getFileName()); /////loggingggg
            values.put("amount", listexceldata.get(i).getAmount().replace("-", ""));

            if (listexceldata.get(i).getPayment_mode().isEmpty()) {
                values.put("payment_mode", "Cash");
            } else {
                values.put("payment_mode", listexceldata.get(i).getPayment_mode());
            }

            try {
                if (listexceldata.get(i).getUserId().isEmpty()) {
                    values.put("user_id", "1");
                } else {
                    values.put("user_id", listexceldata.get(i).getUserId());
                }
            } catch (Exception e) {
                values.put("user_id", "1");
            }

            if (!listexceldata.get(i).getDate().isEmpty()) {
                String d[] = listexceldata.get(i).getDate().split("-");
                String mon = d[2];
                values.put("month", mon);
            } else {
                values.put("month", "");
            }
            db.insert("tbl_transactions", null, values);
        }
        db.close();
    }


    public List<ExcelDataModel> GetAllFinalTransactionData(String startDate, String endDate) {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select group_concat(d.colA) as Tags,a.fileName,a.time,a.trans_id,a.date,a.trans_type,a.cat_id,a.memo,a.amount,a.payment_mode, " +
                "b.cat_name,b.cat_iconname,u.user_name from tbl_transactions " +
                "a join tbl_category b on a.cat_id=b.cat_id " +
                "join tbl_users u on a.user_id=u.user_id " +
                "LEFT join tbl_dummy2 d2 on a.trans_id=d2.colA " +
                "left join tbl_dummy d on d.dummy_id1=d2.colB " +
                "WHERE 1=1 and a.date BETWEEN '" + startDate + "' AND '" + endDate + "'" +
                " group by a.trans_id ORDER BY strftime('%Y-%m-%d',a.date) desc";

        Log.d("ExportQuery", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                note.setAmount_(cursor.getDouble(cursor.getColumnIndex("amount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                note.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                note.setPayment_mode(cursor.getString(cursor.getColumnIndex("payment_mode")));
                note.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                note.setTime(cursor.getString(cursor.getColumnIndex("time")));
                note.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                note.setTagName(cursor.getString(cursor.getColumnIndex("Tags")));
                try {
                    if (note.getUserName().isEmpty()) {
                        note.setUserName("Myself");
                    }
                } catch (Exception e) {
                    note.setUserName("Myself");
                }

                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return listdistinctdata;
    }

    public List<ExcelDataModel> GetAllFinalTransactionDataSearch(String search_text, String st_cat, String trans_type, String stratDate, String endDate,
                                                                 String cat_id, String userId, String tagIds) {

        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String where = "a.trans_id > 0";

        if (search_text.length() > 0) where += " AND memo LIKE '%" + search_text + "%'";
        if (cat_id.length() > 0) where += " AND a.cat_id='" + cat_id + "'";
        if (trans_type.length() > 0) where += " AND b.trans_type='" + trans_type + "'";
        if (userId.length() > 0) where += " AND a.user_id='" + userId + "'";
        if (tagIds.length() > 0) where += " AND d.dummy_id1 in (" + tagIds + ")";
        /*if (search_text.length() > 0) where1 += " AND memo LIKE '%" + search_text + "%'";
        if (trans_type.length() > 0) where1 += " AND trans_type='" + trans_type + "'";
        if (userId.length() > 0) where1 += " AND user_id='" + userId + "'";*/
        //if (tagIds.length() > 0) where1 += " AND tagIds='" + tagIds + "'";
        String selectQuery = "select group_concat(d.colA) as Tags,a.fileName,a.time,a.trans_id,a.date,a.trans_type,a.cat_id," +
                "a.memo,a.amount,a.payment_mode, " +
                "b.cat_name,b.cat_iconname,u.user_name from " +
                "tbl_transactions " +
                "a join tbl_category b on a.cat_id=b.cat_id " +
                "join tbl_users u on a.user_id=u.user_id " +
                "LEFT join tbl_dummy2 d2 on a.trans_id=d2.colA " +
                "left join tbl_dummy d on d.dummy_id1=d2.colB " +
                "WHERE " + where + " " +
                "and a.date BETWEEN '" + stratDate + "' AND '" + endDate + "'" +
                " group by a.trans_id ORDER BY strftime('%Y-%m-%d',a.date) desc";

        Log.d("ExportQuerySearch", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                note.setAmount_(cursor.getDouble(cursor.getColumnIndex("amount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                note.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                note.setPayment_mode(cursor.getString(cursor.getColumnIndex("payment_mode")));
                note.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                note.setTime(cursor.getString(cursor.getColumnIndex("time")));
                note.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                note.setTagName(cursor.getString(cursor.getColumnIndex("Tags")));
                try {
                    if (note.getUserName().isEmpty()) {
                        note.setUserName("Myself");
                    }
                } catch (Exception e) {
                    note.setUserName("Myself");
                }

                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return listdistinctdata;
    }


    public List<ExcelDataModel> GetAllTransaction() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select group_concat(d.colA) as Tags,a.fileName,a.time,a.trans_id,a.date,a.trans_type,a.cat_id,a.memo,a.amount,a.payment_mode,b.cat_name,b.cat_iconname,u.user_name \n" +
                "from tbl_transactions " +
                "a join tbl_category b on a.cat_id=b.cat_id " +
                "join tbl_users u on a.user_id=u.user_id " +
                "LEFT join tbl_dummy2 d2 on a.trans_id=d2.colA " +
                "LEFT join tbl_dummy d on d.dummy_id1=d2.colB " +
                "WHERE 1=1 " +
                " group by a.trans_id ORDER BY strftime('%Y-%m-%d',a.date) desc";

        Log.d("ExportQueryAll123", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                note.setAmount_(cursor.getDouble(cursor.getColumnIndex("amount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                note.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                note.setPayment_mode(cursor.getString(cursor.getColumnIndex("payment_mode")));
                note.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                note.setTime(cursor.getString(cursor.getColumnIndex("time")));
                note.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                note.setTagName(cursor.getString(cursor.getColumnIndex("Tags")));
                try {
                    if (note.getUserName().isEmpty()) {
                        note.setUserName("Myself");
                    }
                } catch (Exception e) {
                    note.setUserName("Myself");
                }

                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return listdistinctdata;
    }


    public List<ExcelDataModel> GetAllFinalTransactionDataGroup() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_transactions a LEFT join tbl_category b where a.cat_id=b.cat_id group by cat_name LIMIT 10";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                note.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }


    public Multimap<String, String> getTagsInMap() {
        Multimap<String, String> listdistinctdata = ArrayListMultimap.create();
        String selectQuery = "select trans_id,column_2 from tbl_transactions";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex("column_2")) != null) {
                    if (!cursor.getString(cursor.getColumnIndex("column_2")).isEmpty()) {
                        String a[] = cursor.getString(cursor.getColumnIndex("column_2")).split(",");
                        for (int i = 0; i < a.length; i++) {
                            listdistinctdata.put(cursor.getString(cursor.getColumnIndex("trans_id")), a[i]);
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }

    public StringBuilder getTagsInMap1() {
        StringBuilder val = new StringBuilder();
        String selectQuery = "select trans_id,column_2 from tbl_transactions";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex("column_2")) != null) {
                    if (!cursor.getString(cursor.getColumnIndex("column_2")).isEmpty()) {
                        String a[] = cursor.getString(cursor.getColumnIndex("column_2")).split(",");
                        for (int i = 0; i < a.length; i++) {
                            val.append(cursor.getString(cursor.getColumnIndex("trans_id")));
                            val.append(":");
                            val.append(a[i]);
                            val.append("::");
                        }
                        //listdistinctdata.put(cursor.getString(cursor.getColumnIndex("trans_id")), cursor.getString(cursor.getColumnIndex("column_2")));
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return val;
    }


    public ExcelDataModel GetExpensesData(String transtype, String current_month) {
        SQLiteDatabase db = this.getWritableDatabase();
        ExcelDataModel excelDataModel;
        String query = "SELECT SUM(amount) as Total,count(*) as count FROM tbl_transactions where trans_type='" + transtype + "' " +
                "and strftime('%Y-%m', date) = '" + current_month + "'";
        Log.d("SumQuery", query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(cursor.getDouble(cursor.getColumnIndex("Total")));
            excelDataModel.setCount_(cursor.getInt(cursor.getColumnIndex("count")));
            Log.d("Count1", "" + cursor.getInt(cursor.getColumnIndex("count")));
        } else {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(0);
            excelDataModel.setCount_(0);
        }
        cursor.close();
        db.close();
        return excelDataModel;
    }


    public ExcelDataModel GetExpensesDataDateRange(String transtype, String startDate, String endDate, String memo, String cat_id, String userID, String tagId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ExcelDataModel excelDataModel;
        if (transtype.isEmpty()) {
            transtype = "";
        }
        String where1 = "";
        if (memo.length() > 0) where1 += " AND memo LIKE '%" + memo + "%'";
        if (transtype.length() > 0) where1 += " AND trans_type='" + transtype + "'";
        try {
            if (cat_id.length() > 0) where1 += " AND cat_id='" + cat_id + "'";
        } catch (Exception e) {
        }

        try {
            if (userID.length() > 0) where1 += " AND user_id='" + userID + "'";
        } catch (Exception e) {
        }

        //String query = "SELECT SUM(amount) as Total,count(*) as count FROM tbl_transactions where trans_type='" + transtype + "' and date like '%" + current_month + "%'";
        /*String query = "SELECT SUM(amount) as Total,count(*) as count FROM tbl_transactions where trans_type='" + transtype + "' " +
                "and DATE(date) BETWEEN '" + startDate + "' AND '" + endDate + "' " + where1 + " ";*/

        String query = "";
        if (!tagId.isEmpty()) {
            query = "Select sum(tt.amount) as Total, COUNT(*) as count from(select DISTINCT(trns.trans_id), trns.amount from tbl_transactions trns " +
                    "left join tbl_dummy2 d2 on d2.colA=trns.trans_id where trans_type='" + transtype + "' " +
                    "and DATE(date) BETWEEN '" + startDate + "' AND '" + endDate + "' " + where1 +
                    "and d2.colB in (" + tagId + ")) as tt";
        } else {
            query = "SELECT SUM(amount) as Total,count(*) as count FROM tbl_transactions where trans_type='" + transtype + "' " +
                    "and DATE(date) BETWEEN '" + startDate + "' AND '" + endDate + "' " + where1 + " ";
        }


        Log.d("searchTotal", query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(cursor.getDouble(cursor.getColumnIndex("Total")));
            excelDataModel.setCount_(cursor.getInt(cursor.getColumnIndex("count")));
            Log.d("Count1", "" + cursor.getInt(cursor.getColumnIndex("count")));
        } else {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(0);
            excelDataModel.setCount_(0);
        }
        cursor.close();
        db.close();
        return excelDataModel;
    }


    public ExcelDataModel GetExpensesDataModeWise(String transtype, String current_month, String paymentMode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ExcelDataModel excelDataModel;
        String query = "";

        if (paymentMode.isEmpty()) {
            query = "SELECT SUM(amount) as Total FROM tbl_transactions where trans_type='" + transtype + "' and date like '%" + current_month + "%'";
        } else {
            query = "SELECT SUM(amount) as Total FROM tbl_transactions where trans_type='" + transtype + "' and payment_mode='" + paymentMode + "' and date like '%" + current_month + "%'";
        }

        Log.d("GetExpensesDataModeWise", query);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(cursor.getDouble(cursor.getColumnIndex("Total")));
            //excelDataModel.setCount_(cursor.getInt(cursor.getColumnIndex("count")));
            //Log.d("Count", "" + cursor.getInt(cursor.getColumnIndex("count")));
        } else {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(0);
            // excelDataModel.setCount_(0);
        }
        cursor.close();
        db.close();
        return excelDataModel;
    }


    public ExcelDataModel GetExpensesDataModeWiseWithId(String transtype, String current_month, String paymentMode, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ExcelDataModel excelDataModel;
        String query = "";

        if (paymentMode.isEmpty()) {
            query = "SELECT SUM(amount) as Total FROM tbl_transactions where trans_type='" + transtype + "' and  cat_id='" + id + "' and strftime('%Y-%m', date) = '" + current_month + "'";
        } else {
            query = "SELECT SUM(amount) as Total FROM tbl_transactions where trans_type='" + transtype + "' and payment_mode='" + paymentMode + "' and  cat_id='" + id + "' and strftime('%Y-%m', date) = '" + current_month + "'";
        }

        Log.d("GetExpensesDataMode", query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(cursor.getDouble(cursor.getColumnIndex("Total")));
            //excelDataModel.setCount_(cursor.getInt(cursor.getColumnIndex("count")));
            //Log.d("Count", "" + cursor.getInt(cursor.getColumnIndex("count")));
        } else {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(0);
            // excelDataModel.setCount_(0);
        }
        cursor.close();
        db.close();
        return excelDataModel;
    }


    public ExcelDataModel GetExpensesDataModeDayWise(String transtype, String current_month, String paymentMode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ExcelDataModel excelDataModel;
        String query = "";

        if (paymentMode.isEmpty()) {
            query = "SELECT SUM(amount) as Total FROM tbl_transactions where trans_type='" + transtype + "' and date = '" + current_month + "'";
        } else {
            query = "SELECT SUM(amount) as Total FROM tbl_transactions where trans_type='" + transtype + "' and payment_mode='" + paymentMode + "' and date = '" + current_month + "'";
        }

        Log.d("GetExpensesDataMode", query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(cursor.getDouble(cursor.getColumnIndex("Total")));
        } else {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(0);
            // excelDataModel.setCount_(0);
        }
        cursor.close();
        db.close();
        return excelDataModel;
    }


    public ExcelDataModel GetExpensesDataIdWise(String transtype, String current_month, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ExcelDataModel excelDataModel;
        String query = "SELECT SUM(amount) as Total,count(*) as count FROM tbl_transactions where trans_type='" + transtype + "' " +
                "and cat_id='" + id + "' and strftime('%Y-%m', date) = '" + current_month + "'";
        Log.d("query32112121", query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(cursor.getDouble(cursor.getColumnIndex("Total")));
            excelDataModel.setCount_(cursor.getInt(cursor.getColumnIndex("count")));
            //Log.d("Count", "" + cursor.getInt(cursor.getColumnIndex("count")));
        } else {
            excelDataModel = new ExcelDataModel();
            excelDataModel.setAmount_(0);
            excelDataModel.setCount_(0);
        }
        cursor.close();
        db.close();
        return excelDataModel;
    }


    /*public List<ExcelDataModel> GetExpensesDayWiseExpInc(String stDate) {
        List<ExcelDataModel> listExcelmodel=new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery1 = "select tr.fileName,tr.date,tr.time,tr.trans_type, b.cat_name,tr.memo,amount,tr.trans_id,b.cat_iconname,tr.payment_mode" +
                " from tbl_transactions tr join tbl_category " +
                " b where tr.cat_id=b.cat_id and date='" + stDate + "' " +
                " ORDER BY datetime(tr.time) desc ,tr.trans_id desc";

        Log.d("asdadad", selectQuery1);
        Cursor cursor2 = db.rawQuery(selectQuery1, null);
        if (cursor2.moveToFirst()) {
            do{
                ExcelDataModel prod_subcat = new ExcelDataModel();
                prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                prod_subcat.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                prod_subcat.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                prod_subcat.setMemo(cursor2.getString(cursor2.getColumnIndex("memo")));
                prod_subcat.setAmount(cursor2.getString(cursor2.getColumnIndex("amount")));
                prod_subcat.setId(cursor2.getString(cursor2.getColumnIndex("trans_id")));
                prod_subcat.setIcon_name(cursor2.getString(cursor2.getColumnIndex("cat_iconname")));
                prod_subcat.setPayment_mode(cursor2.getString(cursor2.getColumnIndex("payment_mode")));
                //prod_subcat.setUserId(cursor2.getString(cursor2.getColumnIndex("user_id")));
                prod_subcat.setTime(cursor2.getString(cursor2.getColumnIndex("time")));
                prod_subcat.setFileName(cursor2.getString(cursor2.getColumnIndex("fileName")));
                listExcelmodel.add(prod_subcat);
            }while (cursor2.moveToNext());

        }
        cursor2.close();
        db.close();
        return listExcelmodel;
    }*/


    public List<ExcelDataModel> GetTransactionDatafsfsdfdsfs(String stDate) {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select tr.fileName,tr.date,tr.time,tr.trans_type, b.cat_name,tr.memo,amount,tr.trans_id,b.cat_iconname,tr.payment_mode" +
                " from tbl_transactions tr join tbl_category " +
                " b where tr.cat_id=b.cat_id and date='" + stDate + "' " +
                " ORDER BY datetime(tr.time) desc ,tr.trans_id desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                note.setAmount_(cursor.getDouble(cursor.getColumnIndex("amount")));
                note.setId(cursor.getString(cursor.getColumnIndex("trans_id")));
                note.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                note.setPayment_mode(cursor.getString(cursor.getColumnIndex("payment_mode")));
                //prod_subcat.setUserId(cursor2.getString(cursor2.getColumnIndex("user_id")));
                note.setTime(cursor.getString(cursor.getColumnIndex("time")));
                note.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }


    public List<ExcelDataModel> GetTransactionData() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_transactions where cat_id='1' and trans_type='Income'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                // note.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                note.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }

    public List<ExcelDataModel> getTransactionDetails(String current_month) {
        List<ExcelDataModel> listProd = new ArrayList<>();
        Log.d("CurrentMonthDB", current_month);
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String selectQuery = "select f.date,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' and date=f.date)exp, " +
                    "(select sum(amount) from tbl_transactions where trans_type='Income' and date=f.date)inc from tbl_transactions f " +
                    "where strftime('%Y-%m', f.date) = '" + current_month + "' GROUP by f.date ORDER BY strftime('%s', f.date) desc";

            Log.d("FirstQueryAllTrans12344", selectQuery);
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    ExcelDataModel foc_cat = new ExcelDataModel();
                    String date = c.getString(c.getColumnIndex("date"));
                    String income = c.getString(c.getColumnIndex("inc"));
                    String expense = c.getString(c.getColumnIndex("exp"));

                    String selectQuery1 = "select tr.fileName,tr.date,tr.time,tr.trans_type, b.cat_name,tr.memo,amount,tr.trans_id,b.cat_iconname,tr.payment_mode" +
                            " from tbl_transactions tr join tbl_category " +
                            " b where tr.cat_id=b.cat_id and date='" + date + "' " +
                            " ORDER BY datetime(tr.time) desc ,tr.trans_id desc";

                    Log.d("SecondQueryAllDetails", selectQuery1);
                    Cursor cursor2 = db.rawQuery(selectQuery1, null);
                    if (cursor2.moveToFirst()) {
                        List<ExcelDataModel> subCat_list = new ArrayList<>();
                        do {
                            ExcelDataModel prod_subcat = new ExcelDataModel();
                            prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                            prod_subcat.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                            prod_subcat.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                            prod_subcat.setMemo(cursor2.getString(cursor2.getColumnIndex("memo")));
                            prod_subcat.setAmount_(cursor2.getDouble(cursor2.getColumnIndex("amount")));
                            prod_subcat.setId(cursor2.getString(cursor2.getColumnIndex("trans_id")));
                            prod_subcat.setIcon_name(cursor2.getString(cursor2.getColumnIndex("cat_iconname")));
                            prod_subcat.setPayment_mode(cursor2.getString(cursor2.getColumnIndex("payment_mode")));
                            prod_subcat.setTime(cursor2.getString(cursor2.getColumnIndex("time")));
                            prod_subcat.setFileName(cursor2.getString(cursor2.getColumnIndex("fileName")));

                            subCat_list.add(prod_subcat);
                        } while (cursor2.moveToNext());

                        foc_cat.setDate(date);

                        if (expense != null) {
                            foc_cat.setExp_amt_(Double.parseDouble(expense));
                        } else {
                            foc_cat.setExp_amt_(0);
                        }

                        if (income != null) {
                            foc_cat.setInc_amt_(Double.parseDouble(income));
                        } else {
                            foc_cat.setInc_amt_(0);
                        }

                        foc_cat.setDate(date);
                        foc_cat.setGroupFocAll(subCat_list);
                        listProd.add(foc_cat);
                        cursor2.close();
                    }
                } while (c.moveToNext());
            }
            if (c != null && !c.isClosed()) {
                c.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return listProd;
    }


    public List<ExcelDataModel> getTransactionFilter(String current_month, String filterType) {
        List<ExcelDataModel> listProd = new ArrayList<>();
        Log.d("CurrentMonthDB", current_month);

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String selectQuery = "";
            if (!filterType.isEmpty()) {
                selectQuery = "select f.date,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' and payment_mode='" + filterType + "' and date=f.date)exp, " +
                        "(select sum(amount) from tbl_transactions where trans_type='Income' and payment_mode='" + filterType + "' and date=f.date)inc from tbl_transactions f " +
                        "where strftime('%Y-%m', f.date) = '" + current_month + "' GROUP by f.date ORDER BY strftime('%s', f.date) desc";
            } else {
                selectQuery = "select f.date,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' and date=f.date)exp, " +
                        "(select sum(amount) from tbl_transactions where trans_type='Income' and date=f.date)inc from tbl_transactions f where strftime('%Y-%m', f.date) = '" + current_month + "' GROUP by f.date ORDER BY strftime('%s', f.date) desc";
            }
            Log.d("FirstQueryAllTrans", selectQuery);
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    ExcelDataModel foc_cat = new ExcelDataModel();
                    String date = c.getString(c.getColumnIndex("date"));
                    String income = c.getString(c.getColumnIndex("inc"));
                    String expense = c.getString(c.getColumnIndex("exp"));
                    String selectQuery1 = "";

                    if (!filterType.isEmpty()) {
                        selectQuery1 = "select tr.fileName,tr.date,tr.time,tr.trans_type,cat_name,memo,amount,trans_id,cat_iconname,payment_mode,tu.user_id " +
                                "FROM tbl_transactions tr JOIN tbl_category tc ON tr.cat_id = tc.cat_id  " +
                                "JOIN tbl_users tu ON tr.user_id = tu.user_id " +
                                "where payment_mode='" + filterType + "' and date='" + date + "' " +
                                "ORDER BY datetime(tr.time) desc,trans_id desc";
                    } else {
                        selectQuery1 = "select tr.fileName,tr.date,tr.time,tr.trans_type,cat_name,memo,amount,trans_id,cat_iconname,payment_mode,tu.user_id " +
                                "FROM tbl_transactions tr JOIN tbl_category tc ON tr.cat_id = tc.cat_id  " +
                                "JOIN tbl_users tu ON tr.user_id = tu.user_id " +
                                "where date='" + date + "' " +
                                "ORDER BY datetime(tr.time) desc, trans_id desc";
                    }
                    Log.d("SecondQueryAllTrans123", selectQuery1);
                    Cursor cursor2 = db.rawQuery(selectQuery1, null);
                    if (cursor2.moveToFirst()) {
                        List<ExcelDataModel> subCat_list = new ArrayList<>();
                        do {
                            ExcelDataModel prod_subcat = new ExcelDataModel();
                            prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                            prod_subcat.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                            prod_subcat.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                            prod_subcat.setMemo(cursor2.getString(cursor2.getColumnIndex("memo")));
                            prod_subcat.setAmount_(cursor2.getDouble(cursor2.getColumnIndex("amount")));
                            prod_subcat.setId(cursor2.getString(cursor2.getColumnIndex("trans_id")));
                            prod_subcat.setIcon_name(cursor2.getString(cursor2.getColumnIndex("cat_iconname")));
                            prod_subcat.setPayment_mode(cursor2.getString(cursor2.getColumnIndex("payment_mode")));
                            prod_subcat.setUserId(cursor2.getString(cursor2.getColumnIndex("user_id")));
                            prod_subcat.setTime(cursor2.getString(cursor2.getColumnIndex("time")));
                            prod_subcat.setFileName(cursor2.getString(cursor2.getColumnIndex("fileName")));
                            subCat_list.add(prod_subcat);
                        } while (cursor2.moveToNext());
                        foc_cat.setDate(date);

                        if (expense != null) {
                            foc_cat.setExp_amt_(Double.parseDouble(expense));
                        } else {
                            foc_cat.setExp_amt_(0);
                        }

                        if (income != null) {
                            foc_cat.setInc_amt_(Double.parseDouble(income));
                        } else {
                            foc_cat.setInc_amt_(0);
                        }

                        foc_cat.setDate(date);
                        foc_cat.setGroupFocAll(subCat_list);
                        listProd.add(foc_cat);
                        if (cursor2 != null && !cursor2.isClosed()) {
                            cursor2.close();
                        }
                    }
                } while (c.moveToNext());
            }
            if (c != null && !c.isClosed()) {
                c.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return listProd;
    }


    public List<CategoryModel> GetAllCatgories(String type) {
        List<CategoryModel> listdata = new ArrayList<>();
        // String selectQuery = "select * from tbl_category where trans_type='" + type + "' order by CAST(sort as INT) asc";
        String selectQuery = "select *, caty.cat_id, caty.cat_name, count(trans.cat_id) as total from tbl_category caty left join tbl_transactions trans on caty.cat_id = trans.cat_id where caty.trans_type='" + type + "' group by upper(cat_name) order by total DESC";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CategoryModel excelDataModel = new CategoryModel();
                excelDataModel.setCat_id(cursor.getString(cursor.getColumnIndex("cat_id")));
                excelDataModel.setCat_iconname(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                excelDataModel.setCat_name(cursor.getString(cursor.getColumnIndex("cat_name")));
                excelDataModel.setTrans_type(cursor.getString(cursor.getColumnIndex("trans_type")));
                excelDataModel.setSortOrder(cursor.getString(cursor.getColumnIndex("sort")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }

    public List<TransactionType> GetAllTransactionType() {
        List<TransactionType> listdata = new ArrayList<>();
        String selectQuery = "select * from tbl_transaction_type ORDER by trans_type ASC";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TransactionType excelDataModel = new TransactionType();
                excelDataModel.setTrans_id(cursor.getString(cursor.getColumnIndex("trans_type_id")));
                excelDataModel.setTrans_type(cursor.getString(cursor.getColumnIndex("trans_type")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }


    public long AddTransactionDataTO_TransactionTable(ExcelDataModel listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", listexceldata.getDate());
        values.put("trans_type", listexceldata.getIncome_Expenses());
        values.put("cat_id", listexceldata.getCat_id());
        values.put("memo", listexceldata.getMemo());
        values.put("amount", listexceldata.getAmount().replace("-", ""));
        values.put("month", listexceldata.getMonth());
        values.put("user_id", listexceldata.getUserId());
        values.put("payment_mode", listexceldata.getPayment_mode());
        values.put("time", listexceldata.getTime());
        values.put("capture_date", listexceldata.getCaptureDate());
        values.put("fileName", listexceldata.getFileName());
        values.put("column_1", listexceldata.getTagId());
        long id = db.insert("tbl_transactions", null, values);
        db.close();
        return id;
    }

    public List<ExcelDataModel> GetTransactionById(String id) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        //String selectQuery = "select * from tbl_transactions where trans_id='" + id + "'";
        //String selectQuery = "select * from tbl_transactions a LEFT join tbl_category b where a.cat_id=b.cat_id and a.trans_id='" + id + "'";
        String selectQuery = "select * from tbl_transactions a join tbl_category b on a.cat_id=b.cat_id join tbl_users u on a.user_id=u.user_id WHERE a.trans_id='" + id + "'";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        if (cursor2.moveToFirst()) {
            do {
                ExcelDataModel prod_subcat = new ExcelDataModel();
                prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                prod_subcat.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                prod_subcat.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                prod_subcat.setMemo(cursor2.getString(cursor2.getColumnIndex("memo")));
                prod_subcat.setAmount_(cursor2.getDouble(cursor2.getColumnIndex("amount")));
                prod_subcat.setId(cursor2.getString(cursor2.getColumnIndex("trans_id")));
                prod_subcat.setCat_id(cursor2.getString(cursor2.getColumnIndex("cat_id")));
                prod_subcat.setIcon_name(cursor2.getString(cursor2.getColumnIndex("cat_iconname")));
                prod_subcat.setPayment_mode(cursor2.getString(cursor2.getColumnIndex("payment_mode")));
                prod_subcat.setUserId(cursor2.getString(cursor2.getColumnIndex("user_id")));
                prod_subcat.setUserName(cursor2.getString(cursor2.getColumnIndex("user_name")));
                prod_subcat.setUser_relation(cursor2.getString(cursor2.getColumnIndex("user_relation")));
                prod_subcat.setTime(cursor2.getString(cursor2.getColumnIndex("time")));
                prod_subcat.setCaptureDate(cursor2.getString(cursor2.getColumnIndex("capture_date")));
                prod_subcat.setFileName(cursor2.getString(cursor2.getColumnIndex("fileName")));
                prod_subcat.setTagId(cursor2.getString(cursor2.getColumnIndex("column_1")));
                //Log.d("UserNameTest", prod_subcat.getUserName());
                listdata.add(prod_subcat);
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        db.close();
        return listdata;
    }

    public List<ExcelDataModel> GetTagsbyCommaSeperatedIds(String tagIds) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "select * from tbl_dummy where dummy_id1 in (" + tagIds + ")";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        if (cursor2.moveToFirst()) {
            do {
                ExcelDataModel prod_subcat = new ExcelDataModel();
                prod_subcat.setTagId(cursor2.getString(cursor2.getColumnIndex("dummy_id1")));
                prod_subcat.setTagName(cursor2.getString(cursor2.getColumnIndex("colA")));
                listdata.add(prod_subcat);
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        db.close();
        return listdata;
    }


    public void deleteTrasactionDataRow(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_TRANSACTION + " WHERE trans_id='" + value + "'");
        db.close();
    }

    public void deleteTrasactionDataRowfromTags(String transId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String del = "DELETE FROM tbl_dummy2" + " WHERE colA='" + transId + "'";
        Log.e("DeleteQuery", del);
        db.execSQL(del);
        db.close();
    }

    public void deleteUserDataRow(String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_USER + " WHERE user_id='" + userID + "'");
        db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_TRANSACTION + " WHERE user_id='" + userID + "'");
        db.close();
    }

    public void deleteUserTagRow(String tagID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_TAGS + " WHERE dummy_id1='" + tagID + "'");
        db.close();
    }


    public void deleteCategoryRow(String cat_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_CATEGORY + " WHERE cat_id='" + cat_id + "'");
        db.execSQL("DELETE FROM " + ExcelDataModel.TABLE_TRANSACTION + " WHERE cat_id='" + cat_id + "'");
        db.close();
    }


    public int CheckCatIdCount(String cat_id) {
        int count = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select count(1) as count from tbl_transactions WHERE cat_id='" + cat_id + "'";
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        if (cursor2.moveToFirst()) {
            do {
                count = cursor2.getInt(cursor2.getColumnIndex("count"));
            } while (cursor2.moveToNext());
        }
        db.close();

        return count;
    }

    public int CheckUserIdCount(String user_id) {
        int count = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select count(1) as count from tbl_transactions WHERE user_id='" + user_id + "'";
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        if (cursor2.moveToFirst()) {
            do {
                count = cursor2.getInt(cursor2.getColumnIndex("count"));
            } while (cursor2.moveToNext());
        }
        db.close();
        return count;
    }


    public int CheckTagIdCount(String tagId) {
        int count = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select count(1) as count from tbl_dummy2 WHERE colB='" + tagId + "'";
        Log.e("selectQuery", selectQuery);
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        if (cursor2.moveToFirst()) {
            do {
                count = cursor2.getInt(cursor2.getColumnIndex("count"));
            } while (cursor2.moveToNext());
        }
        db.close();

        return count;
    }


    public long updateNote(String userID, String userName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_name", userName);

        long id = db.update("tbl_users", values, "user_id" + " = ?",
                new String[]{String.valueOf(userID)});
        return id;
    }


    public long updateTag(String tagId, String tagName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("colA", tagName);

        long id = db.update("tbl_dummy", values, "dummy_id1" + " = ?",
                new String[]{String.valueOf(tagId)});
        return id;
    }


    public long updateCategory(String catID, String catName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cat_name", catName);
        long id = db.update("tbl_category", values, "cat_id" + " = ?",
                new String[]{String.valueOf(catID)});
        return id;
    }

    public long updateFileName(String transId, String fileName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fileName", fileName);
        long id = db.update("tbl_transactions", values, "trans_id" + " = ?",
                new String[]{String.valueOf(transId)});
        return id;
    }


    public long updateUserImage(String userId, String userImg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_relation", userImg);
        long id = db.update("tbl_users", values, "user_id" + " = ?",
                new String[]{String.valueOf(userId)});
        return id;
    }


    public long UpdateTransactionDataTO_TransactionTable(ExcelDataModel listexceldata, String trans_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", listexceldata.getDate());
        values.put("trans_type", listexceldata.getIncome_Expenses());
        values.put("cat_id", listexceldata.getCat_id());
        values.put("memo", listexceldata.getMemo());
        values.put("amount", listexceldata.getAmount().replace("-", ""));
        values.put("month", listexceldata.getMonth());
        values.put("payment_mode", listexceldata.getPayment_mode());
        values.put("user_id", listexceldata.getUserId());
        values.put("time", listexceldata.getTime());
        values.put("capture_date", listexceldata.getCaptureDate());
        values.put("fileName", listexceldata.getFileName());
        values.put("column_1", listexceldata.getTagId());
        long id = db.update("tbl_transactions", values, "trans_id=" + trans_id, null);
        db.close();
        return id;
    }


    /* public void UpdateMassExpenseData() {
     *//* SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE tbl_transactions set cat_id='15' where  trans_type='Expenses' and cat_id='38'";
        db.execSQL(query);
        Log.d("SQul", query);
        db.close();*//*
    }


    public void UpdateMassIncomeData() {
       *//* SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE tbl_transactions set cat_id='38' where trans_type='Income' and cat_id='15'";
        db.execSQL(query);
        Log.d("SQul", query);
        db.close();*//*
    }*/


    public List<ExcelDataModel> GetSearchData(String search_text, String st_cat, String trans_type, String from_date, String to_date) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String where = "";
        if (search_text.length() > 0) where += " AND memo LIKE '%" + search_text + "%'";
        if (st_cat.length() > 0) where += " AND cat_name='" + st_cat + "'";
        if (trans_type.length() > 0) where += " AND b.trans_type='" + trans_type + "'";
        if (from_date.length() > 0) where += " AND date='" + from_date + "'";
        if (to_date.length() > 0) where += " AND date='" + to_date + "'";

        String selectQuery = "select * from tbl_transactions a LEFT join tbl_category b where a.cat_id=b.cat_id" + where;

        Log.e("SearchQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        if (cursor2.moveToFirst()) {
            do {
                ExcelDataModel prod_subcat = new ExcelDataModel();
                prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                prod_subcat.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                prod_subcat.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                prod_subcat.setMemo(cursor2.getString(cursor2.getColumnIndex("memo")));
                prod_subcat.setAmount(cursor2.getString(cursor2.getColumnIndex("amount")));
                prod_subcat.setId(cursor2.getString(cursor2.getColumnIndex("trans_id")));
                prod_subcat.setCat_id(cursor2.getString(cursor2.getColumnIndex("cat_id")));
                listdata.add(prod_subcat);
            } while (cursor2.moveToNext());
        }
        db.close();
        return listdata;
    }

   /* public void UpdateSortField() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cat_id", Integer.parseInt(cat_id));
        db.update(ExcelDataModel.TABLE_CATEGORY, values, "sort='" + val + "'", null);
        db.close();
    }*/

    public void UpdateSortFieldSwap(List<ExcelDataModel> listexceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < listexceldata.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("sort", String.valueOf(i));
            Log.d("Sorted", "" + i);
            db.update(ExcelDataModel.TABLE_CATEGORY, values, "cat_id='" + listexceldata.get(i).getId() + "'", null);
        }
        db.close();
    }

    public List<ExcelDataModel> getSearchDataFormatted(String search_text, String st_cat, String trans_type, String stratDate, String endDate,
                                                       String cat_id, String userId, String tagIds) {
        List<ExcelDataModel> listProd = new ArrayList<>();
        String where = "";
        String where1 = "";

        if (search_text.length() > 0) where += " AND memo LIKE '%" + search_text + "%'";
        if (cat_id.length() > 0) where += " AND a.cat_id='" + cat_id + "'";
        if (trans_type.length() > 0) where += " AND b.trans_type='" + trans_type + "'";
        if (userId.length() > 0) where += " AND a.user_id='" + userId + "'";
        //if (tagIds.length() > 0) where += " AND tagIds='" + tagIds + "'";

        if (search_text.length() > 0) where1 += " AND memo LIKE '%" + search_text + "%'";
        if (trans_type.length() > 0) where1 += " AND trans_type='" + trans_type + "'";
        if (userId.length() > 0) where1 += " AND user_id='" + userId + "'";
        //if (tagIds.length() > 0) where1 += " AND tagIds='" + tagIds + "'";


        try {
            if (cat_id.length() > 0) where1 += " AND cat_id='" + cat_id + "'";
        } catch (Exception e) {
        }

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String selectQuery = "";
            if (tagIds.isEmpty()) {
                if (where1.isEmpty()) {
                    selectQuery = "select f.date,f.memo,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' " +
                            "and date=f.date)exp, (select sum(amount) from tbl_transactions where trans_type='Income' " +
                            "and date=f.date)inc, f.cat_id from tbl_transactions f  where DATE(date) BETWEEN '" + stratDate + "' AND '" + endDate + "'  " +
                            " GROUP by f.date ORDER BY strftime('%s', f.date) desc";
                } else {
                    if (userId.isEmpty()) {
                        selectQuery = "select f.date,f.memo,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' " +
                                "and date=f.date " + where1 + ")exp, (select sum(amount) from tbl_transactions where trans_type='Income' " +
                                "and date=f.date " + where1 + ")inc, f.cat_id from tbl_transactions f  where  DATE(date) BETWEEN '" + stratDate + "' AND '" + endDate + "' " + where1 + "" +
                                " GROUP by f.date ORDER BY strftime('%s', f.date) desc";
                    } else {
                        selectQuery = "select f.date,f.memo,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' " +
                                "and date=f.date " + where1 + ")exp, (select sum(amount) from tbl_transactions where trans_type='Income' " +
                                "and date=f.date " + where1 + ")inc, f.cat_id from tbl_transactions f  where DATE(date) BETWEEN '" + stratDate + "' AND '" + endDate + "' " + where1 + "" +
                                " GROUP by f.date ORDER BY strftime('%s', f.date) desc";
                    }
                }
            } else {
                if (where1.isEmpty()) {
                    /*selectQuery = "select f.date,f.memo,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' " +
                            "and date=f.date)exp, (select sum(amount) from tbl_transactions where trans_type='Income' " +
                            "and date=f.date)inc, f.cat_id from tbl_transactions f left join tbl_dummy2 d2 on f.trans_id=d2.colA " +
                            "left join tbl_dummy d on d.dummy_id1=d2.colB where DATE(date) BETWEEN '" + stratDate + "' AND '" + endDate + "'" +
                            "AND d2.colB in (" + tagIds + ")" +
                            " GROUP by f.date ORDER BY strftime('%s', f.date) desc";*/
                    selectQuery = "select f.date,f.memo,(select sum(tt.amount) from(select DISTINCT(trns.trans_id), " +
                            "trns.amount from tbl_transactions trns " +
                            "left join tbl_dummy2 d2 on d2.colA=trns.trans_id " +
                            " where trns.trans_type = 'Expenses' " +
                            "and date=f.date AND d2.colB in (" + tagIds + ")) as tt)exp, (select sum(tt.amount) from(select DISTINCT(trns.trans_id), " +
                            "trns.amount from tbl_transactions trns " +
                            "left join tbl_dummy2 d2 on d2.colA=trns.trans_id" +
                            " where trns.trans_type = 'Income' " +
                            "and date=f.date AND d2.colB in (" + tagIds + ")) as tt)inc, f.cat_id from tbl_transactions f left join tbl_dummy2 d2 on f.trans_id=d2.colA " +
                            "left join tbl_dummy d on d.dummy_id1=d2.colB where DATE(date) BETWEEN '" + stratDate + "' AND '" + endDate + "'" +
                            "AND d2.colB in (" + tagIds + ")" +
                            " GROUP by f.date ORDER BY strftime('%s', f.date) desc";
                } else {
                    if (userId.isEmpty()) {
                        /*selectQuery = "select f.date,f.memo,(select sum(amount) from tbl_transactions e left join tbl_dummy2 d2 on e.trans_id=d2.colA " +
                                "left join tbl_dummy d on d.dummy_id1=d2.colB WHERE trans_type='Expenses' " +
                                "and date=f.date " + where1 + " and d2.colB in (" + tagIds + "))exp, (select sum(amount) from tbl_transactions g left join tbl_dummy2 d2 on g.trans_id=d2.colA " +
                                "left join tbl_dummy d on d.dummy_id1=d2.colB where trans_type='Income' " +
                                "and date=f.date " + where1 + " and d2.colB in (" + tagIds + "))inc, f.cat_id from tbl_transactions f left join tbl_dummy2 d2 on f.trans_id=d2.colA " +
                                "left join tbl_dummy d on d.dummy_id1=d2.colB where DATE(date) BETWEEN '" + stratDate + "' AND '" + endDate + "' " +
                                "AND d2.colB in (" + tagIds + ")" +
                                " GROUP by f.date ORDER BY strftime('%s', f.date) desc";*/

                        selectQuery = "select f.date,f.memo,(select sum(tt.amount) from(select DISTINCT(trns.trans_id), " +
                                "trns.amount from tbl_transactions trns " +
                                "left join tbl_dummy2 d2 on d2.colA=trns.trans_id " +
                                "where trns.trans_type = 'Expenses' " +
                                "and date=f.date " + where1 + " and d2.colB in (" + tagIds + ")) as tt)exp, (select sum(tt.amount) from(select DISTINCT(trns.trans_id), " +
                                "trns.amount from tbl_transactions trns " +
                                "left join tbl_dummy2 d2 on d2.colA=trns.trans_id" +
                                " where trns.trans_type = 'Income' " +
                                "and date=f.date " + where1 + " and d2.colB in (" + tagIds + ")) as tt)inc, f.cat_id from tbl_transactions f left join tbl_dummy2 d2 on f.trans_id=d2.colA " +
                                "left join tbl_dummy d on d.dummy_id1=d2.colB where DATE(date) BETWEEN '" + stratDate + "' AND '" + endDate + "' " +
                                "AND d2.colB in (" + tagIds + ")" +
                                " GROUP by f.date ORDER BY strftime('%s', f.date) desc";
                    } else {
                        selectQuery = "select f.date,f.memo,(select sum(tt.amount) from(select DISTINCT(trns.trans_id), " +
                                "trns.amount from tbl_transactions trns " +
                                "left join tbl_dummy2 d2 on d2.colA=trns.trans_id " +
                                "where trns.trans_type = 'Expenses' " +
                                "and date=f.date " + where1 + " and d2.colB in (" + tagIds + ")) as tt)exp, (select sum(tt.amount) from(select DISTINCT(trns.trans_id), " +
                                "trns.amount from tbl_transactions trns " +
                                "left join tbl_dummy2 d2 on d2.colA=trns.trans_id" +
                                " where trns.trans_type = 'Income' " +
                                "and date=f.date " + where1 + " and d2.colB in (" + tagIds + ")) as tt)inc, f.cat_id from tbl_transactions f left join tbl_dummy2 d2 on f.trans_id=d2.colA  " +
                                "left join tbl_dummy d on d.dummy_id1=d2.colB where DATE(date) BETWEEN '" + stratDate + "' AND '" + endDate + "' AND user_id='" + userId + "' " +
                                "AND d2.colB in (" + tagIds + ")" +
                                " GROUP by f.date ORDER BY strftime('%s', f.date) desc";
                    }
                }
            }

            Log.d("SearchMainQuery", selectQuery);
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    ExcelDataModel foc_cat = new ExcelDataModel();
                    String date1 = c.getString(c.getColumnIndex("date"));
                    String income = c.getString(c.getColumnIndex("inc"));
                    String expense = c.getString(c.getColumnIndex("exp"));
                    String selectQuery1 = "";
                    if (tagIds.isEmpty()) {
                        /*selectQuery1 = "select * from tbl_transactions a join tbl_category b where " +
                                "a.cat_id=b.cat_id" + where + " AND
                                 date ='" + date1 + "' " +
                                "order by memo asc";*/

                        selectQuery1 = "select *, group_concat(d.colA) as Tags from tbl_transactions a " +
                                "join tbl_category b on a.cat_id=b.cat_id " +
                                "left join tbl_dummy2 d2 on a.trans_id=d2.colA " +
                                "left join tbl_dummy d on d.dummy_id1=d2.colB " +
                                "WHERE " +
                                "date ='" + date1 + "' " + where + "group by a.trans_id order by memo asc";
                    } else {
                        selectQuery1 = "select *,group_concat(d.colA) as Tags from tbl_transactions a join tbl_category b " +
                                "left join tbl_dummy2 d2 on a.trans_id=d2.colA " +
                                "left join tbl_dummy d on d.dummy_id1=d2.colB " +
                                "where a.cat_id=b.cat_id " + where + "  AND d2.colB in (" + tagIds + ") AND date ='" + date1 + "' " +
                                "group by trans_id order by memo asc";
                    }


                    //String selectQuery1 = "select * from tbl_transactions a LEFT join tbl_category b on a.cat_id=b.cat_id where " + where + " AND date ='" + date1 + "' order by memo asc";
                    Log.d("SearchSubQuery", selectQuery1);
                    Cursor cursor2 = db.rawQuery(selectQuery1, null);
                    if (cursor2.moveToFirst()) {
                        List<ExcelDataModel> subCat_list = new ArrayList<>();
                        //ExcelDataModel pp_dummy = new ExcelDataModel();
                        // pp_dummy.setId("");
                        //subCat_list.add(pp_dummy);
                        do {
                            ExcelDataModel prod_subcat = new ExcelDataModel();
                            prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                            prod_subcat.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                            prod_subcat.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                            prod_subcat.setMemo(cursor2.getString(cursor2.getColumnIndex("memo")));
                            prod_subcat.setAmount_(cursor2.getDouble(cursor2.getColumnIndex("amount")));
                            prod_subcat.setId(cursor2.getString(cursor2.getColumnIndex("trans_id")));
                            prod_subcat.setIcon_name(cursor2.getString(cursor2.getColumnIndex("cat_iconname")));
                            prod_subcat.setPayment_mode(cursor2.getString(cursor2.getColumnIndex("payment_mode")));
                            prod_subcat.setTime(cursor2.getString(cursor2.getColumnIndex("time")));
                            prod_subcat.setTime(cursor2.getString(cursor2.getColumnIndex("time")));
                            prod_subcat.setTagName(cursor2.getString(cursor2.getColumnIndex("Tags")));
                            /*if (prod_subcat.getIncome_Expenses().equals("Expenses")) {
                                income_amt += Integer.parseInt(prod_subcat.getAmount());
                                prod_subcat.setExp_amt(income_amt);
                                Utils.listProd1_exp.add(income_amt);
                            } else {
                                expense_amt += Integer.parseInt(prod_subcat.getAmount());
                                prod_subcat.setExp_amt(expense_amt);
                                Utils.listProd_inc.add(expense_amt);
                            }*/
                            subCat_list.add(prod_subcat);
                        } while (cursor2.moveToNext());
                        foc_cat.setDate(date1);
                        if (expense != null) {
                            foc_cat.setExp_amt_(Double.parseDouble(expense));
                        } else {
                            foc_cat.setExp_amt_(0);
                        }

                        if (income != null) {
                            foc_cat.setInc_amt_(Double.parseDouble(income));
                        } else {
                            foc_cat.setInc_amt_(0);
                        }

                        foc_cat.setGroupFocAll(subCat_list);
                        listProd.add(foc_cat);
                    }
                    cursor2.close();
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return listProd;
    }


    public List<ExcelDataModel> GetAllCAtegoryDataSortedByName() {
        List<ExcelDataModel> listdata = new ArrayList<>();
        //String selectQuery = "select * from tbl_category  group by upper(cat_name) order by cat_name asc";
        String selectQuery = "select * from tbl_category order by cat_name asc";
        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                excelDataModel.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                excelDataModel.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                excelDataModel.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }


    public List<ExcelDataModel> getAverageData(String date, String trans_type) {
        List<ExcelDataModel> listdata = new ArrayList<>();

        String selectQuery = "select sum(trans.amount) as amt,trans.payment_mode, caty.cat_name,trans.cat_id, caty.cat_iconname " +
                "from tbl_transactions trans left join tbl_category caty on trans.cat_id=caty.cat_id where trans.trans_type='" + trans_type + "' " +
                "and strftime('%Y-%m', date) = '" + date + "' GROUP by trans.cat_id  order by CAST(amt as INT) DESC";

        Log.e("AverageData", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                excelDataModel.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                excelDataModel.setAmount_(cursor.getDouble(cursor.getColumnIndex("amt")));
                excelDataModel.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                excelDataModel.setPayment_mode(cursor.getString(cursor.getColumnIndex("payment_mode")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }


    public List<ExcelDataModel> getAverageDataCategoryWise(String date, String trans_type, String id) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "select trans.trans_id,trans.payment_mode,trans.memo,trans.amount,trans.date,caty.cat_name,trans.cat_id,caty.cat_iconname " +
                "from tbl_transactions trans left join tbl_category caty on trans.cat_id=caty.cat_id " +
                "where trans.trans_type='" + trans_type + "' and strftime('%Y-%m', date) = '" + date + "' and caty.cat_id='" + id + "'" +
                " order by CAST(trans.amount as DOUBLE) DESC";
        Log.e("QueryCategoryWise", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setId(cursor.getString(cursor.getColumnIndex("trans_id")));
                excelDataModel.setCat_id(cursor.getString(cursor.getColumnIndex("cat_id")));
                excelDataModel.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                excelDataModel.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                excelDataModel.setAmount_(cursor.getDouble(cursor.getColumnIndex("amount")));
                excelDataModel.setDate(cursor.getString(cursor.getColumnIndex("date")));
                excelDataModel.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                excelDataModel.setPayment_mode(cursor.getString(cursor.getColumnIndex("payment_mode")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }

    public List<ExcelDataModel> getAverageDataDayWise(String date, String trans_type) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "select trans.trans_id,trans.payment_mode,trans.memo,trans.amount,trans.date,caty.cat_name,trans.cat_id,caty.cat_iconname " +
                "from tbl_transactions trans left join tbl_category caty on trans.cat_id=caty.cat_id " +
                "where trans.trans_type='" + trans_type + "' and strftime('%Y-%m', date) = '" + date + "'" +
                " order by CAST(trans.amount as DOUBLE) DESC";
        Log.e("QueryCategoryWise", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setId(cursor.getString(cursor.getColumnIndex("trans_id")));
                excelDataModel.setCat_id(cursor.getString(cursor.getColumnIndex("cat_id")));
                excelDataModel.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                excelDataModel.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                excelDataModel.setAmount_(cursor.getDouble(cursor.getColumnIndex("amount")));
                excelDataModel.setDate(cursor.getString(cursor.getColumnIndex("date")));
                excelDataModel.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                excelDataModel.setPayment_mode(cursor.getString(cursor.getColumnIndex("payment_mode")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }


    public List<ExcelDataModel> getMonthWiseData(String year, String month) {
        if (Integer.parseInt(month) < 10) {
            month = "0" + month;
        }
        Log.e("cscscss", month + " : " + year);
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "SELECT a.date, (SELECT sum(amount) from tbl_transactions where date = a.date and trans_type = 'Expenses') exp, " +
                "(SELECT sum(amount) from tbl_transactions where date = a.date and trans_type = 'Income') inc  " +
                "FROM tbl_transactions a " +
                "WHERE strftime('%Y',strftime('%Y-%m-%d', a.date)) = '" + year + "' and strftime('%m',strftime('%Y-%m-%d', a.date)) = '" + month + "'" +
                " GROUP by a.date ORDER BY strftime('%s', a.date) asc";
        Log.e("QueryCategoryWise", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel excelDataModel = new ExcelDataModel();
                excelDataModel.setDate(cursor.getString(cursor.getColumnIndex("date")));
                excelDataModel.setInc_amt_(cursor.getDouble(cursor.getColumnIndex("inc")));
                excelDataModel.setExp_amt_(cursor.getDouble(cursor.getColumnIndex("exp")));
                listdata.add(excelDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdata;
    }


    public boolean CheckIsDataAlreadyInDBorNot(String memo, String category, String amount) {
        SQLiteDatabase sqldb = this.getReadableDatabase();
        String Query = "select * from tbl_transactions a LEFT join tbl_category b on a.cat_id=b.cat_id where a.memo='" + memo + "' and b.cat_name='" + category + "' and a.amount='" + amount + "' ";
        Log.d("Query", Query);
        Cursor cursor = sqldb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        sqldb.close();
        return true;
    }

    public boolean CheckIsCategoryExist(String cat_name, String trans_type) {
        SQLiteDatabase sqldb = this.getReadableDatabase();
        String CAtegoryQuery = "select * from tbl_category where cat_name='" + cat_name + "' and trans_type='" + trans_type + "'";
        Log.d("Query", CAtegoryQuery);
        Cursor cursor = sqldb.rawQuery(CAtegoryQuery, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        sqldb.close();
        return true;
    }


    public List<ExcelDataModel> getExpenseDetailsMonthWise(String current_year) {
        List<ExcelDataModel> listProd = new ArrayList<>();
        Log.d("CurrentMonthDB", current_year);

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String selectQuery = "SELECT distinct strftime('%Y-%m', date) as date from tbl_transactions WHERE strftime('%Y', date)='" + current_year + "'";
            Log.d("FirstQueryMonthWise", selectQuery);
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    ExcelDataModel foc_cat = new ExcelDataModel();
                    String querydate = c.getString(c.getColumnIndex("date"));
                    String selectQuery1 = "select f.date,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' " +
                            "and strftime('%Y-%m',date) = '" + querydate + "')exp, (select sum(amount) " +
                            "from tbl_transactions where trans_type='Income' and strftime('%Y-%m',date) = '" + querydate + "')inc from tbl_transactions f where strftime('%Y-%m',f.date) = '" + querydate + "' GROUP by strftime('%Y-%m',f.date) ORDER BY strftime('%s', f.date) desc";
                    Log.d("SecondQueryMonthWise", selectQuery1);
                    Cursor cursor2 = db.rawQuery(selectQuery1, null);
                    if (cursor2.moveToFirst()) {
                        List<ExcelDataModel> subCat_list = new ArrayList<>();
                        do {
                            ExcelDataModel prod_subcat = new ExcelDataModel();
                            prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                            prod_subcat.setExp_amt_(cursor2.getDouble(cursor2.getColumnIndex("exp")));
                            prod_subcat.setInc_amt_(cursor2.getDouble(cursor2.getColumnIndex("inc")));
                            subCat_list.add(prod_subcat);
                        } while (cursor2.moveToNext());
                        foc_cat.setDate(querydate);
                        foc_cat.setGroupFocAll(subCat_list);
                        listProd.add(foc_cat);
                        cursor2.close();
                    }
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return listProd;
    }


    public List<ExcelDataModel> getExpenseDetailsYearWise() {
        List<ExcelDataModel> listProd = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String selectQuery = "SELECT distinct strftime('%Y', date) as date from tbl_transactions";
            Log.d("FirstQueryAllTrans", selectQuery);
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    ExcelDataModel foc_cat = new ExcelDataModel();
                    String querydate = c.getString(c.getColumnIndex("date"));
                    //String selectQuery1 = "select f.date,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' and strftime('%Y-%m',date) like '%" + querydate + "%')exp, (select sum(amount) from tbl_transactions where trans_type='Income' and strftime('%Y-%m',date) like '%" + querydate + "%')inc from tbl_transactions f where strftime('%Y-%m',f.date) like '%" + querydate + "%' GROUP by strftime('%Y-%m',f.date) ORDER BY strftime('%s', f.date) desc";
                    String selectQuery1 = "select f.date,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' " +
                            "and strftime('%Y',date) = '" + querydate + "')exp, (select sum(amount) from tbl_transactions where trans_type='Income' " +
                            "and strftime('%Y',date) = '" + querydate + "')inc from tbl_transactions f where strftime('%Y',f.date) = '" + querydate + "' " +
                            "GROUP by strftime('%Y',f.date) ORDER BY strftime('%s', f.date) desc";

                    Log.d("SecondQueryYearWise12", selectQuery1);
                    Cursor cursor2 = db.rawQuery(selectQuery1, null);
                    if (cursor2.moveToFirst()) {
                        List<ExcelDataModel> subCat_list = new ArrayList<>();
                        do {
                            ExcelDataModel prod_subcat = new ExcelDataModel();
                            prod_subcat.setDate(cursor2.getString(cursor2.getColumnIndex("date")));
                            prod_subcat.setExp_amt_(cursor2.getDouble(cursor2.getColumnIndex("exp")));
                            prod_subcat.setInc_amt_(cursor2.getDouble(cursor2.getColumnIndex("inc")));
                            subCat_list.add(prod_subcat);
                        } while (cursor2.moveToNext());
                        foc_cat.setDate(querydate);
                        foc_cat.setGroupFocAll(subCat_list);
                        listProd.add(foc_cat);
                        //cursor2.close();
                    }
                } while (c.moveToNext());
            }
            c.close();
            //db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return listProd;
    }


    public List<ExcelDataModel> GetAllCategoryDataByGrouping() {
        List<ExcelDataModel> listProd = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String selectQuery = "select distinct trans_type from tbl_category order by trans_type asc";
            Log.d("FirstQueryAllTrans", selectQuery);
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    ExcelDataModel foc_cat = new ExcelDataModel();
                    String transType = c.getString(c.getColumnIndex("trans_type"));
                    //String selectQuery1 = "select f.date,(select sum(amount) from tbl_transactions WHERE trans_type='Expenses' and strftime('%Y-%m',date) like '%" + querydate + "%')exp, (select sum(amount) from tbl_transactions where trans_type='Income' and strftime('%Y-%m',date) like '%" + querydate + "%')inc from tbl_transactions f where strftime('%Y-%m',f.date) like '%" + querydate + "%' GROUP by strftime('%Y-%m',f.date) ORDER BY strftime('%s', f.date) desc";
                    String selectQuery1 = "select * from tbl_category where trans_type='" + transType + "' order by cat_name asc";

                    Log.d("SecondQueryGrouping", selectQuery1);
                    Cursor cursor2 = db.rawQuery(selectQuery1, null);
                    if (cursor2.moveToFirst()) {
                        List<ExcelDataModel> subCat_list = new ArrayList<>();
                        do {
                            ExcelDataModel excelDataModel = new ExcelDataModel();
                            excelDataModel.setId(cursor2.getString(cursor2.getColumnIndex("cat_id")));
                            excelDataModel.setCategory(cursor2.getString(cursor2.getColumnIndex("cat_name")));
                            excelDataModel.setIncome_Expenses(cursor2.getString(cursor2.getColumnIndex("trans_type")));
                            excelDataModel.setIcon_name(cursor2.getString(cursor2.getColumnIndex("cat_iconname")));
                            excelDataModel.setCatIsAdded(cursor2.getString(cursor2.getColumnIndex("isAdded")));
                            subCat_list.add(excelDataModel);
                        } while (cursor2.moveToNext());
                        foc_cat.setIncome_Expenses(transType);
                        foc_cat.setGroupFocAll(subCat_list);
                        listProd.add(foc_cat);
                        cursor2.close();
                    }
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
        return listProd;
    }


    public long addCategory(ExcelDataModel exceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trans_type", exceldata.getIncome_Expenses());
        values.put("cat_name", exceldata.getCategory());
        values.put("isAdded", exceldata.getCatIsAdded());
        values.put("cat_iconname", "");
        values.put("sort", "1000");
        long id = db.insert("tbl_category", null, values);
        db.close();
        return id;
    }


    public long addUser(ExcelDataModel exceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_name", exceldata.getUserName());
        long id = db.insert("tbl_users", null, values);
        db.close();
        return id;
    }


    public long addLabels(ExcelDataModel exceldata) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("colA", exceldata.getTagName());
        long id = db.insert("tbl_dummy", null, values);
        db.close();
        return id;
    }


    public List<ExcelDataModel> getAllUsers() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_users";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                note.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
                note.setUser_relation(cursor.getString(cursor.getColumnIndex("user_relation")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }

    public List<ExcelDataModel> getALlTags() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_dummy order by colA asc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setTagId(cursor.getString(cursor.getColumnIndex("dummy_id1")));
                note.setTagName(cursor.getString(cursor.getColumnIndex("colA")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }


    public List<ExcelDataModel> getAllMyDetails() {
        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_users where user_id='1'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                note.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
                note.setUser_relation(cursor.getString(cursor.getColumnIndex("user_relation")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }


    public List<ExcelDataModel> GetAllFinalTransactionDataComparision(String startDate, String endDate, String catId, String transaction_type) {
        String where1 = "";
        if (!catId.isEmpty()) {
            try {
                if (catId.length() > 0) where1 += "a.cat_id='" + catId + "'";
            } catch (Exception e) {
            }
        } else {
            where1 = "1";
        }

        List<ExcelDataModel> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_transactions a join tbl_category b on a.cat_id=b.cat_id join " +
                "tbl_users u on a.user_id=u.user_id " +
                "WHERE   " + where1 + "" +
                " and a.trans_type='" + transaction_type + "'" +
                " and " +
                "a.date BETWEEN '" + startDate + "' AND '" + endDate + "'" +
                " ORDER BY datetime(a.time) asc";

        Log.d("CompareQuery", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                ExcelDataModel note = new ExcelDataModel();
                note.setDate(cursor.getString(cursor.getColumnIndex("date")));
                note.setIncome_Expenses(cursor.getString(cursor.getColumnIndex("trans_type")));
                note.setCategory(cursor.getString(cursor.getColumnIndex("cat_name")));
                note.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                note.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
                note.setId(cursor.getString(cursor.getColumnIndex("cat_id")));
                note.setIcon_name(cursor.getString(cursor.getColumnIndex("cat_iconname")));
                note.setPayment_mode(cursor.getString(cursor.getColumnIndex("payment_mode")));
                note.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                note.setTime(cursor.getString(cursor.getColumnIndex("time")));

                try {
                    if (note.getUserName().isEmpty()) {
                        note.setUserName("Myself");
                    }
                } catch (Exception e) {
                    note.setUserName("Myself");
                }

                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return listdistinctdata;
    }


    public double GetTotalCompCount(String startDate, String endDate, String catId, String transaction_type) {
        double count = 0.0;
        String where1 = "";
        if (!catId.isEmpty()) {
            try {
                if (catId.length() > 0) where1 += "a.cat_id='" + catId + "'";
            } catch (Exception e) {
            }
        } else {
            where1 = "1";
        }

        String selectQuery = "select sum(amount) as count from tbl_transactions a join tbl_category b " +
                "on a.cat_id=b.cat_id join tbl_users u on a.user_id=u.user_id " +
                "WHERE   " + where1 + "" +
                " and a.trans_type='" + transaction_type + "'" +
                " and " +
                "a.date BETWEEN '" + startDate + "' AND '" + endDate + "'";

        Log.d("CompareQuery", selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                count = cursor.getInt(cursor.getColumnIndex("count"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return count;
    }


    public long AddSyncData(Sync sync) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sync_date", sync.getSyncDate());
        values.put("sync_fileid", sync.getFileId());
        values.put("sync_status", sync.getSyncStatus());
        values.put("sync_filename", sync.getFileName());
        values.put("colA", sync.getAccountName());
        long id = db.insert("tbl_syncstatus", null, values);
        db.close();
        return id;
    }

    public List<Sync> getAllSyncData() {
        List<Sync> listdistinctdata = new ArrayList<>();
        String selectQuery = "select * from tbl_syncstatus ORDER BY datetime(sync_date) desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Sync note = new Sync();
                note.setSyncId(cursor.getString(cursor.getColumnIndex("sync_id")));
                note.setSyncDate(cursor.getString(cursor.getColumnIndex("sync_date")));
                note.setFileId(cursor.getString(cursor.getColumnIndex("sync_fileid")));
                note.setSyncStatus(cursor.getString(cursor.getColumnIndex("sync_status")));
                note.setFileName(cursor.getString(cursor.getColumnIndex("sync_filename")));
                note.setAccountName(cursor.getString(cursor.getColumnIndex("colA")));
                listdistinctdata.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listdistinctdata;
    }


    public void AddTagsInTempWithId(long transId, String tagIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tbl_dummy2" + " WHERE colA='" + transId + "'");
        if (!tagIds.isEmpty()) {
            String a[] = tagIds.split(",");
            for (int i = 0; i < a.length; i++) {
                ContentValues values = new ContentValues();
                values.put("colA", transId);
                values.put("colB", a[i]);
                db.insert("tbl_dummy2", null, values);
            }
        }
        db.close();
    }

    public void DeleteTagstfromDummyTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tbl_dummy3");
        db.close();
    }

    public void AddTagstToDummyTable(String transId, String tagName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("colA", transId);
        values.put("colB", tagName);
        db.insert("tbl_dummy3", null, values);
        db.close();
    }


    public List<ExcelDataModel> GetTagsByTransId(String transId) {
        List<ExcelDataModel> listdata = new ArrayList<>();
        String selectQuery = "select distinct d.colA as tags ,d2.colA as transid, d2.colB as tagid from tbl_dummy2 d2 join tbl_dummy d on d2.colB=d.dummy_id1 where d2.colA='" + transId + "'";

        Log.e("Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        if (cursor2.moveToFirst()) {
            do {
                ExcelDataModel prod_subcat = new ExcelDataModel();
                prod_subcat.setTagId(cursor2.getString(cursor2.getColumnIndex("tagid")));
                prod_subcat.setTagName(cursor2.getString(cursor2.getColumnIndex("tags")));

                //Log.d("UserNameTest", prod_subcat.getUserName());
                listdata.add(prod_subcat);
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        db.close();
        return listdata;
    }
}