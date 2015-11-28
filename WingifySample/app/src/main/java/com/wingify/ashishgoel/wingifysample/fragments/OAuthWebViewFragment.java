package com.wingify.ashishgoel.wingifysample.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wingify.ashishgoel.wingifysample.R;
import com.wingify.ashishgoel.wingifysample.activities.LaunchActivity;


public class OAuthWebViewFragment extends Fragment {
    private WebView webView;
    private String authenticationUrl;

    public OAuthWebViewFragment() {

    }

    @SuppressLint("ValidFragment")
    public OAuthWebViewFragment(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView.loadUrl(authenticationUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("oauth_verifier=")) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), LaunchActivity.class);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_activity_layout, container, false);
        webView = (WebView) view.findViewById(R.id.webViewOAuth);
        return view;
    }
}