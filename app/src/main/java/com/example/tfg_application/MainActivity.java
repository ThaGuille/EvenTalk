package com.example.tfg_application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/*revisar import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;*/
import com.example.tfg_application.ui.dashboard.model.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tfg_application.databinding.ActivityMainBinding;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 99;
    //private MessageAdapter adapter;
    //private ActivityResultContracts.OpenDocument openDocument; comentat fins posar immatges

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if(getIntent().getExtras()!=null) {
            try {
                int intentFragment = getIntent().getExtras().getInt("fragment");
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_home, true)
                        .build();
                Bundle bundle = new Bundle();
                switch (intentFragment) {
                    case 3:
                        Event event = (Event) getIntent().getExtras().getSerializable("event");
                        bundle.putSerializable("event", event);
                        Navigation.findNavController(findViewById(R.id.profile_layout)).navigate
                                (R.id.action_navigation_home_to_navigation_map, bundle, navOptions);
                        //Navigation.findNavController(v).clearBackStack(R.id.action_navigation_events_to_navigation_chat);
                    case 4:
                        bundle.putString("event", getIntent().getExtras().getString("id"));
                        bundle.putString("name", getIntent().getExtras().getString("name"));
                        Navigation.findNavController(findViewById(R.id.profile_layout)).navigate
                                (R.id.action_navigation_home_to_navigation_chat, bundle, navOptions);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_events, R.id.navigation_map, R.id.navigation_chat)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //Això sol s'active si l'usuari decline el permís d'ubicació
        /*if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            Toast.makeText(this, "Location", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), "El permiso de ubicación es necesario para el correcto funcionamiento de la aplicación", Snackbar.LENGTH_LONG)
                    .setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startLocationPermissionRequest();
                        }
                    });
        }else{
            startLocationPermissionRequest();
        }*/

    }

    public void goToMap(Event event){

    }

    @Override
    protected void onStart(){
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        }
    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("permission", "Displaying permission rationale to provide additional context.");
            Snackbar.make(findViewById(android.R.id.content), "El permiso de ubicación es " +
                            "necesario para el correcto funcionamiento de la aplicación", Snackbar.LENGTH_LONG)
                    .setAction("Dar permiso", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startLocationPermissionRequest();
                        }
                    }).show();

        } else {
            Log.i("permission", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions, grantResults);
        Log.i("permission", "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Snackbar.make(findViewById(android.R.id.content), "Ha habido un error otorgando el permiso", Snackbar.LENGTH_LONG).show();
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i("permission", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            } else {
                // Permission denied.
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(findViewById(android.R.id.content), "El permiso no se ha otorgado correctamente", Snackbar.LENGTH_LONG)
                        .setAction("Dar permiso", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }

}