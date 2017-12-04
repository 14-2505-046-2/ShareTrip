package com.example.chai.sharetrip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

public class DetailActivity extends AppCompatActivity implements TripDetailFragment.OnFragmentInteractionListener {
    public static final String TOUR_ID = "TOUR_ID";
    public static long tour_id;
    private static final long ERR_CD = -1;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        tour_id = intent.getLongExtra(TOUR_ID, ERR_CD);
        showDetailList();
        change_title();
        mRealm = Realm.getDefaultInstance();
        Tour tour = mRealm.where(Tour.class).equalTo("tour_id", tour_id).findFirst();
        if(tour.objectId.equals("local_data")) {
            ImageButton imageButton = (ImageButton) findViewById(R.id.upload_button);
            imageButton.setVisibility(View.VISIBLE);
        }
    }

    private void change_title() {
        Realm realm = Realm.getDefaultInstance();
        Tour tour = realm.where(Tour.class).equalTo("tour_id", tour_id).findFirst();
        TextView title = (TextView) findViewById(R.id.tour_title);
        title.setText(tour.tour_title);
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

    public void onClickAddRouteActivityButton(View v) {
        //新規ルート追加
        /*
        mRealm.beginTransaction();
        Number maxId = mRealm.where(Route.class).max("route_id");
        long nextId = 0;
        if(maxId != null) nextId = maxId.longValue() + 1;
        Route route = mRealm.createObject(Route.class, new Long(nextId));
        route.tour_id = tour_id;
        mRealm.commitTransaction();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Route delete_route = realm.where(Route.class).equalTo("route_id", MyUtils.ADDROUTE).findFirst();
                delete_route.deleteFromRealm();
                Route route = realm.createObject(Route.class, MyUtils.ADDROUTE);
            }
        });
        */

        NewRouteFragment newRouteFragment = NewRouteFragment.newInstance(tour_id);//nextId);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content, newRouteFragment, "NewRouteFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //アップロードボタン
    public void onClickUploadButton(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("このツアーを公開しますか")
                .setTitle("確認")
                .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MyUtils.upload_tour(tour_id);
                        Toast.makeText(DetailActivity.this, "アップロードしました。", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("いいえ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(DetailActivity.this, "キャンセルされました。", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }
}
