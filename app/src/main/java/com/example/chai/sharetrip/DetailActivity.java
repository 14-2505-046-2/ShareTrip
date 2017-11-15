package com.example.chai.sharetrip;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity implements TripDetailFragment.OnFragmentInteractionListener {
    public static final String TOUR_ID = "TOUR_ID";
    public static long tour_id;
    private static final long ERR_CD = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        tour_id = intent.getLongExtra(TOUR_ID, ERR_CD);
        showDetailList();
    }

    private void showDetailList() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("TripDetailFragment");
        if (fragment == null) {
            fragment = new TripDetailFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.content, fragment, "TripDetailFragment");
            transaction.commit();
        }
    }

    @Override
    public void onAddTourSelected() {
        //新規ルート追加処理をここに
    }
}
