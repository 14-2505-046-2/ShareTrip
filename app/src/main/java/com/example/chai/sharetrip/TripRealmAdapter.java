package com.example.chai.sharetrip;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by enPiT-P22 on 2017/11/04.
 */

public class TripRealmAdapter extends RealmRecyclerViewAdapter<Tour,TripRealmAdapter.TripViewHolder>{

    private Context context;

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView author;
        protected TextView uploadDate;
        protected TextView start;
        protected TextView total;
        protected ImageView photo;
        protected ImageButton delete;
        protected ImageButton edit;

        public TripViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tour_title);
            author = (TextView) itemView.findViewById(R.id.author);
            uploadDate = (TextView) itemView.findViewById(R.id.upload_date);
            start = (TextView) itemView.findViewById(R.id.start_time);
            total = (TextView) itemView.findViewById(R.id.total_time);
            photo = (ImageView) itemView.findViewById(R.id.trip_photo);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
            edit = (ImageButton) itemView.findViewById(R.id.edit);
        }
    }

    public TripRealmAdapter(@Nullable Context context, @Nullable OrderedRealmCollection<Tour> data,boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tripcard_layout, parent, false);
        final TripViewHolder holder = new TripViewHolder(itemView);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("このツアーを削除しますか\nアップロードしたツアーは消えません。")
                        .setTitle("ツアーの削除")
                        .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int position = holder.getAdapterPosition();
                                Tour tour = getData().get(position);
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                RealmResults results = realm.where(Route.class).equalTo("tour_id", tour.tour_id).findAll();
                                results.deleteAllFromRealm();
                                tour.deleteFromRealm();
                                realm.commitTransaction();
                                Toast.makeText(context, "削除しました。", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("いいえ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(context, "キャンセルされました。", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            }
        });


        //編集時呼び出される。
        holder.edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Tour tour = getData().get(position);

                NewTourFragment newTourFragment = NewTourFragment.newInstance();
                FragmentManager manager = ((AddTourActivity)context).getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putLong("id", tour.tour_id);
                newTourFragment.setArguments(bundle);
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.addTourContent,newTourFragment,"NewTourFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Tour tour = getData().get(position);
                long tourId = tour.tour_id;
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(DetailActivity.TOUR_ID, tourId);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        Tour tour = getData().get(position);
        holder.title.setText(tour.tour_title);
        holder.author.setText(tour.author);
        holder.uploadDate.setText(tour.upload_date);
        holder.start.setText(tour.start_time);
        holder.total.setText(String.valueOf(tour.total_time) + "時間");

        if(tour.image != null && tour.image.length != 0) {
            /*
            Uri uri = Uri.parse(tour.image);
            Uri.Builder builder = uri.buildUpon();
            AsyncTaskHttpRequest task = new AsyncTaskHttpRequest(holder.photo);
            task.execute(builder);
            */
            Bitmap bitmap = MyUtils.getImageFromByte(tour.image);
            holder.photo.setImageBitmap(bitmap);
        }
        if(tour.objectId.equals("local_data")) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
        }
    }
}
