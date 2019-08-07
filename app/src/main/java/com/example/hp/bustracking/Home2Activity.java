package com.example.hp.bustracking;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Home2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SliderLayout sliderLayout;

    CardView cvLiveTrack;
    CardView cardStop;
    CardView cvtime;
    CardView cvlivetracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //My Codes Start From Here
        sliderLayout=findViewById(R.id.slider);
        cvLiveTrack=findViewById(R.id.cv_livetrack);
        cardStop = findViewById(R.id.card_stop);
        cvtime =findViewById(R.id.cv_timetable);
        cvlivetracking=findViewById(R.id.cv_livetracking);
        cvlivetracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  in = new Intent(Home2Activity.this,RoutesActivity.class);
                startActivity(in);

            }
        });

        cvtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Home2Activity.this,timedetailActivity.class);
                startActivity(in);
            }
        });


        cardStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Home2Activity.this, StopActivity.class);
                startActivity(in);
            }
        });

        cvLiveTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home2Activity.this,PermissionActivity.class);
                startActivity(intent);
            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Top);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
        fetchDataFromServer();
    }
    private  void fetchDataFromServer(){
        final ProgressDialog dialog = new ProgressDialog(Home2Activity.this);
        dialog.setMessage("Please Wait a while");
        dialog.setCancelable(false);
        dialog.show();

        final StringRequest request = new StringRequest(ApiConfig.MAIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Toast.makeText(Home2Activity.this,"Success",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jobject = new JSONObject(response);
                    JSONArray sliderArray = jobject.getJSONArray("slide_list");
                    for (int i = 0; i < sliderArray.length(); i++) {
                        JSONObject slideObject = sliderArray.getJSONObject(i);
                        String slidename = slideObject.getString("slide_name");
                        String slideImage = slideObject.getString("slide_image");

                        TextSliderView slide = new TextSliderView(Home2Activity.this);
                        slide.image(slideImage);
                        slide.description(slidename);
                        sliderLayout.addSlider(slide);

                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Home2Activity.this,"Parsing Error",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(Home2Activity.this, "Volley Error", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue queue = Volley .newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(Home2Activity.this,Home2Activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_email) {
           /* String url = "https://mail.google.com/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);*/
            try{
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "husnainmalik4334@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
                intent.putExtra(Intent.EXTRA_TEXT, "your_text");
                startActivity(intent);
            }catch(ActivityNotFoundException e){

            }

        } else if (id == R.id.nav_website) {
            String url = "https://www.bzu.edu.pk/v2_transport.php";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);

        } else if (id == R.id.nav_about) {
Intent intent = new Intent(Home2Activity.this,AboutActivity.class);
startActivity(intent);

        }
        else if (id == R.id.nav_share) {
            String url = "market://details?id=<package_name>";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);


        } else if (id == R.id.nav_send) {
            String url = "http://webmail.student.bzu.edu.pk/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);

        }
        else if(id==R.id.nav_logout){

         SessionHelper.logout(Home2Activity.this);
         Intent intent = new Intent(Home2Activity.this,SplashActivity.class);

         startActivity(intent);
         finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sliderLayout.startAutoCycle();
    }
}
