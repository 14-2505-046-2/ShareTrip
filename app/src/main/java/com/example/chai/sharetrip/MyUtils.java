package com.example.chai.sharetrip;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import com.nifty.cloud.mb.core.NCMBFile;
import com.nifty.cloud.mb.core.FetchFileCallback;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by chai on 2017/11/03.
 */

public class MyUtils {
    public static ArrayList<Long> list = new ArrayList<Long>();
    public static boolean is_test = true;//テストの間はtrueで。（upload用)

    //byte型配列からBitmapインスタンスへの変換　-> p314
    public static Bitmap getImageFromByte(byte[] bytes) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);
        int bitmapSize = 1;

        if((opt.outHeight * opt.outWidth) > 5000) {
            double outSize = (double) (opt.outHeight * opt.outWidth) / 500000;
            bitmapSize = (int)(Math.sqrt(outSize) + 1);
        }
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = bitmapSize;
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);
        return bmp;
    }

    //Bitmapインスタンスからbyte配列への変換 -> p314
    public static byte[] getByteFromImage(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Bitmap getImageFromStream(ContentResolver resolver, Uri uri)
            throws IOException {
        InputStream in;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        in = resolver.openInputStream(uri);
        BitmapFactory.decodeStream(in, null, opt);
        in.close();
        int bitmapSize = 1;
        if((opt.outHeight * opt.outWidth) > 500000) {
            double outSize = (double) (opt.outHeight * opt.outWidth) / 500000;
            bitmapSize = (int)(Math.sqrt(outSize) + 1);
        }

        opt.inJustDecodeBounds = false;
        opt.inSampleSize = bitmapSize;
        in = resolver.openInputStream(uri);
        Bitmap bmp = BitmapFactory.decodeStream(in,null,opt);
        in.close();
        return bmp;
    }

    //ルートをrealmに追加する。
    public static void saveRoute(Realm mRealm, NCMBObject o, long tour_id) {
        mRealm.beginTransaction();
        mRealm = Realm.getDefaultInstance();
        Number maxId = mRealm.where(Route.class).max("route_id");
        long nextId = 0;
        if (maxId != null) nextId = maxId.longValue() + 1;
        Route route = mRealm.createObject(Route.class, new Long(nextId));
        route.flag_area = o.getBoolean("flag_area");
        route.means = o.getInt("means");
        route.tour_id = tour_id;
        route.start_time = o.getString("start_time");
        route.end_time = o.getString("end_time");
        route.comment = o.getString("comment");
        route.link = o.getString("link");
        route.name = o.getString("name");
        NCMBFile file = new NCMBFile(o.getString("image"));
        try {
            route.image = file.fetch();
        } catch (NCMBException e) {
            Log.e("fetch_image", e.toString());
        }
        mRealm.commitTransaction();
    }

    //NCMBObjectを引数とし、realmに追加する。
    public static void saveTour(Realm mRealm, NCMBObject o) {
        mRealm.beginTransaction();
        mRealm = Realm.getDefaultInstance();
        Number maxId = mRealm.where(Tour.class).max("tour_id");
        long nextId = 0;
        if (maxId != null) nextId = maxId.longValue() + 1;
        Tour tour = mRealm.createObject(Tour.class, new Long(nextId));
        tour.tour_title = o.getString("tour_title");
        tour.author = o.getString("author");
        tour.comment = o.getString("comment");
        tour.start_time = o.getString("start_time");
        tour.total_time = o.getLong("total_time");
        tour.upload_date = o.getString("createDate");
        tour.objectId = o.getString("objectId");
        //tour.image = o.getString("image");

        NCMBFile file = new NCMBFile(o.getString("image"));
        try {
            tour.image = file.fetch();
        } catch (NCMBException e) {
            Log.e("fetch_image", e.toString());
        }

        Log.d("test", tour.objectId);
        mRealm.commitTransaction();

        //Route
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Route");
        query.whereEqualTo("tour_id", o.getString("objectId"));
        try {
            List<NCMBObject> results = query.find();
            for (int i = 0, n = results.size(); i < n; i++) {
                saveRoute(mRealm, results.get(i), nextId);
            }
        } catch (NCMBException e) {
            Log.e("NCMB_Route", "error");
        }
    }

    //必要なツアーのtour_idを配列として返す。引数は検索する文字列。
    //"おすすめ"、"MyTour"をキーワードとしておすすめ、マイツアーのtour_idを返します。
    public static RealmResults<Tour> getAllObjectId(String word, String area, long time) throws NCMBException {
        if (word.equals("MyTour")) {
            Realm realm = Realm.getDefaultInstance();
            RealmQuery results = realm.where(Tour.class).equalTo("objectId", "local_data");
            return results.findAll();
        } else {
            //TestClassを検索するためのNCMBQueryインスタンスを作成
            NCMBQuery<NCMBObject> query = new NCMBQuery<>("Tour");
            //wordに一致するものに絞る。
            if(!word.equals("all") && !word.equals("")) {
                //検索キーワード
                query.whereEqualTo("tour_title", word);
                Log.d("on_serch", word);
            }
            //areaに一致するものを検索
            if(!area.equals("全て")) {
                query.whereEqualTo("area", area);
            }
            //timeに一致するものを検索
            if(time != 0) {
                query.whereEqualTo("rough_time", time);
            }
            list = new ArrayList<Long>();
            //データストアからデータを検索
            List<NCMBObject> results = query.find();
            Realm mRealm;
            mRealm = Realm.getDefaultInstance();
            Log.d("resultsdata", String.valueOf(results.size()));
            for (int i = 0, n = results.size(); i < n; i++) {
                NCMBObject o = results.get(i);
                mRealm.beginTransaction();
                RealmResults<Tour> query_r = mRealm.where(Tour.class).contains("objectId", o.getString("objectId")).findAll();
                boolean empty = query_r.isEmpty();
                mRealm.commitTransaction();
                if (empty) {
                    Log.d("query_test", "save");
                    saveTour(mRealm, o);
                } else {
                    Log.d("query_test", "downloaded");
                }
                mRealm.beginTransaction();
                query_r = mRealm.where(Tour.class).contains("objectId", o.getString("objectId")).findAll();
                list.add(query_r.first().tour_id);
                mRealm.commitTransaction();
            }

            RealmQuery<Tour> rResults = mRealm.where(Tour.class);

            for (int i = 0, size = list.size(); i < size; i++) {
                if (i != 0) {
                    rResults = rResults.or();
                }
                rResults = rResults.equalTo("tour_id", list.get(i));
            }
            if (list.size() != 0) {
                return rResults.findAll();
            }
        }
        return null;
    }

    //tourのアップロード
    public static void upload_tour(Long tour_id) {
        Realm realm = Realm.getDefaultInstance();
        Tour tour = realm.where(Tour.class).equalTo("tour_id", tour_id).findFirst();

        NCMBObject obj = new NCMBObject("Tour");

        obj.put("tour_title", tour.tour_title);
        obj.put("start_time", tour.start_time);
        obj.put("total_time", tour.total_time);
        obj.put("author", tour.author);
        obj.put("comment", tour.comment);
        obj.put("area", "宇部市");
        obj.put("flag_route", false);
        obj.put("image", "");
        obj.put("is_test", is_test);
        if(tour.total_time <= 2) {
            obj.put("rough_time", 1);
        }
        else if(tour.total_time <= 4) {
            obj.put("rough_time", 2);
        }
        else {
            obj.put("rough_time", 2);
        }

        try {
            obj.save();
        } catch (NCMBException e) {
            Log.e("upload_tour", e.toString());
        }

        String objectId = obj.getString("objectId");

        RealmResults<Route> results = realm.where(Route.class).equalTo("tour_id", tour_id).findAll();
        for(int i = 0; i < results.size(); i++) {
            Route route = results.get(i);
            NCMBObject obj_route = new NCMBObject("Route");
            obj_route.put("tour_id", objectId);
            obj_route.put("flag_area", route.flag_area);
            obj_route.put("comment", route.comment);
            obj_route.put("is_test", is_test);
            if(route.flag_area) {
                obj_route.put("name", route.name);
                obj_route.put("image", "");
                obj_route.put("link", "");
            }
            else {
                obj_route.put("means", route.means);
                obj_route.put("start_time", route.start_time);
                obj_route.put("end_time", route.end_time);
            }
            try {
                obj_route.save();
            } catch (NCMBException e) {
                Log.e("upload_route", e.toString());
            }
        }
    }

    public static void delete_test() {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Tour");
        query.whereEqualTo("is_test", true);
        try {
            List<NCMBObject> results = query.find();
            for(int i = 0; i < results.size(); i++) {
                NCMBObject o = results.get(i);
                o.deleteObject();
            }
        } catch (NCMBException e) {
            Log.e("delete_test", e.toString());
        }
        NCMBQuery<NCMBObject> query1 = new NCMBQuery<>("Route");
        query1.whereEqualTo("is_test", true);
        try {
            List<NCMBObject> results = query1.find();
            for(int i = 0; i < results.size(); i++) {
                NCMBObject o = results.get(i);
                o.deleteObject();
            }
        } catch (NCMBException e) {
            Log.e("delete_test", e.toString());
        }
    }
}
