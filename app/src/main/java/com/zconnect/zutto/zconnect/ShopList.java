package com.zconnect.zutto.zconnect;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.ShopDetailsItem;
import com.zconnect.zutto.zconnect.ItemFormats.ShopListItem;

import java.util.Vector;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ShopList extends AppCompatActivity {
    ShopListRV adapter;
    String temp;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Shop").child("Shops");
    private Vector<ShopListItem> shopListItems = new Vector<>();
    private Vector<ShopDetailsItem> shopDetailsItems = new Vector<>();
    private RecyclerView recycleView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycleView = (RecyclerView) findViewById(R.id.content_shop_list_rv);
        progressBar = (ProgressBar) findViewById(R.id.content_shop_list_progress);
        setSupportActionBar(toolbar);
        temp = getIntent().getStringExtra("Category");
        if (temp != null) {
            getSupportActionBar().setTitle(temp);

        }
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
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ShopListRV(this, shopListItems);
        recycleView.setAdapter(adapter);
        databaseReference.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.e("TAG","omg pro boy");
                progressBar.setVisibility(VISIBLE);
                shopListItems.clear();
                shopDetailsItems.clear();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    shopDetailsItems.add(shot.getValue(ShopDetailsItem.class));
                }
                for (int i = 0; i < shopDetailsItems.size(); i++) {
                    if (temp != null && shopDetailsItems.get(i).getCat().equals(temp))
                    shopListItems.add(new ShopListItem(shopDetailsItems.get(i).getImageurl(), shopDetailsItems.get(i).getName(), shopDetailsItems.get(i)));
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(INVISIBLE);
                Log.e("TAG", databaseError.getDetails());
            }
        });


    }
}
