package com.inquilinosrd.inquilinosmobile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.inquilinosrd.inquilinosmobile.Models.Residence;
import com.inquilinosrd.inquilinosmobile.Models.ResidenceTypes;
import com.inquilinosrd.inquilinosmobile.Services.AppCore;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import layout.ListFragment;
import layout.MapFragment;
import layout.SearchFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inquilinosrd.inquilinosmobile.Services.AppCore.createGoogleApiClient;
import static com.inquilinosrd.inquilinosmobile.Services.AppCore.createInquilinosApiClient;
import static com.inquilinosrd.inquilinosmobile.Services.AppCore.isLogin;
import static com.inquilinosrd.inquilinosmobile.Services.AppCore.mInquilinosApiClient;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.DEFAULT_LATITUDE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.DEFAULT_LONGITUDE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.LIST_VIEW_MODE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.MAP_VIEW_MODE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.POPUP_VIEW;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.SEARCH_VIEW;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ListFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener, GoogleMap.OnMarkerClickListener, SearchFragment.OnFragmentInteractionListener,
        ResidenceFragment.OnFragmentInteractionListener{

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        followPrevProcess();
        getReady();

        ImageView iconAddImage = (ImageView) findViewById(R.id.iconAddImage);
        iconAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addResidence_btn(null);
            }
        });
    }

    private void getReady(){
        showSearchView();
        showMapView(null);
        createGoogleApiClient(this);
        createInquilinosApiClient();
        updateLoginLabels();
    }

    private void followPrevProcess(){
        String inProcessParameter = getIntent().getStringExtra("InProcess");
        if(inProcessParameter != null && !inProcessParameter.isEmpty()){
            switch (inProcessParameter){
                case "AddResidence":
                    ShowNewResidenceDialog();
                    break;
            }
        }
    }

    public void login_btn(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void join_btn(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void goToProfile_btn(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void logout_btn(View view){
        FirebaseAuth.getInstance().signOut();
        AppCore.isLogin = false;
        updateLoginLabels();
    }

    private void AskToLogin(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.askToLoginTitle));
        dialog.setMessage(getResources().getString(R.string.askToLoginMessage))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.login_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.putExtra("InProcess", "AddResidence");
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Stay the same
                    }
                });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void ShowNewResidenceDialog(){
        Intent intent = new Intent(MainActivity.this, NewResidenceActivity.class);
        startActivity(intent);
    }

    public void addResidence_btn(View view){
        if(!AppCore.isLogin){
            AskToLogin();
        }else{
            ShowNewResidenceDialog();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), 12));
        addMarkers();
        AppCore.getGoogleApiClient(this).connect();
    }

    public void addMarkers(){
        mInquilinosApiClient.getResidences().enqueue(new Callback<List<Residence>>() {
            @Override
            public void onResponse(Call<List<Residence>> call, Response<List<Residence>> response) {
                if(response == null || response.body() == null){
                    Log.d("InquilinosApp", "No Values Found");
                    return;
                }
                Log.d("InquilinosApp", "Valores encontrados " + response.body());
                for (Residence residence : response.body()) {
                    LatLng place = new LatLng(residence.Latitude, residence.Longitude);
                    if (residence.ForRent) {
                        if (residence.ResidenceType == ResidenceTypes.Building) {

                        } else if (residence.ResidenceType == ResidenceTypes.House) {

                        } else {

                        }
                    } else {
                        if (residence.ResidenceType == ResidenceTypes.Building) {

                        } else if (residence.ResidenceType == ResidenceTypes.House) {

                        } else {

                        }
                    }
                    mMap.addMarker(new MarkerOptions().position(place)).setTag(String.valueOf(residence.Id));
                }
            }

            @Override
            public void onFailure(Call<List<Residence>> call, Throwable t) {
                Log.d("InquilinosApp", "Error encontrado " + t.getMessage());
            }
        });
    }

    public void getLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(AppCore.getGoogleApiClient(this));
            if (mLastLocation != null) {
                double lat = mLastLocation.getLatitude();
                double lng = mLastLocation.getLongitude();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14));
            } else {
                Toast.makeText(this, "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
                Log.d("InquilinosApp", "LAST LOCATION VARIABLE IS NULL");
            }
        }else{
            Log.d("InquilinosApp", "USER DID NOT GAVE LOCATION PERMISSION TO THE APP");
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }else{
            Log.d("InquilinosApp", "USER ALREADY GAVE LOCATION PERMISSION TO THE APP");
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NotNull ConnectionResult connectionResult) {
    }

    public void showSearchView(){
        DisplayFragment displayFragment =
                new DisplayFragment(this, getSupportFragmentManager(), getFragmentManager(), mMap, R.id.searchFrameLayout);
        displayFragment.execute(SEARCH_VIEW);
    }

    public void showMapView(View view){
        DisplayFragment displayFragment =
                new DisplayFragment(this, getSupportFragmentManager(), getFragmentManager(), null, R.id.displayFragment);
        displayFragment.execute(MAP_VIEW_MODE);
    }

    public void showListView(View view){
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.popupFragment);
        frameLayout.setVisibility(View.GONE);
        DisplayFragment displayFragment =
                new DisplayFragment(this, getSupportFragmentManager(), getFragmentManager(), null, R.id.displayFragment);
        displayFragment.execute(LIST_VIEW_MODE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int selectedHouseId = Integer.parseInt(marker.getTag().toString());
        Log.d("InquilinosApp", "Click on Marker: " + Integer.parseInt(marker.getTag().toString()));
        mInquilinosApiClient.getResidenceById(selectedHouseId).enqueue(new Callback<Residence>() {
            @Override
            public void onResponse(Call<Residence> call, Response<Residence> response) {
                //Loading Fragment
                DisplayFragment displayFragment = new DisplayFragment(MainActivity.this,
                        getSupportFragmentManager(), getFragmentManager(), response.body(), R.id.popupFragment);
                displayFragment.execute(POPUP_VIEW);
                //Making Fragment visible
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.popupFragment);
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<Residence> call, Throwable t) {
                Log.d("InquilinosApp", "Error encontrado " + t.getMessage());
            }
        });
        return true;
    }

    private void updateLoginLabels(){
        RelativeLayout relativeLayout1 = (RelativeLayout) findViewById(R.id.defaultWelcomeLabels);
        RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.loginWelcomeLabels);
        if(isLogin){
            relativeLayout1.setVisibility(View.GONE);
            relativeLayout2.setVisibility(View.VISIBLE);
            TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
            usernameTextView.setText(AppCore.loginUser + ", ");
        }else{
            relativeLayout1.setVisibility(View.VISIBLE);
            relativeLayout2.setVisibility(View.GONE);
        }
    }

}
