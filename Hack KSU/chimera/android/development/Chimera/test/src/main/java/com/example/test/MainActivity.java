package com.example.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView myText = null;
    public String gpsCoords = "120 60";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout lView = new LinearLayout(this);

        myText = new TextView(this);
        myText.setText(gpsCoords);

        lView.addView(myText);

        setContentView(lView);
    }
}
