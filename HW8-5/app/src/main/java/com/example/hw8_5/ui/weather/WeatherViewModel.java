package com.example.hw8_5.ui.weather;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class WeatherViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WeatherViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Brandon Michael Que Z23479912");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
