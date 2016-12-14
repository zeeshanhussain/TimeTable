package com.nealgosalia.timetable;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        Button btnSubjects, btnTimetable,importdb,exportdb;
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
                if (isStoragePermissionGranted()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Restore Timetable");
                    alertDialog.setMessage("Do you want to restore the timetable?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String backupDBPath = "data/data/com.nealgosalia.timetable/databases/";
                            final File subjectDB=new File(backupDBPath+"subject.db");
                            final File lectureDB=new File(backupDBPath+"lecture.db");
                            if(!(subjectDB.exists()||lectureDB.exists())) {
                                if (importDatabase("subject.db") && importDatabase("lecture.db")) {
                                    Toast.makeText(getApplicationContext(), "Restore Successful!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Backup not found!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                AlertDialog overwriteDialog=new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Warning!")
                                        .setMessage("Overwrite current timetable?")
                                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                subjectDB.delete();
                                                lectureDB.delete();
                                                if(importDatabase("subject.db")&&importDatabase("lecture.db")){
                                                    Toast.makeText(getApplicationContext(), "Restore Successful!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Backup not found", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .create();
                                overwriteDialog.show();
                            }
                        }
                    });
                    alertDialog.setNegativeButton("No", null);
                    alertDialog.show();

                } else {
                    Toast.makeText(MainActivity.this, "No Permissions Granted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        exportdb = (Button) findViewById(R.id.exportdb);
        exportdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Backup Timetable");
                    alertDialog.setMessage("Do you want to backup the timetable?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String backupDBPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Timetable/";
                            final File subjectDB=new File(backupDBPath+"subject.db");
                            final File lectureDB=new File(backupDBPath+"lecture.db");
                            if(!(subjectDB.exists()||lectureDB.exists())) {
                                if(exportDatabase("subject.db")&&exportDatabase("lecture.db")){
                                    Toast.makeText(getApplicationContext(), "Backup Successful!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please create a timetable before trying to export", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                AlertDialog overwriteDialog=new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Warning!")
                                        .setMessage("Overwrite previous backup?")
                                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                subjectDB.delete();
                                                lectureDB.delete();
                                                if(exportDatabase("subject.db")&&exportDatabase("lecture.db")){
                                                    Toast.makeText(getApplicationContext(), "Backup Successful!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Please create a timetable before trying to export", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .create();
                                overwriteDialog.show();
                            }
                        }
                    });
                    alertDialog.setNegativeButton("No", null);
                    alertDialog.show();
                } else {
                    Toast.makeText(MainActivity.this, "No Permissions Granted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean importDatabase(String ePath) {
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
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean exportDatabase(String mPath) {
        try {
            File outDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Timetable");
            if (!outDir.exists()) {
                Log.d(TAG, "Creating directory");
                outDir.mkdir();
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
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission granted");
                return true;
            } else {
                Log.v(TAG,"Permission denied");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
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
