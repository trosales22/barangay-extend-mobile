package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Adapters.Adapter_FAQs;
import com.breadbuster.barangayextend.classes.Adapters.EmptyRecyclerViewAdapter;
import com.breadbuster.barangayextend.classes.DataObjects.FAQs_DataObject;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FrequentlyAskedQuestionActivity extends AppCompatActivity {
    @InjectView(R.id.recyclerView_faqs) RecyclerView recyclerView_faqs;
    @InjectView(R.id.swipeToRefresh) SwipeRefreshLayout mSwipeRefreshLayout;

    urls url = new urls();

    private List<FAQs_DataObject> FAQs_list;
    private RecyclerView.Adapter adapter;

    private String question,answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequently_asked_question);
        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView_faqs.setHasFixedSize(true);
        recyclerView_faqs.setLayoutManager(new LinearLayoutManager(FrequentlyAskedQuestionActivity.this));

        FAQs_list = new ArrayList<>();

        showAllFAQs();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(FAQs_list != null){
                    FAQs_list.clear();
                }

                showAllFAQs();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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

    private void showAllFAQs(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(FrequentlyAskedQuestionActivity.this);
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            MyStringRequest request = new MyStringRequest(null, Request.Method.POST, url.getShowAllFAQsUrl(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();

                            if(response.equalsIgnoreCase("no_data")){
                                adapter = new EmptyRecyclerViewAdapter();
                                recyclerView_faqs.setAdapter(adapter);
                            }else{
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray FAQs_array = jsonObject.getJSONArray("FAQs");
                                    for (int i = 0; i < FAQs_array.length(); i++) {
                                        JSONObject FAQs_object = FAQs_array.getJSONObject(i);

                                        question = FAQs_object.getString("Question");
                                        answer = FAQs_object.getString("Answer");

                                        FAQs_DataObject FAQsData = new FAQs_DataObject(
                                                question, answer
                                        );

                                        FAQs_list.add(FAQsData);
                                    }

                                    adapter = new Adapter_FAQs(FAQs_list, FrequentlyAskedQuestionActivity.this);
                                    recyclerView_faqs.setAdapter(adapter);
                                } catch (JSONException e) {
                                    Log.e("Error Message", e.toString());
                                }
                            }
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

            MySingleton.getInstance(FrequentlyAskedQuestionActivity.this).addToRequestQueue(request);
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }
}
