package com.tnc.wishlist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tnc.wishlist.ModelClasses.OrphanAgeHomeInformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.staticClass.DataCentre;

import java.util.ArrayList;

public class HomesRecycleViewAdapter extends RecyclerView.Adapter<HomesRecycleViewAdapter.MyViewHolder> {
    Context context;
    private int resources;
    ArrayList<OrphanAgeHomeInformation> homeData;
    FirebaseDatabase database;
    DatabaseReference myRefrence;

    public HomesRecycleViewAdapter(Context context, int resources, ArrayList<OrphanAgeHomeInformation> homeData) {
        this.context = context;
        this.resources = resources;
        this.homeData = homeData;
        database = FirebaseDatabase.getInstance();
        myRefrence = database.getReference().child(context.getString(R.string.orphanagesFirebase));
    }


    @NonNull
    @Override
    public HomesRecycleViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myview = LayoutInflater.from(parent.getContext()).inflate(resources, parent, false);
        return new HomesRecycleViewAdapter.MyViewHolder(myview);
    }

    @Override
    public void onBindViewHolder(@NonNull HomesRecycleViewAdapter.MyViewHolder holder, int position) {

        final OrphanAgeHomeInformation currentChild = homeData.get(position);
        holder.accept.setVisibility(View.GONE);
        holder.decline.setVisibility(View.GONE);
        if (!currentChild.getStatus().equals(context.getString(R.string.approved))) {
            holder.accept.setVisibility(View.VISIBLE);
            holder.decline.setVisibility(View.VISIBLE);
        }
        if (homeData.get(position).getPhoto() != null)
            Glide.with(context).load(currentChild.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_icon).into(holder.homeProfile);
        else
            holder.homeProfile.setVisibility(View.GONE);
        holder.homeName.setText(currentChild.getName());
        holder.myLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: child descriptive activity
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentChild.setStatus(context.getString(R.string.approved));
                myRefrence.child(currentChild.getOrphanageId()).setValue(currentChild);

            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentChild.setStatus(context.getString(R.string.decline));
                myRefrence.child(currentChild.getOrphanageId()).setValue(currentChild);
            }
        });


    }

    @Override
    public int getItemCount() {
        return homeData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView homeProfile;
        TextView homeName;
        LinearLayout myLinear;
        ImageView accept, decline;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myLinear = itemView.findViewById(R.id.homeCardLinearLayout);
            homeName = itemView.findViewById(R.id.homeCardName);
            homeProfile = itemView.findViewById(R.id.homeProfile);
            accept = itemView.findViewById(R.id.acceptHome);
            decline = itemView.findViewById(R.id.rejectHome);
        }

    }
}
