package com.example.hw8_5.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.hw8_5.MainActivity;
import com.example.hw8_5.R;
import com.example.hw8_5.ui.weather.WeatherFragment;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    double latitudeValue;
    double longitudeValue;
    TextView theLocation, theLatitude, theLongitude;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        theLatitude = (TextView)root.findViewById(R.id.theLatitude);
        theLongitude = (TextView)root.findViewById(R.id.theLongitude);
        theLocation = (TextView)root.findViewById(R.id.theLocation);

        Intent intent = getActivity().getIntent();
        String theLatitudeValue = intent.getStringExtra(WeatherFragment.transferLat);
        String theLongitudeValue = intent.getStringExtra(WeatherFragment.transferLon);
        String theLocationValue = intent.getStringExtra(WeatherFragment.transferLocation);

        latitudeValue = Double.parseDouble(theLatitudeValue);
        longitudeValue = Double.parseDouble(theLongitudeValue);

        theLatitude.setText(String.valueOf(latitudeValue));
        theLongitude.setText(String.valueOf(longitudeValue));
        theLocation.setText(theLocationValue);




        return root;
    }

    //add functions here

}