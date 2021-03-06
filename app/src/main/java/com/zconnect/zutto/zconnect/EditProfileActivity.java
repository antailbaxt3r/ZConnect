package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
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
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.zconnect.zutto.zconnect.addActivities.AddProduct;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.InfoneCategoryModel;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.ProductUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import mabbas007.tagsedittext.TagsEditText;

import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_CABPOOL;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_EVENT;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_OFFERS;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_STOREROOM;

public class EditProfileActivity extends BaseActivity implements TagsEditText.TagsEditListener, View.OnClickListener {

    private static final int GALLERY_REQUEST = 7;
    private static final String DEFAULT_PHOTO_URL = "https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/PhonebookImage%2FdefaultprofilePhone.png?alt=media&token=5f814762-16dc-4dfb-ba7d-bcff0de7a336";
    private final String TAG = getClass().getSimpleName();
    public ProgressDialog mProgress;


    Toolbar toolbar;

    private SimpleDraweeView userImageView;
    private String userName,userEmail,userMobile,userWhatsapp,userAbout,userSkillTags,userInfoneType, anonymousUserName;
    private MaterialEditText userNameText, userEmailText, userMobileNumberText, userWhatsappNumberText, userAboutText,anonymousUserNameET;
    private Button userTypeText;
    private TagsEditText userSkillTagsText;
    private MaterialBetterSpinner userInfoneTypeSpinner;
    private Boolean newUser = false;
    private Boolean isReferred = false;
    private Vector<InfoneCategoryModel> infoneCategories = new Vector<InfoneCategoryModel>();
    private Vector<String> infoneCategoriesName = new Vector<String>();
    private ArrayAdapter<String> infoneAdapter;
    private int infoneTypeIndex=-1;
    Boolean flag = false;
    private Long postTimeMillis;

    private CheckBox hideContactCB;

