package com.example.memories;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.memories.util.OnSwipeTouchListener;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

import java.io.File;

public class Gallery extends AppCompatActivity {

    protected View GalleryView;
    protected ImageView imageView;
    protected GridView gridview;
    protected File[] mlistFiles;
    private static final int MY_CAMERA_REQUEST_CODE = 1;
    SharedPreferences sharedPreferences;
    boolean isImageFitToScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        // Cache la barre de titre.
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        // L'application est en fullscreen.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // On accède au dossier "test_photo"
        File repertoire = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_photo");

        // Si le répertoire n'exsiste pas :
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

        mlistFiles = repertoire.listFiles();

        // Initialisation de la View Galerie
        GalleryView = (View) findViewById(R.id.Gallery_view);

        // Initialisation de la GridView
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));



        //Gestion du swipe sur la view
        GalleryView.setOnTouchListener(new OnSwipeTouchListener(Gallery.this) {
            public void onSwipeTop() {
                //On swipe vers le haut
            }
            public void onSwipeRight() {
                //On swipe vers la droite
            }
            public void onSwipeLeft() {
                //On swipe vers la gauche
                //On lance Camera
                Intent intent = new Intent(Gallery.this, Camera.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
            public void onSwipeBottom() {
                //On swipe vers le bas
            }

        });

        // Gestion du swipe sur la gridview
        gridview.setOnTouchListener(new OnSwipeTouchListener(Gallery.this) {

            @Override
            public void onSwipeTop() {
                //On swipe vers le haut
            }
            @Override
            public void onSwipeRight() {
                //On swipe vers la droite
            }
            @Override
            public void onSwipeLeft() {
                //On swipe vers la gauche
                //On lance Camera
                Intent intent = new Intent(Gallery.this, Camera.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
            @Override
            public void onSwipeBottom() {
                //On swipe vers le bas
            }


        });
    }


    public class ImageAdapter extends BaseAdapter {
        private Context c;

        public ImageAdapter(Context c) {this.c = c;}

        @Override
        public int getCount() {return mlistFiles.length;}

        @Override
        public Object getItem(int position) {return position;}

        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                // Créer une nouvelle vue
                imageView = new ImageView(this.c);
                imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {

                // On utilise convertView s'il est disponible
                imageView = (ImageView) convertView;
            }

            // Demande au décodeur de sous-échantillonner l'image d'origine, en renvoyant une image plus petite pour économiser de la mémoire.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 20;

            // Affiche les images dans la Galerie
            imageView.setImageBitmap(BitmapFactory.decodeFile(mlistFiles[position].getAbsolutePath(), options));


            // Les SharedPreferences sont à récupérer depuis un context
            sharedPreferences = c.getSharedPreferences("click_photo",c.MODE_PRIVATE);

            // Ecoute sur les images de la Galerie
            imageView.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Afin de sauvegarder un élémént, on ouvre un éditeur, avec sharedPreferences.edit() (sans oubliez d’appeler la méthode .apply() )
                    sharedPreferences
                            .edit()
                            .putString("photo_actuelle", mlistFiles[position].getAbsolutePath()) //enregistre le chemin absolu de la photo dans la sharedpreferences
                            .apply();

                    // Changement d'activity
                    Intent intent = new Intent(Gallery.this, BigImage.class);
                    startActivity(intent);
                }
            });

                TypedValue outValue = new TypedValue();
                getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                imageView.setBackgroundResource(outValue.resourceId);

            return imageView;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
        }

    }
}
