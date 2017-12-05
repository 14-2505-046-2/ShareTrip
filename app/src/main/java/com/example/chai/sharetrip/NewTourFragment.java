package com.example.chai.sharetrip;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import io.realm.Realm;

//教科書p305～を参考にしています。
public class NewTourFragment extends Fragment /*implements View.OnClickListener*/{

    private static final String TOUR_ID =  "TOUR_ID";
    private static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private long mTourId;
    private Realm mRealm;
    private EditText mTitleEdit;
    private EditText mTotalTimeEdit;
    private EditText mCommentEdit;
    private TextView mTimeText;
    private int startHour;
    private int startMinute;
    private View view;

    public static NewTourFragment newInstance() {
        NewTourFragment fragment = new NewTourFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_tour,container, false);
        view = v;
        //Button btn = (Button)v.findViewById(R.id.startTimeButton);
        //btn.setOnClickListener(this);

        mTimeText = (TextView) view.findViewById(R.id.startTimeText);
        Button save = (Button) view.findViewById(R.id.tourSaveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleEdit = (EditText) view.findViewById(R.id.editTitle);
                mTotalTimeEdit = (EditText) view.findViewById(R.id.editTotalTime);
                mCommentEdit = (EditText) view.findViewById(R.id.editComment);
                Spinner area =(Spinner) view.findViewById(R.id.newTourArea);

                mRealm.beginTransaction();
                Number maxId = mRealm.where(Tour.class).max("tour_id");
                long nextId = 0;
                if(maxId != null) nextId = maxId.longValue() + 1;
                Tour tour = mRealm.createObject(Tour.class, new Long(nextId));
                tour.tour_title = mTitleEdit.getText().toString();
                tour.start_time = mTimeText.getText().toString();
                tour.total_time = Integer.parseInt(mTotalTimeEdit.getText().toString());
                tour.comment = mCommentEdit.getText().toString();
                tour.area = area.getSelectedItem().getClass().toString();
                mRealm.commitTransaction();
                getFragmentManager().popBackStack();
            }
        });
        Button timeButton = (Button) v.findViewById(R.id.startTimeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = calendar.get(java.util.Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d("test", String.format("%02d:%02d", hourOfDay, minute));
                        startHour = hourOfDay;
                        startMinute = minute;
                        mTimeText.setText(String.format("%02d:%02d", startHour, startMinute));
                    }
                },hour, minute, true);
                dialog.show();
            }
        });

        Log.d("title","log");


        Spinner spinner_area = (Spinner)v.findViewById(R.id.newTourArea);
        spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String item = (String)spinner.getSelectedItem();
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute (Realm realm) {
                        Tour tour = realm.where(Tour.class).equalTo("tour_id", mTourId).findFirst();
                        Log.d("tourid","" + mTourId);
                    }
                });
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return v;
    }


    /*@Override
    public void onClick(final View v) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = calendar.get(java.util.Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d("test", String.format("%02d:%02d", hourOfDay, minute));
                startHour = hourOfDay;
                startMinute = minute;
                mTimeText.setText(String.format("%02d:%02d", startHour, startMinute));
            }
        },hour, minute, true);
        dialog.show();
    }
*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
    }
}
