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
import com.nifty.cloud.mb.core.FindCallback;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by chai on 2017/11/03.
 */

public class MyUtils {
    public static ArrayList<Long> list = new ArrayList<Long>();

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
        tour.total_time = o.getString("total_time");
        tour.upload_date = o.getString("createDate");
        tour.objectId = o.getString("objectId");
        Log.d("test", tour.objectId);
        mRealm.commitTransaction();
    }

    //必要なツアーのtour_idを配列として返す。引数は検索する文字列。
    //"おすすめ"、"マイツアー"をキーワードとしておすすめ、マイツアーのtour_idを返します。
    public static RealmResults<Tour> getAllObjectId(String word) throws NCMBException {
        if (word == "MyTour") {
            Realm realm = Realm.getDefaultInstance();
            RealmQuery results = realm.where(Tour.class).equalTo("objectId", "local_data");
            return results.findAll();
        } else {
            //TestClassを検索するためのNCMBQueryインスタンスを作成
            NCMBQuery<NCMBObject> query = new NCMBQuery<>("Tour");
            if(word != "all") {
                //検索キーワード
                query.whereEqualTo("tour_title", word);
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
}
