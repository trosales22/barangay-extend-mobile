package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddSecurityQuestionActivity extends AppCompatActivity {
    @InjectView(R.id.txtSecretQuestion) MaterialEditText txtSecretQuestion;
    @InjectView(R.id.btnCloseAddQuestionInRegistration) AppCompatButton btnCloseAddQuestionInRegistration;
    @InjectView(R.id.btnAddQuestionInRegistration) AppCompatButton btnAddQuestionInRegistration;

    urls url = new urls();
    String question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_security_question);
        ButterKnife.inject(this);

        final MaterialEditText[] editTexts = new MaterialEditText[]{
                txtSecretQuestion
        };

        btnCloseAddQuestionInRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddQuestionInRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Validation.validateMaterialEditText(editTexts)){
                    final ProgressDialog progressDialog = new ProgressDialog(AddSecurityQuestionActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Adding question in registration... Please wait!");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    addQuestionInRegistration();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
            }
        });

    }

    private void init(){
        question = txtSecretQuestion.getText().toString();
    }

    private void addQuestionInRegistration(){
        init();

        Map<String, String> params = new HashMap<>();
        params.put("question", question);

        MyStringRequest request = new MyStringRequest(params, Request.Method.POST, url.getAddQuestionInRegistrationUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddSecurityQuestionActivity.this, response, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddSecurityQuestionActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                });

        MySingleton.getInstance(AddSecurityQuestionActivity.this).addToRequestQueue(request);
    }
}
