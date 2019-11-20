package com.example.memories;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class Maps extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mapAPI;
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final int GOOGLE_MAPS_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;

        Permissions();

       /*LatLng Maharashtra = new LatLng(19.389137, 76.031094);
        mapAPI.addMarker(new MarkerOptions().position(Maharashtra).title("Maharashtra"));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(Maharashtra));*/
    }

    private void setUpMap(){
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null && mapAPI != null) {

                                LatLng moi = new LatLng(location.getLatitude(),location.getLongitude());
                                mapAPI.addMarker(new MarkerOptions().position(moi).title("Moi"));

                                mapAPI.moveCamera(CameraUpdateFactory.newLatLng(moi));
                            }
                        }
                    });

            mapAPI.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }
    }

    protected void Permissions() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GOOGLE_MAPS_REQUEST_CODE);
        }
        else setUpMap();

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, GOOGLE_MAPS_REQUEST_CODE);
        }
        else setUpMap();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, GOOGLE_MAPS_REQUEST_CODE);
        }
        else setUpMap();*/
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case GOOGLE_MAPS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    setUpMap();
                } else {
                    // permission denied
                }
                return;
            }
        }
    }
    //On gere le retour vers Camera
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Maps.this, Camera.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
