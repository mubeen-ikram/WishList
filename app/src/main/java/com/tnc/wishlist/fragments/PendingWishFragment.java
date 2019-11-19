package com.tnc.wishlist.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tnc.wishlist.ModelClasses.UserInformation;
import com.tnc.wishlist.ModelClasses.Wishinformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.adapters.WishRecycleViewAdapter;
import com.tnc.wishlist.staticClass.DataCentre;

import java.util.ArrayList;

public class PendingWishFragment extends Fragment {
    RecyclerView recyclerView;
    WishRecycleViewAdapter wishRecycleViewAdapter;
    ArrayList<Wishinformation> pendingWishes;
    public PendingWishFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_wish, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        setValues();
        recyclerView=getView().findViewById(R.id.recyclePendingWishes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        wishRecycleViewAdapter=new WishRecycleViewAdapter(getContext(),R.layout.wish_card_view,pendingWishes);
        recyclerView.setAdapter(wishRecycleViewAdapter);
    }

    private void setValues() {
        pendingWishes=new ArrayList<>();
        if(DataCentre.wishinformations.size()>0){
            for(Wishinformation cWish:DataCentre.wishinformations){

                if(cWish.getCurrentCondition().equals(getString(R.string.Pending0))){
                    for(UserInformation user:DataCentre.userInformations){
                        if(user.getUserId().equals(cWish.getOrphanId())&&user.getOrphanageId().equals(DataCentre.userId)){
                            pendingWishes.add(cWish);
                        }

                    }
                }
            }
        }
        else{
        }
    }


}
