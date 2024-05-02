package com.example.assurini.UI.Authentification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assurini.MainActivity;
import com.example.assurini.Models.Personne;
import com.example.assurini.UI.Authentification.Login;

import com.example.assurini.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    private DatabaseReference referencePer;

    private TextView loginTextView;
    private ProgressDialog pd;

    private ImageView arrowImage;
    private EditText editTextEmail, editTextPassword;
    private Button buttonSignup;
    private ImageButton eyePassword;
    private boolean isPasswordVisible = false;
    private Date dateOfBirth;


    private FirebaseAuth auth;
    private Button buttonSignUp;

    private EditText fullNameEditText, lastNameEditText, nationalNumberEditText,
            dateOfBirthEditText, placeOfBirthEditText, numTelephoneEditText, nationalityEditText, usernameEditText,
            emailEditText, passwordEditText, confirmPasswordEditText,dateEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.d(TAG, "onCreate: Started");


        loginTextView = findViewById(R.id.loginTextview);
        arrowImage = findViewById(R.id.arrowImage);
        eyePassword = findViewById(R.id.eye_Password);
        passwordEditText = findViewById(R.id.editTextPassword);
        emailEditText = findViewById(R.id.editTextEmail);

        eyePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Eye password clicked");
                togglePasswordVisibility(passwordEditText, isPasswordVisible, eyePassword);
                isPasswordVisible = !isPasswordVisible;
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Signup text clicked");
                openLoginActivity();
            }
        });


        arrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Arrow image clicked");
                openMain();
            }
        });


// user registeration

        buttonSignUp = findViewById(R.id.SignUpButton);

        fullNameEditText = findViewById(R.id.fullname);
        nationalNumberEditText = findViewById(R.id.nationalnumber);
        dateOfBirthEditText = findViewById(R.id.dateEditText);
        placeOfBirthEditText = findViewById(R.id.placeofbirth);
        numTelephoneEditText = findViewById(R.id.numTelephone);


