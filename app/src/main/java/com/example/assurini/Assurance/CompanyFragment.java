package com.example.assurini.Assurance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assurini.Adapter.CompanyAdapter;
import com.example.assurini.Models.InsuranceAgency;
import com.example.assurini.Models.InsuranceType;
import com.example.assurini.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.SearchView;

public class CompanyFragment extends Fragment {

    private RecyclerView recyclerView;
    private CompanyAdapter companyAdapter;
    private List<InsuranceAgency> agencyList;
    private List<InsuranceType> insuranceTypeList;
    private DatabaseReference databaseReference;
    private DatabaseReference typeReference;
    private androidx.appcompat.widget.SearchView searchView;


    private Spinner filterSpinner;
    private static final String TAG = "CompanyFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_companys);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        agencyList = new ArrayList<>();
        insuranceTypeList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("InsuranceAgency");
        typeReference = FirebaseDatabase.getInstance().getReference("InsuranceType");

        searchView = view.findViewById(R.id.searchView);
        filterSpinner = view.findViewById(R.id.spinner_filtre);

        // Fetch data from Firebase
        fetchInsuranceAgencies();
        fetchInsuranceTypes();

        // Set up TextWatcher for SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        return view;
    }

    private void fetchInsuranceAgencies() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                agencyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InsuranceAgency agency = snapshot.getValue(InsuranceAgency.class);
                    if (agency != null) {
                        agencyList.add(agency);
                    }
                }
                filterList("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void fetchInsuranceTypes() {
        typeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                insuranceTypeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InsuranceType insuranceType = snapshot.getValue(InsuranceType.class);
                    if (insuranceType != null) {
                        insuranceTypeList.add(insuranceType);
                    }
                }
                populateFilterSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void populateFilterSpinner() {
        List<String> typeNames = new ArrayList<>();
        typeNames.add("All");
        for (InsuranceType type : insuranceTypeList) {
            typeNames.add(type.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterList(searchView.getQuery().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void filterList(String query) {
        if (filterSpinner.getSelectedItem() != null && !insuranceTypeList.isEmpty()) {
            String selectedInsuranceType = filterSpinner.getSelectedItem().toString();

            List<InsuranceAgency> filteredList = new ArrayList<>();

            for (InsuranceAgency agency : agencyList) {
                String insuranceType = agency.getInsuranceType();
                if (insuranceType != null && agency.getName().toLowerCase().contains(query.toLowerCase()) && (selectedInsuranceType.equals("All") || insuranceType.equals(selectedInsuranceType))) {
                    filteredList.add(agency);
                }
            }

            if (companyAdapter == null) {
                companyAdapter = new CompanyAdapter(getContext(), filteredList, insuranceTypeList);
                recyclerView.setAdapter(companyAdapter);
            } else {
                companyAdapter.updateList(filteredList);
            }
        } else {
            Log.e(TAG, "filterSpinner or insuranceTypeList is null or empty");
        }
    }
}
