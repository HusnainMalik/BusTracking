package com.example.hp.bustracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class UsignupActivity extends AppCompatActivity {
    TextInputLayout tillname;
    EditText etname;
    TextInputLayout tillemail;
    EditText etemail;
    TextInputLayout tillpassword;
    EditText etpassword;
    Button btnsignup;
    TextView tvlogin;
    TextInputLayout tillconfirmPassword;
    EditText etconfirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usignup);
        tillname=(TextInputLayout)findViewById(R.id.till_name);
        etname=findViewById(R.id.et_name);
        tillemail=(TextInputLayout)findViewById(R.id.till_email);
        etemail=findViewById(R.id.et_email);
        tillpassword=(TextInputLayout)findViewById(R.id.till_password);
        etpassword=findViewById(R.id.et_password);
        tillconfirmPassword=findViewById(R.id.till_confirm_pass);
        etconfirmpassword=findViewById(R.id.et_confirm_pass);


        btnsignup=findViewById(R.id.btn_signup);
        tvlogin=findViewById(R.id.tv_login);
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tillname.setError(null);
                tillemail.setError(null);
                tillpassword.setError(null);
                tillconfirmPassword.setError(null);
                final String name =etname.getText().toString().trim();
                final String email =etemail.getText().toString().trim();
                final String password =etpassword.getText().toString().trim();
                String confirmpassword =etconfirmpassword.getText().toString();

                if(name.isEmpty()){
                    tillname.setError("PLease Enter Name");
                    etname.requestFocus();
                }else if(!name.matches("^[a-zA-Z  .-]{3,}$")){
                    tillname.setError("Invalid Name");
                    etname.requestFocus();

                }else if(email.isEmpty()){
                    tillemail.setError("Please Enter Email");
                    etemail.requestFocus();
                }else if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
                    tillemail.setError("Invalid Email");
                    etemail.requestFocus();

                }else if(password.isEmpty()){
                    tillpassword.setError("Please Enter Password");
                    etpassword.requestFocus();
                }else if(password.length()<5){
                    tillpassword.setError("Password Must be 4 Character Long");
                    etpassword.requestFocus();

                }else if(password.length()>20){
                    tillpassword.setError("Password must be Less than 20 characters");
                    etpassword.requestFocus();
                }else if(!password.equals(confirmpassword)){
                    tillconfirmPassword.setError("Password not match");
                    etconfirmpassword.requestFocus();
                }else{
                    final ProgressDialog pdialod = new ProgressDialog(UsignupActivity.this);
                    pdialod.setMessage("Please Wait");
                    pdialod.setCancelable(false);
                    pdialod.show();
                    StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.SIGNUP_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pdialod.dismiss();
                            try {
                                JSONObject jObject = new JSONObject(response);
                                int status = jObject.getInt("status");
                                String message = jObject.getString("message");
                                if (status == 0) {
                                    Toast.makeText(UsignupActivity.this, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UsignupActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {

                                e.printStackTrace();
                                Toast.makeText(UsignupActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pdialod.dismiss();
                            error.printStackTrace();
                            Toast.makeText(UsignupActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", name);
                            params.put("email", email);
                            params.put("pass", password);
                            return params;
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(request);
                }

            }
        });
        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UsignupActivity.this,StudentLoginActivity.class));
                finish();
            }
        });

    }
}
