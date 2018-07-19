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

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.itemFormats.Event;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.RecentTypeUtilities;

import java.io.IOException;
import java.util.ArrayList;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
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
                    messageInput.setText(hint.getText());
                    messageInput.setEnabled(true);
                    messageInput.requestFocus();
                    messageInput.setSelection(hint.getText().length()-1);
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
            public void onClick(View view) {

                mProgress.setMessage("Posting Status..");
                mProgress.show();

                String messageText = messageInput.getText().toString();
                if(anonymousCheck.isChecked())
                    anonymous = "y";
                else
                    anonymous = "n";

                final DatabaseReference newMessage = home.push();
                final String key = newMessage.getKey();
                newMessage.child("Key").setValue(key);
                newMessage.child("desc").setValue(messageText);
                newMessage.child("desc2").setValue(anonymous);
                newMessage.child("feature").setValue("Message");
                newMessage.child("name").setValue("Message");
                newMessage.child("recentType").setValue(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);

                newMessage.child("imageurl").setValue("https://www.iconexperience.com/_img/o_collection_png/green_dark_grey/512x512/plain/message.png");
                newMessage.child("id").setValue(key);
                newMessage.child("PostTimeMillis").setValue(System.currentTimeMillis());
                mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                        if (anonymousCheck.isChecked()){
                            newMessage.child("PostedBy").child("Username").setValue("Anonymous");
                        }else {
                            newMessage.child("PostedBy").child("Username").setValue(user.getUsername());
                        }
                        newMessage.child("PostedBy").child("UID").setValue(user.getUserUID());
                        newMessage.child("PostedBy").child("ImageThumb").setValue(user.getImageURLThumbnail());
                        FirebaseMessaging.getInstance().subscribeToTopic(key);
                        CounterManager.publicStatusAdd(anonymousCheck.isChecked());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(mImageUri!= null) {

                    StorageReference filepath = mStorage.child((mImageUri.getLastPathSegment()) + FirebaseAuth.getInstance().getCurrentUser().getUid());

                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            newMessage.child("imageurl").setValue(downloadUri != null ? downloadUri.toString() : null);

                            mProgress.dismiss();
                            finish();

                        }
                    });

                }else {
                    newMessage.child("imageurl").setValue(RecentTypeUtilities.KEY_RECENTS_NO_IMAGE_STATUS);
                    mProgress.dismiss();
                    finish();
                }
            }
        };

        submit.setOnClickListener(submitlistener);

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
                    String path = MediaStore.Images.Media.insertImage(AddStatus.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    postImageView.setVisibility(View.VISIBLE);
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

}
