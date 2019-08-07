package com.example.hp.bustracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DriverLoginActivity extends AppCompatActivity {
    TextInputLayout tillphone;
    TextInputLayout tilldpassword;
    EditText etphone;
    EditText etdpassword;
    Button btndlogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        tillphone = findViewById(R.id.till_phone);
        etphone = findViewById(R.id.et_phone);
        tilldpassword = findViewById(R.id.till_dpassword);
        etdpassword = findViewById(R.id.et_dpassword);
        btndlogin = findViewById(R.id.btn_Dlogin);


        if (SessionHelper.getCurrentDriver(DriverLoginActivity.this) != null) {
            startActivity(new Intent(this, DriverHome.class));
            finish();
            return;

        }

        btndlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tillphone.setError(null);
                tilldpassword.setError(null);
                final String number = etphone.getText().toString().trim();
                final String password = etdpassword.getText().toString();
                if (number.isEmpty()) {
                    // Toast.makeText(SloginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    tillphone.setError("Please Enter Number");
                    etphone.requestFocus();
                } else if (!number.matches("^((\\+92)|(0092))-{0,1}\\d{3}-{0,1}\\d{7}$|^\\d{11}$|^\\d{4}-\\d{7}$")) {

                    tillphone.setError("Invalid phone Number");
                    etphone.requestFocus();

                } else if (number.length() < 11) {
                    tillphone.setError("Phone Number Must Be 11 Digit");
                    etphone.requestFocus();
                } else if (number.length() > 11) {
                    tillphone.setError("Phone Number Must Be 11 Digit ");
                    etphone.requestFocus();
                } else if (password.isEmpty()) {
                    //  Toast.makeText(SloginActivity.this,"Please Enter Password",Toast.LENGTH_SHORT).show();
                    tilldpassword.setError("Please Enter Password");
                    etdpassword.requestFocus();

                } else if (password.length() < 5) {
                    tilldpassword.setError("Password Must be 5 Character Long");
                    etdpassword.requestFocus();

                } else if (password.length() > 20) {
                    tilldpassword.setError("Password must be Less than 20 characters");
                    etdpassword.requestFocus();
                } else {
                    // Toast.makeText(SloginActivity.this,"Processed With Login",Toast.LENGTH_SHORT).show();
                    final ProgressDialog dialog = new ProgressDialog(DriverLoginActivity.this);
                    dialog.setMessage("Please wait");
                    dialog.setCancelable(false);
                    dialog.show();
                    StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.DRIVER_SIGNIN_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            Log.i("mytag", response);
                            //Toast.makeText(loginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jObject = new JSONObject(response);
                                int status = jObject.getInt("status");
                                String message = jObject.getString("message");
                                if (status == 0) {
                                    Toast.makeText(DriverLoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DriverLoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                    JSONObject userObject = jObject.getJSONObject("driver");
                                    Driver driver = new Gson().fromJson(userObject.toString(), Driver.class);
                                    SessionHelper.createDriverLoginSession(DriverLoginActivity.this, driver);

                                    Intent in = new Intent(DriverLoginActivity.this, DriverHome.class);
                                    startActivity(in);
                                    finish();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                                Toast.makeText(DriverLoginActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
                            error.printStackTrace();
                            Toast.makeText(DriverLoginActivity.this, "Volley Error", Toast.LENGTH_LONG).show();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("driver_number", number);
                            params.put("driver_pass", password);
                            return params;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(DriverLoginActivity.this);
                    queue.add(request);

                }

            }
        });


    }
}
