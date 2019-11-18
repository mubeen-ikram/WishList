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
import com.tnc.wishlist.ModelClasses.UserInformation;
import com.tnc.wishlist.R;

import java.util.ArrayList;

public class ChildRecycleViewAdapter extends RecyclerView.Adapter<ChildRecycleViewAdapter.MyViewHolder> {
    private Context context;
    private int resources;
    private ArrayList<UserInformation> childData;

    public ChildRecycleViewAdapter(Context context, int resources, ArrayList<UserInformation> childData) {
        this.context = context;
        this.resources = resources;
        this.childData = childData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myview= LayoutInflater.from(parent.getContext()).inflate(resources,parent,false);
        return new ChildRecycleViewAdapter.MyViewHolder(myview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserInformation currentChild=childData.get(position);
        Glide.with(context).load(currentChild.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_icon).into(holder.childProfile);
        holder.childName.setText(currentChild.getName());
        holder.myLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: child descriptive activity
            }
        });
    }

    @Override
    public int getItemCount() {
        return childData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView childProfile;
        TextView childName;
        LinearLayout myLinear;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myLinear=itemView.findViewById(R.id.childCardLinearLayout);
            childName=itemView.findViewById(R.id.childCardName);
            childProfile=itemView.findViewById(R.id.childProfile);
        }
    }
}
