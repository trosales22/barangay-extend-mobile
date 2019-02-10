package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Comments;
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Newsfeed;
import com.breadbuster.barangayextend.classes.Adapters.EmptyRecyclerViewAdapter;
import com.breadbuster.barangayextend.classes.Beans.CommentBean;
import com.breadbuster.barangayextend.classes.DataObjects.CommentsDataObject;
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

public class CommentsActivity extends AppCompatActivity {
    @InjectView(R.id.swipeToRefresh_comments) SwipeRefreshLayout swipeToRefresh_comments;
    @InjectView(R.id.recyclerView_comments) RecyclerView recyclerView_comments;

    View view;
    urls url = new urls();
    public static CommentBean comment = new CommentBean();

    private List<CommentsDataObject> commentsDataObjectList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter adapter_emptyRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.inject(this);

        FloatingActionButton fab_addComment = findViewById(R.id.fab_addComment);
        fab_addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddCommentActivity.class));
            }
        });

        commentsDataObjectList = new ArrayList<>();

        recyclerView_comments = findViewById(R.id.recyclerView_comments);
        recyclerView_comments.setHasFixedSize(true);
        recyclerView_comments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        swipeToRefresh_comments.setColorSchemeResources(R.color.colorPrimary);

        showAllComments();

        swipeToRefresh_comments.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(commentsDataObjectList != null){
                    commentsDataObjectList.clear();
                }

                showAllComments();

                swipeToRefresh_comments.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void showAllComments(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(CommentsActivity.this);
            progressDialog.setMessage("Loading comments...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            MyStringRequest request = new MyStringRequest(null, Request.Method.POST, url.getShowAllCommentUrl() + Adapter_Newsfeed.topicPostID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();

                            if(response.equalsIgnoreCase("no_data")){
                                adapter_emptyRecyclerView = new EmptyRecyclerViewAdapter();
                                recyclerView_comments.setAdapter(adapter_emptyRecyclerView);
                            }else{
                                try {
                                    JSONObject response_object = new JSONObject(response);

                                    JSONArray comments = response_object.getJSONArray("comments");

                                    for(int a = 0; a < comments.length(); a++) {
                                        JSONObject comments_object = comments.getJSONObject(a);

                                        JSONArray commenterInfo = comments_object.getJSONArray("commenterInfo");
                                        for (int b = 0; b < commenterInfo.length(); b++) {
                                            JSONObject commenterInfo_object = commenterInfo.getJSONObject(b);

                                            comment.setCommentID(comments_object.getString("CommentID"));
                                            comment.setPostID(comments_object.getString("PostID"));
                                            comment.setCommentByID(comments_object.getString("CommentBy"));
                                            comment.setDateAndTimeCommented(comments_object.getString("DateAndTimeCommented"));
                                            comment.setComment(comments_object.getString("Comment"));

                                            comment.setCommentBy(commenterInfo_object.getString("Firstname") + " " + commenterInfo_object.getString("Lastname"));
                                            comment.setCommentBy_profilePicture(commenterInfo_object.getString("ProfilePicture"));

                                            CommentsDataObject commentsDataObject = new CommentsDataObject(
                                                    comment.getCommentID(),
                                                    comment.getPostID(),
                                                    comment.getCommentBy(),
                                                    comment.getCommentByID(),
                                                    comment.getCommentBy_profilePicture(),
                                                    comment.getComment(),
                                                    comment.getDateAndTimeCommented()
                                            );

                                            commentsDataObjectList.add(commentsDataObject);
                                        }
                                    }

                                    adapter = new Adapter_Comments(commentsDataObjectList,getApplicationContext());
                                    recyclerView_comments.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    Log.e("CommentsActivity", e.toString());
                                }

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Log.e("CommentsActivity", error.toString());
                        }
                    }
            );

            MySingleton.getInstance(CommentsActivity.this).addToRequestQueue(request);
        }catch(Exception ex){
            Log.e("CommentsActivity", ex.toString());
        }
    }

}
