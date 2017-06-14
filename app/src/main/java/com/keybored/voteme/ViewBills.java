package com.keybored.voteme;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import java.util.Set;

import data_objects.Bill;
import data_objects.BillAdapter;
import data_objects.DataHandler;

public class ViewBills extends AppCompatActivity {

    String url = "https://congress.api.sunlightfoundation.com/upcoming_bills?order=scheduled_at?apikey=";
    JSONObject json = null;
    RecyclerView recList;
    BillAdapter ba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bills);
        String key = this.getResources().getString(R.string.sunlight_api_key);
        url += key;
        new JSON_Pull().execute("params");

    }

    public static JSONObject readJsonFromURL(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try{
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            is.close();
            return json;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String readAll(Reader rd) throws IOException{
        StringBuilder sb = new StringBuilder();
        int cp;
        while((cp = rd.read()) != -1){
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private class JSON_Pull extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog progressDialog = new ProgressDialog(ViewBills.this);
        @Override
        protected void onPreExecute(){
            progressDialog.setMessage("Pulling upcoming bills");
            progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... params){
            try{
                return readJsonFromURL(url);
            }catch (Exception e){
                Log.i("failure", "Do in background failed");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject j){
            super.onPostExecute(j);
            progressDialog.dismiss();
            json = j;
            recList = (RecyclerView)findViewById(R.id.bill_list);
            recList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(ViewBills.this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);
            ArrayList<Bill> list = new ArrayList<Bill>();

            try{
                JSONArray array = j.getJSONArray("results");
                for(int i = 0; i < array.length(); i++){

                    JSONObject object = array.getJSONObject(i);
                    Bill b;
                    if(object.getString("chamber").equals("senate")){
                        b = new Bill(object.getString("chamber"), object.getString("legislative_day"),
                                "", object.getString("context"),
                                object.getString("url"));
                    }else {
                        b = new Bill(object.getString("chamber"), object.getString("legislative_day"),
                                object.getString("consideration"), object.getString("description"),
                                object.getString("bill_url"));
                    }
                    list.add(b);
                }
                DataHandler dataHandler = new DataHandler(ViewBills.this);
                ArrayList<Bill> votedList = dataHandler.readList();
                ArrayList<Bill> finalList;
                if(votedList == null){
                    Log.i("fun", "Voted List is null");
                    finalList = list;
                }else{
                    Log.i("fun", "Voted list isn't null");
                    finalList = list;
                    Iterator<Bill> i1 = finalList.iterator();

                    while(i1.hasNext()){
                        Iterator<Bill> i2 = votedList.iterator();
                        Bill b1 = i1.next();
                        while(i2.hasNext()){
                            Bill b2 = i2.next();
                            if(b1.equals(b2)){
                                i1.remove();
                            }
                        }
                    }

                }
                for(Bill b : list){
                    Log.i("fun", b.getDescription());
                }
                ba = new BillAdapter(finalList, ViewBills.this);
                Log.i("fun", "Adapter has been initialized");
                recList.setAdapter(ba);
                Log.i("fun", "Adapter has been set");
            }catch (Exception e){
                Log.i("failure", "On Post Execute has failed");
                e.printStackTrace();
            }
        }

    }
}

