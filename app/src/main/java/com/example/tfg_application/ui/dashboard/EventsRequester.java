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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import ch.hsr.geohash.GeoHash;

import com.example.tfg_application.BuildConfig;
import com.example.tfg_application.R;

import org.json.JSONArray;
import org.json.JSONObject;

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
    //private JSONArray jsonArray;

    public void getEvent(String query, Context context, Location location, TextView viewTempoText){
        Log.i("events", "EventsRequester location " + location);
        RequestQueue queue = Volley.newRequestQueue(context);

        //La url es cree a partir de la query passada
        if(queue==null){
            Log.i("query", "queue is null");
        }

        String url = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=ES&apikey=";

        //query a la api de ticketmaster, com no estan fets els filtres se'n fa una predeterminada amb la ubicacio de l'usuari
        //format per inclore paraula, localitzacio i radi
        //String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);
        String urlLocation = "https://app.ticketmaster.com/discovery/v2/events.json?latlong="+location.getLatitude()+","+location.getLongitude()+ "&radius=60&size=10&apikey=";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlLocation + BuildConfig.ticketmaster_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("events", "respuesta en string: " + response);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject embedded = jsonObject.getJSONObject("_embedded");
                    JSONArray events = embedded.getJSONArray("events");
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject event = events.getJSONObject(i);
                        processEvent(event);
                        System.out.println(event);
                        String name = event.getString("name");
                        Log.i("events", "the name of this events is: " + name);
                    }
                    //Log.i("events", "juan" + response.length() + "juan");
                    //llamar metodo con el resultado
                    //viewTempoText.setText(response);
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
        Log.i("events", "se acabó");

        queue.add(stringRequest);
    }

    private void processEvent(JSONObject event){
        try {
            String id = event.getString("id");
            String name = event.getString("name");
            String type = event.getString("type");
            String url = event.getString("url");
            String images = event.getString("images"); //dins de images:[{"url": lo que volem}{"url": lo que volem}]
            //String dates = event.getString("dates");
            String location = event.getString("location");

        }catch (Exception e){
            Log.i("EXCEPTION", e.getMessage());
        }

    }

}



//No es pot agafar directament en format JSON, s'ha de transformar a posteriori.
        /*JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlLocation + BuildConfig.ticketmaster_api, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.i("RESPUESTA", response.);
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject event = response.getJSONObject(i);
                        System.out.println(event);
                    }
                    //llamar metodo con el resultado
                    //viewTempoText.setText(response);
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
