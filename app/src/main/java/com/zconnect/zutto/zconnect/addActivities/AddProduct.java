package com.zconnect.zutto.zconnect.addActivities;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.CustomSpinner;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.commonModules.NumberNotificationForFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.FeatureDBName;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mabbas007.tagsedittext.TagsEditText;

import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_STOREROOM;

public class AddProduct extends BaseActivity implements TagsEditText.TagsEditListener{
    private static final int GALLERY_REQUEST = 7;
    IntentHandle intentHandle;
    Intent galleryIntent;
    DatabaseReference mFeaturesStats;
    private Uri mImageUri = null;
    private ImageButton mAddImage;
    private Button mPostBtn;
    String key;
    private MaterialEditText mProductName;
    private MaterialEditText mProductDescription;
    private MaterialEditText mProductPrice;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsername;
    private DatabaseReference mPostedByDetails;
    private ProgressDialog mProgress;
    private CustomSpinner spinner1;
    private FirebaseAuth mAuth;
    private String sellerName;
    private CheckBox negotiableCheckBox;
    private Long postTimeMillis;
    private boolean isAsk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAsk = getIntent().getStringExtra("type").equals("ASK");
        if(isAsk)
            setContentView(R.layout.activity_add_product_ask);
        else
            setContentView(R.layout.activity_add_product);
        setToolbar();
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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        mAddImage = (ImageButton) findViewById(R.id.imageButton);
        mProductName = (MaterialEditText) findViewById(R.id.name);
        mProductDescription = (MaterialEditText) findViewById(R.id.description);
        mProductPrice = (MaterialEditText) findViewById(R.id.price);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("storeroom").child("products");
        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        spinner1 = (CustomSpinner) findViewById(R.id.categories);
        negotiableCheckBox=(CheckBox) findViewById(R.id.priceNegotiable);
        spinner1.setSelection(7);
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
            Uri imageUri = intentHandle.getPickImageResultUri(data, AddProduct.this); //Get data
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
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
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
        if(!String.valueOf(spinner1.getSelectedItem()).equals("Choose Category")) {
            mProgress.setMessage("Posting Product..");
            mProgress.show();
        }
        final String productNameValue = mProductName.getText().toString().trim();
        final String productDescriptionValue = mProductDescription.getText().toString().trim();
        final String productPriceValue = mProductPrice.getText().toString().trim();
        final Boolean negotiable;

        if(negotiableCheckBox.isChecked()) {
            negotiable = true;
        } else {
            negotiable = false;
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        final String userId = user.getUid();
        mUsername = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
        mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Stats");
        final String category = spinner1.getSelectedItem().toString();
        boolean _noFieldsEmpty = !TextUtils.isEmpty(productNameValue) && !TextUtils.isEmpty(productDescriptionValue) && category != null;
        final boolean noFieldsEmpty = isAsk ? _noFieldsEmpty : _noFieldsEmpty && mImageUri != null && (!TextUtils.isEmpty(productPriceValue));

        final Map<String, Object> taskMap = new HashMap<String, Object>();
        final DatabaseReference _newPost = mDatabase.push();
//        DatabaseReference _postedBy = _newPost.child("PostedBy");
        key = _newPost.getKey();
        postTimeMillis = System.currentTimeMillis();
        taskMap.put("Category", String.valueOf(spinner1.getSelectedItem()));
        taskMap.put("Key", key);
        taskMap.put("ProductName", productNameValue);
        taskMap.put("ProductDescription", productDescriptionValue);
        taskMap.put("PostedBy", mAuth.getCurrentUser().getUid());
        taskMap.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        taskMap.put("PostTimeMillis", postTimeMillis);

        final DatabaseReference _newPost2 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(_newPost.getKey());
        final Map<String, Object> taskMapHome = new HashMap<String, Object>();
        taskMapHome.put("name", productNameValue);
        taskMapHome.put("desc", productDescriptionValue);
        taskMapHome.put("feature", "StoreRoom");
        taskMapHome.put("id", key);
        taskMapHome.put("Key", _newPost2.getKey());
        taskMapHome.put("PostTimeMillis", postTimeMillis);

        final Map<String, Object> postedByMap = new HashMap<String, Object>();
        postedByMap.put("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                postedByMap.put("Username", user.getUsername());
                postedByMap.put("ImageThumb", user.getImageURLThumbnail());

                taskMap.put("Phone_no", user.getMobileNumber());
                taskMap.put("SellerUsername", user.getUsername());

//                postedBy.child("Username").setValue(user.getUsername());
//                postedBy.child("ImageThumb").setValue(user.getImageURLThumbnail());
//                newPost.child("Phone_no").setValue(user.getMobileNumber());
//                newPost.child("SellerUsername").setValue(user.getUsername());

                taskMap.put("PostedBy", postedByMap);
                taskMapHome.put("PostedBy", postedByMap);
                if(isAsk)
                {
                    taskMap.put("type", "ASK");

                    taskMapHome.put("productType", "ASK");
                }
                else
                {
                    taskMap.put("type", "ADD");
                    taskMap.put("Price", productPriceValue);
                    taskMap.put("isNegotiable", negotiable);

                    taskMapHome.put("productType", "ADD");
                    taskMapHome.put("desc2", productPriceValue);
                    taskMapHome.put("productPrice", productPriceValue);
                }
                if (noFieldsEmpty) {
                    Log.i("EEEEEEEEEEEEE", "entered");
                    if(String.valueOf(spinner1.getSelectedItem()).equals("Choose Category"))
                    {
                        Toast.makeText(AddProduct.this, "Please select a category for the product", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (mImageUri != null) {
                            final StorageReference filepath = mStorage.child("ProductImage").child((mImageUri.getLastPathSegment()) + mAuth.getCurrentUser().getUid());
                            UploadTask uploadTask = filepath.putFile(mImageUri);
                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return filepath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        final Uri downloadUri = task.getResult();
//                        final DatabaseReference newPost = mDatabase.push();
//                        final DatabaseReference postedBy = newPost.child("PostedBy");
//                        key = newPost.getKey();
//                        postTimeMillis = System.currentTimeMillis();
//                        newPost.child("Category").setValue(String.valueOf(spinner1.getSelectedItem()));
//                        newPost.child("Key").setValue(key);
//                        newPost.child("ProductName").setValue(productNameValue);
//                        newPost.child("ProductDescription").setValue(productDescriptionValue);
//                        newPost.child("Image").setValue(downloadUri != null ? downloadUri.toString() : null);
//                        newPost.child("PostedBy").setValue(mAuth.getCurrentUser().getUid());
//                        newPost.child("userID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                        newPost.child("PostTimeMillis").setValue(postTimeMillis);
                                        taskMap.put("Image", downloadUri != null ? downloadUri.toString() : null);
                                        taskMapHome.put("imageurl", downloadUri != null ? downloadUri.toString() : null);
//                        postedBy.setValue(null);
//                        postedBy.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
//                                postedBy.child("Username").setValue(user.getUsername());
//                                postedBy.child("ImageThumb").setValue(user.getImageURLThumbnail());
//                                newPost.child("Phone_no").setValue(user.getMobileNumber());
//                                newPost.child("SellerUsername").setValue(user.getUsername());
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });


//                        DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(newPost.getKey());
//                        final DatabaseReference newPost2Postedby = newPost2.child("PostedBy");
//                        newPost2.child("name").setValue(productNameValue);
//                        newPost2.child("desc").setValue(productDescriptionValue);
//                        newPost2.child("imageurl").setValue(downloadUri != null ? downloadUri.toString() : null);
//                        newPost2.child("feature").setValue("StoreRoom");
//                        newPost2.child("id").setValue(key);
//                        newPost2.child("Key").setValue(newPost2.getKey());
//                        newPost2.child("PostTimeMillis").setValue(postTimeMillis);

//                        newPost2Postedby.setValue(null);
//                        newPost2Postedby.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        _newPost.updateChildren(taskMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                if (databaseError != null)
                                                    Log.i("WWWWWW Error upd", databaseError.getMessage());
                                                else
                                                    Log.i("WWWWWW No Error", "Yahoo!");
                                            }
                                        });
                                        _newPost2.updateChildren(taskMapHome, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                if (databaseError != null)
                                                    Log.i("WWWWWW home Error upd", databaseError.getMessage());
                                                else
                                                    Log.i("WWWWWW home No Error", "Yahoo!");
                                            }
                                        });
