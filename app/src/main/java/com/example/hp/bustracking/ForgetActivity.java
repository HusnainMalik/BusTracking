package com.example.hp.bustracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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

public class ForgetActivity extends AppCompatActivity {
    TextInputLayout tillforgetemail;
    EditText etforgetemail;
    TextInputLayout tillforgetpassword;
    EditText etforgetpassword;
    EditText etforgetconfirmpassword;
    Button btnforgetforgetpassword;
    TextView tvforgetlogin;
    TextInputLayout tillconfirmforgetpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        tillforgetemail=findViewById(R.id.till_forget_email);
        tillforgetpassword=findViewById(R.id.till_forget_password);
        etforgetemail=findViewById(R.id.et_forget_email);
        etforgetpassword=findViewById(R.id.et_forget_password);
        etforgetconfirmpassword=findViewById(R.id.et_forget_confirm_pass);
        tillconfirmforgetpass=findViewById(R.id.till_forget_confirm_pass);


        btnforgetforgetpassword= findViewById(R.id.btn_reset_password);

        btnforgetforgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final String email = etforgetemail.getText().toString().trim();
                final String pass = etforgetpassword.getText().toString().trim();
                String confirmpass = etforgetconfirmpassword.getText().toString();
                if(!pass.equals(confirmpass)){
                    tillconfirmforgetpass.setError("Password not matched");
                    tillconfirmforgetpass.requestFocus();
                }
                else{
                    final ProgressDialog pdilog = new ProgressDialog(ForgetActivity.this);
                    pdilog.setMessage("Please Wait");
                    pdilog.setCancelable(false);
                    pdilog.show();





                     StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.RESET_PASSWORD, new Response.Listener<String>() {
                         @Override
                         public void onResponse(String response) {
                             pdilog.dismiss();
                             try {
                                 JSONObject jsonObject = new JSONObject(response);
                                 int status = jsonObject.getInt("status");
                                 String message = jsonObject.getString("message");
                                 if (status == 0) {
                                     Toast.makeText(ForgetActivity.this, message, Toast.LENGTH_SHORT).show();
                                 } else if (status == 1) {
                                     Toast.makeText(ForgetActivity.this, message, Toast.LENGTH_SHORT).show();
                                 } else if (status == 2) {

                                     Toast.makeText(ForgetActivity.this, message, Toast.LENGTH_SHORT).show();
                                     startActivity(new Intent(ForgetActivity.this, StudentLoginActivity.class));
                                     finish();
                                 } else if (status == 3) {
                                     Toast.makeText(ForgetActivity.this, message, Toast.LENGTH_SHORT).show();

                                 }
                             } catch (JSONException e) {
                                 e.printStackTrace();
                                 Toast.makeText(ForgetActivity.this, "No Response", Toast.LENGTH_SHORT).show();
                             }

                         }
                     }, new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError error) {
                             pdilog.dismiss();
                             error.printStackTrace();
                             Toast.makeText(ForgetActivity.this, "No Network", Toast.LENGTH_SHORT).show();

                         }
                     }) {
                         @Override
                         protected Map<String, String> getParams() throws AuthFailureError {
                             Map<String, String> params = new HashMap<>();
                             params.put("user_email", email);
                             params.put("user_pass", pass);
                             return params;
                         }
                     };

                     RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                     queue.add(request);
                 }
            }
        });

    }
}
