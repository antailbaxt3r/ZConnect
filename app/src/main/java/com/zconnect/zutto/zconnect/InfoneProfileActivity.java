package com.zconnect.zutto.zconnect;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.addActivities.CreateForum;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.zconnect.zutto.zconnect.R.drawable.ic_arrow_back_black_24dp;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class InfoneProfileActivity extends BaseActivity {

    // TODO: Remove hardcoded paths

    /*UI elements*/
    private MaterialEditText nameEt;
    private TextView descTv;
    private String userType = "No";
    //private TextView desc;
    MaterialEditText phone1Et;
    MaterialEditText phone2Et;
    //    Button saveEditBtn;
    SimpleDraweeView profileImage;
    Toolbar toolbar;
    private Menu menu;
    private TextView verifiedDateTextView, validLabel;
    private String verfiedDate;
    private Long postTimeMillis;
    private RelativeLayout phone1Etrl;
    private RelativeLayout whatsApprl;

    /*image uploading elements*/
    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 7;
    private Uri mImageUriSmall;
    private StorageReference mStorageRef;

    private String userImageURL;

    ArrayList<String> phoneNums;
    public static boolean isActivityRunning;
    /*user id of the current infone contact in /infone/numbers */
    String infoneUserId, catID;

    /*DB elements*/
    DatabaseReference databaseReferenceContact;
    DatabaseReference databaseReferenceInfone;
    ValueEventListener listener;
    DatabaseReference databaseRefEdit;
    DatabaseReference databaseRefEditNum;
    DatabaseReference databaseReferenceUser;
    DatabaseReference databaseReferenceInfoneUser;

    /*to get current community*/
    private SharedPreferences communitySP;
    public String communityReference;

    /*db elements needed for views calculation*/
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ValueEventListener listenerView;
    private DatabaseReference mDatabaseViews;
    private Boolean flag;
    private Button viewProfileButton;

    //Elements for call verification(All belong to the popup dialog
    private static boolean hasCalled = false;
    Dialog verifyDialog;
    Button dialogVerifyYesbtn;
    Button dialogVerifyNobtn;
    MaterialEditText dialogVerifyphoneEt;
    MaterialEditText dialogVerifyNameEt;
    SimpleDraweeView dialogVerifyProfileImg;

    //Elements for VerifiedProgress
    LinearLayout validatePercentYesLl;
    LinearLayout validatePercentNoLl;
    LinearLayout validatePercentLl;


    //Elements for valid/invalid
    private RelativeLayout validrl; //Encloses
    private LinearLayout validLl;
    private Button validButton;
    private Button invalidButton;
    private TextView thankYou;

    private TextView infoneCategory;

    //Elements for personal chat
    RelativeLayout personalChat;
    private String infoneUserUID;


    UserItemFormat infoneUserDetails = new UserItemFormat();


    private final String TAG = getClass().getSimpleName();
    private String name, desc, mobileNumber;
    LinearLayout linearLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ic_arrow_back_black_24dp);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
        

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

        progressBar = (ProgressBar) findViewById(R.id.infone_profile_progress_circle);
        linearLayout = (LinearLayout) findViewById(R.id.infone_profile_linear_layout);
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
        whatsApprl = findViewById(R.id.whatsappll);
        nameEt = (MaterialEditText) findViewById(R.id.et_name_infone_profile);
        descTv = (TextView) findViewById(R.id.tv_desc_infone_profile);
        profileImage = (SimpleDraweeView) findViewById(R.id.image_profile_infone);
        phone1Et = (MaterialEditText) findViewById(R.id.et_phone1_infone_profile);
        phone1Etrl = findViewById(R.id.phone1ll);
        phone2Et = (MaterialEditText) findViewById(R.id.et_phone2_infone_profile);
