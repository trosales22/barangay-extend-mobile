package com.breadbuster.barangayextend.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.Activity.EditUserDetailsActivity;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Requests.MyJSONRequest;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.SharedPrefManager;
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.urls;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicInformationFragment extends Fragment {
    @InjectView(R.id.imgProfilePictureOfLoggedInUser) CircleImageView imgProfilePictureOfLoggedInUser;
    @InjectView(R.id.txtFullnameOfLoggedInUser) TextView txtFullnameOfLoggedInUser;
    @InjectView(R.id.btnUpdateInfoOfLoggedInUser) AppCompatButton btnUpdateInfoOfLoggedInUser;
    @InjectView(R.id.txtEmailAddressOfLoggedInUser) TextView txtEmailAddressOfLoggedInUser;
    @InjectView(R.id.txtUsernameOfLoggedInUser) TextView txtUsernameOfLoggedInUser;
    @InjectView(R.id.txtPhoneNumberOfLoggedInUser) TextView txtPhoneNumberOfLoggedInUser;
    @InjectView(R.id.txtGenderOfLoggedInUser) TextView txtGenderOfLoggedInUser;
    @InjectView(R.id.txtUserTypeOfLoggedInUser) TextView txtUserTypeOfLoggedInUser;
    @InjectView(R.id.txtBarangayOfLoggedInUser) TextView txtBarangayOfLoggedInUser;

    View view;
    urls url = new urls();
    UserBean userBean = new UserBean();

    public BasicInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_basic_information, container, false);
        ButterKnife.inject(this, view);

        showInfoOfLoggedInUser();

        btnUpdateInfoOfLoggedInUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditUserDetailsActivity.class));
            }
        });
        return view;
    }

    private void showInfoOfLoggedInUser(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            MyJSONRequest request = new MyJSONRequest(null,Request.Method.POST, url.getInfoOfLoggedInUserUrl() + SharedPrefManager.getInstance(getActivity()).getEmailAddressOrUsername(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();

                            try{
                                JSONArray userInfo = response.getJSONArray("userInfo");

                                for(int i = 0; i < userInfo.length(); i++) {
                                    JSONObject user = userInfo.getJSONObject(i);

                                    userBean.setProfilePicture(user.getString("ProfilePicture"));

                                    userBean.setFirstname(user.getString("Firstname"));
                                    userBean.setLastname(user.getString("Lastname"));
                                    userBean.setEmailAddress(user.getString("EmailAddress"));
                                    userBean.setUsername(user.getString("Username"));
                                    userBean.setPhoneNumber(user.getString("PhoneNumber"));
                                    userBean.setGender(user.getString("Gender"));
                                    userBean.setUserType(user.getString("UserType"));
                                    userBean.setBarangay(user.getString("Barangay"));

                                    if(userBean.getProfilePicture().isEmpty()){
                                        imgProfilePictureOfLoggedInUser.setImageResource(R.mipmap.ic_launcher_round);
                                    }else{
                                        Picasso.with(getActivity().getApplicationContext())
                                                .load(userBean.getProfilePicture())
                                                .error(R.drawable.ic_broken_image_black)
                                                .placeholder(R.drawable.ic_portrait)
                                                .into(imgProfilePictureOfLoggedInUser);
                                    }

                                    txtFullnameOfLoggedInUser.setText(userBean.getFirstname() + " " + userBean.getLastname());
                                    txtEmailAddressOfLoggedInUser.setText(userBean.getEmailAddress());
                                    txtUsernameOfLoggedInUser.setText(userBean.getUsername());
                                    txtPhoneNumberOfLoggedInUser.setText(userBean.getPhoneNumber());
                                    txtGenderOfLoggedInUser.setText(userBean.getGender());
                                    txtUserTypeOfLoggedInUser.setText(userBean.getUserType());
                                    txtBarangayOfLoggedInUser.setText(userBean.getBarangay());
                                }
                            }catch (Exception ex){
                                Log.e("Error Message", ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String message = null;
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }

                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });

            request.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
            MySingleton.getInstance(getActivity()).addToRequestQueue(request);
        }catch (Exception ex){
            Log.e("Error Message",ex.toString());
        }
    }

}
