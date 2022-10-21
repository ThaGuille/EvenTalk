package com.example.tfg_application.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.tfg_application.MainActivity;
import com.example.tfg_application.R;
import com.example.tfg_application.RegisterActivity;
import com.example.tfg_application.SignInActivity;
import com.example.tfg_application.User;
import com.example.tfg_application.databinding.FragmentHomeBinding;
import com.example.tfg_application.ui.home.tabs.Configuration;
import com.example.tfg_application.ui.home.tabs.SavedEvents;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String TAG = "HomeFragment";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final LinearLayout preferences = binding.profileLayout;
        final LinearLayout configuration = binding.configurationLayout;
        final LinearLayout savedEvents = binding.savedEventsLayout;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
        if(user.isEmailVerified()){
            binding.textVerifyEmail.setText("");
        }else{
            binding.textVerifyEmail.setText(R.string.home_email_not_verified);
            binding.textVerifyEmail.setTextColor(getResources().getColor(R.color.colorRed) );
            binding.textVerifyEmail.setTextColor(getResources().getColor(R.color.colorScarletRed) );
        }
            binding.userName.setText(user.getDisplayName());
            if(user.getPhotoUrl()==null){
                binding.profileImage.setColorFilter(ContextCompat.getColor(getContext(),
                        R.color.owl_blue_satured_clear), android.graphics.PorterDuff.Mode.MULTIPLY);
            }else {
                Glide.with(binding.profileImage).load(user.getPhotoUrl()).into(binding.profileImage);
            }
        }else binding.userName.setText("ANONYMOUS"); //per si futurament es volen posar usuaris sense registrar
        preferences.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast toastPrueba = Toast.makeText(getContext(), "Próximamente...", Toast.LENGTH_SHORT);
                toastPrueba.show();

            }
        });

        configuration.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openLayout(Configuration.class);
            }
        });
        savedEvents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openLayout(SavedEvents.class);
            }
        });
        ImageButton btnLogout = binding.btnLogout;
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
        if(user.isEmailVerified()){
            binding.textVerifyEmail.setText("");
        }
            user.reload();

                //Sitema per a guardar usuaris a la DB, en un futur...

                /*FirebaseUser currentUser = auth.getCurrentUser();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("users").child(currentUser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Aquest mètode no s'execute correctament, s'haura de eliminar
                            Log.i(TAG, "onDataChanged");
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                binding.userName.setText(user.firstName);
                                //tvLastName.setText("Last Name: " + user.lastName);
                                //tvEmail.setText("Email: " + user.email);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });*/

                //User usuario = database.getReference("users").getClass(User.class);
                //es pot fer agafant els elements de la classe User, amb una mescla del que es fa a ChatFragment i al mètode anterior
                //o es pot actualitzar correctament el perfil amb pdateProfile() i assignar valors al nom, correu, foto...
                /*binding.userName.setText(user.getDisplayName());
            if(user.getPhotoUrl().toString().isEmpty()){
                binding.profileImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.owl_blue_satured_clear), android.graphics.PorterDuff.Mode.MULTIPLY);
            }else {
                Glide.with(binding.profileImage).load(user.getPhotoUrl()).into(binding.profileImage);
            }*/
            }
            else binding.userName.setText("ANONYMOUS");

    }

    public void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void openLayout(Class classe){
        Intent intent = new Intent(getActivity(), classe);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}