package com.example.memories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memories.util.OnSwipeTouchListener;
import com.example.memories.util.ShakeDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.prefs.Preferences;

public class Camera extends AppCompatActivity {
    private Button btn_camera,btn_save;
    private ImageView imgview;
    private String photoPath = null;
    private Bitmap image;
    private static final int MY_CAMERA_REQUEST_CODE = 1;
    private static final int PICTURE_RESULT = 1;
    private static final int GOOGLE_MAPS_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    public TextView test;
    protected File photoFile;
    SharedPreferences LAT, LON;
    SharedPreferences.Editor LAT_editor, LON_editor;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    View CameraView;

    MediaPlayer test_son1, test_son2;

    Bitmap image2;
    Bitmap bitmap;
    String picture_location;

    Uri photoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        // Cache la barre de titre.
        getSupportActionBar().hide();

        // L'application est en fullscreen.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        CameraView = (View) findViewById(R.id.camera_view);
        btn_camera = (Button) findViewById(R.id.camera_btn);
        btn_save = (Button) findViewById(R.id.camera_btn_save);
        imgview = (ImageView) findViewById(R.id.camera_image);
        test = (TextView) findViewById(R.id.camera_test_text);

        LON = getSharedPreferences("LATITUDE", MODE_PRIVATE);
        LAT = getSharedPreferences("LONGITUDE", MODE_PRIVATE);
        LAT_editor = LAT.edit();
        LON_editor = LON.edit();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(savedInstanceState != null){
            photoUri =  savedInstanceState.getParcelable("UriImage");
            imgview.setImageURI(photoUri);

            btn_save.setEnabled(savedInstanceState.getBoolean("enable"));
            btn_save.setVisibility(savedInstanceState.getInt("visible"));
        }

        // ENREGISTREMENT DE LA PHOTO + ECOUTE SUR LE BOUTON ENREGISTRER
        btn_save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MediaStore.Images.Media.insertImage(getContentResolver(), image, "nom image", "description");

                // name est un nom quelconque : on a choisit comme nom le format date.
                String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                savePicture(image,name+".jpg");

                setImgLatLng(name+".jpg");
                image = null;
                imgview.setImageBitmap(image);

                Toast.makeText(getApplicationContext(),"Picture saved !", Toast.LENGTH_LONG).show();

