package com.example.assurini.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assurini.Assurance.CompanyDetailFragment;
import com.example.assurini.Models.InsuranceAgency;
import com.example.assurini.Models.InsuranceType;
import com.example.assurini.R;

import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private Context context;
    private List<InsuranceAgency> agencyList;
    private List<InsuranceType> insuranceTypeList;

    public CompanyAdapter(Context context, List<InsuranceAgency> agencyList, List<InsuranceType> insuranceTypeList) {
        this.context = context;
        this.agencyList = agencyList;
        this.insuranceTypeList = insuranceTypeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.company_assurance_layout, parent, false);
        return new ViewHolder(view);
    }

// ...

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InsuranceAgency agency = agencyList.get(position);
        holder.companyName.setText(agency.getName());
        holder.companyPhone.setText(agency.getPhone());
        holder.companyAddress.setText(agency.getAddress());

        // Fetch insuranceType name from InsuranceType list
        for (InsuranceType type : insuranceTypeList) {
            if (type.getName().equals(agency.getInsuranceType())) {
                holder.companyType.setText(type.getName());
                break;
            }
        }

        holder.companyCard.setOnClickListener(v -> {
            openDetailFragment(agency.getIdInsuranceAgency());
            System.out.println(agency.getIdInsuranceAgency());
        });
    }

    private void openDetailFragment(String agencyId) {
        // Remove current fragment from the back stack
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();

        CompanyDetailFragment detailFragment = new CompanyDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("agencyId", agencyId);
        detailFragment.setArguments(bundle);

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public int getItemCount() {
        return agencyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView companyName, companyType, companyPhone, companyAddress;
        RelativeLayout companyCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.company_name);
            companyType = itemView.findViewById(R.id.company_anssurance_type);
            companyPhone = itemView.findViewById(R.id.company_phone);
            companyAddress = itemView.findViewById(R.id.company_address);
            companyCard = itemView.findViewById(R.id.linear_layout);
        }
    }

    public void updateList(List<InsuranceAgency> newList) {
        agencyList = newList;
        notifyDataSetChanged();
    }
}
