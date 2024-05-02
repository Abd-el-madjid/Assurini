package com.example.assurini.Profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.assurini.Adapter.CarAdapter2;
import com.example.assurini.Assurance.AddCarFragment;
import com.example.assurini.Assurance.CarListFragment;
import com.example.assurini.Assurance.CompanyDetailFragment;
import com.example.assurini.Models.GrayCard;
import com.example.assurini.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class InsurancePlanFragment extends Fragment {

    private DatabaseReference databaseReference;
    private String agencyId;
    private DatabaseReference typeReference;
    private TextView companyName;
    private TextView companyType;
    private TextView price;
    private  DatabaseReference cartgreyRef ;


    private TextView evaluation;
    private ImageView companyImage;
    private CardView linear_layout_arrow ;
    private Button show_panel_request ,hide_panel_request,request_button,addCar_button;
    private LinearLayout panel_raquest ;
    private RelativeLayout no_car_panel ;
    private RecyclerView recyclerView;
    private androidx.appcompat.widget.SearchView searchView;
    private List<GrayCard> carList;
    private CarAdapter2 caradapter2;
    private String UID;
    private FirebaseAuth auth ;

    private static final String TAG = "CarListFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            agencyId = getArguments().getString("agencyId");
            Log.e(TAG, agencyId);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insurance_plan, container, false);
        auth = FirebaseAuth.getInstance();
        UID= auth.getUid();
        cartgreyRef = FirebaseDatabase.getInstance().getReference("grayCards");
        addCar_button= view.findViewById(R.id.addCar_button);
        addCar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Remove the current fragment
                fragmentManager.beginTransaction().remove(InsurancePlanFragment.this).commit();

                // Remove current fragment from the back stack

                AddCarFragment  AddCarFragment = new AddCarFragment();
                Bundle bundle = new Bundle();
                bundle.putString("agencyId", agencyId);
                AddCarFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, AddCarFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                // Optional: If you want to pop the fragment from the back stack
                // fragmentManager.popBackStack();
            }
        });

        linear_layout_arrow = view.findViewById(R.id.linear_layout_arrow);

        linear_layout_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Remove the current fragment
                fragmentManager.beginTransaction().remove(InsurancePlanFragment.this).commit();

                // Remove current fragment from the back stack

                ProfileFragment  ProfileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("agencyId", agencyId);
                ProfileFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, ProfileFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                // Optional: If you want to pop the fragment from the back stack
                // fragmentManager.popBackStack();
            }
        });

        carList = new ArrayList<>();
        no_car_panel=view.findViewById(R.id.no_car_panel);
        recyclerView = view.findViewById(R.id.recycler_view_cars);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = view.findViewById(R.id.searchView);
        fetchCarList(true);
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


        // Add this code in your onCreateView method after setting up the searchView
        TabLayout tabLayout = view.findViewById(R.id.table_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fetchCarList(true);  // fetch isAssure = true
                        break;
                    case 1:
                        fetchCarList(false);  // fetch isAssure = false
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        fetchCarList(true);  // Fetch initially with isAssure = true

        return view;
    }
    private void fetchCarList(boolean isAssure) {
        cartgreyRef.orderByChild("personCar").equalTo(UID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carList.clear();
                boolean hasCars = false;  // Flag to check if the user has cars

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GrayCard car = snapshot.getValue(GrayCard.class);
                    if (car != null && car.isAssure() == isAssure) { // Check if assure matches
                        carList.add(car);
                        hasCars = true;
                    }
                }

                if (hasCars) {
                    no_car_panel.setVisibility(View.GONE);
                } else {
                    no_car_panel.setVisibility(View.VISIBLE);
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

    private void filterList(String query) {

        List<GrayCard> filteredList = new ArrayList<>();

        for (GrayCard car : carList) {
            if ( car.getMark().toLowerCase().contains(query.toLowerCase())  || car.getType().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(car);
            }
        }

        if (caradapter2 == null) {
            caradapter2 = new CarAdapter2(getContext(), filteredList, agencyId);
            recyclerView.setAdapter(caradapter2);
        } else {
            caradapter2.updateList(filteredList);
        }
    }
}


