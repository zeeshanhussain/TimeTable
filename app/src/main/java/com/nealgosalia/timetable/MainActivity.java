package com.nealgosalia.timetable;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ncapdevi.fragnav.FragNavController;
import com.nealgosalia.timetable.activities.PreferencesActivity;
import com.nealgosalia.timetable.fragments.SubjectsFragment;
import com.nealgosalia.timetable.fragments.TimetableFragment;
import com.nealgosalia.timetable.fragments.TodayFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;
    boolean doubleBackToExitPressedOnce = false;
    private FragNavController fragNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Fragment> fragments = new ArrayList<>(3);
        fragments.add(new TimetableFragment());
        fragments.add(new TodayFragment());
        fragments.add(new SubjectsFragment());
        fragNavController = new FragNavController(savedInstanceState, getSupportFragmentManager(), R.id.contentContainer, fragments, TAB_FIRST);
        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.noTopOffset();
        bottomBar.setFixedInactiveIconColor(Color.argb(128, 0, 0, 0));
        bottomBar.setItems(R.menu.bottombar_menu);
        bottomBar.setActiveTabColor(Color.argb(255, 255, 255, 255));
        bottomBar.getBar().setBackgroundColor(Color.argb(255, 67, 133, 244));
        bottomBar.setDefaultTabPosition(1);
        bottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_timetable:
                        fragNavController.switchTab(TAB_FIRST);
                        break;
                    case R.id.tab_today:
                        fragNavController.switchTab(TAB_SECOND);
                        break;
                    case R.id.tab_subjects:
                        fragNavController.switchTab(TAB_THIRD);
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back button again to exit!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
