package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddQuestionWithAnswerActivity extends AppCompatActivity {
    @InjectView(R.id.txtQuestion) MaterialEditText txtQuestion;
    @InjectView(R.id.txtAnswer) MaterialEditText txtAnswer;
    @InjectView(R.id.btnAddQuestionWithAnswerForFAQ) AppCompatButton btnAddQuestionWithAnswerForFAQ;
    @InjectView(R.id.btnCloseAddQuestionWithAnswerForFAQ) AppCompatButton btnCloseAddQuestionWithAnswerForFAQ;

    urls url = new urls();
    String question,answer,dateAndTimeAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question_with_answer);
        ButterKnife.inject(this);

        final MaterialEditText[] editTexts = new MaterialEditText[]{
                txtQuestion,txtAnswer
        };

        btnCloseAddQuestionWithAnswerForFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddQuestionWithAnswerForFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Validation.validateMaterialEditText(editTexts)){
                    final ProgressDialog progressDialog = new ProgressDialog(AddQuestionWithAnswerActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Adding question w/ answer for FAQ... Please wait!");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    insertQuestionWithAnswerForFAQ();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
            }
        });
    }

    private void init(){
        question = txtQuestion.getText().toString();
        answer = txtAnswer.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
        Calendar dateAndTimePosted_calendar = Calendar.getInstance();

        dateAndTimeAdded = sdf.format(dateAndTimePosted_calendar.getTime());
    }

    private void insertQuestionWithAnswerForFAQ(){
        init();

        Map<String, String> params = new HashMap<>();
        //params.put("Content-Type", "image/jpeg; charset=utf-8");
        params.put("question", question);
        params.put("answer", answer);
        params.put("dateAndTimeAdded", dateAndTimeAdded);

        MyStringRequest request = new MyStringRequest(params, Request.Method.POST,url.getInsertQuestionForFAQUrl(),
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddQuestionWithAnswerActivity.this, response, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddQuestionWithAnswerActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        }
        );

        MySingleton.getInstance(AddQuestionWithAnswerActivity.this).addToRequestQueue(request);
    }
}
