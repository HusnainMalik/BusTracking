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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;

public class timedetailActivity extends AppCompatActivity {

    RecyclerView rvtimedetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timedetail);

        rvtimedetail=findViewById(R.id.rv_timedetial);

        final ProgressDialog dialog=new ProgressDialog(timedetailActivity.this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request = new StringRequest(ApiConfig.TIME_INFO_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("mytag", response);
                Toast.makeText(timedetailActivity.this, "Success", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray timeArray = jsonObject.getJSONArray("time_list");
                    final ArrayList<time> timelist = new ArrayList<>();
                    for(int i = 0; i < timeArray.length(); i++){
                        JSONObject timeObject = timeArray.getJSONObject(i);
                        time tim = new time();

                        tim.timetitle = timeObject.getString("time_title");
                        tim.timename = timeObject.getString("time_name");
                        tim.timeofdepart = timeObject.getString("time_ofdepart");
                        tim.timeofarrival = timeObject.getString("time_ofarrival");
                        timelist.add(tim);
                    }
                    GridLayoutManager manager = new GridLayoutManager(timedetailActivity.this,1);
                    rvtimedetail.setLayoutManager(manager);

                    timedetailAdapter adapter = new timedetailAdapter(timelist, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    });

                    rvtimedetail.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(timedetailActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(timedetailActivity.this, "Volley Error",Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        queue.add(request);


    }
}
