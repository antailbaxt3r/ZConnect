package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.Product;
import com.zconnect.zutto.zconnect.adapters.ProductsRVAdapter;

import java.util.Collections;
import java.util.Vector;

public class StoreroomIndividualCategory extends BaseActivity {

    public String category;
    Query queryCategory;
    private RecyclerView mProductList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private GridLayoutManager gridLayoutManager;
    private boolean flag = false;
    private ProductsRVAdapter adapter;
    private Product singleProduct;
    private Vector<Product> productVector = new Vector<Product>();
    private Boolean flagNoProductsAvailable;
    private TextView noProductsAvailableText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setToolbar();
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            category = intent.getStringExtra("Category");
            getSupportActionBar().setTitle(category);
        }


        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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

        noProductsAvailableText = (TextView) findViewById(R.id.no_products_available_text);
        mProductList = (RecyclerView) findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this,2);
        mProductList.setLayoutManager(gridLayoutManager);
        progressBar = (ProgressBar) findViewById(R.id.individual_categories_progress_bar);
        mAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.VISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");
        queryCategory = mDatabase.orderByChild("Category").equalTo(category);

        queryCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productVector.clear();;
                flagNoProductsAvailable = true;
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    try{
                        singleProduct = shot.getValue(Product.class);
                        if(!singleProduct.getKey().equals(null)&& !singleProduct.getProductName().equals(null)) {

                            if (!shot.hasChild("isNegotiable")){
                                if(shot.hasChild("negotiable")){
                                    if(shot.child("negotiable").getValue(Integer.class)==1){
                                        singleProduct.setIsNegotiable(Boolean.TRUE);
                                    }else {
                                        singleProduct.setIsNegotiable(Boolean.FALSE);
                                    }
                                }else {
                                    singleProduct.setIsNegotiable(Boolean.FALSE);
                                }
                            }
                            productVector.add(singleProduct);
                            flagNoProductsAvailable=false;
                        }
                    }
                    catch (Exception e){
                        Log.d("Error Alert", e.getMessage());
                    }


                }

                progressBar.setVisibility(View.INVISIBLE);
                if(flagNoProductsAvailable){
                    noProductsAvailableText.setVisibility(View.VISIBLE);
                }else {
                    noProductsAvailableText.setVisibility(View.GONE);
                }
                Collections.reverse(productVector);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        mDatabase.keepSynced(true);
        adapter = new ProductsRVAdapter(productVector,this,mDatabase);
        mProductList.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}


