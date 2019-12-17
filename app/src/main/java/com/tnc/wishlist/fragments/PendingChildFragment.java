package com.tnc.wishlist.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tnc.wishlist.ModelClasses.childInformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.adapters.ChildRecycleViewAdapter;
import com.tnc.wishlist.staticClass.DataCentre;

import java.util.ArrayList;


public class PendingChildFragment extends Fragment {
    ChildRecycleViewAdapter childRecycleViewAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<childInformation> totalPendingUser;


    public PendingChildFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_child, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setValues();
        layoutManager=new LinearLayoutManager(getContext());
        recyclerView=getView().findViewById(R.id.pendingChildRelative);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        childRecycleViewAdapter=new ChildRecycleViewAdapter(getContext(),R.layout.child_card_view, totalPendingUser);
        recyclerView.setAdapter(childRecycleViewAdapter);
    }

    private void setValues() {
        totalPendingUser =new ArrayList<>();
        if(DataCentre.childInformations.size()>0){
            for(childInformation cUser:DataCentre.childInformations){
                if(!cUser.getCondition().equals(getString(R.string.approved))) {
                    Integer childLevel = Integer.parseInt(cUser.getCondition());
                    if (DataCentre.userType == 1) {
                        if (cUser.getOrphanageId().equals(DataCentre.userId) && (childLevel <= DataCentre.userType)&&childLevel>-1) {
                            totalPendingUser.add(cUser);
                        }
                    } else {
                        if (childLevel>-1) {
                            totalPendingUser.add(cUser);
                        }
                    }
                }
            }
        }
        else{
        }
    }

}
