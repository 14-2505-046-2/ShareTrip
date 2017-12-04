package com.example.chai.sharetrip;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TimePicker;

import java.util.Calendar;

import io.realm.Realm;

public class NewRouteFragment extends Fragment implements View.OnClickListener {

    private static final String ROUTE_ID =  "ROUTE_ID";
    private static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private long mRouteId;
    private Realm mRealm;

    public static NewRouteFragment newInstance(long RouteId) {
        NewRouteFragment fragment = new NewRouteFragment();
        Bundle args = new Bundle();
        args.putLong(ROUTE_ID, RouteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRouteId = getArguments().getLong(ROUTE_ID);
        }
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
        View v = inflater.inflate(R.layout.fragment_new_route,container, false);
        EditText editText = (EditText)v.findViewById(R.id.route_title);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("eeeeee", "after");
            }
        });
        RadioGroup radioGroup = (RadioGroup)v.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                ScrollView scrollView_area = (ScrollView) getView().findViewById(R.id.scrollView_area);
                ScrollView scrollView_means = (ScrollView) getView().findViewById(R.id.scrollView_means);
                if(i == R.id.radioButton_area) {
                    scrollView_means.setVisibility(View.GONE);
                    scrollView_area.setVisibility(View.VISIBLE);
                }
                else if(i == R.id.radioButton_means) {
                    scrollView_area.setVisibility(View.GONE);
                    scrollView_means.setVisibility(View.VISIBLE);

                }

            }
        });
        Button button = (Button)v.findViewById(R.id.button_startTime);
        button.setOnClickListener(this);
        Button button1 = (Button)v.findViewById(R.id.button_endTime);
        button1.setOnClickListener(this);
        Button button2 = (Button)v.findViewById(R.id.add);
        button2.setOnClickListener(this);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
    }

    @Override
    public void onClick(View v) {
        if(v == v.findViewById(R.id.button_startTime)) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog dialog = new TimePickerDialog(
                    getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Log.d("test", String.format("%02d:%02d", hourOfDay, minute));
                        }
                    },
                    hour, minute, true);
            dialog.show();
        }
    }
}
