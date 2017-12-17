package com.example.chai.sharetrip;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;

import io.realm.Realm;

import static android.app.Activity.RESULT_OK;

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
    private ImageView mImageView;
    private CheckBox mIslocal;
    private EditText auther_txt;

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

        Bundle bundle = getArguments();
        final long tour_id = bundle.getLong("id", -2);
        //ツアーの編集の場合。
        if(tour_id > 0) {
            Tour selected_tour = mRealm.where(Tour.class).equalTo("tour_id", tour_id).findFirst();
            mTitleEdit = (EditText) view.findViewById(R.id.editTitle);
            mTotalTimeEdit = (EditText) view.findViewById(R.id.editTotalTime);
            mCommentEdit = (EditText) view.findViewById(R.id.editComment);
            mIslocal = (CheckBox) view.findViewById(R.id.is_localcheck);
            Spinner area = (Spinner) view.findViewById(R.id.newTourArea);
            ImageView imageView = (ImageView) view.findViewById(R.id.tour_image);
            TextView startTime = (TextView) view.findViewById(R.id.startTimeText);
            auther_txt = (EditText) view.findViewById(R.id.auther_txt);

            mTitleEdit.setText(selected_tour.tour_title);
            mTotalTimeEdit.setText(String.valueOf(selected_tour.total_time));
            mCommentEdit.setText(selected_tour.comment);
            mIslocal.setChecked(selected_tour.is_local);
            auther_txt.setText(selected_tour.author);

            int index = 0;
            switch (selected_tour.area) {
                case "下関市":
                    index = 1;
                    break;
                case "山陽小野田市":
                    index = 2;
                    break;
                case "宇部市":
                    index = 3;
                    break;
                case "美祢市":
                    index = 4;
                    break;
                case "山口市":
                    index = 5;
                    break;
                case "長門市":
                    index = 6;
                    break;
                case "萩市":
                    index = 7;
                    break;
                case "防府市":
                    index = 8;
                    break;
                case "下松市":
                    index = 9;
                    break;
                case "光市":
                    index = 10;
                    break;
                case "周南市":
                    index = 11;
                    break;
                case "柳井市":
                    index = 12;
                    break;
                case "岩国市":
                    index = 13;
                    break;
            }
            area.setSelection(index);
            imageView.setImageBitmap(MyUtils.getImageFromByte(selected_tour.image));
            startTime.setText(selected_tour.start_time);
        }

        mTimeText = (TextView) view.findViewById(R.id.startTimeText);

        Button save = (Button) view.findViewById(R.id.tourSaveButton);
        mImageView = (ImageView) v.findViewById(R.id.tour_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestReadStorage(view);
            }
        });
        Button timeButton = (Button) v.findViewById(R.id.startTimeButton);
        Spinner spinner_area = (Spinner)v.findViewById(R.id.newTourArea);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tour_id < 0) {
                    mTitleEdit = (EditText) view.findViewById(R.id.editTitle);
                    mTotalTimeEdit = (EditText) view.findViewById(R.id.editTotalTime);
                    mCommentEdit = (EditText) view.findViewById(R.id.editComment);
                    mIslocal = (CheckBox) view.findViewById(R.id.is_localcheck);
                    Spinner area = (Spinner) view.findViewById(R.id.newTourArea);
                    ImageView imageView = (ImageView) view.findViewById(R.id.tour_image);
                    auther_txt = (EditText) view.findViewById(R.id.auther_txt);

                    mRealm.beginTransaction();
                    Number maxId = mRealm.where(Tour.class).max("tour_id");
                    long nextId = 0;
                    if (maxId != null) nextId = maxId.longValue() + 1;
                    Tour tour = mRealm.createObject(Tour.class, new Long(nextId));
                    tour.tour_title = mTitleEdit.getText().toString();
                    tour.start_time = mTimeText.getText().toString();
                    if(!mTotalTimeEdit.getText().toString().equals("")) {
                        Log.d("total_time", "null");
                        tour.total_time = Integer.parseInt(mTotalTimeEdit.getText().toString());
                    }

                    tour.comment = mCommentEdit.getText().toString();
                    tour.area = area.getSelectedItem().toString();
                    tour.image = MyUtils.getByteFromImage(MyUtils.resize((((BitmapDrawable) imageView.getDrawable()).getBitmap())));
                    tour.is_local = mIslocal.isChecked();
                    tour.author = auther_txt.getText().toString();
                    mRealm.commitTransaction();
                    getFragmentManager().popBackStack();
                }else {
                    mTitleEdit = (EditText) view.findViewById(R.id.editTitle);
                    mTotalTimeEdit = (EditText) view.findViewById(R.id.editTotalTime);
                    mCommentEdit = (EditText) view.findViewById(R.id.editComment);
                    mIslocal = (CheckBox) view.findViewById(R.id.is_localcheck);
                    Spinner area = (Spinner) view.findViewById(R.id.newTourArea);
                    ImageView imageView = (ImageView) view.findViewById(R.id.tour_image);
                    auther_txt = (EditText) view.findViewById(R.id.auther_txt);

                    mRealm.beginTransaction();
                    Tour tour = mRealm.where(Tour.class).equalTo("tour_id", tour_id).findFirst();
                    tour.tour_title = mTitleEdit.getText().toString();
                    tour.start_time = mTimeText.getText().toString();
                    if(!mTotalTimeEdit.getText().toString().equals("")) {
                        Log.d("total_time", "notnull");
                        tour.total_time = Integer.parseInt(mTotalTimeEdit.getText().toString());
                    }
                    tour.comment = mCommentEdit.getText().toString();
                    tour.area = area.getSelectedItem().toString();
                    tour.image = MyUtils.getByteFromImage(MyUtils.resize(((BitmapDrawable) imageView.getDrawable()).getBitmap()));
                    tour.is_local = mIslocal.isChecked();
                    tour.author = auther_txt.getText().toString();
                    mRealm.commitTransaction();
                    getFragmentManager().popBackStack();
                }
            }
        });
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
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = (data == null) ? null : data.getData();
            if(uri != null) {
                try {
                    Bitmap img = MyUtils.getImageFromStream(
                            getActivity().getContentResolver(), uri);
                    mImageView.setImageBitmap(img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void requestReadStorage(View view) {
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if(shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view,R.string.rationale,
                        Snackbar.LENGTH_LONG).show();
            }

            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);

        } else {
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        getString(R.string.pick_image)
                ),
                REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(grantResults.length != 1 ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mImageView, R.string.permission_deny,
                        Snackbar.LENGTH_LONG).show();

            } else {
                pickImage();
            }
        }
    }
}
