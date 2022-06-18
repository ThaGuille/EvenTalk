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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import ch.hsr.geohash.GeoHash;

import com.example.tfg_application.BuildConfig;
import com.example.tfg_application.R;
import com.example.tfg_application.ui.dashboard.model.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventsRequester {

    /*Aquí s'ha de passar tot el codi per fer les peticions a la api, així com el sistema de filtres
    i classificació que segueixin les querys

    https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/

    La query sempre començe amb https://app.ticketmaster.com/discovery/v2/

    Just després se pose events.json? o attractions.json? o lo que vulguem

    a partir de aqui els filtres se posen tal que https://app.ticketmaster.com/discovery/v2/events.json?filtre=tal&filtre=qual
    acabat sempre amb &apikey=ZyOtcio7lAmQOX21ynhfQ0mmdFmCQ01J


    Important tema pàgines, si no especifiques res la response sol retorne la 1a pag. Al tag "page"
    Al final de tot podem buscar a "totalPages" el nº, i a "number" la pag actual.
    Tmb tenim el "size", que done elements per pagina i "totalElements".

    */
    //private GeoHash geoHash;
    //private JSONArray jsonArray;

    public void getEvent(String query, Context context, Location location, TextView viewTempoText){
        Log.i("events", "EventsRequester location " + location);
        RequestQueue queue = Volley.newRequestQueue(context);

        //La url es cree a partir de la query passada

        String url = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=ES&apikey=";

        //query a la api de ticketmaster, com no estan fets els filtres se'n fa una predeterminada amb la ubicacio de l'usuari
        //format per inclore paraula, localitzacio i radi
        //String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);
        //        String urlLocation = "https://app.ticketmaster.com/discovery/v2/events.json?latlong="+location.getLatitude()+","+location.getLongitude()+ "&radius=60&size=10&apikey=";
        String urlLocation = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=ES&apikey=";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlLocation + BuildConfig.ticketmaster_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("events", "respuesta en string: " + response);
                    processEvent(response);
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
        queue.add(stringRequest);


    }

    private void processEvent(String response){
        Event[] mAllEvents = new Event[0];

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject embedded = jsonObject.getJSONObject("_embedded");
            JSONObject page = jsonObject.getJSONObject("page");
            JSONArray events = embedded.getJSONArray("events");
            mAllEvents = new Event[events.length()];
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                String id ;
                String name ;
                String type;
                String url ;
                JSONArray images;
                JSONObject imagesJSON;
                String localDate;
                String localTime;
                String location;
                String place = null;
                Event mEvent;
                Event.EventBuilder mEventBuilder;
                Log.i("events", "process Event: " + i + ": " + event);
                //witeResult(event);
                int totalPages = page.getInt("totalPages");
                Log.i("events", "Toal pages: " + totalPages);
                try{
                    id = event.getString("id");
                    name = event.getString("name");
                    type = event.getString("type");
                    url = event.getString("url");
                    //images = event.getString("images"); //dins de images:[{"url": lo que volem}{"url": lo que volem}]
                    //Log.i("events","images: "+ images);
                    images = event.getJSONArray("images");
                    for(int k=0; k<images.length();k++){
                        Log.i("events","imagesJSONArray: "+ k + images.get(k));
                    }

                    mEventBuilder = new Event.EventBuilder(id, name, type, url, images);
                }catch (Exception e){
                    Log.i("events", "Error en la carga: " + e);
                    throw new RuntimeException(e);
                }
                try {
                    localDate = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
                    localTime = event.getJSONObject("dates").getJSONObject("start").getString("localTime");
                    //Log.i("events", "localDate: " + localDate);
                    //Log.i("events", "localTime: " + localTime);
                    SimpleDateFormat formatoOrigen = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String fechaOriginal  = localDate +" " + localTime;
                    SimpleDateFormat formatoDestino = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Date date = formatoOrigen.parse(fechaOriginal); //format Wed Jun 15 17:00:00 GMT 2022
                    Log.i("events", "Date parsed: " + date);
                    mEventBuilder.setEventDate(date);
                }catch (Exception e){
                    Log.i("events", "Error en la carga: " + e);
                }
                try{
                location = event.getString("location");
                //Falte passarla a Location mEventBuilder.setEventLocation(location);
                }
                catch (Exception ignored){}
                try{
                    place = event.getString("place");
                    mEventBuilder.setEventPlace(place);
                }catch (Exception ignored){}
                if(place==null){
                    try{
                        place = event.getString("address");
                        mEventBuilder.setEventPlace(place);
                    }catch (Exception ignored){}
                }
                Log.i("events", "proba 2");

                mEvent = new Event(mEventBuilder);
                Log.i("events", "mEvent: "  + mEvent);
                mAllEvents[i] = mEvent;
            }


        }catch (Exception e){
            Log.i("EXCEPTION", e.getMessage());
        }

        callEventAdapter(mAllEvents);
        //Llamar a EventAdapter
    }

    private void callEventAdapter(Event[] mAllEvents){
        if(mAllEvents.length == 0){return;}
        //Class df = DashboardFragment;

        //classe.getMethod(method, c)
        DashboardFragment.changeEvents(mAllEvents);
        //EventAdapter eventAdapter = new EventAdapter();
        //eventAdapter.setmAllEvents(mAllEvents);

        //Es pot vincular desde aqui o cridar un mètode de la DashboardFragment retornant el Event[]...
    }

}

 /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlLocation + BuildConfig.ticketmaster_api, null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("events", "response JSON : " + response.toString()) ;
                    //Log.i("events", "response JSON lenght: " + response.length()) ;
                    //Log.i("RESPUESTA", response);
                        //System.out.println(event);}catch (JSONException e){ throw new RuntimeException(e);}
                        //Log.i("events", "JSON event: " + response) ;
                    /*for (int i = 0; i < response.length(); i++) {
                        JSONObject event = response.getJSONObject(i);
                        Log.i("events", "JSON array response: " + i + ": " + event) ;
                    }*//*
                }
                catch (Exception e)
                {
                    Log.i("EXCEPTION", e.getMessage());
                    throw new RuntimeException(e);
                }
                    //llamar metodo con el resultado
                    //viewTempoText.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        });
        queue.add(jsonObjectRequest);*/

