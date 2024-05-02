package com.example.assurini.Assurance;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.example.assurini.Models.GrayCard;
import com.example.assurini.Models.Notification;
import com.example.assurini.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase; // <-- Added this import

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddCarFragment extends Fragment {
    private static final String TAG = "AddCarFragment";

    private View rootView;

    private TextInputEditText carteGriseNumberEditText;
    private TextInputEditText registrationNumberEditText;
    private TextInputEditText previousRegistrationNumberEditText;
    private TextInputEditText yearOfCirculationEditText;
    private TextInputEditText typeSeriesNumberEditText;
    private TextInputEditText wilayaEditText;
    private TextInputEditText communeEditText;
    private TextInputEditText carTypeEditText;
    private TextInputEditText carModelEditText;
    private TextInputEditText genderEditText;
    private TextInputEditText payloadEditText;
    private TextInputEditText grossWeightEditText;
    private TextInputEditText horsePowerEditText;
    private TextInputEditText bodyEditText;
    private TextInputEditText fuelTypeEditText;

    private HorizontalStepView stepView;
    private List<StepBean> stepsBeanList;

    private Date dateOfBirth;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    DatabaseReference notificationRef;

    private String agencyId ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            agencyId = getArguments().getString("agencyId");
            if (agencyId != null) {
                Log.e(TAG, agencyId);
            } else {
                Log.e(TAG, "Agency ID is null"); // or any other suitable message
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_car, container, false);

        mAuth = FirebaseAuth.getInstance(); // Initialize mAuth here
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");

        mDatabase = FirebaseDatabase.getInstance().getReference();


        stepView = rootView.findViewById(R.id.step_view);
        stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("Step 1", -1);
        StepBean stepBean1 = new StepBean("Step 2", -1);
        StepBean stepBean2 = new StepBean("Step 3", -1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);

        stepView
                .setStepViewTexts(stepsBeanList)
                .setTextSize(12)
                .setStepsViewIndicatorCompletedLineColor(getResources().getColor(R.color.lightMain))
                .setStepsViewIndicatorUnCompletedLineColor(getResources().getColor(R.color.whiteColor))
                .setStepViewComplectedTextColor(getResources().getColor(android.R.color.white))
                .setStepViewUnComplectedTextColor(getResources().getColor(R.color.secondaryLight))
                .setStepsViewIndicatorCompleteIcon(getResources().getDrawable(R.drawable.baseline_check_circle_outline_24))
                .setStepsViewIndicatorDefaultIcon(getResources().getDrawable(R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(getResources().getDrawable(R.drawable.attention));

        carteGriseNumberEditText = rootView.findViewById(R.id.carteGriseNumber);

        // Step 1 fields
        carteGriseNumberEditText = rootView.findViewById(R.id.carteGriseNumber);
        registrationNumberEditText = rootView.findViewById(R.id.registrationNumber);
        previousRegistrationNumberEditText = rootView.findViewById(R.id.previousRegistrationNumber);
        yearOfCirculationEditText = rootView.findViewById(R.id.yearOfCirculation);
        typeSeriesNumberEditText = rootView.findViewById(R.id.typeSeriesNumber);

        // Step 2 fields
        wilayaEditText = rootView.findViewById(R.id.wilaya);
        communeEditText = rootView.findViewById(R.id.commune);
        carTypeEditText = rootView.findViewById(R.id.carType);
        carModelEditText = rootView.findViewById(R.id.carModel);
        genderEditText = rootView.findViewById(R.id.gender);

        // Step 3 fields
        payloadEditText = rootView.findViewById(R.id.payload);
        grossWeightEditText = rootView.findViewById(R.id.grossWeight);
        horsePowerEditText = rootView.findViewById(R.id.horsePower);
        bodyEditText = rootView.findViewById(R.id.body);
        fuelTypeEditText = rootView.findViewById(R.id.fuelType);

        rootView.findViewById(R.id.nextButton).setOnClickListener(v -> onNextClicked());
        rootView.findViewById(R.id.prevButton).setOnClickListener(v -> onBackClicked());

        showStep(0);
        yearOfCirculationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYearPickerDialog();
            }
        });

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
        return rootView;
    }

    private void onNextClicked() {
        int currentStepPosition = getCurrentStepPosition();

        if (currentStepPosition == 0) {
            if (validateStep1()) {
                stepsBeanList.get(0).setState(1);
                showStep(currentStepPosition + 1);
            } else {
                stepsBeanList.get(0).setState(0);
            }
        } else if (currentStepPosition == 1) {
            if (validateStep2()) {
                stepsBeanList.get(1).setState(1);
                showStep(currentStepPosition + 1);
            } else {
                stepsBeanList.get(1).setState(0);
            }
        } else if (currentStepPosition == 2) {
            if (validateStep3()) {
                stepsBeanList.get(2).setState(1);
                ((TextInputEditText) rootView.findViewById(R.id.nextButton)).setText("ADD");
                rootView.findViewById(R.id.nextButton).setOnClickListener(v -> onAddClicked());
            } else {
                stepsBeanList.get(2).setState(0);
            }
        }

        stepView.setStepViewTexts(stepsBeanList);
    }

    private void onAddClicked() {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Please wait...");
        pd.show();

        GrayCard grayCard = new GrayCard();
        grayCard.setWilaya(wilayaEditText.getText().toString());
        grayCard.setCommune(communeEditText.getText().toString());
        grayCard.setRegistrationNumber(Integer.parseInt(registrationNumberEditText.getText().toString()));
        grayCard.setPreviousNumber(Integer.parseInt(previousRegistrationNumberEditText.getText().toString()));
        grayCard.setYearOfFirstCirculation(Integer.parseInt(yearOfCirculationEditText.getText().toString()));
        grayCard.setNumberInTheTypeSeries(typeSeriesNumberEditText.getText().toString());
        grayCard.setType(carTypeEditText.getText().toString());
        grayCard.setMark(carModelEditText.getText().toString());
        grayCard.setGender(genderEditText.getText().toString());
        grayCard.setPayload(Integer.parseInt(payloadEditText.getText().toString()));
        grayCard.setGrossWeight(Integer.parseInt(grossWeightEditText.getText().toString()));
        grayCard.setPower(Integer.parseInt(horsePowerEditText.getText().toString()));
        grayCard.setBody(bodyEditText.getText().toString());
        grayCard.setFuelType(fuelTypeEditText.getText().toString());

        String userId = mAuth.getCurrentUser().getUid();
        if (userId != null){
            grayCard.setPersonCar(userId);
        }

        DatabaseReference grayCardsRef = mDatabase.child("grayCards"); // Updated Firebase structure
        String idGrayCard = carteGriseNumberEditText.getText().toString();
        grayCard.setIdGrayCard(Long.parseLong(idGrayCard));

        grayCardsRef.child(idGrayCard).setValue(grayCard)
                .addOnSuccessListener(aVoid -> {
                    pd.dismiss();
                    showMessage("Gray card added successfully");
                    String notificationId = notificationRef.push().getKey();
                    String title = getContext().getString(R.string.new_car_title);
                    String content = getContext().getString(R.string.new_car);

                    String personId = mAuth.getCurrentUser().getUid();
                    Date currentDate = new Date();

                    // Create a Notification object
                    Notification newNotification = new Notification(title, content, personId, currentDate);
                    notificationRef.child(notificationId).setValue(newNotification);

                    clearFields();
                    Log.d(TAG, "Gray card added successfully: " + grayCard.toString());

                    if (agencyId !=null){
                        // Get the FragmentManager
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                        // Remove the current fragment
                        fragmentManager.beginTransaction().remove(AddCarFragment.this).commit();

                        // Remove current fragment from the back stack

                        CarListFragment  CarListFragment = new CarListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("agencyId", agencyId);
                        CarListFragment.setArguments(bundle);

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, CarListFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    }
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    showMessage("Error adding gray card: " + e.getMessage());
                    Log.e(TAG, "Error adding gray card: " + e.getMessage());
                });
    }

    private void onBackClicked() {
        if (getCurrentStepPosition() != 0) {
            showStep(getCurrentStepPosition() - 1);
        }
    }


    private boolean validateStep1() {
        if (carteGriseNumberEditText.getText().toString().isEmpty()) {
            carteGriseNumberEditText.setError("Carte Grise number is required");
            return false;
        }
        if (registrationNumberEditText.getText().toString().isEmpty()) {
            registrationNumberEditText.setError("Registration number is required");
            return false;
        }
        if (previousRegistrationNumberEditText.getText().toString().isEmpty()) {
            previousRegistrationNumberEditText.setError("Previous registration number is required");
            return false;
        }
        if (yearOfCirculationEditText.getText().toString().isEmpty()) {
            yearOfCirculationEditText.setError("Year of circulation is required");
            return false;
        }
        if (typeSeriesNumberEditText.getText().toString().isEmpty()) {
            typeSeriesNumberEditText.setError("Type series number is required");
            return false;
        }
        return true;
    }

    private boolean validateStep2() {
        if (wilayaEditText.getText().toString().isEmpty()) {
            wilayaEditText.setError("Wilaya is required");
            return false;
        }
        if (communeEditText.getText().toString().isEmpty()) {
            communeEditText.setError("Commune is required");
            return false;
        }
        if (carTypeEditText.getText().toString().isEmpty()) {
            carTypeEditText.setError("Car type is required");
            return false;
        }
        if (carModelEditText.getText().toString().isEmpty()) {
            carModelEditText.setError("Car model is required");
            return false;
        }
        if (genderEditText.getText().toString().isEmpty()) {
            genderEditText.setError("Gender is required");
            return false;
        }
        return true;
    }

    private boolean validateStep3() {
        if (payloadEditText.getText().toString().isEmpty()) {
            payloadEditText.setError("Payload is required");
            return false;
        }
        if (grossWeightEditText.getText().toString().isEmpty()) {
            grossWeightEditText.setError("Gross weight is required");
            return false;
        }
        if (horsePowerEditText.getText().toString().isEmpty()) {
            horsePowerEditText.setError("Horse power is required");
            return false;
        }
        if (bodyEditText.getText().toString().isEmpty()) {
            bodyEditText.setError("Body is required");
            return false;
        }
        if (fuelTypeEditText.getText().toString().isEmpty()) {
            fuelTypeEditText.setError("Fuel type is required");
            return false;
        }
        return true;
    }

    private void showStep(int step) {
        rootView.findViewById(R.id.step1Layout).setVisibility(step == 0 ? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.step2Layout).setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.step3Layout).setVisibility(step == 2 ? View.VISIBLE : View.GONE);

        if (step == 0) {
            rootView.findViewById(R.id.prevButton).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.prevButton).setVisibility(View.VISIBLE);
        }

        if (step == 2) {
            ((Button) rootView.findViewById(R.id.nextButton)).setText("ADD");
            rootView.findViewById(R.id.nextButton).setOnClickListener(v -> onAddClicked());
        } else {
            ((Button) rootView.findViewById(R.id.nextButton)).setText("Next");
            rootView.findViewById(R.id.nextButton).setOnClickListener(v -> onNextClicked());
        }

        stepView.setStepViewTexts(stepsBeanList);
    }

    private int getCurrentStepPosition() {
        View step1Layout = rootView.findViewById(R.id.step1Layout);
        View step2Layout = rootView.findViewById(R.id.step2Layout);
        View step3Layout = rootView.findViewById(R.id.step3Layout);

        if (step1Layout.getVisibility() == View.VISIBLE) {
            return 0;
        } else if (step2Layout.getVisibility() == View.VISIBLE) {
            return 1;
        } else if (step3Layout.getVisibility() == View.VISIBLE) {
            return 2;
        }
        return 0;
    }

    private void showMessage(String message) {
        Log.d(TAG, "showMessage: Displaying Toast Message: " + message);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showYearPickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int month, int dayOfMonth) {
                        // Update the EditText with the chosen year
                        String selectedDate = String.valueOf(selectedYear);
                        yearOfCirculationEditText.setText(selectedDate);
                    }
                },
                year,
                0, // January (since we're only selecting a year)
                1  // First day of the month
        );

        datePickerDialog.getDatePicker().init(year, 0, 1, null); // Initialize date picker to show year
        datePickerDialog.getDatePicker().setCalendarViewShown(false); // Hide the calendar view
        datePickerDialog.getDatePicker().setSpinnersShown(true); // Show spinners

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
    private void clearFields() {
        carteGriseNumberEditText.setText("");
        registrationNumberEditText.setText("");
        previousRegistrationNumberEditText.setText("");
        yearOfCirculationEditText.setText("");
        typeSeriesNumberEditText.setText("");
        wilayaEditText.setText("");
        communeEditText.setText("");
        carTypeEditText.setText("");
        carModelEditText.setText("");
        genderEditText.setText("");
        payloadEditText.setText("");
        grossWeightEditText.setText("");
        horsePowerEditText.setText("");
        bodyEditText.setText("");
        fuelTypeEditText.setText("");

        // Reset the state of all steps to -1
        for (StepBean stepBean : stepsBeanList) {
            stepBean.setState(-1);
        }

        stepView.setStepViewTexts(stepsBeanList);

        // Go back to step 1
        showStep(0);
    }

}