//        saveEditBtn = (Button) findViewById(R.id.save_edit_infone_profile);
        validLabel = (TextView) findViewById(R.id.valid_label);
        validButton = (Button) findViewById(R.id.valid_button);
        verifiedDateTextView = (TextView) findViewById(R.id.verified_date);
        viewProfileButton = (Button) findViewById(R.id.viewProfileButton);
        validatePercentLl = findViewById(R.id.validate_percent);
        validatePercentNoLl = findViewById(R.id.validate_percent_no);
        validatePercentYesLl = findViewById(R.id.validate_percent_yes);
        personalChat = findViewById(R.id.personalchatrl);
        nameEt.setEnabled(false);
        phone1Et.setEnabled(false);
        phone2Et.setEnabled(false);
        profileImage.setEnabled(false);
//        saveEditBtn.setVisibility(View.GONE);

        infoneUserId = getIntent().getExtras().getString("infoneUserId");
        infoneUserDetails.setUserUID(infoneUserId);
//        Log.d("infoneUserImageThumb",getIntent().getExtras().getString("infoneUserImageThumb"));
        infoneUserDetails.setImageURL(getIntent().getExtras().getString("infoneUserImageThumb"));

        catID = getIntent().getExtras().getString("catID");
        Log.e(InfoneProfileActivity.class.getName(), "data :" + infoneUserId);

        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);


        databaseReferenceInfone = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW);
        databaseReferenceContact = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW).child("numbers").child(infoneUserId);
        mDatabaseViews = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW).child("numbers").child(infoneUserId).child("views");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        validrl = findViewById(R.id.valid_rl);
        validLl = findViewById(R.id.valid_ll);
        invalidButton = findViewById(R.id.invalid_button);
        thankYou = findViewById(R.id.thank_you_tv);
        infoneCategory = findViewById(R.id.infonecategory);

        verifyDialog = new Dialog(this);
        verifyDialog.setContentView(R.layout.dialog_validate_number);
        verifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogVerifyYesbtn = verifyDialog.findViewById(R.id.validate_infone_yes_btn);
        dialogVerifyNobtn = verifyDialog.findViewById(R.id.validate_infone_no_btn);
        dialogVerifyNameEt = verifyDialog.findViewById(R.id.et_name_infone_profile);
        dialogVerifyphoneEt = verifyDialog.findViewById(R.id.et_phone1_infone_profile);
        dialogVerifyProfileImg = verifyDialog.findViewById(R.id.image_profile_infone);
        dialogVerifyYesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReferenceInfone.child("numbers").child(infoneUserId).child("invalid").child(mAuth.getCurrentUser().getUid()).removeValue();
                databaseReferenceInfone.child("numbers").child(infoneUserId).child("valid").child(mAuth.getCurrentUser().getUid()).setValue("true");
                verifyDialog.dismiss();

            }
        });
        dialogVerifyNobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReferenceInfone.child("numbers").child(infoneUserId).child("valid").child(mAuth.getCurrentUser().getUid()).removeValue();
                databaseReferenceInfone.child("numbers").child(infoneUserId).child("invalid").child(mAuth.getCurrentUser().getUid()).setValue("true");
                verifyDialog.dismiss();

            }
        });

        personalChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Try", "clicked");
                if (databaseReferenceUser == null) {
                    Toast.makeText(v.getContext(), "The user does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
                databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.child("userChats").hasChild(infoneUserUID)) {
                            userImageURL = dataSnapshot.child("imageURL").getValue().toString();
                            Log.d("Try", createPersonalChat(mAuth.getCurrentUser().getUid(), infoneUserUID));
                        }
                        databaseReferenceUser.child("userChats").child(infoneUserUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String key = dataSnapshot.getValue().toString();
                                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                                intent.putExtra("type", "forums");
                                intent.putExtra("name", name);
                                intent.putExtra("tab", "personalChats");
                                intent.putExtra("key", key);
                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        Log.e(TAG, "data comRef:" + communityReference);

        updateViews();
        Log.e(TAG, "insidethekjld" + catID);

        databaseReferenceInfone.child("categoriesInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                infoneCategory.setText(dataSnapshot.child(catID).child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                desc = dataSnapshot.child("desc").getValue(String.class);
                nameEt.setText(name);
                dialogVerifyNameEt.setText(name);
                toolbar.setTitle("Contact Details");

                databaseReferenceContact.child("validCount").setValue(dataSnapshot.child("valid").getChildrenCount());
                databaseReferenceContact.child("invalidCount").setValue(dataSnapshot.child("invalid").getChildrenCount());


//                if(desc==null && dataSnapshot.child("type").getValue(String.class).equals("User"))
//                {
//                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB).child(communityReference)
//                            .child(ZConnectDetails.USERS_DB).child(dataSnapshot.getKey());
//                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshotUser) {
//                            if(!dataSnapshotUser.child("about").getValue(String.class).isEmpty())
//                            {
//                                desc = dataSnapshotUser.child("about").getValue(String.class);
//                            }
//                            if(desc==null)
//                            {
//                                descTv.setVisibility(View.GONE);
//                            }
//                            else
//                            {
//                                descTv.setVisibility(View.VISIBLE);
//                                descTv.setText(desc);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//                else
//                {
                if (desc == null) {
                    descTv.setVisibility(View.GONE);
                    Log.i("BBBB", "NULL");
                } else {
                    descTv.setVisibility(View.VISIBLE);
                    descTv.setText(desc);
                    Log.i("BBBB", "NOT NULL");
                }
//                }
//                validrl.setVisibility(View.VISIBLE);
//                    validLl.setVisibility(View.VISIBLE);
//                if(dataSnapshot.child("valid").hasChild(mAuth.getCurrentUser().getUid()) || dataSnapshot.child("invalid").hasChild(mAuth.getCurrentUser().getUid())){
//                    validrl.setVisibility(View.INVISIBLE);
//                }
                String imageThumb = dataSnapshot.child("thumbnail").getValue(String.class);
                infoneUserDetails.setImageURLThumbnail(imageThumb);
                String imageUrl = dataSnapshot.child("imageurl").getValue(String.class);
                validLabel.setText(dataSnapshot.child("validCount").getValue().toString() + " validations");

                int validCounts = Integer.parseInt(dataSnapshot.child("validCount").getValue().toString());
                int invalidCounts = 0;
                if (dataSnapshot.hasChild("invalidCount")) {
                    invalidCounts = Integer.parseInt(dataSnapshot.child("invalidCount").getValue().toString());
                } else {
                    databaseReferenceContact.child("invalidCount").setValue(0);
                }

                int totalCounts = validCounts + invalidCounts;
                validatePercentLl.setVisibility(View.VISIBLE);
                if (totalCounts < 1) {
                    validatePercentLl.setVisibility(View.GONE);
                } else {
                    LinearLayout.LayoutParams yesLlLayoutParams = (LinearLayout.LayoutParams) validatePercentYesLl.getLayoutParams();
                    yesLlLayoutParams.weight = (float) validCounts / (float) totalCounts;
                    validatePercentYesLl.setLayoutParams(yesLlLayoutParams);

                    LinearLayout.LayoutParams noLlLayoutParams = (LinearLayout.LayoutParams) validatePercentNoLl.getLayoutParams();
                    noLlLayoutParams.weight = 1 - yesLlLayoutParams.weight;
                    validatePercentNoLl.setLayoutParams(noLlLayoutParams);
                }
                if (dataSnapshot.child("validCount").getValue().toString().equals("0")) {
                    validLabel.setText("No Validations yet");

                } else if (dataSnapshot.child("validCount").getValue().toString().equals("1")) {
                    validLabel.setText(dataSnapshot.child("validCount").getValue().toString() + " validation");

                }


                userType = dataSnapshot.child("type").getValue(String.class);
                infoneUserDetails.setUserType(userType);
                Log.d("date", String.valueOf(dataSnapshot.getRef()));
                verfiedDate = dataSnapshot.child("verifiedDate").getValue().toString();
                TimeUtilities ta = new TimeUtilities(Long.parseLong(verfiedDate), System.currentTimeMillis());
                verifiedDateTextView.setText(ta.calculateTimeAgo());

                if (userType.equals("User")) {
                    if (menu != null) {
                        menu.findItem(R.id.action_edit).setVisible(false);
                    }
                    infoneUserUID = dataSnapshot.child("UID").getValue().toString();
                    viewProfileButton.setVisibility(View.VISIBLE);
                    personalChat.setVisibility(View.VISIBLE);
                    databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                            .child(communityReference).child(ZConnectDetails.USERS_DB).child(mAuth.getCurrentUser().getUid());
                    databaseReferenceInfoneUser = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                            .child(communityReference).child(ZConnectDetails.USERS_DB).child(infoneUserUID);
                    viewProfileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();

                            meta.put("type", "fromInfone");
                            meta.put("userID", dataSnapshot.child("UID").getValue().toString());
                            meta.put("catID", catID);

                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);

                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();

                            Intent i = new Intent(InfoneProfileActivity.this, OpenUserDetail.class);
                            i.putExtra("Uid", dataSnapshot.child("UID").getValue().toString());
//                            Log.d("AAKKHHIILL", dataSnapshot.child("UID").getValue().toString());
                            startActivity(i);
                        }
                    });
                }
                //setting image if not default
                if (imageUrl != null && !imageUrl.equalsIgnoreCase("default")) {
                    Uri imageUri = Uri.parse(imageUrl);
                    profileImage.setImageURI(imageUri);
                    dialogVerifyProfileImg.setImageURI(imageUri);


                }
                flag = dataSnapshot.child("valid").hasChild(mAuth.getCurrentUser().getUid()) || dataSnapshot.child("invalid").hasChild(mAuth.getCurrentUser().getUid());


                phoneNums = new ArrayList<>();
                DataSnapshot dataSnapshot1 = dataSnapshot.child("phone");
                for (DataSnapshot childSnapshot :
                        dataSnapshot1.getChildren()) {
                    String phone = childSnapshot.getValue(String.class);

                    phoneNums.add(phone);
                }

                phone1Et.setText(phoneNums.get(0));
                dialogVerifyphoneEt.setText(phoneNums.get(0));
                mobileNumber = phoneNums.get(0);
                phone2Et.setText(phoneNums.get(1));
                verifiedDateTextView.setText(ta.calculateTimeAgo());
                if (phone2Et.getText().toString().length() < 9) {
                    whatsApprl.setVisibility(View.GONE);
                }
                if (phone1Et.getText().toString().length() < 9) {
                    phone1Etrl.setVisibility(View.GONE);
                }

                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error :" + databaseError.toString());
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        };


        phone1Et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone1Et.getText().toString().isEmpty()) {
                    callCounter();
                    makeCall(phone1Et.getText().toString());
                    Log.d("InfoneProfileActivity", phone1Et.getText().toString());
                }
            }
        });


        whatsApprl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone2Et.getText().toString().length() < 10) {

                    Toast.makeText(InfoneProfileActivity.this, "WhatsApp number does not exist.", Toast.LENGTH_SHORT).show();
                    return;

                }
                redirectToWhatsApp(phone2Et.getText().toString());
            }
        });

        phone1Etrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callCounter();
                hasCalled = true;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone1Et.getText().toString()));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider cal
                    // ling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

                Toast.makeText(getApplicationContext(), "call being made to " + phone1Et.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        phone2Et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone2Et.getText().toString().isEmpty()) {
                    makeCall(phone2Et.getText().toString());
                    callCounter();
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(InfoneProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            InfoneProfileActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                }
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

//        saveEditBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveEdits();
//            }
//        });

        validButton.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                    .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW).child("numbers").child(infoneUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String uid = dataSnapshot.child("UID").getValue().toString();
                    databaseReferenceInfone.child("numbers").child(infoneUserId).child("valid").child(mAuth.getCurrentUser().getUid()).setValue("true");
                    databaseReferenceInfone.child("numbers").child(infoneUserId).child("invalid").child(mAuth.getCurrentUser().getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserItemFormat userItemFormat = new UserItemFormat();
                            HashMap<String, Object> metadata = new HashMap<>();
                            userItemFormat.setUsername((String) dataSnapshot.child("username").getValue());
                            userItemFormat.setUserUID((String) dataSnapshot.child("userUID").getValue());
                            userItemFormat.setImageURL((String) dataSnapshot.child("imageURL").getValue());
                            metadata.put("catID",catID);
                            GlobalFunctions.inAppNotifications("has validated your phone number",phoneNums.get(0),userItemFormat,false,"infonevalidate",metadata,uid);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            postTimeMillis = System.currentTimeMillis();
            databaseReferenceContact.child("verifiedDate").setValue(postTimeMillis);

            CounterItemFormat counterItemFormat = new CounterItemFormat();
            HashMap<String, String> meta = new HashMap<>();

            meta.put("catID", catID);

            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
            counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_VALIDATE);
            counterItemFormat.setTimestamp(System.currentTimeMillis());
            counterItemFormat.setMeta(meta);

            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
            counterPush.pushValues();
//                displayThankYou();
        });
        invalidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                        .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW).child("numbers").child(infoneUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        databaseReferenceInfone.child("numbers").child(infoneUserId).child("invalid").child(mAuth.getCurrentUser().getUid()).setValue("true");
                        databaseReferenceInfone.child("numbers").child(infoneUserId).child("valid").child(mAuth.getCurrentUser().getUid()).removeValue();
                        postTimeMillis = System.currentTimeMillis();
                        databaseReferenceContact.child("verifiedDate").setValue(postTimeMillis);

                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserItemFormat userItemFormat = new UserItemFormat();
                                HashMap<String,Object> metadata = new HashMap<>();
                                userItemFormat.setUsername((String) dataSnapshot.child("username").getValue());
                                userItemFormat.setUserUID((String) dataSnapshot.child("userUID").getValue());
                                userItemFormat.setImageURL((String) dataSnapshot.child("imageURL").getValue());
                                metadata.put("catID",catID);
                                GlobalFunctions.inAppNotifications("has invalidated your phone number",phoneNums.get(0),userItemFormat,false,"infoneinvalidate",metadata,infoneUserUID);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                displayThankYou();

            }
        });
    }

    private String createPersonalChat(final String uid, final String infoneUserUID) {
        final DatabaseReference databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories");
        final DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child("personalChats");
        final DatabaseReference newPush = databaseReferenceCategories.push();
//        final DatabaseReference databaseReferenceUserForums = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("userForums");
        newPush.child("name").setValue(false);
        Long postTimeMillis = System.currentTimeMillis();
        newPush.child("PostTimeMillis").setValue(postTimeMillis);
        newPush.child("UID").setValue(newPush.getKey());
        newPush.child("tab").setValue("personalChats");
        newPush.child("Chat");
        final UserItemFormat[] user = {null};



        databaseReferenceInfoneUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);

                UsersListItemFormat userDetails = new UsersListItemFormat();
                userDetails.setImageThumb(userItem.getImageURLThumbnail());
                userDetails.setName(userItem.getUsername());
                userDetails.setPhonenumber(userItem.getMobileNumber());
                userDetails.setUserUID(userItem.getUserUID());
                userDetails.setUserType(ForumsUserTypeUtilities.KEY_ADMIN);

                HashMap<String,UsersListItemFormat> userList = new HashMap<String,UsersListItemFormat>();
                userList.put(infoneUserUID,userDetails);
                Log.d("USEROBJECT",UserUtilities.currentUser.toString());
                UsersListItemFormat currentUser = new UsersListItemFormat();
                currentUser.setImageThumb(UserUtilities.currentUser.getImageURLThumbnail());
                currentUser.setName(UserUtilities.currentUser.getUsername());
                currentUser.setPhonenumber(UserUtilities.currentUser.getMobileNumber());
                currentUser.setUserUID(UserUtilities.currentUser.getUserUID());
                currentUser.setUserType(ForumsUserTypeUtilities.KEY_ADMIN);
                userList.put(uid,currentUser);