    private Uri mImageUri=null;
    private Uri mImageUriSmall=null;
    private StorageReference mStorageRef;
    private FirebaseUser mUser;
    private DatabaseReference mUserReference,databaseInfoneCategories,databaseHome,newContactNumRef,databaseReferenceInfone,newContactRef;
    private String communityName;
    private UserItemFormat userDetails;
    private DatabaseReference homePush;
    @Override
    public void onBackPressed() {
        Dialog exitDialog = new Dialog(EditProfileActivity.this);
        exitDialog.setContentView(R.layout.new_dialog_box);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.findViewById(R.id.dialog_box_image_sdv).setBackground(ContextCompat.getDrawable(EditProfileActivity.this,R.drawable.ic_profile_icon));
        TextView heading =  exitDialog.findViewById(R.id.dialog_box_heading);
        heading.setText("Unsaved Changes");
        TextView body = exitDialog.findViewById(R.id.dialog_box_body);
        body.setText("Are you sure you want to go back?");
        Button positiveButton = exitDialog.findViewById(R.id.dialog_box_positive_button);
        positiveButton.setText("YES");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();

                NavUtils.navigateUpFromSameTask(EditProfileActivity.this);

            }
        });
        Button askButton = exitDialog.findViewById(R.id.dialog_box_negative_button);
        askButton.setText("NO");
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              exitDialog.dismiss();
            }
        });

        exitDialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProgress = new ProgressDialog(this);

        userNameText = (MaterialEditText) findViewById(R.id.user_name);
        userTypeText = (Button) findViewById(R.id.user_type);
        userEmailText = (MaterialEditText) findViewById(R.id.user_email);
        userMobileNumberText = (MaterialEditText) findViewById(R.id.mobile_number);
        userWhatsappNumberText = (MaterialEditText) findViewById(R.id.whatsapp_number);
        userAboutText= (MaterialEditText) findViewById(R.id.user_about);
        userImageView = (SimpleDraweeView) findViewById(R.id.user_image_view);
        userInfoneTypeSpinner = (MaterialBetterSpinner) findViewById(R.id.user_infone_type);
        userSkillTagsText = (TagsEditText) findViewById(R.id.user_skill_tags);
        hideContactCB = (CheckBox) findViewById(R.id.hide_contact_check_box);
        anonymousUserNameET = findViewById(R.id.anonymous_username_et);

        Typeface ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        Typeface ralewaySemiBold = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        userNameText.setTypeface(ralewaySemiBold);
        userTypeText.setTypeface(ralewaySemiBold);
        userAboutText.setTypeface(ralewayRegular);
        userMobileNumberText.setTypeface(ralewayRegular);
        userWhatsappNumberText.setTypeface(ralewayRegular);
        userEmailText.setTypeface(ralewayRegular);

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

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(mUser.getUid());
        mUserReference.keepSynced(true);
        databaseInfoneCategories =FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("infone").child("categoriesInfo");
        databaseHome = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");
        databaseReferenceInfone = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("infone");


        if (mUser == null) {
            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
            finish();
        }

        Bundle bundle = getIntent().getExtras();
        newUser = bundle.getBoolean("newUser");
        isReferred = bundle.getBoolean("isReferred");
        Log.d("RRRR", "Bundle is referred" + String.valueOf(isReferred));
        if (getSupportActionBar() != null) {
            if (newUser) getSupportActionBar().setTitle("Add Contact");
            else getSupportActionBar().setTitle("Edit Profile");
        }

        databaseInfoneCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    infoneCategories.add(shot.getValue(InfoneCategoryModel.class));
                    infoneCategoriesName.add(shot.child("name").getValue(String.class));
                    Log.d(shot.child("name").getValue(String.class), "onDataChangeon: ");
                    Log.d(shot.getValue(InfoneCategoryModel.class).getCatId(), "onDataChangeon: ");
                }
                infoneAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        infoneAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, infoneCategoriesName);

        userInfoneTypeSpinner.setAdapter(infoneAdapter);
        userImageView.setOnClickListener(this);

        userSkillTagsText.setTagsListener(this);
        userSkillTagsText.setTagsWithSpacesEnabled(false);

        userSkillTagsText.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.skills)));
        userSkillTagsText.setThreshold(1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        flag =false;
        if(newUser) {
            userNameText.setText(mUser.getDisplayName());
            if(mUser.getDisplayName()==null) {
                for(UserInfo userInfo : mUser.getProviderData())
                {
                    userNameText.setText(userInfo.getDisplayName());
                }
            }
            userEmailText.setText(mUser.getEmail());
            userTypeText.setVisibility(View.GONE);

            anonymousUserNameET.setText("Unknown");
            hideContactCB.setChecked(false);

            userEmailText.setFocusable(false);
            if (mImageUri==null) {
                if (mUser.getPhotoUrl() != null) {
                    userImageView.setImageURI(mUser.getPhotoUrl());
                } else {
                    for(UserInfo userInfo : mUser.getProviderData())
                    {
                        if(userInfo.getPhotoUrl() != null)
                            userImageView.setImageURI(userInfo.getPhotoUrl());
                        else
                            userImageView.setImageURI(Uri.parse(DEFAULT_PHOTO_URL));
                    }
                }
            }
        }else {
            mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userDetails=dataSnapshot.getValue(UserItemFormat.class);
                    if(!dataSnapshot.hasChild("contactHidden")){
                        userDetails.setContactHidden(false);
                    }
                    updateViewDetails();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateViewDetails() {
        userEmailText.setText(userDetails.getEmail());
        if(userDetails.getUserType().equals(UsersTypeUtilities.KEY_ADMIN)){
            userTypeText.setText("Admin");
        }else if(userDetails.getUserType().equals(UsersTypeUtilities.KEY_VERIFIED)){
            userTypeText.setText("Verfied Member");
        }else if(userDetails.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED)){
            userTypeText.setText("Not Verified, Verify Now");
            userTypeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(),VerificationPage.class);
                    startActivity(i);
                }
            });
        }

        userNameText.setText(userDetails.getUsername());
        userWhatsappNumberText.setText(userDetails.getWhatsAppNumber());
        userMobileNumberText.setText(userDetails.getMobileNumber());
        userAboutText.setText(userDetails.getAbout());
        if(userDetails.getAnonymousUsername() != null){
            anonymousUserNameET.setText(userDetails.getAnonymousUsername());
        }
        else{
            anonymousUserNameET.setText("Unknown");
        }

        hideContactCB.setChecked(userDetails.getContactHidden());

        for(int i=0;i<infoneCategoriesName.size();i++){
            Log.d(userDetails.getInfoneType(), "updateViewDetailsfor: ");
            if(infoneCategories.get(i).getCatId().equals(userDetails.getInfoneType())){
                Log.d(infoneCategoriesName.get(i), "updateViewDetails: ");
                userInfoneTypeSpinner.setText(infoneCategoriesName.get(i));
                infoneAdapter.notifyDataSetChanged();
                infoneTypeIndex=i;
            }
        }
        if(mImageUri==null) {
            userImageView.setImageURI(userDetails.getImageURL());
        }
        String skills = userDetails.getSkillTags();
        if (skills == null || skills.equalsIgnoreCase("[]")) {
            skills = "";
        }
        if (!skills.equals("") || skills.indexOf(',') > 0) {
            String[] skillsArray = skills.split(",");
            skillsArray[0] = skillsArray[0].substring(1);
            skillsArray[skillsArray.length - 1] = skillsArray[skillsArray.length - 1]
                    .substring(0, skillsArray[skillsArray.length - 1].length() - 1);
            userSkillTagsText.setTags(skillsArray);
        } else {
            userSkillTagsText.setText(skills);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if (!isNetworkAvailable(getApplicationContext())) {
                Snackbar snackbar = Snackbar.make(userAboutText, "No Internet. Can't Add Contact.", Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                snackbar.show();
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snack.show();
            } else{
                try {
                    attemptUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setSnapRadius(2)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    mImageUri = result.getUri();
                    mImageUriSmall = result.getUri();
                    if (mImageUri == null) {
                        Log.e(TAG, "onActivityResult: got empty imageUri");
                        return;
                    }
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    Bitmap bitmapSmall = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUriSmall);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    bitmapSmall.compress(Bitmap.CompressFormat.JPEG, 5, out);
                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    Bitmap bitmap2Small = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                    String path = MediaStore.Images.Media.insertImage(EditProfileActivity.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);
                    String pathSmall = MediaStore.Images.Media.insertImage(EditProfileActivity.this.getContentResolver(), bitmap2Small, mImageUri.getLastPathSegment(), null);
                    mImageUri = Uri.parse(path);
                    mImageUriSmall = Uri.parse(pathSmall);
                    userImageView.setImageURI(mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: ", result.getError());
            }
        }
    }

    public void attemptUpdate() throws IOException {
        mProgress.setMessage("Updating...");
        mProgress.show();

        userName = userNameText.getText().toString().trim();
        userEmail = userEmailText.getText().toString().trim();
        userAbout = userAboutText.getText().toString();
        userWhatsapp = userWhatsappNumberText.getText().toString();
        userMobile = userMobileNumberText.getText().toString();
        userSkillTags = userSkillTagsText.getTags().toString();
        anonymousUserName = anonymousUserNameET.getText().toString().trim();
        Boolean contactHidden = false;
        contactHidden = hideContactCB.isChecked();

        for (int i=0;i<infoneCategories.size();i++){
            if(infoneCategoriesName.get(i).equals(userInfoneTypeSpinner.getText().toString())){
                userInfoneType = infoneCategories.get(i).getCatId();
            }
        }

        Uri photoUri = null;
        for(UserInfo userInfo : mUser.getProviderData())
        {
            photoUri = userInfo.getPhotoUrl();
        }
        if (userName == null || userName.equals("") || anonymousUserName == null || anonymousUserName.equals("")
                || userEmail == null
                || userMobile.length() <10 || userWhatsapp.length() <10 || userInfoneType ==null || ((photoUri == null)&& mImageUri==null)) {

            if(userMobile.length()<10 || userWhatsapp.length() <10)
            {
                Snackbar snackbar = Snackbar.make(userAboutText, "Please check your contact details", Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                snackbar.show();
            }
            else
            {
                Snackbar snackbar = Snackbar.make(userAboutText, "Fields are empty. Can't Update details.", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();
            }
            mProgress.dismiss();
        } else {
            final DatabaseReference newPost = mUserReference;
            if(isReferred) {
                Log.d("RRRR", "is referred");
                DatabaseReference referredUserRef = FirebaseDatabase.getInstance().getReference().child("referredUsers").child(mUser.getUid());
                referredUserRef.child("status").setValue("converted");
                referredUserRef.child("meta").child("communityCode").setValue(communityReference);
                referredUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("RRRR", "Adding referredBy node in user in Users1");
                        newPost.child("referredBy").setValue(dataSnapshot.child("referredBy").getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("RRRR", "Adding referredBy node in user in Users1 failed.");
                    }
                });
            }
            newPost.child("username").setValue(userName);
            newPost.child("email").setValue(userEmail);
            newPost.child("mobileNumber").setValue(userMobile);
            newPost.child("anonymousUsername").setValue(anonymousUserName);
            if (userWhatsapp.length()==0){
                newPost.child("whatsAppNumber").setValue(" ");
            }else {
                newPost.child("whatsAppNumber").setValue(userWhatsapp);
            }
            if (userAbout == null){
                newPost.child("about").setValue(" ");
            }else {
                newPost.child("about").setValue(userAbout);
            }
            newPost.child("skillTags").setValue(userSkillTags);
            newPost.child("userUID").setValue(mUser.getUid().toString());
            newPost.child("infoneType").setValue(userInfoneType);
            newPost.child("contactHidden").setValue(contactHidden);

            //for infone
            newContactNumRef = databaseReferenceInfone.child("numbers").child(mUser.getUid());
            newContactRef = databaseReferenceInfone.child("categories").child(userInfoneType).child(mUser.getUid());

            postTimeMillis = System.currentTimeMillis();

            //Inside Categories
            newContactRef.child("name").setValue(userName);
            newContactRef.child("phone").child("0").setValue(userMobile);
            newContactRef.child("phone").child("1").setValue(userWhatsapp);
            newContactRef.child("key").child(mUser.getUid());
            newContactRef.child("contactHidden").setValue(contactHidden);

            //Inside Contacts
            newContactNumRef.child("category").setValue(userInfoneType);
            newContactNumRef.child("key").child(mUser.getUid());
            newContactNumRef.child("name").setValue(userName);
            newContactNumRef.child("phone").child("0").setValue(userMobile);
            newContactNumRef.child("phone").child("1").setValue(userWhatsapp);
            newContactNumRef.child("type").setValue("User");
            newContactNumRef.child("validCount").setValue(0);
            newContactNumRef.child("verifiedDate").setValue(postTimeMillis);
            newContactNumRef.child("PostTimeMillis").setValue(postTimeMillis);
            newContactNumRef.child("UID").setValue(mUser.getUid());
            if(userAbout!=null)
            {
                newContactNumRef.child("desc").setValue(userAbout);
                newContactRef.child("desc").setValue(userAbout);
            }
            else
            {
                newContactNumRef.child("desc").setValue("");
                newContactRef.child("desc").setValue("");
            }

            if(newUser){

                newPost.child("userPoints").setValue(0);
                newPost.child("notificationStatus").child("seenNotifications").setValue(0);
                newPost.child("notificationStatus").child("totalNotifications").setValue(0);

                SharedPreferences userVerification = getSharedPreferences("userType", MODE_PRIVATE);
                Boolean userTypeBoolean = userVerification.getBoolean("userVerification", false);

                if(userTypeBoolean){
                    newPost.child("userType").setValue(UsersTypeUtilities.KEY_VERIFIED);

                }else {
                    newPost.child("userType").setValue(UsersTypeUtilities.KEY_NOT_VERIFIED);
                }

                homePush = databaseHome.push();
                homePush.child("PostedBy").child("UID").setValue(mUser.getUid());
                homePush.child("PostedBy").child("Username").setValue(userName);
                homePush.child("feature").setValue("Users");
                homePush.child("communityName").setValue(communityName);
                homePush.child("PostTimeMillis").setValue(postTimeMillis);

                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("AddCabPool", true);
                taskMap.put("AddEvent", true);
                taskMap.put("AddForum",true);
                taskMap.put("EventBoosted", true);
                taskMap.put("StoreRoom", true);
                taskMap.put("Offers", true);
                newPost.child("NotificationChannels").setValue(taskMap);

                FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD + communityReference);
                FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD + communityReference);
                FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_OFFERS_ADD + communityReference);
                FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD + communityReference);
                FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD + communityReference);

                FirebaseMessaging.getInstance().subscribeToTopic(mUser.getUid());
            }


            if (mImageUri != null) {
                flag = false;
                final StorageReference filepath = mStorageRef.child("Users").child(mImageUri.getLastPathSegment() + mUser.getUid());
                UploadTask uploadTask = filepath.putFile(mImageUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            Uri downloadUri = task.getResult();
                            if (downloadUri == null) {
                                Log.e(TAG, "onSuccess: error got empty downloadUri");
                                return;
                            }
                            newPost.child("imageURL").setValue(downloadUri.toString());
                            newContactNumRef.child("imageurl").setValue(downloadUri.toString());

                            if (flag){
                                updateCurrentUser();
                                mProgress.dismiss();

                                if (newUser) {
                                    Intent intent = new Intent(getApplicationContext(), ExploreForumsActivity.class);
                                    intent.putExtra("newUser", true);
                                    startActivity(intent);
                                }
                                finish();
                            }else {
                                flag=true;
                            }
                        }
                        else {
                            // Handle failures
                            // ...
                            Snackbar snackbar = Snackbar.make(userAboutText, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                            snackbar.show();
                        }
                    }
                });
                final StorageReference filepathThumb = mStorageRef.child("PhonebookImageSmall").child(mImageUriSmall.getLastPathSegment() + mUser.getUid() + "Thumbnail");
                UploadTask uploadTaskThumb = filepathThumb.putFile(mImageUriSmall);
                uploadTaskThumb.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filepathThumb.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            Uri downloadUriThumb = task.getResult();
                            if (downloadUriThumb == null) {
                                Log.e(TAG, "onSuccess: error got empty downloadUri");
                                return;
                            }
                            if (newUser) {
                                homePush.child("PostedBy").child("ImageThumb").setValue(downloadUriThumb.toString());
                            }
                            newPost.child("imageURLThumbnail").setValue(downloadUriThumb.toString());
                            newContactRef.child("thumbnail").setValue(downloadUriThumb.toString());
                            newContactNumRef.child("thumbnail").setValue(downloadUriThumb.toString());
                            if (flag){
                                updateCurrentUser();
                                mProgress.dismiss();

                                if (newUser) {
                                    Intent intent = new Intent(getApplicationContext(), ExploreForumsActivity.class);
                                    intent.putExtra("newUser", true);
                                    startActivity(intent);
                                }
                                finish();
                            }else {
                                flag=true;
                            }
                        }
                        else {
                            // Handle failures
                            // ...
                            Snackbar snackbar = Snackbar.make(userAboutText, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                            snackbar.show();
                        }
                    }
                });
            }else{
                if (newUser) {
                    Uri photoUri2 = null;
                    for(UserInfo userInfo : mUser.getProviderData())
                    {
                        photoUri2 = userInfo.getPhotoUrl();
                    }
                    homePush.child("PostedBy").child("ImageThumb").setValue(photoUri2.toString());
                    newPost.child("imageURLThumbnail").setValue(photoUri2.toString());
                    newPost.child("imageURL").setValue(photoUri2.toString());
                    newContactNumRef.child("imageurl").setValue(photoUri2.toString());
                    newContactRef.child("thumbnail").setValue(photoUri2.toString());
                    newContactNumRef.child("thumbnail").setValue(photoUri2.toString());

                    Intent intent = new Intent(this, ExploreForumsActivity.class);
                    intent.putExtra("newUser", true);
                    startActivity(intent);

                }
                updateCurrentUser();
                mProgress.dismiss();
                finish();
            }
        }
    }

    public void updateCurrentUser(){
        DatabaseReference currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        currentUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserUtilities.currentUser = dataSnapshot.getValue(UserItemFormat.class);
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
            //skillTags.showDropDown();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_image_view: {
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                }
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                break;
            }
        }
    }

    @Override
    public void onTagsChanged(Collection<String> collection) {
        /*required*/
    }

    @Override
    protected void onDestroy() {
        mProgress.dismiss(); /*Fix for window leak*/
        super.onDestroy();
    }

    @Override
    public void onEditingFinished() {
        /*required*/
    }
}
