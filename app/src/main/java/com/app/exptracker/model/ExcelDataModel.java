package com.app.exptracker.model;

import java.util.List;

public class ExcelDataModel {

    public static String expense_type = "expense_type";
    public static String category = "tcategory";
    public static String TABLE_TEMP_NAME = "tbl_transaction_temp";
    public static String TABLE_TRANSACTION = "tbl_transactions";
    public static String TABLE_USER = "tbl_users";
    public static String TABLE_TAGS = "tbl_dummy";

    public static String TABLE_CATEGORY = "tbl_category";
    public static String COLUMN_AMOUNT = "amount";
    public List<ExcelDataModel> GroupFocAll;
    public static String COLUMN_MONTH = "month";
    public static String COLUMN_CAPTURE_DATE = "capture_date";
    public static String COLUMN_MODIFIED_DATE = "modified_date";


    String month = "", payment_mode = "", catIsAdded = "", user_relation = "";
    String date = "", Income_Expenses = "", Category = "", Memo = "", Amount = "", id = "", cat_id;
    int exp_amt = 0, inc_amt = 0;
    String sorted = "0";
    String icon_name = "";
    String userId = "", userName = "";

    String captureDate = "";
    String modifiedDate = "";
    String time = "";

    double exp_amt_ = 0, inc_amt_ = 0, amount_;
    int count_;
    String fileName = "";

    String tagName = "";
    String tagId = "";
    private boolean isSelected = false;

    public ExcelDataModel() {
    }

    public ExcelDataModel(String date, String income_Expenses, String category, String memo, String amount) {
        this.date = date;
        this.Income_Expenses = income_Expenses;
        this.Category = category;
        this.Memo = memo;
        this.Amount = amount;
    }


    public ExcelDataModel(String date, String income_Expenses, String category, String memo, String amount, String payment_mode) {
        this.date = date;
        this.Income_Expenses = income_Expenses;
        this.Category = category;
        this.Memo = memo;
        this.Amount = amount;
        this.payment_mode = payment_mode;
    }

    public ExcelDataModel(String date, String income_Expenses, String category, String memo, String amount, String payment_mode, String userName) {
        this.date = date;
        this.Income_Expenses = income_Expenses;
        this.Category = category;
        this.Memo = memo;
        this.Amount = amount;
        this.payment_mode = payment_mode;
        this.userName = userName;
    }

    public ExcelDataModel(String date, String income_Expenses, String category, String memo, String amount, String payment_mode, String userName, String time) {
        this.date = date;
        this.Income_Expenses = income_Expenses;
        this.Category = category;
        this.Memo = memo;
        this.Amount = amount;
        this.payment_mode = payment_mode;
        this.userName = userName;
        this.time = time;
    }


    public ExcelDataModel(String date, String income_Expenses, String category, String memo, String amount, String payment_mode, String userName, String time, String fileName) {
        this.date = date;
        this.Income_Expenses = income_Expenses;
        this.Category = category;
        this.Memo = memo;
        this.Amount = amount;
        this.payment_mode = payment_mode;
        this.userName = userName;
        this.time = time;
        this.fileName = fileName;
    }

    public ExcelDataModel(String date, String income_Expenses, String category, String memo, String amount, String payment_mode, String userName, String time, String fileName, String tagName) {
        this.date = date;
        this.Income_Expenses = income_Expenses;
        this.Category = category;
        this.Memo = memo;
        this.Amount = amount;
        this.payment_mode = payment_mode;
        this.userName = userName;
        this.time = time;
        this.fileName = fileName;
        this.tagName = tagName;
    }


    public ExcelDataModel(String date, double income, double expense) {
        this.date = date;
        this.inc_amt_ = income;
        this.exp_amt_ = expense;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(String captureDate) {
        this.captureDate = captureDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getUser_relation() {
        return user_relation;
    }

    public void setUser_relation(String user_relation) {
        this.user_relation = user_relation;
    }

    public String getCatIsAdded() {
        return catIsAdded;
    }

    public void setCatIsAdded(String catIsAdded) {
        this.catIsAdded = catIsAdded;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getAmount_() {
        return amount_;
    }

    public void setAmount_(double amount_) {
        this.amount_ = amount_;
    }

    public int getCount_() {
        return count_;
    }

    public void setCount_(int count_) {
        this.count_ = count_;
    }

    public double getExp_amt_() {
        return exp_amt_;
    }

    public void setExp_amt_(double exp_amt_) {
        this.exp_amt_ = exp_amt_;
    }

    public double getInc_amt_() {
        return inc_amt_;
    }

    public void setInc_amt_(double inc_amt_) {
        this.inc_amt_ = inc_amt_;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public void setIcon_name(String icon_name) {
        this.icon_name = icon_name;
    }

    public String getSorted() {
        return sorted;
    }

    public void setSorted(String sorted) {
        this.sorted = sorted;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getExp_amt() {
        return exp_amt;
    }

    public void setExp_amt(int exp_amt) {
        this.exp_amt = exp_amt;
    }

    public int getInc_amt() {
        return inc_amt;
    }

    public void setInc_amt(int inc_amt) {
        this.inc_amt = inc_amt;
    }

    public List<ExcelDataModel> getGroupFocAll() {
        return GroupFocAll;
    }

    public void setGroupFocAll(List<ExcelDataModel> groupFocAll) {
        GroupFocAll = groupFocAll;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIncome_Expenses() {
        return Income_Expenses;
    }

    public void setIncome_Expenses(String income_Expenses) {
        Income_Expenses = income_Expenses;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
