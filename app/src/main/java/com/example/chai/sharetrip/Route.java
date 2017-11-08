package com.example.chai.sharetrip;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chai on 2017/11/01.
 */

public class Route extends RealmObject {
    //最低限必要なもの
    @PrimaryKey
    public long route_id;
    public long tour_id;
    public boolean flag_area;//場所ならTrueで交通手段ならFalseでお願いします。
    public String comment;
    //場所の場合必要なもの（交通手段なら参照されない。）
    public String name;
    public byte[] image;
    public String link;
    //交通手段で必要なもの（場所なら参照されない。）
    public int means;//この交通手段は0-6までで（0:徒歩 1:自転車 2:車 3:バス 4:電車 5:新幹線 6:飛行機)
    public String start_time;
    public String end_time;
}
