package com.breadbuster.barangayextend.classes.BottomSheetDialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.Activity.HomeActivity;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Users;
import com.breadbuster.barangayextend.classes.Handlers.ErrorHandler;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Users_BottomSheetDialogFragment extends BottomSheetDialogFragment {
    @InjectView(R.id.imgProfilePictureOfSelectedUser) CircleImageView imgProfilePictureOfSelectedUser;

    @InjectView(R.id.txtFullnameOfSelectedUser) TextView txtFullnameOfSelectedUser;
    @InjectView(R.id.txtEmailAddressOfSelectedUser) TextView txtEmailAddressOfSelectedUser;
    @InjectView(R.id.txtUsernameOfSelectedUser) TextView txtUsernameOfSelectedUser;
    @InjectView(R.id.txtPhoneNumberOfSelectedUser) TextView txtPhoneNumberOfSelectedUser;
    @InjectView(R.id.txtGenderOfSelectedUser) TextView txtGenderOfSelectedUser;
    @InjectView(R.id.txtUserTypeOfSelectedUser) TextView txtUserTypeOfSelectedUser;
    @InjectView(R.id.txtBarangayOfSelectedUser) TextView txtBarangayOfSelectedUser;

    @InjectView(R.id.btnDeleteSelectedUser) AppCompatButton btnDeleteSelectedUser;

    private Context context;
    private View view;

    urls url = new urls();

    public Users_BottomSheetDialogFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_bottom_sheet_modal, container, false);
        ButterKnife.inject(this,view);

        if(Adapter_Users.userBean.getProfilePicture().isEmpty()){
            imgProfilePictureOfSelectedUser.setImageResource(R.mipmap.ic_launcher_round);
        }else{
            Picasso.with(context)
                    .load(Adapter_Users.userBean.getProfilePicture())
                    .error(R.mipmap.ic_launcher_round)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(imgProfilePictureOfSelectedUser);
        }

        if(HomeActivity.userEmailAddress.equals(Adapter_Users.userBean.getEmailAddress())){
            txtFullnameOfSelectedUser.setText(Adapter_Users.userBean.getFullname() + " (YOU)");
            btnDeleteSelectedUser.setVisibility(View.GONE);
        }else{
            txtFullnameOfSelectedUser.setText(Adapter_Users.userBean.getFullname());
            btnDeleteSelectedUser.setVisibility(View.VISIBLE);
        }

        if(HomeActivity.userType.equals("Resident") || HomeActivity.userType.equals("Council") || HomeActivity.userType.equals("Employee")){
            btnDeleteSelectedUser.setVisibility(View.GONE);
        }

        txtEmailAddressOfSelectedUser.setText(Adapter_Users.userBean.getEmailAddress());
        txtUsernameOfSelectedUser.setText(Adapter_Users.userBean.getUsername());
        txtPhoneNumberOfSelectedUser.setText(Adapter_Users.userBean.getPhoneNumber());
        txtGenderOfSelectedUser.setText(Adapter_Users.userBean.getGender());
        txtUserTypeOfSelectedUser.setText(Adapter_Users.userBean.getUserType());
        txtBarangayOfSelectedUser.setText(Adapter_Users.userBean.getBarangay());

        btnDeleteSelectedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme);
                alertDialogBuilder.setTitle("Confirmation");

                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                deleteSelectedUser(Adapter_Users.userBean.getEmailAddress());
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

        return view;
    }

    public void deleteSelectedUser(String emailAddress){
        MyStringRequest request = new MyStringRequest(null, Request.Method.POST, url.getDeleteSelectedUser() + emailAddress,
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
