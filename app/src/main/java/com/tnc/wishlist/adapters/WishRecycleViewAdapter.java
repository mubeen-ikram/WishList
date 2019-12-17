package com.tnc.wishlist.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.tnc.wishlist.ModelClasses.Wishinformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.activities.ShowWishActivity;
import com.tnc.wishlist.staticClass.DataCentre;

import java.util.ArrayList;

public class WishRecycleViewAdapter extends RecyclerView.Adapter<WishRecycleViewAdapter.MyViewHolder> {
    private Context context;
    private int resources;
    private ArrayList<Wishinformation> wishData;
    FirebaseDatabase database;
    DatabaseReference myRefrence;

    public WishRecycleViewAdapter(Context context, int resources, ArrayList<Wishinformation> wishData) {
        this.context = context;
        this.resources = resources;
        this.wishData = wishData;
        database=FirebaseDatabase.getInstance();
        myRefrence=database.getReference().child(context.getString(R.string.wishesFirebaseReference));
    }

    @NonNull
    @Override
    public WishRecycleViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myview = LayoutInflater.from(parent.getContext()).inflate(resources, parent, false);
        return new WishRecycleViewAdapter.MyViewHolder(myview);

    }

    @Override
    public void onBindViewHolder(@NonNull WishRecycleViewAdapter.MyViewHolder holder, int position) {
        final Wishinformation wish = wishData.get(position);
        holder.accept.setVisibility(View.GONE);
        holder.decline.setVisibility(View.GONE);
        if(!wish.getCurrentCondition().equals(context.getString(R.string.approved))&&!wish.getCurrentCondition().equals(context.getString(R.string.completed))&&!wish.getCurrentCondition().equals(context.getString(R.string.aproval_complete))){
            Integer myInt=Integer.parseInt(wish.getCurrentCondition());
            Integer userType=DataCentre.userType;
            if(myInt<userType){
                holder.accept.setVisibility(View.VISIBLE);
                holder.decline.setVisibility(View.VISIBLE);
            }
        }
        if (wishData.get(position).getPhoto() != null)
            Glide.with(context).load(wish.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_add_item).into(holder.profilePic);
        else
            holder.profilePic.setVisibility(View.GONE);
        holder.tvDate.setText("Date: " + wish.getDate());
        holder.tvPrice.setText("Price: " + wish.getPrice());
        holder.tvWisher.setText("Wished by : " + wish.getWisherName());
        holder.wishName.setText(wish.getName());
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DataCentre.userType>1) {
                    wish.setCurrentCondition(context.getString(R.string.approved));
                }
                else{
                    wish.setCurrentCondition(String.valueOf(DataCentre.userType));
                }
                String wisherName=wish.getWisherName();
                wish.setWisherName("");
                myRefrence.child(wish.getWishId()).setValue(wish);
                wish.setWisherName(wisherName);
            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wish.setCurrentCondition(context.getString(R.string.decline));
                String wisherName=wish.getWisherName();
                wish.setWisherName("");
                myRefrence.child(wish.getWishId()).setValue(wish);
                wish.setWisherName(wisherName);
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCentre.selectedWish=wish;
                Intent intent=new Intent(context, ShowWishActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView wishName, tvWisher, tvPrice, tvDate;
        ImageView profilePic,accept,decline;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            wishName = itemView.findViewById(R.id.wishCardName);
            tvWisher = itemView.findViewById(R.id.wishCardWisher);
            tvPrice = itemView.findViewById(R.id.wishCardPrice);
            tvDate = itemView.findViewById(R.id.wishCardDate);
            profilePic = itemView.findViewById(R.id.wishProfile);
            accept=itemView.findViewById(R.id.acceptWish);
            decline=itemView.findViewById(R.id.rejectWish);
            linearLayout = itemView.findViewById(R.id.wishCardLinearLayout);
        }
    }
}
