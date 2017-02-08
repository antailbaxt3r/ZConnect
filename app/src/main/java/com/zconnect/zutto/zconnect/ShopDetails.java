package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

public class ShopDetails extends AppCompatActivity {
    TextView name, details, number;
    LinearLayout linearLayout, numberlayout;
    SimpleDraweeView menu, image;
    String nam, detail, lat, lon, imageurl, num, menuurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            getWindow().setStatusBarColor(colorPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        nam = getIntent().getStringExtra("Name");
        detail = getIntent().getStringExtra("Details");
        lat = getIntent().getStringExtra("Lat");
        lon = getIntent().getStringExtra("Lon");
        imageurl = getIntent().getStringExtra("Imageurl");
        menuurl = getIntent().getStringExtra("Menu");
        num = getIntent().getStringExtra("Number");
        name = (TextView) findViewById(R.id.shop_details_name);
        details = (TextView) findViewById(R.id.shop_details_details);
        image = (SimpleDraweeView) findViewById(R.id.shop_details_image);
        menu = (SimpleDraweeView) findViewById(R.id.shop_details_menu_image);
        number = (TextView) findViewById(R.id.shop_details_number);
        numberlayout = (LinearLayout) findViewById(R.id.shop_details_num);
        linearLayout = (LinearLayout) findViewById(R.id.shop_details_directions);
        if (nam != null && detail != null && lat != null && lon != null && imageurl != null && menuurl != null && num != null) {
            name.setText(nam);
            details.setText(detail);
            image.setImageURI(Uri.parse(imageurl));
            menu.setImageURI(Uri.parse(menuurl));
            number.setText(num);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            numberlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num)));
                }
            });
        }
    }

}

