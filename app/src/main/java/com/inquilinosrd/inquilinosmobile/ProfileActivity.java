package com.inquilinosrd.inquilinosmobile;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.inquilinosrd.inquilinosmobile.Services.AppCore;

import layout.ListFragment;

import static com.inquilinosrd.inquilinosmobile.Services.Constants.LIST_VIEW_MODE;

public class ProfileActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        DisplayFragment displayFragment = new DisplayFragment(ProfileActivity.this,
                getSupportFragmentManager(), getFragmentManager(), null, R.id.myHousesList);
        displayFragment.execute(LIST_VIEW_MODE);

        TextView welcomeLabel = (TextView) findViewById(R.id.welcomeLabelProfile);
        if(AppCore.loginUser != null){
            welcomeLabel.setText( "Bienvenido/a" + " " + AppCore.loginUser);
        }else {
            welcomeLabel.setText("Bienvenido/a otra vez");
        }
    }

    public void addResidence_btn(View view){
        Intent intent = new Intent(ProfileActivity.this, NewResidenceActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
