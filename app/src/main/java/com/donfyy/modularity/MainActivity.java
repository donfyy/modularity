package com.donfyy.modularity;

import android.os.Bundle;
import android.widget.TextView;

import com.donfyy.AutoBind;
import com.donfyy.annotations.BindView;
import com.donfyy.annotations.BindView2;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @BindView
    String abc;
    @BindView2(R.id.t1)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoBind.getInstance().inject(this);
        tv.setText("hi auto bind!");
    }
}