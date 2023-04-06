package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Help extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(view ->{
            finish();
        });

    }
}