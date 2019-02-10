package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Handlers.ErrorHandler;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.urls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RequestForPositionActivity extends AppCompatActivity {
    @InjectView(R.id.cmbRequestedPosition) AppCompatSpinner cmbRequestedPosition;
    @InjectView(R.id.btnCloseRequestForPosition) AppCompatButton btnCloseRequestForPosition;
    @InjectView(R.id.btnConfirmRequestForPosition) AppCompatButton btnConfirmRequestForPosition;

    UserBean user = new UserBean();
    urls url = new urls();

    private ArrayList<String> listOfPossiblePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_for_position);
        ButterKnife.inject(this);

        listOfPossiblePosition = new ArrayList<>();

        getAllPossiblePosition();

        btnCloseRequestForPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnConfirmRequestForPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateDropdown(cmbRequestedPosition,"CHOOSE YOUR REQUESTED POSITION","Please choose your requested position!")){
                    confirmRequestForPosition();
                }
            }
        });
    }

    private void getAllPossiblePosition(){
        try{
            listOfPossiblePosition.clear();
            listOfPossiblePosition.add("CHOOSE YOUR REQUESTED POSITION");
            listOfPossiblePosition.add("Developer");
            listOfPossiblePosition.add("Council");
            listOfPossiblePosition.add("Employee");
            listOfPossiblePosition.add("Resident");

            removePossiblePosition("Developer",listOfPossiblePosition);
            removePossiblePosition("Council",listOfPossiblePosition);
            removePossiblePosition("Employee",listOfPossiblePosition);
            removePossiblePosition("Resident",listOfPossiblePosition);

            initializeArrayAdapter(listOfPossiblePosition,cmbRequestedPosition);
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    private void removePossiblePosition(String position,ArrayList<String> arrayList){
        if(HomeActivity.userType.equalsIgnoreCase(position)){
            arrayList.remove(position);
        }
    }

    private void init(){
        user.setUserType(cmbRequestedPosition.getSelectedItem().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
        Calendar dateAndTimeRegistered_calendar = Calendar.getInstance();

        user.setDateAndTimeRegistered(sdf.format(dateAndTimeRegistered_calendar.getTime()));
    }

    private void confirmRequestForPosition(){
        init();

        Map<String, String> parameters = new HashMap<>();

        parameters.put("requestedBy",HomeActivity.userID);
        parameters.put("requestedPosition",user.getUserType());
        parameters.put("requesterBarangay",HomeActivity.userBarangay);
        parameters.put("requestDateAndTimeRegistered",user.getDateAndTimeRegistered());

        final ProgressDialog progressDialog = new ProgressDialog(RequestForPositionActivity.this);
        progressDialog.setMessage("Submitting your request.. Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        MyStringRequest request = new MyStringRequest(parameters, Request.Method.POST, url.getAddRequestedPositionUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(RequestForPositionActivity.this, response, Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        ErrorHandler.getInstance(getApplicationContext()).viewErrorMessage(getApplicationContext(),"Error Message",error.toString());
                    }
                }
        );

        MySingleton.getInstance(RequestForPositionActivity.this).addToRequestQueue(request);
    }

    private boolean validateDropdown(AppCompatSpinner spinner, String selectedText, String errorMessage){
        boolean isEmpty = false;

        if(spinner.getSelectedItem().toString() == selectedText){
            ErrorHandler.getInstance(RequestForPositionActivity.this).viewErrorMessage(RequestForPositionActivity.this,"Error Message",errorMessage);
            spinner.requestFocus();
            isEmpty = true;
        }

        return isEmpty;
    }

    private void initializeArrayAdapter(ArrayList<String> arrayList, AppCompatSpinner spinner){
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                RequestForPositionActivity.this,R.layout.spinner_item,arrayList){
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
        spinner.setAdapter(spinnerArrayAdapter);
    }
}
