package com.example.chai.sharetrip;

import android.content.Context;
import android.content.Intent;
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
 * Created by enPiT-P22 on 2017/11/04.
 */

public class TripRealmAdapter extends RealmRecyclerViewAdapter<Tour,TripRealmAdapter.TripViewHolder>{

    Context context;

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView author;
        protected TextView uploadDate;
        protected TextView start;
        protected TextView total;
        protected ImageView photo;

        public TripViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tour_title);
            author = (TextView) itemView.findViewById(R.id.author);
            uploadDate = (TextView) itemView.findViewById(R.id.upload_date);
            start = (TextView) itemView.findViewById(R.id.start_time);
            total = (TextView) itemView.findViewById(R.id.total_time);
            photo = (ImageView) itemView.findViewById(R.id.trip_photo);
        }
    }

    public TripRealmAdapter(@Nullable Context context, @Nullable OrderedRealmCollection<Tour> data,boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tripcard_layout, parent, false);
        final TripViewHolder holder = new TripViewHolder(itemView);

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
        holder.total.setText(tour.total_time);
        if(tour.image != null && tour.image.length != 0) {
            Bitmap bmp = MyUtils.getImageFromByte(tour.image);
            holder.photo.setImageBitmap(bmp);
        }
    }
}
