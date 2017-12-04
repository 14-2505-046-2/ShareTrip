package com.example.chai.sharetrip;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.Calendar;

import io.realm.Realm;

import static android.app.Activity.RESULT_OK;

public class NewRouteFragment extends Fragment implements View.OnClickListener {

    private static final String TOUR_ID =  "TOUR_ID";
    private static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private long tour_id;
    private Realm mRealm;
    private View view;
    private ImageView mImageView;

    public static NewRouteFragment newInstance(long TourId) {
        NewRouteFragment fragment = new NewRouteFragment();
        Bundle args = new Bundle();
        args.putLong(TOUR_ID, TourId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tour_id = getArguments().getLong(TOUR_ID);
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
        view = v;
        EditText editText = (EditText)v.findViewById(R.id.route_title);
        mImageView = (ImageView) v.findViewById(R.id.route_image);
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
        Button button2 = (Button)v.findViewById(R.id.add_area);
        button2.setOnClickListener(this);
        Button button3 = (Button)v.findViewById(R.id.add_means);
        button3.setOnClickListener(this);
        ImageView button4 = (ImageView)v.findViewById(R.id.route_image);
        button4.setOnClickListener(this);

        return v;
    }

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

    @Override
    public void onClick(final View v) {
        if (v == v.findViewById(R.id.button_startTime) || v == v.findViewById(R.id.button_endTime)) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog dialog = new TimePickerDialog(
                    getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Log.d("route_onTimeSet", String.format("%02d:%02d", hourOfDay, minute));
                            if(v == v.findViewById(R.id.button_startTime)){
                                TextView start_time_view = (TextView) getView().findViewById(R.id.start_time_view);
                                start_time_view.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                            else if(v == v.findViewById(R.id.button_endTime)) {
                                TextView end_time_view = (TextView) getView().findViewById(R.id.end_time_view);
                                end_time_view.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }
                    },
                    hour, minute, true);
            dialog.show();
        } else if (v == v.findViewById(R.id.add_area) || v == v.findViewById(R.id.add_means)) {
            mRealm.beginTransaction();
            Number maxId = mRealm.where(Route.class).max("route_id");
            long nextId = 0;
            if (maxId != null) nextId = maxId.longValue() + 1;
            Route route = mRealm.createObject(Route.class, new Long(nextId));
            route.tour_id = tour_id;
            if(v == v.findViewById(R.id.add_area)) {
                    EditText name  = (EditText) view.findViewById(R.id.route_title);
                    ImageView image = (ImageView) view.findViewById(R.id.route_image);
                    EditText link = (EditText) view.findViewById(R.id.editText_link);
                    EditText comment = (EditText) view.findViewById(R.id.comment_area);

                    route.tour_id = tour_id;
                    route.flag_area = true;
                    route.name = name.getText().toString();
                    route.image = MyUtils.getByteFromImage(((BitmapDrawable)image.getDrawable()).getBitmap());
                    route.link = link.getText().toString();
                    route.comment = comment.getText().toString();
            }
            else {
                Spinner means = (Spinner) view.findViewById(R.id.spinner_means);
                TextView start_time_view = (TextView) view.findViewById(R.id.start_time_view);
                TextView end_time_view = (TextView) view.findViewById(R.id.end_time_view);
                EditText comment = (EditText) view.findViewById(R.id.comment_means);

                route.means = (int)means.getSelectedItemId();
                route.start_time = start_time_view.getText().toString();
                route.end_time = end_time_view.getText().toString();
                route.comment = comment.getText().toString();
            }
            mRealm.commitTransaction();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Route delete_route = realm.where(Route.class).equalTo("route_id", MyUtils.ADDROUTE).findFirst();
                    delete_route.deleteFromRealm();
                    Route route = realm.createObject(Route.class, MyUtils.ADDROUTE);
                }
            });
            getFragmentManager().popBackStack();
        }
        else if(v == v.findViewById(R.id.route_image)) {
            requestReadStorage(view);
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
