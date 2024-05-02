package com.example.assurini.UI.Authentification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.assurini.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgetPassword extends AppCompatActivity {
    private ImageView arrowImage;
    private Button buttonLink;
    private EditText editTextEmail;
    private FirebaseAuth auth ;


    private static final String TAG = "MyAppLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        editTextEmail = findViewById(R.id.editTextEmail);
        auth = FirebaseAuth.getInstance();


        arrowImage = findViewById(R.id.arrowImage);
        arrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: Arrow image clicked");
                openLoginActivity();
            }
        });
    }
    public void openLoginActivity() {
        Log.d(TAG, "openSignUpActivity: Opening SignUp Activity");
        Intent intent = new Intent(ForgetPassword.this, Login.class);
        startActivity(intent);
        finish();
    }
    public void viewResetClicked(View view) {
        String email = editTextEmail.getText().toString();

        if (email.isEmpty()) {
            Log.d(TAG, "onClick: Empty email");
            editTextEmail.setError("Email is required");
            return;
        }
        Log.d(TAG, "login: Attempting login for email: " + email);
        final ProgressDialog pd = new ProgressDialog(ForgetPassword.this);
        pd.setMessage("Please wait...");
        pd.show();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Personne");

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email exists, send password reset email
                    sendPasswordResetEmail(email,pd);
                } else {
                    // Email does not exist
                    showMessage("Email does not exist. Please sign up.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // An error occurred while checking email existence
                showMessage("Error checking email existence: " + databaseError.getMessage());
            }
        });
    }

    private void sendPasswordResetEmail(String email,ProgressDialog pd) {
        // Send password reset email
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password reset email sent successfully
                            pd.dismiss();
                            showMessage("Password reset email sent successfully.");
                            openLoginActivity();
                        } else {
                            // Password reset email sending failed
                            showMessage("Password reset email failed to send.");
                        }
                    }
                });
    }

    private void showMessage(String message) {
        Log.d(TAG, "showMessage: Displaying Toast Message: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}