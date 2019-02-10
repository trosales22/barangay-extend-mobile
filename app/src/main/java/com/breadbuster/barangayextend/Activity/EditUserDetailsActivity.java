package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.Handlers.ErrorHandler;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.Requests.MyJSONRequest;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.SharedPrefManager;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserDetailsActivity extends AppCompatActivity {
    @InjectView(R.id.txtUserID) MaterialEditText txtUserID;
    @InjectView(R.id.txtFirstname) MaterialEditText txtFirstname;
    @InjectView(R.id.txtLastname) MaterialEditText txtLastname;
    @InjectView(R.id.txtUsername) MaterialEditText txtUsername;
    @InjectView(R.id.txtPhoneNumber) MaterialEditText txtPhoneNumber;
    @InjectView(R.id.txtPassword) MaterialEditText txtPassword;
    @InjectView(R.id.imgProfilePictureOfLoggedInUser) CircleImageView imgProfilePictureOfLoggedInUser;
    @InjectView(R.id.btnUpdateInfo) AppCompatButton btnUpdateInfo;
    @InjectView(R.id.btnShowOrHidePassword) TextView btnShowOrHidePassword;

    private int PICK_IMAGE_REQUEST = 1;

    urls url = new urls();
    UserBean userBean = new UserBean();
    UserBean user = new UserBean();

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);

        ButterKnife.inject(this);

        final MaterialEditText[] editTexts = new MaterialEditText[]{
                txtFirstname,txtLastname,txtUsername,txtPhoneNumber,txtPassword
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showInfoOfLoggedInUser();

        txtUserID.setKeyListener(null);

        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Validation.validateMaterialEditText(editTexts)){
                    updateInfoOfLoggedInUser();
                }
            }
        });

        btnShowOrHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnShowOrHidePassword.getText().equals("Show Password")){
                    btnShowOrHidePassword.setText("Hide Password");
                    btnShowOrHidePassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_off_black, 0, 0, 0);

                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                }else if(btnShowOrHidePassword.getText().equals("Hide Password")){
                    btnShowOrHidePassword.setText("Show Password");
                    btnShowOrHidePassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_black, 0, 0, 0);

                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        imgProfilePictureOfLoggedInUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // 2. pick image only
                intent.setType("image/*");
                // 3. start activity
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

                Uri uri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                    // rotating bitmap
                    String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                    Cursor cur = getContentResolver().query(uri, orientationColumn, null, null, null);
                    int orientation = -1;
                    if (cur != null && cur.moveToFirst()) {
                        orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                    }
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);

                    bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(), matrix, true);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    userBean.setProfilePicture(encodedImage);

                    imgProfilePictureOfLoggedInUser.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void showInfoOfLoggedInUser(){
        final ProgressDialog progressDialog = new ProgressDialog(EditUserDetailsActivity.this);
        progressDialog.setMessage("Loading data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        MyJSONRequest request = new MyJSONRequest(null,Request.Method.POST, url.getInfoOfLoggedInUserUrl() + SharedPrefManager.getInstance(EditUserDetailsActivity.this).getEmailAddressOrUsername(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();

                        try{
                            JSONArray userInfo = response.getJSONArray("userInfo");

                            for(int i = 0; i < userInfo.length(); i++) {
                                JSONObject user = userInfo.getJSONObject(i);

                                userBean.setProfilePicture(user.getString("ProfilePicture"));

                                userBean.setUserID(user.getString("UserID"));
                                userBean.setFirstname(user.getString("Firstname"));
                                userBean.setLastname(user.getString("Lastname"));
                                userBean.setUsername(user.getString("Username"));
                                userBean.setPhoneNumber(user.getString("PhoneNumber"));
                                userBean.setPassword(user.getString("Password"));

                                if(userBean.getProfilePicture().isEmpty()){
                                    imgProfilePictureOfLoggedInUser.setImageResource(R.mipmap.ic_launcher_round);
                                }else {
                                    Picasso.with(EditUserDetailsActivity.this)
                                            .load(userBean.getProfilePicture())
                                            .error(R.drawable.ic_broken_image_black)
                                            .placeholder(R.drawable.ic_portrait)
                                            .into(imgProfilePictureOfLoggedInUser);
                                }

                                txtUserID.setText(userBean.getUserID());
                                txtFirstname.setText(userBean.getFirstname());
                                txtLastname.setText(userBean.getLastname());
                                txtUsername.setText(userBean.getUsername());
                                txtPhoneNumber.setText(userBean.getPhoneNumber());
                                txtPassword.setText(userBean.getPassword());
                            }
                        }catch (Exception ex){
                            Log.e("Error Message", ex.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e("Error Message", error.toString());
            }
        });

        MySingleton.getInstance(EditUserDetailsActivity.this).addToRequestQueue(request);
    }

    private void init(){
        user.setUserID(txtUserID.getText().toString());
        user.setFirstname(txtFirstname.getText().toString());
        user.setLastname(txtLastname.getText().toString());
        user.setUsername(txtUsername.getText().toString());
        user.setPhoneNumber(txtPhoneNumber.getText().toString());
        user.setPassword(txtPassword.getText().toString());
    }

    private void updateInfoOfLoggedInUser(){
        try{
            init();

            Map<String, String> params = new HashMap<>();
            params.put("Content-Type", "image/jpeg; charset=utf-8");
            params.put("userID", user.getUserID());
            params.put("userFirstname", user.getFirstname());
            params.put("userLastname", user.getLastname());
            params.put("username", user.getUsername());
            params.put("userPhoneNumber", user.getPhoneNumber());
            params.put("userPassword", user.getPassword());
            params.put("userProfilePicture",userBean.getProfilePicture());

            final ProgressDialog progressDialog = new ProgressDialog(EditUserDetailsActivity.this);
            progressDialog.setMessage("Updating your information.. Please wait!");
            progressDialog.setCancelable(false);
            progressDialog.show();

            MyStringRequest request = new MyStringRequest(params,Request.Method.POST,url.getUpdateInfoOfLoggedInUserUrl() + user.getUserID(),
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            ErrorHandler.getInstance(getApplicationContext()).viewToastErrorMessage(getApplicationContext(),response);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                    },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    ErrorHandler.getInstance(getApplicationContext()).viewErrorMessage(getApplicationContext(),"Error Message",error.toString());
                }
            }
            );

            MySingleton.getInstance(EditUserDetailsActivity.this).addToRequestQueue(request);
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }
}
