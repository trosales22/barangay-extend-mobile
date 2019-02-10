package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.Fragments.NewsfeedFragment;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Newsfeed;
import com.breadbuster.barangayextend.classes.Beans.CommentBean;
import com.breadbuster.barangayextend.classes.Handlers.ErrorHandler;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddCommentActivity extends AppCompatActivity {
    @InjectView(R.id.txtComment) MaterialEditText txtComment;
    @InjectView(R.id.btnCancelComment) AppCompatButton btnCancelComment;
    @InjectView(R.id.btnSendComment) AppCompatButton btnSendComment;

    CommentBean comment = new CommentBean();
    urls url = new urls();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        ButterKnife.inject(this);

        final MaterialEditText[] editTexts = new MaterialEditText[]{
               txtComment
        };

        btnCancelComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Validation.validateMaterialEditText(editTexts)){
                    final ProgressDialog progressDialog = new ProgressDialog(AddCommentActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Posting comment... Please wait!");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    sendComment();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
            }
        });
    }

    private void init(){
        comment.setComment(txtComment.getText().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
        Calendar dateAndTimeCommented_calendar = Calendar.getInstance();

        comment.setDateAndTimeCommented(sdf.format(dateAndTimeCommented_calendar.getTime()));
    }

    private void sendComment(){
        init();

        Map<String, String> parameters = new HashMap<>();

        parameters.put("postID", Adapter_Newsfeed.topicPostID);
        parameters.put("commentBy",HomeActivity.userID);
        parameters.put("comment",comment.getComment());
        parameters.put("dateAndTimeCommented",comment.getDateAndTimeCommented());

        MyStringRequest request = new MyStringRequest(parameters, Request.Method.POST, url.getInsertCommentToAPostUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddCommentActivity.this, response, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), CommentsActivity.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AddCommentActivity", error.toString());
                    }
                }
        );

        MySingleton.getInstance(AddCommentActivity.this).addToRequestQueue(request);
    }
}
