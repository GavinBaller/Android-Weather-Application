package com.example.weatherapplicationfinal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

/** Explore Screen Fragment Class which has a button for the user to get a random locations weather. */
public class ExploreScreenFragment extends Fragment {

    // used to group all output messages in logcat.
    String TAG;

    // creating instances for each of the fields.
    ImageButton bt_exploreRandomCity;
    TextView tv_cityName;
    TextView tv_temperature;
    TextView tv_weatherComment;
    ImageView iv_weatherCondition;

    // global strings used across the methods for the url and accessing the open weather api.
    String urlForApiSearch;
    String urlSegmentOne = "https://api.openweathermap.org/data/2.5/weather?q=";
    String urlSegmentTwo = "&appid=";
    String apiKey = "fca09cfd87339fe98f55e74c6c9c6198";

    // store a random number generated
    int randomNumber;

    /** Explore Screen Fragment Class on create method */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // creating a view instance of the inflated xml view.
        View view = inflater.inflate(R.layout.fragment_explore_screen, container, false);

        // assigning the instances to the fields.
        bt_exploreRandomCity = (ImageButton) view.findViewById(R.id.bt_exploreRandomCity);
        tv_cityName = (TextView) view.findViewById(R.id.tv_cityName);
        tv_temperature = (TextView) view.findViewById(R.id.tv_temperature);
        tv_weatherComment = (TextView) view.findViewById(R.id.tv_weatherComment);
        iv_weatherCondition = (ImageView) view.findViewById(R.id.iv_weatherCondition);

        // listener for when the user wants to get a random locations weather.
        bt_exploreRandomCity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // calling method to get random number.
                randomNumber();

                // calling method to find the random city.
                String cityToExplore = cityToExploreSelector();

                // create a url for the api and then try to get the weather
                urlForApiSearch = urlSegmentOne + cityToExplore + urlSegmentTwo + apiKey;
                getWeatherInformation();
            }
        });
        return view;
    }

    /** Method used to get a random number. */
    public void randomNumber(){
        Random random = new Random();
        randomNumber = random.nextInt(371);
    }

    /** Method for getting a random city name from a list. */
    public String cityToExploreSelector(){

        // initiated string for the location to explore.
        String cityToExplore = "";

        // iterator to traverse the document until we reach the relevant line.
        int i = 0;

        // Resource Used - Android Training - https://developer.android.com/reference/android/content/res/AssetManager

        // Try and catch required for reading a file.
        try{

            // Get an input stream instance to read the file. Get assets to read from assets folder.
            InputStream inputStream = getActivity().getAssets().open("ListOfCities.txt");

            // Create a Scanner to read from the InputStream
            Scanner input = new Scanner(inputStream);

            // loop through the lines while the iterator is less than the random and there is a next.
            while (i <= randomNumber && input.hasNext()){

                // string to save the line of location name
                String nextLine = input.nextLine();

                // if iterator equals the random number generated then that is the location to search.
                if (i == randomNumber){
                    cityToExplore = nextLine;
                }
                i++;
            }

            // Close the input stream and scanner.
            input.close();
            inputStream.close();
        }

        catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "Error accessing or reading the file. ");
        }

        return cityToExplore;
    }

    /** Method for getting weather information from the Open Weather API. */
    public void getWeatherInformation(){

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
                    int temperatureCelsius =  (int) Math.round(temperatureKelvin - 273.15);

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

                tv_weatherComment.setText("An error occurred. Please try again");
            }
        });

        // adding the request to the single application instance of volley.
        queue.add(request);
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
