package com.breadbuster.barangayextend.CheckFeedbacksActivityTabs;


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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Feedbacks;
import com.breadbuster.barangayextend.classes.Adapters.EmptyRecyclerViewAdapter;
import com.breadbuster.barangayextend.classes.Beans.FeedbackBean;
import com.breadbuster.barangayextend.classes.DataObjects.Feedbacks_DataObject;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class BugReportFragment extends Fragment {
    @InjectView(R.id.recyclerView_bugReport) RecyclerView recyclerView_bugReport;
    @InjectView(R.id.swipeToRefresh_bugReport) SwipeRefreshLayout swipeToRefresh_bugReport;

    View view;
    urls url = new urls();
    FeedbackBean feedbackBean = new FeedbackBean();

    private List<Feedbacks_DataObject> feedbacksDataObjectList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter adapter_emptyRecyclerView;

    public BugReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bug_report, container, false);
        ButterKnife.inject(this, view);

        recyclerView_bugReport.setHasFixedSize(true);
        recyclerView_bugReport.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        feedbacksDataObjectList = new ArrayList<>();

        swipeToRefresh_bugReport.setColorSchemeResources(R.color.colorPrimary);

        showAllBugReport();

        swipeToRefresh_bugReport.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(feedbacksDataObjectList != null){
                    feedbacksDataObjectList.clear();
                }

                showAllBugReport();
                swipeToRefresh_bugReport.setRefreshing(false);
            }
        });

        return view;
    }

    private void showAllBugReport(){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        MyStringRequest request = new MyStringRequest(null, Request.Method.POST, url.getShowAllFeedbacksBasedOnFeedbackTypeUrl() + "Bug%20Report",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        if(response.equalsIgnoreCase("no_data")){
                            adapter_emptyRecyclerView = new EmptyRecyclerViewAdapter();
                            recyclerView_bugReport.setAdapter(adapter_emptyRecyclerView);
                        }else{
                            try {
                                JSONObject response_object = new JSONObject(response);
                                JSONArray feedbacks = response_object.getJSONArray("feedbacks");

                                for(int a = 0; a < feedbacks.length(); a++){
                                    JSONObject feedback_object = feedbacks.getJSONObject(a);

                                    JSONArray feedbackByInfo = feedback_object.getJSONArray("feedbackByInfo");
                                    for(int b = 0; b < feedbackByInfo.length(); b++){
                                        JSONObject feedbackByInfo_object = feedbackByInfo.getJSONObject(b);

                                        feedbackBean.setFeedBackBy(feedbackByInfo_object.getString("Firstname") + " " + feedbackByInfo_object.getString("Lastname"));
                                        feedbackBean.setFeedbackBy_dateAndTimeSubmitted(feedback_object.getString("DateAndTimeSubmitted"));
                                        feedbackBean.setFeedbackBy_profilePicture(feedbackByInfo_object.getString("ProfilePicture"));

                                        feedbackBean.setFeedback(feedback_object.getString("Feedback"));

                                        Feedbacks_DataObject feedbacksDataObject = new Feedbacks_DataObject(
                                                feedbackBean.getFeedBackBy(),
                                                feedbackBean.getFeedbackBy_profilePicture(),
                                                feedbackBean.getFeedback(),
                                                feedbackBean.getFeedbackBy_dateAndTimeSubmitted()
                                        );

                                        feedbacksDataObjectList.add(feedbacksDataObject);
                                    }
                                }

                                adapter = new Adapter_Feedbacks(feedbacksDataObjectList,getActivity().getApplicationContext());
                                recyclerView_bugReport.setAdapter(adapter);
                            } catch (JSONException e) {
                                Log.e("BugReportFragment", e.toString());
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Log.e("BugReportFragment", error.toString());
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
    }

}
