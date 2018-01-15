package com.keybored.voteme;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data_objects.AppController;
import data_objects.Bill;
import data_objects.BillAdapter;
import data_objects.DataHandler;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;

public class ViewBills extends AppCompatActivity {

    private ProgressDialog dialog;
    String url = "https://api.propublica.org/congress/v1/congress/115/both/bills/introduced.json";
    String key;
    String header = "X-API-Key";
   // JSONObject json = null;
    RecyclerView recList;
    LinearLayoutManager llm;
    BillAdapter ba;
    ViewGroup view;
    RequestQueue queue;
    private static final String tag = "volley";

    private void doDialog(){
        if(!dialog.isShowing()) dialog.show();
        else dialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_view_bills);
        getSupportActionBar().hide();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        key = getResources().getString(R.string.propublica_api_key);
        view = (ViewGroup)findViewById(R.id.viewbills_layout);
        recList = (RecyclerView)findViewById(R.id.bill_list);
        llm = new LinearLayoutManager(this);
        jsonPull();

    }

    private void jsonPull(){
        doDialog();


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(tag, response.toString());

                try{
                    JSONObject results = (JSONObject)response.get("results");
                    JSONArray bills = (JSONArray)results.get("bills");
                    ArrayList<Bill> list = new ArrayList<Bill>();

                    for(int i = 0; i < bills.length(); i++){
                        JSONObject object = (JSONObject)bills.get(i);
                        String sponsor = object.getString("sponsor_name");
                        String url = object.getString("congressdotgov_url");
                        String subject = object.getString("primary_subject");
                        String title = object.getString("title");
                        String leg_day = object.getString("introduced_date");
                        list.add(new Bill(sponsor, leg_day, subject, title, url));
                    }

                    if(list.isEmpty()){
                        doDialog();
                        Snackbar.make(view, "No new bills recently!", Snackbar.LENGTH_SHORT).show();
                    }else {
                        recList.setHasFixedSize(true);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        recList.setLayoutManager(llm);
                        ba = new BillAdapter(list, getApplicationContext());
                        recList.setAdapter(ba);
                        doDialog();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Snackbar.make(view, "Connection error...", Snackbar.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(tag, "Error: " + error.getMessage());
                doDialog();
                Snackbar.make(view, "Connection error...", Snackbar.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Log.i(tag, "Key is called " + key);
                Map<String,String> params = new HashMap<String, String>();
                params.put("X-API-Key:", key);
                return params;
            }
        };

        queue.add(req);
    }

    @Override
    protected void onStop(){
        super.onStop();
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                //This is gud programming practice
                return true;
            }
        });
    }

    private class Animator extends HeaderStikkyAnimator{
        @Override
        public AnimatorBuilder getAnimatorBuilder(){
            View mHeaderImage = getHeader().findViewById(R.id.header_image);
            return AnimatorBuilder.create().applyVerticalParallax(mHeaderImage);
        }
    }


}

