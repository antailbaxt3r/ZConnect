package com.zconnect.zutto.zconnect.addActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;
import java.io.IOException;
import java.util.Calendar;

import static com.zconnect.zutto.zconnect.Utilities.RequestCodes.GALLERY_REQUEST;

public class CreateForum extends AppCompatActivity {
    String mtabName, uid;
    FrameLayout addForumIcon, done;
    MaterialEditText addForumName, firstMessage;
    IntentHandle intentHandle;
    Intent galleryIntent;
    Uri mImageUri, mImageUriThumb;
    StorageReference mStorage;
    boolean flag;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_forum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);
        intentHandle = new IntentHandle();
        calendar = Calendar.getInstance();

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

        uid = getIntent().getStringExtra("uid");
        DatabaseReference tabName= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabs").child(uid);
        tabName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               mtabName = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addForumName = (MaterialEditText) findViewById(R.id.edit_forum_name_activity_create_forum);
        addForumIcon = (FrameLayout) findViewById(R.id.layout_add_icon_activity_create_forum);
        firstMessage = (MaterialEditText) findViewById(R.id.edit_first_msg_create_forum_alert);
        done = (FrameLayout) findViewById(R.id.layout_done_activity_create_forum);
        intentHandle = new IntentHandle();
        mStorage = FirebaseStorage.getInstance().getReference();

        addForumIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        CreateForum.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateForum.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                } else {
                    galleryIntent = intentHandle.getPickImageIntent(CreateForum.this);
                    CreateForum.this.startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(addForumName.getText()!=null && addForumName.getText().toString().length()!=0
                        && firstMessage.getText()!=null && firstMessage.getText().toString().length()!=0)
                {
                    String catName = addForumName.getText().toString();
                    final DatabaseReference databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories");
                    final DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(uid);
                    final DatabaseReference databaseReferenceHome = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");

                    final DatabaseReference newPush=databaseReferenceCategories.push();
                    DatabaseReference mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    newPush.child("name").setValue(catName);
                    Long postTimeMillis = System.currentTimeMillis();
                    newPush.child("PostTimeMillis").setValue(postTimeMillis);
                    newPush.child("UID").setValue(newPush.getKey());
                    newPush.child("tab").setValue(uid);



                    final UsersListItemFormat userDetails = new UsersListItemFormat();
                    DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            UserItemFormat userItemFormat = dataSnapshot2.getValue(UserItemFormat.class);
                            userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                            userDetails.setName(userItemFormat.getUsername());
                            userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                            userDetails.setUserUID(userItemFormat.getUserUID());
                            newPush.child("users").child(userItemFormat.getUserUID()).setValue(userDetails);
                            CounterManager.forumsAddCategory(mtabName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    databaseReferenceTabsCategories.child(newPush.getKey()).child("name").setValue(catName);
                    databaseReferenceTabsCategories.child(newPush.getKey()).child("catUID").setValue(newPush.getKey());
                    databaseReferenceTabsCategories.child(newPush.getKey()).child("tabUID").setValue(uid);


                    //Home

                    databaseReferenceHome.child(newPush.getKey()).child("feature").setValue("Forums");
                    databaseReferenceHome.child(newPush.getKey()).child("name").setValue(catName);
                    databaseReferenceHome.child(newPush.getKey()).child("id").setValue(uid);
                    databaseReferenceHome.child(newPush.getKey()).child("desc").setValue(mtabName);
                    databaseReferenceHome.child(newPush.getKey()).child("Key").setValue(newPush.getKey());
                    databaseReferenceHome.child(newPush.getKey()).child("PostTimeMillis").setValue(postTimeMillis);

                    databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPush.child("PostedBy").child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                            newPush.child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());

                            databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                            databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    ChatItemFormats message = new ChatItemFormats();
                    message.setTimeDate(calendar.getTimeInMillis());
                    UserItemFormat userItem = UserUtilities.currentUser;
//                    UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);
                    message.setUuid(userItem.getUserUID());
                    message.setName(userItem.getUsername());
                    message.setImageThumb(userItem.getImageURLThumbnail());
                    message.setMessage("\""+firstMessage.getText()+"\"");
                    message.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);
                    newPush.child("Chat").push().setValue(message);

                    NotificationSender notificationSender = new NotificationSender(CreateForum.this, UserUtilities.currentUser.getUserUID());
                    NotificationItemFormat addForumNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD,UserUtilities.currentUser.getUserUID());
                    addForumNotification.setCommunityName(UserUtilities.CommunityName);
                    addForumNotification.setItemKey(newPush.getKey());
                    addForumNotification.setItemCategoryUID(uid);
                    addForumNotification.setItemCategory(mtabName);
                    addForumNotification.setItemName(catName);
                    addForumNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                    addForumNotification.setUserName(UserUtilities.currentUser.getUsername());

                    notificationSender.execute(addForumNotification);


                    if(mImageUri!=null && mImageUriThumb!=null)
                    {
                        flag = false;
                        final StorageReference filePath = mStorage.child(communityReference).child("features").child("forums").child("groupIcons").child((mImageUri.getLastPathSegment()) + FirebaseAuth.getInstance().getCurrentUser().getUid());
                        filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                newPush.child("image").setValue(downloadUri != null ? downloadUri.toString() : null);
                                databaseReferenceTabsCategories.child(newPush.getKey()).child("image").setValue(downloadUri != null ? downloadUri.toString() : null);
                                databaseReferenceHome.child(newPush.getKey()).child("image").setValue(downloadUri != null ? downloadUri.toString() : null);
                                if(flag)
                                {
                                    finish();
                                }
                                else flag = true;
                            }
                        });
                        final StorageReference filePathThumb = mStorage.child(communityReference).child("features").child("forums").child("groupIcons").child((mImageUri.getLastPathSegment()) + FirebaseAuth.getInstance().getCurrentUser().getUid() + "Thumb");
                        filePathThumb.putFile(mImageUriThumb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downlaodUri = taskSnapshot.getDownloadUrl();
                                newPush.child("imageThumb").setValue(downlaodUri != null ? downlaodUri.toString() : null);
                                databaseReferenceTabsCategories.child(newPush.getKey()).child("imageThumb").setValue(downlaodUri != null ? downlaodUri.toString() : null);
                                databaseReferenceHome.child(newPush.getKey()).child("imageThumb").setValue(downlaodUri != null ? downlaodUri.toString() : null);
                                if (flag)
                                    finish();
                                else flag = true;
                            }
                        });
                    }
                }
                else {
                    Snackbar snack = Snackbar.make(addForumName, "Forum name and first message required", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentHandle intentHandle = new IntentHandle();
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data); //Get data
            CropImage.activity(imageUri).setAspectRatio(1,1)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(CreateForum.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {
                    mImageUri = result.getUri();

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(CreateForum.this.getContentResolver(), mImageUri);
                    Double ratio = ((double) bitmap.getWidth()) / bitmap.getHeight();
                    if (bitmap.getByteCount() > 350000) {

                        bitmap = Bitmap.createScaledBitmap(bitmap, 960, (int) (960 / ratio), false);
                    }
                    Bitmap bitmapSmall = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                    String path = MediaStore.Images.Media.insertImage(CreateForum.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);
                    String pathSmall = MediaStore.Images.Media.insertImage(CreateForum.this.getContentResolver(), bitmapSmall, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    mImageUriThumb = Uri.parse(pathSmall);

                    ((SimpleDraweeView)findViewById(R.id.forum_icon_create_forum_alert)).setImageURI(mImageUriThumb);
                    (findViewById(R.id.camera_icon_create_forum_alert)).setVisibility(View.GONE);
                    Log.d("SET IMAGE", "");

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