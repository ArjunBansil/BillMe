package data_objects;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Arjun Bansil on 8/30/2017.
 */

public class AppController extends Application {

    public static final String tag = "request";

    private RequestQueue requestQueue;
    private static AppController mInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance(){ return mInstance;  }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String t){
        req.setTag(TextUtils.isEmpty(t) ? tag: t);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object t){
        if(requestQueue != null){
            requestQueue.cancelAll(t);
        }
    }
}
