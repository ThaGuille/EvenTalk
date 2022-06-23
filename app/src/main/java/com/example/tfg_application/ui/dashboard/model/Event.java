package com.example.tfg_application.ui.dashboard.model;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class Event {
    public String id;
    public String name;
    public String description;
    public String type;
    public String url;
    public JSONArray images;
    public double distance = -1;
    public Location location;
    public Date date;
    public String startDateTime;
    public String endDateTime;
    public String place;

    public JSONObject toJSONObject(){
        JSONObject obj = new JSONObject();
        try{
            //Elementos que siempre deben estar
            obj.put("id", id);
            obj.put("name",name);
            obj.put("type", type);
            obj.put("url",url);
            obj.put("images",images);

            //Elementos que pueden no estar
            if(distance!=-1){obj.put("distance",distance);}
            if(location!=null){obj.put("location",location);}
            if(date!=null){obj.put("date",date);}
            if(place!=null){obj.put("place",place);}
            if(description!=null){obj.put("description",description);}


        }catch (Exception e){
            e.printStackTrace();
        }
        return obj;
    }

    //builder pattern
    public static class EventBuilder {
        private String id;
        private String name;
        private String description;
        private String type;
        private String url;
        private JSONArray images;
        private double distance;
        private Location location;
        private Date date;
        private String place;
        private String startDateTime;
        private String endDateTime;


        //EventBuilder bàsic
        public EventBuilder(String id, String name, String type, String url, JSONArray images) {
            Log.i("Event", "Standard eventBuilder start");
            this.id = id;
            this.name = name;
            this.type = type;
            this.url = url;
            this.images = images;
        }

        //EventBuilder + localització
        public EventBuilder(String id, String name, String type, String url, JSONArray images, double distance, Location location) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.url = url;
            this.images = images;
            this.distance = distance;
            this.location = location;
        }



        //Per posar valors un a un
        public Event build(){
            return new Event(this);
        }

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
            this.images = images;
            return this;
        }
        public EventBuilder setEventDistance(double distance) {
            this.distance = distance;
            return this;
        }
        public EventBuilder setEventLocation(Location location) {
            this.location = location;
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
        public EventBuilder setStartDateTime(String startDateTime){
            this.startDateTime = startDateTime;
            return this;
        }
        public EventBuilder setEndDateTime(String endDateTime){
            this.endDateTime = endDateTime;
            return this;
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
        this.distance = builder.distance;
        this.location = builder.location;
        this.description = builder.description;
        this.date = builder.date;
        this.place = builder.place;;
        this.startDateTime = builder.startDateTime;
        this.endDateTime = builder.endDateTime;

        // when build the item we get
       /* public static void main(String[] args) {
            Event event = new Event.EventBuilder().setAddress("abc").setDistance(19).build();
        }*/
}}
