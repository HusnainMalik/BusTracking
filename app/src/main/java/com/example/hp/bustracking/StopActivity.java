package com.example.hp.bustracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;

public class StopActivity extends AppCompatActivity {

    RecyclerView rvStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

        rvStop = findViewById(R.id.rv_stop);


        final ProgressDialog dialog=new ProgressDialog(StopActivity.this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);
        dialog.show();

        StringRequest request = new StringRequest(ApiConfig.STOP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("mytag", response);
                Toast.makeText(StopActivity.this, "Success 1", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    JSONArray stopArray = jObject.getJSONArray("stop_list");
                    final ArrayList<Stop> stopList = new ArrayList<>();
                    for(int i = 0; i < stopArray.length(); i++){
                        JSONObject stopObject = stopArray.getJSONObject(i);
                        Stop stop = new Stop();
                        stop.stopid = stopObject.getInt("stop_id");
                        stop.Routename = stopObject.getString("stop_name");
                        stopList.add(stop);
                    }


                    GridLayoutManager manager = new GridLayoutManager(StopActivity.this,1);
                    rvStop.setLayoutManager(manager);

                    StopAdapter adapter = new StopAdapter(StopActivity.this,stopList, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Stop Selectedstop = stopList.get(position);

                            Intent intent = new Intent(StopActivity.this, StopsActivity.class);
                            intent.putExtra("stop_id",Selectedstop.stopid);
                            intent.putExtra("stop_name",Selectedstop.Routename);


                            startActivity(intent);


                        }
                    });

                    rvStop.setAdapter(adapter);

                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(StopActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(StopActivity.this, "Volley Error", Toast.LENGTH_LONG).show();
            }

        });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}
