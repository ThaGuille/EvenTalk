package com.example.tfg_application.ui;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
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

import java.util.Calendar;

public class BigEvent extends AppCompatActivity {

    private boolean favorito;
    private BigEventBinding binding;
    private final String TAG = "BigEvent";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private Event event;
    Bundle extras;
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
            if(event==null){
                binding.titleTextView.setText("EVENT NOT FOUND");
                return;
            }
            try {
                binding.titleTextView.setText(event.name);
                if(event.shortDate == null) binding.dateTextView.setText("sin información");
                else {
                    String tempDate = event.shortDate + "   " + event.startDateTime + " - " + event.endDateTime;
                    binding.dateTextView.setText(tempDate);}
                //binding.hourTextView.setText(event.startDateTime + " - " + event.endDateTime);
                binding.locationTextView.setText(event.place);

                loadImages();

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(event.price==null){binding.priceTextView.setText("Pulsa aquí para más información");}
            else{binding.priceTextView.setText(event.price);}

            //Funcio per guardar l'event al calendari
            binding.dateImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCalendar();
                }
            });
            binding.dateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCalendar();
                }
            });

            //Funcio per anar a la pagina web de l'event (a ticketmaster)
            binding.priceImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.url));
                    startActivity(browserIntent);
                }
            });
            binding.priceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.url));
                    startActivity(browserIntent);
                }
            });

        }
    }

    private void setPageView(Bundle event){}

    private void loadImages(){
        //Revisar: sistema per trobar la imatge correcta (en ratio --> uns 4:3 O 16:9) i (en width > 600 i height > 350)
        try {
            JSONArray jsonArrayGallery = new JSONArray(event.images);
            Log.i(TAG, "jsonArrayGallery: " + jsonArrayGallery);
            int galleryImageIndex1 = -1;
            int galleryImageIndex2 = -1;

            //---------------Sistema de filtres per agafar imatges d'una mida mínima i ratio bo---------------------------------//
            try {
                //jsonArrayGallery = new JSONArray(galleryPhotos);
                for (int i = 0; i < jsonArrayGallery.length(); i++) {
                    JSONObject explrObject = jsonArrayGallery.getJSONObject(i);
                    Log.i(TAG, "IMAGEN: " + i + ": " + explrObject);
                    if (galleryImageIndex1 == -1) {
                        //Revisar: molt de tant en tant algu no pose el ratio de una imatge i no surten
                        if ((explrObject.getString("ratio").equals("16_9") || explrObject.getString("ratio").equals("4_3")) && explrObject.getInt("width") >= 600) {
                            galleryImageIndex1 = i;
                            Log.i(TAG, "imatge seleccionada: " + i + ": " + explrObject.getString("ratio") + "width: " + explrObject.getInt("width"));
                        }
                    } else if (galleryImageIndex1 != i) {
                        if (galleryImageIndex2 == -1) {
                            if ((explrObject.getString("ratio").equals("16_9") || explrObject.getString("ratio").equals("4_3")) && explrObject.getInt("width") >= 600) {
                                galleryImageIndex2 = i;
                                Log.i(TAG, "imatge 2 seleccionada: " + i + ": " + explrObject.getString("ratio") + "width: " + explrObject.getInt("width"));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (galleryImageIndex2 == -1) galleryImageIndex2 = galleryImageIndex1;
            //JSONArray galleryJSONArray = new JSONArray();
            //galleryJSONArray.put(galleryPhotos);
            if (jsonArrayGallery != null && jsonArrayGallery.length() >= 2) {
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch (JSONException e) {
        e.printStackTrace();
    }
    }

    private void openCalendar(){
        Calendar beginTime = Calendar.getInstance();
        String[] dateTemp = event.shortDate.split("-");
        Log.i(TAG, "date splitted: " + dateTemp[0] +", "+dateTemp[2]);
        String[] dayTemp = event.startDateTime.split(":");
        //event.shortDate per yyy-mm-dd
        //event.startDateTime i event.endDateTime per hh:mm
        beginTime.set(Integer.parseInt(dateTemp[0]), Integer.parseInt(dateTemp[1]), Integer.parseInt(dateTemp[2]),  Integer.parseInt(dayTemp[0]),  Integer.parseInt(dayTemp[1]));
        Calendar endTime = Calendar.getInstance();
        dayTemp = event.endDateTime.split(":");
        Log.i(TAG, "hour splitted: " + dayTemp[0] +", "+dayTemp[1]);
        endTime.set(Integer.parseInt(dateTemp[0]), Integer.parseInt(dateTemp[1]), Integer.parseInt(dateTemp[2]),  Integer.parseInt(dayTemp[0]),  Integer.parseInt(dayTemp[1]));
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, event.name)
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
                //.putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.place)
                //.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, event.url);
        startActivity(intent);
    }
    private void setFavouriteIcon(MenuItem item){
        int id ;
        if(!favorito) id = R.drawable.ic_baseline_favorite_border_24;
        else id = R.drawable.ic_baseline_favorite_24;
        item.setIcon(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        //Comprove si existeix un arxiu a sharedPreferences amb el nom de l'event. Si existeix, coloreje l'icono
        if( !(sharedPref.getString(event.id, "fail").equals("fail"))){
            favorito=true;
            MenuItem favItem = menu.findItem(R.id.toolbarButtonFav);
            setFavouriteIcon(favItem);
        }
        return super.onCreateOptionsMenu(menu);
    }

    //---------------funcio per interactuar amb la topbar-----------------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.go_back){
            finish();
        }
        else if(item.getItemId() == R.id.toolbarButtonFav){
            if(!favorito){
                Gson gson = new Gson();
                String jsonEvent = gson.toJson(event);
                //key: event id, value: all event in json format
                editor.putString(event.id, jsonEvent);
                //editor.commit();
                editor.apply();
                Log.i(TAG, "event added "+ event.id+ " added");
            }else{
                editor.remove(event.id);
                editor.apply();
                Log.i(TAG, "event "+ event.id+ " removed");
            }
            favorito= !favorito;
            setFavouriteIcon(item);


        } else if (item.getItemId() == R.id.toolbarButtonShare) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, event.url);
            startActivity(shareIntent);
        }
            return super.onOptionsItemSelected(item);
    }
}
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
