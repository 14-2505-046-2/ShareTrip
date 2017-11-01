package com.example.chai.sharetrip;

import io.realm.RealmObject;

/**
 * Created by v033ff on 2017/11/01.
 */

public class Detail extends RealmObject {
    @PrimaryKey
    public long id;
    public String title;
    public String bodyText;
    public String date;
    public byte[] image;
}

