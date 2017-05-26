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

import data_objects.Bill;
import data_objects.BillAdapter;

public class ViewBills extends AppCompatActivity {

    String url = "https://congress.api.sunlightfoundation.com/upcoming_bills?chamber=house&order=scheduled_at?apikey=";
    JSONObject json = null;
    ArrayList<Bill> list = new ArrayList<>();
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
            progressDialog.setMessage("Pulling the upcoming bills");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    JSON_Pull.this.cancel(true);
                }
            });
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
                    Bill b = new Bill(object.getString("chamber"), object.getString("legislative_day"),
                            object.getString("consideration"), object.getString("description"),
                            object.getString("bill_url"));
                    list.add(b);
                }

                ba = new BillAdapter(list, ViewBills.this);
                recList.setAdapter(ba);
            }catch (Exception e){
                Log.i("failure", "On Post Execute has failed");
                e.printStackTrace();
            }
        }

    }
}

