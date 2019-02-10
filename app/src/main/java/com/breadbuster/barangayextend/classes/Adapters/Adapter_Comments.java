package com.breadbuster.barangayextend.classes.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.Activity.CommentsActivity;
import com.breadbuster.barangayextend.Activity.HomeActivity;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.DataObjects.CommentsDataObject;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Comments extends RecyclerView.Adapter<Adapter_Comments.ViewHolder>{
    private List<CommentsDataObject> commentsList;
    private Context context;
    private View view;

    urls url = new urls();

    private static String commentID;

    public Adapter_Comments(List<CommentsDataObject> commentsList, Context context) {
        this.commentsList = commentsList;
        this.context = context;
    }

    @Override
    public Adapter_Comments.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comments, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Adapter_Comments.ViewHolder holder, int position) {
        final CommentsDataObject commentsDataObject = commentsList.get(position);

        holder.txtCommentBy.setText(commentsDataObject.getCommentBy());
        holder.txtDateAndTimeCommented.setText(commentsDataObject.getDateAndTimeCommented());

        if(commentsDataObject.getCommentBy_profilePicture().isEmpty()){
            holder.imgCommentBy.setImageResource(R.mipmap.ic_launcher_round);
        }else {
            Picasso.with(context)
                    .load(commentsDataObject.getCommentBy_profilePicture())
                    .error(R.mipmap.ic_launcher_round)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(holder.imgCommentBy);
        }

        holder.lblComment.setText(commentsDataObject.getComment());

        holder.txtComment.setText(commentsDataObject.getComment());
        holder.txtComment.setVisibility(View.GONE);

        holder.btnCancelUpdateComment.setVisibility(View.GONE);
        holder.btnUpdateComment.setVisibility(View.GONE);

        holder.btnCancelUpdateComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lblComment.setVisibility(View.VISIBLE);
                holder.txtComment.setVisibility(View.GONE);
                holder.btnCancelUpdateComment.setVisibility(View.GONE);
                holder.btnUpdateComment.setVisibility(View.GONE);
            }
        });

        holder.btnUpdateComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setTitle("Update This Comment?");

                alertDialogBuilder
                        .setMessage("Once you update your copy of the comment, it cannot be undone.")
                        .setPositiveButton("Update Comment",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Map<String, String> parameters = new HashMap<>();
                                parameters.put("comment",holder.txtComment.getText().toString());

                                deleteOrUpdateComment(parameters,url.getUpdateCommentUrl() + commentID);
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
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

        if(CommentsActivity.comment.getCommentByID().equals(HomeActivity.userID)){
            holder.cardView_comments.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    commentID = commentsDataObject.getCommentID();

                    final String[] option = new String[] { "Edit", "Delete" };
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(), android.R.layout.select_dialog_item, option);
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Comment");
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which){
                                case 0:
                                    holder.lblComment.setVisibility(View.GONE);
                                    holder.txtComment.setVisibility(View.VISIBLE);
                                    holder.btnCancelUpdateComment.setVisibility(View.VISIBLE);
                                    holder.btnUpdateComment.setVisibility(View.VISIBLE);

                                    break;
                                case 1:
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                                    alertDialogBuilder.setTitle("Delete This Comment?");

                                    alertDialogBuilder
                                            .setMessage("Once you delete your copy of the comment, it cannot be undone.")
                                            .setPositiveButton("Delete Comment",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    deleteOrUpdateComment(null,url.getDeleteCommentUrl() + commentID);
                                                }
                                            })
                                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    // if this button is clicked, just close
                                                    // the dialog box and do nothing
                                                    dialog.cancel();
                                                }
                                            });

                                    AlertDialog alertDialog = alertDialogBuilder.create();

                                    // Show Alert Message
                                    alertDialog.show();

                                    break;
                                default:
                                    break;
                            }
                        }});

                    final AlertDialog dialog = builder.create();

                    dialog.show();

                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.cardView_comments) CardView cardView_comments;
        @InjectView(R.id.imgCommentBy) CircleImageView imgCommentBy;
        @InjectView(R.id.txtCommentBy) TextView txtCommentBy;
        @InjectView(R.id.txtDateAndTimeCommented) TextView txtDateAndTimeCommented;
        @InjectView(R.id.lblComment) TextView lblComment;
        @InjectView(R.id.txtComment) MaterialEditText txtComment;

        @InjectView(R.id.btnCancelUpdateComment) AppCompatButton btnCancelUpdateComment;
        @InjectView(R.id.btnUpdateComment) AppCompatButton btnUpdateComment;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this,view);
        }
    }

    private void deleteOrUpdateComment(Map parameter, String url){
        MyStringRequest request = new MyStringRequest(parameter, Request.Method.POST, url,
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
                        Log.e("Adapter_Comments",error.toString());
                    }
                }
        );

        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}
