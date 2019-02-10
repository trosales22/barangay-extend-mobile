package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ForgotPasswordActivity extends AppCompatActivity {
    @InjectView(R.id.txtEmailAddress) MaterialEditText txtEmailAddress;
    @InjectView(R.id.txtAnswer) MaterialEditText txtAnswer;
    @InjectView(R.id.txtPassword) MaterialEditText txtPassword;

    @InjectView(R.id.txtQuestion) TextView txtQuestion;

    @InjectView(R.id.btnCheckIfEmailExists) AppCompatButton btnCheckIfEmailExists;
    @InjectView(R.id.btnCheckAnswer) AppCompatButton btnCheckAnswer;
    @InjectView(R.id.btnChangePassword) AppCompatButton btnChangePassword;

    UserBean user = new UserBean();
    urls url = new urls();
    String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtQuestion.setVisibility(View.GONE);
        txtAnswer.setVisibility(View.GONE);
        btnCheckAnswer.setVisibility(View.GONE);
        txtPassword.setVisibility(View.GONE);
        btnChangePassword.setVisibility(View.GONE);

        btnCheckIfEmailExists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Validation.validateSingleMaterialEditText(txtEmailAddress)){
                    checkIfEmailExists();
                }

            }
        });

        btnCheckAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Validation.validateSingleMaterialEditText(txtAnswer)){
                    checkAnswerIfCorrect();
                }

            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Validation.validateSingleMaterialEditText(txtPassword)){
                    updatePassword();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void init(){
        user.setEmailAddress(txtEmailAddress.getText().toString());
        user.setSecurityQuestion_answer(txtAnswer.getText().toString());
        user.setPassword(txtPassword.getText().toString());
    }

    private void checkIfEmailExists(){
        init();

        MyStringRequest request = new MyStringRequest(null, Request.Method.POST, url.getCheckIfEmailExistsUrl() + user.getEmailAddress(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            if(response.toString().equals("invalid")){
                                Toast.makeText(ForgotPasswordActivity.this, "Email address is not existing in the database. Please try again!", Toast.LENGTH_LONG).show();
                            }else {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray infoOfUser_array = jsonObject.getJSONArray("infoOfUser");
                                for(int i = 0; i < infoOfUser_array.length(); i++) {
                                    JSONObject infoOfUser_object = infoOfUser_array.getJSONObject(i);

                                    txtQuestion.setText(infoOfUser_object.getString("SecretQuestion"));
                                    answer = infoOfUser_object.getString("SecretAnswer");
                                }

                                btnCheckIfEmailExists.setVisibility(View.GONE);
                                txtQuestion.setVisibility(View.VISIBLE);
                                txtAnswer.setVisibility(View.VISIBLE);
                                btnCheckAnswer.setVisibility(View.VISIBLE);
                            }
                        }catch (JSONException e) {
                            Toast.makeText(ForgotPasswordActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgotPasswordActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        MySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(request);
    }

    private void checkAnswerIfCorrect(){
        init();

        if(user.getSecurityQuestion_answer().equalsIgnoreCase(answer)){
            btnCheckAnswer.setVisibility(View.GONE);
            txtPassword.setVisibility(View.VISIBLE);
            btnChangePassword.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, "Invalid answer! Please try again!", Toast.LENGTH_LONG).show();
        }
    }

    private void updatePassword(){
        init();

        final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setMessage("Updating your password... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("userPassword",user.getPassword());

        MyStringRequest request = new MyStringRequest(parameters, Request.Method.POST, url.getUpdatePasswordUrl() + user.getEmailAddress(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, response, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        MySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(request);
    }
}
