package com.otacodes.goestateapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.otacodes.goestateapp.Item.ListItem;
import com.otacodes.goestateapp.Models.PropertyModels;
import com.otacodes.goestateapp.R;
import com.otacodes.goestateapp.Utils.DatabaseHelper;

import java.util.ArrayList;


public class FavouriteFragment extends Fragment {


    View getView;
    Context context;
    ArrayList<PropertyModels> listItem;
    public RecyclerView recyclerView;
    ListItem adapter;
    DatabaseHelper databaseHelper;
    LinearLayout  notFound;
    RelativeLayout progress;
    Toolbar toolbar;
    CardView filterandsort;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_recycle, container, false);
        context = getContext();
        toolbar = getView.findViewById(R.id.toolbar);
        listItem = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getActivity());
        recyclerView = getView.findViewById(R.id.recycle);
        progress = getView.findViewById(R.id.progress);
        notFound = getView.findViewById(R.id.noresult);
        filterandsort = getView.findViewById(R.id.rlfilter);
        filterandsort.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));



        return getView;
    }

    @Override
    public void onResume() {
        super.onResume();
            listItem = databaseHelper.getFavourite();
        displayData();
    }

    private void displayData() {
        adapter = new ListItem(getActivity(), listItem, R.layout.item_list);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            notFound.setVisibility(View.VISIBLE);
        } else {
            notFound.setVisibility(View.GONE);
        }

    }

}
