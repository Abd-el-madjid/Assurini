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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assurini.Adapter.CarAdapter;
import com.example.assurini.Adapter.CompanyAdapter;
import com.example.assurini.Models.GrayCard;
import com.example.assurini.Models.InsuranceAgency;
import com.example.assurini.Models.InsuranceType;
import com.example.assurini.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.SearchView;

public class CarListFragment extends Fragment {

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
    private CarAdapter caradapter;
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
        View view = inflater.inflate(R.layout.fragment_car_list, container, false);
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
                fragmentManager.beginTransaction().remove(CarListFragment.this).commit();

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
                fragmentManager.beginTransaction().remove(CarListFragment.this).commit();

                // Remove current fragment from the back stack

                CompanyDetailFragment  CompanyDetailFragment = new CompanyDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("agencyId", agencyId);
                CompanyDetailFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, CompanyDetailFragment);
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
        fetchCarList();
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
    private void fetchCarList() {
        cartgreyRef.orderByChild("personCar").equalTo(UID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carList.clear();
                boolean hasCars = false;  // Flag to check if the user has cars

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GrayCard car = snapshot.getValue(GrayCard.class);
                    if (car != null && !car.isAssure()) { // Check if assure is false
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

            if (caradapter == null) {
                caradapter = new CarAdapter(getContext(), filteredList, agencyId);
                recyclerView.setAdapter(caradapter);
            } else {
                caradapter.updateList(filteredList);
            }
        }
    }


