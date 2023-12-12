package com.example.weatherapplicationfinal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Home Screen Fragment Class which allows the user to search for a locations weather or use their location. */
public class HomeScreenFragment extends Fragment {

    // used to group all output messages in logcat.
    String TAG;

    // creating instances for each of the fields.
    ImageButton bt_searchForLocation;
    ImageButton bt_locationServicesSearch;
    EditText ed_locationToSearch;
    TextView tv_cityName;
    TextView tv_temperature;
    TextView tv_weatherComment;
    ImageView iv_weatherCondition;

    // global strings used across the methods for the url and accessing the open weather api.
    String urlForApiSearch;
    String urlSegmentOne = "https://api.openweathermap.org/data/2.5/weather?q=";
    String urlSegmentOneAlt = "https://api.openweathermap.org/data/2.5/weather?lat=";
    String urlSegmentTwo = "&appid=";
    String apiKey = "fca09cfd87339fe98f55e74c6c9c6198";

    // boolean for identifying issue related to incorrect default location in settings.
    boolean defaultLocationChecked = false;

    // Resource Used - Android Training - https://developer.android.com/training/location/retrieve-current

    /** Home Screen Fragment Class on create method */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // creating a view instance of the inflated xml view.
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        // assigning the instances to the fields.
        bt_searchForLocation = (ImageButton) view.findViewById(R.id.bt_searchForLocation);
        bt_locationServicesSearch = (ImageButton) view.findViewById(R.id.bt_locationServicesSearch);
        ed_locationToSearch = (EditText) view.findViewById(R.id.ed_locationToSearch);
        tv_cityName = (TextView) view.findViewById(R.id.tv_cityName);
        tv_temperature = (TextView) view.findViewById(R.id.tv_temperature);
        tv_weatherComment = (TextView) view.findViewById(R.id.tv_weatherComment);
        iv_weatherCondition = (ImageView) view.findViewById(R.id.iv_weatherCondition);

        // calling method that checks if there is a default location set in the database.
        defaultLocation();

        // listener for when the user enters the location and hits enter.
        ed_locationToSearch.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                // if a button is pressed down and that button is enter proceed.
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    // if the edit text field is empty than put a hint for the user to put a location in.
                    if (String.valueOf(ed_locationToSearch.getText()).equals("")) {
                        ed_locationToSearch.setHint("Please enter a city. ");
                    }

                    // otherwise create a url for the api and then try to get the weather.
                    else {

                        // used to empty the hint message and so it doesn't stay after an error occurred.
                        ed_locationToSearch.setHint("Canberra");

                        urlForApiSearch = urlSegmentOne + ed_locationToSearch.getText() + urlSegmentTwo + apiKey;
                        getWeatherInformation();
                    }

