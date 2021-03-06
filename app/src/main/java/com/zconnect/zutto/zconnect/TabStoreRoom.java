package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.ProductsRVAdapter;
import com.zconnect.zutto.zconnect.addActivities.AddProduct;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.Product;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ProductUtilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class TabStoreRoom extends BaseActivity implements PopupMenu.OnMenuItemClickListener {

    FirebaseUser user;
    FirebaseAuth mAuth;
//    private SectionsPagerAdapter mSectionsPagerAdapter;
//    private ViewPager mViewPager;

    PopupMenu mPopupMenu;


    //ProductsTab variables
    NotificationCompat.Builder mBuilder;
    String TotalProducts;
    String userId;

    Query productsQuery;

    DatabaseReference mUserStats, mFeaturesStats;

    private RecyclerView mProductList;

    private DatabaseReference mDatabase;
    private Query query;
    private boolean flag = false;

    private ProductsRVAdapter productAdapter;
    private Vector<Product> productVector= new Vector<Product>();
    private Vector<Product> searchProductVector;
    private ValueEventListener mListener;

    private Product singleProduct;
    private Boolean flagNoProductsAvailable;
    private TextView noProductsAvailableText;

    private FloatingActionButton fab;
    private ShimmerFrameLayout shimmerContainer;

    private int OPT_ELEC = 0, OPT_SPK = 1, OPT_STG = 2, OPT_ACAD = 3, OPT_ROOM = 4, OPT_FIC = 5, OPT_OTH = 6, OPT_ALL = 7;
    private int currentOptionCategory = 7;
    private String curretOptionCategoryString = ProductUtilities.CAT_ALL;
    private int OPT_ADD = 0, OPT_ASK = 1, OPT_BOTH = 2;
    private int currentOptionProductType = 2;


    //    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_store_room);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setToolbar();
        toolbar.setTitle("Storeroom");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

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

//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        // Set up the ViewPager with the sections joinedForumsAdapter.
//        mViewPager = (ViewPager) findViewById(R.id.view_pager);
//        mViewPager.setAdapter(mSectionsPagerAdapter);

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_app_bar_home);
//        tabLayout.setupWithViewPager(mViewPager);

        final CounterItemFormat counterItemFormat = new CounterItemFormat();
        final HashMap<String, String> meta= new HashMap<>();
        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_PRODUCTS_TAB_OPEN);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        counterItemFormat.setMeta(meta);

        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int pos = tab.getPosition();
//                if (pos == 0) {
//                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
//                    counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_PRODUCTS_TAB_OPEN);
//                    counterItemFormat.setTimestamp(System.currentTimeMillis());
//                    counterItemFormat.setMeta(meta);
//
//                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
//                    counterPush.pushValues();
//                } else if (pos == 1) {
//
//                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
//                    counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_CATEGORIES_TAB_OPEN);
//                    counterItemFormat.setTimestamp(System.currentTimeMillis());
//                    counterItemFormat.setMeta(meta);
//
//                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
//                    counterPush.pushValues();
//                }
//            }

