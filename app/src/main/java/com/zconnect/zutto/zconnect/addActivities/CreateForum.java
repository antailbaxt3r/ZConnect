package com.zconnect.zutto.zconnect.addActivities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.CustomSpinner;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityTitle;
import static com.zconnect.zutto.zconnect.utilities.RequestCodes.GALLERY_REQUEST;

public class CreateForum extends BaseActivity {
    public class ForumTabObject {
        String name, UID;
        ForumTabObject() {

        }
        ForumTabObject(String UID, String name) {
            this.UID = UID;
            this.name = name;
        }

        public String getUID() {
            return UID;
        }

        public String getName() {
            return name;
        }
    }
    String mtabName, uid;
    boolean editForumFlag;
    FrameLayout addForumIcon, done;
    MaterialEditText addForumName, firstMessage;
    IntentHandle intentHandle;
    Intent galleryIntent;
    Uri mImageUri, mImageUriThumb;
    StorageReference mStorage;
    boolean flag;
    Calendar calendar;
    ProgressDialog progressDialog;
    TextView titleFirstMessage;
    LinearLayout deleteForumLL;

    CustomSpinner forumTabsSpinner;
    LinearLayout forumTabsSpinnerLayout;
    ArrayList<ForumTabObject> forumTabsObjectList;
    ArrayList<String> forumTabsNameList;
    ArrayAdapter<String> forumTabsSpinnerAdapter;
    private int init_opn_index;

