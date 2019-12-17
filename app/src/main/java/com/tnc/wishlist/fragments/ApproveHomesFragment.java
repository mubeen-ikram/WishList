package com.tnc.wishlist.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tnc.wishlist.ModelClasses.OrphanAgeHomeInformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.adapters.ChildRecycleViewAdapter;
import com.tnc.wishlist.adapters.HomesRecycleViewAdapter;
import com.tnc.wishlist.staticClass.DataCentre;

import java.util.ArrayList;

public class ApproveHomesFragment extends Fragment {
    HomesRecycleViewAdapter homesRecycleViewAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public ApproveHomesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_approved_child, container, false);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = getView().findViewById(R.id.approvedChildRelative);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<OrphanAgeHomeInformation> homes=new ArrayList<>();
        for(OrphanAgeHomeInformation home: DataCentre.orphanAgeHomeInformations){
            if(home.getStatus().equals(getString(R.string.Pending0)))
                homes.add(home);
        }

        homesRecycleViewAdapter = new HomesRecycleViewAdapter(getContext(), R.layout.homes_approve_card,homes);
        recyclerView.setAdapter(homesRecycleViewAdapter);
    }
}
