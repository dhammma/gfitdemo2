package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class DistancessFragment extends LayoutFragment {
    String result = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        ((MainActivity) getActivity()).getGoogleFitManager().readDistancesData(
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
                        result = e.getMessage();
                        refreshLayout();
                    }
                }
        );
        return inflater.inflate(R.layout.distances_layout, container, false);
    }

    @Override
    public void refreshLayout() {
        if (getView() != null) {
            ((TextView) getView().findViewById(R.id.distancesTextView))
                    .setText(result);
        }
    }
}
