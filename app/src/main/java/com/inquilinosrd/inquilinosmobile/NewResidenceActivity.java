package com.inquilinosrd.inquilinosmobile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.inquilinosrd.inquilinosmobile.Models.Residence;
import com.inquilinosrd.inquilinosmobile.Models.ResidenceTypes;
import com.inquilinosrd.inquilinosmobile.Services.AppCore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import layout.MapFragment;
import layout.SearchFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inquilinosrd.inquilinosmobile.Services.AppCore.createGoogleApiClient;
import static com.inquilinosrd.inquilinosmobile.Services.AppCore.createInquilinosApiClient;
import static com.inquilinosrd.inquilinosmobile.Services.AppCore.getGoogleApiClient;
import static com.inquilinosrd.inquilinosmobile.Services.AppCore.mInquilinosApiClient;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.CAMERA_PERMISSION_REQUEST;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.CAMERA_REQUEST;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.DEFAULT_LATITUDE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.DEFAULT_LONGITUDE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.FORM_PAGE1_VIEW;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.FORM_PAGE2_VIEW;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.FORM_PAGE3_VIEW;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.GALLERY_PERMISSION_REQUEST;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.GALLERY_PICTURE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.SEARCH_VIEW;

public class NewResidenceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        NewResidenceForm_page1.OnFragmentInteractionListener, NewResidenceForm_page2.OnFragmentInteractionListener,
        NewResidenceForm_page3.OnFragmentInteractionListener, GoogleMap.OnMapClickListener, View.OnClickListener,
        MapFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener{

    private GoogleMap mMap;
    public Residence residence;

    private Bitmap bitmap;
    private String selectedImagePath;

    private Button nextPage_btn;

    public EditText roomsEditText;
    public EditText bathroomsEditText;
    public EditText parkingEditText;
    public EditText dimensionsEditText;
    public EditText priceEditText;
    public Switch isfForRentSaleSwitch;
    public EditText descriptionText;
    public Spinner residenceTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_residence);

        residence = new Residence();

        getReady();
    }

    private void getReady(){
        createGoogleApiClient(this);
        createInquilinosApiClient();

        DisplayFragment displayFragment =
                new DisplayFragment(this, getSupportFragmentManager(), getFragmentManager(), null, R.id.newResidenceForm);
        displayFragment.execute(FORM_PAGE1_VIEW);
    }

    public void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Opciones para imagenes");
        myAlertDialog.setMessage("Como deseas subir tu imagen?");
        myAlertDialog.setPositiveButton("Galeria",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = ContextCompat.checkSelfPermission(NewResidenceActivity.this, android.Manifest.permission_group.STORAGE);
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            launchGallery();
                        } else {
                            ActivityCompat.requestPermissions(NewResidenceActivity.this, new String[]{
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST);
                        }
                    }
                });
        myAlertDialog.setNegativeButton("Camara",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = ContextCompat.checkSelfPermission(NewResidenceActivity.this, android.Manifest.permission.CAMERA);
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            launchCamera();
                        } else {
                            ActivityCompat.requestPermissions(NewResidenceActivity.this, new String[]{
                                    android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST);
                        }
                    }
                });
        myAlertDialog.show();
    }

    private void launchGallery() {
        Intent pictureActionIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(
                pictureActionIntent,
                GALLERY_PICTURE);
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }

    private void saveUIParams_page1(){
        setUpResidenceType();
        residence.Rooms = Integer.parseInt(bathroomsEditText.getText().toString());
        residence.Bathrooms = Integer.parseInt(bathroomsEditText.getText().toString());
        residence.ParkingSpaces = Integer.parseInt(bathroomsEditText.getText().toString());
        residence.Description = descriptionText.getText().toString();
        residence.Dimensions = Double.parseDouble(dimensionsEditText.getText().toString());
        residence.Price = Integer.parseInt(priceEditText.getText().toString());
        residence.ResidenceSeller = AppCore.loginUser;
    }

    private void setUpResidenceType(){
        for (ResidenceTypes e : ResidenceTypes.values()) {
            if (e.ordinal() == residenceTypeSpinner.getSelectedItemId()) {
                residence.ResidenceType = e;
            }
        }

        if(isfForRentSaleSwitch.isChecked()){
            residence.ForRent = true;
        }else{
            residence.ForRent = false;
        }
    }

    private void postResidence(){
        mInquilinosApiClient.postResidence(residence).enqueue(new Callback<Residence>() {
            @Override
            public void onResponse(Call<Residence> call, Response<Residence> response) {
                Intent intent = new Intent(NewResidenceActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Residence> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        getGoogleApiClient(this).connect();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), 14));

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.newResidenceForm);
        if(fragment instanceof NewResidenceForm_page2){
            DisplayFragment displayFragment =
                    new DisplayFragment(this, getSupportFragmentManager(), getFragmentManager(), mMap,
                            ((NewResidenceForm_page2) fragment).searchAddressFrameLayout.getId());
            displayFragment.execute(SEARCH_VIEW);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (CAMERA_PERMISSION_REQUEST == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        launchCamera();
                    }
                }
            }
        } else if (GALLERY_PERMISSION_REQUEST == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        launchGallery();
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NewResidenceForm_page3 fragment =
                (NewResidenceForm_page3) getSupportFragmentManager().findFragmentById(R.id.newResidenceForm);
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            fragment.loadedImage.setImageBitmap(bitmap);
            fragment.loadedImage.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Imagen añadida correctamente", Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {
                Uri pickedImage = data.getData();
                // Let's read picked image path using content resolver
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                selectedImagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
                fragment.loadedImage.setVisibility(View.VISIBLE);
                fragment.loadedImage.setImageBitmap(bitmap);
                // Do something with the bitmap
                Toast.makeText(this, "Imagen añadida correctamente", Toast.LENGTH_LONG).show();
                // At the end remember to close the cursor or you will end with the RuntimeException!
                cursor.close();
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void cancelForm_btn(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean isDouble(EditText editText){
        try{
            Double.parseDouble(editText.getText().toString());
        }catch (Exception e){
            Log.d("InquilinosApp", e.getMessage());
            editText.setError("Solo valores numericos son permitidos");
            return false;
        }
        return true;
    }

    public boolean isInteger(EditText editText){
        try{
            Integer.parseInt(editText.getText().toString());
        }catch (Exception e){
            Log.d("InquilinosApp", e.getMessage());
            editText.setError("Solo valores numericos son permitidos");
            return false;
        }
        return true;
    }

    private boolean isValid(){
        EditText focusView = null;

        if(descriptionText.getText().toString().isEmpty() ) focusView = roomsEditText;
        if(priceEditText.getText().toString().isEmpty() ) focusView = roomsEditText;
        if(dimensionsEditText.getText().toString().isEmpty() ) focusView = roomsEditText;
        if(parkingEditText.getText().toString().isEmpty() ) focusView = roomsEditText;
        if(bathroomsEditText.getText().toString().isEmpty() ) focusView = roomsEditText;
        if(roomsEditText.getText().toString().isEmpty() ) focusView = roomsEditText;

        if(focusView!= null){
            focusView.setError("Este campo es requerido");
            return false;
        }

        if(!isDouble(priceEditText)) return false;
        if(!isDouble(priceEditText)) return false;

        if(!isInteger(parkingEditText)) return false;
        if(!isInteger(bathroomsEditText)) return false;
        if(!isInteger(roomsEditText)) return false;

        return true;
    }

    public void nextPage_btn(View view){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.newResidenceForm);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressAnimation);
        if(fragment instanceof NewResidenceForm_page1){
            if(isValid()) {
                saveUIParams_page1();
                progressBar.setProgress(66);
                DisplayFragment displayFragment =
                        new DisplayFragment(this, getSupportFragmentManager(), getFragmentManager(), null, R.id.newResidenceForm);
                displayFragment.execute(FORM_PAGE2_VIEW);
            }
        }else if(fragment instanceof NewResidenceForm_page2){
            nextPage_btn = (Button) findViewById(R.id.nextPage_btn);
            nextPage_btn.setText("Guardar");
            progressBar.setProgress(100);
            DisplayFragment displayFragment =
                    new DisplayFragment(this, getSupportFragmentManager(), getFragmentManager(), null, R.id.newResidenceForm);
            displayFragment.execute(FORM_PAGE3_VIEW);
        }else if(fragment instanceof NewResidenceForm_page3){
            //Need to save information
            sendForm();
        }
    }

    private void sendForm() {
        nextPage_btn.setEnabled(false);
        NewResidenceForm_page3 fragment =
                (NewResidenceForm_page3) getSupportFragmentManager().findFragmentById(R.id.newResidenceForm);
        fragment.loadedImage.setDrawingCacheEnabled(true);
        fragment.loadedImage.buildDrawingCache();
        Bitmap bitmap = fragment.loadedImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference("Images");
        final String fileName = "IMG_" + new Date().getTime() + ".jpg";
        residence.Images = fileName;
        StorageReference mountainsRef = storageRef.child(fileName);
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                postResidence();
                finish();
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        residence.Latitude = latLng.latitude;
        residence.Longitude = latLng.longitude;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            residence.Address =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        startDialog();
    }
}
