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
        //setContentView(R.layout.activity_saved_events);
        //SmallEventBinding seb1 =  binding.smallEvent1;
        //seb1.eventButtonChat
        sharedPref = getSharedPreferences("com.example.tfg_application.saved_events", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        mEventList.clear();
        //Obtenim totes les claus (id) dels events que tenim guardats
        //Vull guardar events com a tal i no cam a fking Strings.

        Map<String, ?> evento1 = sharedPref.getAll();
        for(Map.Entry<String, ?> x:evento1.entrySet()){
            //i passem estes llistes a classe Events
            Log.i(TAG, "key:  "+ x.getKey());
            Log.i(TAG, "value:  "+ x.getValue());
            //Event mEvent;
            //Event.EventBuilder mEventBuilder;
            //mEventBuilder = new Event.EventBuilder();
            //mEventBuilder.tryToPasrseAll((String) x.getValue());
            //mEvent = new Event(mEventBuilder);
            Gson gson = new Gson();
            //String json = sharedPref.getString(x.getKey(), "fail");
            String jsonEvent = (String) x.getValue();
            if(jsonEvent==null){ return;}
            Event mEvent = gson.fromJson(jsonEvent, Event.class);
            Log.i(TAG, "event with json name " + mEvent.name);
            mEventList.add(mEvent);
        }

            //mEventList.add(mEvent);
            //System.out.println(x.getKey());
            //System.out.println(x.getValue());

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        initilizeToolbar(menu);
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
