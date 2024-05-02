package com.example.assurini.Assurance;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assurini.Adapter.CompanyAdapter;
import com.example.assurini.Adapter.GridAdapter;
import com.example.assurini.Adapter.GridAdapter3;
import com.example.assurini.Dashboard.LandingFragment;
import com.example.assurini.Models.InsuranceAgency;
import com.example.assurini.Models.InsuranceType;
import com.example.assurini.Profile.InsurancePlanFragment;
import com.example.assurini.Profile.ProfileFragment;
import com.example.assurini.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.SearchView;
public class TypaInsuranceFragment extends Fragment {
    private CardView linear_layout_arrow ;
    private GridView gridViewType;
    private String[] namesTypes = {"Car", "Life", "Travel", "House"}; // Predefined insurance type names
    private int[] imageResourcesTypes = {
            R.drawable.car,
            R.drawable.life,
            R.drawable.travel,
            R.drawable.house
    }; // Predefined image resources for insurance types

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_typa_insurance, container, false);

        gridViewType = view.findViewById(R.id.recycler_view_types);

        // Set up grid view with predefined data
        setUpGridView();

        linear_layout_arrow = view.findViewById(R.id.linear_layout_arrow);

        linear_layout_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Remove the current fragment
                fragmentManager.beginTransaction().remove(TypaInsuranceFragment.this).commit();

                // Remove current fragment from the back stack

                LandingFragment  LandingFragment= new LandingFragment();
                Bundle bundle = new Bundle();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, LandingFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                // Optional: If you want to pop the fragment from the back stack
                // fragmentManager.popBackStack();
            }
        });


        return view;
    }

    private void setUpGridView() {
        // Set up grid view with predefined data
        GridAdapter3 gridAdapter3 = new GridAdapter3(requireContext(), imageResourcesTypes, namesTypes);
        gridViewType.setAdapter(gridAdapter3);
    }
}
