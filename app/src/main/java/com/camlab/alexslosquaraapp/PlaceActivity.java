package com.camlab.alexslosquaraapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by alex on 11.11.2014.
 */
public class PlaceActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        TextView mPlaceNameView = (TextView) findViewById(R.id.placeNameView);
        TextView mPlaceCategoryView = (TextView) findViewById(R.id.placeCategoryView);
        TextView mPlaceDistanceView = (TextView) findViewById(R.id.placeDistanceView);
        TextView mPlaceAddressView = (TextView) findViewById(R.id.placeAddressView);

        mPlaceNameView.setText(getIntent().getStringExtra("Name"));
        mPlaceCategoryView.setText(getIntent().getStringExtra("Category"));
        mPlaceDistanceView.setText(getIntent().getStringExtra("Distance"));
        mPlaceAddressView.setText(getIntent().getStringExtra("Address"));


    }
}