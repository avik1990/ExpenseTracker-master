package com.app.exptracker;

import android.content.Context;
import android.content.Intent;

import com.app.exptracker.utility.BaseActivity;

public class SplashActivity extends BaseActivity {

    Context mContext;
    private static final int SPLASH_TIME = 3 * 1000;
    boolean otp_flag = false;
    //String aTime = "2019-11-20 09:50:00";

    @Override
    protected void InitListner() {

        ///Log.e("TimeForamt", Utils.getFormattedTime(aTime));
        /*aTime = "11:11 PM";
        try {
            if (!aTime.isEmpty()) {
                String t[] = aTime.split(":");
                String h = t[0];
                if (Integer.parseInt(h) < 10) {
                    h = "0" + h;
                    aTime = h + ":" + t[1];
                }
            }
            Log.e("TIMEEEEE", aTime);

            SimpleDateFormat h_mm_a = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat hh_mm_ss = new SimpleDateFormat("HH:mm:ss");
            Date d1 = h_mm_a.parse(aTime);
            Log.e("FormattedTime", hh_mm_ss.format(d1));

            //excelDataModel.setTime(tv_date.getText().toString().trim() + " " + hh_mm_ss.format(d1));
        } catch (Exception e) {
            e.printStackTrace();
            //excelDataModel.setTime(tv_date.getText().toString().trim() + " " + aTime);
        }*/


        /*if (otp_flag) {
            goToHomeActivity();
        } else {
            goSliderActivity();
        }*/
    }

    private void goToHomeActivity() {
        Thread background = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_TIME);
                    Intent i = new Intent(getBaseContext(), SLiderActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                }
            }
        };
        background.start();
    }

    private void goSliderActivity() {
        Thread background = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_TIME);
                    Intent i = new Intent(getBaseContext(), SLiderActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                }
            }
        };
        background.start();
    }

    @Override
    protected void InitResources() {
        mContext = this;
    }

    @Override
    protected void InitPermission() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }
}
