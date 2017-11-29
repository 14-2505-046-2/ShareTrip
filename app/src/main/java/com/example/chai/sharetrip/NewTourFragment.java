package com.example.chai.sharetrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.realm.Realm;

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

        return inflater.inflate(R.layout.fragment_new_tour, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
    }
}
