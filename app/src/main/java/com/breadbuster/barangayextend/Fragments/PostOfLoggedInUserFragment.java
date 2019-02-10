package com.breadbuster.barangayextend.Fragments;


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
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Newsfeed;
import com.breadbuster.barangayextend.classes.Requests.CacheRequest;
import com.breadbuster.barangayextend.classes.Adapters.EmptyRecyclerViewAdapter;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.DataObjects.NewsfeedDataObject;
import com.breadbuster.barangayextend.classes.Beans.TopicBean;
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
public class PostOfLoggedInUserFragment extends Fragment {
    @InjectView(R.id.recyclerView_postsOfLoggedInUser) RecyclerView recyclerView_postsOfLoggedInUser;
    @InjectView(R.id.swipeToRefresh) SwipeRefreshLayout swipeToRefresh;

    View view;
    urls url = new urls();
    TopicBean topicBean = new TopicBean();

    private List<NewsfeedDataObject> newsfeedList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter adapter_emptyRecyclerView;

    private static String topicPostID,topicPosterUserID;

    public PostOfLoggedInUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post_of_logged_in_user, container, false);
        ButterKnife.inject(this, view);

        recyclerView_postsOfLoggedInUser.setHasFixedSize(true);
        recyclerView_postsOfLoggedInUser.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        swipeToRefresh.setColorSchemeResources(R.color.colorPrimary);

        newsfeedList = new ArrayList<>();

        showAllPostOfLoggedInUser();

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(newsfeedList != null){
                    newsfeedList.clear();
                }

                showAllPostOfLoggedInUser();

                swipeToRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    private void showAllPostOfLoggedInUser(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            CacheRequest request = new CacheRequest(null, Request.Method.POST, url.getShowAllPostOfLoggedInUserUrl() + HomeActivity.userID,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            progressDialog.dismiss();

                            try{
                                final String jsonString = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers));

                                if(jsonString.equalsIgnoreCase("no_data")){
                                    adapter_emptyRecyclerView = new EmptyRecyclerViewAdapter();
                                    recyclerView_postsOfLoggedInUser.setAdapter(adapter_emptyRecyclerView);
                                }else {
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    JSONArray posts = jsonObject.getJSONArray("posts");
                                    JSONArray posterInfo_array = jsonObject.getJSONArray("posterInfo");
                                    for (int i = 0; i < posts.length(); i++) {
                                        JSONObject post = posts.getJSONObject(i);

                                        for (int a = 0; a < posterInfo_array.length(); a++) {
                                            JSONObject posterInfo_object = posterInfo_array.getJSONObject(a);

                                            topicPostID = post.getString("ID");
                                            topicPosterUserID = post.getString("TopicPostedBy");
                                            topicBean.setTopicPostedBy(posterInfo_object.getString("Firstname") + " " + posterInfo_object.getString("Lastname"));
                                            topicBean.setTopicPostedBy_profilePicture(posterInfo_object.getString("ProfilePicture"));

                                            topicBean.setTopicType(post.getString("TopicType"));
                                            topicBean.setTopicDateAndTimePosted(post.getString("TopicDateAndTimePosted"));
                                            topicBean.setTopicLocationAddress(post.getString("TopicLocationAddress"));
                                            topicBean.setTopicTitle(post.getString("TopicTitle"));
                                            topicBean.setTopicDescription(post.getString("TopicDescription"));
                                            topicBean.setTopicPosterUserBarangay("#" + post.getString("TopicPosterBarangay"));
                                            topicBean.setTopicPosterUserType("#" + posterInfo_object.getString("UserType"));

                                            topicBean.setTopicImage(post.getString("TopicImage"));

                                            NewsfeedDataObject newsfeedDataObject = new NewsfeedDataObject(
                                                    topicPostID,
                                                    topicPosterUserID,
                                                    topicBean.getTopicPostedBy(),
                                                    topicBean.getTopicType(),
                                                    topicBean.getTopicDateAndTimePosted(),
                                                    topicBean.getTopicLocationAddress(),
                                                    topicBean.getTopicTitle(),
                                                    topicBean.getTopicDescription(),
                                                    topicBean.getTopicPostedBy_profilePicture(),
                                                    topicBean.getTopicImage(),
                                                    topicBean.getTopicPosterUserBarangay(),
                                                    topicBean.getTopicPosterUserType()
                                            );

                                            newsfeedList.add(newsfeedDataObject);
                                        }
                                    }

                                    adapter = new Adapter_Newsfeed(newsfeedList, getActivity().getApplicationContext());
                                    recyclerView_postsOfLoggedInUser.setAdapter(adapter);
                                }
                            }catch (UnsupportedEncodingException | JSONException e) {
                                Log.e("Error Message", e.toString());
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
                            } else if (error instanceof NoConnectionError) {
                                message = "Cannot connect to Internet...Please check your connection!";
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
            Log.e("Error Message",ex.toString());
        }
    }

}
