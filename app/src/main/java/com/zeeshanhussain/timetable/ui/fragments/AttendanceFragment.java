package com.zeeshanhussain.timetable.ui.fragments;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zeeshanhussain.timetable.database.AppDatabase;
import com.zeeshanhussain.timetable.utils.AppExecutors;
import com.zeeshanhussain.timetable.viewmodel.MainViewModel;
import com.zeeshanhussain.timetable.R;
import com.zeeshanhussain.timetable.adapters.AttendanceAdapter;
import com.zeeshanhussain.timetable.utils.DividerItemDecoration;
import com.zeeshanhussain.timetable.utils.RecyclerItemClickListener;
import com.zeeshanhussain.timetable.model.Subject;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;

public class AttendanceFragment extends Fragment {
    public static final String TARGET_ATTENDANCE = "target_attendance";
    public static final String ATTENDANCE_PREFS = "attendancePrefs";
    public static final String DEF_TARGET_ATTENDANCE = "75";

    private int targetAttendance;
    private List<Subject> subjectsList = new ArrayList<>();
    private RecyclerView listSubjects;
    private AttendanceAdapter mAttendanceAdapter;
    private TextView placeholderText;
    private View view;
    private List<Integer> progressList = new ArrayList<>();
    private Paint p = new Paint();
    private CharSequence options[] = new CharSequence[] {"Bunk Manager", "Update"};
    int attended,total;
    MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_attendance, container, false);
        Context context = getContext();

        if (context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            targetAttendance = Integer.parseInt(
                    prefs.getString(TARGET_ATTENDANCE, DEF_TARGET_ATTENDANCE));
        } else
            targetAttendance = Integer.parseInt(DEF_TARGET_ATTENDANCE);

        listSubjects = view.findViewById(R.id.listAttendance);
        placeholderText = view.findViewById(R.id.attendancePlaceholderText);
        mAttendanceAdapter = new AttendanceAdapter(subjectsList, progressList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listSubjects.setLayoutManager(mLayoutManager);
        listSubjects.setItemAnimator(new DefaultItemAnimator());
        listSubjects.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        listSubjects.setAdapter(mAttendanceAdapter);
        mainViewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getSubject().observe(this, new Observer<List<Subject>>() {
            @Override
            public void onChanged(@Nullable List<Subject> subjects) {
                subjectsList.clear();
                progressList.clear();
                subjectsList.addAll(subjects);
                for (int i=0;i<subjectsList.size();i++){
                    int progress;
                    if(subjectsList.get(i).getTotalLectures()!=0){
                        progress=subjectsList.get(i).getAttendedLectures() * 100 / subjectsList.get(i).getTotalLectures();
                    } else{
                        progress=0;
                    }
                    progressList.add(progress);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAttendanceAdapter.notifyDataSetChanged();
                        }
                    });
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (subjectsList.size() != 0) {
                            placeholderText.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        listSubjects.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final Subject subject = subjectsList.get(position);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Options");
                alertDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //check if you can Bunk or not
                                int targetOffset;
                                int attendedLectures = subjectsList.get(position).getAttendedLectures();
                                int totalLectures = subjectsList.get(position).getTotalLectures();
                                if(attendedLectures==0){
                                    Toast.makeText(getContext(), "Please update your attendance", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    targetOffset=attendedLectures - (totalLectures * targetAttendance / 100);
                                    if(targetOffset==0)
                                        Toast.makeText(getContext(), "you can\'t bunk any lectures", Toast.LENGTH_SHORT).show();
                                    else {
                                        String plural = Math.abs(targetOffset)==1?"":"s";
                                        if(targetOffset>0)
                                            Toast.makeText(getContext(), "you can bunk " +targetOffset+" lecture"+plural, Toast.LENGTH_SHORT).show();
                                        if(targetOffset<0)
                                        Toast.makeText(getContext(), "you need to attend " +(-1*targetOffset)+" lecture"+plural, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            case 1:
                                //update Attendance manually
                                showAttendanceDialog(subject, position);

                        }
                    }
                });
                alertDialog.show();

            }
        }));
        initSwipe();
        return view;
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder,final int direction) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final int position = viewHolder.getAdapterPosition();
                        final Subject subjects = subjectsList.get(position);
                        attended = subjectsList.get(position).getAttendedLectures();
                        total = subjectsList.get(position).getTotalLectures();
                        final int progress;
                        if (direction == ItemTouchHelper.LEFT) {
                            progress = attended * 100 / (++total);
                        } else {
                            progress = (++attended) * 100 / (++total);
                        }
                        subjects.setAttendedLectures(attended);
                        subjects.setTotalLectures(total);
                        mainViewModel.updateAttendance(attended,total,subjectsList.get(position).getId());
                        progressList.set(position, progress);
                        subjectsList.set(position, subjects);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAttendanceAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                });


            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#4CAF50"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else if (dX < 0) {
                        p.setColor(Color.parseColor("#F44336"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clear);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(listSubjects);
    }

    public void showAttendanceDialog(final Subject subject, final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_attendance, null);
        final NumberPicker attendedLecturesNumberPicker = dialogView.findViewById(R.id.attendedLecturesNumberPicker);
        final NumberPicker totalLecturesNumberPicker = dialogView.findViewById(R.id.totalLecturesNumberPicker);
        attendedLecturesNumberPicker.setValue(subject.getAttendedLectures());
        totalLecturesNumberPicker.setValue(subject.getTotalLectures());
        attendedLecturesNumberPicker.setMaxValue(totalLecturesNumberPicker.getValue());
        totalLecturesNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                attendedLecturesNumberPicker.setMaxValue(totalLecturesNumberPicker.getValue());
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(getResources().getString(R.string.attendance) + ": " + subject.getSubjectName());
        dialogBuilder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int attendedLectures = attendedLecturesNumberPicker.getValue();
                        int totalLectures = totalLecturesNumberPicker.getValue();
                        mainViewModel.updateAttendance(attendedLectures,totalLectures,subjectsList.get(position).getId());
                        int x;
                        if (attendedLectures==0 && totalLectures==0){
                            x=0;
                        }else{
                            x = attendedLectures * 100 / totalLectures;
                        }
                        progressList.set(position, x);
                        subject.setAttendedLectures(attendedLectures);
                        subject.setTotalLectures(totalLectures);
                        subjectsList.set(position, subject);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAttendanceAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

            }
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}