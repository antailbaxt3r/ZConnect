package com.zconnect.zutto.zconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OpenProductDetail extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private DatabaseReference mUsername;
    private TextView productName;
    private TextView productDescription;
    private TextView sellerName;
    private TextView sellerNumber;
    private ImageView productImage;
    private String temporaryVariable;
    private TextView price;
    private String value;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_product_detail);

        price = (TextView) findViewById(R.id.price);
        productName = (TextView) findViewById(R.id.name);
        productDescription = (TextView) findViewById(R.id.description);
        sellerName = (TextView) findViewById(R.id.sellerName);
        sellerNumber = (TextView) findViewById(R.id.sellerNumber);
        productImage = (ImageView) findViewById(R.id.image);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ZConnect");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        value = null;
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.getString("Flag") != null && extras.getString("Flag").equals("true")) {
            productName.setText(extras.getString("Name"));
            productDescription.setText(extras.getString("Description"));
            price.setText(extras.getString("Price"));
            sellerNumber.setText(extras.getString("Phone_no"));
            Picasso.with(OpenProductDetail.this).load(extras.getString("ImageUri")).into(productImage);
        } else {
            if (extras != null) {
                value = extras.getString("key").toString();
                //The key argument here must match that used in the other activity
//            Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
            }
            temporaryVariable = value;


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    final String userId = user.getUid();
                    String productNameString = (String) snapshot.child("storeroom").child(value).child("ProductName").getValue();  //prints "Do you have data? You'll love Firebase."
                    String imageUri = (String) snapshot.child("storeroom").child(value).child("Image").getValue();
                    String productDescriptionString = (String) snapshot.child("storeroom").child(value).child("ProductDescription").getValue();
                    String priceString = (String) snapshot.child("storeroom").child(value).child("Price").getValue();
                    String sellerNumberString = (String) snapshot.child("storeroom").child(value).child("Phone_no").getValue();

                    Picasso.with(OpenProductDetail.this).load(imageUri).into(productImage);
                    productName.setText(productNameString);
                    productDescription.setText(productDescriptionString);
                    price.setText(priceString);
                    sellerNumber.setText(sellerNumberString);


                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sellerNameString = (String) dataSnapshot.child("Users").child(userId).child("Username").getValue();
                sellerName.setText(sellerNameString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
