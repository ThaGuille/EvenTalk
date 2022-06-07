package com.example.tfg_application.ui.dashboard.model;

import android.location.Location;

import org.json.JSONObject;

public class Event {
    private String id;
    private String name;
    //private String description;
    private String type;
    private String url;
    private String[] imagesUrl;
    private double distance;
    private Location location;
    //private Object dates;

    public JSONObject toJSONObject(){
        JSONObject obj = new JSONObject();
        try{
            obj.put("id", id);
            obj.put("name",name );
            obj.put("type", type);
            obj.put("url",url);
            obj.put("imagesUrl",imagesUrl);
            obj.put("distance",distance);
            obj.put("location",location);
            //obj.put("description",description);
            // obj.put("dates",dates);

        }catch (Exception e){
            e.printStackTrace();
        }
        return obj;
    }

    //builder pattern
    public static class EventBuilder {
        private String id;
        private String name;
        //private String description;
        private String type;
        private String url;
        private String[] imagesUrl;
        private double distance;
        private Location location;

        //Per posar tots els valors a la vegada
        public EventBuilder(String id, String name, String type, String url, String[] imagesUrl, double distance, Location location) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.url = url;
            this.imagesUrl = imagesUrl;
            this.distance = distance;
            this.location = location;
        }


        //private Object dates;

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
        public EventBuilder setEventImagesUrl(String[] imagesUrl) {
            this.imagesUrl = imagesUrl;
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

    }

    //Per posar valors amb un objecte eventBuilder
    public Event(EventBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.url = builder.url;
        this.imagesUrl = builder.imagesUrl;
        this.distance = builder.distance;
        this.location = builder.location;

        // when build the item we get
       /* public static void main(String[] args) {
            Event event = new Event.EventBuilder().setAddress("abc").setDistance(19).build();
        }*/
}}
