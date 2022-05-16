package com.example.tfg_application.ui.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import ch.hsr.geohash.GeoHash;

import com.example.tfg_application.BuildConfig;
import com.example.tfg_application.R;

public class EventsRequester extends Activity {

    /*Aquí s'ha de passar tot el codi per fer les peticions a la api, així com el sistema de filtres
    i classificació que segueixin les querys

    https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/

    La query sempre començe amb https://app.ticketmaster.com/discovery/v2/

    Just després se pose events.json? o attractions.json? o lo que vulguem

    a partir de aqui els filtres se posen tal que https://app.ticketmaster.com/discovery/v2/events.json?filtre=tal&filtre=qual
    acabat sempre amb &apikey=ZyOtcio7lAmQOX21ynhfQ0mmdFmCQ01J


    Ara, a la pagina web esta pensat per js o cURL, yo ho faré en java suposo, per tant sa de adaptar el codi
    Mirar la part per seqüenciar la resposta, per obtenir les imatges o caps en específic etc.

    */
    //private GeoHash geoHash;
    private RequestQueue queue;
    private StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=ES&apikey=";
        Log.i("FULL URL", BuildConfig.ticketmaster_api);
        queue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.GET, url + BuildConfig.ticketmaster_api, new Response.Listener<String>() {
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
        });
    }

    public void getEvent(String query){
        //La url es cree a partir de la query passada
        Log.i("getEvent()", "getEvent()");
        queue.add(stringRequest);
    }
}
