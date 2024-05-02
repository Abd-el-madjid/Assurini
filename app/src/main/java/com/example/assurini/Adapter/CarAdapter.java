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

import com.example.assurini.Assurance.AssurancePlanFragment;
import com.example.assurini.Models.GrayCard;
import com.example.assurini.R;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private Context context;
    private List<GrayCard> carList;
    private String agencyId ;

    public CarAdapter(Context context, List<GrayCard> carList,String agencyId) {
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
        // Remove current fragment from the back stack
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();

        AssurancePlanFragment AssurancePlanFragment = new AssurancePlanFragment();
        Bundle bundle = new Bundle();
        bundle.putString("IdGrayCard", String.valueOf(getIdGrayCard));
        bundle.putString("agencyId", String.valueOf(agencyId));  // Correctly adding agencyId to bundle
        AssurancePlanFragment.setArguments(bundle);

        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, AssurancePlanFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
