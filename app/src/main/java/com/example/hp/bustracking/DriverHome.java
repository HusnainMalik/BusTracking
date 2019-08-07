package com.example.hp.bustracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverHome extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvDriverName;
    private TextView tvRoute;
    private TextView tvBusNumber;
    private TextView tvConductorName;
    private Button btnStartDriving;
    private RouteInfo routeInfo;
    private ProgressDialog progressDialog;
    private ImageView ivprofile;
    private Button btnlogout;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;

    private TextView tvUpdateStatus;
    private TextView tvLastUpdate;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFuseLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        tvDriverName = findViewById(R.id.tv_driver_name);
        tvRoute = findViewById(R.id.tv_routename);
        tvBusNumber = findViewById(R.id.tv_bus_number);
        tvConductorName = findViewById(R.id.tv_conductor_name);
        btnStartDriving = findViewById(R.id.btn_start_driving);
        btnlogout = findViewById(R.id.btn_logoutDriver);
        ivprofile = findViewById(R.id.iv_profile);
        tvUpdateStatus = findViewById(R.id.tv_update_status);
        tvLastUpdate = findViewById(R.id.tv_last_update);

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStartDriving.getText().equals("Stop Driving")) {
                    Toast.makeText(DriverHome.this, "Please stop bus then logout", Toast.LENGTH_SHORT).show();
                    return;
                }
                SessionHelper.logoutDriver(DriverHome.this);
                Intent intent = new Intent(DriverHome.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Dexter.withActivity(DriverHome.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(DriverHome.this);
                        mFuseLocationProviderClient = LocationServices.getFusedLocationProviderClient(DriverHome.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DriverHome.this);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to Access Device location is permanently denied you need to go to setting to Allow permissiion")
                                    .setNegativeButton("Cacnel", null)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(DriverHome.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        final Driver currentDriver = SessionHelper.getCurrentDriver(this);

        tvDriverName.setText(currentDriver.driver_name);
        tvConductorName.setText(currentDriver.conductor_name);
        Picasso.with(ivprofile.getContext()).load(String.valueOf(currentDriver.driver_image)).into(ivprofile);


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
                            tvBusNumber.setText(routeInfo.bus_no);
                            tvRoute.setText(routeInfo.bus_name);
                            tvBusNumber.setVisibility(View.VISIBLE);
                            tvRoute.setVisibility(View.VISIBLE);
                            tvConductorName.setVisibility(View.VISIBLE);
                            btnStartDriving.setVisibility(View.VISIBLE);

                            if (routeInfo.latitude != 0 && routeInfo.longitude != 0) {
                                tvLastUpdate.setText("Last Updated: " + new SimpleDateFormat("dd MMMM, yyyy - hh:mm:ss a").format(routeInfo.locationUpdateTime * 1000));
                                tvLastUpdate.setVisibility(View.VISIBLE);
                                String address = getAddressFromLatLng(routeInfo.latitude, routeInfo.longitude);
                                if (address.isEmpty()) {
                                    tvUpdateStatus.setText("Last Location: " + routeInfo.latitude + ", " + routeInfo.longitude);
                                } else {
                                    tvUpdateStatus.setText("Last Location: " + address);
                                }
                                tvUpdateStatus.setVisibility(View.VISIBLE);
                            }

                            if (routeInfo.bus_status == 1) {
                                btnStartDriving.setText("Stop Driving");
                                btnStartDriving.setBackgroundColor(getResources().getColor(R.color.btn_stop_driving_color));
                                startLocationUpdates();
                            } else {
                                btnStartDriving.setText("Start Driving");
                                btnStartDriving.setBackgroundColor(getResources().getColor(R.color.btn_start_driving_color));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DriverHome.this, "parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(DriverHome.this, "volley error", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(getApplicationContext()).add(request);

        btnStartDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStartDriving.getText().equals("Stop Driving")) {
                    updateBusStatus(routeInfo.bus_id, 0);
                } else {
                    updateBusStatus(routeInfo.bus_id, 1);
                }
            }
        });
    }

    private void updateBusStatus(final int busId, final int busStatus) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.UPDATE_BUS_STATUS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    Log.i("mytag", response);
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");
                    if (status == 0) {
                        Toast.makeText(DriverHome.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        if (busStatus == 1) {
                            btnStartDriving.setText("Stop Driving");
                            btnStartDriving.setBackgroundColor(getResources().getColor(R.color.btn_stop_driving_color));
                            startLocationUpdates();
                        } else {
                            btnStartDriving.setText("Start Driving");
                            btnStartDriving.setBackgroundColor(getResources().getColor(R.color.btn_start_driving_color));
                            stopLocationUpdates();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DriverHome.this, "parsing error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(DriverHome.this, "Volley error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("bus_id", String.valueOf(busId));
                params.put("status", String.valueOf(busStatus));
                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (locationCallback != null)
            mFuseLocationProviderClient.removeLocationUpdates(locationCallback);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }

                Log.i("mytag", "location callback rcivd: " + locationResult.getLastLocation());

                /*Location newLocation = locationResult.getLastLocation();
                if (mLastKnownLocation.getLatitude() == newLocation.getLatitude() && mLastKnownLocation.getLongitude() == newLocation.getLongitude()) {
                    return;
                }*/

                mLastKnownLocation = locationResult.getLastLocation();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                markerOptions.title("Your Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.clear();
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 15));

                tvUpdateStatus.setText("Location Received: " + mLastKnownLocation.getLatitude() + ", " + mLastKnownLocation.getLongitude());
                tvUpdateStatus.setVisibility(View.VISIBLE);

                uploadLocationToDatabase(locationResult.getLastLocation());
            }
        };
        mFuseLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void uploadLocationToDatabase(final Location location) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.UPDATE_LOCATION_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("mytag", response);
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");
                    if (status == 1) {
                        tvUpdateStatus.setText("Location Received: " + mLastKnownLocation.getLatitude() + ", " + mLastKnownLocation.getLongitude());
                        tvUpdateStatus.setVisibility(View.VISIBLE);
                        long lastUpdateTime = jsonObject.getLong("locationUpdateTime");
                        tvLastUpdate.setText("Last Updated: " + new SimpleDateFormat("dd MMMM, yyyy hh:mm:ss a").format(lastUpdateTime * 1000));
                        tvLastUpdate.setVisibility(View.VISIBLE);
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(location.getLatitude()));
                params.put("longitude", String.valueOf(location.getLongitude()));
                params.put("bus_id", String.valueOf(routeInfo.bus_id));
                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void stopLocationUpdates() {
        if (locationCallback != null)
            mFuseLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        //check if gps is enable or not amd then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(DriverHome.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(DriverHome.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });
        task.addOnFailureListener(DriverHome.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(DriverHome.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
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
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 15));
                                LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                double latitude = mLastKnownLocation.getLatitude();
                                double longitude = mLastKnownLocation.getLongitude();
                                Toast.makeText(DriverHome.this, " Longitude & Latitude = " + latitude + "," + longitude, Toast.LENGTH_LONG).show();
                                //Toast.makeText(MapActivity.this, "Longitude = "+longitude, Toast.LENGTH_SHORT).show();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title("Your Position");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mMap.addMarker(markerOptions);
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
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                                        markerOptions.title("Your Position");
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                        mMap.addMarker(markerOptions);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 15));
                                        mFuseLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFuseLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        } else {
                            Toast.makeText(DriverHome.this, "Unable to get last Location", Toast.LENGTH_SHORT).show();
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