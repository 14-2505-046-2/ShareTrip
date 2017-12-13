package com.example.chai.sharetrip;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
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
        protected ImageView icon;
        protected ImageButton add_route_button;
        protected ImageButton delete;
        protected ImageButton edit;

        public TripViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            start_time = (TextView) itemView.findViewById(R.id.start_time);
            end_time = (TextView) itemView.findViewById(R.id.end_time);
            comment = (TextView) itemView.findViewById(R.id.commentText);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            add_route_button = (ImageButton) itemView.findViewById(R.id.add_route_button);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
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
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Route route = getData().get(position);
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                route.deleteFromRealm();
                realm.commitTransaction();
                MyUtils.reload_add();
                Toast.makeText(context, "削除しました。", Toast.LENGTH_SHORT).show();
            }
        });
        //編集時呼び出される。
        holder.edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Route route = getData().get(position);

                NewRouteFragment newRouteFragment = NewRouteFragment.newInstance();
                FragmentManager manager = ((DetailActivity)context).getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putLong("route_id", route.route_id);
                bundle.putLong("tour_id", route.tour_id);
                newRouteFragment.setArguments(bundle);
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content,newRouteFragment,"NewRouteFragment");
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        Route route = getData().get(position);
        if (route.route_id >= 0) {
            holder.name.setText(route.name);
            holder.start_time.setText(route.start_time);
            holder.end_time.setText(route.end_time);
            holder.comment.setText(String.valueOf(route.comment));

            if (route.flag_area) {
                if (route.image != null && route.image.length != 0) {
                    Bitmap bitmap = MyUtils.getImageFromByte(route.image);
                    holder.photo.setImageBitmap(bitmap);
                }
            } else {
                holder.photo.setVisibility(View.INVISIBLE);
                switch (route.means) {
                    case 0:
                        holder.icon.setImageResource(R.drawable.icon_walk);
                        break;
                    case 1:
                        holder.icon.setImageResource(R.drawable.icon_bike);
                        break;
                    case 2:
                        holder.icon.setImageResource(R.drawable.icon_car);
                        break;
                    case 3:
                        holder.icon.setImageResource(R.drawable.icon_bus);
                        break;
                    case 4:
                        holder.icon.setImageResource(R.drawable.icon_train);
                        break;
                    case 5:
                        holder.icon.setImageResource(R.drawable.icon_bullet);
                        break;
                    case 6:
                        holder.icon.setImageResource(R.drawable.icon_airplane);
                        break;
                }
            }

            Realm realm = Realm.getDefaultInstance();
            Tour tour = realm.where(Tour.class).equalTo("tour_id", route.tour_id).findFirst();
            if(tour.objectId.equals("local_data")) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.VISIBLE);
            }
        }
        else if(route.route_id == MyUtils.ADDROUTE) {
            holder.add_route_button.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.INVISIBLE);
        }
    }
}
