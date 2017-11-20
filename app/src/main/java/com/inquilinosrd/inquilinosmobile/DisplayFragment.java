package com.inquilinosrd.inquilinosmobile;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.inquilinosrd.inquilinosmobile.Models.Residence;
import com.inquilinosrd.inquilinosmobile.Services.AppCore;

import java.util.List;

import layout.ListFragment;
import layout.MapFragment;
import layout.SearchFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inquilinosrd.inquilinosmobile.R.*;
import static com.inquilinosrd.inquilinosmobile.Services.AppCore.mInquilinosApiClient;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.FORM_PAGE1_VIEW;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.FORM_PAGE2_VIEW;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.FORM_PAGE3_VIEW;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.LIST_VIEW_MODE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.LIST_VIEW_MODE_MAIN;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.LIST_VIEW_MODE_PROFILE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.MAP_VIEW_MODE;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.POPUP_VIEW;
import static com.inquilinosrd.inquilinosmobile.Services.Constants.SEARCH_VIEW;

public class DisplayFragment extends AsyncTask<Integer, Void, Integer> {

    private android.app.FragmentManager fragmentManager;
    private FragmentManager supportFragmentManager;
    private Object parameterInstance;
    private Context context;
    private int fragmentId;
    private Fragment parentFragment;

    public DisplayFragment(Context context, FragmentManager supportFragmentManager,
                           android.app.FragmentManager fragmentManager, Object parameterInstance, int fragmentId){
        this.supportFragmentManager = supportFragmentManager;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.parameterInstance = parameterInstance;
        this.fragmentId = fragmentId;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        switch (params[0]){
            case MAP_VIEW_MODE:
                this.supportFragmentManager.beginTransaction()
                        .replace(fragmentId, MapFragment.newInstance("", ""))
                        .commit();
                AppCore.SELECTED_VIEW_MODE = MAP_VIEW_MODE;
                return MAP_VIEW_MODE;
            case LIST_VIEW_MODE:
                this.supportFragmentManager.beginTransaction()
                        .replace(fragmentId, ListFragment.newInstance("", ""))
                        .commit();
                AppCore.SELECTED_VIEW_MODE = LIST_VIEW_MODE;
                if(fragmentId == id.displayFragment) return LIST_VIEW_MODE_MAIN;
                else if(fragmentId == id.myHousesList) return LIST_VIEW_MODE_PROFILE;
                return (1<<32);
            case SEARCH_VIEW:
                this.supportFragmentManager.beginTransaction()
                        .replace(fragmentId, SearchFragment.newInstance("", ""))
                        .commit();
                return SEARCH_VIEW;
            case FORM_PAGE1_VIEW:
                this.supportFragmentManager.beginTransaction()
                        .replace(fragmentId, NewResidenceForm_page1.newInstance("", ""))
                        .commit();
                return FORM_PAGE1_VIEW;
            case FORM_PAGE2_VIEW:
                this.supportFragmentManager.beginTransaction()
                        .replace(fragmentId, NewResidenceForm_page2.newInstance("", ""))
                        .commit();
                return FORM_PAGE2_VIEW;
            case FORM_PAGE3_VIEW:
                this.supportFragmentManager.beginTransaction()
                        .replace(fragmentId, NewResidenceForm_page3.newInstance("", ""))
                        .commit();
                return FORM_PAGE3_VIEW;
            case POPUP_VIEW:
                this.supportFragmentManager.beginTransaction()
                        .replace(fragmentId, ResidenceFragment.newInstance("", ""))
                        .commit();
                return POPUP_VIEW;
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer aInteger) {
        super.onPostExecute(aInteger);
        parentFragment = this.supportFragmentManager.findFragmentById(fragmentId);

        switch (aInteger){
            case MAP_VIEW_MODE:
                startMap();
                break;
            case LIST_VIEW_MODE_MAIN:
                fillList();
                break;
            case LIST_VIEW_MODE_PROFILE:
                fillListForProfile();
                break;
            case SEARCH_VIEW:
                setUpSearch();
                break;
            case FORM_PAGE1_VIEW:
                setUpPage1();
                break;
            case FORM_PAGE2_VIEW:
                setUpPage2();
                break;
            case FORM_PAGE3_VIEW:
                setUpPage3();
                break;
            case POPUP_VIEW:
                setUpPopup();
                break;
        }
    }

    private void setUpPage1(){
        ((NewResidenceActivity)context).roomsEditText = ((NewResidenceForm_page1)parentFragment).roomsEditText;
        ((NewResidenceActivity)context).bathroomsEditText = ((NewResidenceForm_page1)parentFragment).bathroomsEditText;
        ((NewResidenceActivity)context).parkingEditText = ((NewResidenceForm_page1)parentFragment).parkingEditText;
        ((NewResidenceActivity)context).dimensionsEditText = ((NewResidenceForm_page1)parentFragment).dimensionsEditText;
        ((NewResidenceActivity)context).priceEditText = ((NewResidenceForm_page1)parentFragment).priceEditText;
        ((NewResidenceActivity)context).isfForRentSaleSwitch = ((NewResidenceForm_page1)parentFragment).isfForRentSaleSwitch;
        ((NewResidenceActivity)context).descriptionText = ((NewResidenceForm_page1)parentFragment).descriptionText;
        ((NewResidenceActivity)context).residenceTypeSpinner = ((NewResidenceForm_page1)parentFragment).residenceTypeSpinner;
        AppCore.setUpSpinner(context, ((NewResidenceActivity)context).residenceTypeSpinner, R.array.property_types);
    }

    private void setUpPage2(){
        DisplayFragment mapFragment =
                new DisplayFragment(context, supportFragmentManager, fragmentManager, null,
                        ((NewResidenceForm_page2) parentFragment).newResidenceLocationLayout.getId());
        mapFragment.execute(MAP_VIEW_MODE);
    }

    private void setUpPage3(){
        ((NewResidenceForm_page3) parentFragment).imageIcon.setOnClickListener((View.OnClickListener) context);
    }

    private void startMap(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) parentFragment.getChildFragmentManager().findFragmentById(id.residencesMap);
        mapFragment.getMapAsync( (OnMapReadyCallback) context);
    }

