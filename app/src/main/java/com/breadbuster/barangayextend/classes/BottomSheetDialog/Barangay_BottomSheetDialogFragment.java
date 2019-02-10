package com.breadbuster.barangayextend.classes.BottomSheetDialog;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
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
import com.breadbuster.barangayextend.classes.Adapters.Adapter_Barangay;
import com.breadbuster.barangayextend.classes.Beans.BarangayBean;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Barangay_BottomSheetDialogFragment extends BottomSheetDialogFragment {
    @InjectView(R.id.imgLogoOfSelectedBarangay) CircleImageView imgLogoOfSelectedBarangay;
    @InjectView(R.id.txtNameOfSelectedBarangay) TextView txtNameOfSelectedBarangay;
    @InjectView(R.id.txtDescriptionOfSelectedBarangay) TextView txtDescriptionOfSelectedBarangay;

    @InjectView(R.id.btnDeleteSelectedBarangay) AppCompatButton btnDeleteSelectedBarangay;
    @InjectView(R.id.btnEditSelectedBarangay) AppCompatButton btnEditSelectedBarangay;

    @InjectView(R.id.btnUploadBarangayLogo_update) AppCompatButton btnUploadBarangayLogo_update;
    @InjectView(R.id.imgLogoOfBarangay_update) CircleImageView imgLogoOfBarangay_update;
    @InjectView(R.id.txtBarangayName_update) MaterialEditText txtBarangayName_update;
    @InjectView(R.id.txtBarangayDescription_update) MaterialEditText txtBarangayDescription_update;
    @InjectView(R.id.btnUpdateBarangay) AppCompatButton btnUpdateBarangay;
    @InjectView(R.id.btnBack) AppCompatButton btnBack;

    private int PICK_IMAGE_REQUEST = 30;

    private Context context;
    private View view;

    urls url = new urls();
    BarangayBean barangayBean = new BarangayBean();

    Bitmap barangayLogo_bitmap;

    public Barangay_BottomSheetDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.barangay_bottom_sheet_modal, container, false);
        ButterKnife.inject(this,view);

        final MaterialEditText[] editTexts = new MaterialEditText[]{
                txtBarangayName_update,txtBarangayDescription_update
        };

        btnUploadBarangayLogo_update.setVisibility(View.GONE);
        imgLogoOfBarangay_update.setVisibility(View.GONE);
        txtBarangayName_update.setVisibility(View.GONE);
        txtBarangayDescription_update.setVisibility(View.GONE);
        btnUpdateBarangay.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);

        if(HomeActivity.userType.equalsIgnoreCase("Developer")){
            btnDeleteSelectedBarangay.setVisibility(View.VISIBLE);
            btnEditSelectedBarangay.setVisibility(View.VISIBLE);
        }else{
            btnDeleteSelectedBarangay.setVisibility(View.GONE);
            btnEditSelectedBarangay.setVisibility(View.GONE);
        }

        Picasso.with(context)
                .load(Adapter_Barangay.barangayBean.getBarangayLogo())
                .error(R.drawable.ic_broken_image_black)
                .placeholder(R.drawable.ic_broken_image_black)
                .into(imgLogoOfSelectedBarangay);

        txtNameOfSelectedBarangay.setText(Adapter_Barangay.barangayBean.getBarangayName());
        txtDescriptionOfSelectedBarangay.setText(Adapter_Barangay.barangayBean.getBarangayDescription());

        //updating barangay
        Picasso.with(context)
                .load(Adapter_Barangay.barangayBean.getBarangayLogo())
                .error(R.drawable.ic_broken_image_black)
                .placeholder(R.drawable.ic_broken_image_black)
                .into(imgLogoOfBarangay_update);

        barangayBean.setBarangayLogo(Adapter_Barangay.barangayBean.getBarangayLogo());

        txtBarangayName_update.setText(Adapter_Barangay.barangayBean.getBarangayName());
        txtBarangayDescription_update.setText(Adapter_Barangay.barangayBean.getBarangayDescription());

        btnDeleteSelectedBarangay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme);
                alertDialogBuilder.setTitle("Confirmation");

                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this barangay?")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                deleteOrUpdateSelectedBarangay(null,url.getDeleteSelectedBarangay() + Adapter_Barangay.barangayBean.getBarangayID());
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

        btnEditSelectedBarangay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgLogoOfSelectedBarangay.setVisibility(View.GONE);
                txtNameOfSelectedBarangay.setVisibility(View.GONE);
                txtDescriptionOfSelectedBarangay.setVisibility(View.GONE);

                btnDeleteSelectedBarangay.setVisibility(View.GONE);
                btnEditSelectedBarangay.setVisibility(View.GONE);

                btnUploadBarangayLogo_update.setVisibility(View.VISIBLE);
                imgLogoOfBarangay_update.setVisibility(View.VISIBLE);
                txtBarangayName_update.setVisibility(View.VISIBLE);
                txtBarangayDescription_update.setVisibility(View.VISIBLE);
                btnUpdateBarangay.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
            }
        });

        btnUploadBarangayLogo_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // 2. pick image only
                intent.setType("image/*");
                // 3. start activity
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btnUpdateBarangay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Validation.validateMaterialEditText(editTexts)){
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Saving changes... Please wait!");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    Map<String, String> params = new HashMap<>();

                                    params.put("barangayName",txtBarangayName_update.getText().toString());
                                    params.put("barangayDescription",txtBarangayDescription_update.getText().toString());
                                    params.put("barangayLogo",barangayBean.getBarangayLogo());

                                    deleteOrUpdateSelectedBarangay(params,url.getUpdateSelectedBarangay() + Adapter_Barangay.barangayBean.getBarangayID());
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgLogoOfSelectedBarangay.setVisibility(View.VISIBLE);
                txtNameOfSelectedBarangay.setVisibility(View.VISIBLE);
                txtDescriptionOfSelectedBarangay.setVisibility(View.VISIBLE);
                btnDeleteSelectedBarangay.setVisibility(View.VISIBLE);
                btnEditSelectedBarangay.setVisibility(View.VISIBLE);

                btnUploadBarangayLogo_update.setVisibility(View.GONE);
                imgLogoOfBarangay_update.setVisibility(View.GONE);
                txtBarangayName_update.setVisibility(View.GONE);
                txtBarangayDescription_update.setVisibility(View.GONE);
                btnUpdateBarangay.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                barangayLogo_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                // rotating bitmap
                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cur = getActivity().getContentResolver().query(uri, orientationColumn, null, null, null);
                int orientation = -1;
                if (cur != null && cur.moveToFirst()) {
                    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                barangayLogo_bitmap = Bitmap.createBitmap(barangayLogo_bitmap, 0, 0,barangayLogo_bitmap.getWidth(), barangayLogo_bitmap.getHeight(), matrix, true);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                barangayLogo_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedByte = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                barangayBean.setBarangayLogo(encodedByte);

                imgLogoOfBarangay_update.setImageBitmap(barangayLogo_bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteOrUpdateSelectedBarangay(Map params,String url){
        MyStringRequest request = new MyStringRequest(params,Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getActivity().getApplicationContext(), HomeActivity.class));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
        );

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
    }
}
