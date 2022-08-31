package com.example.tfg_application.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tfg_application.MainActivity;
import com.example.tfg_application.R;
import com.example.tfg_application.databinding.BigEventBinding;
import com.example.tfg_application.databinding.FragmentHomeBinding;
import com.example.tfg_application.ui.dashboard.model.Event;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BigEvent extends AppCompatActivity {

    private boolean favorito;
    private BigEventBinding binding;
    private final String TAG = "BigEvent";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private Event event;
    Bundle extras;
    String stringEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BigEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPref = getSharedPreferences("com.example.tfg_application.saved_events", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        extras = getIntent().getExtras();
        //Si ese evento ya ha sido guardado:


        //---------------Donem valors als elements de la pantalla del event en gran---------------------------------//
        if(extras!=null){
            setPageView(extras);
            event =(Event) extras.getSerializable("event");
            stringEvent = extras.getString("jsonEvent");
            JSONObject JSONEvent = null;
            try {
                JSONEvent = new JSONObject(stringEvent);
            } catch (JSONException e) {
                //Revisar: saltar un error gordo o un missatge predeterminat al títol de "not found"
                binding.titleTextView.setText("EVENT NOT FOUND");
                e.printStackTrace();
            }if(JSONEvent==null)return;
            try {
                Log.i(TAG, "title: " + JSONEvent.getString("name"));
                //binding.titleTextView.setText(JSONEvent.getString("name"));
                binding.titleTextView.setText(event.name);
                binding.dateTextView.setText(JSONEvent.getString("shortDate"));
                binding.hourTextView.setText(JSONEvent.getString("startDateTime") + " - "+extras.getString("endDateTime"));
                binding.locationTextView.setText(JSONEvent.getString("place"));
                //price a part

                //Revisar: sistema per trobar la imatge correcta (en ratio --> uns 4:3 O 16:9) i (en width > 600 i height > 350)
                //String galleryPhotos = JSONEvent.getString("images");
                JSONArray jsonArrayGallery = JSONEvent.getJSONArray("images");
                Log.i(TAG, "jsonArrayGallery: " + jsonArrayGallery);
                //JSONArray jsonArrayGallery = null;
                int galleryImageIndex1=-1;
                int galleryImageIndex2=-1;
                //---------------Sistema de filtres per agafar imatges d'una mida mínima i ratio bo---------------------------------//
                try {
                    //jsonArrayGallery = new JSONArray(galleryPhotos);
                    for (int i = 0; i < jsonArrayGallery.length(); i++) {
                        JSONObject explrObject = jsonArrayGallery.getJSONObject(i);
                        Log.i("event", "IMAGEN: " + i + ": " + explrObject);
                        if(galleryImageIndex1==-1){
                            //Revisar: molt de tant en tant algu no pose el ratio de una imatge i no surten
                            if((explrObject.getString("ratio").equals("16_9") || explrObject.getString("ratio").equals("4_3")) && explrObject.getInt("width") >= 600 ){
                                galleryImageIndex1 = i;
                                Log.i("event", "imatge seleccionada: " + i + ": " + explrObject.getString("ratio") + "width: " + explrObject.getInt("width"));
                            }}else if(galleryImageIndex1!=i){
                            if(galleryImageIndex2==-1){
                                if((explrObject.getString("ratio").equals("16_9") || explrObject.getString("ratio").equals("4_3")) && explrObject.getInt("width") >= 600){
                                    galleryImageIndex2 = i;
                                    Log.i("event", "imatge 2 seleccionada: " + i + ": " + explrObject.getString("ratio") + "width: " + explrObject.getInt("width"));
                                }
                            }
                        }
                    }
                } catch (JSONException e) {e.printStackTrace();}
                if(galleryImageIndex2==-1)galleryImageIndex2=galleryImageIndex1;
                //JSONArray galleryJSONArray = new JSONArray();
                //galleryJSONArray.put(galleryPhotos);
                if(jsonArrayGallery!=null && jsonArrayGallery.length()>=2){
                    try {
                        Glide.with(binding.galleryImageView).load(jsonArrayGallery.getJSONObject(galleryImageIndex2).getString("url")).into(binding.galleryImageView);
                        Log.i(TAG, "IMAGE 1 URL: " + jsonArrayGallery.getJSONObject(galleryImageIndex2).getString("url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Glide.with(binding.topImageView).load(jsonArrayGallery.getJSONObject(galleryImageIndex1).getString("url")).into(binding.topImageView);
                    Log.i(TAG, "IMAGE 2 URL: " + jsonArrayGallery.getJSONObject(galleryImageIndex1).getString("url"));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //----try/catch a part per al price, que no sempre esta--------
            String price = null;
            try {
                price = JSONEvent.getString("price");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(price!=null) binding.priceTextView.setText(price);
            else binding.priceTextView.setText("Click here to more info");



/*          Revisar: eliminar
            Log.i(TAG, "big event title: " + extras.getString("title"));
            binding.titleTextView.setText(extras.getString("title"));
            binding.dateTextView.setText(extras.getString("shortDate"));
            binding.hourTextView.setText(extras.getString("startDateTime") + " - "+extras.getString("endDateTime"));
            binding.locationTextView.setText(extras.getString("place"));
            String price = extras.getString("price");
            if(price!=null) binding.priceTextView.setText(price);
            else binding.priceTextView.setText("Click here to moer info");
            //Revisar: sistema per trobar la imatge correcta (en ratio --> uns 4:3 O 16:9) i (en width > 600 i height > 350)
            String galleryPhotos = extras.getString("galleryPhotos");
            JSONArray jsonArrayGallery = null;
            int galleryImageIndex1=-1;
            int galleryImageIndex2=-1;
            //---------------Sistema de filtres per agafar imatges d'una mida mínima i ratio bo---------------------------------//
            try {
                jsonArrayGallery = new JSONArray(galleryPhotos);
                for (int i = 0; i < jsonArrayGallery.length(); i++) {
                   JSONObject explrObject = jsonArrayGallery.getJSONObject(i);
                    Log.i("event", "IMAGEN: " + i + ": " + explrObject);
                    if(galleryImageIndex1==-1){
                    if((explrObject.getString("ratio").equals("16_9") || explrObject.getString("ratio").equals("4_3")) && explrObject.getInt("width") >= 600 ){
                        galleryImageIndex1 = i;
                        Log.i("event", "imatge seleccionada: " + i + ": " + explrObject.getString("ratio") + "width: " + explrObject.getInt("width"));
                    }}else{
                        if(galleryImageIndex2==-1){
                            if((explrObject.getString("ratio").equals("16_9") || explrObject.getString("ratio").equals("4_3")) && explrObject.getInt("width") >= 600){
                                galleryImageIndex2 = i;
                                Log.i("event", "imatge 2 seleccionada: " + i + ": " + explrObject.getString("ratio") + "width: " + explrObject.getInt("width"));
                            }
                        }
                    }
                }
            } catch (JSONException e) {e.printStackTrace();}
            if(galleryImageIndex2==-1)galleryImageIndex2=galleryImageIndex1;
            JSONArray galleryJSONArray = new JSONArray();
            galleryJSONArray.put(galleryPhotos);
            if(galleryPhotos!=null && jsonArrayGallery.length()>=2){
                try {
                    Glide.with(binding.galleryImageView).load(jsonArrayGallery.getJSONObject(galleryImageIndex2).getString("url")).into(binding.galleryImageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //binding.topImageView.setText(extras.getString("shortDate"));

            //Revisar: eliminar el sistema de mainPhoto ya que tenim el nou filtre
            //Glide.with(binding.topImageView).load(extras.getString("mainPhoto")).into(binding.topImageView);
            try {
                Glide.with(binding.topImageView).load(jsonArrayGallery.getJSONObject(galleryImageIndex1).getString("url")).into(binding.topImageView);
            }catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    }

    /*public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BigEventBinding binding = BigEventBinding.inflate(inflater, container, false);
        binding.titleTextView.setText("alfombra");
        Log.i(TAG, "big event binded");
        return binding.getRoot();
    }*/


    private void setPageView(Bundle event){

    }

    private void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void setFavouriteIcon(MenuItem item){
        int id ;
        if(favorito == false) id = R.drawable.ic_baseline_favorite_border_24;
        else id = R.drawable.ic_baseline_favorite_24;
        item.setIcon(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        Gson gson = new Gson();
        //String json = sharedPref.getString(event.id, "fail");
        //Event obj = gson.fromJson(json, Event.class);
        //if( !(sharedPref.getString(event.id, "fail").equals("fail"))){
        //Comprove si existeix un arxiu a sharedPreferences amb el nom de l'event. Si existeix, coloreje l'icono
        if( !(sharedPref.getString(event.id, "fail").equals("fail"))){
            favorito=true;
            MenuItem favItem = menu.findItem(R.id.toolbarButtonFav);
            setFavouriteIcon(favItem);
        }
        //Revisar: aquí s'haurie de mirar a les sharedPreferences si el event ya esta a favoritos
        //i actualitzar una variable aquí que servirà per cambiar el icono de la toolbar
        return super.onCreateOptionsMenu(menu);
    }

    //---------------funcio per interactuar amb la topbar-----------------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == R.id.go_back){
            goBack();
        }
        else if(item.getItemId() == R.id.toolbarButtonFav){
            if(!favorito){
                //Revisar: añadir límite de eventos guardados, en un sharedPref controlador
                //añadir a las prefs
                //Log.i(TAG, "EXTRAS: "+ extras.toString());
                //editor.put
                Gson gson = new Gson();
                String jsonEvent = gson.toJson(event);
                //key: event id, value: all event in json format
                editor.putString(event.id, jsonEvent);

                //editor.putString(extras.getString("id"), extras.toString());
                //editor.commit();
                editor.apply();
                Log.i(TAG, "event added "+ event.id+ " added");
                Log.i(TAG, "json: "+ jsonEvent.toString());
            }else{
                editor.remove(event.id);
                editor.apply();
                Log.i(TAG, "event "+ event.id+ " removed");
                //eliminar de las prefs
            }
            favorito= !favorito;
            setFavouriteIcon(item);


        } else if (item.getItemId() == R.id.toolbarButtonShare) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "linkk");
            startActivity(shareIntent);
        }
            return super.onOptionsItemSelected(item);
    }


}
