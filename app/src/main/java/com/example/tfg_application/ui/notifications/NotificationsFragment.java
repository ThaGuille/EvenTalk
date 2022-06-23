package com.example.tfg_application.ui.notifications;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tfg_application.LocationActivity;
import com.example.tfg_application.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;


import com.example.tfg_application.databinding.FragmentNotificationsBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NotificationsFragment extends Fragment implements OnMapReadyCallback{

    private FragmentNotificationsBinding binding;
    private LocationActivity locationActivity;
    private Location currentLocation;
    private GoogleMap googleMap;
    private Location moveWhenReady = null;
    private Bundle bundle;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
        locationActivity = new LocationActivity(getContext(), getActivity());
        //final TextView textView = binding.textNotifications;
        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments!=null){
            bundle = arguments;
            Log.i("MAPAPROBA", "onCreate has arguments");
            Double lat =null, lon=null;
            try{
                lat = Double.parseDouble(arguments.getString("latitude"));
                lon = Double.parseDouble(arguments.getString("longitude"));
            }catch(Exception e){
                Log.e("MAP", e.getMessage());
            }
            if(lat!=null && lon!=null) {
                Location location = new Location("event");
                location.setLatitude(lat);
                location.setLongitude(lon);
                Log.i("MAP", "map location: "+location.toString());
                moveWhenReady = location;
                //focusCamera(location);
            }
        }
    }

   /* @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        Log.i("MAPAPROBA", "1");
        if(savedInstanceState != null){
            Log.i("MAPAPROBA", "onViewCreated has bundle");

        }
    }*/

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        Object o = (Object) this;
        if(moveWhenReady!=null){
            Log.i("MAPAPROBA", "moveWhenReady:");
            focusCamera(moveWhenReady);
        }else{
        locationActivity.lastLocation(o, getClass(), "focusCamera");
        Log.i("location", "currentLocation:" + currentLocation);

        if(currentLocation!=null) {
            Log.i("MAPAPROBA", "moveToLLEIDA:");
            LatLng ltlg = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlg, 20));
        }}
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                //get lat and lng
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                googleMap.clear();
                //make zoom
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                //googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng);
                //add red market
                googleMap.addMarker(markerOptions);
            }
        });
    }

    public void focusCamera(Location location){
        Log.i("MAPAPROBA", "focusCamera :" + location);
        currentLocation = location;
        LatLng ltlg = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        Log.i("MAPAPROBA", "latlong:" + ltlg);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlg, 13));
        if(moveWhenReady!=null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ltlg);
            //get lat and lng
            markerOptions.title(bundle.getString("name"));
            googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}