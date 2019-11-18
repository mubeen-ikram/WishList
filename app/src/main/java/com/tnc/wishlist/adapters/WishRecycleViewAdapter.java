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
import com.tnc.wishlist.ModelClasses.Wishinformation;
import com.tnc.wishlist.R;

import java.util.ArrayList;

public class WishRecycleViewAdapter extends RecyclerView.Adapter<WishRecycleViewAdapter.MyViewHolder> {
    private Context context;
    private int resources;
    private ArrayList<Wishinformation> wishData;

    public WishRecycleViewAdapter(Context context, int resources, ArrayList<Wishinformation> wishData) {
        this.context = context;
        this.resources = resources;
        this.wishData = wishData;
    }

    @NonNull
    @Override
    public WishRecycleViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myview= LayoutInflater.from(parent.getContext()).inflate(resources,parent,false);
        return new WishRecycleViewAdapter.MyViewHolder(myview);

    }

    @Override
    public void onBindViewHolder(@NonNull WishRecycleViewAdapter.MyViewHolder holder, int position) {
        Wishinformation wish=wishData.get(position);
        Glide.with(context).load(wish.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_add_item).into(holder.profilePic);
        holder.tvDate.setText("Date: "+wish.getDate());
        holder.tvPrice.setText("Price: " +wish.getPrice());
        holder.tvWisher.setText("Wished by : "+wish.getWisherName());
        holder.wishName.setText(wish.getName());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: open detailed activity
            }
        });
    }
    @Override
    public int getItemCount() {
        return wishData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView wishName,tvWisher,tvPrice,tvDate;
        ImageView profilePic;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            wishName=itemView.findViewById(R.id.wishCardName);
            tvWisher=itemView.findViewById(R.id.wishCardWisher);
            tvPrice=itemView.findViewById(R.id.wishCardPrice);
            tvDate=itemView.findViewById(R.id.wishCardDate);
            profilePic=itemView.findViewById(R.id.wishProfile);
            linearLayout=itemView.findViewById(R.id.wishCardLinearLayout);
        }
    }
}
