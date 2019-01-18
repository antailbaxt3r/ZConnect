package com.zconnect.zutto.zconnect.pools;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.pools.adapters.PoolAddItemAdapter;
import com.zconnect.zutto.zconnect.pools.models.ActivePool;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.pools.models.PoolDish;
import com.zconnect.zutto.zconnect.pools.models.PoolInfo;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPoolItemActivity extends BaseActivity {

    public static final String TAG = "PoolDetailsActivity";

    private Button btn_payment;
    private RecyclerView recyclerView;
    private TextView offers, description, joined_peoples;
    private LinearLayout ll_progressBar;
    private TextView loading_text;

    private PoolAddItemAdapter adapter;
    private ValueEventListener poolItemListener, poolOfferListener;

    private Pool pool;
    private String userUID;
    private int discount_percentage, max_amount, min_item;
    private Boolean offer_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pool_item);
        //base case
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("newPool")) {

                pool = Pool.getPool(b.getBundle("newPool"));
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    //TODO start login acitvity
                    finish();
                } else {
                    userUID = user.getUid();
                    //TODO set proper data from the preference
                    setToolbar();

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
                        getWindow().setStatusBarColor(colorDarkPrimary);
                        getWindow().setNavigationBarColor(colorPrimary);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    }

                    attachID();
                    setPoolInfo();
                    loadItemView();

                    btn_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //paymentSuccess();
                            openBillView();
                        }
                    });
                }


            } else {
                Log.d(TAG, "onCreate : bundle does not contain newPool key finishing activity");
                finish();
            }
        } else {
            Log.d(TAG, "onCreate : null bundle finishing activity");
            finish();
        }

    }

    private void openBillView() {
        Intent intent = new Intent(this, PoolBillActivity.class);
        ArrayList<PoolItem> items = adapter.getPoolsList();
        HashMap<String, Integer> mp = adapter.getMp();

        int n = mp.size();
        if (n == 0) {
            //TODO prompt user to order something
        } else {
            Log.d(TAG, "openBillView : start with n = " + String.valueOf(n));
            String[] ids = new String[n];
            String[] imageURL = new String[n];
            String[] name = new String[n];
            int[] quantities = new int[n];
            int[] prices = new int[n];
            int i = 0;
            for (String key : mp.keySet()) {
                ids[i] = key;
                quantities[i] = mp.get(key);
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).getID().compareTo(key) == 0) {
                        name[i] = items.get(j).getName();
                        imageURL[i] = items.get(j).getImageURL();
                        prices[i] = items.get(j).getPrice();
                        break;
                    }
                }

                i++;
            }
            Bundle b = new Bundle();
            b.putStringArray(PoolItem.ITEM_ID, ids);
            b.putStringArray(PoolItem.IMAGE_URL, imageURL);
            b.putStringArray(PoolItem.NAME, name);
            b.putIntArray(PoolItem.QUANTITY, quantities);
            b.putIntArray(PoolItem.PRICE, prices);
            intent.putExtra("orderList", b);
            if (offer_flag) {
                b.putInt(PoolInfo.DISCOUNT_PERCENTAGE, discount_percentage);
                b.putInt(PoolInfo.MAX_DISCOUNT, max_amount);
                b.putInt(PoolInfo.MIN_QUANTITY, min_item);
            } else {
                //prompt user to wait as offer is not loaded
            }
            b.putString(Pool.SHOP_ID, pool.getShopID());
            b.putString(Pool.POOL_ID, pool.getPoolID());
            b.putString(Pool.POOL_PUSH_ID, pool.getID());
            b.putString("poolName", pool.getName());
            startActivity(intent);
        }


    }

    private void paymentSuccess() {
        //load order data
        setProgressBarView(View.VISIBLE, "Saving order info\n DO NOT press back button.");
       /* ref.updateChildren(adapter.getMp()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                joinForums();
                updateJoinedCount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO on fail to save user data
            }
        });*/


    }

    private void updateJoinedCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(ActivePool.URL_ACTIVE_POOL,
                communityReference)).child(pool.getID()).child(ActivePool.JOINED);
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                int p;
                try {
                    p = Integer.parseInt(mutableData.getValue(String.class));
                } catch (NumberFormatException e) {
                    p = 0;
                }
                p++;
                mutableData.setValue(String.valueOf(p));
                return Transaction.success(mutableData);


            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });

    }

    private void joinForums() {
        setProgressBarView(View.VISIBLE, "Joining forums");
        setProgressBarView(View.GONE, "Joined");

    }

    private void loadItemView() {
        setProgressBarView(View.VISIBLE, "Loading list\nplease wait..");
        setProgressBarView(View.VISIBLE, "Loading list\nplease wait..");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolItem.URL_POOL_ITEM, pool.getShopID(), pool.getPoolID()));
        Log.d(TAG, "loadItemView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(poolItemListener);
    }

    private void setPoolInfo() {
        toolbar.setTitle(pool.getName());
        description.setText(pool.getDescription());
        joined_peoples.setText("Ordered : " + String.valueOf(pool.getTotalOrder()));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolInfo.URL_POOL_OFFER, pool.getShopID(), pool.getPoolID()));
        Log.d(TAG, "setPoolView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(poolOfferListener);
    }

    private void attachID() {
        btn_payment = findViewById(R.id.payment_btn);
        recyclerView = findViewById(R.id.pool_item_rv);
        offers = findViewById(R.id.pool_offers);
        description = findViewById(R.id.pool_description);
        joined_peoples = findViewById(R.id.joined_peoples);
        ll_progressBar = findViewById(R.id.ll_progressBar);
        loading_text = findViewById(R.id.loading_text);

        //setup adapter
        adapter = new PoolAddItemAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        defineListener();
    }

    private void defineListener() {
        poolItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clearDataset();
                for (DataSnapshot items : dataSnapshot.getChildren()) {
                    PoolItem dish = items.getValue(PoolItem.class);
                    dish.setID(items.getKey());
                    adapter.insertAtEnd(dish);
                }
                setProgressBarView(View.GONE, "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on cancel
                setProgressBarView(View.GONE, "");
            }
        };
        poolOfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    offer_flag = true;
                    discount_percentage = dataSnapshot.child(PoolInfo.DISCOUNT_PERCENTAGE).getValue(Integer.class);
                    max_amount = dataSnapshot.child(PoolInfo.MAX_DISCOUNT).getValue(Integer.class);
                    min_item = dataSnapshot.child(PoolInfo.MIN_QUANTITY).getValue(Integer.class);
                    // if(disPer != 0 && maxDiscount != 0 && minQuantity !=0)
                    offers.setVisibility(View.VISIBLE);
                    offers.setText(String.format("Discount Percentage : %d\nMax Discount %d\nMin Quantity : %d", discount_percentage, max_amount, min_item));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void setProgressBarView(int visibility, String message) {
        ll_progressBar.setVisibility(visibility);
        loading_text.setText(message);

    }
}
