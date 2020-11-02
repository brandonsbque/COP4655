package com.example.hw8_5.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Unfortunately could not get intents to work with fragments, therefore map could not be implemented");
    }

    public LiveData<String> getText() {
        return mText;
    }
}