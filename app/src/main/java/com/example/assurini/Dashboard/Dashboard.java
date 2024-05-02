package com.example.assurini.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.assurini.Assurance.AssuranceFragment;
import com.example.assurini.Assurance.CompanyDetailFragment;
import com.example.assurini.Assurance.CompanyFragment;
import com.example.assurini.R;
import com.example.assurini.Assurance.AddCarFragment;
import com.example.assurini.Dashboard.LandingFragment;
import com.example.assurini.Profile.ProfileFragment;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Dashboard extends AppCompatActivity {

    private MeowBottomNavigation bottomNavigation;
    private FragmentManager fragmentManager;
    private boolean isAddCarFragmentDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        fragmentManager = getSupportFragmentManager();

        bottomNavigation.show(1, true);

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_format_list_bulleted_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.add));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.outline_shield_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.baseline_manage_accounts_24));

        // Set default fragment to LandingFragment
        switchFragment(new LandingFragment());

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        switchFragment(new LandingFragment());
                        break;
                    case 2:
                        switchFragment(new CompanyFragment());
                        break;
                    case 3:
                        switchFragment(new AddCarFragment());
                        break;
                    case 4:
                        switchFragment(new AssuranceFragment());
                        break;
                    case 5:
                        switchFragment(new ProfileFragment());
                        break;
                }
                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                return null;
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        if (fragment instanceof AddCarFragment) {
            bottomNavigation.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (fragment instanceof CompanyDetailFragment) {
            bottomNavigation.setBackgroundColor(getResources().getColor(R.color.whiteColor));

        }else if (fragment instanceof ProfileFragment) {
            bottomNavigation.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else  {
            bottomNavigation.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }
}
