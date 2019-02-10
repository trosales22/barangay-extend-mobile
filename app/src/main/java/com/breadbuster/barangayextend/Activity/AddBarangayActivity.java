package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.urls;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddBarangayActivity extends AppCompatActivity {
    @InjectView(R.id.imgLogoOfBarangay) CircleImageView imgLogoOfBarangay;
    @InjectView(R.id.txtBarangayName) MaterialEditText txtBarangayName;
    @InjectView(R.id.txtBarangayDescription) MaterialEditText txtBarangayDescription;
    @InjectView(R.id.btnAddBarangay) AppCompatButton btnAddBarangay;
    @InjectView(R.id.btnCloseAddBarangay) AppCompatButton btnCloseAddBarangay;
    @InjectView(R.id.btnUploadBarangayLogo) AppCompatButton btnUploadBarangayLogo;

    private int PICK_IMAGE_REQUEST = 1;

    urls url = new urls();

    String barangayName,barangayDescription,dateAndTimeAdded,barangayLogo;
    Bitmap barangayLogo_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_barangay);
        ButterKnife.inject(this);

        final MaterialEditText[] editTexts = new MaterialEditText[]{
                txtBarangayName
        };

        btnCloseAddBarangay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUploadBarangayLogo.setOnClickListener(new View.OnClickListener() {
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

        btnAddBarangay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Validation.validateMaterialEditText(editTexts)){
                    if(imgLogoOfBarangay.getDrawable() == null){
                        Toast.makeText(AddBarangayActivity.this, "Please choose a logo for this barangay!", Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        final ProgressDialog progressDialog = new ProgressDialog(AddBarangayActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Adding barangay... Please wait!");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        addBarangay();
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                barangayLogo_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                // rotating bitmap
                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cur = getContentResolver().query(uri, orientationColumn, null, null, null);
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
                barangayLogo = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                imgLogoOfBarangay.setImageBitmap(barangayLogo_bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void init(){
        barangayName = txtBarangayName.getText().toString();
        barangayDescription = txtBarangayDescription.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
        Calendar dateAndTimePosted_calendar = Calendar.getInstance();

        dateAndTimeAdded = sdf.format(dateAndTimePosted_calendar.getTime());
    }

    private void addBarangay(){
        init();

        Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "image/jpeg; charset=utf-8");
        params.put("barangayName", barangayName);
        params.put("barangayDescription", barangayDescription);
        params.put("barangayLogo", barangayLogo);
        params.put("dateAndTimeAdded", dateAndTimeAdded);

        MyStringRequest request = new MyStringRequest(params, Request.Method.POST,url.getAddBarangayUrl(),
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddBarangayActivity.this, response, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddBarangayActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        }
        );

        MySingleton.getInstance(AddBarangayActivity.this).addToRequestQueue(request);
    }
}
