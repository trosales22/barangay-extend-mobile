package com.breadbuster.barangayextend.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.Activity.HomeActivity;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Newsfeed;
import com.breadbuster.barangayextend.classes.Adapters.EmptyRecyclerViewAdapter;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.DataObjects.NewsfeedDataObject;
import com.breadbuster.barangayextend.classes.Beans.TopicBean;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsfeedFragment extends Fragment implements SearchView.OnQueryTextListener{
    @InjectView(R.id.recyclerView_newsfeed) RecyclerView recyclerView_newsfeed;
    @InjectView(R.id.swipeToRefresh) SwipeRefreshLayout mSwipeRefreshLayout;

    View view;
    urls url = new urls();
    TopicBean topicBean = new TopicBean();

    private List<NewsfeedDataObject> newsfeedList;
    //private RecyclerView.Adapter adapter;
    private Adapter_Newsfeed adapter;
    private RecyclerView.Adapter adapter_emptyRecyclerView;

    public static String topicPostID,topicPosterUserID;

    private Map<String, String> parameters;

    public NewsfeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        ButterKnife.inject(this, view);

        recyclerView_newsfeed = view.findViewById(R.id.recyclerView_newsfeed);
        recyclerView_newsfeed.setHasFixedSize(true);
        recyclerView_newsfeed.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        newsfeedList = new ArrayList<>();
        parameters = new HashMap<>();
        parameters.put("userBarangay",HomeActivity.userBarangay);

        if(HomeActivity.userType == null){
            showAllPost(url.getShowAllPostUrl(),null);
        }else if(HomeActivity.userType.equalsIgnoreCase("Developer")){
            showAllPost(url.getShowAllPostUrl(),null);
        }else{
            showAllPost(url.getShowAllPostBasedOnUserBarangayUrl(),parameters);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(newsfeedList != null){
                    newsfeedList.clear();
                }

                if(HomeActivity.userType == null){
                    showAllPost(url.getShowAllPostUrl(),null);
                }else if(HomeActivity.userType.equalsIgnoreCase("Developer")){
                    showAllPost(url.getShowAllPostUrl(),null);
                }else{
                    showAllPost(url.getShowAllPostBasedOnUserBarangayUrl(),parameters);
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter = new Adapter_Newsfeed(newsfeedList, getActivity().getApplicationContext());
        recyclerView_newsfeed.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.home,menu);
        final MenuItem menuItem = menu.findItem(R.id.btnSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("Search by Topic Title or Topic Poster...");
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(menuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(newsfeedList);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    public void showAllPost(final String url, Map params){
        try{
            adapter_emptyRecyclerView = new EmptyRecyclerViewAdapter();
            recyclerView_newsfeed.setAdapter(adapter_emptyRecyclerView);

            MyStringRequest request = new MyStringRequest(params, Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equalsIgnoreCase("no_data")){
                                adapter_emptyRecyclerView = new EmptyRecyclerViewAdapter();
                                recyclerView_newsfeed.setAdapter(adapter_emptyRecyclerView);
                            }else{
                                try {
                                    JSONObject response_object = new JSONObject(response);

                                    JSONArray posts = response_object.getJSONArray("posts");

                                    for(int a = 0; a < posts.length(); a++){
                                        JSONObject post = posts.getJSONObject(a);

                                        JSONArray posterInfo_array = post.getJSONArray("posterInfo");
                                        for(int b = 0; b < posterInfo_array.length(); b++){
                                            JSONObject posterInfo_object = posterInfo_array.getJSONObject(b);

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
                                    recyclerView_newsfeed.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    Log.e("NewsfeedFragment", e.toString());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            adapter_emptyRecyclerView = new EmptyRecyclerViewAdapter();
                            recyclerView_newsfeed.setAdapter(adapter_emptyRecyclerView);

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

            adapter.notifyDataSetChanged();
        }catch(Exception ex){
            Log.e("NewsfeedFragment", ex.toString());
        }
    }

    private List<NewsfeedDataObject> filter(List<NewsfeedDataObject> models, String query) {
        query = query.toLowerCase();
        final List<NewsfeedDataObject> filteredModelList = new ArrayList<>();
        for (NewsfeedDataObject model : models) {
            final String topicTitle = model.getTopicTitle().toLowerCase();
            final String topicPoster = model.getTopic_postedBy().toLowerCase();

            if (topicTitle.contains(query) || topicPoster.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<NewsfeedDataObject> filteredModelList = filter(newsfeedList,newText);
        adapter.setFilter(filteredModelList);
        return true;
    }
}
