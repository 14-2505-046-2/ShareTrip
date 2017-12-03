package com.example.chai.sharetrip;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nifty.cloud.mb.core.NCMBException;

import io.realm.Realm;
import io.realm.RealmResults;

//p286-
public class TripListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Realm mRealm;

    private String tour_id = "all";
    private String area = "全て";
    private long time = 0;

    public TripListFragment() {

    }

    public static TripListFragment newInstance() {
        TripListFragment fragment = new TripListFragment();
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
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        tour_id = bundle.getString("id", "all");
        time = bundle.getLong("time", 0);
        area = bundle.getString("area", "全て");

        View v = inflater.inflate(R.layout.fragment_trip_list, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        Log.d("tour_id", tour_id);
        try {
            RealmResults<Tour> tours = MyUtils.getAllObjectId(tour_id, area, time);
            //RealmResults<Tour> tours = mRealm.where(Tour.class).findAll();
            TripRealmAdapter adapter = new TripRealmAdapter(getActivity(), tours, true);
            recyclerView.setAdapter(adapter);
        } catch (NCMBException e) {
            Log.e("getTour", "ERR");
        }
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_tour, menu);
        MenuItem addTour = menu.findItem(R.id.menu_item_add_tour);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add_tour:
                Log.d("AddTourSelected", "Add");
                if (mListener != null) mListener.onAddTourSelected();
                return true;
        }
        return false;
    }
}
