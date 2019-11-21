package com.example.memories;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    protected ImageView Logo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Cache la barre de titre.
        getSupportActionBar().hide();

        // L'application est en fullscreen.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialisation de l'ImageView (le logo)
        Logo = (ImageView) findViewById(R.id.imgLogo);

        // Splash Screen pendant 3 secondes puis on passe sur l'activity Camera
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, Camera.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
