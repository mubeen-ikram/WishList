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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tnc.wishlist.ModelClasses.OrphanAgeHomeInformation;
import com.tnc.wishlist.R;

import java.io.IOException;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class SignUpActivity extends AppCompatActivity {
    EditText etEmail, etpass, eTcrtpass, eTDescrption, eTcontact, eTUsername;
    Button btnRegister;
    ImageView profile;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference orphanageRef;
    OrphanAgeHomeInformation orphanAgeInfo;
    StorageReference storageReference;
    Uri filePath;
    //Permission Variables

    private static final int PERMISSION_REQUEST_CODE = 1;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeVariable();
    }

    private void initializeVariable() {
        orphanAgeInfo = new OrphanAgeHomeInformation();
        etEmail = findViewById(R.id.signUpEmail);
        etpass = findViewById(R.id.signUpPass);
        eTcrtpass = findViewById(R.id.signUpConfirmPass);
        eTDescrption = findViewById(R.id.signUpDescrpition);
        eTcontact = findViewById(R.id.signUpContactInfo);
        eTUsername = findViewById(R.id.signUpUserName);
        btnRegister = findViewById(R.id.approveWishbtn);
        profile = findViewById(R.id.signupProfile);
        database = FirebaseDatabase.getInstance();
        orphanageRef = database.getReference(getString(R.string.orphanges_firebase));
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setOnClickListner();
    }

    private void setOnClickListner() {
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissionForStorage();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, pass, crtpass, desc, contact, username;
                email = etEmail.getText().toString();
                pass = etpass.getText().toString();
                crtpass = eTcrtpass.getText().toString();
                desc = eTDescrption.getText().toString();
                contact = eTcontact.getText().toString();
                username = eTUsername.getText().toString();
                if (!isValidEmail(email)) {
                    Toast.makeText(SignUpActivity.this, getString(R.string.emailCorrectionText), Toast.LENGTH_LONG).show();
                    etEmail.setError(getString(R.string.emailError));
                    return;
                }
                if (pass.length() < 7) {
                    Toast.makeText(SignUpActivity.this, getString(R.string.password_Correction), Toast.LENGTH_LONG).show();
                    etpass.setError(getString(R.string.password_Error));
                    return;
                }

                if (crtpass.length() < 7) {
                    Toast.makeText(SignUpActivity.this, getString(R.string.password_Correction), Toast.LENGTH_LONG).show();
                    eTcrtpass.setError(getString(R.string.password_Error));
                    return;
                }
                if (!crtpass.equals(pass)) {
                    Toast.makeText(SignUpActivity.this, getString(R.string.password_not_match_warn), Toast.LENGTH_LONG).show();
                    etpass.setError(getString(R.string.password_mismatch));
                    eTcrtpass.setError(getString(R.string.password_mismatch));
                    return;
                }
                if (desc.length() < 1) {
                    Toast.makeText(SignUpActivity.this, getString(R.string.descrption_warning), Toast.LENGTH_LONG).show();
                    eTDescrption.setError(getString(R.string.empty_input_error));
                    return;
                }
                if (username.length() < 1) {
                    Toast.makeText(SignUpActivity.this, R.string.signup_name_warn, Toast.LENGTH_LONG).show();
                    eTUsername.setError(getString(R.string.empty_input_error));
                    return;
                }
                if (contact.length() < 10) {
                    Toast.makeText(SignUpActivity.this, R.string.signup_contact_warning, Toast.LENGTH_LONG).show();
                    eTcontact.setError(getString(R.string.empty_input_error));
                    return;
                }
                if (filePath == null) {
                    Toast.makeText(SignUpActivity.this, R.string.imageIconNotFound, Toast.LENGTH_LONG).show();
                    return;
                }
                orphanAgeInfo.setContactNumber(contact);
                orphanAgeInfo.setDescrpition(desc);
                orphanAgeInfo.setEmail(email);
                orphanAgeInfo.setName(username);
                orphanAgeInfo.setNoifchildren("0");
                orphanAgeInfo.setStatus(getString(R.string.Pending0));
                uploadImage();
            }
        });
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("OrphanageImages/" + UUID.randomUUID().toString());
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
                            orphanAgeInfo.setPhoto(filePath.toString());
                            Toast.makeText(SignUpActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            continueRegistration();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void askPermissionForStorage() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        else {
            openStorageApp();
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
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void continueRegistration() {
        String email, password;
        email = etEmail.getText().toString();
        password = etpass.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed. please try again",
                                    Toast.LENGTH_SHORT).show();

                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String key = user.getUid();
            orphanAgeInfo.setOrphanageId(key);
            orphanageRef.child(key).setValue(orphanAgeInfo);
            Toast.makeText(SignUpActivity.this, R.string.registeredSuccessfully, Toast.LENGTH_LONG).show();
            mAuth.signOut();
            gotoSignInScreen();

        }
    }

    private void gotoSignInScreen() {
        Intent signIn = new Intent(SignUpActivity.this, SignInActivity.class);
        this.finish();
        startActivity(signIn);
    }
}
