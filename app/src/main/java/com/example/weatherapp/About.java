package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class About extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(view ->{
            finish();
        });
    }
}