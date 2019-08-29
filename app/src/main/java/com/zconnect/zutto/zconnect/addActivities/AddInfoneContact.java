package com.zconnect.zutto.zconnect.addActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.InfoneContactListActivity;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.PermissionUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddInfoneContact extends BaseActivity {
    int count=0;
    ArrayList<String> arrayList = new ArrayList<>();
    String catId,catName,catImageURL,catAdmin;
    int totalContacts;
    private SharedPreferences communitySP;
    public String communityReference;
    DatabaseReference databaseReferenceInfone;
    DatabaseReference databaseRecents;
    DatabaseReference newContactRef;
    MaterialEditText nameEt;
    MaterialEditText phone1Et, phone2Et, descEt;
    Button saveButton;
    DatabaseReference databaseReference;
    SimpleDraweeView addImage;
    String key;
    private Long postTimeMillis;
    private final String TAG = getClass().getSimpleName();
    private Boolean flag1,flag2;

    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 7;
    private Uri mImageUriSmall;
    private StorageReference mStorageRef;
    private DatabaseReference newContactNumRef;
    private DatabaseReference mPostedByDetails;
    private DatabaseReference categoryInfo;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;

    private PermissionUtilities permissionUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Contact");
        setContentView(R.layout.activity_infone_add_contact);
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
        mProgress = new ProgressDialog(this);

        catId = getIntent().getExtras().getString("catId");
        catName = getIntent().getExtras().getString("catName");
        catImageURL = getIntent().getExtras().getString("catImageURL");
        totalContacts = getIntent().getIntExtra("totalContacts",0);
        catAdmin = getIntent().getExtras().getString("categoryadmin");
        String contactName = getIntent().getStringExtra("contactName");
        String contactNumber = getIntent().getStringExtra("contactNumber");

        permissionUtilities = new PermissionUtilities(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!permissionUtilities.isEnabled(PermissionUtilities.READ_CONTACTS))
                permissionUtilities.request(permissionUtilities.READ_CONTACTS);
            if(!permissionUtilities.isEnabled(PermissionUtilities.READ_EXTERNAL_STORAGE))
                permissionUtilities.request(permissionUtilities.READ_EXTERNAL_STORAGE);
        }
//        Bitmap contactPhoto = (Bitmap) getIntent().getParcelableExtra("contactPhoto");
//        Uri photoUri=null;
        addImage = (SimpleDraweeView) findViewById(R.id.image_add_infone_add);
