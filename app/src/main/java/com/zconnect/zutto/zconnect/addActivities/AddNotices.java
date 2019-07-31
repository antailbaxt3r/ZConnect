package com.zconnect.zutto.zconnect.addActivities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.commonModules.NumberNotificationForFeatures;
import com.zconnect.zutto.zconnect.commonModules.SquareImageView;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.FeatureDBName;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zconnect.zutto.zconnect.utilities.RequestCodes.GALLERY_REQUEST;

public class AddNotices extends BaseActivity {

    private SimpleDraweeView mAddPhoto;
    private RelativeLayout mAddPhotoLayout;
    private TextView mName;
    private String key;
    private Intent galleryIntent;
    private IntentHandle intentHandle;
    private StorageReference mStorage;
    private DatabaseReference mDatabase,mPostedByDetails;
    private ProgressDialog mProgress;
    private Button submit;
    private Long postTimeMillis;
    private DatabaseReference mUsername;
    private FirebaseAuth mAuth;
    private Uri mImageUri=null, mImageUriSmall = null;
    private LinearLayout expiryDateLL;
    private static TextView expiryDateTV;
    private static Map<String, Integer> expiryDate;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        mProgress = new ProgressDialog(this);

        setToolbar();
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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        mAddPhotoLayout = findViewById(R.id.image_layout);
        mAddPhoto = findViewById(R.id.imageButton);
        mName=(TextView)findViewById(R.id.name);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("notices").child("activeNotices");
        submit=(Button)findViewById(R.id.submit);
        expiryDateLL = findViewById(R.id.expiryDateLayout);
        expiryDateTV = findViewById(R.id.expiryDateText);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        expiryDate = new HashMap<>();

        intentHandle = new IntentHandle();

        mAddPhotoLayout.setOnClickListener(new View.OnClickListener() {
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

        expiryDateLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
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

                    Picasso.with(this).load(mImageUri).into(mAddPhoto);

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

        final String noticeNameValue = mName.getText().toString().trim();

        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        final String userId = user.getUid();
        mUsername = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");



        if ( !TextUtils.isEmpty(noticeNameValue) &&  mImageUri != null && mImageUriSmall !=null) {
            mProgress.show();
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
                        key = newPost.getKey();
                        postTimeMillis = System.currentTimeMillis();
                        final Map<String, Object> newPostMap = new HashMap<>();
                        newPostMap.put("key", key);
                        newPostMap.put("title", noticeNameValue);
                        newPostMap.put("imageURL", downloadUri != null ? downloadUri.toString() : null);
                        newPostMap.put("postTimeMillis", postTimeMillis);
                        final Map<String, Object> postedByMap = new HashMap<>();
                        postedByMap.put("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        if(expiryDate.get("Year")!=null)
                        {
                            Map<String, Object> expiryDateTaskMap = new HashMap<>();
                            expiryDateTaskMap.put("year", expiryDate.get("Year"));
                            expiryDateTaskMap.put("month", expiryDate.get("Month"));
                            expiryDateTaskMap.put("day", expiryDate.get("Day"));
                            newPostMap.put("expiryDate", expiryDateTaskMap);
                        }
                        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                                postedByMap.put("Username", user.getUsername());
                                postedByMap.put("ImageThumb", user.getImageURLThumbnail());
                                newPostMap.put("PostedBy", postedByMap);
                                newPost.updateChildren(newPostMap);
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
        }else{
            RelativeLayout relativeLayout = findViewById(R.id.addNoticeLayout);
            Snackbar snack = (Snackbar) Snackbar.make(relativeLayout, "Fields are empty. Can't Add Notice", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            snack.show();
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);



            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            expiryDate.put("Year", year);
            expiryDate.put("Month", month);
            expiryDate.put("Day", day);
            //month range is from 0 to 11
            String expiryDateText = "Valid till: " + day + "/" + (month+1) + "/" + (year%100);
            expiryDateTV.setText(expiryDateText);
        }
    }
}