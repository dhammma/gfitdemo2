package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends LayoutFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        return inflater.inflate(R.layout.home_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        refreshLayout();
    }

    @Override
    public void refreshLayout() {
        if (getView() != null) {

            TextView statusTextView = getView().findViewById(R.id.statusTextView);
            Button connectGoogleFitButton = getView().findViewById(R.id.connectGoogleFitButton);
            Button disconnectGoogleFitButton = getView().findViewById(R.id.disconnectGoogleFitButton);

            if (((MainActivity) getActivity()).getGoogleFitManager().hasPermissions()) {
                statusTextView.setText("Google Fit is connected");
                connectGoogleFitButton.setVisibility(View.GONE);
                disconnectGoogleFitButton.setVisibility(View.VISIBLE);
            } else {
                statusTextView.setText("Google Fit is not connected");
                connectGoogleFitButton.setVisibility(View.VISIBLE);
                disconnectGoogleFitButton.setVisibility(View.GONE);
            }
        }
    }
}
