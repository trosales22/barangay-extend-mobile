package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.SharedPrefManager;
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.txtEmailOrUsername) MaterialEditText txtEmailOrUsername;
    @InjectView(R.id.txtPassword) MaterialEditText txtPassword;
    @InjectView(R.id.btnLogin) Button btnLogin;
    @InjectView(R.id.btnSignInOrRegister) TextView btnSignInOrRegister;
    @InjectView(R.id.btnForgotPassword) TextView btnForgotPassword;
    @InjectView(R.id.btnWhatsThis) TextView btnWhatsThis;

    ProgressDialog progressDialog;

    urls url = new urls();
    UserBean userBean = new UserBean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, HomeActivity.class));
            return;
        }

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                userBean.setEmailAddressOrUsername(txtEmailOrUsername.getText().toString());
                userBean.setPassword(txtPassword.getText().toString());

                try{
                    validate();
                }catch(Exception ex){
                    Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnWhatsThis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AppDescriptionActivity.class));
            }
        });

        btnSignInOrRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateAnAccountActivity.class));
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });

    }

    public void validate(){
        if(TextUtils.isEmpty(userBean.getEmailAddressOrUsername())){
            txtEmailOrUsername.setError("Please enter your email or username!");
            txtEmailOrUsername.requestFocus();
        }else if(TextUtils.isEmpty(userBean.getPassword())){
            txtPassword.setError("Please enter your password!");
            txtPassword.requestFocus();
        }else {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            LoginUser();
                        }
                    }, 3000);
        }
    }

    public void LoginUser(){
        final Map<String, String> parameters = new HashMap<>();

        parameters.put("emailAddress",userBean.getEmailAddressOrUsername());
        parameters.put("username",userBean.getEmailAddressOrUsername());
        parameters.put("password",userBean.getPassword());

        MyStringRequest request = new MyStringRequest(parameters, Request.Method.POST, url.getLoginUserUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        if(response.equalsIgnoreCase("invalid")){
                            Toast.makeText(MainActivity.this, "Invalid email/username and password! Please try again!", Toast.LENGTH_LONG).show();
                        }else{
                            SharedPrefManager.getInstance(getApplicationContext())
                                    .userLogin(userBean.getEmailAddressOrUsername());

                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);
    }
}
