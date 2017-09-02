package com.nealgosalia.timetable.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.nealgosalia.timetable.MainActivity;
import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.activities.PreferencesActivity;
import com.nealgosalia.timetable.utils.Alarms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by men_in_black007 on 15/12/16.
 */

public class MyPreferenceFragment extends PreferenceFragment {

    private static final String TAG = "MyPreferenceFragment";
    private static List<Alarms> alarmsList = new ArrayList<>();
    private PreferencesActivity mActivity;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        mActivity = (PreferencesActivity) getActivity();
        ListPreference notificationTime = (ListPreference) findPreference("notificationTime");
        notificationTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("NOTIFICATION_TIME", (String) o);
                editor.apply();
                if (alarmsList.size() != 0) {
                    for (Alarms alarm : alarmsList) {
                        AlarmManager alarmManager = (AlarmManager) alarm.getContext().getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(alarm.getPendingIntent());
                    }
                }
                if (!o.equals("-1")) {
                    Intent i = new Intent();
                    i.setAction("com.nealgosalia.timetable.NOTIFY");
                    getActivity().sendBroadcast(i);
                    Log.d(TAG, "Broadcasted");
                }
                return true;
            }
        });
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("NOTIFICATION_TIME", notificationTime.getValue());
        editor.apply();
        Preference backup = findPreference("backup");
        Preference restore = findPreference("restore");
        Preference reset = findPreference("reset");
        backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mActivity.isStoragePermissionGranted(getActivity())) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getResources().getString(R.string.backup));
                    alertDialog.setMessage(getResources().getString(R.string.backup_question));
                    alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String backupDBPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Timetable/";
                            final File subjectDB = new File(backupDBPath + "subject.db");
                            final File lectureDB = new File(backupDBPath + "lecture.db");
                            if (!(subjectDB.exists() || lectureDB.exists())) {
                                if (mActivity.exportDatabase("subject.db") && mActivity.exportDatabase("lecture.db")) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.backup_successful), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.please_create_timetable), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                AlertDialog overwriteDialog = new AlertDialog.Builder(getActivity())
                                        .setTitle(getResources().getString(R.string.warning))
                                        .setMessage(getResources().getString(R.string.overwrite_backup))
                                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                subjectDB.delete();
                                                lectureDB.delete();
                                                if (mActivity.exportDatabase("subject.db") && mActivity.exportDatabase("lecture.db")) {
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.backup_successful), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.please_create_timetable), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.no), null)
                                        .create();
                                overwriteDialog.show();
                            }
                        }
                    });
                    alertDialog.setNegativeButton(getResources().getString(R.string.no), null);
                    alertDialog.show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_permission_granted), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mActivity.isStoragePermissionGranted(getActivity())) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getResources().getString(R.string.restore));
                    alertDialog.setMessage(getResources().getString(R.string.restore_question));
                    alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String backupDBPath = "data/data/com.nealgosalia.timetable/databases/";
                            final File subjectDB = new File(backupDBPath + "subject.db");
                            final File lectureDB = new File(backupDBPath + "lecture.db");
                            if (!(subjectDB.exists() || lectureDB.exists())) {
                                if (mActivity.importDatabase("subject.db") && mActivity.importDatabase("lecture.db")) {
                                    restartApplication();
                                    Toast.makeText(getActivity(), getResources().getString(R.string.restore_successful), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.backup_not_found), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                AlertDialog overwriteDialog = new AlertDialog.Builder(getActivity())
                                        .setTitle(getResources().getString(R.string.warning))
                                        .setMessage(getResources().getString(R.string.overwrite_timetable))
                                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                subjectDB.delete();
                                                lectureDB.delete();
                                                if (mActivity.importDatabase("subject.db") && mActivity.importDatabase("lecture.db")) {
                                                    restartApplication();
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.restore_successful), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.backup_not_found), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.no), null)
                                        .create();
                                overwriteDialog.show();
                            }
                        }
                    });
                    alertDialog.setNegativeButton(getResources().getString(R.string.no), null);
                    alertDialog.show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_permission_granted), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getResources().getString(R.string.reset_the_timetable));
                alertDialog.setMessage(getResources().getString(R.string.reset_question));
                alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String resetDBPath = "data/data/com.nealgosalia.timetable/databases/";
                        final File subjectDB = new File(resetDBPath + "subject.db");
                        final File lectureDB = new File(resetDBPath + "lecture.db");
                        if (subjectDB.exists() && lectureDB.exists()) {
                            subjectDB.delete();
                            lectureDB.delete();
                        }
                        restartApplication();
                        Toast.makeText(mActivity, getResources().getString(R.string.reset_successful), Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setNegativeButton(getResources().getString(R.string.no), null);
                alertDialog.show();
                return false;
            }
        });
    }

    public void addAlarm(Alarms alarm) {
        alarmsList.add(alarm);
    }

    private void restartApplication() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().finish();
        startActivity(intent);
    }
}
