package com.example.weatherapplicationfinal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/** Settings Screen Fragment Class which allows the user to set a default location and see their total uses. */
public class SettingsScreenFragment extends Fragment {

    // creating instances for edit text and text view.
    EditText ed_defaultLocation;
    TextView tv_totalLoginsOutput;

    /** Settings Screen Fragment Class on create method */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // creating a view instance of the inflated xml view.
        View view = inflater.inflate(R.layout.fragment_settings_screen, container, false);

        // assigning the instances to the fields.
        ed_defaultLocation = (EditText) view.findViewById(R.id.ed_defaultLocation);
        tv_totalLoginsOutput = (TextView) view.findViewById(R.id.tv_totalLoginsOutput);

        // call the database method and get the default location stored and total uses.
        ApplicationDatabase applicationDatabase = new ApplicationDatabase(getContext());

        ed_defaultLocation.setText(applicationDatabase.getUserLocation());
        tv_totalLoginsOutput.setText(Integer.toString(applicationDatabase.getUserTotalLogins()));

        // listener for if the user changes focus from the text field to another fragment or segment.
        ed_defaultLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                // if the edit text was clicked onto and loses focus then process.
                if (!hasFocus){

                    // if the edit text field is empty than put a hint for the user to put a location in.
                    if (String.valueOf(ed_defaultLocation.getText()).equals("")) {
                        ed_defaultLocation.setHint("Enter a city. ");
                    }

                    // storing the text in the text field into the string.
                    String userLocationToUpdate = String.valueOf(ed_defaultLocation.getText());

                    // calling a setter in the database.
                    applicationDatabase.setDefaultLocation(userLocationToUpdate);
                }
            }
        });

        // listener for when the using phone keyboard and enter is pressed.
        ed_defaultLocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                // if a button is pressed down and that button is enter.
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    // if the edit text field is empty than put a hint for the user to put a location in.
                    if (String.valueOf(ed_defaultLocation.getText()).equals("")) {
                        ed_defaultLocation.setHint("Enter a city. ");
                    }

                    // if it is not empty then we pass it to the setter method in the database class.
                    else {
                        // storing the text in the text field into the string.
                        String userLocationToUpdate = String.valueOf(ed_defaultLocation.getText());

                        // calling a setter in the database.
                        applicationDatabase.setDefaultLocation(userLocationToUpdate);
                    }

                    return true;
                }
                return false;
            }
        });
        return view;
    }
}
