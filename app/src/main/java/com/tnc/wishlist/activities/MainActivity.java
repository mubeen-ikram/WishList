package com.tnc.wishlist.activities;

import android.app.ProgressDialog;
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
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DataCentre.userId=mAuth.getUid();
        userReference = database.getReference(getString(R.string.usersFirebase));
        wishReference = database.getReference(getString(R.string.wishesFirebaseReference));
        //Nulling predefined data
        DataCentre.userInformations = new ArrayList<>();
        DataCentre.wishinformations = new ArrayList<>();
        DataCentre.orphanAgeHomeInformations = new ArrayList<>();
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

        wishReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}