package com.breadbuster.barangayextend.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.breadbuster.barangayextend.Fragments.BarangayFragment;
import com.breadbuster.barangayextend.Fragments.NewsfeedFragment;
import com.breadbuster.barangayextend.Fragments.NoInternetConnectionFragment;
import com.breadbuster.barangayextend.Fragments.UsersFragment;
import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Requests.CacheRequest;
import com.breadbuster.barangayextend.classes.MyApplication;
import com.breadbuster.barangayextend.classes.Requests.MySingleton;
import com.breadbuster.barangayextend.classes.SharedPrefManager;
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.urls;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Menu menu;
    NavigationView navigationView;
    View header;

    TextView txtFullnameOfLoggedInUser;
    TextView txtUserTypeOfLoggedInUser;
    ImageView imgProfilePictureOfLoggedInUser;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_menu_public,
            R.drawable.ic_menu_council_or_employee
    };

    urls url = new urls();
    UserBean userBean = new UserBean();
    public static String userID,userEmailAddress,userBarangay,userType;
    public static boolean isConnected;

    MenuItem[] menuItems_residents,menuItems_councilOrEmployees,menuItems_developer,menuItems_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // At activity startup we manually check the internet status and change the text status
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                isConnected = true;
            } else {
                isConnected = false;
            }

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            header = navigationView.getHeaderView(0);

            txtUserTypeOfLoggedInUser = header.findViewById(R.id.txtUserTypeOfLoggedInUser);
            txtFullnameOfLoggedInUser = header.findViewById(R.id.txtFullnameOfLoggedInUser);
            imgProfilePictureOfLoggedInUser = header.findViewById(R.id.imgProfilePictureOfLoggedInUser);

            viewPager = findViewById(R.id.viewpager);
            setupViewPager(viewPager);
            viewPager.getAdapter().notifyDataSetChanged();

            tabLayout = findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#EEEEEE"));
            //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            tabLayout.setTabMode(TabLayout.MODE_FIXED);

            //setupTabIcons();
            showInfoOfLoggedInUser();

            menu = navigationView.getMenu();

            menuItems_residents = new MenuItem[]{
                    menu.findItem(R.id.nav_barangay),
                    menu.findItem(R.id.nav_requestForPosition),
                    menu.findItem(R.id.nav_addQuestionWithAnswer),
                    menu.findItem(R.id.nav_addSecurityQuestion),
                    menu.findItem(R.id.nav_checkRequests),
                    menu.findItem(R.id.nav_checkFeedbacks)
            };

            menuItems_councilOrEmployees = new MenuItem[]{
                    menu.findItem(R.id.nav_barangay),
                    menu.findItem(R.id.nav_requestForPosition),
                    menu.findItem(R.id.nav_addQuestionWithAnswer),
                    menu.findItem(R.id.nav_checkFeedbacks)
            };

            menuItems_developer = new MenuItem[]{
                    menu.findItem(R.id.nav_requestForPosition)
            };

            menuItems_all = new MenuItem[]{
                    menu.findItem(R.id.nav_myAccount),
                    menu.findItem(R.id.nav_postSomething),
                    menu.findItem(R.id.nav_faq),
                    menu.findItem(R.id.nav_addSecurityQuestion),
                    menu.findItem(R.id.nav_barangay),
                    menu.findItem(R.id.nav_requestForPosition),
                    menu.findItem(R.id.nav_addQuestionWithAnswer),
                    menu.findItem(R.id.nav_requestForPosition),
                    menu.findItem(R.id.nav_checkRequests),
                    menu.findItem(R.id.nav_checkFeedbacks),
                    menu.findItem(R.id.action_feedbackAndHelp)
            };

            if(!isConnected){
                hideMenuItem(menuItems_all);
            }
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_FAQs) {
            startActivity(new Intent(getApplicationContext(), FrequentlyAskedQuestionActivity.class));
        } else if(id == R.id.action_aboutBarangayExtend){
            startActivity(new Intent(getApplicationContext(), AppDescriptionActivity.class));
        } else if(id == R.id.action_aboutTheDeveloper){
            String url = "http://tristanrosales.x10.mx/tristanrosales/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }else if(id == R.id.action_feedbackAndHelp){
            startActivity(new Intent(getApplicationContext(), AddFeedbackActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myAccount) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        } else if(id == R.id.nav_faq){
            startActivity(new Intent(getApplicationContext(), FrequentlyAskedQuestionActivity.class));
        } else if (id == R.id.nav_logout) {
            showLogoutPrompt("Are you sure you want to logout?", "You cannot undo this action.");
        } else if (id == R.id.nav_requestForPosition) {
            startActivity(new Intent(getApplicationContext(), RequestForPositionActivity.class));
        } else if (id == R.id.nav_checkFeedbacks) {
            startActivity(new Intent(getApplicationContext(), CheckFeedbacksActivity.class));
        }else if (id == R.id.nav_checkRequests) {
            startActivity(new Intent(getApplicationContext(), CheckRequestsActivity.class));
        } else if (id == R.id.nav_postSomething) {
            startActivity(new Intent(getApplicationContext(), WriteSomethingActivity.class));
        } else if(id == R.id.nav_barangay){
            startActivity(new Intent(getApplicationContext(), AddBarangayActivity.class));
        } else if(id == R.id.nav_addQuestionWithAnswer){
            startActivity(new Intent(getApplicationContext(), AddQuestionWithAnswerActivity.class));
        } else if(id == R.id.nav_addSecurityQuestion){
            startActivity(new Intent(getApplicationContext(), AddSecurityQuestionActivity.class));
        } else if (id == R.id.nav_barangayExtend) {
            startActivity(new Intent(getApplicationContext(), AppDescriptionActivity.class));
        } else if (id == R.id.nav_developer) {
            String url = "http://tristanrosales.x10.mx/tristanrosales/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }else if(id == R.id.nav_feedbackAndHelp){
            startActivity(new Intent(getApplicationContext(), AddFeedbackActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if(isConnected){
            adapter.addFrag(new NewsfeedFragment(), "Newsfeed");
            adapter.addFrag(new UsersFragment(), "Users");
            adapter.addFrag(new BarangayFragment(), "Barangay");
        }else{
            adapter.addFrag(new NoInternetConnectionFragment(), "Newsfeed");
            adapter.addFrag(new NoInternetConnectionFragment(), "Users");
            adapter.addFrag(new NoInternetConnectionFragment(), "Barangay");
        }

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
            //return null;
        }
    }

    public void showLogoutPrompt(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        alertDialogBuilder.setTitle(title);

        alertDialogBuilder
                .setMessage(message)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

    public void showInfoOfLoggedInUser(){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            CacheRequest request = new CacheRequest(null, Request.Method.POST, url.getInfoOfLoggedInUserUrl() + SharedPrefManager.getInstance(HomeActivity.this).getEmailAddressOrUsername(),
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            progressDialog.dismiss();

                            try {
                                final String jsonString = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers));

                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray users = jsonObject.getJSONArray("userInfo");

                                for(int i = 0; i < users.length(); i++){
                                    JSONObject user = users.getJSONObject(i);

                                    userBean.setProfilePicture(user.getString("ProfilePicture"));
                                    userID = user.getString("UserID");
                                    userEmailAddress = user.getString("EmailAddress");
                                    userBarangay = user.getString("Barangay");
                                    userBean.setFirstname(user.getString("Firstname"));
                                    userBean.setLastname(user.getString("Lastname"));
                                    userBean.setUserType(user.getString("UserType"));
                                    userType = userBean.getUserType();

                                    if(userBean.getProfilePicture().isEmpty()){
                                        imgProfilePictureOfLoggedInUser.setImageResource(R.mipmap.ic_launcher_round);
                                    }else{
                                        Picasso.with(HomeActivity.this)
                                                .load(userBean.getProfilePicture())
                                                .error(R.mipmap.ic_launcher_round)
                                                .placeholder(R.mipmap.ic_launcher_round)
                                                .into(imgProfilePictureOfLoggedInUser);
                                    }

                                    txtFullnameOfLoggedInUser.setText(userBean.getFirstname() + " " + userBean.getLastname());
                                    txtUserTypeOfLoggedInUser.setText(userBean.getUserType());

                                    if(userBean.getUserType().equalsIgnoreCase("Resident")){
                                        hideMenuItem(menuItems_residents);
                                    }else if(userBean.getUserType().equalsIgnoreCase("Council") || userBean.getUserType().equalsIgnoreCase("Employee")){
                                        hideMenuItem(menuItems_councilOrEmployees);
                                    }else if(userBean.getUserType().equalsIgnoreCase("Developer")){
                                        hideMenuItem(menuItems_developer);
                                    }else{
                                        hideMenuItem(menuItems_all);
                                    }
                                }

                            } catch (JSONException e) {
                                Log.e("Error Message", e.toString());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            String message = null;
                            if (error instanceof NetworkError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (error instanceof ServerError) {
                                message = "The server could not be found. Please try again after some time!!";
                            } else if (error instanceof AuthFailureError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (error instanceof ParseError) {
                                message = "Parsing error! Please try again after some time!!";
                            } else if (error instanceof NoConnectionError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (error instanceof TimeoutError) {
                                message = "Connection TimeOut! Please check your internet connection.";
                            }

                            hideMenuItem(menuItems_all);
                            Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }
            );

            request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(HomeActivity.this).addToRequestQueue(request);
        }catch(Exception ex){
            Log.e("Error Message", ex.toString());
        }
    }

    private void hideMenuItem(MenuItem[] items){
        for(int i=0; i<items.length; i++){
            MenuItem item = items[i];
            item.setVisible(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();// On Resume notify the Application
    }
}
