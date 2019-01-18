package com.zconnect.zutto.zconnect.pools;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.pools.adapters.PoolItemCartAdapter;
import com.zconnect.zutto.zconnect.pools.models.PoolItem;
import com.zconnect.zutto.zconnect.pools.models.ShopOrder;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetailActivity extends BaseActivity {

    public static final String TAG = "OrderDetailActivity";

    private ImageView qr_image;
    private String userUID;
    private ShopOrder order;
    private RecyclerView recyclerView;
    private PoolItemCartAdapter adapter;
    private ValueEventListener poolItemListener,orderItemListener;
    private ArrayList<PoolItem> poolItems = new ArrayList<>();
    private TextView orderStatus,userName,userEmail,userAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Bundle b = getIntent().getExtras();

        if (b != null) {
            if (b.containsKey("order")) {

                order = ShopOrder.getShopOrder(b.getBundle("order"));
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    //TODO start login acitvity
                    finish();
                } else {
                    userUID = user.getUid();
                    //TODO set proper data from the preference

                    setToolbar();
                    //activity main block with all valid parameters
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
                    setOrderQRView();
                    loadItemView();
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

    private void loadItemView() {
        //setProgressBarView(View.VISIBLE, "Loading list\nplease wait..");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(PoolItem.URL_POOL_ITEM, order.getShopID(), order.getPoolID()));
        Log.d(TAG, "loadItemView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(poolItemListener);
    }

    private void setOrderQRView() {

        Bitmap myBitmap = QRCode.from(order.getRazorPayID()+"-"+userUID).bitmap();
        qr_image.setImageBitmap(myBitmap);

        orderStatus.setText(order.getOrderStatus());
        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        userAmount.setText(String.format("Amount : %s%d",getResources().getString(R.string.Rs),order.getAmount()));
    }

    private void attachID() {
        toolbar.setTitle(order.getPoolName());
        qr_image = findViewById(R.id.qr_image);
        orderStatus = findViewById(R.id.order_status);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userAmount = findViewById(R.id.user_amount);
        recyclerView = findViewById(R.id.recycleView);

        adapter = new PoolItemCartAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        defineListener();
    }

    private void defineListener() {
        poolItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                poolItems.clear();
                for (DataSnapshot items : dataSnapshot.getChildren()) {
                    PoolItem dish = items.getValue(PoolItem.class);
                    dish.setID(items.getKey());
                    poolItems.add(dish);
                }
                loadOrderItemList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on cancel
            }
        };
        orderItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot != null){
                  for(DataSnapshot item : dataSnapshot.getChildren()){
                      PoolItem orderItem =null;
                      for(int i = 0 ; i < poolItems.size();i++){
                          if(poolItems.get(i).getID().compareTo(item.getKey())==0){
                              orderItem = poolItems.get(i);
                              orderItem.setQuantity(item.getValue(Integer.class));
                              break;
                          }
                      }
                      if(orderItem != null){
                          adapter.insertAtEnd(orderItem);
                      }
                  }

               } else {
                   //TODO no such order
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void loadOrderItemList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(ShopOrder.URL_ORDER_ITEM_LIST,
                communityReference, order.getShopID(),order.getPoolPushID() ,order.getRazorPayID()));
        Log.d(TAG, "loadItemView : ref " + ref.toString());
        ref.addListenerForSingleValueEvent(orderItemListener);
    }
}
