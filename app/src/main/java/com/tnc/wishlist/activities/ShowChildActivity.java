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
import com.tnc.wishlist.ModelClasses.childInformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.staticClass.DataCentre;

public class ShowChildActivity extends AppCompatActivity {
    TextView childName,childContact,childDescription,childDateOfBirth,childEthnicity;
    ImageView childProfile;
    Button editChild;
    childInformation childInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_child);
        initializeValue();
        setValues();

    }

    private void setValues() {
        childName.setText(childInformation.getName());
        childDescription.setText(childInformation.getDescription());
        childContact.setText((childInformation.getContactNumber()));
        childDateOfBirth.setText((childInformation.getDateOfBirth()));
        childEthnicity.setText(childInformation.getEthnicity());
        Glide.with(this).load(childInformation.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_icon).into(childProfile);
        if(!childInformation.getCondition().equals(getString(R.string.approved))&&DataCentre.userType>Integer.parseInt(childInformation.getCondition())){
            editChild.setVisibility(View.VISIBLE);
        }
        editChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ShowChildActivity.this,EditChildActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeValue() {
        childName=findViewById(R.id.childNameTv);
        childContact=findViewById(R.id.childcontactNumberTV);
        childDescription=findViewById(R.id.childDescriptionTv);
        childDateOfBirth=findViewById(R.id.childDateOfBirthTV);
        childProfile=findViewById(R.id.childProfileTv);
        childInformation= DataCentre.selectedChild;
        editChild=findViewById(R.id.editChildBtn);
        childEthnicity=findViewById(R.id.childEthnicityTV);
    }
}