//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        GridLayoutManager productGridLayout = new GridLayoutManager(this, 2);

        noProductsAvailableText = (TextView) findViewById(R.id.no_products_available_text);

        shimmerContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container1);
        mProductList = (RecyclerView) findViewById(R.id.productList);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(productGridLayout);

        fab = (FloatingActionButton) findViewById(R.id.fab_content_store_room);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNetworkAvailable(v.getContext())) {
                    Snackbar snack = Snackbar.make(fab, "No internet. Please try again later.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimaryDark));
                    snack.show();
                } else {
                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta = new HashMap<>();

                    meta.put("type", "fromFeature");
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_PRODUCT_ADD_OPEN);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();

                    Dialog addAskDialog = new Dialog(TabStoreRoom.this);
                    addAskDialog.setContentView(R.layout.new_dialog_box);
                    addAskDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    addAskDialog.findViewById(R.id.dialog_box_image_sdv).setBackground(ContextCompat.getDrawable(TabStoreRoom.this, R.drawable.ic_outline_store_36));
                    TextView heading = addAskDialog.findViewById(R.id.dialog_box_heading);
                    heading.setText("Sell/Ask");
                    TextView body = addAskDialog.findViewById(R.id.dialog_box_body);
                    body.setText("Do you want to sell a product or ask for a product?");
                    Button addButton = addAskDialog.findViewById(R.id.dialog_box_positive_button);
                    addButton.setText("Sell");
                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(TabStoreRoom.this, AddProduct.class);
                            intent.putExtra("type", ProductUtilities.TYPE_ADD_STR);
                            startActivity(intent);
                            addAskDialog.dismiss();

                        }
                    });
                    Button askButton = addAskDialog.findViewById(R.id.dialog_box_negative_button);
                    askButton.setText("Ask");
                    askButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(TabStoreRoom.this, AddProduct.class);
                            intent.putExtra("type", ProductUtilities.TYPE_ASK_STR);
                            startActivity(intent);
                            addAskDialog.dismiss();

                        }
                    });

                    addAskDialog.show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        shimmerContainer.startShimmerAnimation();

        SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        SharedPreferences communitySP;
        String communityReference;

        communitySP = getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        // StoreRoom feature Reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");
        productsQuery = mDatabase.orderByChild("PostTimeMillis");
        mDatabase.keepSynced(true);

        if(!status){
            user = mAuth.getCurrentUser();

            mUserStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(user.getUid()).child("Stats");
            mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Stats");

            mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TotalProducts = dataSnapshot.child("TotalProducts").getValue().toString();
                    DatabaseReference newPost = mUserStats;
                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put("TotalProducts", TotalProducts);
                    newPost.updateChildren(taskMap);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        productAdapter = new ProductsRVAdapter(productVector,this,mDatabase);
        mProductList.setAdapter(productAdapter);

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productVector.clear();;
                flagNoProductsAvailable = true;

                for (DataSnapshot shot: dataSnapshot.getChildren()){

                    try{

                        singleProduct = shot.getValue(Product.class);

                        if(singleProduct.getType()==null)
                        {
                            singleProduct.setType(ProductUtilities.TYPE_ADD_STR);
                        }

                        if(currentOptionProductType == OPT_ADD && !singleProduct.getType().equals(ProductUtilities.TYPE_ADD_STR))
                            continue;
                        else if(currentOptionProductType == OPT_ASK && !singleProduct.getType().equals(ProductUtilities.TYPE_ASK_STR))
                            continue;

                        if(!singleProduct.getKey().equals(null)&& !singleProduct.getProductName().equals(null))
                        {
                            if (!shot.hasChild("isNegotiable"))
                            {
                                if(shot.hasChild("negotiable"))
                                {
                                    if(shot.child("negotiable").getValue(String.class).equals("1"))
                                    {
                                        singleProduct.setIsNegotiable(Boolean.TRUE);
                                    }
                                    else
                                        {
                                        singleProduct.setIsNegotiable(Boolean.FALSE);
                                    }
                                }
                                else
                                    {
                                    singleProduct.setIsNegotiable(Boolean.FALSE);
                                }
                            }

                            productVector.add(singleProduct);
                            flagNoProductsAvailable = false;
                        }
                    }
                    catch (Exception e){
                        Log.d("Error Alert", e.getMessage());
                    }
                }



                Collections.sort(productVector, new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return Long.compare(o2.getPostTimeMillis(), o1.getPostTimeMillis());
                    }
                });

                productAdapter.notifyDataSetChanged();
                shimmerContainer.stopShimmerAnimation();
                shimmerContainer.setVisibility(View.INVISIBLE);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mBuilder = new NotificationCompat.Builder(this);

        setAdapter("lite",false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        productsQuery.addListenerForSingleValueEvent(mListener);
    }

    private void setAdapter(String queryString, boolean search) {

        if(search){
            if(!queryString.equals("")) {
                searchProductVector = new Vector<Product>();
                for (int i = 0; i < productVector.size(); i++) {

                    if (productVector.get(i).getProductName().toLowerCase().trim().contains(queryString.toLowerCase())) {
                        searchProductVector.add(productVector.get(i));
                    }
                    if (searchProductVector.size() > 7) {
                        break;
                    }
                }

                productAdapter = new ProductsRVAdapter(searchProductVector,this,mDatabase);
                mProductList.setAdapter(productAdapter);
                //progressBar.setVisibility(View.GONE);
                //mProductList.setVisibility(View.VISIBLE);
            }else {
                productAdapter = new ProductsRVAdapter(productVector,this,mDatabase);
                mProductList.setAdapter(productAdapter);
                //progressBar.setVisibility(View.GONE);
                //mProductList.setVisibility(View.VISIBLE);
            }

        }else {
            productsQuery.addListenerForSingleValueEvent(mListener);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status){
            getMenuInflater().inflate(R.menu.menu_storeroom, menu);

            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
            EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            ImageView searchClose = (ImageView) searchView.findViewById (android.support.v7.appcompat.R.id.search_close_btn);
            searchClose.setColorFilter (Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
            searchEditText.setTextColor(getResources().getColor(R.color.black));
            searchEditText.setHintTextColor(getResources().getColor(R.color.secondaryText));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    setAdapter(query,true);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    setAdapter(newText,true);
                    return false;
                }
            });


            MenuItem menuItem = menu.findItem(R.id.search);

            MenuItemCompat.setOnActionExpandListener(menuItem,new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {

                    /*final CounterItemFormat counterItemFormat = new CounterItemFormat();
                    final HashMap<String, String> meta= new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_PRODUCTS_TAB_OPEN);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();*/

                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    setAdapter("lite",false);
                    //Toast.makeText(InfoneContactListActivity.this, "Collapsed", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            return true;

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        CounterItemFormat counterItemFormat;
        CounterPush counterPush;
        HashMap<String, String> meta;
        switch (id) {
            case R.id.action_storeroom:
                counterItemFormat = new CounterItemFormat();
                meta= new HashMap<>();

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_MY_PRODUCTS_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();
                startActivity(new Intent(getApplicationContext(), MyProducts.class));
                return true;
            case R.id.action_storeroom_shortlist:
                counterItemFormat = new CounterItemFormat();
                meta= new HashMap<>();

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_SHORTLIST_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();
                startActivity(new Intent(getApplicationContext(), Shortlist.class));
                return true;
            case R.id.action_filter_products:
                mPopupMenu = new PopupMenu(this, findViewById(R.id.action_filter_products));
                mPopupMenu.inflate(R.menu.menu_storeroom_filter);
                mPopupMenu.setOnMenuItemClickListener(this);
                MenuItem menuItem = null;
                switch (currentOptionCategory) {
                    case 0:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_electronics);
                        break;
                    case 1:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_speakers_headphones);
                        break;
                    case 2:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_storage_devices);
                        break;
                    case 3:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_academics);
                        break;
                    case 4:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_room_necessities);
                        break;
                    case 5:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_fiction);
                        break;
                    case 6:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_others);
                        break;
                    case 7:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_all_products);
                        break;
                    default:
                        break;
                }
                menuItem.setChecked(true);
                switch (currentOptionProductType) {
                    case 0:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_product_type_add);
                        break;
                    case 1:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_product_type_ask);
                        break;
                    case 2:
                        menuItem = mPopupMenu.getMenu().findItem(R.id.option_product_type_both);
                        break;
                }
                menuItem.setChecked(true);
                mPopupMenu.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        productsQuery.addValueEventListener(mListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        productsQuery.removeEventListener(mListener);
        shimmerContainer.stopShimmerAnimation();
        shimmerContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productsQuery.removeEventListener(mListener);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(!item.isChecked());
        switch (id) {
            case R.id.option_electronics:
                currentOptionCategory = 0;
                productsQuery = mDatabase.orderByChild("Category").equalTo(ProductUtilities.CAT_ELECTRONICS);
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_speakers_headphones:
                currentOptionCategory = 1;
                productsQuery = mDatabase.orderByChild("Category").equalTo(ProductUtilities.CAT_SPEAKERS_AND_HEADPHONES);
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_storage_devices:
                currentOptionCategory = 2;
                productsQuery = mDatabase.orderByChild("Category").equalTo(ProductUtilities.CAT_STORAGE_DEVICES);
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_academics:
                currentOptionCategory = 3;
                productsQuery = mDatabase.orderByChild("Category").equalTo(ProductUtilities.CAT_ACADEMIC_BOOKS);
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_room_necessities:
                currentOptionCategory = 4;
                productsQuery = mDatabase.orderByChild("Category").equalTo(ProductUtilities.CAT_ROOM_NECESSITIES);
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_fiction:
                currentOptionCategory = 5;
                productsQuery = mDatabase.orderByChild("Category").equalTo(ProductUtilities.CAT_FICTION);
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_others:
                currentOptionCategory = 6;
                productsQuery = mDatabase.orderByChild("Category").equalTo(ProductUtilities.CAT_OTHERS);
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_all_products:
                currentOptionCategory = 7;
                productsQuery = mDatabase.orderByPriority();
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_product_type_add:
                currentOptionProductType = 0;
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_product_type_ask:
                currentOptionProductType = 1;
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_product_type_both:
                currentOptionProductType = 2;
                productsQuery.addValueEventListener(mListener);
                productAdapter.notifyDataSetChanged();
                return true;
            default:
                return false;
        }
    }


