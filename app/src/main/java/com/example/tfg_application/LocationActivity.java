package com.example.tfg_application;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import com.example.tfg_application.ui.notifications.NotificationsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class LocationActivity {

    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Context context;
    private Activity mActivity;

    public LocationActivity(Context context, Activity activity){
        this.context = context;
        this.mActivity = activity;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void lastLocation(Object o,Class classe, String method) {
        Log.i("location", "getting last location2");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.i("location", "getting last location");
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(mActivity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            /*Log.i("location", String.format(Locale.ENGLISH, "%s: %f", mLastLocation.getLatitude()));
                            Log.i("location", String.format(Locale.ENGLISH, "%s: %f", mLastLocation.getLongitude()));*/
                            Log.i("location", "LocationActivity query:"+ mLastLocation.toString());
                            //return mLastLocation;
                            try {
                                Log.i("location", "almenos hace el try");
                                Class [] c = new Class[1];
                                c[0] = Location.class;
                                Method method1 = classe.getMethod(method, c);
                                Log.i("location", "Invoking method :"+ method1.toString());
                                Log.i("location", "And now last location is :"+mLastLocation);

                                method1.invoke(o, mLastLocation);
                            }catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e){
                                Log.w("location", "method failed: " + e.toString());
                            }

                        } else {
                            Log.w("location", "location fail:" + task.getException());
                            mLastLocation = task.getResult();
                        }
                    }
                });
        Log.i("location", "llega hasta aqui?" + mLastLocation);
    }

}
