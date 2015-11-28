package com.wingify.ashishgoel.wingifysample.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.wingify.ashishgoel.wingifysample.R;

public class BaseActivity extends AppCompatActivity {

    ProgressBar progressBar;
    LinearLayout progressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setProgressBarVariables() {
        progressBar = (ProgressBar) findViewById(R.id.progressbarloading);
        progressBarLayout = (LinearLayout) findViewById(R.id.progressbarcontaienr);
    }

    void showLoadingLayout() {
        progressBar.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    void hideLoadingLayout() {
        progressBar.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
    }
}