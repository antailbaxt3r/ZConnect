package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.zconnect.zutto.zconnect.commonModules.BaseActivity;

public class WebViewActivity extends BaseActivity {
    private WebView webViewurl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        webViewurl = (WebView) findViewById(R.id.webView1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.web_view_app_bar_home);
        setSupportActionBar(toolbar);
        setActionBarTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
        webViewurl.getSettings().setJavaScriptEnabled(true);
        webViewurl.getSettings().setBuiltInZoomControls(true);
        final Activity activity = this;
        webViewurl.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });
        webViewurl.loadUrl(getIntent().getStringExtra("url"));

    }
}
