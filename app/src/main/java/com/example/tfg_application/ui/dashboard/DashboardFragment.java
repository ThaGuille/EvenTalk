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
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg_application.LocationActivity;
import com.example.tfg_application.MainActivity;
import com.example.tfg_application.R;
import com.example.tfg_application.SignInActivity;
import com.example.tfg_application.databinding.FragmentDashboardBinding;
import com.example.tfg_application.ui.chat.ChatFragment;
import com.example.tfg_application.ui.dashboard.model.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {


    private String TAG = "DashboardFragment";
    private FragmentDashboardBinding binding;
    private LinearLayout.LayoutParams param1, param2;
    private String sorting = "relevance";
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

        //Sistema per vincular un element amb la classe ViewModel
        //final TextView textView = binding.textDashboard;
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        param1 = (LinearLayout.LayoutParams) binding.underline1.getLayoutParams();
        param2 = (LinearLayout.LayoutParams) binding.underline2.getLayoutParams();
        eventsRequester = new EventsRequester();
        eventsRequester.setProcedence("events");
        locationActivity = new LocationActivity(getContext(), getActivity());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        //.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        final View popular = binding.orderByPopular;
        popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sorting.equals("relevance"))
                    return;
                setUnderlineActive(binding.underline1);
                sorting = "relevance";
                setBasicFilters();
                Log.i(TAG, "sorting by relevance");
                eventsRequester.getEvent(getContext());
            }
        });
        final View distance = binding.orderByDistance;
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sorting.equals("distance"))
                    return;
                if(requestPermission()) {
                setUnderlineActive(binding.underline2);
                sorting = "distance";
                setBasicFilters();
                Log.i(TAG, "sorting by distance");
                Object o2 = (Object)  DashboardFragment.this;
                    locationActivity.lastLocation(o2, DashboardFragment.class, "getLocation");
                }
                /*NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_events, true)
                        .build();
                Bundle bundle = new Bundle();
                Navigation.findNavController(v).navigate(R.id.action_navigation_events_to_navigation_chat, bundle, navOptions);*/
                //Navigation.findNavController(v).clearBackStack(R.id.action_navigation_events_to_navigation_chat);
                /*Class classe = ChatFragment.class;
                Intent intent = new Intent(getContext(), classe);
                startActivity(intent);*/
            }
        });
        final View recommended = binding.orderByRecommended;
        recommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sorting.equals("date"))
                    return;
                setUnderlineActive(binding.underline3);
                sorting = "date";
                setBasicFilters();
                Log.i(TAG, "sorting by date");
                eventsRequester.getEvent(getContext());
            }
        });


        //Sistema de queries a la API. Cambiarà bastant un cop s'apliquin els filtres així que de moment està bé així

        EditText buscador = binding.textSearchEvents;
        buscador.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    Context context = getContext();
                    Object o2 = (Object)  DashboardFragment.this;
                    //I això es farà amb aquest mètode, però de moment per fer probes no cal (pero ja funciona)
                    /*
                    * if tenim permisos de localització (suposo que tmb es pot cridar directament i comprovar-ho allí, depenent del resultat), el sistema actual i return
                    * else, cridar al eventsRequester desde aquí sense la localització
                    * */

                    /*if(requestPermission()) {
                        Log.i(TAG, "location permission passed");
                        //eventsRequester.setCountryCode("ES");
                        locationActivity.lastLocation(o2, DashboardFragment.class, "getLocation");
                        return true;
                    }*/
                    setBasicFilters();
                    eventsRequester.getEvent(getContext());
                    //eventsRequester.getEvent(buscador.getText().toString(), context, mLastLocation);
                    return true;
                }
                return false;
            }
        });

        return root;
    }

    //Mètode cridat desde LocationActivity per retornar la localització
    public void getLocation(Double latitude, Double longitude){
        Log.i(TAG, "estem al getLocation");
        //-------Revisar: sa de passar latLong a geoHash ------------------
        //String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
        Location locTemp = new Location("nom");
        locTemp.setLatitude(latitude);
        locTemp.setLongitude(longitude);
        mLastLocation = locTemp;
        LatLng ltlg = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        EditText buscador = binding.textSearchEvents;
        Log.i(TAG, "al getLocation" + mLastLocation);
        eventsRequester.setLocation(mLastLocation);
        //Actualment passem un radi predeterminat de 1000km pk a espanya hi ha poca cosa (res)
        //Quan es tinguin més events o alguna solució ja es farà que ho pugui determinar l'usuari
        eventsRequester.setRadius(1000);
        eventsRequester.getEvent(getContext());
    }

    //Tmb esta al LocationActivity, pero com android studio fa coses rares millor tenir-ho repetit
    private boolean requestPermission(){
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setBasicFilters(){
        EditText buscador = binding.textSearchEvents;
        if(buscador.getText().length()>0){
            eventsRequester.setTextFilter(buscador.getText().toString());
        }
        eventsRequester.setProcedence("events");
        eventsRequester.setSorting(sorting);
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
        List<Event> eventList = Arrays.asList(events);
        mEventList.clear();
        mEventList.addAll(eventList);
        Log.i("EventAdapter", "event passing from dashboard: " + events[0].toJSONObject().toString());
        //Log.i("EventAdapter", "event passing from dashboard: " + events[1].toJSONObject().toString());
        //Log.i("EventAdapter", "event passing from dashboard: " + events[2].toJSONObject().toString());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}