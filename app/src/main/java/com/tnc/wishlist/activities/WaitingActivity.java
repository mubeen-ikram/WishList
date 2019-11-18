package com.tnc.wishlist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tnc.wishlist.ModelClasses.OrphanAgeHomeInformation;
import com.tnc.wishlist.R;

public class WaitingActivity extends AppCompatActivity {
TextView email;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference(getString(R.string.orphanagesFirebase));
        email=findViewById(R.id.emailWaiting);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"+email.getText().toString())); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, email.getText().toString());
                intent.putExtra(Intent.EXTRA_SUBJECT, (getString(R.string.approveSubject)));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    WaitingActivity.this.finish();
                }
            }
        });
        startDatabaselistner();
    }

    private void startDatabaselistner() {
        myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OrphanAgeHomeInformation orphanAgeHomeInformation = dataSnapshot.getValue(OrphanAgeHomeInformation.class);
                if (orphanAgeHomeInformation.getStatus().equals(getString(R.string.Pending0))) {
                } else {
                    Toast.makeText(WaitingActivity.this,"Your request have been approved",Toast.LENGTH_LONG).show();
                    Intent toMainActivity = new Intent(WaitingActivity.this, MainActivity.class);
                    startActivity(toMainActivity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}