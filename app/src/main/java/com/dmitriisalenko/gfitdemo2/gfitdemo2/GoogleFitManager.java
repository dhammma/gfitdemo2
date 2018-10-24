package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

final public class GoogleFitManager {
    private Activity mActivity;

    private boolean processingConnect = false;

    public int PERMISSION_REQUEST_CODE = 11235;

    public GoogleFitManager(Activity activity) {
        mActivity = activity;
    }

    private FitnessOptions getFitnessOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SAMPLES)
                .build();
    }

    //
    // BLOCK START: public methods list
    //

    public boolean hasPermissions() {
        return GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(mActivity),
                getFitnessOptions());
    }

    public void connectGoogleFit() {
        if (processingConnect || hasPermissions()) {
            return;
        }

        processingConnect = true;

        GoogleSignIn.requestPermissions(
                mActivity,
                PERMISSION_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(mActivity),
                getFitnessOptions());
    }

    public void connectGoogleFitCompleted() {
        processingConnect = false;
    }

    public void disconnectGoogleFit() {
        if (processingConnect || !hasPermissions()) {
            return;
        }

        processingConnect = true;

        Logger.log("Test");

        Fitness.getConfigClient(mActivity, GoogleSignIn.getLastSignedInAccount(mActivity))
                .disableFit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Logger.log("Disconnect Google Fit is okay");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logger.log("Disconnect Google Fit failure " + e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        processingConnect = false;

                        GoogleSignInOptions signInOptions = (new GoogleSignInOptions.Builder()).addExtension(getFitnessOptions()).build();
                        GoogleSignInClient client = GoogleSignIn.getClient(mActivity, signInOptions);
                        client.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Logger.log("Google Account revoked access");
                                ((MainActivity) mActivity).refreshMainContentLayout();
                            }
                        });
//                        ((MainActivity) mActivity).refreshMainContentLayout();
                    }
                });
    }

    // BLOCK END
}
