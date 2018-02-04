package com.nealgosalia.timetable.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.adapters.AttendanceAdapter;
import com.nealgosalia.timetable.database.SubjectDatabase;
import com.nealgosalia.timetable.database.SubjectDetails;
import com.nealgosalia.timetable.utils.DividerItemDecoration;
import com.nealgosalia.timetable.utils.RecyclerItemClickListener;
import com.nealgosalia.timetable.utils.Subject;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;

public class AttendanceFragment extends Fragment {

    private List<Subject> subjectsList = new ArrayList<>();
    private RecyclerView listSubjects;
    private AttendanceAdapter mAttendanceAdapter;
    private SubjectDatabase subjectDatabase;
    private TextView placeholderText;
    private View view;
    private List<Integer> progressList = new ArrayList<>();
    private Paint p = new Paint();
    private CharSequence options[] = new CharSequence[] {"Bunk Manager", "Update"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_attendance, container, false);
        listSubjects = view.findViewById(R.id.listAttendance);
        placeholderText = view.findViewById(R.id.attendancePlaceholderText);
        subjectDatabase = new SubjectDatabase(getActivity());
        subjectsList.clear();
        for (SubjectDetails subjectDetails : subjectDatabase.getSubjectDetail()) {
            int progress;
            Subject subject = new Subject();
            subject.setSubjectName(subjectDetails.getSubject());
            subject.setAttendedLectures(subjectDetails.getAttendedLectures());
            subject.setTotalLectures(subjectDetails.getTotalLectures());
            if (subjectDetails.getTotalLectures() != 0) {
                progress = (subjectDetails.getAttendedLectures() * 100 / subjectDetails.getTotalLectures());
            } else {
                progress = 0;
            }
            progressList.add(progress);
            subjectsList.add(subject);
        }
        if (subjectsList.size() != 0) {
            placeholderText.setVisibility(View.GONE);
        }
        mAttendanceAdapter = new AttendanceAdapter(subjectsList, progressList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listSubjects.setLayoutManager(mLayoutManager);
        listSubjects.setItemAnimator(new DefaultItemAnimator());
        listSubjects.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        listSubjects.setAdapter(mAttendanceAdapter);
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
                                int temp1,temp2,i=0;
                                int attendedLectures = subjectsList.get(position).getAttendedLectures();
                                int totalLectures = subjectsList.get(position).getTotalLectures();
                                temp1=attendedLectures;
                                temp2=totalLectures;
                                int x= attendedLectures * 100 / totalLectures;
                                while(x<75) {
                                    temp1++;
                                    temp2++;
                                    x = temp1 * 100 / temp2;
                                    i++;
                                }
                                if(i!=0) {
                                    Toast.makeText(getContext(), "You need to attend " + String.valueOf(i) + " lectures", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(getContext(), "Your Attendance is fine", Toast.LENGTH_SHORT).show();
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
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Subject subjectNew = new Subject();
                int attended = subjectsList.get(position).getAttendedLectures();
                int total = subjectsList.get(position).getTotalLectures();
                int progress;
                if (direction == ItemTouchHelper.LEFT) {
                    progress = attended * 100 / (++total);
                } else {
                    progress = (++attended) * 100 / (++total);
                }
                subjectNew.setSubjectName(subjectsList.get(position).getSubjectName());
                subjectNew.setAttendedLectures(attended);
                subjectNew.setTotalLectures(total);
                SubjectDetails sdNew = new SubjectDetails();
                sdNew.setSubject(subjectNew.getSubjectName());
                sdNew.setAttendedLectures(subjectNew.getAttendedLectures());
                sdNew.setTotalLectures(subjectNew.getTotalLectures());
                subjectDatabase.updateSubject(sdNew);
                progressList.set(position, progress);
                subjectsList.set(position, subjectNew);
                mAttendanceAdapter.notifyDataSetChanged();
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
        //int attendedLectures = attendedLecturesNumberPicker.getValue();
        //int totalLectures = ;
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
                int attendedLectures = attendedLecturesNumberPicker.getValue();
                int totalLectures = totalLecturesNumberPicker.getValue();
                SubjectDetails subjectDetails = new SubjectDetails(
                        subject.getSubjectName(),
                        attendedLectures,
                        totalLectures
                );
                subjectDatabase.updateSubject(subjectDetails);
                progressList.set(position, attendedLectures * 100 / totalLectures);
                subject.setAttendedLectures(attendedLectures);
                subject.setTotalLectures(totalLectures);
                subjectsList.set(position, subject);
                mAttendanceAdapter.notifyDataSetChanged();
            }
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}