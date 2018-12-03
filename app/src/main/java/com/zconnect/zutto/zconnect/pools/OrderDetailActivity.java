package com.zconnect.zutto.zconnect.pools;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.models.ShopOrder;

import net.glxn.qrgen.android.QRCode;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String TAG = "OrderDetailActivity";

    private ImageView qr_image;
    private String communityID,userUID;
    private ShopOrder order;

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
                    communityID = "testCollege";

                    //activity main block with all valid parameters

                    attachID();
                    //setPoolInfo();
                    //loadItemView();
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

    private void attachID() {
        qr_image = findViewById(R.id.qr_image);
        Bitmap myBitmap = QRCode.from("www.example.org").bitmap();
        qr_image.setImageBitmap(myBitmap);

    }
}
