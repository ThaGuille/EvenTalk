package com.example.tfg_application;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class LocationActivity {

    //private Location mLastLocation;
    private String TAG = "location";
    private Double latitude;
    private Double longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Context context;
    private Activity mActivity;

    /*public LocationActivity(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        // Do the work here--in this case, upload the images.
        // Indicate whether the work finished successfully with the Result
        Log.i(TAG, "did it");
        return Result.success();
    }*/

    public LocationActivity(Context context, Activity activity){
        this.context = context;
        this.mActivity = activity;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void lastLocation(Object o,Class classe, String method) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.i(TAG, "Starting to get location");
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(mActivity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();

                            //Spaguetti code (mentre es corregeix el BUG de la ultima versió de RecyclerView)
                            try {
                                //c son els diferents tipus d'arguments que passarem al metode (focusCamera). En aquest cas dos doubles
                                Class [] c = new Class[2];
                                c[0] = Double.class;
                                c[1] = Double.class;
                                Method method1 = classe.getMethod(method, c);
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                //No es pot passar amb un Double[2] perque android studio diu que no i punto (3h més tard)
                                method1.invoke(o, latitude, longitude);
                            }catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e){
                                e.printStackTrace();
                            }
                        } else {
                            Log.w(TAG, "location fail:" + task.getException());
                        }
                    }
                });

    }

}
