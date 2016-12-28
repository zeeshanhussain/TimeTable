package com.nealgosalia.timetable.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.os.Handler;

import com.nealgosalia.timetable.MainActivity;
import com.nealgosalia.timetable.R;

public class SplashActivity extends Activity {
    private Runnable runnable;
    private Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        initialize();
    }
    private void initialize()
    {
        handler = new Handler();
        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onBackPressed() {
        finish();
        handler.removeCallbacks(runnable);
    }

}