//        if(contactPhoto!=null)
//        {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
////        contactPhoto.compress(Bitmap.CompressFormat.JPEG, 50, out);
//            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), contactPhoto, "Title", null);
//            photoUri = Uri.parse(path);
//            addImage.setImageURI(photoUri);
//        }
        nameEt = (MaterialEditText) findViewById(R.id.name_et_infone_add);
        nameEt.setText(contactName);
        phone1Et = (MaterialEditText) findViewById(R.id.phone_et_infone_add);
        phone1Et.setText(contactNumber);
        phone2Et = (MaterialEditText) findViewById(R.id.phone2_et_infone_add);
        descEt = (MaterialEditText) findViewById(R.id.desc_et_infone_add);
        saveButton = (Button) findViewById(R.id.btn_save_infone_add);
        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        databaseRecents = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");
        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setMessage("Saving Contact");
                mProgress.setCancelable(false);
                mProgress.show();

                final String name = nameEt.getText().toString().trim();
                String phoneNum1 = phone1Et.getText().toString().trim();
                String phoneNum2 = phone2Et.getText().toString().trim();
                String desc = descEt.getText().toString().trim();
                desc = desc.isEmpty() ? "" : desc;
                if (phoneNum1.isEmpty() && !phoneNum2.isEmpty()) {
                    phoneNum1 = phoneNum2;
                    phoneNum2 = "";
                }


                final String finalPhoneNum = phoneNum1;

                if (!name.isEmpty() && !phoneNum1.isEmpty() && !(phoneNum1.length()<10)) {


                    databaseReferenceInfone = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("infone");
                    final String finalPhoneNum1 = phoneNum2;
                    final String finalDesc = desc;
                    databaseReferenceInfone.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot childsnapshot : dataSnapshot.child("categories").child(catId).getChildren()) {
                                arrayList.add((String) childsnapshot.child("phone").child("0").getValue());
                                Log.d("finalphoneNum", String.valueOf(count));
                            }

                            for (String mobNum : arrayList){
                                Log.d("MobNUm",mobNum);
                                if(mobNum.equals(finalPhoneNum)){

                                    Log.d(TAG, "data phone:" + " " + mobNum);
                                    count=1;
                                    mProgress.dismiss();
                                    Toast.makeText(AddInfoneContact.this, "Number already exists", Toast.LENGTH_SHORT).show();

                                }
                            }

                            postTimeMillis = System.currentTimeMillis();
                            key = databaseReferenceInfone.child("numbers").push().getKey();

                            newContactNumRef = databaseReferenceInfone.child("numbers").child(key);

                            newContactRef = databaseReferenceInfone.child("categories").child(catId).child(key);

                            categoryInfo = databaseReferenceInfone.child("categoriesInfo").child(catId);




                            if(count!=1) {
                                //Inside Categories Info
                                categoryInfo.child("totalContacts").setValue(totalContacts + 1);

                                //Inside Categories
                                newContactRef.child("name").setValue(name);
                                newContactRef.child("phone").child("0").setValue(finalPhoneNum);
                                newContactRef.child("phone").child("1").setValue(finalPhoneNum1);
                                newContactRef.child("desc").setValue(finalDesc);
                                newContactRef.child("key").child(key);

                                //Inside Contacts
                                newContactNumRef.child("category").setValue(catId);
                                newContactNumRef.child("key").child(key);
                                newContactNumRef.child("name").setValue(name);
                                newContactNumRef.child("phone").child("0").setValue(finalPhoneNum);
                                newContactNumRef.child("phone").child("1").setValue(finalPhoneNum1);
                                newContactNumRef.child("type").setValue("NotUser");
                                newContactNumRef.child("validCount").setValue(0);
                                newContactNumRef.child("verifiedDate").setValue(postTimeMillis);
                                newContactNumRef.child("PostTimeMillis").setValue(postTimeMillis);
                                newContactNumRef.child("desc").setValue(finalDesc);
                                newContactNumRef.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                uploadImage();
                                Log.d(TAG, "DATA UPLOADED" + " ");
                                //Inside Recents
                               FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                        UserItemFormat userItemFormat = new UserItemFormat();
                                        HashMap<String,Object> metadata = new HashMap<>();
                                        userItemFormat.setUsername(String.valueOf(dataSnapshot2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").getValue()));
                                        userItemFormat.setImageURL(String.valueOf(dataSnapshot2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imageURL").getValue()));
                                        userItemFormat.setUserUID(String.valueOf(dataSnapshot2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userUID").getValue()));
                                        metadata.put("infoneUserId",key);
                                        Log.d("helloosohsohsh", "onDataChange: "+catId);
                                        metadata.put("catID",catId);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                HashMap<String, String> meta = new HashMap<>();

                                meta.put("catID", catId);

                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_ADDED_CONTACT);
                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                counterItemFormat.setMeta(meta);

                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                counterPush.pushValues();
                            }
                            else{finish();}

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });



                }else {
                    mProgress.dismiss();

                    if(phoneNum1.length()<10) {
                        Snackbar snackbar = Snackbar.make(nameEt, "Please check the contact details", Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }else {
                        Toast.makeText(AddInfoneContact.this, "Some fields are empty", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(AddInfoneContact.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AddInfoneContact.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                }
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        CounterItemFormat counterItemFormat = new CounterItemFormat();
        HashMap<String, String> meta= new HashMap<>();

        meta.put("category",catId);

        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_ADD_CONTACT_OPEN);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        counterItemFormat.setMeta(meta);

        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();
    }

    private void uploadImage() {

        flag1=false;
        flag2=false;


        if (mImageUri != null) {
            final StorageReference filepath = mStorageRef.child("InfoneImage").child(mImageUri.getLastPathSegment() + key);
            UploadTask uploadTask = filepath.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()) {
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
                        newContactNumRef.child("imageurl").setValue(downloadUri.toString());
                        flag1 = true;
                        customFinish();
                    }
                    else {
                        // Handle failures
                        // ...
                        Snackbar snackbar = Snackbar.make(nameEt, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });
            final StorageReference filepathThumb = mStorageRef.child("InfoneImageSmall").child(mImageUriSmall.getLastPathSegment() + key + "Thumbnail");
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
                        newContactRef.child("thumbnail").setValue(downloadUriThumb.toString());
                        newContactNumRef.child("thumbnail").setValue(downloadUriThumb.toString());
                        flag2=true;
                        customFinish();
                    }
                    else {
                        // Handle failures
                        // ...
                        Snackbar snackbar = Snackbar.make(nameEt, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });
        } else {
            newContactNumRef.child("imageurl").setValue(catImageURL);
            newContactRef.child("thumbnail").setValue(catImageURL);
            newContactNumRef.child("thumbnail").setValue(catImageURL);
            mProgress.dismiss();
            finish();
        }
    }

    public void customFinish(){
        if(flag1&&flag2){

            flag1=false;
            flag2=false;

            GlobalFunctions.addPoints(10);
            mProgress.dismiss();
            finish();
        }
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
                    String path = MediaStore.Images.Media.insertImage(AddInfoneContact.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);
                    String pathSmall = MediaStore.Images.Media.insertImage(AddInfoneContact.this.getContentResolver(), bitmap2Small, mImageUri.getLastPathSegment(), null);
                    mImageUri = Uri.parse(path);
                    mImageUriSmall = Uri.parse(pathSmall);
                    addImage.setImageURI(mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: ", result.getError());
            }
        }
    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtilities.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}