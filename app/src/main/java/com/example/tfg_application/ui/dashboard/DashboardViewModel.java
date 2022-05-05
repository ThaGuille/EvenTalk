package com.example.tfg_application.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the event fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setUnderlineActive(){

    }
}