// date
        dateEditText = findViewById(R.id.dateEditText);
        auth = FirebaseAuth.getInstance();

        String dateOfBirthString = "04/12/2002";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dateOfBirth = null;

        try {
            dateOfBirth = sdf.parse(dateOfBirthString);
        } catch (ParseException e) {
            e.printStackTrace();
            showMessage("Erreur: Invalid date format. Please enter the date in the format dd/MM/yyyy");
        }
        String dateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        Date dateTime = null;

        try {
            dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            showMessage("Error creating user");
        }
        // main registre



        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFieldsNotEmpty()) {
                    pd = new ProgressDialog(SignUp.this);
                    pd.setMessage("Please wait...");
                    pd.show();

                    long phoneNumber = Long.parseLong(numTelephoneEditText.getText().toString().trim());
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String fulltName = fullNameEditText.getText().toString().trim();
                    long nationalNumber = Long.parseLong(nationalNumberEditText.getText().toString().trim());
                    String dateOfBirthString = dateOfBirthEditText.getText().toString().trim();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    dateOfBirth = null;

                    try {
                        dateOfBirth = sdf.parse(dateOfBirthString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        showMessage("Erreur: Invalid date format. Please enter the date in the format dd/MM/yyyy");
                    }

                    String placeOfBirth = placeOfBirthEditText.getText().toString().trim();
                    if (checkPhoneNumber(numTelephoneEditText.getText().toString().trim())) {
                        if (isValidEmail(email)) {
                            checkEmail(email, new SignUp.EmailCheckCallback() {
                                @Override
                                public void onEmailCheckComplete(boolean isUniqueEmail) {
                                    if (isUniqueEmail) {
                                        if (password.length() >= 6) {



                                                            String dateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
                                                            Date dateTime = null;

                                                            try {
                                                                dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateTimeString);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                                showMessage("Error creating user");
                                                            }

                                                            Personne personne = new Personne(

                                                                    /* fullnom */ fulltName,
                                                                    /* dateNaissance */ dateOfBirth,
                                                                    /* lieuNaissance */ placeOfBirth,
                                                                    /* email */ email,
                                                                    /* nationalNumber */ nationalNumber,
                                                                    /* numTelephone */ phoneNumber,
                                                                    /* motPasse */ password,
                                                                    /* lastLogin */ null,
                                                                    /* dateCreation */ dateTime
                                                            );
                                                            register(personne,  email, password);
                                             }else {
                                            pd.dismiss();
                                            passwordEditText.setError("Password too short!");

                                        }

                                    } else {
                                        pd.dismiss();
                                        emailEditText.setError("Erreur: Email is not unique. Please choose a different Email.");

                                    }
                                }


                            });
                        }else {
                            pd.dismiss();
                            emailEditText.setError("Please enter a valid email address");
                        }

                    }else {
                        pd.dismiss();
                        numTelephoneEditText.setError("Invalid phone number. Please enter a 10-digit number starting with 0.");

                    }


                } else {
                    showMessage(getEmptyFieldsMessage());
                }

            }
        });







    }

    private void register(Personne personne, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, task -> {
            pd.dismiss();
            if (task.isSuccessful()) {
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (task.isSuccessful()) {
                                    // Get new FCM registration token
                                    String token = task.getResult();
                                    personne.setToken(token);
                                    Log.d("MyTag", "MYAPP   "+ token);

                                    FirebaseUser firebaseUser = auth.getCurrentUser();

                                        String uid = firebaseUser.getUid();
                                        Log.d("UID", "User UID: " + uid);


                                    referencePer = FirebaseDatabase.getInstance().getReference().child("Personne").child(uid);

                                    referencePer.setValue(personne).addOnCompleteListener(innerTask -> {
                                        if (innerTask.isSuccessful()) {


                                            sendVerificationEmail();
                                            showMessage("User created. Please check your email for verification.");

                                            Intent intent = new Intent(SignUp.this, Login.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            showMessage("Failed to write user data to the database: " + innerTask.getException().getMessage());
                                        }
                                    });


                                }else {
                                    Log.w("MyTag", "Fetching FCM registration token failed", task.getException());
                                    return;

                                }


                            }
                        });



            } else {
                showMessage("You can't register with this email and password: " + task.getException().getMessage());
            }
        });
    }
    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showMessage("Verification email sent to " + user.getEmail());
                            } else {
                                showMessage("Failed to send verification email: " + task.getException().getMessage());
                            }
                        }
                    });
        }
    }



    public void openLoginActivity() {
        Log.d(TAG, "openSignUpActivity: Opening SignUp Activity");
        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
    }



    public void openMain() {
        Log.d(TAG, "openMain: Opening Main Activity");
        Intent intent = new Intent(SignUp.this, MainActivity.class);
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
    public void showDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the EditText with the chosen date
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateEditText.setText(selectedDate);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }
    // Method to convert a date string to a Date object
    private Date convertStringToDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle the exception as needed
        }
    }






    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }
    private boolean checkFieldsNotEmpty() {
        return !isEditTextEmpty(fullNameEditText) &&
                !isEditTextEmpty(nationalNumberEditText) &&
                !isEditTextEmpty(dateOfBirthEditText) &&
                !isEditTextEmpty(placeOfBirthEditText) &&
                !isEditTextEmpty(numTelephoneEditText) &&
                !isEditTextEmpty(emailEditText) &&
                !isEditTextEmpty(passwordEditText);
    }


    private String getEmptyFieldsMessage() {
        StringBuilder emptyFieldsMessage = new StringBuilder("Empty fields:");

        int emptyCount = 0;

        if (isEditTextEmpty(fullNameEditText)) {
            fullNameEditText.setError("First Name required");
            emptyCount++;
        }



        if (isEditTextEmpty(nationalNumberEditText)) {
            nationalNumberEditText.setError("National Numbe required");
            emptyCount++;
        }

        if (isEditTextEmpty(dateOfBirthEditText)) {
            dateOfBirthEditText.setError("Date Of Birth  required");

            emptyCount++;
        }

        if (isEditTextEmpty(placeOfBirthEditText)) {
            placeOfBirthEditText.setError("Place Of Birth required");

            emptyCount++;
        }
        if (isEditTextEmpty(numTelephoneEditText)) {
            numTelephoneEditText.setError("Phone number required");
            emptyCount++;
        }

        if (isEditTextEmpty(emailEditText)) {
            emailEditText.setError("Email required");
            emptyCount++;
        }

        if (isEditTextEmpty(passwordEditText)) {
            passwordEditText.setError("Password required");
            emptyCount++;
        }
        // Show the message only if there are 2 or fewer empty fields
        if (emptyCount <= 2) {
            return "Empty fields, please fill in the required information.";
        } else {
            return " ";
        }
    }

    private boolean checkPhoneNumber( String phoneNumber) {
        boolean t = false;
        if (phoneNumber.length() == 10 && phoneNumber.startsWith("0")) {

            t=true;
        } else {
            // Invalid phone number
            Log.d("MyTag", "Invalid phone number. Please enter a 10-digit number starting with 0.");

        }
        return t ;
    }
    private boolean isValidEmail(String email) {
        boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isValid) {
            Log.d("MyTag", "Please enter a valid email address");
        }
        return isValid;
    }

    public interface EmailCheckCallback {
        void onEmailCheckComplete(boolean isUnique);
    }
    private void checkEmail(String email, SignUp.EmailCheckCallback callback) {
        DatabaseReference personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");

        personneRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUnique = !dataSnapshot.exists();
                callback.onEmailCheckComplete(isUnique);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("MyTag", "Error checking Email uniqueness: " + databaseError.getMessage());

                callback.onEmailCheckComplete(false); // Assume not unique in case of error
            }
        });
    }

}