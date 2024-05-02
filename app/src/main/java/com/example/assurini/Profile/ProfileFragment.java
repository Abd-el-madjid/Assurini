package com.example.assurini.Profile;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.assurini.Assurance.CarListFragment;
import com.example.assurini.Assurance.CompanyDetailFragment;
import com.example.assurini.MainActivity;
import com.example.assurini.Models.Notification;
import com.example.assurini.Models.Personne;
import com.example.assurini.R;
import com.example.assurini.UI.Authentification.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ProfileFragment extends Fragment {
    private static final String PREF_SELECTED_LANGUAGE = "selected_language";
    DatabaseReference notificationRef;


    private FirebaseAuth auth;
    private DatabaseReference personneRef;
    private FirebaseUser currentUser;

    private RelativeLayout personnelLayout,passwordLayout,logoutlayout,languageLayout,ResourceLayout,SupportLayout,modeLayout;
    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneNumberEditText,c_newpassowrd,newpassword,oldpassword;
    private Button validchangeinfo;
    private String userId;
    private TextView fullname ;
    private ImageButton eyeOldPassword, eyeNewPassword,eyeValidNewPassword;
    private boolean isOldPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isValidNewPasswordVisible = false;
    private String nom,prenom,email,phonenum,password_db,password_dbdchekc;
    private  Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();

        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");



        // Apply the selected language to the app


        personnelLayout = view.findViewById(R.id.personnelLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        languageLayout = view.findViewById(R.id.languageLayout);
        logoutlayout = view.findViewById(R.id.logoutLayout);
        SupportLayout = view.findViewById(R.id.SupportLayout);
        modeLayout = view.findViewById(R.id.modeLayout);
        // Initialize EditTexts



        auth = FirebaseAuth.getInstance();
        personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
        currentUser = auth.getCurrentUser();
        fullname = view.findViewById(R.id.fullname);

        if(currentUser != null){
            String uid = currentUser.getUid();

            personneRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        // User exists, you can now get the user data
                        String fullnom = dataSnapshot.child("fullnom").getValue(String.class);
                        fullname.setText(fullnom);
                    } else {
                        fullname.setText("no user found");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.w("MyApp", "Failed to read value.", databaseError.toException());
                }
            });
        }









        SupportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Remove the current fragment
                fragmentManager.beginTransaction().remove(ProfileFragment.this).commit();


                InsurancePlanFragment InsurancePlanFragment = new InsurancePlanFragment();
                Bundle bundle = new Bundle();


                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, InsurancePlanFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        passwordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialogpassword();
            }
        });

        logoutlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialoglogout();
            }
        });

        personnelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialog();
            }
        });


        return view;
    }
    private void showDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        final Button valid = dialog.findViewById(R.id.validationPasswordbutton);

        // Initialize EditText fields
        final EditText fullNameEditText = dialog.findViewById(R.id.fullname);
        final EditText nationalNumberEditText = dialog.findViewById(R.id.nationalnumber);
        final EditText phoneNumberEditText = dialog.findViewById(R.id.numTelephone);
        final EditText emailEditText = dialog.findViewById(R.id.editTextEmail);

        final ProgressDialog pd = new ProgressDialog(requireContext());
        pd.setMessage("Please wait...");
        pd.setCancelable(false);

        userId = auth.getUid();
        // Fetch user data from Firebase Realtime Database based on UID
        DatabaseReference personneToUpdateRef = personneRef.child(userId);
        personneToUpdateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user data
                    String fullName = dataSnapshot.child("fullnom").getValue(String.class);
                    Long nationalNumber = dataSnapshot.child("nationalNumber").getValue(Long.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    // Set retrieved data to EditText fields
                    fullNameEditText.setText(fullName);
                    nationalNumberEditText.setText(String.valueOf(nationalNumber));
                    phoneNumberEditText.setText(String.valueOf(nationalNumber)); // same as national number
                    emailEditText.setText(email);
                } else {
                    // Handle the case where user data doesn't exist
                    Toast.makeText(getContext(), "User information not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors in data retrieval
                Toast.makeText(getContext(), "Error fetching user information", Toast.LENGTH_SHORT).show();
                Log.e("MyTag", "Error fetching user information", databaseError.toException());
            }
        });

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = fullNameEditText.getText().toString();
                final String nationalNumber = nationalNumberEditText.getText().toString();
                final String phoneNumber = phoneNumberEditText.getText().toString(); // same as national number
                final String email = emailEditText.getText().toString();

                // Check if any of the fields are empty
                if (fullName.isEmpty()) {
                    fullNameEditText.setError("Full name is required");
                    return;
                }

                if (nationalNumber.isEmpty()) {
                    nationalNumberEditText.setError("National number is required");
                    return;
                }

                if (phoneNumber.isEmpty()) {
                    phoneNumberEditText.setError("Phone number is required");
                    return;
                }

                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    return;
                }

                pd.show();

                boolean changesMade = false;

                DatabaseReference personneToUpdateRef = personneRef.child(userId);
                personneToUpdateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String currentFullName = dataSnapshot.child("fullnom").getValue(String.class);
                            Long currentNationalNumber = dataSnapshot.child("nationalNumber").getValue(Long.class);
                            String currentEmail = dataSnapshot.child("email").getValue(String.class);

                            if (!fullName.equals(currentFullName)) {
                                personneToUpdateRef.child("fullnom").setValue(fullName);
                                String notificationId = notificationRef.push().getKey();
                                String title = context.getString(R.string.notification_title_update_firstname);
                                String content = context.getString(R.string.notification_update_firstname);
                                String personId = userId;
                                Date currentDate = new Date();

                                // Create a Notification object
                                Notification newNotification = new Notification(title, content, personId, currentDate);
                                notificationRef.child(notificationId).setValue(newNotification);

                            }

                            if (!nationalNumber.equals(String.valueOf(currentNationalNumber))) {
                                personneToUpdateRef.child("nationalNumber").setValue(Long.parseLong(nationalNumber));

                            }

                            if (!phoneNumber.equals(String.valueOf(currentNationalNumber))) {
                                personneToUpdateRef.child("numTelephone").setValue(Long.parseLong(phoneNumber));
                                String notificationId = notificationRef.push().getKey();
                                String title = context.getString(R.string.notification_title_change_phone);
                                String content = context.getString(R.string.notification_change_phone);
                                String personId = userId;
                                Date currentDate = new Date();

                                // Create a Notification object
                                Notification newNotification = new Notification(title, content, personId, currentDate);
                                notificationRef.child(notificationId).setValue(newNotification);

                            }

                            if (!email.equals(currentEmail)) {
                                personneRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists() && !email.equals(currentEmail)) {
                                            // Email already exists and is not the user's current email
                                            emailEditText.setError("Email is not unique");
                                            pd.dismiss();
                                        } else {
                                            personneToUpdateRef.child("email").setValue(email);

                                            // Update email in authenticated Firebase user
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user != null) {
                                                user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("MyTag", "User email address updated: " + user.getEmail());
                                                            String notificationId = notificationRef.push().getKey();
                                                            String title = context.getString(R.string.notification_title_change_email);
                                                            String content = context.getString(R.string.notification_change_email);
                                                            String personId = userId;
                                                            Date currentDate = new Date();

                                                            // Create a Notification object
                                                            Notification newNotification = new Notification(title, content, personId, currentDate);
                                                            notificationRef.child(notificationId).setValue(newNotification);

                                                            // Send email verification
                                                            sendVerificationEmail(user);
                                                        } else {
                                                            Log.d("MyTag", "Error updating user email address: " + task.getException().getMessage());
                                                            pd.dismiss();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.d("MyTag", "FirebaseUser is null");
                                                pd.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle errors in data retrieval
                                        Toast.makeText(getContext(), "Error checking email uniqueness", Toast.LENGTH_SHORT).show();
                                        Log.e("MyTag", "Error checking email uniqueness", databaseError.toException());
                                        pd.dismiss();
                                    }
                                });
                            }


                                pd.dismiss();
                                dialog.dismiss();


                            TextView usernameHint = getView().findViewById(R.id.fullname);
                            usernameHint.setText(fullName);
                        } else {
                            // Handle the case where user data doesn't exist
                            Toast.makeText(getContext(), "User information not found", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors in data retrieval
                        Toast.makeText(getContext(), "Error fetching user information", Toast.LENGTH_SHORT).show();
                        Log.e("MyTag", "Error fetching user information", databaseError.toException());
                        pd.dismiss();
                    }
                });
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("MyTag", "Verification email sent to " + user.getEmail());
                            // You can display a message to the user informing them to check their email for verification
                        } else {
                            Log.e("MyTag", "Error sending verification email", task.getException());
                            // You can handle the error here, e.g., show an error message to the user
                        }
                    }
                });
    }
    private void showDialoglogout() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.centrelogout);


        Button logout = dialog.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        // Logout the user


                        currentUser = auth.getCurrentUser();

                        personneRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Personne personne = dataSnapshot.getValue(Personne.class);

                                    userId = dataSnapshot.getKey();
                                    DatabaseReference personneToUpdateRef = personneRef.child(userId);

                                    // Update lastLogin

                                    // Update isactive
                                    updateIsactive(userId, personneToUpdateRef);
                                    String notificationId = notificationRef.push().getKey();
                                    String title = context.getString(R.string.notification_title_logout);
                                    String content = context.getString(R.string.notification_logout);
                                    String personId = userId;
                                    Date currentDate = new Date();

                                    // Create a Notification object
                                    Notification newNotification = new Notification(title, content, personId, currentDate);
                                    notificationRef.child(notificationId).setValue(newNotification);

                                    FirebaseAuth.getInstance().signOut();

                                    // Redirect the user to the login screen or perform any other action after logout
                                    startActivity(new Intent(requireContext(), Login.class));
                                    getActivity().finish(); // Finish the current activity if needed
                                    dialog.dismiss();
                                    dialog2.dismiss();
                                } else {
                                    Toast.makeText(getContext(), "User information not found", Toast.LENGTH_SHORT).show();
                                    Log.d("MyTag", "User Information Not Found for UID: " + currentUser.getUid());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), "Error fetching user information", Toast.LENGTH_SHORT).show();
                                Log.e("MyTag", "Error fetching user information", databaseError.toException());
                            }
                        });

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        dialog.dismiss();
                        dialog2.dismiss();
                    }
                });

                AlertDialog dialog2= builder.create();
                dialog2.show();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Add any other necessary setup for your dialog
    }

    private void showDialogpassword() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottompasswordchange);

        eyeOldPassword = dialog.findViewById(R.id.eye_oldPassword);
        eyeNewPassword = dialog.findViewById(R.id.eye_newPassword);
        eyeValidNewPassword = dialog.findViewById(R.id.eye_valid_newPassword);

        oldpassword = dialog.findViewById(R.id.old_password);
        newpassword = dialog.findViewById(R.id.new_password);
        c_newpassowrd = dialog.findViewById(R.id.validation_new_password);

        eyeOldPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(oldpassword, isOldPasswordVisible, eyeOldPassword);
                isOldPasswordVisible = !isOldPasswordVisible;
            }
        });

        eyeNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(newpassword, isNewPasswordVisible, eyeNewPassword);
                isNewPasswordVisible = !isNewPasswordVisible;
            }
        });

        eyeValidNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(c_newpassowrd, isValidNewPasswordVisible, eyeValidNewPassword);
                isValidNewPasswordVisible = !isValidNewPasswordVisible;
            }
        });

        Button validp = dialog.findViewById(R.id.validationPasswordbutton);
        validp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_password = oldpassword.getText().toString();
                String new_password = newpassword.getText().toString();
                String c_new_password = c_newpassowrd.getText().toString();

                // Check if any of the fields are empty
                if (old_password.isEmpty()) {
                    oldpassword.setError("old password is required");
                    Log.d("PasswordChange", "Old password is empty");
                    return;
                }

                if (new_password.isEmpty()) {
                    newpassword.setError("new password is required");
                    Log.d("PasswordChange", "New password is empty");
                    return;
                }

                if (c_new_password.isEmpty()) {
                    c_newpassowrd.setError("validate your new password first");
                    Log.d("PasswordChange", "Valid new password is empty");
                    return;
                }

                if (!c_new_password.equals(new_password)) {
                    c_newpassowrd.setError("Passwords do not match");
                    Log.d("PasswordChange", "Passwords do not match");
                    return;
                }

                // Re-authenticate user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), old_password);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> reauthTask) {
                                    if (reauthTask.isSuccessful()) {
                                        // Update password
                                        final ProgressDialog pd = new ProgressDialog(ProfileFragment.this.getContext());
                                        pd.setMessage("Please wait...");
                                        pd.show();

                                        user.updatePassword(new_password)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        pd.dismiss();
                                                        if (task.isSuccessful()) {
                                                            String notificationId = notificationRef.push().getKey();
                                                            String title = context.getString(R.string.notification_title_update_password);
                                                            String content = context.getString(R.string.notification_update_password);
                                                            Date currentDate = new Date();
                                                            // Create a Notification object
                                                            Notification newNotification = new Notification(title, content, user.getUid(), currentDate);
                                                            notificationRef.child(notificationId).setValue(newNotification);

                                                            showMessage("Password changed successfully");
                                                            dialog.dismiss();
                                                            Log.d("PasswordChange", "Password changed successfully");
                                                        } else {
                                                            showMessage("Error changing password: " + task.getException().getMessage());
                                                            Log.d("PasswordChange", "Error changing password: " + task.getException().getMessage());
                                                        }
                                                    }
                                                });
                                    } else {
                                        oldpassword.setError("Wrong password");
                                        showMessage("Error re-authenticating user: " + reauthTask.getException().getMessage());
                                        Log.d("PasswordChange", "Error re-authenticating user: " + reauthTask.getException().getMessage());
                                    }
                                }
                            });
                } else {
                    showMessage("No user is currently logged in");
                    Log.d("PasswordChange", "No user is currently logged in");
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    private void togglePasswordVisibility(EditText editText, boolean isVisible,ImageButton k) {
        if (editText == null) {
            // Log an error or handle it appropriately
            return;
        }

        if (isVisible) {
            // Hide password
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            k.setImageResource(R.drawable.eye_closed); // Change to your closed eye icon
        } else {
            // Show password
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            k.setImageResource(R.drawable.eye); // Change to your open eye icon
        }

        // Move cursor to the end of the text
        editText.setSelection(editText.getText().length());
    }



    private void refreshFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }


    // Function to send email verification


    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    private String combineInitials(String nom, String prenom) {
        // Check if nom and prenom are not null or empty
        if (nom != null && !nom.isEmpty() && prenom != null && !prenom.isEmpty()) {
            // Extract the first letter from nom and prenom
            char nomInitial = nom.charAt(0);
            char prenomInitial = prenom.charAt(0);

            // Combine the initials into a single string
            return String.valueOf(nomInitial) + String.valueOf(prenomInitial);
        } else {
            // Handle the case where nom or prenom is null or empty
            return "";
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
