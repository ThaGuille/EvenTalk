package com.example.tfg_application.ui.home.tabs;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.tfg_application.MainActivity;
import com.example.tfg_application.R;
import com.example.tfg_application.SignInActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
public class Configuration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TextView textDeleteAccount = findViewById(R.id.textDeleteAccount);
        SpannableString deleteAccountUnderline = new SpannableString("Borrar cuenta de EvenTalk");
        deleteAccountUnderline.setSpan(new UnderlineSpan(),0,deleteAccountUnderline.length(),0);
        textDeleteAccount.setText(deleteAccountUnderline);
        textDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });
        ImageView goBack = findViewById(R.id.arrow_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        initilizeToolbar(menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void initilizeToolbar(Menu menu){
        Log.i("taskbar", "wtf");
        //menu.findItem(R.id.go_back).setEnabled(false);
        //menu.findItem(R.id.go_back).setChecked(true);
        //menu.findItem(R.id.proba).setVisible(false);
        //Log.i("taskbar", "paso 0");

        //ERROR A LA SEGUENT LINIA
        /*getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);*/
        //ViewGroup v = (ViewGroup)LayoutInflater.from(this)
        //        .inflate(R.menu.toolbar, null);
        /*Log.i("taskbar", "paso 1");

            LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.i("taskbar", "paso 2");
            @SuppressLint("ResourceType") View b = inflator.inflate(R.menu.toolbar, null);
        Log.i("taskbar", "paso 3");
            getActionBar().setCustomView(b, new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL | Gravity.START));*/

    }

    //Revisar: el intent no es necessari ya que usem el activity stack per tornar
    private void goBack() {
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
    }
    private void deleteAccount(){
        mAuth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(this,gso);
        gsc.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signOut();
                    }
                });
    }
    private void signOut(){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

}
