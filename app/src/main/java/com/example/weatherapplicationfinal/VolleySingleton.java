package com.example.weatherapplicationfinal;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

// Resource Used - Google Volley - https://google.github.io/volley/requestqueue.html

/** Volley Singleton Class which enables a single volley request queue across the application. */
public class VolleySingleton {

    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    public VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    // synchronised is a principle of threading and stops multiple instances occurring at once.
    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
