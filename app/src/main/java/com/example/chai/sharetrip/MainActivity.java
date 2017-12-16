package com.example.chai.sharetrip;

import android.content.Context;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.nifty.cloud.mb.core.NCMB;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity implements TripListFragment.OnFragmentInteractionListener {

    private Realm mRealm;
    private String tour_id = "all";
    private String area = "全て";
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onSearchTour();

        mRealm = Realm.getDefaultInstance();
        //次の行コメントアウトで起動のたびテストデータが生成されます。 -> p179
        //createTestData();

        //データベースサーバー使用のため
        NCMB.initialize(this.getApplicationContext(), "041e08f3646a44378c5175408afdedae4eae181550e1f9c225b6951e11870797", "684e732244c930d72c1a10292444b8a2abd285439ac2d4ba70198811ae7c450a");

        //アップロードのテスト用。0は最初にtestデータを作成した場合テストデータをアップロード
        //MyUtils.upload_tour((long) 0);
        //アップロードしたテストの削除
        MyUtils.delete_test();

        showTourList();

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


        //spinner_areaのリスナーを設定
        Spinner spinner_area = (Spinner)findViewById(R.id.spinner_area);
        spinner_area.setOnItemSelectedListener(new OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String item = (String)spinner.getSelectedItem();
                area = item;
                showTourList();
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //spinner_timeのリスナーを設定
        Spinner spinner_time = (Spinner)findViewById(R.id.spinner_time);
        spinner_time.setOnItemSelectedListener(new OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                long item = (long)spinner.getSelectedItemId();

                time = item;
                showTourList();
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        set_title("近くの観光ルート");
    }

    @Override
    protected void onResume(){
        super.onResume();
        showTourList();
    }

    private void set_title(String str) {
        //titleの初期化
        TextView title = (TextView)findViewById(R.id.title);
        Spinner spinner_area = (Spinner)findViewById(R.id.spinner_area);
        Spinner spinner_time = (Spinner)findViewById(R.id.spinner_time);
        title.setText(str);
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
                tour.start_time = "10:00";
                tour.total_time = 8;
                tour.upload_date = "２０１８年１１月１日";
                tour.area = "宇部市";
                tour.is_local = true;
                Bitmap bitmap_tour = BitmapFactory.decodeResource(getResources(), R.drawable.icon_bike);
                tour.image = MyUtils.getByteFromImage(bitmap_tour);

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
                Bitmap bitmap_route = BitmapFactory.decodeResource(getResources(), R.drawable.icon_airplane);
                route.image = MyUtils.getByteFromImage(bitmap_route);

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
                Bitmap bitmap_route3 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_bullet);
                route3.image = MyUtils.getByteFromImage(bitmap_route3);

            }
        });
    }

    private void showTourList() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("TripListFragment");
        if (fragment == null) {
            fragment = new TripListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("id", tour_id);
            bundle.putLong("time", time);
            bundle.putString("area", area);
            fragment.setArguments(bundle);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.content, fragment, "TripListFragment");
            transaction.commit();
        } else {
            fragment = new TripListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("id", tour_id);
            bundle.putLong("time", time);
            bundle.putString("area", area);
            fragment.setArguments(bundle);

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
                    tour_id = text;
                    showTourList();
                    return true;
                }
                return false;
            }
        });
    }

    //マイツアーのボタンを押す
    public void onClickMyTourActivityButton (View v) {
        Intent intent = new Intent(this, AddTourActivity.class);
        startActivity(intent);
    }

    //ツアー追加ボタンを押す
    public void onClickAddTourActivityButton (View v) {
        Intent intent = new Intent(this, AddTourActivity.class);
        intent.putExtra("is_add", true);
        startActivity(intent);
    }

    //検索バー横の検索ボタン
    public void onClickSearchButton(View v) {
        final EditText editText = (EditText) findViewById(R.id.editText);
        String text = editText.getText().toString();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Log.d("Search", "push");
        tour_id = text;

        final RelativeLayout search_bar = (RelativeLayout)findViewById(R.id.search_bar);
        search_bar.setVisibility(View.GONE);

        showTourList();
    }

    //検索バー表示ボタンをクリック
    public void onClickSearchViewButton(View view) {
        final RelativeLayout search_bar = (RelativeLayout)findViewById(R.id.search_bar);
        if(search_bar.getVisibility() == View.GONE) {
            search_bar.setVisibility(View.VISIBLE);
        }
        else {
            search_bar.setVisibility(View.GONE);
        }
        if(tour_id.equals("MyTour")) {
            final EditText editText = (EditText) findViewById(R.id.editText);
            String text = editText.getText().toString();
            tour_id = text;
            reTitle();
            showTourList();
        }
    }

    //マイツアーボタンを押した時(使ってない）
    public void onClickMyTour(View v) {
        RelativeLayout search_bar = (RelativeLayout)findViewById(R.id.search_bar);
        ImageButton button = (ImageButton)findViewById(R.id.myTour);
        if(!tour_id.equals("MyTour")) {
            tour_id = "MyTour";
            search_bar.setVisibility(View.GONE);
            TextView title = (TextView)findViewById(R.id.title);
            title.setText("マイツアー");
        }
        else {
            tour_id = "";
            reTitle();
        }
        showTourList();
    }

    //近くのルートに書き換えるだけ。(使われてない)
    public void reTitle() {
        String text = "";
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("近くのルート");
    }
}