//    public class SectionsPagerAdapter extends FragmentPagerAdapter {

//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }

//        @Override
//        public Fragment getItem(int position) {
//            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
//            Boolean status = sharedPref.getBoolean("mode", false);
//            if (!status){
//                switch (position) {
//                    case 0:
//                        ProductsTab productsTab = new ProductsTab();
//                        return productsTab;
//                    case 1:
//                        CategoriesTab categoriesTab = new CategoriesTab();
//                        return categoriesTab;
//                    default:
//                        return null;
//                }
//            }else {
//                switch (position) {
//                    case 0:
//                        ProductsTab productsTab = new ProductsTab();
//                        return productsTab;
//                    case 1:
//                        CategoriesTab categoriesTab = new CategoriesTab();
//                        return categoriesTab;
//                    default:
//                        return null;
//                }
//
//            }
//        }


//        @Override
//        public int getCount() {
//
//            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
//            Boolean status = sharedPref.getBoolean("mode", false);
//            if(!status){
//                return 2;
//            }else {
//                return 2;
//            }
//        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
//            Boolean status = sharedPref.getBoolean("mode", false);
//            if(!status) {
//                switch (position) {
//                    case 0:
//                        return "Products";
//                    case 1:
//                        return "Categories";
//                }
//            }else {
//                switch (position) {
//                    case 0:
//                        return "Products";
//                    case 1:
//                        return "Categories";
//                }
//            }
//            return null;
//        }
//    }
}