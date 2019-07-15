package com.zconnect.zutto.zconnect.fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;

public class UpdateAppActivity extends BaseActivity {
    Button updateApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
        updateApp = findViewById(R.id.btn_update);
        updateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updateUrl = getIntent().getStringExtra("updateLink");
                if(updateUrl == null){
                    updateUrl = "https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect";
                }
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
