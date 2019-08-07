package com.example.hp.bustracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

public class SplashActivity extends AppCompatActivity {
    private Button btnstudlogin, btndriverlogin;
    RelativeLayout rellay1;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);


        }
    };
String PlayerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        // OneSignal Initialization
       /* OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();*/

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.addSubscriptionObserver(new OSSubscriptionObserver() {
            @Override
            public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
                PlayerID = stateChanges.getTo().getUserId();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                preferences.edit().
                        putString("playerid",PlayerID )
                        .apply();
            }
        });

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        btndriverlogin = findViewById(R.id.btn_driverlogin);
        handler.postDelayed(runnable, 2000);//@200 is the time of splash
        btndriverlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, DriverLoginActivity.class));
            }
        });
        btnstudlogin = findViewById(R.id.btn_studlogin);
        btnstudlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!SessionHelper.isStudentLoggedIn(SplashActivity.this)){
                    startActivity(new Intent(SplashActivity.this, StudentLoginActivity.class));
                }else{
                    Intent in = new Intent(SplashActivity.this, Home2Activity.class);
                    startActivity(in);
                }
            }
        });
    }
}