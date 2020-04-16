package com.donfyy.modularity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.donfyy.annotations.BindView;

public class MainActivity extends AppCompatActivity {


    @BindView
    String abc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}