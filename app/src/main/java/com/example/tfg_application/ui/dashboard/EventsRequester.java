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

    private final String TAG = "eventsRequester";

    private Location location;
    private String radius;
    private String textFilter;
    private String typeFilter;
    private String city;
    private String countryCode;

    public void getEvent(Context context){
        Log.i(TAG, "EventsRequester location " + location);
        RequestQueue queue = Volley.newRequestQueue(context);

        //La url es cree a partir de la query passada

        String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey="+BuildConfig.ticketmaster_api;

        //query a la api de ticketmaster, com no estan fets els filtres se'n fa una predeterminada amb la ubicacio de l'usuari
        //format per inclore paraula, localitzacio i radi
        //String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);
        //        String urlLocation = "https://app.ticketmaster.com/discovery/v2/events.json?latlong="+location.getLatitude()+","+location.getLongitude()+ "&radius=60&size=10&apikey=";
        if(textFilter!=null){
            url += "&keyword="+textFilter;
        }
        if(city!=null)
            url+= "&city="+city;
        //Si hi ha medida "radius" es per filtrar
        if(location!=null & radius!=null)
            url+= "&latLong="+location.getLatitude()+","+ location.getLongitude()+ "&radius="+radius + "&unit=km";
        if(city!=null){
            url+="&city="+city;
        }
        if(countryCode!=null){
            url+="&countryCode="+countryCode;
        }
        Log.i(TAG, url);
        String urlLocation = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=ES&apikey=";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Log.i("events", "respuesta en string: " + response);
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
                String localDate;
                String localTime;
                Location location;
                String place = null;
                Event mEvent;
                Event.EventBuilder mEventBuilder;
                Log.i(TAG, "process Event: " + i + ": " + event);
                //witeResult(event);
                //no cal agafar-ho a tots els events
                int totalPages = page.getInt("totalPages");

                try{     // ----------------  ---------------------
                    id = event.getString("id");
                    name = event.getString("name");
                    type = event.getString("type");
                    url = event.getString("url");
                    //images = event.getString("images"); //dins de images:[{"url": lo que volem}{"url": lo que volem}]
                    //Log.i("events","images: "+ images);
                    images = event.getJSONArray("images");
                    /*for(int k=0; k<images.length();k++){
                        Log.i("events","imagesJSONArray: "+ k + images.get(k));
                    }*/
                    mEventBuilder = new Event.EventBuilder(id, name, type, url, images);
                }catch (Exception e){
                    Log.i(TAG, "Error en la carga: " + e);
                    throw new RuntimeException(e);
                }
                try {    // ---------------- FECHA ---------------------
                    localDate = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
                    localTime = event.getJSONObject("dates").getJSONObject("start").getString("localTime");
                    //Log.i("events", "localDate: " + localDate);
                    //Log.i("events", "localTime: " + localTime);
                    SimpleDateFormat formatoOrigen = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String fechaOriginal  = localDate +" " + localTime;
                    SimpleDateFormat formatoDestino = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Date date = formatoOrigen.parse(fechaOriginal); //format Wed Jun 15 17:00:00 GMT 2022
                    Log.i(TAG, "Date parsed: " + date);
                    mEventBuilder.setEventDate(date);
                }catch (Exception e){ Log.i("events", "Error en la carga: " + e); }
                try{
                    //No se pk les hores de start i end no quadren i surt la de start despres de la de end, pero haurie de ser com està fet.
                    String startDateTime = event.getJSONObject("dates").getJSONObject("start").getString("localTime");
                    String endDateTime = event.getJSONObject("dates").getJSONObject("start").getString("dateTime");
                    startDateTime = startDateTime.substring(0,5);
                    endDateTime = endDateTime.substring(11, 16);
                    String Date = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
                    Log.i("date", "Date: " + Date + ", start Time: " + startDateTime + ", end time: " + endDateTime);
                    SimpleDateFormat formatoAño = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formatoAño2 = new SimpleDateFormat("dd-MM-yyyy");
                    Log.i("date", "empieza");
                    Date temp = formatoAño.parse(Date);
                    Log.i("date", "any: "+formatoAño2.format(temp).toString()); //any2: 30-10-2022
                    //Log.i("date", "any3: "+formatoAño2.parse(formatoAño2.format(temp)));//any3: Thu Oct 27 00:00:00 GMT+02:00 2022
                    Log.i("date", "hora inici: "+ startDateTime); //Thu Jan 01 10:30:00 GMT+01:00 1970
                    Log.i("date", "hora final: "+endDateTime);
                    //mEventBuilder.setEventDate(date);
                    mEventBuilder.setShortDate(Date);
                    mEventBuilder.setStartDateTime(startDateTime);
                    mEventBuilder.setEndDateTime(endDateTime);
                }catch (Exception e){ Log.i(TAG, "Error en la carga: " + e); }
                try{
                    JSONArray temp = event.getJSONObject("_embedded").getJSONArray("venues");
                    place = temp.getJSONObject(0).getJSONObject("address").getString("line1");
                    mEventBuilder.setEventPlace(place);
                }catch (Exception e){Log.i(TAG, "Error en la carga: " + e);}

                try{
                    //JSONObject temp = event.getJSONObject("_embedded").getJSONObject("priceRanges");
                    //int minPrice = temp.getInt("min");
                    //Log.i("events", "PRECIO MINIMO: "+ minPrice);
                    JSONArray temp = event.getJSONArray("priceRanges");
                    boolean foundPrice = false;
                    if(temp.length()>0){
                    for(int j=0;j<temp.length();j++){
                        if(temp.getJSONObject(j).getString("type").equals("standard")){
                            foundPrice = true;
                            Log.i(TAG, "IT FOUND STANDARD PRICE: ");
                            String min = temp.getJSONObject(j).getString("min");
                            String max = temp.getJSONObject(j).getString("max");
                            String currency = temp.getJSONObject(j).getString("currency");
                            if(min.equals(max) && max.equals("0.0")){
                                mEventBuilder.setPrice("Free");
                            }
                            else if(min.equals(max)){
                                mEventBuilder.setPrice(min + " " +currency);
                            }else {
                                mEventBuilder.setPrice(min + " - " + max + " " +currency);
                            }
                        }
                    }}
                    if(!foundPrice) mEventBuilder.setPrice("Check lick");
                    Log.i(TAG, "AND IT DID NOT CRASH IT: ");
                    Log.i(TAG, "TYPE: "+ temp.getJSONObject(0).getString("type"));
                    Log.i(TAG, "PRICE: "+ temp.getJSONObject(0).getString("min") + " - " + temp.getJSONObject(0).getString("max"));
                    Log.i(TAG, "INDEX 2: "+ temp.getString(0));

                }catch (Exception e){Log.i("events", "Error en la carga: " + e);}
                try{
                    JSONArray temp = event.getJSONObject("_embedded").getJSONArray("venues");
                    //location = temp.getJSONObject(0).getString("location");
                    //Log.i("events", "location string: " + location);
                    Location mLocation = new Location(name);
                    JSONObject lol = temp.getJSONObject(0).getJSONObject("location");
                    mLocation.setLongitude(lol.getDouble("longitude"));
                    mLocation.setLatitude(lol.getDouble("latitude"));
                    location = mLocation;
                    //Log.i("events", "location Location: " + mLocation);
                    mEventBuilder.setEventLocation(location);
                }catch (Exception e){Log.i(TAG, "Error en la carga: " + e);}

                mEvent = new Event(mEventBuilder);
                mAllEvents[i] = mEvent;
            }


        }catch (Exception e){
            Log.i("EXCEPTION", e.getMessage());
        }

        callEventAdapter(mAllEvents);
        //Llamar a EventAdapter
    }

    public void setLocation(Location location){
        this.location = location;
    }
    public void setTextFilter(String text){
        this.textFilter = text;
    }
    public void setTypeFilter(String type){
        this.typeFilter = type;
    }
    public void setRadius(Integer radius){
        this.radius = radius.toString();
    }
    public void setCity(String city){
        this.city = city;
    }
    public void setCountryCode(String countryCode){
        this.countryCode = countryCode;
    }

    private void resetParameters(){
        this.location = null;
        this.textFilter = null;
        this.typeFilter = null;
        this.radius = null;
        this.city = null;
        this.countryCode = null;
    }


    private void callEventAdapter(Event[] mAllEvents){
        if(mAllEvents.length == 0){return;}
        //Class df = DashboardFragment;
        resetParameters();
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