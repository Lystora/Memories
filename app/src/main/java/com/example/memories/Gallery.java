package com.example.memories;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

import java.io.File;

public class Gallery extends AppCompatActivity {
    View GalleryView;
    private File[] mlistFiles;
    private static final int MY_CAMERA_REQUEST_CODE = 1;
    private static final int PICTURE_RESULT = 1;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        GalleryView = (View) findViewById(R.id.Gallery_view);

        File directoryName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_photo");
        if (!directoryName.exists() ) {
            //On crée le répertoire (s'il n'existe pas!!)
            directoryName.mkdirs();
        }
        mlistFiles = directoryName.listFiles();

        //File extDir = Environment.getExternalStorageDirectory();
        //mlistFiles = new File(extDir, "DCIM/camera").listFiles();

        GridView gridview = (GridView) findViewById(R.id.gridview);
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
        //Gestion du swipe sur la gridview
        gridview.setOnTouchListener(new OnSwipeTouchListener(Gallery.this) {
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
    }
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {mContext = c;}

        public int getCount() {return mlistFiles.length;}

        public Object getItem(int position) {return position;}

        public long getItemId(int position) {return position;}

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 20;

            imageView.setImageBitmap(BitmapFactory.decodeFile(mlistFiles[position].getAbsolutePath(), options));

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