    private String TAG = CreateForum.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_forum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);
        intentHandle = new IntentHandle();
        calendar = Calendar.getInstance();

        progressDialog = new ProgressDialog(this);

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

        uid = getIntent().getStringExtra("uid");
        editForumFlag = Boolean.parseBoolean(getIntent().getStringExtra("flag"));

        DatabaseReference tabName= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabs").child(uid);
        DatabaseReference tabsRef = tabName.getParent();
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
        titleFirstMessage = (TextView) findViewById(R.id.title_first_msg_create_forum_alert);
        done = (FrameLayout) findViewById(R.id.layout_done_content_create_forum);
        deleteForumLL = (LinearLayout) findViewById(R.id.delete_foruml_layout);
        forumTabsSpinner = findViewById(R.id.spinner_forum_tab);
        forumTabsSpinnerLayout = findViewById(R.id.spinner_forum_tab_layout);
        forumTabsNameList = new ArrayList<>();
        forumTabsObjectList = new ArrayList<>();
        intentHandle = new IntentHandle();
        mStorage = FirebaseStorage.getInstance().getReference();

        tabsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot shot : dataSnapshot.getChildren())
                {
                    if(uid.equals(shot.getKey()))
                    {
                        init_opn_index = i;
                    }
                    i++;
                    forumTabsObjectList.add(new ForumTabObject(shot.getKey(), shot.child("name").getValue().toString()));
                    forumTabsNameList.add(shot.child("name").getValue().toString());
                }
                forumTabsSpinnerAdapter.notifyDataSetChanged();
                forumTabsSpinner.setSelection(init_opn_index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        forumTabsSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, forumTabsNameList);
        forumTabsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        forumTabsSpinner.setAdapter(forumTabsSpinnerAdapter);

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

        if(!editForumFlag)
        {

            titleFirstMessage.setVisibility(View.VISIBLE);
            firstMessage.setVisibility(View.VISIBLE);
            deleteForumLL.setVisibility(View.GONE);

            done.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(addForumName.getText()!=null && addForumName.getText().toString().length()!=0
                            && firstMessage.getText()!=null && firstMessage.getText().toString().length()!=0)
                    {
                        progressDialog.setMessage("Creating forum");
                        progressDialog.show();

                        if(forumTabsSpinner.getSelectedItem()!=null)
                        {
                            uid = forumTabsObjectList.get(forumTabsSpinner.getSelectedItemPosition()).UID;
                            mtabName = forumTabsNameList.get(forumTabsSpinner.getSelectedItemPosition());
                        }

                        final String catName = addForumName.getText().toString();
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

                        final DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("userType") && dataSnapshot.child("userType").getValue().toString().equals(UsersTypeUtilities.KEY_ADMIN))
                                {
                                    newPush.child("verified").setValue(true);
                                    databaseReferenceTabsCategories.child(newPush.getKey()).child("verified").setValue(true);
                                    databaseReferenceHome.child(newPush.getKey()).child("verified").setValue(true);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        databaseReferenceTabsCategories.child(newPush.getKey()).child("name").setValue(catName);
                        databaseReferenceTabsCategories.child(newPush.getKey()).child("catUID").setValue(newPush.getKey());
                        databaseReferenceTabsCategories.child(newPush.getKey()).child("tabUID").setValue(uid);

                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta= new HashMap<>();
                        meta.put("catID",uid);
                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_FORUM_CREATED);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        counterItemFormat.setMeta(meta);
                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();

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

                                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);

                                UsersListItemFormat userDetails = new UsersListItemFormat();

                                userDetails.setImageThumb(userItem.getImageURLThumbnail());
                                userDetails.setName(userItem.getUsername());
                                userDetails.setPhonenumber(userItem.getMobileNumber());
                                userDetails.setUserUID(userItem.getUserUID());
                                userDetails.setUserType(ForumsUserTypeUtilities.KEY_ADMIN);

                                databaseReferenceTabsCategories.child(newPush.getKey()).child("users").child(userItem.getUserUID()).setValue(userDetails);

                                newPush.child("PostedBy").child("Username").setValue(userItem.getUsername());
                                newPush.child("PostedBy").child("ImageThumb").setValue(userItem.getImageURLThumbnail());

                                databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("Username").setValue(userItem.getUsername());
                                databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("ImageThumb").setValue(userItem.getImageURLThumbnail());

                                ChatItemFormats message = new ChatItemFormats();
                                message.setTimeDate(calendar.getTimeInMillis());
                                message.setUuid(userItem.getUserUID());
                                message.setName(userItem.getUsername());
                                message.setImageThumb(userItem.getImageURLThumbnail());
                                message.setMessage("\""+firstMessage.getText()+"\"");
                                message.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);
                                newPush.child("Chat").push().setValue(message);

                                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(uid).child(newPush.getKey()).child("lastMessage").setValue(message);

                                NotificationSender notificationSender = new NotificationSender(CreateForum.this,userItem.getUserUID());
                                NotificationItemFormat addForumNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD,userItem.getUserUID());
                                addForumNotification.setCommunityName(communityTitle);
                                addForumNotification.setItemKey(newPush.getKey());
                                addForumNotification.setItemCategoryUID(uid);
                                addForumNotification.setItemCategory(mtabName);
                                addForumNotification.setItemName(catName);
                                addForumNotification.setUserImage(userItem.getImageURLThumbnail());
                                addForumNotification.setUserName(userItem.getUsername());

                                notificationSender.execute(addForumNotification);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        ////writing uid of post to homePosts node in Users1.uid for handling data conistency
                        mPostedByDetails.child("homePosts").child(newPush.getKey()).setValue(true);


                        if(mImageUri!=null && mImageUriThumb!=null)
                        {
                            flag = false;
                            final StorageReference filePath = mStorage.child(communityReference).child("features").child("forums").child("groupIcons").child((mImageUri.getLastPathSegment()) + FirebaseAuth.getInstance().getCurrentUser().getUid());
                            UploadTask uploadTask = filePath.putFile(mImageUri);
                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return filePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        newPush.child("image").setValue(downloadUri != null ? downloadUri.toString() : null);
                                        databaseReferenceTabsCategories.child(newPush.getKey()).child("image").setValue(downloadUri != null ? downloadUri.toString() : null);
                                        databaseReferenceHome.child(newPush.getKey()).child("image").setValue(downloadUri != null ? downloadUri.toString() : null);
                                        if(flag) {
                                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(newPush.getKey()).toString());
                                            intent.putExtra("type", "forums");
                                            intent.putExtra("name", catName);
                                            intent.putExtra("tab", uid);
                                            intent.putExtra("key", newPush.getKey());
                                            GlobalFunctions.addPoints(10);
                                            startActivity(intent);
                                            finish();
                                        } else{ flag = true;}
                                    } else {
                                        Snackbar snackbar = Snackbar.make(addForumName, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                        snackbar.show();
                                    }
                                }
                            });
                            final StorageReference filePathThumb = mStorage.child(communityReference).child("features").child("forums").child("groupIcons").child((mImageUri.getLastPathSegment()) + FirebaseAuth.getInstance().getCurrentUser().getUid() + "Thumb");
                            UploadTask uploadTaskThumb = filePathThumb.putFile(mImageUriThumb);
                            uploadTaskThumb.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return filePathThumb.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        newPush.child("imageThumb").setValue(downloadUri != null ? downloadUri.toString() : null);
                                        databaseReferenceTabsCategories.child(newPush.getKey()).child("imageThumb").setValue(downloadUri != null ? downloadUri.toString() : null);
                                        databaseReferenceHome.child(newPush.getKey()).child("imageThumb").setValue(downloadUri != null ? downloadUri.toString() : null);
                                        if (flag) {
                                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(newPush.getKey()).toString());
                                            intent.putExtra("type", "forums");
                                            intent.putExtra("name", catName);
                                            intent.putExtra("tab", uid);
                                            intent.putExtra("key", newPush.getKey());
                                            GlobalFunctions.addPoints(10);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            flag = true;
                                        }
                                    } else {
                                        // Handle failures
                                        // ...
                                        Snackbar snackbar = Snackbar.make(addForumName, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                        snackbar.show();
                                    }

                                }
                            });
                        }
                        else {
//                        newPush.child("image").setValue("https://firebasestorage.googleapis.com/v0/b/zconnectmulticommunity.appspot.com/o/testCollege%2Ffeatures%2Fother%20features%20icons%2Fbaseline_fastfood_white_36dp.png?alt=media&token=d1146a76-aff9-4fce-a999-a3b560925d46");
//                        databaseReferenceTabsCategories.child(newPush.getKey()).child("image").setValue("https://firebasestorage.googleapis.com/v0/b/zconnectmulticommunity.appspot.com/o/testCollege%2Ffeatures%2Fother%20features%20icons%2Fbaseline_fastfood_white_36dp.png?alt=media&token=d1146a76-aff9-4fce-a999-a3b560925d46");
//                        databaseReferenceHome.child(newPush.getKey()).child("image").setValue("https://firebasestorage.googleapis.com/v0/b/zconnectmulticommunity.appspot.com/o/testCollege%2Ffeatures%2Fother%20features%20icons%2Fbaseline_fastfood_white_36dp.png?alt=media&token=d1146a76-aff9-4fce-a999-a3b560925d46");
//                        newPush.child("imageThumb").setValue("https://firebasestorage.googleapis.com/v0/b/zconnectmulticommunity.appspot.com/o/testCollege%2Ffeatures%2Fother%20features%20icons%2Fbaseline_fastfood_white_36dp.png?alt=media&token=d1146a76-aff9-4fce-a999-a3b560925d46");
//                        databaseReferenceTabsCategories.child(newPush.getKey()).child("imageThumb").setValue("https://firebasestorage.googleapis.com/v0/b/zconnectmulticommunity.appspot.com/o/testCollege%2Ffeatures%2Fother%20features%20icons%2Fbaseline_fastfood_white_36dp.png?alt=media&token=d1146a76-aff9-4fce-a999-a3b560925d46");
//                        databaseReferenceHome.child(newPush.getKey()).child("imageThumb").setValue("https://firebasestorage.googleapis.com/v0/b/zconnectmulticommunity.appspot.com/o/testCollege%2Ffeatures%2Fother%20features%20icons%2Fbaseline_fastfood_white_36dp.png?alt=media&token=d1146a76-aff9-4fce-a999-a3b560925d46");
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(newPush.getKey()).toString());
                            intent.putExtra("type","forums");
                            intent.putExtra("name", catName);
                            intent.putExtra("tab",uid);
                            intent.putExtra("key",newPush.getKey());
                            startActivity(intent);
                            GlobalFunctions.addPoints(10);
                            finish();
                        }
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(addForumName, "Forum name and first message required", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });
        } else {

            forumTabsSpinnerLayout.setVisibility(View.GONE);

            getSupportActionBar().setTitle("Edit forum info");
            titleFirstMessage.setVisibility(View.GONE);
            firstMessage.setVisibility(View.GONE);
            final String catUid = getIntent().getStringExtra("catUID");


            //use this only for writing new data
            final DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(catUid);
            //use for both retrieving data and writing new data
            final DatabaseReference tabsCategoriesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(uid).child(catUid);

            tabsCategoriesRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        if(snapshot.hasChild("userType") && snapshot.child("userType").getValue().toString().equals(ForumsUserTypeUtilities.KEY_ADMIN))
                            deleteForumLL.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("userType") && dataSnapshot.child("userType").getValue().toString().equals(UsersTypeUtilities.KEY_ADMIN))
                        deleteForumLL.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            deleteForumLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Removing forum started...");
                    categoriesRef.removeValue();
                    tabsCategoriesRef.removeValue();
                    Toast.makeText(CreateForum.this, "Forum successfully deleted", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

            tabsCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    addForumName.setText(dataSnapshot.child("name").getValue().toString());
                    if(dataSnapshot.hasChild("imageThumb"))
                    {
                        ((SimpleDraweeView)findViewById(R.id.forum_icon_create_forum_alert)).setImageURI(dataSnapshot.child("imageThumb").getValue().toString());
                        (findViewById(R.id.camera_icon_create_forum_alert)).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            done.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(addForumName.getText()!=null && addForumName.getText().toString().length()!=0)
                    {
                        progressDialog.setMessage("Updating forum info");
                        progressDialog.show();

                        final String catName = addForumName.getText().toString();
                        final DatabaseReference databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories");
                        final DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(uid);

                        final Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("name", catName);


                        if(mImageUri!=null && mImageUriThumb!=null)
                        {
                            flag = false;
                            final StorageReference filePath = mStorage.child(communityReference).child("features").child("forums").child("groupIcons").child((mImageUri.getLastPathSegment()) + FirebaseAuth.getInstance().getCurrentUser().getUid());
                            UploadTask uploadTask = filePath.putFile(mImageUri);
                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return filePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        taskMap.put("image", downloadUri != null ? downloadUri.toString() : null);
                                        categoriesRef.updateChildren(taskMap);
                                        tabsCategoriesRef.updateChildren(taskMap);
                                        if(flag) {

                                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                                            HashMap<String, String> meta= new HashMap<>();
                                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                            counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_EDITED_FORUM);
                                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                                            meta.put("catUID",catUid);
                                            counterItemFormat.setMeta(meta);
                                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                            counterPush.pushValues();

                                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(catUid).toString());
                                            intent.putExtra("type", "forums");
                                            intent.putExtra("name", catName);
                                            intent.putExtra("tab", uid);
                                            intent.putExtra("key", catUid);
                                            startActivity(intent);
                                            finish();
                                        } else{ flag = true;}
                                    } else {
                                        Snackbar snackbar = Snackbar.make(addForumName, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                        snackbar.show();
                                    }
                                }
                            });
                            final StorageReference filePathThumb = mStorage.child(communityReference).child("features").child("forums").child("groupIcons").child((mImageUri.getLastPathSegment()) + FirebaseAuth.getInstance().getCurrentUser().getUid() + "Thumb");
                            UploadTask uploadTaskThumb = filePathThumb.putFile(mImageUriThumb);
                            uploadTaskThumb.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return filePathThumb.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        taskMap.put("imageThumb", downloadUri != null ? downloadUri.toString() : null);
                                        categoriesRef.updateChildren(taskMap);
                                        tabsCategoriesRef.updateChildren(taskMap);

                                        if (flag) {

                                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                                            HashMap<String, String> meta= new HashMap<>();
                                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                            counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_EDITED_FORUM);
                                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                                            meta.put("catUID",catUid);
                                            counterItemFormat.setMeta(meta);
                                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                            counterPush.pushValues();

                                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(catUid).toString());
                                            intent.putExtra("type", "forums");
                                            intent.putExtra("name", catName);
                                            intent.putExtra("tab", uid);
                                            intent.putExtra("key", catUid);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            flag = true;
                                        }
                                    } else {
                                        // Handle failures
                                        // ...
                                        Snackbar snackbar = Snackbar.make(addForumName, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                        snackbar.show();
                                    }

                                }
                            });
                        } else {

                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta= new HashMap<>();
                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_EDITED_FORUM);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            meta.put("catUID",catUid);
                            counterItemFormat.setMeta(meta);
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();

                            categoriesRef.updateChildren(taskMap);
                            tabsCategoriesRef.updateChildren(taskMap);
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(catUid).toString());
                            intent.putExtra("type","forums");
                            intent.putExtra("name", catName);
                            intent.putExtra("tab",uid);
                            intent.putExtra("key",catUid);
                            startActivity(intent);

                            finish();
                        }
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(addForumName, "Forum name and first message required", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentHandle intentHandle = new IntentHandle();
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data, CreateForum.this); //Get data
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
