package com.example.tfg_application.ui.home;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tfg_application.MainActivity;
import com.example.tfg_application.R;
import com.example.tfg_application.RegisterActivity;
import com.example.tfg_application.SignInActivity;
import com.example.tfg_application.User;
import com.example.tfg_application.databinding.FragmentHomeBinding;
import com.example.tfg_application.ui.home.tabs.Configuration;
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final LinearLayout preferences = binding.preferencesLayout;
        final LinearLayout configuration = binding.configurationLayout;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
        if(user.isEmailVerified()){
            binding.textVerifyEmail.setText("");
        }else{
            binding.textVerifyEmail.setText("Email not verified");
            binding.textVerifyEmail.setTextColor(getResources().getColor(R.color.colorRed) );
        }}
        preferences.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast toastPrueba = Toast.makeText(getContext(), "es carguen les preferencies", Toast.LENGTH_SHORT);
                toastPrueba.show();

            }
        });
        configuration.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openLayout(Configuration.class);
            }
        });
        ImageButton btnLogout = binding.btnLogout;
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users").child(currentUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Aquest mètode no s'execute correctament, s'haura de eliminar
                    Log.i("onDataChange", "onDataChangedLoco");
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
            });
        }


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

                FirebaseUser currentUser = auth.getCurrentUser();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("users").child(currentUser.getUid());

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Aquest mètode no s'execute correctament, s'haura de eliminar
                            Log.i("onDataChange", "onDataChangedLoco2");
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
                    });

                //User usuario = database.getReference("users").getClass(User.class);
                //es pot fer agafant els elements de la classe User, amb una mescla del que es fa a ChatFragment i al mètode anterior
                //o es pot actualitzar correctament el perfil amb pdateProfile() i assignar valors al nom, correu, foto...
                Log.i("onResumenLoco", "onResumenLoco,"+ user.getDisplayName());
                binding.userName.setText(user.getDisplayName());
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
        //getActivity().finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}