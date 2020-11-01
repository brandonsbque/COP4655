package com.example.hw8_5.ui.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.hw8_5.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Locale;


public class WeatherFragment extends Fragment {

    private WeatherViewModel weatherViewModel;

   TextView thePlace, description, temp, theHigh, theLow, feelsLike, humidity, visibility, windSpeed, theLatitude, theLongitude;
    String longitudeValue, latitudeValue, theLocation;
    EditText userInput;
    Button searchButton, myLocation, showMap;
    String getURL = "";

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    // GPSTracker class
    GPSTracker gps;

    //texttospeech
    TextToSpeech t1;
    Button speakButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        weatherViewModel =
                ViewModelProviders.of(this).get(WeatherViewModel.class);
        View root = inflater.inflate(R.layout.fragment_weather, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        weatherViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

            }
        });

        try {
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), mPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        thePlace = (TextView)root.findViewById(R.id.thePlace);
        description = (TextView)root.findViewById(R.id.description);
        temp = (TextView)root.findViewById(R.id.temp);
        theHigh = (TextView)root.findViewById(R.id.theHigh);
        theLow = (TextView)root.findViewById(R.id.theLow);
        feelsLike = (TextView)root.findViewById(R.id.feelsLike);
        humidity = (TextView)root.findViewById(R.id.humidity);
        visibility = (TextView)root.findViewById(R.id.visibility);
        windSpeed = (TextView)root.findViewById(R.id.windSpeed);
        theLatitude = (TextView)root.findViewById(R.id.theLatitude);
        theLongitude = (TextView)root.findViewById(R.id.theLongitude);
        userInput = (EditText)root.findViewById(R.id.userInput);
        searchButton = (Button)root.findViewById(R.id.searchButton);
        myLocation = (Button)root.findViewById(R.id.myLocation);
        speakButton = (Button)root.findViewById(R.id.speakButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getURL = userInput.getText().toString();

                if(Utility.numberOrNot(getURL)){
                    String theURL = "https://api.openweathermap.org/data/2.5/weather?zip="+getURL+"&appid=1b8fe95bda81875bd8dc9263cfd58b13";
                    getWeather(theURL);
                    //showMap.setVisibility(View.VISIBLE);
                }
                else{
                    String theURL = "https://api.openweathermap.org/data/2.5/weather?q="+getURL+"&appid=1b8fe95bda81875bd8dc9263cfd58b13";
                    getWeather(theURL);
                    //showMap.setVisibility(View.VISIBLE);
                }

            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create class object
                        gps = new GPSTracker(getActivity().getApplicationContext());

                        // check if GPS enabled
                        if(gps.canGetLocation()){

                            double latitude = gps.getLatitude();
                            double longitude = gps.getLongitude();

                            // \n is for new line
                            Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: "
                                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                            String theURL = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=1b8fe95bda81875bd8dc9263cfd58b13";
                            getWeather(theURL);
                            //showMap.setVisibility(View.VISIBLE);
                        }else{
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
                            gps.showSettingsAlert();
                        }

            }
        });

        t1=new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

       speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = temp.getText().toString();
                Toast.makeText(getActivity().getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        return root;
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    public void getWeather(String theURL){

        String url= theURL;



        JsonObjectRequest getData = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject mainObject = response.getJSONObject("main"); //mainObject is now connected to "main" in the JSON
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0); //object is now connected to array
                    JSONObject windObject = response.getJSONObject("wind");
                    JSONObject coordObject = response.getJSONObject("coord");

                    String theTemp = String.valueOf(mainObject.getDouble("temp"));
                    String theDescription = object.getString("description");
                    String theLocation = response.getString("name");
                    String tempMin = String.valueOf(mainObject.getDouble("temp_min"));
                    String tempMax = String.valueOf(mainObject.getDouble("temp_max"));
                    String theFeelsLike = String.valueOf(mainObject.getDouble("feels_like"));
                    String theHumidity = String.valueOf(mainObject.getDouble("humidity"));
                    String theVisibility = response.getString("visibility");
                    String theWindSpeed = String.valueOf(windObject.getDouble("speed"));
                    String longitudeValue = String.valueOf(coordObject.getDouble("lon"));
                    String latitudeValue = String.valueOf(coordObject.getDouble("lat"));



                    double temp_int = Double.parseDouble(theTemp); //temp is in kelvin right now so we have to convert to fahrenheit before displaying
                    double fahrenheit = ((temp_int - 273.15)*(1.8))+32;
                    fahrenheit = Math.round(fahrenheit); //we round, but it still has .0 at the end so i'll make this an int
                    int fahrenheit_int = (int)fahrenheit;
                    temp.setText(String.valueOf(fahrenheit_int)+"\u00B0F");

                    thePlace.setText(theLocation);
                    description.setText(theDescription);

                    double tempMin_int = Double.parseDouble(tempMin);
                    double fahrenheitMin = ((tempMin_int - 273.15)*(1.8))+32;
                    fahrenheitMin = Math.round(fahrenheitMin); //we round, but it still has .0 at the end so i'll make this an int
                    int fahrenheitMin_int = (int)fahrenheitMin;
                    theLow.setText("L: " + String.valueOf(fahrenheitMin_int)+"\u00B0F");

                    double tempMax_int = Double.parseDouble(tempMax);
                    double fahrenheitMax = ((tempMax_int - 273.15)*(1.8))+32;
                    fahrenheitMax = Math.round(fahrenheitMax); //round to 2 decimal places
                    int fahrenheitMax_int = (int)fahrenheitMax;
                    theHigh.setText("H: "+String.valueOf(fahrenheitMax_int)+"\u00B0F");

                    double feelsLike_int = Double.parseDouble(theFeelsLike);
                    double fahrenheitFeelsLike = ((feelsLike_int - 273.15)*(1.8))+32;
                    fahrenheitFeelsLike = Math.round(fahrenheitFeelsLike); //round to 2 decimal places
                    int fahrenheitFeelsLike_int = (int)fahrenheitFeelsLike;
                    feelsLike.setText("Feels Like: "+String.valueOf(fahrenheitFeelsLike_int)+"\u00B0F");

                    humidity.setText("Humidity: "+theHumidity+"%");

                    double visibility_int = Double.parseDouble(theVisibility);
                    double visibilityIs = (visibility_int / 1609);
                    visibilityIs = Math.round(visibilityIs); //round to 2 decimal places
                    int visibilityIS_int = (int)visibilityIs;
                    visibility.setText("Visibility: "+String.valueOf(visibilityIS_int)+" mi");

                    double windSpeed_int = Double.parseDouble(theWindSpeed);
                    double windSpeedIs = (windSpeed_int * 2.237);
                    windSpeedIs= Math.round(windSpeedIs); //round to 2 decimal places
                    int windSpeedIs_int = (int)windSpeedIs;
                    windSpeed.setText("Wind Speed: "+String.valueOf(windSpeedIs_int)+" mph");

                    theLongitude.setText("Lon: "+longitudeValue);
                    theLatitude.setText("Lat: "+latitudeValue);


                } catch(JSONException theError){
                    theError.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(getData);

    }

}


class Utility{
    static boolean numberOrNot(String userInput){
        try{
            Integer.parseInt(userInput);
        }catch(NumberFormatException ex){
            return false;
        }
        return true;
    }
}