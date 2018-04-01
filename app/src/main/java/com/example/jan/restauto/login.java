package com.example.jan.restauto;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class login extends AppCompatActivity implements View.OnClickListener{

    private EditText userIn;
    private EditText passwordIn;
    private Button loginBtn;
    private ProgressBar progressBar;
    public  OrderObject currentOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        userIn = (EditText) findViewById(R.id.userIn);
        passwordIn = (EditText) findViewById(R.id.passwordIn);
        loginBtn = (Button) findViewById(R.id.loginButton);
        progressBar = new ProgressBar(this);
        loginBtn.setOnClickListener(this);
    }

    private void userLogin() {
        String user = userIn.getText().toString().trim();
        String passwordToHash = passwordIn.getText().toString().trim();

        progressBar.setVisibility(ProgressBar.VISIBLE);
        if(TextUtils.isEmpty(user)) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Toast.makeText(this, "Please enter user", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(passwordToHash)) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        String generatedPassword = hashPassword(passwordToHash);
        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
        checkUser(user, generatedPassword);
    }

    public void checkUser(String user, final String Password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String getUser = "http://10.0.2.2:8080/get_users?username=";
        StringBuilder sb = new StringBuilder();
        sb.append(getUser);
        sb.append(user);
        sb.append("&password=");
        sb.append(Password);
        //Log.d("url",sb.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, sb.toString(), null, new Response.Listener< JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("response", response.toString());
                        int userID;

                        try {
                            userID = response.getInt("idUsers");
                            if(userID != 0) {
                                Intent intent = new Intent(login.this, MainActivity.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_LONG).show();
                        if(error.networkResponse != null && error.networkResponse.data != null) {
                            VolleyError err = new VolleyError(new String(error.networkResponse.data));
                            error = err;
                        }
                        Log.d("error", error.toString());
                    }
        });

        queue.add(jsonObjectRequest);
    }

    public String hashPassword(String password){
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Add password bytes to digest
            md.update(password.getBytes());
            // Get the hash's bytes
            byte[] bytes = md.digest();
            //Convert bytes[] to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            // Get complete hashed password in hex format
            generatedPassword = sb.toString();
            //Log.i("password", generatedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public void onClick(View v) {
        if(v == loginBtn) {
            userLogin();
        }
    }
}
