package com.example.chai.sharetrip;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.log.LogLevel;

public class TripDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Realm mRealm;

    public TripDetailFragment() {
        // Required empty public constructor
    }

    public static TripDetailFragment newInstance() {
        TripDetailFragment fragment = new TripDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceStat) {
        View v = inflater.inflate(R.layout.fragment_trip_detail, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        Tour tour = mRealm.where(Tour.class).equalTo("tour_id", DetailActivity.tour_id).findFirst();
        Boolean local_flag = false;
        if(tour.objectId.equals("local_data")) {
            local_flag = true;
            MyUtils.reload_add();
        }

        RealmQuery query = mRealm.where(Route.class).equalTo("tour_id", DetailActivity.tour_id);
        if(local_flag) {
            query = query.or();
            query = query.equalTo("route_id", MyUtils.ADDROUTE);
        }
        RealmResults<Route> routes = query.findAll();

        Log.d("tour_id", String.valueOf(routes.size()));
        RouteRealmAdapter adapter = new RouteRealmAdapter(getActivity(), routes, true);
        recyclerView.setAdapter(adapter);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onAddTourSelected();
    }
}
