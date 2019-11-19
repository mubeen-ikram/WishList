package com.tnc.wishlist.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tnc.wishlist.ModelClasses.UserInformation;
import com.tnc.wishlist.ModelClasses.Wishinformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.activities.ui.main.SectionsPagerAdapter;
import com.tnc.wishlist.staticClass.DataCentre;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference userReference;
    DatabaseReference wishReference;
    FirebaseAuth mAuth;
    FloatingActionButton addChild, addWish;
    TextView wishText, personText;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DataCentre.userId = mAuth.getUid();
        userReference = database.getReference(getString(R.string.usersFirebase));
        wishReference = database.getReference(getString(R.string.wishesFirebaseReference));
        //Nulling predefined data
        DataCentre.orphanAgeHomeInformations = new ArrayList<>();
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataCentre.userInformations = new ArrayList<>();
                setWishListner();
                for (DataSnapshot users : dataSnapshot.getChildren()) {
                    UserInformation user = users.getValue(UserInformation.class);
                    DataCentre.userInformations.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                ;
            }
        });


    }

    private void setWishListner() {

        wishReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataCentre.wishinformations = new ArrayList<>();
                for (DataSnapshot wishes : dataSnapshot.getChildren()) {
                    Wishinformation wish = wishes.getValue(Wishinformation.class);
                    DataCentre.wishinformations.add(wish);
                }
                continueWork();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void continueWork() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        personText = findViewById(R.id.childText);
        wishText = findViewById(R.id.wishText);
        FloatingActionButton fab = findViewById(R.id.fabMain);
        addChild = findViewById(R.id.fabAddPerson);
        addWish = findViewById(R.id.fabAddWish);
        addWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddWishActivity.class);
                startActivity(intent);
            }
        });
        addChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddChildActivity.class);
                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpen) {
                    personText.setVisibility(View.VISIBLE);
                    wishText.setVisibility(View.VISIBLE);
                    addChild.show();
                    addWish.show();
                    isOpen=true;

                }
                else{
                    personText.setVisibility(View.INVISIBLE);
                    wishText.setVisibility(View.INVISIBLE);
                    addChild.hide();
                    addWish.hide();
                    isOpen=false;

                }
            }
        });
    }
}