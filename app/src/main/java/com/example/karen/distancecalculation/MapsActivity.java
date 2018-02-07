package com.example.karen.distancecalculation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMapClickListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private int count = 0;
    private Location mLocation1, mLocation2;
    private LatLng mLatLng1, mLatLng2;
    private TextView textDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initViews();
        requestPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initViews() {
        textDistance = findViewById(R.id.txtDistance);
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                count = 0;
                textDistance.setText("");
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "don't have permission");
            return;
        }
        Log.d(TAG, "you have permission");
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapClickListener(this);
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(TAG, "MyLocation button clicked");
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Log.d(TAG, "OnMap location " + location);

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(new LatLng(location.getLatitude(),
                location.getLongitude()));
        mMap.addCircle(circleOptions);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        count++;
        mMap.addMarker(new MarkerOptions().position(latLng));
        counter(latLng);
    }

    private void counter(LatLng latLng) {
        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        switch (count) {
            case 1:
                mLocation1 = location;
                mLatLng1 = latLng;
                break;
            case 2:
                mLocation2 = location;
                mLatLng2 = latLng;
                showDistance();
                break;
        }
    }

    private void showDistance() {
        Log.d(TAG, "distance " + mLocation1.distanceTo(mLocation2));
        count = 0;
        textDistance.setText("The distance between the two points is " + (int)mLocation1.distanceTo(mLocation2) + " meters");
        mMap.addPolyline(new PolylineOptions()
                .add(mLatLng1, mLatLng2)
                .width(5)
                .color(Color.RED));
    }
}
