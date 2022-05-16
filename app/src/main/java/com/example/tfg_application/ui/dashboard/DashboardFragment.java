package com.example.tfg_application.ui.dashboard;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg_application.R;
import com.example.tfg_application.databinding.FragmentDashboardBinding;

import org.json.JSONException;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private LinearLayout.LayoutParams param1,param2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        param1 = (LinearLayout.LayoutParams)binding.underline1.getLayoutParams();
        param2 = (LinearLayout.LayoutParams)binding.underline2.getLayoutParams();
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final View popular = binding.orderByPopular;
        popular.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setUnderlineActive(binding.underline1);
            }
        });
        final View distance = binding.orderByDistance;
        distance.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setUnderlineActive(binding.underline2);
            }
        });
        final View recommended = binding.orderByRecommended;
        recommended.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setUnderlineActive(binding.underline3);
            }
        });

        //Sistema de queries a la API

        EditText buscador = binding.textSearchEvents;
        EventsRequester eventsRequester = new EventsRequester();
        /*RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "https://app.ticketmaster.com/discovery/v2/events.json?attractionId=K8vZ917Gku7&countryCode=CA&apikey=";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + getResources().getString(R.string.ticketmaster_api).toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("RESPUESTA", response);
                    //llamar metodo con el resultado
                }
                catch(Exception e){
                    Log.i("EXCEPTION", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        });*/

        buscador.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Your piece of code on keyboard search click
                    Log.i("SEARCH", "BUSCANDOOOO");
                    //Aquí es passarà la query ya filtrada o s'anirà a un altre mètode que la filtri o algo, no es passe el text a lo bruto
                    eventsRequester.getEvent(buscador.getText().toString());
                    //queue.add(stringRequest);
                    return true;
                }
                return false;
            }
        });

        return root;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}