    private void fillList(){
        final RecyclerView recyclerView = ((ListFragment)parentFragment).recyclerView;
        AppCore.mInquilinosApiClient.getResidences().enqueue(new Callback<List<Residence>>() {
            @Override
            public void onResponse(Call<List<Residence>> call, Response<List<Residence>> response) {
                recyclerView.setLayoutManager(
                        new LinearLayoutManager(context, LinearLayout.VERTICAL, false));
                recyclerView.setAdapter(new residencesListAdapter(response.body()));
            }

            @Override
            public void onFailure(Call<List<Residence>> call, Throwable t) {}
        });
    }

    private void fillListForProfile(){
        mInquilinosApiClient.getResidencesByEmail(AppCore.loginUser).enqueue(new Callback<List<Residence>>() {
            @Override
            public void onResponse(Call<List<Residence>> call, Response<List<Residence>> response) {
                final RecyclerView recyclerView = ((ListFragment)parentFragment).recyclerView;

                final LinearLayout temp = (LinearLayout) ((ProfileActivity)context).findViewById(id.emptyHouseListLayout);

                if(response == null || response.body() == null || response.body().size() == 0){
                    //Seller is not registered. Does not have houses
                    temp.setVisibility(View.VISIBLE);
                    return;
                }

                temp.setVisibility(View.GONE);
                recyclerView.setLayoutManager(
                        new LinearLayoutManager(context, LinearLayout.VERTICAL, false));
                recyclerView.setAdapter(new residencesListAdapter(response.body()));
            }
            @Override
            public void onFailure(Call<List<Residence>> call, Throwable t) {}
        });
    }

    private void setUpPopup() {
        Residence residence = (Residence) parameterInstance;
        final ResidenceFragment residenceFragment = ((ResidenceFragment)parentFragment);
        residenceFragment.frameLayout.setBackgroundColor(Color.WHITE);

        //parentFragment.mainLinearLayout.addView();
        residenceFragment.closePopup_btn.setVisibility(View.VISIBLE);
        residenceFragment.closePopup_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                residenceFragment.frameLayout.setVisibility(View.GONE);
            }
        });
        residenceFragment.addressTV.setText(residence.Address);
        residenceFragment.roomsTV.setText(String.valueOf(residence.Rooms));
        residenceFragment.bathTV.setText(String.valueOf(residence.Bathrooms));
        residenceFragment.parkTV.setText(String.valueOf(residence.ParkingSpaces));
        residenceFragment.priceTV.setText(String.valueOf(residence.Price));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReference("Images").child(residence.Images).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(residenceFragment.mainImageTV);
            }
        });
    }

    private void setUpSearch(){
        PlaceAutocompleteFragment placeAutoComplete =
                    (PlaceAutocompleteFragment) fragmentManager.findFragmentById(id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Maps", "Place selected: " + place.getName());
                GoogleMap mMap = (GoogleMap) parameterInstance;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 14));

                if(context instanceof NewResidenceActivity){
                    mMap.addMarker(new MarkerOptions().position(
                            new LatLng(place.getLatLng().latitude, place.getLatLng().longitude)));
                    ((NewResidenceActivity) context).residence.Latitude = place.getLatLng().latitude;
                    ((NewResidenceActivity) context).residence.Longitude = place.getLatLng().longitude;
                    ((NewResidenceActivity) context).residence.Address = place.getAddress().toString();
                }
            }

            @Override
            public void onError(com.google.android.gms.common.api.Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });
    }

    public class residencesListAdapter extends RecyclerView.Adapter<DisplayFragment.residencesListVH>{
        private List<Residence> items;

        public residencesListAdapter(List<Residence> items) {
            this.items = items;
        }

        @Override
        public DisplayFragment.residencesListVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(layout.fragment_residence, parent, false);
            return new DisplayFragment.residencesListVH(view);
        }

        @Override
        public void onBindViewHolder(final DisplayFragment.residencesListVH holder, int position) {
            Residence residence = items.get(position);
            holder.addressTV.setText(residence.Address);
            holder.roomsTV.setText(String.valueOf(residence.Rooms));
            holder.bathTV.setText(String.valueOf(residence.Bathrooms));
            holder.parkTV.setText(String.valueOf(residence.ParkingSpaces));
            holder.priceTV.setText(String.valueOf(residence.Price));
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReference("Images").child(residence.Images).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(holder.mainImageTV);
                }
            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class residencesListVH extends RecyclerView.ViewHolder{
        public TextView addressTV, roomsTV, bathTV, parkTV, priceTV;
        public ImageView mainImageTV;
        public residencesListVH(View itemView) {
            super(itemView);
            addressTV = (TextView) itemView.findViewById(id.addressTextView);
            roomsTV = (TextView) itemView.findViewById(id.roomsTextView);
            bathTV = (TextView) itemView.findViewById(id.bathTextView);
            parkTV = (TextView) itemView.findViewById(id.parkTextView);
            priceTV = (TextView) itemView.findViewById(id.priceTextView);
            mainImageTV = (ImageView) itemView.findViewById(id.selectedHouseImage);
        }
    }

}

