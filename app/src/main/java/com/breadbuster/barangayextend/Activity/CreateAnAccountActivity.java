package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MenuItem;
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
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.Handlers.ErrorHandler;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.InputValidatorHelper;
import com.breadbuster.barangayextend.classes.JSON;
import com.breadbuster.barangayextend.classes.Requests.MyJSONRequest;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CreateAnAccountActivity extends AppCompatActivity {
    @InjectView(R.id.txtFirstname) MaterialEditText txtFirstname;
    @InjectView(R.id.txtLastname) MaterialEditText txtLastname;
    @InjectView(R.id.txtEmailAddress) MaterialEditText txtEmailAddress;
    @InjectView(R.id.txtUsername) MaterialEditText txtUsername;
    @InjectView(R.id.txtPhoneNumber) MaterialEditText txtPhoneNumber;
    @InjectView(R.id.cmbGender) AppCompatSpinner cmbGender;
    @InjectView(R.id.cmbBarangay) AppCompatSpinner cmbBarangay;
    @InjectView(R.id.cmbSecretQuestion) AppCompatSpinner cmbSecretQuestion;
    @InjectView(R.id.txtSecretAnswer) MaterialEditText txtSecretAnswer;
    @InjectView(R.id.txtPassword) MaterialEditText txtPassword;
    @InjectView(R.id.btnCreateAccount) AppCompatButton btnCreateAccount;

    urls url = new urls();
    UserBean user = new UserBean();

    private ArrayList<String> barangay,securityQuestion;
    private InputValidatorHelper inputValidatorHelper = new InputValidatorHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_an_account);
            ButterKnife.inject(this);

            final MaterialEditText[] editTexts = new MaterialEditText[]{
                    txtFirstname,txtLastname,txtEmailAddress,txtUsername,
                    txtPhoneNumber,txtPassword,txtSecretAnswer
            };

            barangay = new ArrayList<>();
            securityQuestion = new ArrayList<>();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            initializeGenderDropdown();
            getAllAvailableBarangay();
            getAllAvailableSecurityQuestions();

            btnCreateAccount.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(!Validation.validateMaterialEditText(editTexts)){
                        if(!inputValidatorHelper.isValid(txtFirstname.getText().toString(),"[A-Z][a-z]+( [A-Z][a-z]+)")){
                            txtFirstname.setError("Invalid firstname...");
                        }

                        if(!inputValidatorHelper.isValid(txtLastname.getText().toString(),"[a-zA-z]+([ '-][a-zA-Z]+)*")){
                            txtLastname.setError("Invalid lastname...");
                        }

                        if(!inputValidatorHelper.isValid(txtUsername.getText().toString(),"^[a-z0-9._-]{2,25}$")){
                            txtLastname.setError("Invalid username...");
                        }

                        if(!inputValidatorHelper.isValid(txtEmailAddress.getText().toString(),"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
                            txtEmailAddress.setError("Invalid email address...");
                        }

                        if(!inputValidatorHelper.isValidPassword(txtPassword.getText().toString(),false)){
                            txtPassword.setError("Invalid password...");
                        }

                        if(!validateDropdown(cmbBarangay,"CHOOSE YOUR BARANGAY","Please choose your barangay!")) {
                            if (!validateDropdown(cmbGender, "CHOOSE YOUR GENDER", "Please choose your gender!")) {
                                if (!validateDropdown(cmbSecretQuestion, "CHOOSE SECURITY QUESTION", "Please choose security question!")) {
                                    final ProgressDialog progressDialog = new ProgressDialog(CreateAnAccountActivity.this);
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setMessage("Creating your account... Please wait!");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();

                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    insertUser();
                                                    progressDialog.dismiss();
                                                }
                                            }, 3000);
                                }
                            }
                        }
                    }
                }
            });

            spinnerOnItemSelected(cmbBarangay);
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
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

    private void initializeGenderDropdown(){
        try {
            String[] gender = new String[]{
                    "CHOOSE YOUR GENDER",
                    "Male",
                    "Female"
            };

            final List<String> genderList = new ArrayList<>(Arrays.asList(gender));

            // Initializing an ArrayAdapter
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, genderList) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            cmbGender.setAdapter(spinnerArrayAdapter);

            spinnerOnItemSelected(cmbGender);
        }catch(Exception ex){
            ErrorHandler.getInstance(this).viewErrorMessage(this,"Error Message",ex.toString());
        }
    }

    private void init(){
        try {
            user.setFirstname(txtFirstname.getText().toString());
            user.setLastname(txtLastname.getText().toString());
            user.setEmailAddress(txtEmailAddress.getText().toString());
            user.setUsername(txtUsername.getText().toString());
            user.setPhoneNumber(txtPhoneNumber.getText().toString());
            user.setGender(cmbGender.getSelectedItem().toString());
            user.setUserType("Resident");
            user.setUserStatus("Verified");
            user.setBarangay(cmbBarangay.getSelectedItem().toString());
            user.setPassword(txtPassword.getText().toString());
            user.setSecurityQuestion(cmbSecretQuestion.getSelectedItem().toString());
            user.setSecurityQuestion_answer(txtSecretAnswer.getText().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
            Calendar dateAndTimeRegistered_calendar = Calendar.getInstance();

            user.setDateAndTimeRegistered(sdf.format(dateAndTimeRegistered_calendar.getTime()));
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    private boolean validateDropdown(AppCompatSpinner spinner,String selectedText,String errorMessage){
        boolean isEmpty = false;

        if(spinner.getSelectedItem().toString() == selectedText){
            ErrorHandler.getInstance(CreateAnAccountActivity.this).viewToastErrorMessage(CreateAnAccountActivity.this,errorMessage);
            spinner.requestFocus();
            isEmpty = true;
        }

        return isEmpty;
    }

    public void insertUser(){
        try{
            init();

            Map<String, String> parameters = new HashMap<>();

            parameters.put("firstname",user.getFirstname());
            parameters.put("lastname",user.getLastname());
            parameters.put("emailAddress",user.getEmailAddress());
            parameters.put("username",user.getUsername());
            parameters.put("phoneNumber",user.getPhoneNumber());
            parameters.put("gender",user.getGender());
            parameters.put("userType",user.getUserType());
            parameters.put("userStatus",user.getUserStatus());
            parameters.put("barangay",user.getBarangay());
            parameters.put("password",user.getPassword());
            parameters.put("securityQuestion",user.getSecurityQuestion());
            parameters.put("securityQuestion_answer",user.getSecurityQuestion_answer());
            parameters.put("dateAndTimeRegistered",user.getDateAndTimeRegistered());

            MyStringRequest request = new MyStringRequest(parameters, Request.Method.POST, url.getInsertUserUrl(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(CreateAnAccountActivity.this, response, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ErrorHandler.getInstance(getApplicationContext()).viewErrorMessage(getApplicationContext(),"Error Message",error.toString());
                        }
                    }
            );

            MySingleton.getInstance(CreateAnAccountActivity.this).addToRequestQueue(request);
        }catch(Exception ex){
            ErrorHandler.getInstance(this).viewErrorMessage(this,"Error Message",ex.toString());
        }
    }

    public void getAllAvailableBarangay(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(CreateAnAccountActivity.this);
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            MyJSONRequest request = new MyJSONRequest(null, Request.Method.POST, url.getGetAllAvailableBarangayUrl(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();

                            barangay.clear();
                            barangay.add("CHOOSE YOUR BARANGAY");

                            JSON.getJSONData("barangay","BarangayName",barangay,response);

                            // Initializing an ArrayAdapter
                            initializeArrayAdapter(barangay,cmbBarangay);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Log.e("Error Message", error.toString());
                        }
                    }
            );

            MySingleton.getInstance(CreateAnAccountActivity.this).addToRequestQueue(request);
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    public void getAllAvailableSecurityQuestions(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(CreateAnAccountActivity.this);
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            MyJSONRequest request = new MyJSONRequest(null,Request.Method.POST, url.getGetAllAvailableSecurityQuestionsUrl(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();

                            securityQuestion.clear();
                            securityQuestion.add("CHOOSE SECURITY QUESTION");

                            JSON.getJSONData("securityQuestions","Question",securityQuestion,response);

                            // Initializing an ArrayAdapter
                            initializeArrayAdapter(securityQuestion,cmbSecretQuestion);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Log.e("Error Message", error.toString());
                        }
                    }
            );

            MySingleton.getInstance(CreateAnAccountActivity.this).addToRequestQueue(request);
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    private void initializeArrayAdapter(ArrayList<String> arrayList, AppCompatSpinner spinner){
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                CreateAnAccountActivity.this,R.layout.spinner_item,arrayList){
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

    private void spinnerOnItemSelected(AppCompatSpinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
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
    }
}
