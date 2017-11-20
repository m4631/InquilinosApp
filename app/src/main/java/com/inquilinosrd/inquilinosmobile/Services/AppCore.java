package com.inquilinosrd.inquilinosmobile.Services;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.inquilinosrd.inquilinosmobile.MainActivity;
import com.inquilinosrd.inquilinosmobile.NewResidenceActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppCore {
    public static boolean isLogin;
    public static int SELECTED_VIEW_MODE;
    public static InquilinosAPI mInquilinosApiClient;
    private static GoogleApiClient mGoogleApiClient_MainActivity;
    private static GoogleApiClient mGoogleApiClient_NewResidenceActivity;
    public static String loginUser;

    public static void createGoogleApiClient(Context context){
        if(context instanceof MainActivity){
            if(mGoogleApiClient_MainActivity == null){
                mGoogleApiClient_MainActivity = createApiClient(context);
            }
        }else if(context instanceof NewResidenceActivity){
            if(mGoogleApiClient_NewResidenceActivity == null){
                mGoogleApiClient_NewResidenceActivity = createApiClient(context);
            }
        }
    }

    private static GoogleApiClient createApiClient(Context context){
        return new GoogleApiClient.Builder(context)
                .addConnectionCallbacks( (GoogleApiClient.ConnectionCallbacks) context)
                .addOnConnectionFailedListener( (GoogleApiClient.OnConnectionFailedListener) context)
                .addApi(LocationServices.API)
                .build();
    }

    public static void createInquilinosApiClient(){
        if(mInquilinosApiClient == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://inquilinosapi.azurewebsites.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mInquilinosApiClient = retrofit.create(InquilinosAPI.class);
        }
    }

    public static GoogleApiClient getGoogleApiClient(Context context){
        createGoogleApiClient(context);
        if(context instanceof MainActivity){
            return mGoogleApiClient_MainActivity;
        }else if(context instanceof NewResidenceActivity){
            return mGoogleApiClient_NewResidenceActivity;
        }
        return null;
    }

    public static void setUpSpinner(Context context, Spinner spinner, int arrayId){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
