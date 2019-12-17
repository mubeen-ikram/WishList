package com.tnc.wishlist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tnc.wishlist.ModelClasses.AdminInformation;
import com.tnc.wishlist.ModelClasses.OrphanAgeHomeInformation;
import com.tnc.wishlist.R;
import com.tnc.wishlist.staticClass.DataCentre;

public class SignInActivity extends AppCompatActivity {
    EditText signInEmail, signInPass;
    Button signInbtn, signUpBtn, forgotPassButton;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference currentOrphanageRef, adminRef;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        InitializeVariable();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        progressDialog = ProgressDialog.show(SignInActivity.this, "",
                "Loading. Please wait...", true);
        updateUIAuth(currentUser);
        setClickListners();
    }

    private void setClickListners() {
        signInbtn.setOnClickListener(signInClick);
        signUpBtn.setOnClickListener(signupClick);
        forgotPassButton.setOnClickListener(forgotPassClick);
    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    View.OnClickListener signInClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email, pass;
            email = signInEmail.getText().toString();
            pass = signInPass.getText().toString();
            if (!isValidEmail(email)) {
                Toast.makeText(SignInActivity.this, getString(R.string.emailCorrectionText), Toast.LENGTH_LONG).show();
                signInEmail.setError(getString(R.string.emailError));
                return;
            }
            if (pass.length() < 7) {
                Toast.makeText(SignInActivity.this, getString(R.string.password_Correction), Toast.LENGTH_LONG).show();
                signInPass.setError(getString(R.string.password_Error));
                return;
            }

            progressDialog.show();
            //Signin user from firebase
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUIAuth(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignInActivity.this, getString(R.string.auth_failed_try_again),
                                        Toast.LENGTH_SHORT).show();
                                updateUIAuth(null);
                            }
                        }
                    });
        }
    };
    View.OnClickListener signupClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            gotoSignUp();
        }
    };

    private void gotoSignUp() {
        Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    View.OnClickListener forgotPassClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email;
            email = signInEmail.getText().toString();
            if (!isValidEmail(email)) {
                Toast.makeText(SignInActivity.this, getString(R.string.emailCorrectionText), Toast.LENGTH_LONG).show();
                signInEmail.setError(getString(R.string.emailError));
                return;
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Log.d(TAG, "Email sent.");
                                Toast.makeText(SignInActivity.this, getString(R.string.passwor_reset_email), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignInActivity.this, getString(R.string.forgot_error), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    };

    private void updateUIAuth(FirebaseUser currentUser) {
        if (currentUser != null) {
            //call database and get its value

            currentOrphanageRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        OrphanAgeHomeInformation orphanAgeHomeInformation = dataSnapshot.getValue(OrphanAgeHomeInformation.class);
                        if (orphanAgeHomeInformation.getStatus().equals(getString(R.string.Pending0))) {
                            progressDialog.dismiss();
                            Intent toWaitActivity = new Intent(SignInActivity.this, WaitingActivity.class);
                            startActivity(toWaitActivity);
//                        SignInActivity.this.finish();
                        } else {
                            progressDialog.dismiss();
                            DataCentre.userType=1;
                            toMainAcitivty();
                        }
                    } else {
                        adminRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                AdminInformation adminInformation=dataSnapshot.getValue(AdminInformation.class);
                                if(adminInformation.getLevel()!=null&&adminInformation.getLevel().equals("2")){
                                    DataCentre.userType=2;
                                }
                                else{
                                    DataCentre.userType=0;
                                }
                                progressDialog.dismiss();
                                toMainAcitivty();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            progressDialog.dismiss();
            //continue.com
        }
    }

    private void toMainAcitivty() {
        Intent toMainActivity = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(toMainActivity);
        SignInActivity.this.finish();
    }

    private void InitializeVariable() {
        signInEmail = findViewById(R.id.signInEmail);
        signInPass = findViewById(R.id.signInPass);
        signInbtn = findViewById(R.id.signInBtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        database = FirebaseDatabase.getInstance();
        currentOrphanageRef = database.getReference(getString(R.string.orphanagesFirebase));
        adminRef = database.getReference(getString(R.string.admin_firebase));
        forgotPassButton = findViewById(R.id.forgotPassword);
        mAuth = FirebaseAuth.getInstance();
    }
}
