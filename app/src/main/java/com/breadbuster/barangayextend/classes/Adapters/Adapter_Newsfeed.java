package com.breadbuster.barangayextend.classes.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.Activity.CommentsActivity;
import com.breadbuster.barangayextend.Activity.HomeActivity;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.DataObjects.NewsfeedDataObject;
import com.breadbuster.barangayextend.classes.Handlers.ErrorHandler;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Adapter_Newsfeed extends RecyclerView.Adapter<Adapter_Newsfeed.ViewHolder> {
    private List<NewsfeedDataObject> newsfeedList;
    private Context context;
    private View view;

    public static String topicPostID;

    boolean visible;

    urls url = new urls();

    public Adapter_Newsfeed(List<NewsfeedDataObject> newsfeedList, Context context) {
        this.newsfeedList = newsfeedList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsfeed, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NewsfeedDataObject newsfeedDataObject = newsfeedList.get(position);

        holder.txtPostedBy.setText(newsfeedDataObject.getTopic_postedBy());
        holder.txtTopic_postType.setText(newsfeedDataObject.getTopic_postType());
        holder.txtTopic_dateAndTimePosted.setText(newsfeedDataObject.getTopic_dateAndTimePosted());
        holder.txtTopic_locationAddress.setText(newsfeedDataObject.getTopic_locationAddress());
        holder.txtTopicTitle_newsfeed.setText(newsfeedDataObject.getTopicTitle());
        holder.txtTopicDescription_newsfeed.setText(newsfeedDataObject.getTopicDescription());

        holder.txtTopicPosterUserBarangay_newsfeed.setText(newsfeedDataObject.getTopicPosterUserBarangay());
        holder.txtTopicPosterUserType_newsfeed.setText(newsfeedDataObject.getTopicPosterUserType());

        if(newsfeedDataObject.getTopicImage().isEmpty()){
            holder.imgTopic_newsfeed.setImageResource(R.drawable.no_image_available);
        }else {
            Picasso.with(context)
                    .load(newsfeedDataObject.getTopicImage())
                    .error(R.drawable.no_image_available)
                    .placeholder(R.drawable.no_image_available)
                    .into(holder.imgTopic_newsfeed);
        }

        if(newsfeedDataObject.getTopicPostedBy_profilePicture().isEmpty()){
            holder.imgPostedBy.setImageResource(R.mipmap.ic_launcher_round);
        }else {
            Picasso.with(context)
                    .load(newsfeedDataObject.getTopicPostedBy_profilePicture())
                    .error(R.drawable.ic_broken_image_black)
                    .placeholder(R.drawable.ic_portrait)
                    .into(holder.imgPostedBy);
        }

        holder.imgTopic_newsfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(holder.linearLayout_postTypeAndLocation);
                TransitionManager.beginDelayedTransition(holder.linearLayout_topicTitle);

                visible = !visible;
                holder.linearLayout_postTypeAndLocation.setVisibility(visible ? View.VISIBLE : View.GONE);
                holder.linearLayout_topicTitle.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        if(HomeActivity.userID.equals(newsfeedDataObject.getTopicPosterUserID())){
            holder.btnDeletePost.setVisibility(View.VISIBLE);
        }else{
            holder.btnDeletePost.setVisibility(View.GONE);
        }

        holder.btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme);
                alertDialogBuilder.setTitle("Confirmation");

                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                deletePost(newsfeedDataObject.getTopicPostID());
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();

                // Show Alert Message
                alertDialog.show();
            }
        });

        holder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topicPostID = newsfeedDataObject.getTopicPostID();

                view.getContext().startActivity(new Intent(view.getContext(), CommentsActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsfeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //declarations
        @InjectView(R.id.linearLayout_postTypeAndLocation) LinearLayout linearLayout_postTypeAndLocation;
        @InjectView(R.id.linearLayout_topicTitle) LinearLayout linearLayout_topicTitle;

        @InjectView(R.id.txtPostedBy) TextView txtPostedBy;
        @InjectView(R.id.txtTopic_postType) TextView txtTopic_postType;
        @InjectView(R.id.txtTopic_dateAndTimePosted) TextView txtTopic_dateAndTimePosted;
        @InjectView(R.id.txtTopic_locationAddress) TextView txtTopic_locationAddress;
        @InjectView(R.id.txtTopicTitle_newsfeed) TextView txtTopicTitle_newsfeed;
        @InjectView(R.id.txtTopicDescription_newsfeed) TextView txtTopicDescription_newsfeed;
        @InjectView(R.id.txtTopicPosterUserBarangay_newsfeed) TextView txtTopicPosterUserBarangay_newsfeed;
        @InjectView(R.id.txtTopicPosterUserType_newsfeed) TextView txtTopicPosterUserType_newsfeed;

        @InjectView(R.id.imgPostedBy) ImageView imgPostedBy;
        @InjectView(R.id.imgTopic_newsfeed) ImageView imgTopic_newsfeed;

        @InjectView(R.id.btnDeletePost) AppCompatButton btnDeletePost;
        @InjectView(R.id.btnComments) AppCompatButton btnComments;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    private void deletePost(String topicPosterID){
        MyStringRequest request = new MyStringRequest(null, Request.Method.POST, url.getDeletePost() + topicPosterID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(view.getContext(), response, Toast.LENGTH_LONG).show();
                        view.getContext().startActivity(new Intent(view.getContext(), HomeActivity.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ErrorHandler.getInstance(view.getContext()).viewErrorMessage(view.getContext(),"Error Message",error.toString());
                    }
                }
        );

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void setFilter(List<NewsfeedDataObject> newsfeedModels) {
        newsfeedList = new ArrayList<>();
        newsfeedList.addAll(newsfeedModels);
        notifyDataSetChanged();
    }
}
