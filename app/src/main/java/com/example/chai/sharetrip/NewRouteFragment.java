package com.example.chai.sharetrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.realm.Realm;

public class NewRouteFragment extends Fragment {

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

        return inflater.inflate(R.layout.fragment_new_route, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
    }
}
