package com.example.apn.vortex;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String email,password,proimgurl;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    UserSessionManager session;
    RequestQueue requestQueue;
    NavigationView navigationView;
    CircleImageView Pic;
    TextView userEmail;

    String loginurl = "http://10.10.28.104:3000/login/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);
        requestQueue = Volley.newRequestQueue(this);
        session = new UserSessionManager(getApplicationContext());



        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        navigationView.getMenu().clear();
        if(session.checkLogin() == true){
            navigationView.inflateMenu(R.menu.drawermenu2);

        }else {
            navigationView.inflateMenu(R.menu.drawermenu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            if(session.checkLogin() == true){
               setUserInfoToDrawer();
            }else{

                Pic = (CircleImageView) findViewById(R.id.drawerproimg);
                userEmail =(TextView) findViewById(R.id.drawerproname);

                userEmail.setText("User");

                Picasso.with(getApplicationContext()).load(R.drawable.user).into(Pic);
            }

           return true;


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
           // super.onBackPressed();
        }else {
           finish();
        }

    }


    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.All_events:
                Intent a = new Intent(MainActivity.this,AllEvents.class);
                startActivity(a);
                break;
            case R.id.sup_login:
                Intent s = new Intent(MainActivity.this,SupplierLogin.class);
                startActivity(s);
                break;
            case R.id.org_login:
                if(session.checkLogin() == true){
                    userLogin();
                    break;
                }else {
                    Intent o = new Intent(MainActivity.this, OrganizerLogin.class);
                    startActivity(o);
                    break;
                }
            case R.id.logout:
                session.logoutUser();
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.drawermenu);
                break;
            case R.id.about:
                Intent o = new Intent(MainActivity.this, ManageEvent.class);
                startActivity(o);
                break;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void userLogin(){

        HashMap<String, String> user = session.getUserDetails();


         email = user.get(UserSessionManager.KEY_EMAIL);
         password = user.get(UserSessionManager.KEY_PASSWORD);




        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email",email.toString());
        jsonParams.put("password",password.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,loginurl,new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String msg = response.getString("msg");

                            if( msg.equals("success") ){

                                Toast.makeText(getApplicationContext(),"Login successfully", Toast.LENGTH_SHORT).show();

                                String id = response.getString("id");
                                String  fname = response.getString("fname");
                                String imgurl = response.getString("imgurl");
                                String lname = response.getString("lname");
                                String fullName = fname + " " + lname;

                                Intent l = new Intent(MainActivity.this,Profile.class);
                                Bundle b = new Bundle();
                                b.putString("id", id.toString());
                                b.putString("fullName", fullName.toString());
                                b.putString("imgurl", imgurl.toString());
                                l.putExtras(b);
                                startActivity(l);
                            }else{
                                Toast.makeText(getApplicationContext(),"Incorrect email or password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Connection Fail", Toast.LENGTH_SHORT).show();

                    }
                }

        );


        requestQueue.add(jsonObjectRequest);
    }
    public void setUserInfoToDrawer(){

        Pic = (CircleImageView) findViewById(R.id.drawerproimg);
        userEmail =(TextView) findViewById(R.id.drawerproname);

        HashMap<String, String> user = session.getUserDetails();

        proimgurl = user.get(UserSessionManager.KEY_IMGURL);
        email = user.get(UserSessionManager.KEY_EMAIL);

        userEmail.setText(email);

        Picasso.with(getApplicationContext()).load(proimgurl).into(Pic);

    }


}