                btn_save.setEnabled(false);
                btn_save.setVisibility(View.INVISIBLE);

            }
        });


        // OUVERTURE DE L'APPLICATION APPAREIL PHOTO
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ouverture d'une fenetre pour prendre la photo
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // - intent.resolveActivity retourne le composant qui doit être utilisé pour traiter l'intent.
                // - Si getPackage() est non-NULLL, seuls les composants de cette activité seront pris en compte.
                if(intent.resolveActivity(getPackageManager()) != null){
                    String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); // créer un nom de fichier
                    File repertoirePhoto = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // définit le répertoire

                    try {
                        // créer un fichier représentant la photo
                        photoFile = File.createTempFile("photo"+name,".jpg",repertoirePhoto);
                        photoPath = photoFile.getAbsolutePath(); // Initialise le chemin complet de la photo

                        //Accès au fichier
                        photoUri = FileProvider.getUriForFile(Camera.this,
                                Camera.this.getApplicationContext().getPackageName()+".provider", photoFile);

                        //Enregistre la photo dans le fichier temporaire
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                        //Ouvrir l'activité par rapport à l'intent
                        startActivityForResult(intent, PICTURE_RESULT);

                    } catch (IOException e) {e.printStackTrace();}
                }
            }
        });


        //GESTION DU SWIPE GAUCHE / DROITE
        CameraView.setOnTouchListener(new OnSwipeTouchListener(Camera.this) {

            @Override
            public void onSwipeRight() {
                //On swipe vers la droite
                //On lance Gallery
                Intent intent = new Intent(Camera.this, Gallery.class);
                startActivity(intent);

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }

            @Override
            public void onSwipeLeft() {
                //On swipe vers la gauche
                //On lance Maps
                Intent intent = new Intent(Camera.this, Maps.class);
                startActivity(intent);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void onSwipeTop() {/*On swipe vers le haut*/}

            @Override
            public void onSwipeBottom() {/*On swipe vers le bas*/}
        });


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();

        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {

                if(test_son1 == null && photoUri!=null){

                    if(Locale.getDefault().getDisplayLanguage().equals("English")){
                        test_son1 = MediaPlayer.create(getApplicationContext(),R.raw.delete_voice);
                        test_son1.start();

                        test_son1 = null;

                        image = null;
                        photoUri = null;
                        imgview.setImageBitmap(image);

                        Toast.makeText(getApplicationContext(),"Current picture has been deleted !", Toast.LENGTH_LONG).show();
                    }else{
                        test_son1 = MediaPlayer.create(getApplicationContext(),R.raw.supp_photo);
                        test_son1.start();

                        test_son1 = null;

                        image = null;
                        photoUri = null;
                        imgview.setImageBitmap(image);

                        Toast.makeText(getApplicationContext(),"La photo a été suprrimer !", Toast.LENGTH_LONG).show();
                    }


                }else{
                    if(Locale.getDefault().getDisplayLanguage().equals("English")) {
                        test_son2 = MediaPlayer.create(getApplicationContext(), R.raw.no_picture);
                        test_son2.start();

                        Toast.makeText(getApplicationContext(), "There is no picture !", Toast.LENGTH_LONG).show();
                    }else{
                        test_son2 = MediaPlayer.create(getApplicationContext(), R.raw.pas_photo);
                        test_son2.start();

                        Toast.makeText(getApplicationContext(), "Il n'y a pas de photo !", Toast.LENGTH_LONG).show();
                    }

                }

                // Desactive le bouton enregistrer
                btn_save.setEnabled(false);
                btn_save.setVisibility(View.INVISIBLE);

            }
        });
    }

    // METHODE POUR ENREGISTRER LA PHOTO DANS UN REPERTOIRE, LA CONVERTIR EN JPEG  ET REDONNER UN NOM
    public void savePicture(Bitmap bm, String imgName){
        OutputStream fOut = null;

        File repertoire = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_photo");
        if (!repertoire.exists() ) {
            try{
                // Création du répertoire
                repertoire.mkdirs();
                Toast.makeText(this, "Le dossier 'test_photo' est créer !" , Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Toast.makeText(this, "Impossible de créer le dossier !" , Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        String strDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_photo";
        File f = new File(strDirectory, imgName);

        // Ouverture d'un flux d'écriture afin de remodeler notre image : bitmap -> jpeg et le répertoire où elle doit être conserver
        try {
            fOut = new FileOutputStream(f);

            // Compress image
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

            // Enregistre la photo dans la galerie
            MediaStore.Images.Media.insertImage(getContentResolver(),f.getAbsolutePath(), f.getName(), f.getName());
        } catch (Exception e) {e.printStackTrace();}
    }

    protected void setImgLatLng(final String namefile){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GOOGLE_MAPS_REQUEST_CODE);
        }else{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        LatLng current_location = new LatLng(location.getLatitude(), location.getLongitude());
                        LAT_editor.putString(namefile,Double.toString(current_location.latitude));
                        LON_editor.putString(namefile,Double.toString(current_location.longitude));
                        LAT_editor.apply(); LON_editor.apply();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
        }
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
        if(test_son1 != null){
            test_son1.release();
        }
        if(test_son2 != null){
            test_son2.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(test_son1 != null){
            test_son1.release();
        }
        if(test_son2 != null){
            test_son2.release();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICTURE_RESULT && resultCode == RESULT_OK){

            image = BitmapFactory.decodeFile(photoPath); // Récupère l'image
            //imgview.setImageBitmap(image);               // Insère l'image dans l'ImageView
            imgview.setImageURI(photoUri);

            //Activation du bouton ENREGISTRER après avoir pris une photo
            btn_save.setEnabled(true);
            btn_save.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(photoUri != null) outState.putParcelable("UriImage", photoUri);
        outState.putInt("visible",btn_save.getVisibility());
        outState.putBoolean("enable",btn_save.isEnabled());
    }
}