package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.app.Fragment;
import android.view.View;

public abstract class LayoutFragment extends Fragment {
    public GoogleFitManager getGoogleFitManager() {
        return ((MainActivity) getActivity()).getGoogleFitManager();
    }

    public abstract void refreshLayout();
}
