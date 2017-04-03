package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.GalleryFormat;

import java.util.ArrayList;

public class ShopDetails extends AppCompatActivity {


    TextView name, details, number;
    LinearLayout linearLayout, numberlayout;
    SimpleDraweeView menu, image;
    String nam, detail, lat, lon, imageurl, num, menuurl, shopid = null;
    DatabaseReference mDatabase, mDatabaseMenu;
    HorizontalScrollView galleryScroll, menuScroll;

    GalleryAdapter adapter;
    RecyclerView galleryRecycler;
    RecyclerView menuRecycler;
    ArrayList<String> menuImages = new ArrayList<String>();
    ArrayList<String> galleryImages = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        galleryScroll = (HorizontalScrollView) findViewById(R.id.galleryScroll);
        menuScroll = (HorizontalScrollView) findViewById(R.id.menuScroll);
        galleryScroll.setHorizontalScrollBarEnabled(false);
        menuScroll.setHorizontalScrollBarEnabled(false);

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

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        galleryRecycler = (RecyclerView) findViewById(R.id.galleryRecycler);
        galleryRecycler.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManagerMenu = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        menuRecycler = (RecyclerView) findViewById(R.id.menuRecycler);
        menuRecycler.setLayoutManager(layoutManagerMenu);



        nam = getIntent().getStringExtra("Name");
        detail = getIntent().getStringExtra("Details");
        lat = getIntent().getStringExtra("Lat");
        lon = getIntent().getStringExtra("Lon");
        imageurl = getIntent().getStringExtra("Imageurl");
        menuurl = getIntent().getStringExtra("Menu");
        num = getIntent().getStringExtra("Number");
        shopid = getIntent().getStringExtra("ShopId");


        name = (TextView) findViewById(R.id.shop_details_name);
        details = (TextView) findViewById(R.id.shop_details_details);
        image = (SimpleDraweeView) findViewById(R.id.shop_details_image);
//      Menu = (SimpleDraweeView) findViewById(R.id.shop_details_menu_image);
        number = (TextView) findViewById(R.id.shop_details_number);
        number.setPaintFlags(number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        numberlayout = (LinearLayout) findViewById(R.id.shop_details_num);
        linearLayout = (LinearLayout) findViewById(R.id.shop_details_directions);
        if (nam != null && detail != null && lat != null && lon != null && imageurl != null && menuurl != null && num != null) {
            name.setText(nam);
            details.setText(detail);
            image.setImageURI(Uri.parse(imageurl));
//            menu.setImageURI(Uri.parse(menuurl));
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shop").child("Shops").child(shopid).child("Gallery");
        mDatabaseMenu = FirebaseDatabase.getInstance().getReference().child("Shop").child("Shops").child(shopid).child("Menu");

        mDatabase.keepSynced(true);
        mDatabaseMenu.keepSynced(true);


    }


    @Override
    protected void onRestart() {
        super.onRestart();

        menuImages.removeAll(menuImages);
        galleryImages.removeAll(galleryImages);
    }

    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<GalleryFormat, GalleryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GalleryFormat, GalleryViewHolder>(
                GalleryFormat.class,
                R.layout.gallery_row,
                GalleryViewHolder.class,
                mDatabase
        ) {


            @Override
            protected void populateViewHolder(final GalleryViewHolder viewHolder, GalleryFormat model, int position) {

                galleryImages.add(model.getImage());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ShopDetails.this, GalleryActivity.class);
                        intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, galleryImages);
                        startActivity(intent);
                    }
                });
            }

        };

        galleryRecycler.setAdapter(firebaseRecyclerAdapter);

        FirebaseRecyclerAdapter<GalleryFormat, GalleryViewHolder> firebaseRecyclerAdapterMenu = new FirebaseRecyclerAdapter<GalleryFormat, GalleryViewHolder>(
                GalleryFormat.class,
                R.layout.gallery_row,
                GalleryViewHolder.class,
                mDatabaseMenu
        ) {

            @Override
            protected void populateViewHolder(final GalleryViewHolder viewHolder, GalleryFormat model, int position) {

                menuImages.add(model.getImage());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ShopDetails.this, GalleryActivity.class);
                        intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, menuImages);
                        startActivity(intent);
                    }
                });
            }

        };

        menuRecycler.setAdapter(firebaseRecyclerAdapterMenu);


    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public GalleryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(Context ctx, String ImageUrl) {

            ImageView imageHolder = (ImageView) mView.findViewById(R.id.galleryImage);
            Picasso.with(ctx).load(ImageUrl).into(imageHolder);

        }

    }
}