//                databaseReferenceTabsCategories.child(newPush.getKey()).child("users").setValue(userList);
//                databaseReferenceUserForums.child(uid).child("joinedForums").child(newPush.getKey()).child("image").setValue(userItem.getImageURL());
                HashMap<String,Object> forumTabs = new HashMap<>();
                forumTabs.put("name",false);
                forumTabs.put("catUID",newPush.getKey());
                forumTabs.put("tabUID","personalChats");
                forumTabs.put("lastMessage","Null");
                forumTabs.put("users",userList);
                databaseReferenceTabsCategories.child(newPush.getKey()).setValue(forumTabs);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
        //TODO HANDLE NO USER IMAGE



        //updating value in userchats in users1
        databaseReferenceUser.child("userChats").child(infoneUserUID).setValue(newPush.getKey());
        databaseReferenceInfoneUser.child("userChats").child(uid).setValue(newPush.getKey());




        return newPush.getKey();

    }

    private void displayThankYou() {
        validLl.setVisibility(View.GONE);
        thankYou.setVisibility(View.VISIBLE);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        thankYou.startAnimation(aniFade);
    }

    public void callCounter() {
        CounterItemFormat counterItemFormat = new CounterItemFormat();
        HashMap<String, String> meta = new HashMap<>();

        meta.put("type", "fromInfoneProfile");
        meta.put("catID", catID);

        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_CALL);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        counterItemFormat.setMeta(meta);

        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();
    }

    private void shareProfile() {

        CounterItemFormat counterItemFormat = new CounterItemFormat();
        HashMap<String, String> meta = new HashMap<>();
        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_SHARE);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        counterItemFormat.setMeta(meta);
        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();

        String send = "";
        send = "Name: " + name + "\n" + "Number: " + mobileNumber + "\n \nShared using ZConnect. \nDownlaod ZConnect now, to access all contacts of your community" + "\n \nhttps://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect";
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, send);
        startActivity(sharingIntent);
    }

    private void editProfile() {

        nameEt.setEnabled(true);
        phone1Et.setEnabled(true);
        phone2Et.setEnabled(true);
        profileImage.setEnabled(true);
//        saveEditBtn.setVisibility(View.VISIBLE);
        validButton.setVisibility(View.GONE);
        validLabel.setVisibility(View.GONE);

        verifiedDateTextView.setVisibility(View.GONE);
    }

    private void saveEdits() {

        databaseRefEdit = databaseReferenceInfone.child("categories").child(catID).child(infoneUserId);
        databaseRefEditNum = databaseReferenceContact;

        String name = nameEt.getText().toString();
        String phone1 = phone1Et.getText().toString();
        String phone2 = phone2Et.getText().toString();

        if (phone1.isEmpty() && !phone2.isEmpty()) {
            phone1 = phone2;
            phone2 = "";
        }

        if (!name.isEmpty() && !phone1.isEmpty()) {
            databaseRefEditNum.child("name").setValue(name);
            databaseRefEditNum.child("phone").child("0").setValue(phone1);
            databaseRefEditNum.child("phone").child("1").setValue(phone2);
            databaseRefEdit.child("name").setValue(name);
            databaseRefEdit.child("phone").child("0").setValue(phone1);
            databaseRefEdit.child("phone").child("1").setValue(phone2);
            databaseRefEditNum.child("category").setValue(catID);
            uploadImage();

        }

        validButton.setVisibility(View.VISIBLE);
        validLabel.setVisibility(View.VISIBLE);
        verifiedDateTextView.setVisibility(View.VISIBLE);

    }


    private void uploadImage() {

        if (mImageUri != null) {
            final StorageReference filepath = mStorageRef.child("InfoneImage").child(mImageUri.getLastPathSegment() + infoneUserId);
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
                        Uri downloadUri = task.getResult();
                        if (downloadUri == null) {
                            Log.e(TAG, "onSuccess: error got empty downloadUri");
                            return;
                        }
                        //newContactRef.child("imageurl").setValue(downloadUri.toString());
                        databaseRefEditNum.child("imageurl").setValue(downloadUri.toString());
                        finish();
                    } else {
                        // Handle failures
                        // ...
                        Snackbar snackbar = Snackbar.make(nameEt, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });
            final StorageReference filepathThumb = mStorageRef.child("InfoneImageSmall").child(mImageUriSmall.getLastPathSegment() + infoneUserId + "Thumbnail");
            UploadTask uploadTaskThumb = filepathThumb.putFile(mImageUriSmall);
            uploadTaskThumb.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepathThumb.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUriThumb = task.getResult();
                        if (downloadUriThumb == null) {
                            Log.e(TAG, "onSuccess: error got empty downloadUri");
                            return;
                        }
                        databaseRefEdit.child("thumbnail").setValue(downloadUriThumb.toString());
                        databaseRefEditNum.child("thumbnail").setValue(downloadUriThumb.toString());
                        finish();
                    } else {
                        // Handle failures
                        // ...
                        Snackbar snackbar = Snackbar.make(nameEt, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });
        } else {
            databaseRefEditNum.child("imageurl").setValue("default");
            databaseRefEdit.child("thumbnail").setValue("default");
            databaseRefEditNum.child("thumbnail").setValue("default");
            finish();
        }
    }

    private void redirectToWhatsApp(String number) {
        PackageManager pm = getPackageManager();
        try {
            String text = "";

            String toNumber = "91" + number;


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + toNumber + "&text=" + text));
            startActivity(intent);
        } catch (Exception e) {
            Log.d("InfoneProfileActivity", e.toString());
        }

    }

    private void makeCall(String number) {

        String strName = number;


        // to make a call at mobileNumber
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + strName));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider cal
            // ling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        hasCalled = flag;
        ;
        startActivity(intent);

        Toast.makeText(this, "call being made to " + strName, Toast.LENGTH_SHORT).show();

    }

    //this function will update the views of people who have visited this activity
    private void updateViews() {

        SharedPreferences sharedPref = this.getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            listenerView = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    boolean userExists = false;
                    for (DataSnapshot childSnapshot :
                            dataSnapshot.getChildren()) {
                        if (childSnapshot.getKey().equals(user.getUid()) && childSnapshot.exists() &&
                                childSnapshot.getValue(Integer.class) != null) {
                            userExists = true;
                            int originalViews = childSnapshot.getValue(Integer.class);
                            mDatabaseViews.child(user.getUid()).setValue(originalViews + 1);
                            Log.e(TAG, "inside" + originalViews);
                            break;
                        } else {
                            userExists = false;
                        }
                    }
                    if (!userExists) {
                        mDatabaseViews.child(user.getUid()).setValue(1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error :" + databaseError.toString());
                }
            };

            mDatabaseViews.addListenerForSingleValueEvent(listenerView);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityRunning = true;
        databaseReferenceContact.addValueEventListener(listener);
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (hasCalled) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isActivityRunning){
                    verifyDialog.show();
                    hasCalled = false;
                    }
                }
            }, 5000);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
        databaseReferenceContact.removeEventListener(listener);
        mDatabaseViews.removeEventListener(listenerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setBackgroundColor(R.color.white)
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
                    String path = MediaStore.Images.Media.insertImage(InfoneProfileActivity.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);
                    String pathSmall = MediaStore.Images.Media.insertImage(InfoneProfileActivity.this.getContentResolver(), bitmap2Small, mImageUri.getLastPathSegment(), null);
                    mImageUri = Uri.parse(path);
                    mImageUriSmall = Uri.parse(pathSmall);
                    profileImage.setImageURI(mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: ", result.getError());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_edit_infone_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_edit) {
            editProfile();
        } else if (item.getItemId() == R.id.action_share) {
            shareProfile();
        }

        return super.onOptionsItemSelected(item);
    }

}