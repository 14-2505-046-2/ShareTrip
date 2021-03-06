package com.example.chai.sharetrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import io.realm.Realm;

public class AddTourActivity extends AppCompatActivity  implements TripListFragment.OnFragmentInteractionListener {

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tour);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mRealm = Realm.getDefaultInstance();
        showTourList();

        if(getIntent().getBooleanExtra("is_add", false)) {
            onAddTourSelected();
        }
    }

    private void showTourList() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("TripListFragment");
        if (fragment == null) {
            fragment = new TripListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", "MyTour");
            fragment.setArguments(bundle);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.addTourContent, fragment, "TripListFragment");
            transaction.commit();
        }
    }

    public void onAddTourSelected() {
        //新規ツアー追加処理をここに
        /*
        mRealm.beginTransaction();
        Number maxId = mRealm.where(Tour.class).max("tour_id");
        long nextId = 0;
        if(maxId != null) nextId = maxId.longValue() + 1;
        Tour tour = mRealm.createObject(Tour.class, new Long(nextId));
        mRealm.commitTransaction();
        */

        NewTourFragment newTourFragment = NewTourFragment.newInstance();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.addTourContent,newTourFragment,"NewTourFragment");
        transaction.addToBackStack(null);
        Bundle bundle = new Bundle();
        bundle.putLong("id", -2);
        newTourFragment.setArguments(bundle);
        transaction.commit();
    }
}
