package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Handlers.ErrorHandler;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.Requests.MyStringRequest;
import com.breadbuster.barangayextend.classes.Beans.TopicBean;
import com.breadbuster.barangayextend.classes.Handlers.Validation;
import com.breadbuster.barangayextend.classes.urls;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class WriteSomethingActivity extends AppCompatActivity {
    @InjectView(R.id.imgTopicInCardView) CircleImageView imgTopicInCardView;

    @InjectView(R.id.txtTopicLocationName) TextView txtTopicLocationName;
    @InjectView(R.id.txtTopicLocationID) TextView txtTopicLocationID;
    @InjectView(R.id.txtTopicLocationAddress) TextView txtTopicLocationAddress;
    @InjectView(R.id.txtTopicLocationLatAndLong) TextView txtTopicLocationLatAndLong;

    @InjectView(R.id.txtTitleOfTopic) MaterialEditText txtTitleOfTopic;
    @InjectView(R.id.txtTopicDescription) MaterialEditText txtTopicDescription;

    @InjectView(R.id.imgTopic) ImageView imgTopic;

    @InjectView(R.id.cmbPostType) AppCompatSpinner cmbPostType;

    @InjectView(R.id.btnUploadPhoto) AppCompatButton btnUploadPhoto;
    @InjectView(R.id.btnTopicLocation) AppCompatButton btnLocationOfPlace;
    @InjectView(R.id.btnPost) AppCompatButton btnPost;

    private int PICK_IMAGE_REQUEST = 1;
    private int PLACE_PICKER_REQUEST = 1;

    TopicBean topic = new TopicBean();
    urls url = new urls();
    Bitmap bitmap;
    String placeID,placeName,placeAddress,placeLatLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_something);
        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final MaterialEditText[] editTexts = new MaterialEditText[]{
                txtTitleOfTopic,txtTopicDescription
        };

        initializePostType();

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
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

        btnLocationOfPlace.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;

                try{
                    intent = builder.build(WriteSomethingActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(imgTopic.getDrawable() == null){
                    Toast.makeText(WriteSomethingActivity.this, "Please choose topic image or poster.", Toast.LENGTH_LONG).show();
                }else if(topic.getTopicLocationID() == null) {
                    Toast.makeText(WriteSomethingActivity.this, "Please choose topic location.", Toast.LENGTH_LONG).show();
                }else{
                    if(!Validation.validateMaterialEditText(editTexts)){
                        validateDropdown(cmbPostType, "CHOOSE POST TYPE", "Please choose post type!");
                    }
                }

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

    private void initializePostType(){
        try{
            String[] postType = new String[]{
                    "CHOOSE POST TYPE",
                    "Local Attraction","Places of Interest","Special Events and News","Problem","Others"
            };

            final List<String> postTypeList = new ArrayList<>(Arrays.asList(postType));

            // Initializing an ArrayAdapter
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this,R.layout.spinner_item,postTypeList){
                @Override
                public boolean isEnabled(int position){
                    if(position == 0)
                    {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position == 0){
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            cmbPostType.setAdapter(spinnerArrayAdapter);

            cmbPostType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItemText = (String) parent.getItemAtPosition(position);
                    // If user change the default selection
                    // First item is disable and it is used for hint
                    if(position > 0){
                        // Notify the selected item text
                        Toast.makeText
                                (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                Uri uri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                    // rotating bitmap
                    String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                    Cursor cur = getContentResolver().query(uri, orientationColumn, null, null, null);
                    int orientation = -1;
                    if (cur != null && cur.moveToFirst()) {
                        orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                    }
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    topic.setTopicImage(encodedImage);

                    imgTopic.setImageBitmap(bitmap);
                    imgTopicInCardView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK){
                try {
                    Place place = PlacePicker.getPlace(data, WriteSomethingActivity.this);

                    placeID = place.getId();
                    placeName = (String) place.getName();
                    placeAddress = (String) place.getAddress();
                    placeLatLang = String.valueOf(place.getLatLng());

                    txtTopicLocationName.setText(placeName);
                    txtTopicLocationID.setText("Place ID: \n" + placeID + "\n");
                    txtTopicLocationAddress.setText("Address: \n" + placeAddress + "\n");
                    txtTopicLocationLatAndLong.setText(placeLatLang);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    private void init(){
        topic.setTopicTitle(txtTitleOfTopic.getText().toString());
        topic.setTopicDescription(txtTopicDescription.getText().toString());

        topic.setTopicLocationID(placeID);
        topic.setTopicLocationName(placeName);
        topic.setTopicLocationAddress(placeAddress);
        topic.setTopicType(cmbPostType.getSelectedItem().toString());
        topic.setTopicPostedBy(HomeActivity.userID);
        topic.setTopicPosterUserBarangay(HomeActivity.userBarangay);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
        Calendar dateAndTimePosted_calendar = Calendar.getInstance();

        topic.setTopicDateAndTimePosted(sdf.format(dateAndTimePosted_calendar.getTime()));
    }

    private void validateDropdown(AppCompatSpinner spinner, String selectedText, String errorMessage){
        if(spinner.getSelectedItem().toString() == selectedText){
            ErrorHandler.getInstance(WriteSomethingActivity.this).viewToastErrorMessage(WriteSomethingActivity.this,errorMessage);
            spinner.requestFocus();
        }else{
            final ProgressDialog progressDialog = new ProgressDialog(WriteSomethingActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Posting your topic... Please wait!");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            post();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
    }

    public void post(){
        init();

        Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "image/jpeg; charset=utf-8");
        params.put("topicTitle", topic.getTopicTitle());
        params.put("topicDescription",topic.getTopicDescription());

        params.put("topicImage", topic.getTopicImage());

        params.put("topicLocationID",topic.getTopicLocationID());
        params.put("topicLocationName",topic.getTopicLocationName());
        params.put("topicLocationAddress",topic.getTopicLocationAddress());

        params.put("topicType",topic.getTopicType());
        params.put("topicPostedBy",topic.getTopicPostedBy());
        params.put("topicPosterBarangay",topic.getTopicPosterUserBarangay());
        params.put("topicDateAndTimePosted",topic.getTopicDateAndTimePosted());

        MyStringRequest request = new MyStringRequest(params,Request.Method.POST,url.getPostSomethingUrl(),
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        ErrorHandler.getInstance(getApplicationContext()).viewToastErrorMessage(getApplicationContext(),response);
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        MySingleton.getInstance(WriteSomethingActivity.this).addToRequestQueue(request);
    }
}
