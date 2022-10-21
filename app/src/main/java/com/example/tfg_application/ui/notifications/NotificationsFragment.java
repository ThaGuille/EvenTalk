package com.example.tfg_application.ui.notifications;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.tfg_application.LocationActivity;
import com.example.tfg_application.R;
import com.example.tfg_application.RegisterActivity;
import com.example.tfg_application.ui.dashboard.EventsRequester;
import com.example.tfg_application.ui.dashboard.model.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;


import com.example.tfg_application.databinding.FragmentNotificationsBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements OnMapReadyCallback{

    private String TAG = "NotificationsFragment";
    private FragmentNotificationsBinding binding;
    private LocationActivity locationActivity;
    private Double[] currentLocation = new Double[2];
    private Double[] searchLocation = new Double[2];
    private GoogleMap googleMap;
    private Double[] eventLocation = new Double[2];
    //private Location moveWhenReady = null;
    private Bundle arguments;
    private Event event;
    private EventsRequester eventsRequester;
    private static List<Event> mEventList = new ArrayList<>();
    private Boolean findEvents = false;
    ActivityResultLauncher<Intent> launchSomeActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
        locationActivity = new LocationActivity(getContext(), getActivity());
        eventsRequester = new EventsRequester();

        binding.mapsSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng cameraPositionLatLng = googleMap.getCameraPosition().target;
                searchLocation[0] = cameraPositionLatLng.latitude;
                searchLocation[1] = cameraPositionLatLng.longitude;
                findNearEvents();
            }
        });
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        arguments = getArguments();
        if(arguments!=null){
            event = (Event) arguments.getSerializable("event");
            Log.i(TAG, "EVENT: " + event.name);
            if(event.location!=null){
                eventLocation = event.location;
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
        //Si s'ha passat un event com a parametre (s'ha obert el mapa a traves d'un event)
        if(arguments!=null || eventLocation[0] != null || eventLocation[1] != null){
            focusCamera(eventLocation[0], eventLocation[1]);
        }
        else{
        findEvents =true;
        locationActivity.lastLocation(o, getClass(), "focusCamera");
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                //googleMap.clear();
                //make zoom
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                googleMap.addMarker(markerOptions);
            }
        });
    }

    //Sí, usem un Double[2] (currentLocation), un Location (location) i un LatLong (ltlg). ª
    public void focusCamera(Double latitude, Double longitude){
        if(latitude == null || longitude == null) return;
        currentLocation[0] = latitude;
        currentLocation[1] = longitude;
        if(findEvents){
            searchLocation[0] = latitude;
            searchLocation[1] = longitude;
            findNearEvents();
        }
        Log.i(TAG, "focusCamera on :" + latitude + ", "+ longitude);
        LatLng ltlg = new LatLng(latitude,longitude);
        //zoom de 2.0 a 21.0, no especifique escala
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlg, 13));
        if(eventLocation!=null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ltlg);
            markerOptions.title(event.name);
            googleMap.addMarker(markerOptions);
        }
    }

    public void setEventPoints(Event[] events){
    }

    private void findNearEvents(){
        Location location = new Location("");
        location.setLatitude(searchLocation[0]);
        location.setLongitude(searchLocation[1]);
        eventsRequester.setLocation(location);
        eventsRequester.setRadius(500);
        eventsRequester.setProcedence("map");
        eventsRequester.setMethodParams((Object) NotificationsFragment.this, NotificationsFragment.class, "getNearEvents");
        eventsRequester.getEvent(getContext());
    }

    public void getNearEvents(List<Event> events){
        if(events.size()==0){
            Log.i(TAG, "no se encontraron eventos ");
            Toast.makeText( getContext(),"No se ha encontrado ningún evento cerca", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "not empty? ");
        for (Event item: events) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng ltlg = new LatLng(item.location[0],item.location[1]);
            markerOptions.position(ltlg);
            markerOptions.title(item.name);
            googleMap.clear();
            googleMap.addMarker(markerOptions);
            Log.i(TAG, "marker "+ item.name + " + set at: " + ltlg.toString());
        }
    }

    /*public void openWorkerActivity() {
        WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(LocationActivity.class)
                        .build();
        WorkManager.getInstance(getContext()).enqueue(uploadWorkRequest);
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}