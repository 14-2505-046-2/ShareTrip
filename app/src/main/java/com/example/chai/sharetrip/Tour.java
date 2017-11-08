package com.example.chai.sharetrip;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chai on 2017/11/01.
 */

//ツアーの一覧画面用のデータベース。
//tour_idはツアーの詳細画面用のRootデータベースのtour_idと同じになる様に。
public class Tour extends RealmObject {
    @PrimaryKey
    public long tour_id;
    public String tour_title;
    public String start_time;
    public String total_time;
    public String upload_date;
    public String author;
    public String comment;
    public byte[] image;
    public String objectId = "local_data";
}
