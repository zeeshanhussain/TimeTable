package com.zeeshanhussain.timetable.ui.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.zeeshanhussain.timetable.utils.AppExecutors;
import com.zeeshanhussain.timetable.viewmodel.MainViewModel;
import com.zeeshanhussain.timetable.R;
import com.zeeshanhussain.timetable.adapters.SubjectsAdapter;
import com.zeeshanhussain.timetable.utils.DividerItemDecoration;
import com.zeeshanhussain.timetable.model.Subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubjectsFragment extends Fragment {

    private List<Subject> subjectsList = new ArrayList<>();
    private RecyclerView listSubjects;
    private SubjectsAdapter mSubjectsAdapter;
    private TextView placeholderText;
    private View view;
    private View dialogView;
    private Paint p = new Paint();
    private AlertDialog.Builder alertDialog;
    private AutoCompleteTextView editSubject;
    private AutoCompleteTextView newSubjectName;
    private MainViewModel mainViewModel;

    String[] sub;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_subjects, container, false);
        listSubjects = view.findViewById(R.id.listSubjects);
        placeholderText = view.findViewById(R.id.subjectsPlaceholderText);
        mSubjectsAdapter = new SubjectsAdapter(subjectsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listSubjects.setLayoutManager(mLayoutManager);
        listSubjects.setItemAnimator(new DefaultItemAnimator());
        listSubjects.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        listSubjects.setAdapter(mSubjectsAdapter);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getSubject().observe(this, new Observer<List<Subject>>() {
            @Override
            public void onChanged(@Nullable List<Subject> subjects) {
                subjectsList.clear();
                subjectsList.addAll(subjects);
                Log.d("Size",String.valueOf(subjectsList.size()));
                mSubjectsAdapter.notifyDataSetChanged();
                if (subjectsList.size() != 0) {
                    placeholderText.setVisibility(View.GONE);
                }
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSubjectDialog();
            }
        });
        initSwipe();
        return view;
    }

    public void showSubjectDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_subject, null);
        dialogBuilder.setView(dialogView);
        newSubjectName = dialogView.findViewById(R.id.newSubjectName);
        sub = getResources().getStringArray(R.array.subjectNames);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,sub);
        newSubjectName.setThreshold(2);
        newSubjectName.setAdapter(adapter);
        dialogBuilder.setTitle(getResources().getString(R.string.subject));
        dialogBuilder.setMessage(getResources().getString(R.string.enter_subject_name));
        dialogBuilder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String tempSubject = newSubjectName.getText().toString().trim();
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mainViewModel.insertSubject(new Subject(tempSubject,0,0));
                        subjectsList.add(new Subject(tempSubject,0,0));
                        Collections.sort(subjectsList, Subject.Comparators.NAME);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSubjectsAdapter.notifyDataSetChanged();
                                placeholderText.setVisibility(View.GONE);
                                newSubjectName.setText("");
                            }
                        });


                    }
                });


            }
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        newSubjectName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
             if(editable.length()>=1){
                 dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
             } else  {
                 dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
             }
            }
        });
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

                if (direction == ItemTouchHelper.LEFT){
                    deleteSwipe(position);
                } else {
                    initDialog(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#FF5722"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else if(dX < 0) {
                        p.setColor(Color.parseColor("#009688"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
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
    private void initDialog(final int position){
        alertDialog = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_subject,null);
        editSubject = dialogView.findViewById(R.id.edit_subject);
        sub = getResources().getStringArray(R.array.subjectNames);
        ArrayAdapter<String> adapte = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,sub);
        if(editSubject !=null) {
            editSubject.setThreshold(2);
            editSubject.setAdapter(adapte);
        }
        alertDialog.setView(dialogView);
        alertDialog.setTitle(getResources().getString(R.string.edit_subject));
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mainViewModel.updateSubject(editSubject.getText().toString(),subjectsList.get(position).getId());
                            subjectsList.set(position,new Subject(editSubject.getText().toString()));
                            Collections.sort(subjectsList, Subject.Comparators.NAME);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSubjectsAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });

                    dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSubjectsAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        editSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 1) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    mSubjectsAdapter.notifyDataSetChanged();
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    mSubjectsAdapter.notifyDataSetChanged();
                }
            }
        });
        mSubjectsAdapter.notifyDataSetChanged();
    }

    public void deleteSwipe(final int position) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getResources().getString(R.string.warning));
        alertDialog.setMessage(getResources().getString(R.string.subject_question));
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Subject subject = subjectsList.get(position);
                        mainViewModel.deleteSubject(subject);
                        subjectsList.remove(position);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSubjectsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSubjectsAdapter.notifyDataSetChanged();
            }
        });
        alertDialog.show();
        mSubjectsAdapter.notifyDataSetChanged();
    }
}