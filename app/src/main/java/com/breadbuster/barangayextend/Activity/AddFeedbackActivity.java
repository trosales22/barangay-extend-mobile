package com.breadbuster.barangayextend.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Handlers.ErrorHandler;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddFeedbackActivity extends AppCompatActivity {
    @InjectView(R.id.txtFeedback) MaterialEditText txtFeedback;
    @InjectView(R.id.cmbFeedbackType) AppCompatSpinner cmbFeedbackType;
    @InjectView(R.id.btnCloseFeedbackAndHelp) AppCompatButton btnCloseFeedbackAndHelp;
    @InjectView(R.id.btnSendFeedback) AppCompatButton btnSendFeedback;

    urls url = new urls();
    String feedback,feedbackType,submittedBy,dateAndTimeSubmitted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);
        ButterKnife.inject(this);

        final MaterialEditText[] editTexts = new MaterialEditText[]{
                txtFeedback
        };

        initializeFeedbackType();

        btnCloseFeedbackAndHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Validation.validateMaterialEditText(editTexts)){
                    if(!validateDropdown(cmbFeedbackType,"CHOOSE FEEDBACK TYPE","Please choose feedback type!")){
                        sendFeedback();
                    }
                }
            }
        });
    }

    private void init(){
        feedback = txtFeedback.getText().toString();
        feedbackType = cmbFeedbackType.getSelectedItem().toString();

        submittedBy = HomeActivity.userID;

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
        Calendar dateAndTimePosted_calendar = Calendar.getInstance();

        dateAndTimeSubmitted = sdf.format(dateAndTimePosted_calendar.getTime());
    }

    private void initializeFeedbackType(){
        try{
            String[] feedbackType = new String[]{
                    "CHOOSE FEEDBACK TYPE",
                    "Comments","Suggestions","Bug Report"
            };

            final List<String> feedbackTypeList = new ArrayList<>(Arrays.asList(feedbackType));

            // Initializing an ArrayAdapter
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this,R.layout.spinner_item,feedbackTypeList){
                @Override
                public boolean isEnabled(int position){
                    if(position == 0)
                    {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position == 0){
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            cmbFeedbackType.setAdapter(spinnerArrayAdapter);

            cmbFeedbackType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItemText = (String) parent.getItemAtPosition(position);
                    // If user change the default selection
                    // First item is disable and it is used for hint
                    if(position > 0){
                        // Notify the selected item text
                        Toast.makeText
                                (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    private boolean validateDropdown(AppCompatSpinner spinner, String selectedText, String errorMessage){
        boolean isEmpty = false;

        if(spinner.getSelectedItem().toString() == selectedText){
            ErrorHandler.getInstance(AddFeedbackActivity.this).viewErrorMessage(AddFeedbackActivity.this,"Error Message",errorMessage);
            spinner.requestFocus();
            isEmpty = true;
        }

        return isEmpty;
    }

    private void sendFeedback(){
        init();

        Map<String, String> params = new HashMap<>();
        params.put("feedback", feedback);
        params.put("feedbackType", feedbackType);
        params.put("submittedBy", submittedBy);
        params.put("dateAndTimeSubmitted",dateAndTimeSubmitted);

        MyStringRequest request = new MyStringRequest(params, Request.Method.POST,url.getInsertFeedbackAndHelpUrl(),
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        ErrorHandler.getInstance(getApplicationContext()).viewToastErrorMessage(getApplicationContext(),response);
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.getInstance(getApplicationContext()).viewErrorMessage(getApplicationContext(),"Error Message",error.toString());
            }
        }
        );

        MySingleton.getInstance(AddFeedbackActivity.this).addToRequestQueue(request);
    }
}
