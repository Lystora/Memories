package com.example.memories;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class BigImage extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    protected ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigimage);

        // Cache la barre de titre.
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // L'application est en fullscreen.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        img = (ImageView) findViewById(R.id.big_image);

        sharedPreferences = getBaseContext().getSharedPreferences("click_photo", MODE_PRIVATE);
        String name = sharedPreferences.getString("photo_actuelle", null);

        // On met l'image dans l'ImageView
        img.setImageBitmap(BitmapFactory.decodeFile(name));


    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), Gallery.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

}
