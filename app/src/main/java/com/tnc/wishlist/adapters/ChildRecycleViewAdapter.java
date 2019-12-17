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
import com.tnc.wishlist.ModelClasses.childInformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.activities.ShowChildActivity;
import com.tnc.wishlist.staticClass.DataCentre;

import java.util.ArrayList;

public class ChildRecycleViewAdapter extends RecyclerView.Adapter<ChildRecycleViewAdapter.MyViewHolder> {
    private Context context;
    FirebaseDatabase database;
    DatabaseReference myRefrence;
    private int resources;
    private ArrayList<childInformation> childData;

    public ChildRecycleViewAdapter(Context context, int resources, ArrayList<childInformation> childData) {
        this.context = context;
        this.resources = resources;
        this.childData = childData;
        database=FirebaseDatabase.getInstance();
        myRefrence=database.getReference().child(context.getString(R.string.firebaseChild));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myview = LayoutInflater.from(parent.getContext()).inflate(resources, parent, false);
        return new ChildRecycleViewAdapter.MyViewHolder(myview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final childInformation currentChild = childData.get(position);
        holder.accept.setVisibility(View.GONE);
        holder.decline.setVisibility(View.GONE);
        if(!currentChild.getCondition().equals(context.getString(R.string.approved))&&!currentChild.getCondition().equals(context.getString(R.string.completed))){
            Integer myInt=Integer.parseInt(currentChild.getCondition());

            if(myInt<DataCentre.userType){
                holder.accept.setVisibility(View.VISIBLE);
                holder.decline.setVisibility(View.VISIBLE);

            }
        }
        if (childData.get(position).getPhoto() != null)
            Glide.with(context).load(currentChild.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_icon).into(holder.childProfile);
        else
            holder.childProfile.setVisibility(View.GONE);
        holder.childName.setText(currentChild.getName());
        holder.myLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCentre.selectedChild=currentChild;
                Intent intent=new Intent(context, ShowChildActivity.class);
                context.startActivity(intent);
                //TODO: child descriptive activity
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(DataCentre.userType>1) {
                    currentChild.setCondition(context.getString(R.string.approved));
                }
                else{
                    currentChild.setCondition(String.valueOf(DataCentre.userType));
                }

//                currentChild.setCondition(context.getString(R.string.approved));
                myRefrence.child(currentChild.getUserId()).setValue(currentChild);

            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentChild.setCondition(context.getString(R.string.decline));
                myRefrence.child(currentChild.getUserId()).setValue(currentChild);
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
        ImageView accept,decline;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myLinear = itemView.findViewById(R.id.childCardLinearLayout);
            childName = itemView.findViewById(R.id.childCardName);
            childProfile = itemView.findViewById(R.id.childProfile);
            accept=itemView.findViewById(R.id.acceptChild);
            decline=itemView.findViewById(R.id.rejectChild);

        }
    }
}
