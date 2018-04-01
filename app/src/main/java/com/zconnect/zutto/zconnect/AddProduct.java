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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mabbas007.tagsedittext.TagsEditText;

import static com.zconnect.zutto.zconnect.KeyHelper.KEY_STOREROOM;

public class AddProduct extends BaseActivity implements TagsEditText.TagsEditListener{
    private static final int GALLERY_REQUEST = 7;
    IntentHandle intentHandle;
    Intent galleryIntent;
    DatabaseReference mFeaturesStats;
    private Uri mImageUri = null;
    private ImageButton mAddImage;
    private Button mPostBtn;
    String key;
    private EditText mProductName;
    private EditText mProductDescription;
    private EditText mProductPrice;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsername;
    private DatabaseReference mPostedByDetails;
    private ProgressDialog mProgress;
    private CustomSpinner spinner1;
    private FirebaseAuth mAuth;
    private String sellerName;
    private TagsEditText productTags;
    private CheckBox negotiableCheckBox;
    private Long postTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
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
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("storeroom");
        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        spinner1 = (CustomSpinner) findViewById(R.id.categories);
        productTags =(TagsEditText) findViewById(R.id.skillsTags);
        negotiableCheckBox=(CheckBox) findViewById(R.id.priceNegotiable);
        spinner1.setSelection(6);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        mProductPrice.setTypeface(ralewayRegular);
        productTags.setTypeface(ralewayRegular);


        productTags.setTagsListener(this);
        productTags.setTagsWithSpacesEnabled(true);

        productTags.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.productTags)));
        productTags.setThreshold(1);


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
                try {
                    throw error;
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        final String productTagString= productTags.getTags().toString().trim();
        final String negotiable;

        if(productPriceValue.equals("") && negotiableCheckBox.isChecked())
            negotiable="2";
        else
            if(negotiableCheckBox.isChecked())
                negotiable= "1";
            else
                negotiable="0";
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        final String userId = user.getUid();
        mUsername = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users");
        mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Stats");
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


        if (!TextUtils.isEmpty(productNameValue) && !TextUtils.isEmpty(productDescriptionValue) && (!TextUtils.isEmpty(productPriceValue) || negotiable.equals("2")) && mImageUri != null && category != null && productTags!=null && !negotiable.equals("") && negotiableCheckBox!=null) {
            StorageReference filepath = mStorage.child("ProductImage").child((mImageUri.getLastPathSegment()) + mAuth.getCurrentUser().getUid());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mDatabase.push();
                    final DatabaseReference postedBy = newPost.child("PostedBy");
                    key = newPost.getKey();
                    postTimeMillis = System.currentTimeMillis();
                    newPost.child("Category").setValue(String.valueOf(spinner1.getSelectedItem()));
                    newPost.child("Key").setValue(key);
                    newPost.child("ProductName").setValue(productNameValue);
                    newPost.child("ProductDescription").setValue(productDescriptionValue);
                    newPost.child("Image").setValue(downloadUri != null ? downloadUri.toString() : null);
                    newPost.child("PostedBy").setValue(mAuth.getCurrentUser().getUid());
                    newPost.child("SellerUsername").setValue(sellerName);
                    newPost.child("Price").setValue(productPriceValue);
                    newPost.child("skills").setValue(productTagString);
                    newPost.child("negotiable").setValue(negotiable);
                    newPost.child("PostTimeMillis").setValue(postTimeMillis);
                    postedBy.setValue(null);
                    postedBy.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                            postedBy.child("Username").setValue(user.getUsername());
                            postedBy.child("ImageThumb").setValue(user.getImageURLThumbnail());
                            newPost.child("Phone_no").setValue(user.getMobileNumber());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    FirebaseMessaging.getInstance().subscribeToTopic(key);


                    DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").push();
                    final DatabaseReference newPost2Postedby = newPost2.child("PostedBy");
                    newPost2.child("name").setValue(productNameValue);
                    newPost2.child("desc").setValue(productDescriptionValue);
                    newPost2.child("imageurl").setValue(downloadUri != null ? downloadUri.toString() : null);
                    newPost2.child("feature").setValue("StoreRoom");
                    newPost2.child("id").setValue(key);
                    newPost2.child("desc2").setValue(productPriceValue);
                    newPost2.child("Key").setValue(newPost2.getKey());
                    newPost2.child("productPrice").setValue(productPriceValue);
                    newPost2.child("PostTimeMillis").setValue(postTimeMillis);
                    newPost2Postedby.setValue(null);
                    newPost2Postedby.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                            newPost2Postedby.child("Username").setValue(user.getUsername());
                            newPost2Postedby.child("ImageThumb").setValue(user.getImageURLThumbnail());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    // Adding stats

                    mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Object o = dataSnapshot.child("TotalProducts").getValue();
                            if (o == null)
                                o = "0";
                            Integer TotalProducts = Integer.parseInt(o.toString());
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
                    finish();
                }
            });

            NotificationSender notificationSender=new NotificationSender(key,null,null,null,null,null,KEY_STOREROOM,true,false,getApplicationContext());
            notificationSender.execute();

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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //productTags.showDropDown();
        }
    }

    @Override
    public void onTagsChanged(Collection<String> tags) {

    }

    @Override
    public void onEditingFinished() {
        //Log.d(TAG,"OnEditing finished");
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(productTags.getWindowToken(), 0);
//        //productTags.clearFocus();
    }

}

