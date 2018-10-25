package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

final public class GoogleFitManager {
    private Activity mActivity;

    private boolean processingConnect = false;

    public int PERMISSION_REQUEST_CODE = 11235;

    public GoogleFitManager(Activity activity) {
        mActivity = activity;
    }

    private FitnessOptions getFitnessOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .addDataType(DataType.TYPE_ACTIVITY_SAMPLES)
                .build();
    }

    private void subscribeToFitnessData() {
        Fitness.getRecordingClient(mActivity, GoogleSignIn.getLastSignedInAccount(mActivity))
                .subscribe(DataType.TYPE_ACTIVITY_SAMPLES)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Logger.log("Subscribe to fitness data ok");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logger.log("Subscribe to fitness data failure " + e.getMessage());
                    }
                });
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

        if (hasPermissions()) {
            subscribeToFitnessData();
        }
    }

    public void disconnectGoogleFit() {
        if (processingConnect || !hasPermissions()) {
            return;
        }

        processingConnect = true;

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

    public void readActivitiesSamples() {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long startTime = calendar.getTimeInMillis();

        DataReadRequest request = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.TYPE_ACTIVITY_SAMPLES)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(mActivity, GoogleSignIn.getLastSignedInAccount(mActivity))
                .readData(request)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        Logger.log("Read data success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logger.log("Read data failure " + e.getMessage());
                    }
                });
    }

    // BLOCK END
}
