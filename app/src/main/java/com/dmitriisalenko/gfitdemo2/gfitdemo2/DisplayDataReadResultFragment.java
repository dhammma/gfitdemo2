package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

abstract public class DisplayDataReadResultFragment extends LayoutFragment {
    private String result = "";

    public abstract String getType();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        getGoogleFitManager().readData(
                getType(),
                new OnSuccessListener<String>(){
                    @Override
                    public void onSuccess(String s) {
                        result = s;
                        refreshLayout();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        result = e.getMessage();
                        refreshLayout();
                    }
                }
        );
        return inflater.inflate(R.layout.display_data_read_result_layout, container, false);
    }

    @Override
    public void refreshLayout() {
        if (getView() != null) {
            TextView resultTextView = getView().findViewById(R.id.data_read_result);
            resultTextView.setText(result);
        }
    }
}
