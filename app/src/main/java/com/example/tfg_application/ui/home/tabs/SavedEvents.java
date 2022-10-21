package com.example.tfg_application.ui.home.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_application.R;
import com.example.tfg_application.databinding.*;
import com.example.tfg_application.ui.dashboard.EventAdapter;
import com.example.tfg_application.ui.dashboard.model.Event;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import android.content.Context;

public class SavedEvents  extends AppCompatActivity {

    private ActivitySavedEventsBinding binding;
    final String TAG = "savedEvents";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private RecyclerView recyclerView;
    private static EventAdapter adapter;
    private static List<Event> mEventList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = binding.savedEventsLayoutRecyclerView;
        adapter = new EventAdapter(mEventList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPref = getSharedPreferences("com.example.tfg_application.saved_events", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        mEventList.clear();

        //Obtenim totes les claus (id) dels events que tenim guardats
        //I creem una llista d'events amb
        Map<String, ?> eventos = sharedPref.getAll();
        for(Map.Entry<String, ?> x:eventos.entrySet()){
            Log.i(TAG, "key:  "+ x.getKey());
            Log.i(TAG, "value:  "+ x.getValue());

            Gson gson = new Gson();
            String jsonEvent = (String) x.getValue();
            if(jsonEvent==null){ return;}
            Event mEvent = gson.fromJson(jsonEvent, Event.class);
            Log.i(TAG, "event with json name " + mEvent.name);
            mEventList.add(mEvent);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        initilizeToolbar(menu);
        MenuItem favItem = menu.findItem(R.id.toolbarButtonFav);
        favItem.setVisible(false);
        MenuItem shareItem = menu.findItem(R.id.toolbarButtonShare);
        shareItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.go_back) {
            goBack();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initilizeToolbar(Menu menu) {
        //editar la toolbar
    }

    //no se si rebra els events en [] o ya en llista
    public static void changeEvents(Event[] events){
        List<Event> eventList = Arrays.asList(events);
        mEventList.clear();
        mEventList.addAll(eventList);
        adapter.notifyDataSetChanged();
    }

    private void goBack() {
        finish();
    }
}
