package com.nealgosalia.timetable.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nealgosalia.timetable.MainActivity;
import com.nealgosalia.timetable.R;

public class SplashActivity extends Activity {

    @Override
    public void onBackPressed() {
        finish();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000); }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finally{
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        thread.start();
    }
}
