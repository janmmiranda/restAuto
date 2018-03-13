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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class login extends AppCompatActivity implements View.OnClickListener{

    private EditText userIn;
    private EditText passwordIn;
    private Button loginBtn;
    private ProgressBar progressBar;

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

        if(checkUser(user, generatedPassword)) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            //Toast.makeText(this,"success", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Log.w("", "login failure");
            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_LONG).show();
        }

    }

    public boolean checkUser(String user, String Password) {
        boolean result = true;
        /*
        check if valid user
         */

        return result;
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
            Log.i("password", generatedPassword);
            //System.out.println(generatedPassword);
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
