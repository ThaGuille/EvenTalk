package com.example.tfg_application.ui.dashboard;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import ch.hsr.geohash.GeoHash;

import com.example.tfg_application.BuildConfig;
import com.example.tfg_application.R;

public class EventsRequester {

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


    public void getEvent(String query, Context context, Location location, TextView viewTempoText){
        Log.i("events", "EventsRequester location " + location);
        RequestQueue queue = Volley.newRequestQueue(context);
        //La url es cree a partir de la query passada
        Log.i("getEvent()", "getEvent()");
        if(queue==null){
            Log.i("query", "quede is null");
        }

        String url = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=ES&apikey=";

        String urlLocation = "https://app.ticketmaster.com/discovery/v2/events.json?latlong="+location.getLatitude()+","+location.getLongitude()+ "&radius=60&size=10&apikey=";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlLocation + BuildConfig.ticketmaster_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("RESPUESTA", response);
                    //llamar metodo con el resultado
                    viewTempoText.setText(response);
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
        if(stringRequest==null){
            Log.i("query", "stringReq is null");
        }
        queue.add(stringRequest);
    }

}
