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
import com.tnc.wishlist.ModelClasses.childInformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.staticClass.DataCentre;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    childInformation childInformation;
    String mCurrentPhotoPath;
    StorageReference storageReference;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private int PICK_IMAGE_REQUEST = 1;
    private int CAMERA_REQUEST=1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        initializeVariable();
        setOnclickListner();
        setToolbar();
    }

    private void setToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setOnclickListner() {
        childPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workOnChildPic();
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
                    Toast.makeText(AddChildActivity.this, R.string.name_child_missing, Toast.LENGTH_LONG).show();

                    childName.setError(getString(R.string.empty_input_error));
                    return;
                }
                if(childDescription.getText().toString().length()<1){
                    Toast.makeText(AddChildActivity.this, R.string.descriptionnotfound, Toast.LENGTH_LONG).show();
                    childDescription.setError(getString(R.string.empty_input_error));
                    return;
                }
                if(childContact.getText().toString().length()<1){
                    Toast.makeText(AddChildActivity.this, R.string.enter_contact_number, Toast.LENGTH_LONG).show();
                    childContact.setError(getString(R.string.empty_input_error));
                    return;
                }
                childInformation.setCondition(getString(R.string.Pending0));
                childInformation.setContactNumber(childContact.getText().toString());
                childInformation.setDescription(childDescription.getText().toString());
                childInformation.setName(childName.getText().toString());
                childInformation.setOrphanageId(mAuth.getUid());
                childInformation.setType(getString(R.string.OrphanO));
                uploadImage();

            }
        });

    }

    private void workOnChildPic() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(AddChildActivity.this);
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
        if(ActivityCompat.checkSelfPermission(AddChildActivity.this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddChildActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST);
        }
        else{
            callForCamera();
        }

    }


    private void callForCamera() {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
        childReference=database.getReference(getString(R.string.firebaseChild));
        childInformation =new childInformation();
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
            progressDialog.setTitle("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference ref = storageReference.child("ChildImages/" + UUID.randomUUID().toString());
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
                            childInformation.setPhoto(filePath.toString());
//                            Toast.makeText(AddChildActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
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
//                            progressDialog.setMessage("please Wait");

                        }
                    });
        }
    }
    private void continueRegistration() {
        childReference=childReference.push();
        String key=childReference.getKey();
        childInformation.setUserId(key);
        childReference.setValue(childInformation);
        Toast.makeText(AddChildActivity.this, R.string.child_sent_for_approval, Toast.LENGTH_LONG).show();
        DataCentre.childInformations.add(childInformation);

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
        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK ) {

//            filePath = data.getData();
            File file = new File(mCurrentPhotoPath);
            Bitmap bitmap = null;
            try {
                filePath= Uri.fromFile(file);
                bitmap = MediaStore.Images.Media
                        .getBitmap(AddChildActivity.this.getContentResolver(), Uri.fromFile(file));
                FileOutputStream outStream = new FileOutputStream(new File(String.valueOf(filePath)));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                childPic.setImageBitmap(bitmap);
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
        else if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_LONG).show();
                callForCamera();

            } else {
                Toast.makeText(this, "Storage permission denied.Please allow app to use Storage", Toast.LENGTH_LONG).show();

            }
        }
    }

}
