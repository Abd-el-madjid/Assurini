package com.example.assurini.Assurance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.assurini.Models.InsuranceAgency;
import com.example.assurini.Models.InsuranceType;
import com.example.assurini.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CompanyDetailFragment extends Fragment {

    private DatabaseReference databaseReference;
    private String agencyId;

    private TextView companyName;
    private TextView companyType;
    private TextView price;

    private TextView evaluation;
    private ImageView companyImage;
    private CardView linear_layout_arrow ;
    private Button show_panel_request ,hide_panel_request,request_button;
    private LinearLayout panel_raquest ;

    private static final String TAG = "CompanyDetailFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            agencyId = getArguments().getString("agencyId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_detail, container, false);

        request_button =  view.findViewById(R.id.request_button);
        hide_panel_request =  view.findViewById(R.id.cancel_button);
        panel_raquest = view.findViewById(R.id.car_ui_base_layout_content_container);
        show_panel_request = view.findViewById(R.id.show_panel_request);
        linear_layout_arrow = view.findViewById(R.id.linear_layout_arrow);
        companyName = view.findViewById(R.id.company_name);
        companyType = view.findViewById(R.id.company_anssurance_type);
        price = view.findViewById(R.id.price_initial);

        evaluation = view.findViewById(R.id.evaluation);
        companyImage = view.findViewById(R.id.image_company);

        databaseReference = FirebaseDatabase.getInstance().getReference("InsuranceAgency");

        if (agencyId != null){
            fetchCompanyDetail();
        }

        show_panel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel_raquest.setVisibility(View.VISIBLE);
            }
        });
        hide_panel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel_raquest.setVisibility(View.GONE);
            }
        });
        linear_layout_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Remove the current fragment
                fragmentManager.beginTransaction().remove(CompanyDetailFragment.this).commit();

                // Remove current fragment from the back stack

                CompanyFragment  CompanyFragment = new CompanyFragment();
                Bundle bundle = new Bundle();
                bundle.putString("agencyId", agencyId);
                CompanyFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, CompanyFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                // Optional: If you want to pop the fragment from the back stack
                // fragmentManager.popBackStack();
            }
        });
        request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Remove the current fragment
                fragmentManager.beginTransaction().remove(CompanyDetailFragment.this).commit();


                CarListFragment CarListFragment = new CarListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("agencyId", agencyId);
                CarListFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, CarListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void fetchCompanyDetail() {
        databaseReference.child(agencyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InsuranceAgency agency = dataSnapshot.getValue(InsuranceAgency.class);
                if (agency != null) {
                    companyName.setText(agency.getName());
                    companyType.setText(agency.getInsuranceType());
                    evaluation.setText("25"); // Sample evaluation text, you can fetch the actual value if available
                    companyImage.setImageResource(R.drawable.company1);
                    fetchInsuranceTypeDetails(agency.getInsuranceType());// Sample image, you can fetch the actual image if available
                } else {
                    Toast.makeText(getContext(), "Agency details not found", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Agency details not found for ID: " + agencyId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(getContext(), "Failed to load agency details", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load agency details for ID: " + agencyId, databaseError.toException());
            }
        });
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
}