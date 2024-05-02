package com.example.assurini.Adapter;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.assurini.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddCarView extends RelativeLayout {

    private TextInputEditText fullnameEditText, nationalNumberEditText, dateEditText, placeOfBirthEditText, telephoneNumberEditText, emailAddressEditText;

    public AddCarView(Context context) {
        super(context);
        init(context);
    }

    public AddCarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddCarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_add_car, this, true);

        fullnameEditText = view.findViewById(R.id.fullname);
        nationalNumberEditText = view.findViewById(R.id.nationalnumber);
        dateEditText = view.findViewById(R.id.dateEditText);
        placeOfBirthEditText = view.findViewById(R.id.placeofbirth);
        telephoneNumberEditText = view.findViewById(R.id.numTelephone);
        emailAddressEditText = view.findViewById(R.id.editTextEmail);
    }

    // Getter methods for the form fields
    public String getFullname() {
        return fullnameEditText.getText().toString();
    }

    public String getNationalNumber() {
        return nationalNumberEditText.getText().toString();
    }

    public String getDate() {
        return dateEditText.getText().toString();
    }

    public String getPlaceOfBirth() {
        return placeOfBirthEditText.getText().toString();
    }

    public String getTelephoneNumber() {
        return telephoneNumberEditText.getText().toString();
    }

    public String getEmailAddress() {
        return emailAddressEditText.getText().toString();
    }
}
