package com.example.weatherapplicationfinal;

import static android.graphics.Color.WHITE;
import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/** Visual Screen Fragment Class which allows the user to search for a location and get a range of temperatures in a chart. */
public class VisualScreenFragment extends Fragment {

    // used to group all output messages in logcat.
    String TAG;

    // creating an instance of the bar chart view.
    BarChart barChart;

    // creating instances for each of the fields.
    EditText ed_locationToSearch;
    ImageButton bt_searchForLocation;
    TextView tv_headingAndCity;

    // global strings used across the methods for the url and accessing the open weather api.
    String urlForApiSearch;
    String urlSegmentOne = "https://api.openweathermap.org/data/2.5/weather?q=";
    String urlSegmentTwo = "&appid=";
    String apiKey = "fca09cfd87339fe98f55e74c6c9c6198";

    /** Visual Screen Fragment Class on create method */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // creating a view instance of the inflated xml view.
        View view = inflater.inflate(R.layout.fragment_visual_screen, container, false);

        // assigning the instances to the fields.
        barChart = (BarChart) view.findViewById(R.id.barchart);
        ed_locationToSearch = (EditText) view.findViewById(R.id.ed_locationToSearch);
        bt_searchForLocation = (ImageButton) view.findViewById(R.id.bt_searchForLocation);
        tv_headingAndCity = (TextView) view.findViewById(R.id.tv_headingAndCity);

        // if this is not used I found the chart view would show text saying no data available.
        barChart.setNoDataText("");

        // listener for when the user enters the location and hits enter.
        ed_locationToSearch.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                // if a button is pressed down and that button is enter proceed.
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    // if the edit text field is empty than put a hint for the user to put a location in.
                    if (String.valueOf(ed_locationToSearch.getText()).equals("")){
                        ed_locationToSearch.setHint("Please enter a city. ");
                    }

                    // otherwise create a url for the api and then try to get the weather.
                    else {

                        // used to empty the hint message and so it doesn't stay after an error.
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
                if (String.valueOf(ed_locationToSearch.getText()).equals("")){
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
        return view;
    }

    // Resource Used - Google Volley - https://google.github.io/volley/request.html
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

                    // then convert it to a float which is required for the MPAndroidChart
                    Float current = Float.valueOf(temperatureCelsius);

                    // same process as above for the minimum temperature.
                    double temperatureMinimumKelvin = (jsonObject.getDouble("temp_min"));
                    int temperatureMinimumCelsius =  (int) Math.round(temperatureMinimumKelvin - 273.15);
                    Float minimum = Float.valueOf(temperatureMinimumCelsius);

                    // and again for the maximum temperature.
                    double temperatureMaximumKelvin = (jsonObject.getDouble("temp_max"));
                    int temperatureMaximumCelsius =  (int) Math.round(temperatureMaximumKelvin - 273.15);
                    Float maximum = Float.valueOf(temperatureMaximumCelsius);

                    // within the response json object getting the field name for the city.
                    String cityName = response.getString("name");

                    // set the text view with with a heading for the chart.
                    tv_headingAndCity.setText("Temperature Range for " + cityName);

                    // call the method to create the chart and passing the temperature figures.
                    createChart(current, maximum, minimum);
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

                // set a hint in the text field that there was an issue.
                ed_locationToSearch.setText("");
                ed_locationToSearch.setHint("Please try again.");
            }
        });
        queue.add(request);
    }

    /* Resource Used - MPAndroidChart Github - https://github.com/PhilJay/MPAndroidChart
       Resource Used - MPAndroidChart Javadoc - https://javadoc.jitpack.io/com/github/PhilJay/MPAndroidChart/v3.1.0/javadoc
       Resource Used - Geeks for Geeks - https://www.geeksforgeeks.org/how-to-create-a-barchart-in-android */

    /** Method the create the chart and display it using MPAndroidChart. */
    public void createChart(float current, float maximum, float minimum){

        // object for all the data ofr the bar chat.
        BarData barData;

        // object for the data set as the different records are stored in a single set
        BarDataSet barDataSet;

        // an arraylist that stores each entry of the data set.
        ArrayList barEntriesArrayList = new ArrayList<>();

        // the max, current and min were passed from the previous method and assigned to 0, 1, 2.
        barEntriesArrayList.add(new BarEntry(0, maximum));
        barEntriesArrayList.add(new BarEntry(1, current));
        barEntriesArrayList.add(new BarEntry(2, minimum));

        // creating the new bar data set using the constructor and adding the above array to it.
        barDataSet = new BarDataSet(barEntriesArrayList, "");

        // creating the new bar data and adding the above data set to the charts data.
        barData = new BarData(barDataSet);

        // an array of labels for the x-axis rather than having it showing as 0, 1, 2
        // Resource Used - Stack Overflow - https://stackoverflow.com/a/59276967
        ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Max");
        xAxisLabel.add("Current");
        xAxisLabel.add("Min");

        // then these labels are adding to the x-axis and formatted as required.
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        // adding the x-axis information to the bar chart.
        XAxis xAxis = barChart.getXAxis();

        // changing the x-axis to show at the bottom rather than default top of screen.
        xAxis.setPosition(BOTTOM);

        // changing the x-axis to be white to be more visible and look nicer.
        xAxis.setTextColor(WHITE);

        // setting the right and left axis as false as the numbers are shown in the bar.
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);

        // this removes a label on the bottom left for some reason - don't know why to be honest.
        xAxis.setGranularity(1f);

        // hides a description appearing on the bottom right of the chart.
        barChart.getDescription().setEnabled(false);

        // stops gridlines appearing on the chart which didn't look nice.
        barChart.getXAxis().setDrawGridLines(false);

        // hides the legend as not needed.
        barChart.getLegend().setEnabled(false);

        // adds colour to the data as shown by geeks for geeks.
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // setting the columns text to white which looks nice against background
        barDataSet.setValueTextColor(WHITE);

        // setting text size to be larger than default.
        barDataSet.setValueTextSize(16f);

        // finally adding the data to the bar chart.
        barChart.setData(barData);

        // Resource Used - Stack Overflow - Data Not Updating - https://stackoverflow.com/a/47304132
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }
}
