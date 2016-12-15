package com.nealgosalia.timetable.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.nealgosalia.timetable.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class PreferencesActivity extends PreferenceActivity {

    private static final String TAG = "PreferencesActivity";
    private static Context prefContext;

    private static boolean importDatabase(String ePath) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String currentDBPath = sd + "/Timetable/" + ePath;
                String backupDBPath = "data/data/com.nealgosalia.timetable/databases/" + ePath;
                File currentDB = new File(currentDBPath);
                File backupDB = new File(backupDBPath);
                if (currentDB.exists()) {
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

    private static boolean exportDatabase(String mPath) {
        try {
            File outDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Timetable");
            if (!outDir.exists()) {
                Log.d(TAG, "Creating directory");
                outDir.mkdir();
            } else {
                Log.d(TAG, "Directory present");
            }
            String currentDBPath = Environment.getDataDirectory() + "/data/com.nealgosalia.timetable/databases/" + mPath;
            String backupDBPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Timetable/" + mPath;
            File currentDB = new File(currentDBPath);
            File backupDB = new File(backupDBPath);
            if (currentDB.exists()) {
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

    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(prefContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission granted");
                return true;
            } else {
                Log.v(TAG, "Permission denied");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission granted");
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefContext = getApplicationContext();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            Preference backup = findPreference("backup");
            Preference restore = findPreference("restore");
            backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (isStoragePermissionGranted(getActivity())) {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Backup Timetable");
                        alertDialog.setMessage("Do you want to backup the timetable?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String backupDBPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Timetable/";
                                final File subjectDB = new File(backupDBPath + "subject.db");
                                final File lectureDB = new File(backupDBPath + "lecture.db");
                                if (!(subjectDB.exists() || lectureDB.exists())) {
                                    if (exportDatabase("subject.db") && exportDatabase("lecture.db")) {
                                        Toast.makeText(getActivity(), "Backup Successful!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Please create a timetable before trying to export", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    AlertDialog overwriteDialog = new AlertDialog.Builder(getActivity())
                                            .setTitle("Warning!")
                                            .setMessage("Overwrite previous backup?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    subjectDB.delete();
                                                    lectureDB.delete();
                                                    if (exportDatabase("subject.db") && exportDatabase("lecture.db")) {
                                                        Toast.makeText(getActivity(), "Backup Successful!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Please create a timetable before trying to export", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "No Permissions Granted", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (isStoragePermissionGranted(getActivity())) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Restore Timetable");
                        alertDialog.setMessage("Do you want to restore the timetable?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String backupDBPath = "data/data/com.nealgosalia.timetable/databases/";
                                final File subjectDB = new File(backupDBPath + "subject.db");
                                final File lectureDB = new File(backupDBPath + "lecture.db");
                                if (!(subjectDB.exists() || lectureDB.exists())) {
                                    if (importDatabase("subject.db") && importDatabase("lecture.db")) {
                                        Toast.makeText(getActivity(), "Restore Successful!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Backup not found!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    AlertDialog overwriteDialog = new AlertDialog.Builder(getActivity())
                                            .setTitle("Warning!")
                                            .setMessage("Overwrite current timetable?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    subjectDB.delete();
                                                    lectureDB.delete();
                                                    if (importDatabase("subject.db") && importDatabase("lecture.db")) {
                                                        Toast.makeText(getActivity(), "Restore Successful!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Backup not found", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "No Permissions Granted", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        }


    }
}