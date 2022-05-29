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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        Object o = (Object) this;
        locationActivity.lastLocation(o, getClass(), "focusCamera");
        Log.i("location", "currentLocation:" + currentLocation);
        if(currentLocation!=null) {
            LatLng ltlg = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlg, 20));
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                //get lat and lng
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                googleMap.clear();
                //make zoom
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                //add red market
                googleMap.addMarker(markerOptions);
            }
        });
    }

    public void focusCamera(Location location){
        Log.i("location", "focusCamera :" + location);
        currentLocation = location;
        LatLng ltlg = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlg, 13));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}