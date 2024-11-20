package com.examatlas.crownpublication.Utils;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestqueue;
    private static Context mContext;

    // Singleton constructor
    private MySingleton(Context context) {
        mContext = context.getApplicationContext();  // Ensure to use application context
        mRequestqueue = getRequestQueue();
    }

    // Get singleton instance
    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    // Get the request queue
    public RequestQueue getRequestQueue() {
        if (mRequestqueue == null) {
            mRequestqueue = Volley.newRequestQueue(mContext);
        }
        return mRequestqueue;
    }

    // Add request to the queue
    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    /**
     * Handle DELETE request separately if needed for specific cases
     */
    public void addToRequestQueueWithDelete(Request<?> request) {
        // You could implement special handling for DELETE requests here if needed.
        getRequestQueue().add(request);
    }
}
