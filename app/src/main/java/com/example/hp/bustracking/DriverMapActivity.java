package com.example.hp.bustracking;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvDriverName;
    private TextView tvRoute;
    private TextView tvBusNumber;
    private TextView tvConductorName;
    private RouteInfo routeInfo;
    private ProgressDialog progressDialog;
    private TextView tvdrivernumber;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFuseLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private final float DEFAULT_ZOOM = 18;

    private String DriverNumber;
    private String DriverName;
    private String ConductorName;
    private String BusName;
    private String BusNumber;
    private int DriverID;
    private int BusId;

    private TextView tvLastUpdate;
    private TextView tvUpdateStatus;

    private Handler updateHandler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateBusLocation();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        tvUpdateStatus = findViewById(R.id.tv_update_status);
        tvLastUpdate = findViewById(R.id.tv_last_update);

        Bundle bundle = getIntent().getExtras();
        BusId = bundle.getInt("bus_id");
        DriverNumber = bundle.getString("driver_number");
        DriverName = bundle.getString("driver_name");
        ConductorName = bundle.getString("conductor_name");
        BusName = bundle.getString("bus_name");
        BusNumber = bundle.getString("bus_no");
        DriverID = bundle.getInt("driver_id");

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_map_Driver);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        mFuseLocationProviderClient = LocationServices.getFusedLocationProviderClient(DriverMapActivity.this);

        updateHandler = new Handler();

        updateBusLocation();
    }

    private void updateBusLocation() {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.GET_BUS_Location, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    double latitude = jsonObject.getDouble("latitude");
                    double longitude = jsonObject.getDouble("longitude");
                    long locationUpdateTime = jsonObject.getLong("locationUpdateTime") * 1000;
                    String address = getAddressFromLatLng(latitude, longitude);
                    tvUpdateStatus.setText("Last location received: " + latitude + ", " + longitude + "\n" + address);
                    tvUpdateStatus.setVisibility(View.VISIBLE);
                    tvLastUpdate.setText("Last Updated: " + new SimpleDateFormat("dd MMMM, yyyy - hh:mm:ss a").format(locationUpdateTime));
                    tvLastUpdate.setVisibility(View.VISIBLE);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitude, longitude));
                    markerOptions.title(BusName);
                    mMap.clear();
                    mMap.addMarker(markerOptions);

                    Log.i("mytag", "marker updated");

                    int status = jsonObject.getInt("status");
                    if (status == 0) {
                        updateHandler.removeCallbacks(runnable);
                        Toast.makeText(DriverMapActivity.this, "Bus Stopped", Toast.LENGTH_LONG).show();
                    } else {
                        updateHandler.postDelayed(runnable, 5000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DriverMapActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(DriverMapActivity.this, "Volley error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bus_id", String.valueOf(BusId));
                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }
        //check if gps is enable or not amd then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(DriverMapActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(DriverMapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });
        task.addOnFailureListener(DriverMapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(DriverMapActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {

        Log.i("mytag", "inside get device location");
        mFuseLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Log.i("mytag", "last location task complete");
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                Log.i("mytag", "last location found");
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                double latitude = mLastKnownLocation.getLatitude();
                                double longitude = mLastKnownLocation.getLongitude();
                                Toast.makeText(DriverMapActivity.this, " Longitude & Latitude = " + latitude + "," + longitude, Toast.LENGTH_LONG).show();
                                //Toast.makeText(MapActivity.this, "Longitude = "+longitude, Toast.LENGTH_SHORT).show();
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                               /* MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.isDraggable();
                                markerOptions.visible(true);
                                markerOptions.title("Your Position");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mcurrentlocation= mMap.addMarker(markerOptions);*/


                            } else {
                                Log.i("mytag", "last location not found");
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {

                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFuseLocationProviderClient.removeLocationUpdates(locationCallback);


                                    }
                                };
                                mFuseLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(DriverMapActivity.this, "Unable to get last Location", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
        DriverHome();

    }

    public void DriverHome() {

        tvdrivernumber = findViewById(R.id.tv_driver_number);
        tvDriverName = findViewById(R.id.tv_driver_name);
        tvRoute = findViewById(R.id.tv_routename);
        tvBusNumber = findViewById(R.id.tv_bus_number);
        tvConductorName = findViewById(R.id.tv_conductor_name);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        final Driver currentDriver = SessionHelper.getCurrentDriver(this);
        tvdrivernumber.setText(DriverNumber);
        tvDriverName.setText(DriverName);
        tvConductorName.setText(ConductorName);

        int driverId = currentDriver.driver_id;
        progressDialog.show();
        StringRequest request = new StringRequest(ApiConfig.DRIVER_ROUTE_INFO_URL + "?driver_id=" + driverId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("mytag", response);
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject routeObject = jsonObject.getJSONObject("route_info");
                            routeInfo = new Gson().fromJson(routeObject.toString(), RouteInfo.class);
                            tvBusNumber.setText(BusNumber);
                            tvRoute.setText(BusName);
                            tvBusNumber.setVisibility(View.VISIBLE);
                            tvRoute.setVisibility(View.VISIBLE);
                            tvConductorName.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DriverMapActivity.this, "parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(DriverMapActivity.this, "volley error", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(getApplicationContext()).add(request);


    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList.size() > 0) {
                return addressList.get(0).getAddressLine(0);
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}