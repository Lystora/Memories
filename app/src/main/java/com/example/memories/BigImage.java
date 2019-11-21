package com.example.memories;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class BigImage extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    protected ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigimage);

        img = (ImageView) findViewById(R.id.big_image);

        sharedPreferences = getBaseContext().getSharedPreferences("click_photo", MODE_PRIVATE);
        String name = sharedPreferences.getString("photo_actuelle", null);

        img.setImageBitmap(BitmapFactory.decodeFile(name));


    }

}
