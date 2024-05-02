package com.example.assurini.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.assurini.Assurance.AddCarFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public  class MyStepperAdapter extends AbstractFragmentStepAdapter {

    public MyStepperAdapter(androidx.fragment.app.FragmentManager fm, android.content.Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        AddCarFragment step = new AddCarFragment();
        Bundle b = new Bundle();
        b.putInt("CURRENT_STEP_POSITION", position);
        step.setArguments(b);
        return (Step) step;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@org.jetbrains.annotations.NotNull @androidx.annotation.IntRange(from = 0) int position) {
        return new StepViewModel.Builder(context)
                .setTitle("Step " + (position + 1)) // You can set your title here
                .create();
    }
}
