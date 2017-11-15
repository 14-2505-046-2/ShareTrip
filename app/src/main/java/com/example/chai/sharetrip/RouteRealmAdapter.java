package com.example.chai.sharetrip;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by chai on 2017/11/15.
 */

public class RouteRealmAdapter extends RealmRecyclerViewAdapter<Route, RouteRealmAdapter.TripViewHolder> {
    Context context;

    public static class TripViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView start_time;
        protected TextView end_time;
        protected TextView comment;
        protected ImageView photo;

        public TripViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            start_time = (TextView) itemView.findViewById(R.id.start_time);
            end_time = (TextView) itemView.findViewById(R.id.end_time);
            comment = (TextView) itemView.findViewById(R.id.comment);
            photo = (ImageView) itemView.findViewById(R.id.photo);

        }
    }

    public RouteRealmAdapter(@Nullable Context context, @Nullable OrderedRealmCollection<Route> data,boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_detail_card_layout, parent, false);
        final TripViewHolder holder = new TripViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        Route route = getData().get(position);
        holder.name.setText(route.name);
        holder.start_time.setText(route.start_time);
        holder.end_time.setText(route.end_time);
        holder.comment.setText(route.comment);
        /*
        if(route.image != null && route.image.length != 0) {
            Bitmap bmp = MyUtils.getImageFromByte(route.image);
            holder.photo.setImageBitmap(bmp);
        }
        */
        if(route.flag_area == false) {
            switch (route.means) {
                case 0:
                    holder.photo.setImageResource(R.drawable.icon_walk);
                    break;
                case 1:
                    holder.photo.setImageResource(R.drawable.icon_bike);
                    break;
                case 2:
                    holder.photo.setImageResource(R.drawable.icon_car);
                    break;
                case 3:
                    holder.photo.setImageResource(R.drawable.icon_bus);
                    break;
                case 4:
                    holder.photo.setImageResource(R.drawable.icon_train);
                    break;
                case 5:
                    holder.photo.setImageResource(R.drawable.icon_bullet);
                    break;
                case 6:
                    holder.photo.setImageResource(R.drawable.icon_airplane);
                    break;
            }
        }
    }
}
