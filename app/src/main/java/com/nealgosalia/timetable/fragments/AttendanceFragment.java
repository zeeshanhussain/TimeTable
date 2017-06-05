package com.nealgosalia.timetable.fragments;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.adapters.AttendanceAdapter;
import com.nealgosalia.timetable.database.SubjectDatabase;
import com.nealgosalia.timetable.database.SubjectDetails;
import com.nealgosalia.timetable.utils.DividerItemDecoration;
import com.nealgosalia.timetable.utils.Subject;

import java.util.ArrayList;
import java.util.List;

public class AttendanceFragment extends Fragment {

    private List<Subject> subjectsList = new ArrayList<>();
    private RecyclerView listSubjects;
    private AttendanceAdapter mAttendanceAdapter;
    private SubjectDatabase subjectDatabase;
    private TextView placeholderText;
    private View view;
    private List<Integer> progressList= new ArrayList<>();
    private Paint p = new Paint();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_attendance, container, false);
        listSubjects = (RecyclerView) view.findViewById(R.id.listAttendance);
        placeholderText = (TextView) view.findViewById(R.id.attendancePlaceholderText);
        subjectDatabase = new SubjectDatabase(getActivity());
        subjectsList.clear();
        for (SubjectDetails subjectDetails : subjectDatabase.getSubjectDetail()) {
            int progress;
            Subject subject = new Subject();
            subject.setSubjectName(subjectDetails.getSubject());
            if(subjectDetails.getTotalLectures()!=0) {
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
        initSwipe();
        return view;
    }

        private void initSwipe(){
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
                        if (direction == ItemTouchHelper.LEFT){
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
                        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                            View itemView = viewHolder.itemView;
                            float height = (float) itemView.getBottom() - (float) itemView.getTop();
                            float width = height / 3;

                            if(dX > 0){
                                p.setColor(Color.parseColor("#4CAF50"));
                                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                                c.drawRect(background,p);
                                icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done);
                                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                                c.drawBitmap(icon,null,icon_dest,p);
                            } else if(dX < 0) {
                                p.setColor(Color.parseColor("#F44336"));
                                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                                c.drawRect(background,p);
                                icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clear);
                                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                                c.drawBitmap(icon,null,icon_dest,p);
                            }
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(listSubjects);
            }
}