package com.example.tfg_application.ui.dashboard;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tfg_application.R;
import com.example.tfg_application.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private LinearLayout.LayoutParams param1,param2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        param1 = (LinearLayout.LayoutParams)binding.underline1.getLayoutParams();
        param2 = (LinearLayout.LayoutParams)binding.underline2.getLayoutParams();
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final View popular = binding.orderByPopular;
        popular.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setUnderlineActive(binding.underline1);
            }
        });
        final View distance = binding.orderByDistance;
        distance.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setUnderlineActive(binding.underline2);
            }
        });
        final View recommended = binding.orderByRecommended;
        recommended.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setUnderlineActive(binding.underline3);
            }
        });

        return root;
    }

    //No funcione amb fragments crec...
    /*public void clickPopulares(View view){
        Toast toastPrueba = Toast.makeText(getContext(), "populares", Toast.LENGTH_SHORT);
        toastPrueba.show();
        //setUnderlineActive(binding.underline1);
    }*/


    private void setUnderlineActive(View v){
        binding.underline1.setBackgroundColor(getResources().getColor(R.color.gray));
        binding.underline2.setBackgroundColor(getResources().getColor(R.color.gray));
        binding.underline3.setBackgroundColor(getResources().getColor(R.color.gray));
        v.setBackgroundColor(getResources().getColor(R.color.blue));
        /*ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(1,1);
        binding.underline1.setLayoutParams(params);
        binding.underline2.setLayoutParams(params);
        binding.underline3.setLayoutParams(params);
        params.height = 3;
        v.setLayoutParams(params);*/
        /*binding.underline1.setLayoutParams(new LinearLayout.LayoutParams(0,1));
        binding.underline2.setLayoutParams(new LinearLayout.LayoutParams(0,1));
        binding.underline3.setLayoutParams(new LinearLayout.LayoutParams(0,1));*/
        binding.underline1.setLayoutParams(param2);
        binding.underline2.setLayoutParams(param2);
        binding.underline3.setLayoutParams(param2);
        v.setLayoutParams(param1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}