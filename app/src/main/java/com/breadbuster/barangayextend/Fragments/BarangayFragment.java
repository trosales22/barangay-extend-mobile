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
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Barangay;
import com.breadbuster.barangayextend.classes.Adapters.EmptyRecyclerViewAdapter;
import com.breadbuster.barangayextend.classes.Beans.BarangayBean;
import com.breadbuster.barangayextend.classes.DataObjects.BarangayDataObject;
import com.breadbuster.barangayextend.classes.Requests.CacheRequest;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
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
public class BarangayFragment extends Fragment implements SearchView.OnQueryTextListener{
    @InjectView(R.id.recyclerView_barangay) RecyclerView recyclerView_barangay;
    @InjectView(R.id.swipeToRefresh_barangay) SwipeRefreshLayout mSwipeRefreshLayout;

    View view;
    urls url = new urls();
    BarangayBean barangayBean = new BarangayBean();

    private List<BarangayDataObject> listOfBarangay;
    //private RecyclerView.Adapter adapter;
    private Adapter_Barangay adapter;
    private RecyclerView.Adapter adapter_emptyRecyclerView;

    public BarangayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_barangay, container, false);
            ButterKnife.inject(this, view);

            recyclerView_barangay = view.findViewById(R.id.recyclerView_barangay);
            recyclerView_barangay.setHasFixedSize(true);
            recyclerView_barangay.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        }catch (Exception ex){
            Log.e("Error Message", ex.toString());
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        listOfBarangay = new ArrayList<>();
        showAllBarangay();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(listOfBarangay != null){
                    listOfBarangay.clear();
                }

                showAllBarangay();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter = new Adapter_Barangay(listOfBarangay, getActivity().getApplicationContext(),BarangayFragment.this.getFragmentManager());
        recyclerView_barangay.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.home,menu);
        final MenuItem menuItem = menu.findItem(R.id.btnSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("Search by Barangay name...");
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(menuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(listOfBarangay);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    private void showAllBarangay(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            CacheRequest request = new CacheRequest(null, Request.Method.POST, url.getShowAllBarangayUrl(),
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            progressDialog.dismiss();

                            try {
                                final String jsonString = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers));

                                if(jsonString.equalsIgnoreCase("no_data")){
                                    adapter_emptyRecyclerView = new EmptyRecyclerViewAdapter();
                                    recyclerView_barangay.setAdapter(adapter_emptyRecyclerView);
                                }else {
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    JSONArray barangay_array = jsonObject.getJSONArray("barangay");
                                    for (int i = 0; i < barangay_array.length(); i++) {
                                        JSONObject barangay_object = barangay_array.getJSONObject(i);

                                        barangayBean.setBarangayID(barangay_object.getString("BarangayID"));
                                        barangayBean.setBarangayName(barangay_object.getString("BarangayName"));
                                        barangayBean.setBarangayDescription(barangay_object.getString("BarangayDescription"));
                                        barangayBean.setBarangayLogo(barangay_object.getString("BarangayLogo"));

                                        BarangayDataObject barangayDataObject = new BarangayDataObject(
                                                barangayBean.getBarangayID(),
                                                barangayBean.getBarangayName(),
                                                barangayBean.getBarangayDescription(),
                                                barangayBean.getBarangayLogo()
                                        );

                                        listOfBarangay.add(barangayDataObject);
                                    }

                                    adapter = new Adapter_Barangay(listOfBarangay, getActivity().getApplicationContext(),BarangayFragment.this.getFragmentManager());
                                    recyclerView_barangay.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (UnsupportedEncodingException | JSONException e) {
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
            Log.e("BarangayFragment", ex.toString());
        }
    }

    private List<BarangayDataObject> filter(List<BarangayDataObject> models, String query) {
        query = query.toLowerCase();
        final List<BarangayDataObject> filteredModelList = new ArrayList<>();
        for (BarangayDataObject model : models) {
            final String barangayName = model.getBarangayName().toLowerCase();

            if (barangayName.contains(query)) {
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
        final List<BarangayDataObject> filteredModelList = filter(listOfBarangay,newText);
        adapter.setFilter(filteredModelList);

        return true;
    }
}
