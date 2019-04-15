package com.zconnect.zutto.zconnect.pools;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.LoginActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemDetailAdapter;
import com.zconnect.zutto.zconnect.pools.models.DiscountOffer;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.pools.models.PoolInfo;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

public class UpcomingPoolDetailsActivity extends BaseActivity {

    public static final String TAG = "UpPoolDetailsActivity";

    private RecyclerView recyclerView;
    private TextView offers, orderDeadlineTime, deliveryTime, poolExtraDesc;
    private LinearLayout ll_progressBar;
    private TextView loading_text;
    private Button btn_activate;

    private PoolItemDetailAdapter adapter;
    private ValueEventListener poolItemListener, poolOfferListener;

    private Pool pool;
    private String userUID;
    private int discount_percentage, max_amount, min_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_item_detail);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("pool")) {

                pool = (Pool) getIntent().getSerializableExtra("pool");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    userUID = user.getUid();

                    //activity main block with all valid parameters
                    setToolbar();
                    attachID();

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
//                        getWindow().setStatusBarColor(colorDarkPrimary);
//                        getWindow().setNavigationBarColor(colorPrimary);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    }

                    setPoolInfo();
                    loadItemView();
                    setAdminView();

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

    private void setAdminView() {
        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForActivePool();
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("communities/" + communityReference + "/Users1/" + userUID + "/userType");
        Log.d(TAG, "setAdminView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                if (type == null) {
                    btn_activate.setVisibility(View.GONE);
                    btn_activate.setEnabled(false);
                } else if (type.compareToIgnoreCase("admin") == 0) {
                    btn_activate.setVisibility(View.VISIBLE);
                    btn_activate.setEnabled(true);
                }else {
                    btn_activate.setVisibility(View.GONE);
                    btn_activate.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void showDialogForActivePool() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are You Sure ?");
        builder.setMessage("This action can not be undone\nbe sure before activating " + pool.getPoolInfo().getName());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activatePool();
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void activatePool() {
        setProgressBarView(View.VISIBLE, "activating pool");
        btn_activate.setEnabled(false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(Pool.URL_POOL, communityReference)).child(pool.getPoolPushID());
        ref.child(Pool.STATUS).setValue(Pool.STATUS_ACTIVE).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Failed to change the state.");
            }
        });
    }

    private void loadItemView() {
        setProgressBarView(View.VISIBLE, "Loading list\nplease wait..");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolItem.URL_POOL_ITEM, pool.getPoolInfo().getShopID(), pool.getPoolInfo().getPoolID()));
        Log.d(TAG, "loadItemView : ref " + ref.toString());
        ref.addValueEventListener(poolItemListener);
    }

    private void setPoolInfo() {

        toolbar.setTitle(pool.getPoolInfo().getName());
        TimeUtilities timeUtilities = new TimeUtilities(pool.getTimestampOrderReceivingDeadline());
        String text = "2. Orders will be taken till " + timeUtilities.getTimeInHHMMAPM() + ", " + timeUtilities.getMonthName("SHORT") + " " + timeUtilities.getDateTime().getDayOfMonth() + " " + timeUtilities.getDateTime().getYearOfEra();
        orderDeadlineTime.setText(text);
        timeUtilities = new TimeUtilities(pool.getDeliveryTime());
        text = "3. Orders will be delivered on " + timeUtilities.getMonthName("SHORT") + " " + timeUtilities.getDateTime().getDayOfMonth() + " " + timeUtilities.getDateTime().getYearOfEra() + ", " + timeUtilities.getTimeInHHMMAPM();
        deliveryTime.setText(text);
        poolExtraDesc.setText(pool.getPoolInfo().getExtras());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolInfo.URL_POOL_OFFER,pool.getPoolInfo().getShopID(), pool.getPoolInfo().getPoolID()));
        Log.d(TAG, "setPoolView : ref " + ref.toString());
        ref.addValueEventListener(poolOfferListener);

    }

    private void attachID() {

        recyclerView = findViewById(R.id.pool_item_rv);
        offers = findViewById(R.id.pool_offers);
        orderDeadlineTime = findViewById(R.id.order_deadline_time);
        deliveryTime = findViewById(R.id.order_delivery_time);
        poolExtraDesc = findViewById(R.id.pool_extra_desc);
        ll_progressBar = findViewById(R.id.ll_progressBar);
        loading_text = findViewById(R.id.loading_text);
        btn_activate = findViewById(R.id.btn_activate);

        //setup adapter
        adapter = new PoolItemDetailAdapter();
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

                    DiscountOffer discountOffer = dataSnapshot.getValue(DiscountOffer.class);


                    discount_percentage = discountOffer.getDiscountPercentage();
                    max_amount = discountOffer.getMaxDiscount();
                    min_item = discountOffer.getMinQuantity();
                    // if(disPer != 0 && maxDiscount != 0 && minQuantity !=0)
                    offers.setVisibility(View.VISIBLE);
                    offers.setText(String.format("1. OFFER: %d%% OFF upto " + getApplicationContext().getString(R.string.Rs) + "%d on a minimum order of %d items.", discount_percentage, max_amount, min_item));
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