//No es pot agafar directament en format JSON, s'ha de transformar a posteriori.
       /*JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlLocation + BuildConfig.ticketmaster_api, null,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.i("events", "response JSON : " + response.toString()) ;
                    //Log.i("events", "response JSON lenght: " + response.length()) ;
                    //Log.i("RESPUESTA", response);
                        //System.out.println(event);}catch (JSONException e){ throw new RuntimeException(e);}
                        //Log.i("events", "JSON event: " + response) ;
                    /*for (int i = 0; i < response.length(); i++) {
                        JSONObject event = response.getJSONObject(i);
                        Log.i("events", "JSON array response: " + i + ": " + event) ;
                    }*//*
                }
                catch (Exception e)
                {
                    Log.i("EXCEPTION", e.getMessage());
                    throw new RuntimeException(e);
                }
                    //llamar metodo con el resultado
                    //viewTempoText.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        });
        queue.add(jsonArrayRequest);*/

/*
// Get geoPoint
  String geoHash = GeoHash.encodeGeohash(lat, lon, 8);

* try {
    keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
  } catch (Exception e) {
    e.printStackTrace();
  }*/

/*String veryLongString = event.toString();
                int maxLogSize = 1000;
                for(int j = 0; j <= veryLongString.length() / maxLogSize; j++) {
                    int start = j * maxLogSize;
                    int end = (j+1) * maxLogSize;
                    end = end > veryLongString.length() ? veryLongString.length() : end;
                    Log.v("events", veryLongString.substring(start, end));
                }*/