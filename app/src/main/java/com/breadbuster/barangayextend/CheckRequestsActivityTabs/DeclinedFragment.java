package com.breadbuster.barangayextend.CheckRequestsActivityTabs;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.breadbuster.barangayextend.Activity.HomeActivity;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Requests.CacheRequest;
import com.breadbuster.barangayextend.classes.Adapters.Adapter_RequestPosition;
import com.breadbuster.barangayextend.classes.Adapters.EmptyRecyclerViewAdapter;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Beans.RequestPositionBean;
import com.breadbuster.barangayextend.classes.DataObjects.RequestPositionDataObject;
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclinedFragment extends Fragment {
    @InjectView(R.id.recyclerView_declined) RecyclerView recyclerView_declined;
    @InjectView(R.id.swipeToRefresh_declined) SwipeRefreshLayout swipeToRefresh_declined;

    View view;
    urls url = new urls();
    RequestPositionBean requestBean = new RequestPositionBean();
    UserBean userBean = new UserBean();

    private List<RequestPositionDataObject> listOfRequestPosition;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter adapter_emptyRecyclerView;

    public static String requesterUserID,requesterLastUserType;

    public DeclinedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try{
            view = inflater.inflate(R.layout.fragment_declined, container, false);
            ButterKnife.inject(this, view);

            recyclerView_declined.setHasFixedSize(true);
            recyclerView_declined.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

            listOfRequestPosition = new ArrayList<>();

            showAllRequestPosition();

            swipeToRefresh_declined.setColorSchemeResources(R.color.colorPrimary);

            swipeToRefresh_declined.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(listOfRequestPosition != null){
                        listOfRequestPosition.clear();
                    }

                    showAllRequestPosition();
                    swipeToRefresh_declined.setRefreshing(false);
                }
            });
        }catch (Exception ex){
            Log.e("Error Message", ex.toString());
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void showAllRequestPosition(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            CacheRequest request = new CacheRequest(null, Request.Method.POST, url.getShowAllRequestBasedOnStatus() + "Declined&requesterBarangay=" + HomeActivity.userBarangay + "&requestedPosition=" + HomeActivity.userType,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            progressDialog.dismiss();

                            try {
                                final String jsonString = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers));

                                if(jsonString.equalsIgnoreCase("no_data")){
                                    adapter_emptyRecyclerView = new EmptyRecyclerViewAdapter();
                                    recyclerView_declined.setAdapter(adapter_emptyRecyclerView);
                                }else{
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    JSONArray request_array = jsonObject.getJSONArray("request");
                                    JSONArray requesterInfo_array = jsonObject.getJSONArray("requesterInfo");

                                    for(int i = 0; i < request_array.length(); i++){
                                        JSONObject request_object = request_array.getJSONObject(i);

                                        for(int a = 0; a < requesterInfo_array.length(); a++){
                                            JSONObject requesterInfo_object = requesterInfo_array.getJSONObject(a);

                                            userBean.setProfilePicture(requesterInfo_object.getString("ProfilePicture"));
                                            requesterUserID = request_object.getString("RequestedBy");
                                            requesterLastUserType = requesterInfo_object.getString("UserType");
                                            requestBean.setRequestedBy(requesterInfo_object.getString("Firstname") + " " + requesterInfo_object.getString("Lastname"));
                                            requestBean.setRequestedPosition(request_object.getString("RequestedPosition"));
                                            requestBean.setRequestDateAndTimeRegistered(request_object.getString("RequestDateAndTimeRegistered"));
                                            requestBean.setRequestDateAndTimeConfirmed(request_object.getString("RequestDateAndTimeConfirmed"));
                                            requestBean.setRequestStatus(request_object.getString("RequestStatus"));

                                            RequestPositionDataObject requestPositionDataObject = new RequestPositionDataObject(
                                                    requesterUserID,
                                                    requestBean.getRequestedBy(),
                                                    requesterLastUserType,
                                                    userBean.getProfilePicture(),
                                                    requestBean.getRequestedPosition(),
                                                    requestBean.getRequestDateAndTimeRegistered(),
                                                    requestBean.getRequestDateAndTimeConfirmed(),
                                                    requestBean.getRequestStatus()
                                            );

                                            listOfRequestPosition.add(requestPositionDataObject);
                                        }
                                    }

                                    adapter = new Adapter_RequestPosition(listOfRequestPosition,getActivity().getApplicationContext());
                                    recyclerView_declined.setAdapter(adapter);
                                }
                            }catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
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
                            } else if (error instanceof TimeoutError) {
                                message = "Connection TimeOut! Please check your internet connection.";
                            }

                            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    }
            );

            request.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

}
