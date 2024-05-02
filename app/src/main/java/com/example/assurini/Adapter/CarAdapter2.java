package com.example.assurini.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assurini.Assurance.AssurancePlanFragment;
import com.example.assurini.Models.GrayCard;
import com.example.assurini.Models.Insurance;
import com.example.assurini.Profile.InsurencePlanDetailFragment;
import com.example.assurini.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CarAdapter2 extends RecyclerView.Adapter<CarAdapter2.ViewHolder> {

    private Context context;
    private List<GrayCard> carList;
    private String agencyId ;

    public CarAdapter2(Context context, List<GrayCard> carList,String agencyId) {
        this.context = context;
        this.carList = carList;
        this.agencyId = agencyId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.car_display_layout, parent, false);
        return new ViewHolder(view);
    }

// ...

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GrayCard car = carList.get(position);
        holder.carmark.setText(car.getMark());
        holder.cartype.setText(car.getType());


        holder.carCard.setOnClickListener(v -> {

            openAnsuranceRequestConfirmFragment(car.getIdGrayCard());
        });
    }

    private void openAnsuranceRequestConfirmFragment(Long getIdGrayCard) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Insurance");

        // Query the Assurance object with the idGrayCard
        databaseReference.orderByChild("idGrayCard").equalTo(String.valueOf(getIdGrayCard)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Insurance assurance = snapshot.getValue(Insurance.class);
                        if (assurance != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("IdGrayCard", String.valueOf(getIdGrayCard));
                            bundle.putSerializable("agencyId", assurance.getAgency()); // Passing Assurance object to the bundle

                            InsurencePlanDetailFragment InsurencePlanDetailFragment = new InsurencePlanDetailFragment();
                            InsurencePlanDetailFragment.setArguments(bundle);

                            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, InsurencePlanDetailFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();

                            // Log the fetched Assurance object
                            Log.d("AssuranceFetch", "Fetched Assurance: " + assurance.toString());
                        }
                        else {                    Toast.makeText(context, "the car isnt asured yet", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Log when no Assurance object is found
                    Log.d("AssuranceFetch", "No insurance details found for the selected gray card.");

                    // Handle the case where no Assurance object is found
                    Toast.makeText(context, "No insurance details found for the selected gray card.", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log the database error
                Log.e("AssuranceFetch", "Database error occurred: " + databaseError.getMessage());

                // Handle database error
                Toast.makeText(context, "Database error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView carmark, cartype;
        RelativeLayout carCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carmark = itemView.findViewById(R.id.car_model);
            cartype = itemView.findViewById(R.id.car_type);
            carCard = itemView.findViewById(R.id.carCard);
        }
    }

    public void updateList(List<GrayCard> newList) {
        carList = newList;
        notifyDataSetChanged();
    }
}
