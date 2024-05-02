package com.example.assurini.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.assurini.Adapter.GridAdapter2;
import com.example.assurini.Assurance.CompanyDetailFragment;
import com.example.assurini.Assurance.CompanyFragment;
import com.example.assurini.Assurance.TypaInsuranceFragment;
import com.example.assurini.Models.Notification;
import com.example.assurini.R;
import com.example.assurini.Adapter.GridAdapter;
import com.example.assurini.UI.Authentification.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LandingFragment extends Fragment {

    private GridView gridView,gridViewRightType,gridViewLeftType,gridViewRightCompanies,gridViewLeftCompanies;

    // Array of image resources and corresponding names
    private int[] imageResourcesTypes = {
            R.drawable.car,
            R.drawable.life,
    };

    private int[] imageResourcesTypes2 = {
            R.drawable.house,
            R.drawable.travel,
    };
    private String[] namesTypes = {

            "Car",
            "Life",
    };

    private String[] namesTypes2 = {

            "House",
            "Travel",
    };
    private int[] imageResourcesCompanies = {
            R.drawable.company3,
            R.drawable.company4,
    };
    private int[] imageResourcesCompanies2 = {
            R.drawable.company1,
            R.drawable.company2,
    };

    private String[] namesCompanies = {
            "Assurance",
            "CarDef",
    };
    private ImageView notificationNumber ;
    private FirebaseAuth auth;
    private DatabaseReference personneRef, notificationRef;
    private TextView fullname ;
    private TextView seeallcomapnies ;

    private TextView seeallType ;

    private FirebaseUser currentUser;
private LinearLayout notification ;
    public LandingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_landing, container, false);


         gridViewLeftType = view.findViewById(R.id.types_body_left);
        GridAdapter gridAdapterLefType = new GridAdapter(requireContext(), imageResourcesTypes, namesTypes);
        gridViewLeftType.setAdapter(gridAdapterLefType);

         gridViewRightType = view.findViewById(R.id.types_body_right);
        GridAdapter gridAdapterRightType = new GridAdapter(requireContext(), imageResourcesTypes2, namesTypes2);
        gridViewRightType.setAdapter(gridAdapterRightType);



        gridViewLeftCompanies = view.findViewById(R.id.companies_body_left);
        GridAdapter2 gridAdapterLeftCompanies = new GridAdapter2(requireContext(), imageResourcesCompanies, namesCompanies);
        gridViewLeftCompanies.setAdapter(gridAdapterLeftCompanies);

        gridViewRightCompanies = view.findViewById(R.id.companies_body_right);
        GridAdapter2 gridAdapterRightCompanies = new GridAdapter2(requireContext(), imageResourcesCompanies2, namesCompanies);
        gridViewRightCompanies.setAdapter(gridAdapterRightCompanies);

        notificationNumber = view.findViewById(R.id.notificationNumber);
        seeallcomapnies = view.findViewById(R.id.seeallcomapnies);

        seeallcomapnies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Remove the current fragment
                fragmentManager.beginTransaction().remove(LandingFragment.this).commit();

                // Remove current fragment from the back stack

                CompanyFragment CompanyFragment = new CompanyFragment();
                Bundle bundle = new Bundle();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, CompanyFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        seeallType = view.findViewById(R.id.seeallType);

        seeallType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Remove the current fragment
                fragmentManager.beginTransaction().remove(LandingFragment.this).commit();


                // Remove current fragment from the back stack

                TypaInsuranceFragment TypaInsuranceFragment = new TypaInsuranceFragment();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, TypaInsuranceFragment);
                transaction.addToBackStack(null);
                transaction.commit();            }
        });

        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");


        fullname = view.findViewById(R.id.fullname);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
if(currentUser != null){
    notificationRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            long notificationCount = 0;

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String idPersonne = snapshot.child("idPersonne").getValue(String.class); // Note the use of Long instead of long
                Boolean status = snapshot.child("status").getValue(Boolean.class);
                if (idPersonne != null && idPersonne == currentUser.getUid() && !status ) {
                    notificationCount++;

                }
            }

            if (notificationCount != 0) {
                notificationNumber.setVisibility(View.VISIBLE);
            } else {
                notificationNumber.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("MyTag", "onCancelled: ", databaseError.toException());
        }
    });

}
        notification = view.findViewById(R.id.notification);

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disconnect the user here
                if (currentUser != null) {

                    // Redirect to login or home page
                    // For example:
                     startActivity(new Intent(getActivity(), NotificationActivity.class));
                   // Finish the current activity
                }
            }
        });
        personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
        currentUser = auth.getCurrentUser();

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
        return view;
    }
}