//                            _newPost.setValue(taskMap);
//                            _newPost2.setValue(taskMapHome);
                                        ////writing uid of product to homePosts node in Users1.uid for handling data conistency
                                        mPostedByDetails.child("homePosts").child(key).setValue(true);

                                        handleNotifAndCountAndStats(productNameValue, productPriceValue, downloadUri);
                                        GlobalFunctions.addPoints(10);
                                        mProgress.dismiss();
                                        finish();
                                    } else {
                                        // Handle failures
                                        // ...
                                        Snackbar snackbar = Snackbar.make(mProductName, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                        snackbar.show();
                                    }
                                }
                            });
                        } else {
                            _newPost.setValue(taskMap);
                            _newPost2.setValue(taskMapHome);
                            ////writing uid of product to homePosts node in Users1.uid for handling data conistency
                            mPostedByDetails.child("homePosts").child(key).setValue(true);

                            handleNotifAndCountAndStats(productNameValue, productPriceValue, null);
                            GlobalFunctions.addPoints(10);
                            mProgress.dismiss();
                            finish();
                        }
                    }
                } else {
                    Snackbar snack = Snackbar.make(mProductDescription, "Fields are empty. Can't Add Product.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    snack.show();
                    mProgress.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void handleNotifAndCountAndStats(final String productNameValue, final String productPriceValue, final Uri downloadUri) {
        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_STOREROOM);
        numberNotificationForFeatures.setCount();
        Log.d("NumberNoti setting for ", FeatureDBName.KEY_STOREROOM);

        CounterItemFormat counterItemFormat = new CounterItemFormat();
        HashMap<String, String> meta= new HashMap<>();

        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_PRODUCT_ADDED);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        counterItemFormat.setMeta(meta);

        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();

        FirebaseMessaging.getInstance().subscribeToTopic(key);

        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
//                                newPost2Postedby.child("Username").setValue(user.getUsername());
//                                newPost2Postedby.child("ImageThumb").setValue(user.getImageURLThumbnail());
                NotificationSender notificationSender = new NotificationSender(AddProduct.this, user.getUserUID());
                NotificationItemFormat addProductNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD,user.getUserUID());
                addProductNotification.setCommunityName(communityTitle);

                addProductNotification.setItemKey(key);
                if(downloadUri!=null)
                    addProductNotification.setItemImage(downloadUri.toString());
                addProductNotification.setItemName(productNameValue);
                if(!isAsk)
                    addProductNotification.setItemPrice(productPriceValue);

                addProductNotification.setUserName(user.getUsername());
                addProductNotification.setUserImage(user.getImageURLThumbnail());

                notificationSender.execute(addProductNotification);
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

