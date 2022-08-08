package com.example.tfg_application.ui;

import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BigEvent extends AppCompatActivity {

    private boolean favorito;
    private BigEventBinding binding;
    private final String TAG = "BigEvent";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BigEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            setPageView(extras);
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
            try {
                jsonArrayGallery = new JSONArray(galleryPhotos);
                for (int i = 0; i < jsonArrayGallery.length(); i++) {
                   JSONObject explrObject = jsonArrayGallery.getJSONObject(i);
                    Log.i("event", "IMAGEN: " + i + ": " + explrObject);
                }
            } catch (JSONException e) {e.printStackTrace();}

            JSONArray galleryJSONArray = new JSONArray();
            galleryJSONArray.put(galleryPhotos);
            if(galleryPhotos!=null && jsonArrayGallery.length()>=2){
                try {
                    Glide.with(binding.galleryImageView).load(jsonArrayGallery.getJSONObject(1).getString("url")).into(binding.galleryImageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //binding.topImageView.setText(extras.getString("shortDate"));

            Glide.with(binding.topImageView).load(extras.getString("mainPhoto")).into(binding.topImageView);
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
        //Revisar: aquí s'haurie de mirar a les sharedPreferences si el event ya esta a favoritos
        //i actualitzar una variable aquí que servirà per cambiar el icono de la toolbar
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == R.id.go_back){
            goBack();
        }
        else if(item.getItemId() == R.id.toolbarButtonFav){
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
