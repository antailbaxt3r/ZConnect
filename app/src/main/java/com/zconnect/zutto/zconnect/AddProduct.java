package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddProduct extends BaseActivity {
    private static final int GALLERY_REQUEST = 7;
    IntentHandle intentHandle;
    Intent galleryIntent;
    DatabaseReference mFeaturesStats;
    private Uri mImageUri = null;
    private ImageButton mAddImage;
    private Button mPostBtn;
    private EditText mProductName;
    private EditText mProductDescription;
    private EditText mProductPrice;
    private EditText mProductPhone;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsername;
    private ProgressDialog mProgress;
    private CustomSpinner spinner1;
    private FirebaseAuth mAuth;
    private String sellerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intentHandle = new IntentHandle();
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

        mAddImage = (ImageButton) findViewById(R.id.imageButton);
        mProductName = (EditText) findViewById(R.id.name);
        mProductDescription = (EditText) findViewById(R.id.description);
        mProductPrice = (EditText) findViewById(R.id.price);
        mProductPhone = (EditText) findViewById(R.id.phoneNo);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("storeroom");
        spinner1 = (CustomSpinner) findViewById(R.id.categories);
        spinner1.setSelection(6);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        AddProduct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AddProduct.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                } else {
                    galleryIntent = intentHandle.getPickImageIntent(AddProduct.this);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        });

        //changing fonts
        Typeface ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        mProductName.setTypeface(ralewayRegular);
        mProductDescription.setTypeface(ralewayRegular);
        mProductPhone.setTypeface(ralewayRegular);
        mProductPrice.setTypeface(ralewayRegular);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data); //Get data
            CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {
                    mImageUri = result.getUri();
                    mAddImage.setImageURI(mImageUri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    Double ratio = ((double) bitmap.getWidth()) / bitmap.getHeight();

                    if (bitmap.getByteCount() > 350000) {

                        bitmap = Bitmap.createScaledBitmap(bitmap, 960, (int) (960 / ratio), false);
                    }
                    String path = MediaStore.Images.Media.insertImage(AddProduct.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    mAddImage.setImageURI(mImageUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if (!isNetworkAvailable(getApplicationContext())) {

                Snackbar snack = Snackbar.make(spinner1, "No Internet. Can't Add Product.", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();

            } else {
                startPosting();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    private void startPosting() {

        mProgress.setMessage("Posting Product..");
        mProgress.show();
        final String productNameValue = mProductName.getText().toString().trim();
        final String productDescriptionValue = mProductDescription.getText().toString().trim();
        final String productPriceValue = mProductPrice.getText().toString().trim();
        final String productPhoneNo = mProductPhone.getText().toString().trim();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        mUsername = FirebaseDatabase.getInstance().getReference().child("Users");
        mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("Stats");
        final String category = spinner1.getSelectedItem().toString();
        mUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sellerName = (String) dataSnapshot.child(userId).child("Username").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (!TextUtils.isEmpty(productNameValue) && !TextUtils.isEmpty(productDescriptionValue) && !TextUtils.isEmpty(productPriceValue) && !TextUtils.isEmpty(productPhoneNo) && mImageUri != null && category != null) {
            StorageReference filepath = mStorage.child("ProductImage").child((mImageUri.getLastPathSegment()) + mAuth.getCurrentUser().getUid());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mDatabase.push();
                    String key = newPost.getKey();
                    newPost.child("Category").setValue(String.valueOf(spinner1.getSelectedItem()));
                    newPost.child("Key").setValue(key);
                    newPost.child("ProductName").setValue(productNameValue);
                    newPost.child("ProductDescription").setValue(productDescriptionValue);
                    newPost.child("Image").setValue(downloadUri.toString());
                    newPost.child("PostedBy").setValue(mAuth.getCurrentUser().getUid());
                    newPost.child("Phone_no").setValue(productPhoneNo);
                    newPost.child("SellerUsername").setValue(sellerName);
                    newPost.child("Price").setValue(productPriceValue);


                    DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("home").push();
                    newPost2.child("name").setValue(productNameValue);
                    newPost2.child("desc").setValue(productDescriptionValue);
                    newPost2.child("imageurl").setValue(downloadUri.toString());
                    newPost2.child("feature").setValue("StoreRoom");
                    newPost2.child("id").setValue(key);
                    newPost2.child("desc2").setValue(productPriceValue);
                    newPost2.child("Key").setValue(newPost2.getKey());

                    // Adding stats

                    mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Integer TotalProducts = Integer.parseInt(dataSnapshot.child("TotalProducts").getValue().toString());
                            TotalProducts = TotalProducts + 1;
                            DatabaseReference newPost = mFeaturesStats;
                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("TotalProducts", TotalProducts);
                            newPost.updateChildren(taskMap);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    CounterManager.StoroomAddProduct(category);
                    mProgress.dismiss();
                    startActivity(new Intent(AddProduct.this, TabStoreRoom.class));
                    finish();
                }
            });
        } else {
            Snackbar snack = Snackbar.make(mProductDescription, "Fields are empty. Can't Add Product.", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
            snack.show();
            mProgress.dismiss();
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent storeIntent =new Intent(AddProduct.this,TabStoreRoom.class);
        startActivity(storeIntent);
        finish();
    }
}

