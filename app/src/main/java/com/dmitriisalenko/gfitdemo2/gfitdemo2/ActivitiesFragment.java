package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ActivitiesFragment extends LayoutFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        ((MainActivity)getActivity()).getGoogleFitManager().readActivitiesSamples();
        return inflater.inflate(R.layout.activities_layout, container, false);
    }

    @Override
    public void refreshLayout() {

    }
}
