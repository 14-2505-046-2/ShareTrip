package com.example.chai.sharetrip;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.nifty.cloud.mb.core.NCMB;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity implements TripListFragment.OnFragmentInteractionListener {

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onSearchTour();

        mRealm = Realm.getDefaultInstance();
        //次の行コメントアウトで起動のたびテストデータが生成されます。 -> p179
        createTestData();
        showTourList();


        //データベースサーバー使用のため
        NCMB.initialize(this.getApplicationContext(), "041e08f3646a44378c5175408afdedae4eae181550e1f9c225b6951e11870797", "684e732244c930d72c1a10292444b8a2abd285439ac2d4ba70198811ae7c450a");

        //テスト用
        //createTestData();
        //検索の利用はこの通り使ってください。ツアータイトルを検索します。全部一致のみ検索できます。allで全てのサーバー上のデータ。MyTourでローカルのみのデータ（testデータはこっち）

        /*
        try {
            //下関観光と検索
            RealmResults result = MyUtils.getAllObjectId("all");
            if(result == null || result.size() == 0) {
                d("realm", "検索結果がありません。");
            } else {
                d("size", String.valueOf(result.size()));
                d("result", String.valueOf(result.first()));
            }
        }catch (NCMBException e){
            Log.e("NCMB", "error");
        }
        */
        //強制で詳細画面を表示
        //Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        //startActivity(intent);
    }

    //テストデータの生成用です。
    private void createTestData() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //Tourデータの作成。tour_idになるnextIdはRouteクラスでも使う。
                Number maxId = mRealm.where(Tour.class).max("tour_id");
                long nextId = 0;
                if (maxId != null) nextId = maxId.longValue() + 1;
                Tour tour = realm.createObject(Tour.class, new Long(nextId));
                tour.tour_title = "宇部満喫旅行(test)";
                tour.author = "ちゃい";
                tour.comment = "テスト用のデータです。";
                tour.start_time = "１０時";
                tour.total_time = "８時間";
                tour.upload_date = "２０１８年１１月１日";

                //ここからRouteデータの作成
                //常盤公園
                Number maxId2 = mRealm.where(Route.class).max("route_id");
                long nextId2 = 0;
                if (maxId2 != null) nextId2 = maxId2.longValue() + 1;
                Route route = realm.createObject(Route.class, new Long(nextId2));
                route.tour_id = nextId;
                route.name = "常盤公園";
                route.flag_area = true;
                route.link = "https://www.tokiwapark.jp";
                route.comment = "常盤公園は彫刻がたくさん！";

                //バス移動
                Number maxId3 = mRealm.where(Route.class).max("route_id");
                long nextId3 = 0;
                if (maxId3 != null) nextId3 = maxId3.longValue() + 1;
                Route route2 = realm.createObject(Route.class, new Long(nextId3));
                route2.tour_id = nextId;
                route2.flag_area = false;
                route2.comment = "便利なバスで移動しよう。";
                route2.means = 3;
                route2.start_time = "11:30";
                route2.end_time = "12:00";

                //山口大学工学部
                Number maxId4 = mRealm.where(Route.class).max("route_id");
                long nextId4 = 0;
                if (maxId4 != null) nextId4 = maxId4.longValue() + 1;
                Route route3 = realm.createObject(Route.class, new Long(nextId4));
                route3.tour_id = nextId;
                route3.name = "山口大学工学部";
                route3.flag_area = true;
                route3.link = "https://www.tokiwafes.com";
                route3.comment = "常盤祭やってます。";

            }
        });
    }

    private void showTourList() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("TripListFragment");
        if (fragment == null) {
            fragment = new TripListFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.content, fragment, "TripListFragment");
            transaction.commit();
        } else {
            fragment = new TripListFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content, fragment, "TripListFragment");
            transaction.commit();
        }
    }

    @Override
    public void onAddTourSelected() {
        //新規ツアー追加処理をここに
    }

    public void onSearchTour(){
        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = editText.getText().toString();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Log.d("Search", "push");
                    TripListFragment.tour_id = text;
                    showTourList();
                    return true;
                }
                return false;
            }
        });
    }

    public void onClickSearchButton(View v) {
        final EditText editText = (EditText) findViewById(R.id.editText);
        String text = editText.getText().toString();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Log.d("Search", "push");
        TripListFragment.tour_id = text;
        showTourList();
    }
}
