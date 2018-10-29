package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFitFactory {
    GoogleFitManager mGoogleFitManager;

    public GoogleFitFactory(GoogleFitManager googleFitManager) {
        mGoogleFitManager = googleFitManager;
    }

    public DataReadRequest getDataReadRequest(DataType inputDataType, DataType outputDataType) {
        Calendar calendar = Calendar.getInstance();
        Date now =  new Date();
        calendar.setTime(now);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long startTime = calendar.getTimeInMillis();

        return new DataReadRequest.Builder()
                .aggregate(inputDataType, outputDataType)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
    }

    public void readData(DataType inputDataType, DataType outputDataType, final OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
        Fitness.getHistoryClient(mGoogleFitManager.getActivity(), GoogleSignIn.getLastSignedInAccount(mGoogleFitManager.getActivity()))
                .readData(getDataReadRequest(inputDataType, outputDataType))
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        String result = "";

                        if (dataReadResponse.getStatus().isSuccess()) {
                            DateFormat dateFormat = DateFormat.getDateInstance();

                            result += "Data read result is success";

                            result += "\n\nTotally buckets count: " + dataReadResponse.getBuckets().size();
                            result += "\nTotally dataSets count: " + dataReadResponse.getDataSets().size();

                            if (dataReadResponse.getBuckets().size() > 0) {
                                result += "\n\nBuckets:";

                                for (Bucket bucket : dataReadResponse.getBuckets()) {
                                    List<DataSet> dataSets = bucket.getDataSets();
                                    String startDate = dateFormat.format(bucket.getStartTime(TimeUnit.MILLISECONDS));
                                    String endDate = dateFormat.format(bucket.getEndTime(TimeUnit.MILLISECONDS));

                                    result += "\n\nActivity: " + bucket.getActivity();
                                    result += "\nStart - End: " + startDate + " - " + endDate + "\n";

                                    if (bucket.getDataSets().size() > 0) {
                                        result += "\nDataSetsCount: " + bucket.getDataSets().size() + "\n";
                                        result += "DataSet list:\n";

                                        for (DataSet dataSet : bucket.getDataSets()) {
                                            result += "\nDataSet type: " + dataSet.getDataType().getName() + "\n";

                                            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                                result += "Fields:";

                                                for (Field field : dataPoint.getDataType().getFields()) {
                                                    result += "\n" + field.getName() + ": " + dataPoint.getValue(field).toString();
                                                }
                                            }

                                            result += "\n";
                                        }
                                    } else {
                                        result += "\nNo dataSets available\n";
                                    }
                                }
                            }
                        } else {
                            result += "Data read result is not success " + dataReadResponse.getStatus().toString();
                        }

                        onSuccessListener.onSuccess(result);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailure(e);
                    }
                });
    }
}
