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
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFitHelper {
    GoogleFitManager mGoogleFitManager;

    public GoogleFitHelper(GoogleFitManager googleFitManager) {
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

    public String getBucketsData(List<Bucket> buckets) {
        String result = "";

        // avoid modifying of arguments
        List<Bucket> reverseBuckets = new ArrayList<>(buckets);
        Collections.reverse(reverseBuckets);

        for (Bucket item : reverseBuckets) {
            String bucketData = getBucketData(item);
            result += bucketData + "\n";
        }

        return result;
    }

    public String getDistanceValue(Value value) {
        return Double.toString(Math.floor(value.asFloat() * 100) / 100) + "m";
    }

    public String getActivityValue(Value value) {
        return value.asActivity();
    }

    public String getDurationValue(Value value) {
        int intValue = value.asInt() / 1000;

        if (intValue < 60) {
            return Integer.toString(intValue) + "s";
        } else if (intValue < 3600) {
            return Integer.toString(intValue / 60) + "m";
        } else {
            return Integer.toString(intValue / 3600) + "h";
        }
    }

    public String getDataPointData(DataPoint dataPoint) {
        String result = "";

        DateFormat timeFormat = DateFormat.getTimeInstance();

        String startTime = timeFormat.format(dataPoint.getStartTime(TimeUnit.MILLISECONDS));
        String endTime = timeFormat.format(dataPoint.getEndTime(TimeUnit.MILLISECONDS));

        result += startTime + " - " + endTime + "\n";

        for (Field field : dataPoint.getDataType().getFields()) {
            Value originalValue = dataPoint.getValue(field);

            if (originalValue == null) {
                continue;
            }

            String stringValue = "";

            Logger.log(field.getName() + ": " + field.getFormat());

            if (field.getName().equals("distance")) {
                stringValue = getDistanceValue(originalValue);
            } else if (field.getName().equals("activity")) {
                stringValue = getActivityValue(originalValue);
            } else if (field.getName().equals("duration")) {
                stringValue = getDurationValue(originalValue);
            } else if (field.getFormat() == Field.FORMAT_FLOAT) {
                stringValue = Float.toString(originalValue.asFloat());
            } else if (field.getFormat() == Field.FORMAT_INT32) {
                stringValue = Integer.toString(originalValue.asInt());
            } else {
                stringValue = originalValue.toString();
            }

            result += "\t" + field.getName() + ": " + stringValue + "\n";
        }

        return result;
    }

    public String getDataSetData(DataSet dataSet) {
        String result = "";

        List<DataPoint> reverseDataPoints = new ArrayList<>(dataSet.getDataPoints());
        Collections.reverse(reverseDataPoints);

        if (reverseDataPoints.size() > 0) {
            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                String dataPointData = getDataPointData(dataPoint);

                result += "\n" + dataPointData;
            }
        } else {
            result += "\nNo data\n";
        }

        return result;
    }

    public String getBucketData(Bucket bucket) {
        String result = "";

        DateFormat dateFormat = DateFormat.getDateInstance();

        String startDate = dateFormat.format(bucket.getStartTime(TimeUnit.MILLISECONDS));
        String endDate = dateFormat.format(bucket.getEndTime(TimeUnit.MILLISECONDS));

        result += startDate + " - " + endDate + "\n";

        // @todo: why we have only one DataSet here?
        result += getDataSetData(bucket.getDataSets().get(0));

        return result;
    }

    public void readData(DataType inputDataType, DataType outputDataType, final OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
        Fitness.getHistoryClient(mGoogleFitManager.getActivity(), GoogleSignIn.getLastSignedInAccount(mGoogleFitManager.getActivity()))
                .readData(getDataReadRequest(inputDataType, outputDataType))
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {

                        String result = getBucketsData(dataReadResponse.getBuckets());

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
