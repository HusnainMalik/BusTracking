package com.example.hp.bustracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StopsActivity extends AppCompatActivity {
    RecyclerView rvstops;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        rvstops=findViewById(R.id.rv_stops);
        Bundle bundle =getIntent().getExtras();
        int stop_id =bundle.getInt("stop_id");
        String RouteNAme =bundle.getString("stop_name");
        if(RouteNAme !=null)
        this.setTitle(RouteNAme);

        String url =ApiConfig.STOP_INFO_URL + "?stop_id="+ stop_id;


        final ProgressDialog dialog=new ProgressDialog(StopsActivity.this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);
        dialog.show();

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("mytag", response);
                Toast.makeText(StopsActivity.this, "Success 1", Toast.LENGTH_SHORT).show();
                try {
                    //JSONObject jObject = new JSONObject(response);
                    JSONArray stopInfoArray =new JSONArray(response);
                   // JSONArray stopInfoArray = jObject.getJSONArray("stop_info_list");
                    final ArrayList<StopInfo> stopInfoList = new ArrayList<>();
                    for(int i = 0; i < stopInfoArray.length(); i++){
                        JSONObject stopInfoObject = stopInfoArray.getJSONObject(i);

                        StopInfo stopInfo = new StopInfo();
                        stopInfo.stopInfoId =stopInfoObject.getInt("stop_info_id");
                        stopInfo.stopInfoName = stopInfoObject.getString("stop_info_name");
                        stopInfo.stopTime = stopInfoObject.getString("stop_time");
                        stopInfoList.add(stopInfo);
                    }


                    GridLayoutManager manager = new GridLayoutManager(StopsActivity.this,1);
                    rvstops.setLayoutManager(manager);

                    StopsAdapter adapter = new StopsAdapter(stopInfoList, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ;


                        }
                    });

                    rvstops.setAdapter(adapter);

                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(StopsActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(StopsActivity.this, "Volley Error", Toast.LENGTH_LONG).show();
            }

        });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }
}
