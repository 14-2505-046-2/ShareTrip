package com.example.chai.sharetrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.realm.Realm;

//教科書p305～を参考にしています。
public class NewTourFragment extends Fragment {

    private static final String TOUR_ID =  "TOUR_ID";
    private static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private long mTourId;
    private Realm mRealm;
    private EditText mTitleEdit;
    private EditText mAutherEdit;

    public static NewTourFragment newInstance(long TourId) {
        NewTourFragment fragment = new NewTourFragment();
        Bundle args = new Bundle();
        args.putLong(TOUR_ID, TourId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTourId = getArguments().getLong(TOUR_ID);
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
        View v = inflater.inflate(R.layout.fragment_new_tour,container, false);

        mTitleEdit = (EditText) v.findViewById(R.id.editTitle);
        mTitleEdit.setText("abc");
        Log.d("title","log");

        mTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute (Realm realm) {
                        Tour tour = realm.where(Tour.class).equalTo("tour_id", mTourId).findFirst();
                        tour.tour_title = mTitleEdit.getText().toString();
                        Log.d("tourid","" + mTourId);
                        Log.d("title","" + mTitleEdit.getText());
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
    }
}
