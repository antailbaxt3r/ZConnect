package com.zconnect.zutto.zconnect.addActivities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.commonModules.NumberNotificationForFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.FeatureDBName;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

import java.io.IOException;
import java.util.HashMap;

import static com.zconnect.zutto.zconnect.utilities.RequestCodes.GALLERY_REQUEST;

public class AddNotices extends BaseActivity {

    ImageButton mAddPhoto;
    TextView mName;
    String key;
    Intent galleryIntent;
    IntentHandle intentHandle;
    private StorageReference mStorage;
    private DatabaseReference mDatabase,mPostedByDetails;
    private ProgressDialog mProgress;
    Button submit;
    private Long postTimeMillis;
    private DatabaseReference mUsername;
    private FirebaseAuth mAuth;
    private Uri mImageUri=null, mImageUriSmall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        mProgress = new ProgressDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);

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

        mAddPhoto = (ImageButton)findViewById(R.id.imageButton);
        mName=(TextView)findViewById(R.id.name);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("notices").child("activeNotices");
        submit=(Button)findViewById(R.id.submit);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        intentHandle = new IntentHandle();

        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        AddNotices.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AddNotices.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                } else {
                    galleryIntent = intentHandle.getPickImageIntent(AddNotices.this);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable(getApplicationContext())){

                    Toast toast=Toast.makeText(getApplicationContext(), "No internet connection",Toast.LENGTH_LONG);
                    toast.show();
                }
                else{

                    startPosting();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data, AddNotices.this); //Get data
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
                    Bitmap bitmapSmall = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);

                    Double ratio = ((double) bitmap.getWidth()) / bitmap.getHeight();

                    if (bitmap.getByteCount() > 550000) {

                        bitmap = Bitmap.createScaledBitmap(bitmap, 1200, (int) (1200 / ratio), false);
                    }

                    if (bitmapSmall.getByteCount() > 150000) {

                        bitmapSmall = Bitmap.createScaledBitmap(bitmapSmall, 400, (int) (400 / ratio), false);
                    }

                    String path = MediaStore.Images.Media.insertImage(AddNotices.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);
                    String pathSmall =  MediaStore.Images.Media.insertImage(AddNotices.this.getContentResolver(), bitmapSmall, mImageUri.getLastPathSegment() + "small", null);

                    mImageUri = Uri.parse(path);
                    mImageUriSmall = Uri.parse(pathSmall);
                    mAddPhoto.setImageURI(mImageUri);

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
    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    private void startPosting() {

        mProgress.setMessage("Posting Notice..");
        mProgress.show();
        final String noticeNameValue = mName.getText().toString().trim();

        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        final String userId = user.getUid();
        mUsername = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");

        if ( !TextUtils.isEmpty(noticeNameValue) &&  mImageUri != null && mImageUriSmall !=null) {
            final StorageReference filepath = mStorage.child("NoticesImages").child((mImageUri.getLastPathSegment()) + mAuth.getCurrentUser().getUid());
            UploadTask uploadTask = filepath.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();
                        final DatabaseReference newPost = mDatabase.push();
                        final DatabaseReference postedBy = newPost.child("PostedBy");
                        key = newPost.getKey();
                        postTimeMillis = System.currentTimeMillis();
                        newPost.child("key").setValue(key);
                        newPost.child("title").setValue(noticeNameValue);
                        newPost.child("imageURL").setValue(downloadUri != null ? downloadUri.toString() : null);
                        newPost.child("postTimeMillis").setValue(postTimeMillis);
                        postedBy.setValue(null);
                        postedBy.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                                postedBy.child("Username").setValue(user.getUsername());
                                postedBy.child("ImageThumb").setValue(user.getImageURLThumbnail());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_NOTICES);
                        numberNotificationForFeatures.setCount();

                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta = new HashMap<>();

                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_ADD_NOTICES);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        counterItemFormat.setMeta(meta);

                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();

                        FirebaseMessaging.getInstance().subscribeToTopic(key);

                        // For Recents
                        DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(newPost.getKey());
                        final DatabaseReference newPost2Postedby = newPost2.child("PostedBy");
                        newPost2.child("name").setValue(noticeNameValue);
                        newPost2.child("imageurl").setValue(downloadUri != null ? downloadUri.toString() : null);
                        newPost2.child("feature").setValue("Notices");
                        newPost2.child("id").setValue(key);
                        newPost2.child("Key").setValue(newPost2.getKey());
                        newPost2.child("PostTimeMillis").setValue(postTimeMillis);

                        newPost2Postedby.setValue(null);
                        newPost2Postedby.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        ////writing uid of notice to homePosts node in Users1.uid for handling data conistency
                        mPostedByDetails.child("homePosts").child(key).setValue(true);

                        final StorageReference filepathSmall = mStorage.child("NoticesImages").child((mImageUriSmall.getLastPathSegment()) + mAuth.getCurrentUser().getUid());
                        UploadTask uploadTask = filepathSmall.putFile(mImageUriSmall);
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful())
                                {
                                    throw task.getException();
                                }
                                return filepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task2) {
                                if (task2.isSuccessful()) {
                                    final Uri downloadSmallUri = task2.getResult();
                                    newPost.child("imageThumbURL").setValue(downloadSmallUri != null ? downloadSmallUri.toString() : null);
                                    mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                                            newPost2Postedby.child("Username").setValue(user.getUsername());
                                            newPost2Postedby.child("ImageThumb").setValue(user.getImageURLThumbnail());

                                            //Notification
                                            NotificationSender notificationSender = new NotificationSender(AddNotices.this, user.getUserUID());
                                            NotificationItemFormat addNoticeNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_NOTICES_ADD, user.getUserUID());
                                            addNoticeNotification.setCommunityName(communityTitle);
                                            addNoticeNotification.setItemKey(key);
                                            addNoticeNotification.setItemImage(downloadSmallUri.toString());
                                            addNoticeNotification.setItemName(noticeNameValue);

                                            addNoticeNotification.setUserName(user.getUsername());
                                            addNoticeNotification.setUserImage(user.getImageURLThumbnail());

                                            notificationSender.execute(addNoticeNotification);
                                            mProgress.dismiss();
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                        });
                    }
                }
            });
        }
    }
}