                    return true;
                }
                return false;
            }
        });

        // listener for when the user hits the submit button.
        bt_searchForLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Resource Used -StackOverflow - Hiding Keyboard - https://stackoverflow.com/a/47198722
                ed_locationToSearch.onEditorAction(EditorInfo.IME_ACTION_DONE);

                // if the edit text field is empty than put a hint for the user to put a location in.
                if (String.valueOf(ed_locationToSearch.getText()).equals("")) {
                    ed_locationToSearch.setHint("Please enter a city. ");
                }

                // otherwise create a url for the api and then try to get the weather.
                else {

                    // used to empty the hint message and so it doesn't stay after an error.
                    ed_locationToSearch.setHint("Canberra");

                    urlForApiSearch = urlSegmentOne + ed_locationToSearch.getText() + urlSegmentTwo + apiKey;
                    getWeatherInformation();
                }
            }
        });

        // listener for when the user wants the weather from the location services button.
        bt_locationServicesSearch.setOnClickListener(new View.OnClickListener() {

            /* Resource Used - TutorialsPoint from Interact - https://www.tutorialspoint.com/android/android_location_based_services.htm
               Resource Used - Android Training - https://developer.android.com/training/permissions/requesting
               Resource Used - Android Training - https://developer.android.com/training/location/retrieve-current
               Resource Used - GitHub Example - https://gist.github.com/kedarmp/26b5697f257d5d0d9f8f2cefe9944ddc */

            @Override
            public void onClick(View view) {

                // used to empty the hint message and so it doesn't stay after an error and cause confusion.
                ed_locationToSearch.setHint("Canberra");

                // using check self permission to see if the access fine location has been granted in the package.
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    // if the permission has been granted then we call the method to get the location information.
                    getLastLocation();
                }

                // otherwise request that the user provide the permission.
                else {

                    // location request code for the checking of services.
                    int locationRequestCode = 001;

                    // requesting permission from the user for location.
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /** Method for accessing the database and getting the default location. */
    public void defaultLocation() {

        // call the database method and get the default location stored.
        ApplicationDatabase applicationDatabase = new ApplicationDatabase(getContext());
        String defaultLocationFromDatabase = applicationDatabase.getUserLocation();

        // if the default location is not empty, create a url for the api and then try to get the weather.
        if (!defaultLocationFromDatabase.equals("")) {

            urlForApiSearch = urlSegmentOne + defaultLocationFromDatabase + urlSegmentTwo + apiKey;
            getWeatherInformation();
        }

        // else if the default location is empty just make all the text views blank on the home screen.
        else {
            tv_cityName.setText("");
            tv_weatherComment.setText("");
            tv_temperature.setText("");
        }
    }

    // Resource Used - Google Volley - https://google.github.io/volley/request.html
    /** Method for getting weather information from the Open Weather API. */
    public void getWeatherInformation() {

        // getting instance of request queue that is shared across the application.
        RequestQueue queue = VolleySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue();

        // creating a json request for getting an object from our created url.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlForApiSearch, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                // try and catch statement for processing the response from the api.
                try {

                    // getting another object called main from the json object response.
                    JSONObject jsonObject = response.getJSONObject("main");

                    // within that main json object get the field temp.
                    double temperatureKelvin = (jsonObject.getDouble("temp"));

                    // convert the returned result which is kelvin into an int and round.
                    int temperatureCelsius = (int) Math.round(temperatureKelvin - 273.15);

                    // within the response json object getting the field name for the city.
                    String cityName = response.getString("name");

                    // the weather conditions is stored in an array within the json object response.
                    JSONArray weatherArray = response.getJSONArray("weather");

                    // within that json array we need to get the json object.
                    JSONObject firstWeatherObject = weatherArray.getJSONObject(0);

                    // then similar to the city we get the string from the above json object.
                    String weatherCondition = firstWeatherObject.getString("main");

                    // all the relevant information is retrieved from the api and the information is set into the text views.
                    tv_temperature.setText(temperatureCelsius + "\u00B0" + "C");
                    tv_weatherComment.setText(weatherCondition);
                    tv_cityName.setText(cityName);

                    // then a method is called to set the associated image for the weather by passing a string of the condition.
                    setWeatherConditionImage(weatherCondition);

                    // if default location is not checked but reached here it means it returned a valid result.
                    if (!defaultLocationChecked){
                        defaultLocationChecked = true;
                    }
                }

                catch (JSONException e) {
                    Log.e(TAG, "Error occurred when attempting to get information from Open Weather.");
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error occurred when attempting to get information from Open Weather.");
                error.printStackTrace();

                // if the default is not checked but reached here it could mean that it is invalid so hint set.
                if (!defaultLocationChecked){
                    ed_locationToSearch.setHint("Default location error.");
                }

                // otherwise there is an issue with the specific search by the user.
                else {
                    // set a hint in the text field that there was an issue.
                    ed_locationToSearch.setText("");
                    ed_locationToSearch.setHint("Please try again.");
                }
            }
        });

        // adding the request to the single application instance of volley.
        queue.add(request);
    }

    // Resource Used - Android Training - https://developer.android.com/training/location/retrieve-current
    // suppressing the error as this method is only called once the permission has been granted.
    @SuppressLint("MissingPermission")
    /** Method to get the last location stored on the phone. */
    private void getLastLocation() {

        // instance of the location client.
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // using the location clients get last location method with a success listener.
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {

                // if there result is not null then information needed is stored into strings.
                if (location != null) {

                    String latitude = Double.toString(location.getLatitude());
                    String longitude = Double.toString(location.getLongitude());

                    // creating the url for the api search using the latitude and longitude.
                    urlForApiSearch = urlSegmentOneAlt + latitude + "&lon=" + longitude + "&appid=" + apiKey;

                    // then call the method to get the weather information.
                    getWeatherInformation();
                }
            }
        });

        // on failure provide a message in the log however in the future an alert or something would be best.
        fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error when trying to access location information. ");
            }
        });
    }

    // Resource Used - Open Weather Icons - https://openweathermap.org/weather-conditions#Weather-Condition-Codes-2
    /** Method to set the weather icons depending on the weather condition. */
    private void setWeatherConditionImage(String weatherCondition){

        // simple if, else if and else structure to set the icons based on the options.
        if (weatherCondition.equals("Thunderstorm")){
            iv_weatherCondition.setImageResource(R.drawable.icon_weather_thunderstorm);
        }

        else if (weatherCondition.equals("Drizzle")){
            iv_weatherCondition.setImageResource(R.drawable.icon_weather_drizzle);
        }

        else if (weatherCondition.equals("Rain")){
            iv_weatherCondition.setImageResource(R.drawable.icon_weather_rain);
        }

        else if (weatherCondition.equals("Snow")){
            iv_weatherCondition.setImageResource(R.drawable.icon_weather_snow);
        }

        else if (weatherCondition.equals("Clear")){
            iv_weatherCondition.setImageResource(R.drawable.icon_weather_clear);
        }

        else if (weatherCondition.equals("Clouds")){
            iv_weatherCondition.setImageResource(R.drawable.icon_weather_clouds);
        }

        else {
            iv_weatherCondition.setImageResource(R.drawable.icon_weather_atmosphere);
        }
    }
}
