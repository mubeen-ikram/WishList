package com.tnc.wishlist.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tnc.wishlist.ModelClasses.UserInformation;
import com.tnc.wishlist.ModelClasses.Wishinformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.staticClass.DataCentre;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class AddWishActivity extends AppCompatActivity {
    ImageView wishPic;
    EditText wishName,wishPrice,wishDescription;
    Spinner wisherName;
    Button approveButton;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference wishReference;
    Uri filePath;
    Wishinformation wishinformation;
    StorageReference storageReference;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wish);
        initializeVariable();
        populateSpinner();
        setOnClicklistner();
    }
    private void setOnClicklistner() {
        wishPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissionForStorage();
            }
        });
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePath == null) {
                    Toast.makeText(AddWishActivity.this, R.string.imageIconNotFound, Toast.LENGTH_LONG).show();
                    return;
                }
                if(wishName.getText().toString().length()<1){
                    Toast.makeText(AddWishActivity.this, R.string.wishnamenotfound, Toast.LENGTH_LONG).show();

                    wishName.setError(getString(R.string.empty_input_error));
                    return;
                }
                if(wishDescription.getText().toString().length()<1){
                    Toast.makeText(AddWishActivity.this, R.string.descriptionnotfound, Toast.LENGTH_LONG).show();
                    wishDescription.setError(getString(R.string.empty_input_error));
                    return;
                }
                if(wishPrice.getText().toString().length()<1){
                    Toast.makeText(AddWishActivity.this, R.string.price_not_found, Toast.LENGTH_LONG).show();
                    wishPrice.setError(getString(R.string.empty_input_error));
                    return;
                }
                if(wisherName.getSelectedItem().toString().length()<1){
                    Toast.makeText(AddWishActivity.this, R.string.wisher_name_not_found, Toast.LENGTH_LONG).show();
                    return;
                }
                wishinformation.setCurrentCondition(getString(R.string.Pending0));
                wishinformation.setDate(new Date().toString());
                wishinformation.setDescription(wishDescription.getText().toString());
                wishinformation.setName(wishName.getText().toString());
                for(UserInformation user:DataCentre.userInformations){
                    if(user.getName().equals(wisherName.getSelectedItem().toString())){
                        wishinformation.setOrphanId(user.getUserId());
                        break;
                    }
                }
                wishinformation.setWisherName(wisherName.getSelectedItem().toString());
                wishinformation.setPrice(wishPrice.getText().toString());
                wishinformation.setRequirnment(getString(R.string.falses));
                uploadImage();
            }
        });

    }
    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("WishesImages/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete()) {
                                try {
                                    sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            filePath = uri.getResult();
                            wishinformation.setPhoto(filePath.toString());
                            Toast.makeText(AddWishActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            continueRegistration();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddWishActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");

                        }
                    });
        }
    }
    private void continueRegistration() {
        wishReference=wishReference.push();
        String key=wishReference.getKey();
        wishinformation.setWishId(key);
        wishReference.setValue(wishinformation);
        Toast.makeText(AddWishActivity.this, R.string.approval_sent, Toast.LENGTH_LONG).show();
        DataCentre.wishinformations.add(wishinformation);
        this.finish();
    }
    private void populateSpinner() {
        ArrayList<String> childName=new ArrayList<>();
        for(UserInformation userInformation: DataCentre.userInformations){
            if(userInformation.getOrphanageId().equals(mAuth.getUid())&&!userInformation.getCondition().equals(getString(R.string.Pending0))){
                childName.add(userInformation.getName());
            }
        }
        ArrayAdapter<String> childNameAdpater =  new ArrayAdapter<String>(this,
                R.layout.spinner_layout, childName);
        wisherName.setAdapter(childNameAdpater);



    }
    private void initializeVariable() {
        wishinformation=new Wishinformation();
        wishPic=findViewById(R.id.addWishPic);
        wishName=findViewById(R.id.addWishName);
        wishPrice=findViewById(R.id.addWishPrice);
        wishDescription=findViewById(R.id.addWishDescrpition);
        wisherName=findViewById(R.id.add_wisher_spinner);
        approveButton=findViewById(R.id.approveWishbtn);
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        wishReference=database.getReference(getString(R.string.wishesFirebaseReference));
        storageReference = FirebaseStorage.getInstance().getReference();
    }
    private void askPermissionForStorage() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        else {
            openStorageApp();
        }
    }
    private void openStorageApp() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // Log.d(TAG, String.valueOf(bitmap));
                wishPic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_LONG).show();
                openStorageApp();

            } else {
                Toast.makeText(this, "Storage permission denied.Please allow app to use Storage", Toast.LENGTH_LONG).show();

            }
        }
    }

}
