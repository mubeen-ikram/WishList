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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Date;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class AddChildActivity extends AppCompatActivity {
    EditText childName,childContact,childDescription;
    Button sendForApprovebutton;
    ImageView childPic;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference childReference;
    Uri filePath;
    UserInformation userInformation;
    StorageReference storageReference;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        initializeVariable();
        setOnclickListner();
    }

    private void setOnclickListner() {
        childPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissionForStorage();
            }
        });
        sendForApprovebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePath == null) {
                    Toast.makeText(AddChildActivity.this, R.string.imageIconNotFound, Toast.LENGTH_LONG).show();
                    return;
                }
                if(childName.getText().toString().length()<1){
                    Toast.makeText(AddChildActivity.this, R.string.wishnamenotfound, Toast.LENGTH_LONG).show();

                    childName.setError(getString(R.string.empty_input_error));
                    return;
                }
                if(childDescription.getText().toString().length()<1){
                    Toast.makeText(AddChildActivity.this, R.string.descriptionnotfound, Toast.LENGTH_LONG).show();
                    childDescription.setError(getString(R.string.empty_input_error));
                    return;
                }
                if(childContact.getText().toString().length()<1){
                    Toast.makeText(AddChildActivity.this, R.string.price_not_found, Toast.LENGTH_LONG).show();
                    childContact.setError(getString(R.string.empty_input_error));
                    return;
                }
                userInformation.setCondition(getString(R.string.Pending0));
                userInformation.setContactNumber(childContact.getText().toString());
                userInformation.setDescription(childDescription.getText().toString());
                userInformation.setName(childName.getText().toString());
                userInformation.setOrphanageId(mAuth.getUid());
                userInformation.setType(getString(R.string.OrphanO));
                uploadImage();

            }
        });

    }
    private void askPermissionForStorage() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        else {
            openStorageApp();
        }
    }

    private void initializeVariable() {
        childName=findViewById(R.id.addChildName);
        childContact=findViewById(R.id.addChildContact);
        childDescription=findViewById(R.id.addChildDescrpition);
        sendForApprovebutton=findViewById(R.id.approveChildBtn);
        childPic=findViewById(R.id.addChildPic);
        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        childReference=database.getReference(getString(R.string.usersFirebase));
        userInformation=new UserInformation();
        storageReference= FirebaseStorage.getInstance().getReference();
    }
    private void openStorageApp() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("UsersImages/" + UUID.randomUUID().toString());
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
                            userInformation.setPhoto(filePath.toString());
                            Toast.makeText(AddChildActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            continueRegistration();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddChildActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        childReference=childReference.push();
        String key=childReference.getKey();
        userInformation.setUserId(key);
        childReference.setValue(userInformation);
        Toast.makeText(AddChildActivity.this, R.string.child_sent_for_approval, Toast.LENGTH_LONG).show();
        DataCentre.userInformations.add(userInformation);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                childPic.setImageBitmap(bitmap);
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
//                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_LONG).show();
                openStorageApp();

            } else {
//                Toast.makeText(this, "Storage permission denied.Please allow app to use Storage", Toast.LENGTH_LONG).show();

            }
        }
    }

}
