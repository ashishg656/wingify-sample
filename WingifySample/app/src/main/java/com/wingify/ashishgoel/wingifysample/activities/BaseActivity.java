package com.wingify.ashishgoel.wingifysample.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class BaseActivity extends AppCompatActivity {

    ProgressBar progressBar;
    LinearLayout progressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setProgressBarVariables(){
        progressBar=(ProgressBar)findViewById(R.id.)
    }
}