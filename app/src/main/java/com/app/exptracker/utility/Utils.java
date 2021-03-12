package com.app.exptracker.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.model.ExcelDataModelDateCompDesc;
import com.app.exptracker.model.Months;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class Utils {

    public static String FOLDER_PATH = "/Expensetracker/";
    public static String BACKUP_FOLDER_PATH = "/Expensetracker/Backup/";
    public static String SHARED_FOLDER_PATH = "/Expensetracker/Share/";
    public static void ShowToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static List<Months> getAllMonths() {
        List<Months> months = new ArrayList<>();
        months.clear();
        months.add(new Months("Jan", "1"));
        months.add(new Months("Feb", "2"));
        months.add(new Months("Mar", "3"));
        months.add(new Months("Apr", "4"));
        months.add(new Months("May", "5"));
        months.add(new Months("Jun", "6"));
        months.add(new Months("Jul", "7"));
        months.add(new Months("Aug", "8"));
        months.add(new Months("Sep", "9"));
        months.add(new Months("Oct", "10"));
        months.add(new Months("Nov", "11"));
        months.add(new Months("Dec", "12"));
        return months;
    }

    public static String getCurrentMonthsIndex(String month) {
        String monthindex = "";
        switch (month) {
            case "Jan":
                return monthindex = "01";
            case "Feb":
                return monthindex = "02";
            case "Mar":
                return monthindex = "03";
            case "Apr":
                return monthindex = "04";
            case "May":
                return monthindex = "05";
            case "Jun":
                return monthindex = "06";
            case "Jul":
                return monthindex = "07";
            case "Aug":
                return monthindex = "08";
            case "Sep":
                return monthindex = "09";
            case "Oct":
                return monthindex = "10";
            case "Nov":
                return monthindex = "11";
            case "Dec":
                return monthindex = "12";
        }
        return monthindex;
    }


    public static String getCurrentMonths(String index) {
        String monthindex = "";
        switch (index) {
            case "01":
                return monthindex = "Jan";
            case "02":
                return monthindex = "Feb";
            case "03":
                return monthindex = "Mar";
            case "04":
                return monthindex = "Apr";
            case "05":
                return monthindex = "May";
            case "06":
                return monthindex = "Jun";
            case "07":
                return monthindex = "Jul";
            case "08":
                return monthindex = "Aug";
            case "09":
                return monthindex = "Sep";
            case "10":
                return monthindex = "Oct";
            case "11":
                return monthindex = "Nov";
            case "12":
                return monthindex = "Dec";
        }
        return monthindex;
    }


    public static String getCurrentMonths() {
        String months = "";
        Calendar cal = Calendar.getInstance();
        months = new SimpleDateFormat("MMM").format(cal.getTime());
        Log.d("CurrentMonth", months);
        // System.out.println(new SimpleDateFormat("MMM").format(cal.getTime()));
        return months;
    }

    public static String getCurrentYear() {
        String months = "";
        Calendar cal = Calendar.getInstance();
        months = new SimpleDateFormat("yyyy").format(cal.getTime());
        Log.d("CurrentYear", months);
        //System.out.println(new SimpleDateFormat("MMM").format(cal.getTime()));
        return months;
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        return formattedDate;
    }


    public static String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.ENGLISH);
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getCurrentDateTimeSync() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getFormattedDate(String normal_date) {
        String anni = normal_date;
        String formated_date = "";
        if (anni.length() > 6) {
            //SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy (EEE)");
            Date date;
            try {
                date = originalFormat.parse(anni);
                formated_date = targetFormat.format(date);  // 20120821
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            formated_date = anni;
        }
        return formated_date;
    }

    public static String getFormattedDateCalender(String normal_date) {
        String anni = normal_date;
        String formated_date = "";
        if (anni.length() > 6) {
            //SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM (EEE)");
            Date date;
            try {
                date = originalFormat.parse(anni);
                formated_date = targetFormat.format(date);  // 20120821
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            formated_date = anni;
        }
        return formated_date;
    }


    public static String getFormattedDateTime1(String normal_date) {
        String anni = normal_date;
        String formated_date = "";
        if (anni.length() > 6) {
            //SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy (EEE) ,hh:mm a");
            Date date;
            try {
                date = originalFormat.parse(anni);
                formated_date = targetFormat.format(date);  // 20120821
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            formated_date = anni;
        }
        return formated_date;
    }


    public static String getFormattedTime(String normal_date) {
        String anni = normal_date;
        String formated_date = "";
        if (anni.length() > 6) {
            //SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat targetFormat = new SimpleDateFormat("hh:mm a");
            Date date;
            try {
                date = originalFormat.parse(anni);
                formated_date = targetFormat.format(date);  // 20120821
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            formated_date = anni;
        }
        return formated_date;
    }


    public static String getFormattedDateTime(String normal_date) {
        String anni = normal_date;
        String formated_date = "";
        if (anni.length() > 6) {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            Date date;
            try {
                date = originalFormat.parse(anni);
                formated_date = targetFormat.format(date);  // 20120821
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            formated_date = anni;
        }
        return formated_date;
    }


    /*public static void openNavDrawer(int id, final Context mContext) {
        if (id == R.id.nav_import) {
            //Toast.makeText(mContext, "Hello", Toast.LENGTH_SHORT).show();
        }
    }*/

    public static boolean getIsExcelReadDone(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        boolean flag = preferences.getBoolean("msg", false);
        return flag;
    }

    public static void setIsExcelReadDone(Context mContext, boolean flag) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("msg", flag);
        editor.apply();
    }

    public static boolean getIsDateShow(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        boolean flag = preferences.getBoolean("isShow", false);
        return flag;
    }

    public static void setIsDateShow(Context mContext, boolean flag) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isShow", flag);
        editor.apply();
    }


    public static boolean getIsCaptureTime(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        boolean flag = preferences.getBoolean("isCaptureTime", false);
        return flag;
    }

    public static void setIsCaptureTime(Context mContext, boolean flag) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isCaptureTime", flag);
        editor.apply();
    }


    public static boolean getIsCaptureTimeON(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        boolean flag = preferences.getBoolean("isCaptureTimeON", false);
        return flag;
    }

    public static void setIsCaptureTimeON(Context mContext, boolean flag) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isCaptureTimeON", flag);
        editor.apply();
    }


    public static void shareAll(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent shareIntent = ShareCompat.IntentBuilder.from((Activity) context)
                .setType("*/*")
                .setStream(uri)
                .getIntent();
        context.startActivity(Intent.createChooser(shareIntent, "Share Using"));
    }


    public static boolean getIsUpdated(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        boolean flag = preferences.getBoolean("msgValue", false);
        return flag;
    }

    public static void setIsUpdated(Context mContext, boolean flag) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("msgValue", flag);
        editor.apply();
    }

    public static String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        // File file = url;
        //   Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void copyDatabase() {
        try {
            final String inFileName = "/data/data/com.app.expensetracker/databases/expenses.db";
            final String outFileName = Environment.getExternalStorageDirectory() + "exp_back.db";
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);


            Log.d("Export", "copyDatabase: outFile = " + outFileName);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
        } catch (Exception e) {
            Log.d("Export", "copyDatabase: backup error");
        }
    }

    public static void ExportToExcelSheet(List<ExcelDataModel> a,
                                          String startDate, String endDate, File apkStorage,
                                          File outputFile, Context context, String isShare) {
        Collections.sort(a, new ExcelDataModelDateCompDesc());
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

        if (a.size() > 0) {
            for (int i = 0; i < a.size(); i++) {
                data.put((i + 2), new Object[]{
                        a.get(i).getDate(),
                        a.get(i).getIncome_Expenses(),
                        a.get(i).getCategory(),
                        a.get(i).getMemo(),
                        String.valueOf(a.get(i).getAmount_()),
                        a.get(i).getPayment_mode(),
                        a.get(i).getUserName(),
                        a.get(i).getTime(),
                        a.get(i).getFileName(),
                        a.get(i).getTagName()});
            }

            Set<Integer> keyset = data.keySet();
            int rownum = 0;
            for (Integer key : keyset) {
                Row row = sheet.createRow(rownum++);
                Object[] objArr = data.get(key);
                Log.e("KYES", Arrays.toString(data.get(key)));
                int cellnum = 0;

                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    if (obj instanceof String)
                        cell.setCellValue((String) obj);
                    else if (obj instanceof Integer)
                        cell.setCellValue((Integer) obj);
                }
            }

            outputFile = new File(apkStorage, "ETracker_" + startDate + "-" + endDate + ".xls"); //Create Output file in Main File

            try {
                FileOutputStream out = new FileOutputStream(outputFile);
                workbook.write(out);
                out.close();

                final Timer t = new Timer();
                File finalOutputFile = outputFile;
                t.schedule(new TimerTask() {
                    public void run() {
                        try {
                            if (isShare.equalsIgnoreCase("1")) {
                                shareAll(context, finalOutputFile);
                            } else if (isShare.equalsIgnoreCase("2")) {
                                openFile(context, finalOutputFile);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                    }
                }, 200);

            } catch (Exception e) {
            }
        } else {
            Utils.ShowToast(context, "No data for Export");
        }
    }


    public static void ExportAndSaveToLocal(List<ExcelDataModel> a, File apkStorage, File outputFile, Context context) {
        Collections.sort(a, new ExcelDataModelDateCompDesc());
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        Map<Integer, Object[]> data = new TreeMap<>();
        data.put(1, new Object[]{"Date", "Income/Expenses", "Category", "Memo", "Amount", "Payment Mode", "User Name", "DateTime", "File Name","Tags"});

        if (a.size() > 0) {
            for (int i = 0; i < a.size(); i++) {
                data.put((i + 2), new Object[]{
                        a.get(i).getDate(),
                        a.get(i).getIncome_Expenses(),
                        a.get(i).getCategory(),
                        a.get(i).getMemo(),
                        String.valueOf(a.get(i).getAmount_()),
                        a.get(i).getPayment_mode(),
                        a.get(i).getUserName(),
                        a.get(i).getTime(),
                        a.get(i).getFileName(),
                        a.get(i).getTagName()});
            }

            Set<Integer> keyset = data.keySet();
            int rownum = 0;
            for (Integer key : keyset) {
                Row row = sheet.createRow(rownum++);
                Object[] objArr = data.get(key);
                Log.e("KYES", Arrays.toString(data.get(key)));
                int cellnum = 0;

                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    if (obj instanceof String)
                        cell.setCellValue((String) obj);
                    else if (obj instanceof Integer)
                        cell.setCellValue((Integer) obj);
                }
            }

            outputFile = new File(apkStorage, "ETracker_" + Utils.getCurrentDateTime() + ".xls"); //Create Output file in Main File

            try {
                FileOutputStream out = new FileOutputStream(outputFile);
                workbook.write(out);
                out.close();
                /*final Timer t = new Timer();
                File finalOutputFile = outputFile;
                t.schedule(new TimerTask() {
                    public void run() {
                       *//* try {
                            if (isShare.equalsIgnoreCase("1")) {
                                shareAll(context, finalOutputFile);
                            } else if (isShare.equalsIgnoreCase("2")) {
                                openFile(context, finalOutputFile);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*//*
                        t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                    }
                }, 200);*/
            } catch (Exception e) {

            }
        } else {
            Utils.ShowToast(context, "No data for Export");
        }
    }





    /*private boolean isDeviceOnline(Context mContext) {
        ConnectivityManager connMgr =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.e(this.toString(), "Checking if device");
        return (networkInfo != null && networkInfo.isConnected());

    }

    private boolean isGooglePlayServicesAvailable(Context mContext) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices(Context mContext) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode,mContext);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode,Context mContext) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                (Activity) mContext,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }*/

}
