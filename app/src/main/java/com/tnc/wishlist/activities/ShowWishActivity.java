package com.tnc.wishlist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tnc.wishlist.ModelClasses.Wishinformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.staticClass.DataCentre;

public class ShowWishActivity extends AppCompatActivity {
    TextView wishName,wishPrice,wishDescription,wishDate,wishWisherName;
    ImageView wishProfile;
    Button editWish;
    Wishinformation wishInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wish);
        initializeVariable();
        setValues();
    }
    private void setValues() {
        wishName.setText(wishInformation.getName());
        wishDescription.setText(wishInformation.getDescription());
        wishPrice.setText(wishInformation.getPrice());
//        childContact.setText((childInformation.getContactNumber()));
        wishDate.setText((wishInformation.getDate()));
        wishWisherName.setText(wishInformation.getWisherName());
        Glide.with(this).load(wishInformation.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_icon).into(wishProfile);
        if(!wishInformation.getCurrentCondition().equals(getString(R.string.approved))&&!wishInformation.getCurrentCondition().equals(getString(R.string.aproval_complete))&&DataCentre.userType>Integer.parseInt(wishInformation.getCurrentCondition())){
            editWish.setVisibility(View.VISIBLE);
        }
        editWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ShowWishActivity.this,EditWishActivity.class);
                startActivity(intent);
            }
        });
    }


    private void initializeVariable() {
        wishName=findViewById(R.id.wishNameTv);
        wishWisherName=findViewById(R.id.wishWisherNameTV);
        wishDescription=findViewById(R.id.wishDescriptionTv);
        wishDate=findViewById(R.id.wishDateTV);
        wishProfile=findViewById(R.id.wishProfileTv);
        wishInformation= DataCentre.selectedWish;
        editWish=findViewById(R.id.editWishBtn);
        wishPrice=findViewById(R.id.wishPriceTV);
    }
}
