package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class InfoneProfileActivity extends BaseActivity {

    /*UI elements*/
    private MaterialEditText nameEt;
    private String userType="No";
    //private TextView desc;
    MaterialEditText phone1Et;
    MaterialEditText phone2Et;
    Button saveEditBtn;
    SimpleDraweeView profileImage;
    Toolbar toolbar;
    private Menu menu;
    private Button validButton;
    private TextView verifiedDateTextView, validLabel;
    private String verfiedDate;
    private Long postTimeMillis;
    ImageButton phone1EtCallbtn;

    /*image uploading elements*/
    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 7;
    private Uri mImageUriSmall;
    private StorageReference mStorageRef;

    ArrayList<String> phoneNums;
    /*user id of the current infone contact in /infone/numbers */
    String infoneUserId;

    /*DB elements*/
    DatabaseReference databaseReferenceContact;
    DatabaseReference databaseReferenceInfone;
    ValueEventListener listener;
    DatabaseReference databaseRefEdit;
    DatabaseReference databaseRefEditNum;

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

    private final String TAG = getClass().getSimpleName();
    private String catId;
    LinearLayout linearLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.infone_profile_progress_circle);
        linearLayout = (LinearLayout) findViewById(R.id.infone_profile_linear_layout);
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        nameEt = (MaterialEditText) findViewById(R.id.et_name_infone_profile);
        profileImage = (SimpleDraweeView) findViewById(R.id.image_profile_infone);
        phone1Et = (MaterialEditText) findViewById(R.id.et_phone1_infone_profile);
        phone1EtCallbtn = (ImageButton) findViewById(R.id.infone_profile_callbtn);
        phone2Et = (MaterialEditText) findViewById(R.id.et_phone2_infone_profile);
        saveEditBtn = (Button) findViewById(R.id.save_edit_infone_profile);
        validLabel = (TextView) findViewById(R.id.valid_label);
        validButton = (Button) findViewById(R.id.valid_button);
        verifiedDateTextView = (TextView) findViewById(R.id.verified_date);
        viewProfileButton = (Button) findViewById(R.id.viewProfileButton);

        nameEt.setEnabled(false);
        phone1Et.setEnabled(false);
        phone2Et.setEnabled(false);
        profileImage.setEnabled(false);
        saveEditBtn.setVisibility(View.GONE);

        infoneUserId = getIntent().getExtras().getString("infoneUserId");

        Log.e(InfoneProfileActivity.class.getName(), "data :" + infoneUserId);

        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceInfone=FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW);
        databaseReferenceContact = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW).child("numbers").child(infoneUserId);
        mDatabaseViews = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW).child("numbers").child(infoneUserId).child("views");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Log.e(TAG, "data comRef:" + communityReference);

        updateViews();
        Log.e(TAG, "inside" + infoneUserId);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                nameEt.setText(name);
                toolbar.setTitle(name);

                String imageThumb = dataSnapshot.child("thumbnail").getValue(String.class);
                String imageUrl = dataSnapshot.child("imageurl").getValue(String.class);
                catId = dataSnapshot.child("catId").getValue(String.class);
                validButton.setText(dataSnapshot.child("validCount").getValue().toString() + " validations");

                userType = dataSnapshot.child("type").getValue(String.class);
                verfiedDate = dataSnapshot.child("verifiedDate").getValue().toString();
                TimeUtilities ta = new TimeUtilities(Long.parseLong(verfiedDate), System.currentTimeMillis());
                verifiedDateTextView.setText(ta.calculateTimeAgo());

                if (userType.equals("User")) {
                    menu.findItem(R.id.action_edit).setVisible(false);
                    viewProfileButton.setVisibility(View.VISIBLE);
                    viewProfileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(InfoneProfileActivity.this,OpenUserDetail.class);
                            i.putExtra("Uid",dataSnapshot.child("UID").getValue().toString());
                            startActivity(i);
                        }
                    });
                }
                //setting image if not default
                if (imageUrl != null && !imageUrl.equalsIgnoreCase("default")) {
                    Uri imageUri = Uri.parse(imageUrl);
                    profileImage.setImageURI(imageUri);
                }

                if (dataSnapshot.child("valid").hasChild(mAuth.getCurrentUser().getUid())){
                    flag=true;
                    validButton.setBackground(getResources().getDrawable(R.drawable.roundradius));
                    validButton.setTextColor(getResources().getColor(R.color.primaryText));
                }else {
                    validButton.setBackgroundColor(getResources().getColor(R.color.infone));
                    validButton.setTextColor(getResources().getColor(R.color.white));
                    flag=false;
                }

                databaseReferenceContact.child("validCount").setValue(dataSnapshot.child("valid").getChildrenCount());


                phoneNums = new ArrayList<>();
                DataSnapshot dataSnapshot1 = dataSnapshot.child("phone");
                for (DataSnapshot childSnapshot :
                        dataSnapshot1.getChildren()) {
                    String phone = childSnapshot.getValue(String.class);
                    phoneNums.add(phone);
                }

                phone1Et.setText(phoneNums.get(0));
                phone2Et.setText(phoneNums.get(1));
                verifiedDateTextView.setText(ta.calculateTimeAgo());

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
                if (!phone1Et.getText().toString().isEmpty())
                    makeCall(phone1Et.getText().toString());
            }
        });
        phone1EtCallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (!phone2Et.getText().toString().isEmpty())
                    makeCall(phone2Et.getText().toString());
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

        saveEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdits();
            }
        });

        validButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    databaseReferenceInfone.child("numbers").child(infoneUserId).child("valid").child(mAuth.getCurrentUser().getUid()).removeValue();
                }else {
                    databaseReferenceInfone.child("numbers").child(infoneUserId).child("valid").child(mAuth.getCurrentUser().getUid()).setValue("true");
                }
                postTimeMillis = System.currentTimeMillis();
                databaseReferenceContact.child("verifiedDate").setValue(postTimeMillis);
            }
        });
    }


    private void editProfile() {

        nameEt.setEnabled(true);
        phone1Et.setEnabled(true);
        phone2Et.setEnabled(true);
        profileImage.setEnabled(true);
        saveEditBtn.setVisibility(View.VISIBLE);
        validButton.setVisibility(View.GONE);
        validLabel.setVisibility(View.GONE);

        verifiedDateTextView.setVisibility(View.GONE);
    }

    private void saveEdits() {

        databaseRefEdit=databaseReferenceInfone.child("categories").child(catId).child(infoneUserId);
        databaseRefEditNum=databaseReferenceContact;

        String name=nameEt.getText().toString();
        String phone1=phone1Et.getText().toString();
        String phone2=phone2Et.getText().toString();

        if (phone1.isEmpty() && !phone2.isEmpty()) {
            phone1 = phone2;
            phone2 = "";
        }

        if(!name.isEmpty() && !phone1.isEmpty()){
            databaseRefEditNum.child("name").setValue(name);
            databaseRefEditNum.child("phone").child("0").setValue(phone1);
            databaseRefEditNum.child("phone").child("1").setValue(phone2);
            databaseRefEdit.child("name").setValue(name);
            databaseRefEdit.child("phone").child("0").setValue(phone1);
            databaseRefEdit.child("phone").child("1").setValue(phone2);
            databaseRefEditNum.child("category").setValue(catId);
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
                        if (downloadUri == null) {
                            Log.e(TAG, "onSuccess: error got empty downloadUri");
                            return;
                        }
                        //newContactRef.child("imageurl").setValue(downloadUri.toString());
                        databaseRefEditNum.child("imageurl").setValue(downloadUri.toString());
                        finish();
                    }
                    else {
                        // Handle failures
                        // ...
                        Snackbar.make(nameEt, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
            final StorageReference filepathThumb = mStorageRef.child("InfoneImageSmall").child(mImageUriSmall.getLastPathSegment() + infoneUserId + "Thumbnail");
            UploadTask uploadTaskThumb = filepathThumb.putFile(mImageUriSmall);
            uploadTaskThumb.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
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
                        databaseRefEdit.child("thumbnail").setValue(downloadUriThumb.toString());
                        databaseRefEditNum.child("thumbnail").setValue(downloadUriThumb.toString());
                        finish();
                    }
                    else {
                        // Handle failures
                        // ...
                        Snackbar.make(nameEt, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT).show();
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
        databaseReferenceContact.addValueEventListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        this.menu=menu;

        getMenuInflater().inflate(R.menu.menu_edit_infone_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_edit) {
                editProfile();
            }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }

}