package com.example.tfg_application.ui.dashboard.model;

import android.location.Location;
import android.util.Log;
import android.widget.Toolbar;

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
    //public Location location; no es serializable
    public Double[] location = new Double[2];
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
        //private Location location;  no es pot perque no es Serializable
        public Double[] location = new Double[2];
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
            Log.i("Event", "EVENT LOCATION:" + location);
            this.location[0] = location.getLatitude();
            this.location[1] = location.getLongitude();
            Log.i("Event", "EVENT LOCATION LATITUDE:" + this.location[0]);
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
        public Event build(){
            return new Event(this);
        }

       /* public static Location locationFromString(String location){
            if (location != null && (location.contains(","))) {
                Location result = new Location("Generated_location");
                String[] locationStrings = location.split(",");
                if (locationStrings.length >= 2) {
                    result.setLatitude(Double.parseDouble(locationStrings[1]));
                    result.setLongitude((Double.parseDouble(locationStrings[2])));
                    return result;
                } else { return null; }
            } else  return null;
        }*/
    }

    //Per posar valors amb un objecte eventBuilder
    public Event(EventBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.url = builder.url;
        this.images = builder.images;
        this.mainImage = builder.mainImage;
        this.distance = builder.distance;
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
