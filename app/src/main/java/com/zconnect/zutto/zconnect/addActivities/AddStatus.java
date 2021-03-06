package com.zconnect.zutto.zconnect.addActivities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.Event;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.PermissionUtilities;
import com.zconnect.zutto.zconnect.utilities.RecentTypeUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddStatus extends BaseActivity {

    FrameLayout submit;
    ImageButton addImage;
    private SimpleDraweeView postImageView;
    CheckBox anonymousCheck;
    MaterialEditText messageInput;
    View.OnClickListener submitlistener;
    Event event;
    Boolean a;
    String anonymous;
    DatabaseReference mPostedByDetails;
    ProgressDialog mProgress;
    SimpleDraweeView userAvatar;
    TextView username;
    ArrayList<TextView> statusHints;

    //For posting photo
    private IntentHandle intentHandle;
    private Intent galleryIntent;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private static final int GALLERY_REQUEST = 7;
    private PermissionUtilities permissionUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));


        mProgress = new ProgressDialog(this);
        setActionBarTitle("Post a status");
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
        permissionUtilities = new PermissionUtilities(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!permissionUtilities.isEnabled(PermissionUtilities.READ_EXTERNAL_STORAGE))
                permissionUtilities.request(permissionUtilities.READ_EXTERNAL_STORAGE);
        }

        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mStorage = FirebaseStorage.getInstance().getReference().child(communityReference).child("Post");

        final DatabaseReference home;
        home= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");

        submit = (FrameLayout) findViewById(R.id.done_content_new_message);
        anonymousCheck = (CheckBox) findViewById(R.id.checkbox_newmessage_anonymous);
        messageInput = (MaterialEditText) findViewById(R.id.edittext_newmessage_input);
        addImage = (ImageButton) findViewById(R.id.add_image_new_message);
        userAvatar = (SimpleDraweeView) findViewById(R.id.avatarCircle_new_message);
        username = (TextView) findViewById(R.id.username_new_message);
        postImageView = (SimpleDraweeView) findViewById(R.id.post_image_view);
        statusHints = new ArrayList<>();
        statusHints.add((TextView) findViewById(R.id.status_hint_1));
        statusHints.add((TextView) findViewById(R.id.status_hint_2));
        statusHints.add((TextView) findViewById(R.id.status_hint_3));
        statusHints.add((TextView) findViewById(R.id.status_hint_4));
        statusHints.add((TextView) findViewById(R.id.status_hint_5));
        for(int i =0; i<5; i++)
        {
            final TextView hint = statusHints.get(i);
            statusHints.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String hint_str = hint.getText().toString().substring(0,hint.getText().length()-3) + " ?";
                    messageInput.setText(hint_str);
                    messageInput.setEnabled(true);
                    messageInput.requestFocus();
                    messageInput.setSelection(hint_str.length()-1);
                }
            });
        }

        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                username.setText(user.getUsername());
                userAvatar.setImageURI(user.getImageURLThumbnail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        intentHandle = new IntentHandle();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        AddStatus.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AddStatus.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                } else {
                    galleryIntent = intentHandle.getPickImageIntent(AddStatus.this);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        });

        submitlistener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if(messageInput.getText().toString().trim().length()==0 && mImageUri ==null)
                {
                    Snackbar snackbar = Snackbar.make(view, "Text or image needed.", Snackbar.LENGTH_SHORT);
                    snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                    snackbar.show();
                }
                else
                {
                    mProgress.setMessage("Posting Status..");
                    mProgress.show();

                    String messageText = messageInput.getText().toString();
                    if(anonymousCheck.isChecked())
                        anonymous = "y";
                    else
                        anonymous = "n";

                    final DatabaseReference newMessage = home.push();
                    final DatabaseReference homePosts = mPostedByDetails.child("homePosts");
                    final String key = newMessage.getKey();

                    homePosts.child(key).setValue(true);

                    final Map<String, Object> taskMap = new HashMap<String, Object>();

                    taskMap.put("Key",key);
                    taskMap.put("desc",messageText);
                    taskMap.put("desc2",anonymous);
                    taskMap.put("feature","Message");
                    taskMap.put("name","Message");
                    taskMap.put("recentType",RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);
                    taskMap.put("id",key);
                    taskMap.put("PostTimeMillis",System.currentTimeMillis());

                    FirebaseMessaging.getInstance().subscribeToTopic(key);

                    final Map<String, Object> postedByMap = new HashMap<String, Object>();
                    postedByMap.put("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                            if (anonymousCheck.isChecked()){
                                postedByMap.put("Username", "Anonymous");
                                postedByMap.put("ImageThumb", "https://firebasestorage.googleapis.com/v0/b/zconnectmulticommunity.appspot.com/o/Icons%2Fanonymous.jpg?alt=media&token=259d06b2-626d-4df8-b8cc-f525195473ab");
                            }else {
                                postedByMap.put("Username", user.getUsername());
                                postedByMap.put("ImageThumb", user.getImageURLThumbnail());
                            }


                            taskMap.put("PostedBy", postedByMap);


                            if(mImageUri!= null) {

                                final StorageReference filepath = mStorage.child((mImageUri.getLastPathSegment()) + FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful())
                                        {
                                            Uri downloadUri = task.getResult();
                                            taskMap.put("imageurl",downloadUri != null ? downloadUri.toString() : null);
                                            newMessage.setValue(taskMap);
                                            GlobalFunctions.addPoints(10);
                                            mProgress.dismiss();

                                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                                            HashMap<String, String> meta= new HashMap<>();
                                            meta.put("type","withImage");
                                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                            counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_ADDED_STATUS);
                                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                                            counterItemFormat.setMeta(meta);
                                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                            counterPush.pushValues();

                                            finish();
                                        }
                                        else {
                                            // Handle failures
                                            // ...
                                            Snackbar snackbar = Snackbar.make(view, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                                            snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                            snackbar.show();
                                        }
                                    }
                                });

                            }else {

                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                HashMap<String, String> meta= new HashMap<>();
                                meta.put("type","withoutImage");
                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_ADDED_STATUS);
                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                counterItemFormat.setMeta(meta);
                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                counterPush.pushValues();

                                taskMap.put("imageurl",RecentTypeUtilities.KEY_RECENTS_NO_IMAGE_STATUS);
                                newMessage.setValue(taskMap);
                                GlobalFunctions.addPoints(5);
                                mProgress.dismiss();
                                finish();
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            }
        };

        submit.setOnClickListener(submitlistener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data, AddStatus.this); //Get data
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
                    String path = MediaStore.Images.Media.insertImage(AddStatus.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    postImageView.setVisibility(View.VISIBLE);
                    addImage.setVisibility(View.GONE);
                    Picasso.with(AddStatus.this).load(mImageUri).into(postImageView);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtilities.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
