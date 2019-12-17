package com.tnc.wishlist.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.tnc.wishlist.ModelClasses.OrphanAgeHomeInformation;
import com.tnc.wishlist.ModelClasses.Wishinformation;
import com.tnc.wishlist.ModelClasses.childInformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.staticClass.DataCentre;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class EditWishActivity extends AppCompatActivity {
    ImageView wishPic;
    EditText wishName, wishPrice, wishDescription;
    Spinner wisherName,orphanAgeName;
    Button approveButton;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference wishReference;
    Uri filePath;
    String mCurrentPhotoPath;
    Wishinformation wishinformation;
    StorageReference storageReference;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private int PICK_IMAGE_REQUEST = 1;
    private int CAMERA_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wish);
        initializeVariable();
        setValues();
        populateSpinner();
        setOnClicklistner();
        setToolbar();
    }

    private void setValues() {
        wishName.setText(wishinformation.getName());
        wishPrice.setText(wishinformation.getPrice());
        wishDescription.setText(wishinformation.getDescription());
        Glide.with(this).load(wishinformation.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_icon).into(wishPic);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setOnClicklistner() {
        wishPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkOnWishPic();
            }
        });
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wishName.getText().toString().length() < 1) {
                    Toast.makeText(EditWishActivity.this, R.string.wishnamenotfound, Toast.LENGTH_LONG).show();

                    wishName.setError(getString(R.string.empty_input_error));
                    return;
                }
                if (wishDescription.getText().toString().length() < 1) {
                    Toast.makeText(EditWishActivity.this, R.string.descriptionnotfound, Toast.LENGTH_LONG).show();
                    wishDescription.setError(getString(R.string.empty_input_error));
                    return;
                }
                if (wishPrice.getText().toString().length() < 1) {
                    Toast.makeText(EditWishActivity.this, R.string.price_not_found, Toast.LENGTH_LONG).show();
                    wishPrice.setError(getString(R.string.empty_input_error));
                    return;
                }
                if (wisherName.getSelectedItem().toString().length() < 1) {
                    Toast.makeText(EditWishActivity.this, R.string.wisher_name_not_found, Toast.LENGTH_LONG).show();
                    return;
                }
                if(DataCentre.userType>1) {
                    wishinformation.setCurrentCondition(getString(R.string.approved));
                }
                else{
                    wishinformation.setCurrentCondition(String.valueOf(DataCentre.userType));
                }


                wishinformation.setDate(new Date().toString());
                wishinformation.setDescription(wishDescription.getText().toString());
                wishinformation.setName(wishName.getText().toString());
                wishinformation.setPricePayed(getString(R.string.zero));
                for (childInformation user : DataCentre.childInformations) {
                    if (user.getName().equals(wisherName.getSelectedItem().toString())) {
                        wishinformation.setOrphanId(user.getUserId());
                        break;
                    }
                }
                wishinformation.setPrice(wishPrice.getText().toString());
                wishinformation.setRequirnment(getString(R.string.falses));
                if (filePath != null)
                    uploadImage();
                else
                    continueRegistration();
            }
        });

    }

    private void WorkOnWishPic() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(EditWishActivity.this);
        builder.setTitle("Take Picture");
        builder.setMessage("Select the picture From");

        builder.setPositiveButton("Camera", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        askPermissionForCamera();
                    }
                });
        builder.setNegativeButton("Gallery", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        askPermissionForStorage();
                        ;//
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void askPermissionForCamera() {
        if (ActivityCompat.checkSelfPermission(EditWishActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditWishActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        } else {
            callForCamera();
        }

    }

    private void callForCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.tnc.wishlist.fileprovider",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setCancelable(false);
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
//                            Toast.makeText(EditWishActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            continueRegistration();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
//                            Toast.makeText(EditWishActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.setMessage("Please Wait");

                        }
                    });
        }
    }

    private void continueRegistration() {
//        wishReference = wishReference.push();
        String key = wishinformation.getWishId();
        wishinformation.setWishId(key);
        wishReference.child(key).setValue(wishinformation);
        Toast.makeText(EditWishActivity.this, R.string.approval_sent, Toast.LENGTH_LONG).show();
        DataCentre.wishinformations.add(wishinformation);
        this.finish();
    }

    private void populateSpinner() {
        if(DataCentre.userType==1) {
            ArrayList<String> childName = new ArrayList<>();
            for (childInformation childInformation : DataCentre.childInformations) {
                if (childInformation.getOrphanageId().equals(mAuth.getUid()) && childInformation.getCondition().equals(getString(R.string.approved))) {
                    childName.add(childInformation.getName());
                }
            }
            ArrayAdapter<String> childNameAdpater = new ArrayAdapter<String>(this,
                    R.layout.spinner_layout, childName);
            wisherName.setAdapter(childNameAdpater);
        }
        else{
            orphanAgeName.setVisibility(View.VISIBLE);
            final ArrayList<String> homeName = new ArrayList<>();
            for (OrphanAgeHomeInformation homeInformation : DataCentre.orphanAgeHomeInformations) {
                if (homeInformation.getStatus().equals(getString(R.string.approved))) {
                    homeName.add(homeInformation.getName());
                }
            }
            ArrayAdapter<String> homeNameAdpater = new ArrayAdapter<String>(this,
                    R.layout.spinner_layout, homeName);
            orphanAgeName.setAdapter(homeNameAdpater);
            orphanAgeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String orphanageName=orphanAgeName.getSelectedItem().toString();
                    for(OrphanAgeHomeInformation home:DataCentre.orphanAgeHomeInformations){
                        if(orphanageName.equals(home.getName())){
                            ArrayList<String> childName = new ArrayList<>();
                            for (childInformation childInformation : DataCentre.childInformations) {
                                if (childInformation.getOrphanageId().equals(home.getOrphanageId()) && childInformation.getCondition().equals(getString(R.string.approved))) {
                                    childName.add(childInformation.getName());
                                }
                            }
                            ArrayAdapter<String> childNameAdpater = new ArrayAdapter<String>(EditWishActivity.this,
                                    R.layout.spinner_layout, childName);
                            wisherName.setAdapter(childNameAdpater);
                            break;

                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    homeName.size();
                }
            });
        }

    }

    private void initializeVariable() {
        wishinformation = DataCentre.selectedWish;
        wishPic = findViewById(R.id.addWishPic);
        wishName = findViewById(R.id.addWishName);
        wishPrice = findViewById(R.id.addWishPrice);
        wishDescription = findViewById(R.id.addWishDescrpition);
        wisherName = findViewById(R.id.add_wisher_spinner);
        approveButton = findViewById(R.id.approveWishbtn);
        orphanAgeName=findViewById(R.id.add_orphanage_spinner);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        wishReference = database.getReference(getString(R.string.wishesFirebaseReference));
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
                Glide.with(this).load(bitmap).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_icon).into(wishPic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

//            filePath = data.getData();
            File file = new File(mCurrentPhotoPath);
            Bitmap bitmap = null;
            try {
                filePath = Uri.fromFile(file);
                bitmap = MediaStore.Images.Media
                        .getBitmap(EditWishActivity.this.getContentResolver(), Uri.fromFile(file));
                FileOutputStream outStream = new FileOutputStream(new File(String.valueOf(filePath)));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                Glide.with(this).load(bitmap).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_icon).into(wishPic);
//                wishPic.setImageBitmap(bitmap);
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
