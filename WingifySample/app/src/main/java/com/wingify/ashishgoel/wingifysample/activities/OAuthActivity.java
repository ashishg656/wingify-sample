package com.wingify.ashishgoel.wingifysample.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.wingify.ashishgoel.wingifysample.extras.AppConstants;
import com.wingify.ashishgoel.wingifysample.fragments.OAuthWebViewFragment;

public class OAuthActivity extends FragmentActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String authenticationUrl = getIntent().getStringExtra(AppConstants.STRING_EXTRA_AUTHENCATION_URL);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        OAuthWebViewFragment oAuthWebViewFragment = new OAuthWebViewFragment(authenticationUrl);
        fragmentTransaction.add(android.R.id.content, oAuthWebViewFragment);
        fragmentTransaction.commit();
    }
}