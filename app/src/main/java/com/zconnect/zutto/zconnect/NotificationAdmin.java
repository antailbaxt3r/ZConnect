package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.zconnect.zutto.zconnect.adapters.NotificationRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class NotificationAdmin extends BaseActivity {

    private ArrayList notification_type=new ArrayList<>(Arrays.asList("Send Text/Image Notification"));
    private Toolbar mActionBarToolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_admin);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mActionBarToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        mActionBarToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        recyclerView=(RecyclerView)findViewById(R.id.menu_notifications_add);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new NotificationRVAdapter(notification_type,getBaseContext()));
    }
}
