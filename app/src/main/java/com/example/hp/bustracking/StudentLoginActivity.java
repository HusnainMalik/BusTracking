package com.example.hp.bustracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentLoginActivity extends AppCompatActivity {
    TextInputLayout tillemail;
    EditText etemail;
    TextInputLayout tillpassword;
    EditText etpassword;
    Button btnlogin;
    TextView tvsignup;
    TextView forgetpass;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slogin);


        forgetpass = findViewById(R.id.tv_forgert_password);
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentLoginActivity.this,ForgetActivity.class));
            }
        });

        tillemail=(TextInputLayout) findViewById(R.id.till_email);
        etemail=findViewById(R.id.et_email);
        tillpassword=(TextInputLayout) findViewById(R.id.till_password);
        etpassword=findViewById(R.id.et_password);
        btnlogin=findViewById(R.id.btn_login);
        tvsignup=findViewById(R.id.tv_signup);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tillpassword.setError(null);
                tillemail.setError(null);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(StudentLoginActivity.this);
                String PlayerID = preferences.getString("playerid",null );
                Toast.makeText(StudentLoginActivity.this, PlayerID + "abc", Toast.LENGTH_SHORT).show();
                email =etemail.getText().toString().trim();
                 password =etpassword.getText().toString();
                if(email.isEmpty()){
                  // Toast.makeText(StudentLoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    tillemail.setError("Please Enter Email");
                    etemail.requestFocus();
                }else if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
                    tillemail.setError("Invalid Email");
                    etemail.requestFocus();

                }
                else if(password.isEmpty()){
                 //  Toast.makeText(StudentLoginActivity.this,"Please Enter Password",Toast.LENGTH_SHORT).show();
                    tillpassword.setError("Please Enter Password");
                    etpassword.requestFocus();

                }else if(password.length()<5){
                    tillpassword.setError("Password Must be 5 Character Long");
                    etpassword.requestFocus();

                }else if(password.length()>20){
                    tillpassword.setError("Password must be Less than 20 characters");
                    etpassword.requestFocus();
                }
                else {
                   // Toast.makeText(StudentLoginActivity.this,"Processed With Login",Toast.LENGTH_SHORT).show();
                    final ProgressDialog dialog=new ProgressDialog(StudentLoginActivity.this);
                    dialog.setMessage("Please wait");
                    dialog.setCancelable(false);
                    dialog.show();
                    StringRequest request=new StringRequest(Request.Method.POST, ApiConfig.SIGNIN_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            Log.i("ptag", response);
                            //Toast.makeText(loginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jObject=new JSONObject(response);
                                int status=jObject.getInt("status");
                                String message=jObject.getString("message");
                                if(status==0)
                                {
                                    Toast.makeText(StudentLoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    JSONObject userObject = jObject.getJSONObject("user");
                                    SessionHelper.createStudentLoginSession(StudentLoginActivity.this,userObject);

                                    Toast.makeText(StudentLoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                    Intent in = new Intent(StudentLoginActivity.this, Home2Activity.class);
                                    startActivity(in);
                                    finish();


                                   /* Bundle bundle = getIntent().getExtras();

                                    if (bundle == null) {
                                        finish();
                                    } else {
                                        String destination = bundle.getString("destination");
                                        if (destination!=null && destination.equals("address")) {


                                        }

                                    } */
                                }
                            } catch (JSONException e) {

                                e.printStackTrace();
                                Toast.makeText(StudentLoginActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
                                error.printStackTrace();

                                Toast.makeText(StudentLoginActivity.this,"Network Error" + error,Toast.LENGTH_LONG).show();

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params=new HashMap<>();
                            params.put("user_email",email);
                            params.put("user_pass",password);

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(StudentLoginActivity.this);
                             String PlayerID = preferences.getString("playerid",null );
                            params.put("playerid", PlayerID);
                            return  params;
                        }
                    };
                    RequestQueue queue= Volley.newRequestQueue(StudentLoginActivity.this);
                    queue.add(request);

                }

            }
        });
        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentLoginActivity.this,UsignupActivity.class));
                finish();

            }
        });
    }
}
