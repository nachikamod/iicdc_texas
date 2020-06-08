package com.iicdc.shiningstars;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent MainActivity = new Intent(splashScreen.this, MainActivity.class);
                MainActivity.putExtra("splash", true);
                startActivity(MainActivity);
                finish();
            }
        }, 2000);
    }
}