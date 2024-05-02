package com.example.assurini.UI.Authentification;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assurini.Dashboard.Dashboard;
import com.example.assurini.MainActivity;
import com.example.assurini.Models.Notification;
import com.example.assurini.Models.Personne;
import com.example.assurini.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Login extends AppCompatActivity {
    private static final String TAG = "MyAppLogin";
    private  DatabaseReference notificationRef;

    private TextView signupTextView;
    private TextView forgetPassword;
    private ImageView arrowImage;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ImageButton eyePassword;
    private boolean isPasswordVisible = false;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Started");

        signupTextView = findViewById(R.id.signupTextview);
        forgetPassword = findViewById(R.id.forgetpassword);
        arrowImage = findViewById(R.id.arrowImage);

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Signup text clicked");
                openSignUpActivity();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Forget password clicked");
                openForgetPassword();
            }
        });

        arrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Arrow image clicked");
                openMain();
            }
        });

        eyePassword = findViewById(R.id.eye_Password);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.loginButton);

        eyePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Eye password clicked");
                togglePasswordVisibility(editTextPassword, isPasswordVisible, eyePassword);
                isPasswordVisible = !isPasswordVisible;
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Login button clicked");
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();


                if (email.isEmpty()) {
                    Log.d(TAG, "onClick: Empty email");
                    editTextEmail.setError("Email is required");
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Log.d(TAG, "onClick: Invalid email format");
                    editTextEmail.setError("Please enter a valid email address");
                } else if (password.isEmpty()) {
                    Log.d(TAG, "onClick: Empty password");
                    editTextPassword.setError("Password is required");
                } else {
                    login(email, password);
                }
            }
        });

        auth = FirebaseAuth.getInstance();
    }

    public void openSignUpActivity() {
        Log.d(TAG, "openSignUpActivity: Opening SignUp Activity");
        Intent intent = new Intent(Login.this, SignUp.class);
        startActivity(intent);
    }

    public void openForgetPassword() {
        Log.d(TAG, "openForgetPassword: Opening ForgetPassword Activity");
        Intent intent = new Intent(Login.this, ForgetPassword.class);
        startActivity(intent);
    }

    public void openMain() {
        Log.d(TAG, "openMain: Opening Main Activity");
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void togglePasswordVisibility(EditText editText, boolean isVisible, ImageButton k) {
        Log.d(TAG, "togglePasswordVisibility: Toggling password visibility");
        if (editText == null) {
            Log.e(TAG, "togglePasswordVisibility: EditText is null");
            return;
        }

        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            k.setImageResource(R.drawable.eye_closed); // Change to your closed eye icon
        } else {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            k.setImageResource(R.drawable.eye); // Change to your open eye icon
        }

        // Move cursor to the end of the text
        editText.setSelection(editText.getText().length());
    }

    private void showMessage(String message) {
        Log.d(TAG, "showMessage: Displaying Toast Message: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void login(String email, String password) {
        Log.d(TAG, "login: Attempting login for email: " + email);
        final ProgressDialog pd = new ProgressDialog(Login.this);
        pd.setMessage("Please wait...");
        pd.show();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d(TAG, "login: Invalid email format");
            showMessage("Please enter a valid email address");
            pd.dismiss();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Check if the user's email is verified
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        // Email is verified, proceed with login
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                DatabaseReference personneRef = FirebaseDatabase.getInstance().getReference("Personne");
                                String userId = user.getUid();
                                DatabaseReference personneToUpdateRef = personneRef.child(userId);

                                // Update lastLogin
                                updateLastLogin(userId, personneToUpdateRef);

                                // Update isactive
                                updateIsactive(userId, personneToUpdateRef);

                                DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notification"); // Initialize notificationRef

                                Context context = getApplicationContext();
                                String notificationId = notificationRef.push().getKey();

                                // Retrieve the string values using the resource IDs
                                String title = context.getString(R.string.new_login);
                                String content = context.getString(R.string.notification_new_login);
                                String personId = userId;
                                Date currentDate = new Date();

                                // Create a Notification object
                                Notification newNotification = new Notification(title, content, personId, currentDate);
                                newNotification.setId(notificationId); // Set the id of the Notification

                                notificationRef.child(notificationId).setValue(newNotification);

                                pd.dismiss();
                                Intent intent = new Intent(Login.this, Dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle database error
                                pd.dismiss();
                                showMessage("Database error");
                            }
                        });
                    } else {
                        // Email is not verified, show a message to the user
                        pd.dismiss();
                        showMessage("Please verify your email before logging in.");
                        // You may also want to provide a way for the user to resend the verification email.
                    }
                } else {
                    pd.dismiss();
                    showMessage("User not login: " + task.getException().getMessage());
                }
            }
        });    }

    public void updateLastLogin(String userId, DatabaseReference personneToUpdateRef) {
        // Generate the current date and time as a Date object
        String dateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        Date currentDateTime = null;

        try {
            currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the parsing exception according to your requirements
        }

        // Update the lastLogin field in the database
        if (currentDateTime != null) {
            personneToUpdateRef.child("lastLogin").setValue(currentDateTime)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Handle success, if needed
                            Log.d(TAG, "Last login for Personne with ID " + userId + " updated successfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure, if needed
                            Log.w(TAG, "Error updating last login for Personne with ID " + userId, e);
                        }
                    });
        }
    }

    public void updateIsactive(String userId, DatabaseReference personneToUpdateRef) {
        // Update isactive field
        personneToUpdateRef.child("isactive").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the current value of isactive
                Boolean currentIsactive = dataSnapshot.getValue(Boolean.class);

                // Toggle the value of isactive (true to false and vice versa)
                boolean updatedIsactive = currentIsactive != null && !currentIsactive;

                // Update the value of isactive
                personneToUpdateRef.child("isactive").setValue(updatedIsactive)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Handle success, if needed
                                Log.d("MyTag", "isactive for Personne with ID " + userId + " updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure, if needed
                                Log.w("MyTag", "Error updating isactive for Personne with ID " + userId, e);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if needed
                Log.w("MyTag", "Error reading isactive for Personne with ID " + userId, databaseError.toException());
            }
        });
    }

}
