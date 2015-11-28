package com.wingify.ashishgoel.wingifysample.activities;

import android.content.Intent;
import android.os.Bundle;

import com.wingify.ashishgoel.wingifysample.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ashish Goel on 11/28/2015.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                switchToHomeOrLoginActivity();
            }
        }, 1000);
    }

    private void switchToHomeOrLoginActivity() {
        Intent intent = new Intent(this, LaunchActivity.class);
        startActivity(intent);
    }
}
