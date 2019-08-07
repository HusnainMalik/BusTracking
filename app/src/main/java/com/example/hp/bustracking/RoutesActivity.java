package com.example.hp.bustracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class RoutesActivity extends AppCompatActivity {
    RecyclerView rvroutes;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        rvroutes = findViewById(R.id.rv_routes);


        final ProgressDialog dialog = new ProgressDialog(RoutesActivity.this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);
        dialog.show();

        final StringRequest request = new StringRequest(ApiConfig.STOP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("mytag", response);
                Toast.makeText(RoutesActivity.this, "Success 1", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    JSONArray stopArray = jObject.getJSONArray("stop_list");
                    final ArrayList<Stop> stopList = new ArrayList<>();
                    for (int i = 0; i < stopArray.length(); i++) {
                        JSONObject stopObject = stopArray.getJSONObject(i);
                        Stop stop = new Stop();
                        stop.busstatus = stopObject.getInt("bus_status");
                        stop.driverId = stopObject.getInt("driver_id");
                        stop.stopid = stopObject.getInt("stop_id");
                        stop.Routename = stopObject.getString("stop_name");
                        stopList.add(stop);
                    }

                    GridLayoutManager manager = new GridLayoutManager(RoutesActivity.this, 1);
                    rvroutes.setLayoutManager(manager);

                    RoutesAdapter adapter = new RoutesAdapter(RoutesActivity.this, stopList, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            /*Intent intent = new Intent(RoutesActivity.this,Driver_PermissionActivity.class);
                            startActivity(intent);*/
                            final Stop Selectedstop = stopList.get(position);

                            StringRequest request_driver = new StringRequest(Request.Method.POST, ApiConfig.GET_DRIVER_DETAILS, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        Intent intent = new Intent(RoutesActivity.this, DriverMapActivity.class);
                                        intent.putExtra("bus_id", jsonObject.getInt("bus_id"));
                                        intent.putExtra("driver_number", jsonObject.getString("driver_number"));
                                        intent.putExtra("driver_name", jsonObject.getString("driver_name"));
                                        intent.putExtra("conductor_name", jsonObject.getString("conductor_name"));
                                        intent.putExtra("bus_name", jsonObject.getString("bus_name"));
                                        intent.putExtra("bus_no", jsonObject.getString("bus_no"));
                                        startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(RoutesActivity.this, "parsing error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(RoutesActivity.this, "volley error", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("driver_id", String.valueOf(Selectedstop.driverId));
                                    return params;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            queue.add(request_driver);

                            /*Stop Selectedstop = stopList.get(position);
                            Intent intent = new Intent(RoutesActivity.this, Driver_PermissionActivity.class);
                            intent.putExtra("stop_id",Selectedstop.stopid);
                            intent.putExtra("stop_name",Selectedstop.Routename);
                            startActivity(intent);*/


                        }
                    });


                    rvroutes.setAdapter(adapter);

                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(RoutesActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(RoutesActivity.this, "Volley Error", Toast.LENGTH_LONG).show();
            }

        });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}
