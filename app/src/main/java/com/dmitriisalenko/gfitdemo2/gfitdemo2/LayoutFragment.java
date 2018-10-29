package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.app.Fragment;

public abstract class LayoutFragment extends Fragment {
    public GoogleFitManager getGoogleFitManager() {
        return ((MainActivity) getActivity()).getGoogleFitManager();
    }

    public abstract void refreshLayout();
}
