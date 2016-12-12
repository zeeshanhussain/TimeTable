package com.nealgosalia.timetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;

import com.nealgosalia.timetable.activities.PreferencesActivity;
import com.nealgosalia.timetable.activities.SubjectsActivity;
import com.nealgosalia.timetable.activities.TimetableActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Timetable";
    private Button btnSubjects, btnTimetable,importdb,exportdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSubjects = (Button) findViewById(R.id.btnSubjects);
        btnSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubjectsActivity.class);
                startActivity(intent);
            }
        });
        btnTimetable = (Button) findViewById(R.id.btnTimetable);
        btnTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimetableActivity.class);
                startActivity(intent);
            }
        });
        importdb = (Button) findViewById(R.id.importdb);
        importdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Restore Timetable");
                alertDialog.setMessage("Do you want to Restore Timetable?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        importDatabase("subject.db");
                        importDatabase("lecture.db");
                    }
                });
                alertDialog.setNegativeButton("No", null);
                alertDialog.show();

            }
        });
        exportdb = (Button) findViewById(R.id.exportdb);
        exportdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Backup Timetable");
                alertDialog.setMessage("Do you want to Backup Timetable?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exportDatabase("subject.db");
                        exportDatabase("lecture.db");
                    }
                });
                alertDialog.setNegativeButton("No", null);
                alertDialog.show();
            }
        });
    }

    private void importDatabase(String ePath) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String currentDBPath = sd + "/Timetable/" + ePath;
                String backupDBPath = "data/data/com.nealgosalia.timetable/databases/" + ePath;
                File currentDB = new File(currentDBPath);
                File backupDB = new File(backupDBPath);
                if(currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(this, "Import Successfull", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Backup doesn't exists", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Import failed!", Toast.LENGTH_SHORT).show();
        }
    }
    private void exportDatabase(String mPath) {
        try {
            File outDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Timetable");
            if (!outDir.exists()) {
                Log.d(TAG, "Creating directory");
                outDir.mkdirs();
            } else {
                Log.d(TAG, "Directory present");
            }
            String currentDBPath =  Environment.getDataDirectory() + "/data/com.nealgosalia.timetable/databases/" + mPath;
                String backupDBPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Timetable/" + mPath;
                File currentDB = new File(currentDBPath);
                File backupDB = new File(backupDBPath);
            if(currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getApplicationContext(), "Backup Successful!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Database doesn't exists go and make your timetable before exporting", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Backup Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
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
