package com.example.chai.sharetrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    Route first_route;
    Uri gmmIntentUri;

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView start_time;
        protected TextView end_time;
        protected TextView comment;
        protected TextView means;
        protected ImageView photo;
        protected ImageView icon;
        protected ImageButton add_route_button;
        protected ImageButton delete;
        protected ImageButton edit;
        protected Button first_route_button;
        protected TextView link;

        public TripViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            start_time = (TextView) itemView.findViewById(R.id.start_time);
            end_time = (TextView) itemView.findViewById(R.id.end_time);
            comment = (TextView) itemView.findViewById(R.id.commentText);
            means = (TextView) itemView.findViewById(R.id.meansText);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            add_route_button = (ImageButton) itemView.findViewById(R.id.add_route_button);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
            first_route_button = (Button) itemView.findViewById(R.id.first_route_button);
            link = (TextView) itemView.findViewById(R.id.link_url);
        }
    }

    public RouteRealmAdapter(@Nullable Context context, @Nullable OrderedRealmCollection<Route> data,boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
        if(!data.isEmpty()) {
            this.first_route = data.first();
            //gmmIntentUri = Uri.parse("geo:latitude,longitude?q=" + this.first_route.name);
            gmmIntentUri = Uri.parse("geo:0,0?q=" + this.first_route.name);
        }
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
        holder.first_route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.setPackage("com.google.android.apps.maps");
                //context.startActivity(mapIntent);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String destination = "萩";

                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                intent.setData(Uri.parse("http://maps.google.com/maps?saddr=0,0&daddr="+destination+"&dirflg=w"));
                context.startActivity(intent);
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
            holder.link.setText(route.link);


            if (route.flag_area) {
                holder.icon.setVisibility(View.INVISIBLE);
                if (route.image != null && route.image.length != 0) {
                    Bitmap bitmap = MyUtils.getImageFromByte(route.image);
                    holder.photo.setImageBitmap(bitmap);
                }
            } else {
                holder.photo.setVisibility(View.INVISIBLE);
                switch (route.means) {
                    case 0:
                        holder.icon.setImageResource(R.drawable.icon_walk);
                        holder.means.setText("徒歩");
                        break;
                    case 1:
                        holder.icon.setImageResource(R.drawable.icon_bike);
                        holder.means.setText("自転車");
                        break;
                    case 2:
                        holder.icon.setImageResource(R.drawable.icon_car);
                        holder.means.setText("自動車");
                        break;
                    case 3:
                        holder.icon.setImageResource(R.drawable.icon_bus);
                        holder.means.setText("バス");
                        break;
                    case 4:
                        holder.icon.setImageResource(R.drawable.icon_train);
                        holder.means.setText("電車");
                        break;
                    case 5:
                        holder.icon.setImageResource(R.drawable.icon_bullet);
                        holder.means.setText("新幹線");
                        break;
                    case 6:
                        holder.icon.setImageResource(R.drawable.icon_airplane);
                        holder.means.setText("飛行機");
                        break;
                }
            }

            Realm realm = Realm.getDefaultInstance();
            Tour tour = realm.where(Tour.class).equalTo("tour_id", route.tour_id).findFirst();
            if(tour.objectId.equals("local_data")) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.VISIBLE);
            }

            if(route.route_id == first_route.route_id) {
                holder.first_route_button.setVisibility(View.VISIBLE);
            }
        }
        else if(route.route_id == MyUtils.ADDROUTE) {
            holder.add_route_button.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.INVISIBLE);
        }
    }
}
