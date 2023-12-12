package com.example.weatherapplicationfinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/** Main Activity Class for the application. Facilitates the fragment manager. */
public class MainActivity extends AppCompatActivity {

    // creating instances for each of the image buttons in the main activity.
    ImageButton bt_home, bt_explore, bt_visual, bt_settings;

    /** Main Activity Class on create method */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // call the database method to add one to the login count.
        ApplicationDatabase applicationDatabase = new ApplicationDatabase(MainActivity.this);
        applicationDatabase.incrementUserUses();

        // creating the request queue with application context so a single queue can be called across the application.
        VolleySingleton.getInstance(this);

        // assigning the image button instances to the image buttons fields.
        bt_home = (ImageButton) findViewById(R.id.bt_home);
        bt_explore = (ImageButton) findViewById(R.id.bt_explore);
        bt_visual = (ImageButton) findViewById(R.id.bt_visual);
        bt_settings = (ImageButton) findViewById(R.id.bt_settings);

        // creating a fragment manager for use by the on click listeners.
        FragmentManager fragmentManager = getSupportFragmentManager();

        // listener for button clicks and calling fragment manager to manage transitions.
        bt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // fragment manager used to transition between the fragments.
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, HomeScreenFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // listener for explore clicks and calling fragment manager to manage transitions.
        bt_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // fragment manager used to transition between the fragments.
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ExploreScreenFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // listener for visual clicks and calling fragment manager to manage transitions.
        bt_visual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // fragment manager used to transition between the fragments.
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, VisualScreenFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // listener for settings clicks and calling fragment manager to manage transitions.
        bt_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // fragment manager used to transition between the fragments.
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, SettingsScreenFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
