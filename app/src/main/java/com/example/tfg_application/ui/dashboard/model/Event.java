package com.example.tfg_application.ui.dashboard.model;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Serializable {
    //Revisar: no se si aixo haurie de ser public
    public String id;
    public String name;
    public String description;
    public String type;
    public String url;
    public String images;
    public String mainImage;
    public double distance = -1;
    //public Location location;
    public Double[] location = new Double[2];
    //public Double longitude;
    public Date date;
    public String shortDate;
    public String startDateTime;
    public String endDateTime;
    public String price;
    public String place;

    public JSONObject toJSONObject(){
        JSONObject obj = new JSONObject();
        try{
            //Elementos que siempre deben estar
            obj.put("id", id);
            obj.put("name",name);
            obj.put("type", type);
            obj.put("url",url);
            //obj.put("images",images);

            //Elementos que pueden no estar
            if(distance!=-1){obj.put("distance",distance);}
            //if(location!=null){obj.put("location",location);}
            if(location!=null) {obj.put("location", location);}
            if(date!=null){obj.put("date",date);}
            if(shortDate!=null && startDateTime!=null && endDateTime !=null){
                obj.put("shortDate", shortDate);
                obj.put("startDateTime", startDateTime);
                obj.put("endDateTime", endDateTime);
            }
            if(price!=null){
                obj.put("price", price);
            }
            if(place!=null){obj.put("place",place);}
            if(description!=null){obj.put("description",description);}


        }catch (Exception e){
            e.printStackTrace();
        }
        return obj;
    }

    //builder pattern
    public static class EventBuilder implements Serializable{
        private String id;
        private String name;
        private String description;
        private String type;
        private String url;
        private String images;
        private String mainImage;
        private double distance;
        //private Location location;
        public Double[] location;
        private Date date;
        private String place;
        public String shortDate;
        private String startDateTime;
        private String endDateTime;
        public String price;

        //EventBuilder bàsic
        public EventBuilder(String id, String name, String type, String url, JSONArray images) throws JSONException {
            Log.i("Event", "Standard eventBuilder start");
            this.id = id;
            this.name = name;
            this.type = type;
            this.url = url;
            this.images = images.toString();
            try {
                this.mainImage = images.getJSONObject(0).getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //EventBuilder + localització
        public EventBuilder(String id, String name, String type, String url, JSONArray images, double distance, Location location) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.url = url;
            this.images = images.toString();
            this.distance = distance;
            this.location[0] = location.getLatitude();
            this.location[1] = location.getLongitude();
            //this.location = location;
        }



        //Per posar valors un a un
        public Event build(){
            return new Event(this);
        }

        public EventBuilder(){}

        public EventBuilder setEventId(String id){
            this.id = id;
            return this;
        }
        public EventBuilder setEventName(String name){
            this.name = name;
            return this;
        }
        public EventBuilder setEventType(String type) {
            this.type = type;
            return this;
        }
        public EventBuilder setEventUrl(String url) {
            this.url = url;
            return this;
        }
        public EventBuilder setEventImagesUrl(JSONArray images) {
            this.images = images.toString();
            try {
                this.mainImage = images.getJSONObject(0).getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        public EventBuilder setEventMainImage(String mainImageimage){
            this.mainImage = mainImage;
            return this;
        }
        public EventBuilder setEventDistance(double distance) {
            this.distance = distance;
            return this;
        }
        public EventBuilder setEventLocation(Location location) {
            this.location[0] = location.getLatitude();
            this.location[1] = location.getLongitude();
            //this.location = location;
            return this;
        }
        public EventBuilder setEventDescription(String description) {
            this.description = description;
            return this;
        }
        public EventBuilder setEventDate(Date date) {
            this.date = date;
            return this;
        }
        public EventBuilder setEventPlace(String place) {
            this.place = place;
            return this;
        }
        public EventBuilder setShortDate(String shortDate){
            this.shortDate = shortDate;
            return this;
        }
        public EventBuilder setStartDateTime(String startDateTime){
            this.startDateTime = startDateTime;
            return this;
        }
        public EventBuilder setEndDateTime(String endDateTime){
            this.endDateTime = endDateTime;
            return this;
        }
        public EventBuilder setPrice(String price){
            this.price = price;
            return this;
        }

        /*public EventBuilder tryToPasrseAll(String fullEvent) {
            Log.i("savedEvents", "tryToParseAll: ");
            JSONObject JSONEvent = null;
            //String substring = fullEvent.substring(7, fullEvent.length()-1);
            String substring = fullEvent.substring(18, fullEvent.length()-2);
            Log.i("savedEvents", "event substring: " + substring);
            try {
                JSONEvent = new JSONObject(substring);
            } catch (JSONException e) {
                e.printStackTrace();
            }Log.i("savedEvents", "0");

            Log.i("savedEvents", "event JSON: " + JSONEvent);
            if (JSONEvent == null) return null;
            Log.i("savedEvents", "0.5");
            try {
                //Bundle[{jsonEvent={"id":"vvG1bZ9CcWooGU","name":"WWE Sunday Stunner","type":"event",
                // "url":"https:\/\/www.ticketmaster.com\/wwe-sunday-stunner-topeka-kansas-10-16-2022\/event\/06005CEB7D451336","distance":0,
                // "location":"Location[WWE Sunday Stunner 39,034593,-95,684822 hAcc=??? t=?!? et=?!? vAcc=??? sAcc=??? bAcc=???]",
                // "date":"Sun Oct 16 19:00:00 GMT+02:00 2022","shortDate":"2022-10-16","startDateTime":"19:00","endDateTime":"00:00","price":"15.0 - 95.0 USD",
                // "place":"One Expocentre Drive"}}]
                //Elementos que siempre deben estar
                this.name = JSONEvent.getString("name");
                this.id = JSONEvent.getString("id");
                this.type = JSONEvent.getString("type");
                this.url = JSONEvent.getString("url");
                this.images = JSONEvent.getString("images");
            } catch (JSONException e) {e.printStackTrace();}
            Log.i("savedEvents", "1");
            try {
                this.mainImage = JSONEvent.getString("mainImage");
            } catch (JSONException e) {e.printStackTrace();}
            try {
                this.distance = Double.parseDouble(JSONEvent.getString("distance"));
            } catch (JSONException e) {e.printStackTrace();}
            //Revisar com estar a l'event i guardarla igual
            try {
                this.location = locationFromString(JSONEvent.getString("location"));
                Log.i("savedEvents", "location: " + JSONEvent.getString("location"));
                Log.i("savedEvents", "location: "+ location.getLatitude() + ", " + location.getLongitude());
            } catch (JSONException e) {e.printStackTrace();}
            try {
                this.description = JSONEvent.getString("description");
            } catch (JSONException e) {e.printStackTrace();}
            Log.i("savedEvents", "2");
            try {
                Log.i("savedEvents", "3");
            //"date":"Fri Aug 26 18:40:00 GMT+02:00 2022"
                // String fechaOriginal  = localDate +" " + localTime;
                SimpleDateFormat formatoOrigen = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Log.i("savedEvents", "date string: "+ JSONEvent.getString("date"));
                Log.i("savedEvents", "4");
                Date date = formatoOrigen.parse(JSONEvent.getString("date")); //format Wed Jun 15 17:00:00 GMT 2022
                Log.i("savedEvents", "date: "+ date);
                this.date = date;
            } catch (JSONException | ParseException e) {e.printStackTrace();}
            try {
                this.shortDate = JSONEvent.getString("shortDate");
            } catch (JSONException e) {e.printStackTrace();}
            try {
                this.place = JSONEvent.getString("place");
            } catch (JSONException e) {e.printStackTrace();}
            try {
                this.startDateTime = JSONEvent.getString("startDateTime");
            }catch (JSONException e) {e.printStackTrace();}
            try {
                this.endDateTime = JSONEvent.getString("endDateTime");
            }catch (JSONException e) {e.printStackTrace();}
            try {
                this.price = JSONEvent.getString("price");
            }catch (JSONException e) {e.printStackTrace();}
            return this;
        }*/

        //Si no funcione, cambiar les posicions 1 i 2 per 0 i 1. Sino guardar la location com string i ya
        public static Location locationFromString(String location){
            if (location != null && (location.contains(","))) {
                Location result = new Location("Generated_location");
                String[] locationStrings = location.split(",");
                if (locationStrings.length >= 2) {
                    result.setLatitude(Double.parseDouble(locationStrings[1]));
                    result.setLongitude((Double.parseDouble(locationStrings[2])));
                    return result;
                } else { return null; }
            } else  return null;
        }
    }

    //Per posar valors amb un objecte eventBuilder
    public Event(EventBuilder builder) {
        Log.i("Event", "building event after EventBuilder");
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.url = builder.url;
        this.images = builder.images;
        this.mainImage = builder.mainImage;
        this.distance = builder.distance;
        //this.location = builder.location;
        this.description = builder.description;
        this.date = builder.date;
        this.shortDate = builder.shortDate;
        this.place = builder.place;;
        this.startDateTime = builder.startDateTime;
        this.endDateTime = builder.endDateTime;
        this.price = builder.price;
        this.location = builder.location;
        // when build the item we get
       /* public static void main(String[] args) {
            Event event = new Event.EventBuilder().setAddress("abc").setDistance(19).build();
        }*/
}}
