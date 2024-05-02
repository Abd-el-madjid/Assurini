package com.example.assurini.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.assurini.Models.GrayCard;
import com.example.assurini.Models.Insurance;
import com.example.assurini.Models.InsuranceAgency;
import com.example.assurini.Models.InsuranceType;
import com.example.assurini.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InsurencePlanDetailFragment extends Fragment {

    private DatabaseReference databaseReference;
    private String agencyId, IdGrayCard;

    private static final String TAG = "InsurencePlanDetailFragm";

    private TextView companyName;
    private TextView companyType;
    private TextView evaluation;
    private ImageView companyImage;
    private LinearLayout panel_raquest;
    private RelativeLayout no_car_panel;
    private TextView car_type;
    private TextView car_model;
    private TextView price;
    private TextView date_assurance;
    private Button request_button, hide_panel_request, show_panel_request;
    private CardView doawnload_facture ;
    private CardView linear_layout_arrow;
    private StorageReference storageReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            agencyId = getArguments().getString("agencyId");
            IdGrayCard = getArguments().getString("IdGrayCard");
            Log.e("myapp", agencyId);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_insurence_plan_detail, container, false);

        doawnload_facture = view.findViewById(R.id.doawnload_facture);
        companyName = view.findViewById(R.id.company_name);
        companyType = view.findViewById(R.id.company_anssurance_type);
        evaluation = view.findViewById(R.id.evaluation);
        companyImage = view.findViewById(R.id.image_company);
        panel_raquest = view.findViewById(R.id.car_ui_base_layout_content_container);
        car_type = view.findViewById(R.id.car_type);
        car_model = view.findViewById(R.id.car_model);
        price = view.findViewById(R.id.price_initial);
        date_assurance = view.findViewById(R.id.date_assurance);
        request_button = view.findViewById(R.id.request_button);
        hide_panel_request = view.findViewById(R.id.cancel_button);
        linear_layout_arrow = view.findViewById(R.id.linear_layout_arrow);

        fetchCompanyDetail();
        fetchGrayCardDetail();


        doawnload_facture.setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Insurance");

            // Query the Insurance object with the idGrayCard
            databaseReference.orderByChild("idGrayCard").equalTo(IdGrayCard).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Insurance insurance = snapshot.getValue(Insurance.class);
                            if (insurance != null && insurance.getInsurancefacture() != null) {
                                String insuranceFactureUrl = insurance.getInsurancefacture();

                                // Open the PDF file using an intent
                                openPdfFile(insuranceFactureUrl);
                            }
                        }
                    } else {
                        // Log and display a message when no Insurance object is found
                        Log.d(TAG, "No insurance details found for the selected gray card.");
                        Toast.makeText(getContext(), "No insurance details found for the selected gray card.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Log and display a database error message
                    Log.e(TAG, "Database error occurred: " + databaseError.getMessage());
                    Toast.makeText(getContext(), "Database error occurred.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        linear_layout_arrow.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(InsurencePlanDetailFragment.this).commit();

            InsurancePlanFragment InsurancePlanFragment = new InsurancePlanFragment();
            Bundle bundle = new Bundle();
            bundle.putString("agencyId", agencyId);
            InsurancePlanFragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, InsurancePlanFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private void fetchCompanyDetail() {
        databaseReference.child("InsuranceAgency").child(agencyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InsuranceAgency agency = dataSnapshot.getValue(InsuranceAgency.class);
                if (agency != null) {
                    companyName.setText(agency.getName());
                    companyType.setText(agency.getInsuranceType());
                    evaluation.setText("25");
                    companyImage.setImageResource(R.drawable.company1);
                    fetchInsuranceTypeDetails(agency.getInsuranceType());
                } else {
                    Toast.makeText(getContext(), "Agency details not found", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Agency details not found for ID: " + agencyId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load agency details", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load agency details for ID: " + agencyId, databaseError.toException());
            }
        });
    }

    private void fetchGrayCardDetail() {
        databaseReference.child("grayCards").child(IdGrayCard).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GrayCard grayCard = dataSnapshot.getValue(GrayCard.class);
                if (grayCard != null) {
                    car_type.setText(grayCard.getType());
                    car_model.setText(grayCard.getMark());
                } else {
                    Toast.makeText(getContext(), "Gray card details not found", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Gray card details not found for ID: " + IdGrayCard);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load gray card details", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load gray card details for ID: " + IdGrayCard, databaseError.toException());
            }
        });

        // Set date of today + 1 year
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String nextYearDate = dateFormat.format(calendar.getTime());
        date_assurance.setText(nextYearDate);
    }

    private void fetchInsuranceTypeDetails(String insuranceTypeName) {
        DatabaseReference typeReference = FirebaseDatabase.getInstance().getReference("InsuranceType");
        typeReference.orderByChild("name").equalTo(insuranceTypeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InsuranceType insuranceType = snapshot.getValue(InsuranceType.class);
                    if (insuranceType != null) {
                        price.setText( insuranceType.getPrice() + "€");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(getContext(), "Failed to load insurance type details", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load insurance type details for name: " + insuranceTypeName, databaseError.toException());
            }
        });
    }

    private void openPdfFile(String insuranceFactureUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(insuranceFactureUrl));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
