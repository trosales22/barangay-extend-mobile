package com.breadbuster.barangayextend.Fragments;

import android.app.ProgressDialog;
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
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Users;
import com.breadbuster.barangayextend.classes.Requests.CacheRequest;
import com.breadbuster.barangayextend.classes.Adapters.EmptyRecyclerViewAdapter;
import com.breadbuster.barangayextend.classes.Requests.MyJSONRequest;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.DataObjects.UserDataObject;
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
public class UsersFragment extends Fragment implements SearchView.OnQueryTextListener{
    @InjectView(R.id.recyclerView_users) RecyclerView recyclerView;
    @InjectView(R.id.swipeToRefresh) SwipeRefreshLayout mSwipeRefreshLayout;

    View view;
    urls url = new urls();
    UserBean userBean = new UserBean();

    private List<UserDataObject> listUsers;
    //private RecyclerView.Adapter adapter;
    private Adapter_Users adapter;
    private RecyclerView.Adapter adapter_emptyRecyclerView;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_users, container, false);
            ButterKnife.inject(this, view);

            recyclerView = view.findViewById(R.id.recyclerView_users);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        listUsers = new ArrayList<>();
        showAllUsers();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(listUsers != null){
                    listUsers.clear();
                }

                showAllUsers();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter = new Adapter_Users(listUsers,getActivity().getApplicationContext(),UsersFragment.this.getFragmentManager());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.home,menu);
        final MenuItem menuItem = menu.findItem(R.id.btnSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("Search by Firstname, Lastname or Username...");
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(menuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(listUsers);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    public void showAllUsers(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            CacheRequest request = new CacheRequest(null, Request.Method.POST, url.getShowAllUsersUrl(),
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            progressDialog.dismiss();

                            try {
                                final String jsonString = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers));

                                if(jsonString.equalsIgnoreCase("no_data")){
                                    adapter_emptyRecyclerView = new EmptyRecyclerViewAdapter();
                                    recyclerView.setAdapter(adapter_emptyRecyclerView);
                                }else {
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    JSONArray users = jsonObject.getJSONArray("users");
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = users.getJSONObject(i);

                                        userBean.setProfilePicture(user.getString("ProfilePicture"));
                                        userBean.setFirstname(user.getString("Firstname"));
                                        userBean.setLastname(user.getString("Lastname"));
                                        userBean.setEmailAddress(user.getString("EmailAddress"));
                                        userBean.setUsername(user.getString("Username"));
                                        userBean.setPhoneNumber(user.getString("PhoneNumber"));
                                        userBean.setGender(user.getString("Gender"));
                                        userBean.setUserType(user.getString("UserType"));
                                        userBean.setBarangay(user.getString("Barangay"));

                                        UserDataObject userData = new UserDataObject(
                                                userBean.getFirstname(),
                                                userBean.getLastname(),
                                                userBean.getEmailAddress(),
                                                userBean.getUsername(),
                                                userBean.getPhoneNumber(),
                                                userBean.getGender(),
                                                userBean.getUserType(),
                                                userBean.getBarangay(),
                                                userBean.getProfilePicture()
                                        );

                                        listUsers.add(userData);
                                    }

                                    adapter = new Adapter_Users(listUsers,getActivity().getApplicationContext(),UsersFragment.this.getFragmentManager());
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (UnsupportedEncodingException | JSONException e) {
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

            adapter.notifyDataSetChanged();
        }catch(Exception ex){
            Log.e("UsersFragment", ex.toString());
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<UserDataObject> filteredModelList = filter(listUsers,newText);
        adapter.setFilter(filteredModelList);

        return true;
    }

    private List<UserDataObject> filter(List<UserDataObject> models, String query) {
        query = query.toLowerCase();
        final List<UserDataObject> filteredModelList = new ArrayList<>();
        for (UserDataObject model : models) {
            final String firstName = model.getFirstname().toLowerCase();
            final String lastName = model.getLastname().toLowerCase();
            final String username = model.getUsername().toLowerCase();

            if (firstName.contains(query) || lastName.contains(query) || username.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
