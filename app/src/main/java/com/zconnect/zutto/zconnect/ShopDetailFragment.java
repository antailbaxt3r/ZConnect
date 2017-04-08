package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ShopDetailFragment extends Fragment {

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
    public ShopDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_detail, container, false);
        galleryScroll = (HorizontalScrollView) view.findViewById(R.id.galleryScroll);
        menuScroll = (HorizontalScrollView) view.findViewById(R.id.menuScroll);
        galleryScroll.setHorizontalScrollBarEnabled(false);
        menuScroll.setHorizontalScrollBarEnabled(false);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        galleryRecycler = (RecyclerView) view.findViewById(R.id.galleryRecycler);
        galleryRecycler.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManagerMenu = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        menuRecycler = (RecyclerView) view.findViewById(R.id.menuRecycler);
        menuRecycler.setLayoutManager(layoutManagerMenu);


        nam = getActivity().getIntent().getStringExtra("Name");
        detail = getActivity().getIntent().getStringExtra("Details");
        lat = getActivity().getIntent().getStringExtra("Lat");
        lon = getActivity().getIntent().getStringExtra("Lon");
        imageurl = getActivity().getIntent().getStringExtra("Imageurl");
        menuurl = getActivity().getIntent().getStringExtra("Menu");
        num = getActivity().getIntent().getStringExtra("Number");
        shopid = getActivity().getIntent().getStringExtra("ShopId");


        name = (TextView) view.findViewById(R.id.shop_details_name);
        details = (TextView) view.findViewById(R.id.shop_details_details);
        image = (SimpleDraweeView) view.findViewById(R.id.shop_details_image);
//      Menu = (SimpleDraweeView) findViewById(R.id.shop_details_menu_image);
        number = (TextView) view.findViewById(R.id.shop_details_number);
        number.setPaintFlags(number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        numberlayout = (LinearLayout) view.findViewById(R.id.shop_details_num);
        linearLayout = (LinearLayout) view.findViewById(R.id.shop_details_directions);
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
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        menuImages.clear();
        galleryImages.clear();


        FirebaseRecyclerAdapter<GalleryFormat, ShopDetailFragment.GalleryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GalleryFormat, ShopDetailFragment.GalleryViewHolder>(
                GalleryFormat.class,
                R.layout.gallery_row,
                ShopDetailFragment.GalleryViewHolder.class,
                mDatabase
        ) {


            @Override
            protected void populateViewHolder(final ShopDetailFragment.GalleryViewHolder viewHolder, GalleryFormat model, int position) {

                galleryImages.add(model.getImage());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), GalleryActivity.class);
                        intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, galleryImages);
                        startActivity(intent);
                    }
                });
            }

        };

        galleryRecycler.setAdapter(firebaseRecyclerAdapter);

        FirebaseRecyclerAdapter<GalleryFormat, ShopDetailFragment.GalleryViewHolder> firebaseRecyclerAdapterMenu = new FirebaseRecyclerAdapter<GalleryFormat, ShopDetailFragment.GalleryViewHolder>(
                GalleryFormat.class,
                R.layout.gallery_row,
                ShopDetailFragment.GalleryViewHolder.class,
                mDatabaseMenu
        ) {

            @Override
            protected void populateViewHolder(final ShopDetailFragment.GalleryViewHolder viewHolder, GalleryFormat model, int position) {

                menuImages.add(model.getImage());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), GalleryActivity.class);
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
