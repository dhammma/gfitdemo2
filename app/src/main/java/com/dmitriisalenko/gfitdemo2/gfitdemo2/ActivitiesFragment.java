package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class ActivitiesFragment extends LayoutFragment {
    String result = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        ((MainActivity)getActivity()).getGoogleFitManager().readActivitiesSamples(
                new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        result = s;
                        refreshLayout();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result = "Error read activities: " + e.getMessage();
                        refreshLayout();
                    }
                }
        );
        return inflater.inflate(R.layout.activities_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        refreshLayout();
    }

    @Override
    public void refreshLayout() {
        if (getView() != null) {
            ((TextView) getView().findViewById(R.id.activitesTextView))
                    .setText(result);
        }
    }
}
