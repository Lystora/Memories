package com.example.memories;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;

public class Maps extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mapAPI;
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int GOOGLE_MAPS_REQUEST_CODE = 1;
    private File[] mlistFiles;
    SharedPreferences LAT, LON;
    SharedPreferences.Editor LAT_editor, LON_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        File directoryName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_photo");
        if (!directoryName.exists() ) {
            //On crée le répertoire (s'il n'existe pas!!)
            directoryName.mkdirs();
        }
        mlistFiles = directoryName.listFiles();
        LON = getSharedPreferences("LATITUDE", MODE_PRIVATE);
        LAT = getSharedPreferences("LONGITUDE", MODE_PRIVATE);
        LAT_editor = LAT.edit();
        LON_editor = LON.edit();
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
        for(int i=0; i<mlistFiles.length;i++){
            //recupération des noms des fichiers
            File current_file = mlistFiles[i];
            String current_name = current_file.getName();
            double latitude = Double.valueOf(LAT.getString(current_name, "0"));
            double longitude = Double.valueOf(LON.getString(current_name, "0"));
            if(latitude !=0 && longitude != 0) {
                LatLng current_location = new LatLng(latitude, longitude);
                Bitmap icone = BitmapFactory.decodeFile(current_file.getAbsolutePath());
                Bitmap small_icone = Bitmap.createScaledBitmap(icone, 120, 120, false);
                mapAPI.addMarker(new MarkerOptions()
                        .position(current_location)
                        .title("Photo" + i)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(icone)))
                        .anchor(0.5f, 1));
                        //.icon(BitmapDescriptorFactory.fromBitmap(small_icone)));
            }
        }
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null && mapAPI != null) {

                                LatLng moi = new LatLng(location.getLatitude(),location.getLongitude());
                                //mapAPI.addMarker(new MarkerOptions().position(moi).title("Moi"));
                                mapAPI.moveCamera(CameraUpdateFactory.newLatLng(moi));

                            }
                        }
                    });

            mapAPI.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap getMarkerBitmapFromView(Bitmap img){
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageBitmap(img);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
    protected void Permissions() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GOOGLE_MAPS_REQUEST_CODE);
        }
        else setUpMap();
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
