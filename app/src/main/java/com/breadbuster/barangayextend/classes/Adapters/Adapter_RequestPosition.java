package com.breadbuster.barangayextend.classes.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.Activity.HomeActivity;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Handlers.ErrorHandler;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.DataObjects.RequestPositionDataObject;
import com.breadbuster.barangayextend.classes.urls;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_RequestPosition extends RecyclerView.Adapter<Adapter_RequestPosition.ViewHolder> {
    private List<RequestPositionDataObject> listOfRequestedPosition;
    private Context context;
    private View view;

    urls url = new urls();

    public Adapter_RequestPosition(List<RequestPositionDataObject> listOfRequestedPosition, Context context) {
        this.listOfRequestedPosition = listOfRequestedPosition;
        this.context = context;
    }

    @Override
    public Adapter_RequestPosition.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_of_request, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter_RequestPosition.ViewHolder holder, int position) {
        try{
            final RequestPositionDataObject requestPositionDataObject = listOfRequestedPosition.get(position);

            Picasso.with(context)
                    .load(requestPositionDataObject.getRequester_profilePicture())
                    .error(R.drawable.ic_broken_image_black)
                    .placeholder(R.drawable.ic_broken_image_black)
                    .into(holder.imgProfilePicture);

            holder.txtFullname.setText(requestPositionDataObject.getRequestedBy());
            holder.txtUserType.setText(requestPositionDataObject.getRequesterUserType());
            holder.txtRequestedPosition.setText("Requested Position:\n" + requestPositionDataObject.getRequestedPosition());
            holder.txtRequestDateAndTimeRegistered.setText("Request Date & Time Registered:\n" + requestPositionDataObject.getRequestDateAndTimeRegistered());

            if(requestPositionDataObject.getRequestDateAndTimeConfirmed().equals("")){
                holder.txtRequestDateAndTimeConfirmed.setText("Request Date & Time Confirmed:\nN/A");
            }else{
                holder.txtRequestDateAndTimeConfirmed.setText("Request Date & Time Confirmed:\n" +requestPositionDataObject.getRequestDateAndTimeConfirmed());
            }

            holder.txtRequestStatus.setText("Request Status:\n" + requestPositionDataObject.getRequestStatus());

            if(requestPositionDataObject.getRequestStatus().equalsIgnoreCase("Pending")){
                holder.btnDeclineRequestForPosition.setVisibility(View.VISIBLE);
                holder.btnApproveRequestForPosition.setVisibility(View.VISIBLE);
            }else if(requestPositionDataObject.getRequestStatus().equalsIgnoreCase("Declined")){
                holder.btnDeclineRequestForPosition.setVisibility(View.GONE);
                holder.btnApproveRequestForPosition.setVisibility(View.GONE);
            }else if(requestPositionDataObject.getRequestStatus().equalsIgnoreCase("Approved")){
                holder.btnDeclineRequestForPosition.setVisibility(View.GONE);
                holder.btnApproveRequestForPosition.setVisibility(View.GONE);
            }else{
                holder.btnDeclineRequestForPosition.setVisibility(View.GONE);
                holder.btnApproveRequestForPosition.setVisibility(View.GONE);
            }

            holder.btnDeleteRequestForPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme);
                    alertDialogBuilder.setTitle("Confirmation");

                    alertDialogBuilder
                            .setMessage("Are you sure you want to delete this request?")
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    deleteRequest(requestPositionDataObject.getRequesterUserID());
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

            holder.btnDeclineRequestForPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
                    Calendar dateAndTimeConfirmed_calendar = Calendar.getInstance();

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("requestStatus","Declined");
                    parameters.put("requestDateAndTimeConfirmed",sdf.format(dateAndTimeConfirmed_calendar.getTime()));
                    parameters.put("userType", requestPositionDataObject.getRequesterUserType());

                    updateStatusOfUser(parameters,requestPositionDataObject.getRequesterUserID());
                }
            });

            holder.btnApproveRequestForPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
                    Calendar dateAndTimeConfirmed_calendar = Calendar.getInstance();

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("requestStatus","Approved");
                    parameters.put("requestDateAndTimeConfirmed",sdf.format(dateAndTimeConfirmed_calendar.getTime()));
                    parameters.put("userType", requestPositionDataObject.getRequestedPosition());

                    updateStatusOfUser(parameters,requestPositionDataObject.getRequesterUserID());
                }
            });
        }catch(Exception ex){
            Snackbar snackbar = Snackbar
                    .make(view, ex.toString(), Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    @Override
    public int getItemCount() {
        return listOfRequestedPosition.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.linearLayout_request) LinearLayout linearLayout_request;
        @InjectView(R.id.imgProfilePicture) CircleImageView imgProfilePicture;

        @InjectView(R.id.txtFullname) TextView txtFullname;
        @InjectView(R.id.txtUserType) TextView txtUserType;
        @InjectView(R.id.txtRequestedPosition) TextView txtRequestedPosition;
        @InjectView(R.id.txtRequestDateAndTimeRegistered) TextView txtRequestDateAndTimeRegistered;
        @InjectView(R.id.txtRequestDateAndTimeConfirmed) TextView txtRequestDateAndTimeConfirmed;
        @InjectView(R.id.txtRequestStatus) TextView txtRequestStatus;

        @InjectView(R.id.btnDeleteRequestForPosition) AppCompatButton btnDeleteRequestForPosition;
        @InjectView(R.id.btnDeclineRequestForPosition) AppCompatButton btnDeclineRequestForPosition;
        @InjectView(R.id.btnApproveRequestForPosition) AppCompatButton btnApproveRequestForPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    private void updateStatusOfUser(Map<String, String> parameters,String requestedBy){
        MyStringRequest request = new MyStringRequest(parameters, Request.Method.POST, url.getUpdateStatusOfUser() + requestedBy,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(view.getContext(), response, Toast.LENGTH_LONG).show();
                        view.getContext().startActivity(new Intent(view.getContext(), HomeActivity.class));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.getInstance(view.getContext()).viewErrorMessage(view.getContext(),"Error Message",error.toString());
            }
        }
        );

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void deleteRequest(String requestedBy){
        MyStringRequest request = new MyStringRequest(null, Request.Method.POST, url.getDeleteRequestForPosition() + requestedBy,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(view.getContext(), response, Toast.LENGTH_LONG).show();
                        view.getContext().startActivity(new Intent(view.getContext(), HomeActivity.class));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.getInstance(view.getContext()).viewErrorMessage(view.getContext(),"Error Message",error.toString());
            }
        }
        );

        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}
