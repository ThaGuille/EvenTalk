package com.example.tfg_application.ui.dashboard;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg_application.LocationActivity;
import com.example.tfg_application.R;
import com.example.tfg_application.SignInActivity;
import com.example.tfg_application.databinding.FragmentDashboardBinding;
import com.example.tfg_application.ui.dashboard.model.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private LinearLayout.LayoutParams param1, param2;
    private EventsRequester eventsRequester;
    private LocationManager locationManager;
    private Location mLastLocation;
    private LocationActivity locationActivity;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private RecyclerView recyclerView;
    private static EventAdapter adapter;
    private static List<Event> mEventList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //RecyclerView binding
        recyclerView = binding.eventRecyclerView;

        adapter = new EventAdapter(mEventList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final TextView textView = binding.textDashboard;
        param1 = (LinearLayout.LayoutParams) binding.underline1.getLayoutParams();
        param2 = (LinearLayout.LayoutParams) binding.underline2.getLayoutParams();
        eventsRequester = new EventsRequester();
        locationActivity = new LocationActivity(getContext(), getActivity());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        //.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final View popular = binding.orderByPopular;
        popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUnderlineActive(binding.underline1);
            }
        });
        final View distance = binding.orderByDistance;
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUnderlineActive(binding.underline2);
            }
        });
        final View recommended = binding.orderByRecommended;
        recommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUnderlineActive(binding.underline3);
            }
        });


        //Sistema de queries a la API. Cambiarà bastant un cop s'apliquin els filtres així que de moment està bé així

        EditText buscador = binding.textSearchEvents;
        buscador.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Your piece of code on keyboard search click
                    Log.i("events", "BUSCANDOOOO");

                    //Per si ho volgues realitzar amb IntentService, pero no té pinta que sigui rentable
                    /*Intent intent = new Intent(getActivity(), EventsRequester.class);
                    intent.setClassName("com.example.tfg_application.ui.dashboard", "com.example.tfg_application.ui.dashboard.EventsRequester");
                    getActivity().startService(intent);*/
                    Log.i("events", "started");

                    //Aquí es passarà la query ya filtrada o s'anirà a un altre mètode que la filtri o algo, no es passe el text a lo bruto
                    //lastLocation();
                    Context context = getContext();
                    //Apuntar com s'ha fet aixo per a que funcioni: antic codi
                    //Object o = (Object) this;
                    //locationActivity.lastLocation(o, getclass(), "getLocation");
                    //pero aixo retornava la classe com...DashboardFragment$4. S'ha hagut d'agafar la referencia a la classe i al objecte per a que funcioni
                    Object o2 = (Object)  DashboardFragment.this;
                    //I això es farà amb aquest mètode, però de moment per fer probes no cal (pero ja funciona)
                    locationActivity.lastLocation(o2, DashboardFragment.class, "getLocation");
                    Log.i("events", "proba1" + mLastLocation);
                    //eventsRequester.getEvent(buscador.getText().toString(), context, mLastLocation);
                    return true;
                }
                return false;
            }
        });

        return root;
    }

    public void getLocation(Location location){
        //-------Revisar: sa de passar latLong a geoHash ------------------
        //String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
        mLastLocation = location;
        LatLng ltlg = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        EditText buscador = binding.textSearchEvents;
        Log.i("events", "al getLocation" + mLastLocation);
        TextView textoTemporal = binding.textDashboard;
        eventsRequester.getEvent(buscador.getText().toString(), getContext(), mLastLocation, textoTemporal);
    }

    //Això s'ha d'eliminar i substituir pel mètode de LocationActivity
    private void lastLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            /*Log.i("location", String.format(Locale.ENGLISH, "%s: %f", mLastLocation.getLatitude()));
                            Log.i("location", String.format(Locale.ENGLISH, "%s: %f", mLastLocation.getLongitude()));*/
                            Log.i("locationnnn", mLastLocation.toString());
                            //Revisar:
                            getLocation(mLastLocation);
                        } else {
                            Log.w("location", "location fail:" + task.getException());
                        }
                    }
                });
    }

    private boolean requestPermission(){
        int permissionState = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void setUnderlineActive(View v){
        binding.underline1.setBackgroundColor(getResources().getColor(R.color.gray));
        binding.underline2.setBackgroundColor(getResources().getColor(R.color.gray));
        binding.underline3.setBackgroundColor(getResources().getColor(R.color.gray));
        v.setBackgroundColor(getResources().getColor(R.color.blue));

        binding.underline1.setLayoutParams(param2);
        binding.underline2.setLayoutParams(param2);
        binding.underline3.setLayoutParams(param2);
        v.setLayoutParams(param1);
    }

    //Enlloc de tantes coses estatiquees tmb es podrie fer adapter.registerAdapterDataObserver(classe(adapter, eventsRequester))
    //I en aquella classe fer les operacions, agafant els events de la propia classe eventsRequester
    public static void changeEvents(Event[] events){
        Log.i("events", "changing Events");
        List<Event> eventList = Arrays.asList(events);
        mEventList.clear();
        mEventList.addAll(eventList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}