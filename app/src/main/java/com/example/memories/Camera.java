package com.example.memories;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.memories.util.OnSwipeTouchListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Camera extends AppCompatActivity {
    private Button btn,btn_save;
    private ImageView imgview;
    private String photoPath = null;
    private Bitmap image;
    private static final int MY_CAMERA_REQUEST_CODE = 1;
    private static final int PICTURE_RESULT = 1;

    View CameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        CameraView = (View) findViewById(R.id.camera_view);

        btn = (Button) findViewById(R.id.camera_btn);
        btn_save = (Button) findViewById(R.id.camera_btn_save);
        imgview = (ImageView) findViewById(R.id.camera_image);

        btn_save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enregistrer la photo
                MediaStore.Images.Media.insertImage(getContentResolver(), image, "nom image", "description");
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ouvrir une fenetre pour prendre la photo
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Gerer l'intent
                if(intent.resolveActivity(getPackageManager()) != null){
                    //creer un nom de fichier
                    String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    //File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File photoDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_photo");
                    try {
                        File photoFile = File.createTempFile("photo"+time,".jpg",photoDir);
                        //Enregistrer le chemin complet
                        photoPath = photoFile.getAbsolutePath();

                        //Accès au fichier
                        Uri photoUri = FileProvider.getUriForFile(Camera.this,
                                Camera.this.getApplicationContext().getPackageName()+".provider", photoFile);

                        //Enregistre la photo dans le fichier temporaire
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                        //Ouvrir l'activité par rapport à l'intent
                        startActivityForResult(intent, PICTURE_RESULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        //Gestion du swipe
        CameraView.setOnTouchListener(new OnSwipeTouchListener(Camera.this) {
            public void onSwipeTop() {
                //On swipe vers le haut
            }

            public void onSwipeRight() {
                //On swipe vers la droite
                //On lance Gallery
                Intent intent = new Intent(Camera.this, Gallery.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }

            public void onSwipeLeft() {
                //On swipe vers la gauche
                //On lance Maps
                Intent intent = new Intent(Camera.this, Maps.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            public void onSwipeBottom() {
                //On swipe vers le bas
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICTURE_RESULT && resultCode == RESULT_OK){
            //Recupère l'image
            image = BitmapFactory.decodeFile(photoPath);
            //Affichier l'image
            imgview.setImageBitmap(image);
        }
    }
}
