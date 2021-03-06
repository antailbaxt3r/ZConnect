package com.zconnect.zutto.zconnect.addActivities;

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
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.ZConnectDetails;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.PermissionUtilities;
import com.zconnect.zutto.zconnect.utilities.RequestTypeUtilities;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class RequestInfoneCat extends BaseActivity {


    private SharedPreferences communitySP;
    public String communityReference;

    MaterialEditText nameEt;
    Button saveButton;
    SimpleDraweeView addImage;

    DatabaseReference requestInfoneCat,mPostedByDetails;
    String catId;

    Boolean flag1,flag2;
    private final String TAG = getClass().getSimpleName();

    /*uploading elements*/
    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 7;
    private Uri mImageUriSmall;
    private StorageReference mStorageRef;

    private String catImageurl;
    private ProgressDialog progressDialog;

    private PermissionUtilities permissionUtilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Request an Infone Category");
        setContentView(R.layout.activity_infone_add_cat);
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
        permissionUtilities = new PermissionUtilities(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!permissionUtilities.isEnabled(PermissionUtilities.READ_EXTERNAL_STORAGE))
                permissionUtilities.request(permissionUtilities.READ_EXTERNAL_STORAGE);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        nameEt = (MaterialEditText) findViewById(R.id.et_name_cat_add_infone);
        addImage = (SimpleDraweeView) findViewById(R.id.image_add_cat_infone);
        saveButton = (Button) findViewById(R.id.save_image_add_cat_infone);

        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        requestInfoneCat = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/admin/requests");
        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


//        try{
//            if (getIntent().getExtras().getString("catId") != null) {
//                catId = getIntent().getExtras().getString("catId");
//                catName = getIntent().getExtras().getString("catName");
//                catImageurl=getIntent().getExtras().getString("catImageurl");
//
//                Log.e(TAG,"Exception : "+catName+" "+catId+" "+catImageurl);
//
//                Uri imageUri= Uri.parse(catImageurl);
//                addImage.setImageURI(imageUri);
//
//                nameEt.setText(catName);
//
//                toAdd = false;
//            } else {
//                catId = "";
//            }
//        }
//        catch (Exception e){
//            Log.e(TAG,"Exception : "+e.toString());
//            toAdd=true;
//            //finish();
//        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameEt.getText().toString().trim().equals("")) {

                    if(mImageUri != null) {

                        Log.d(String.valueOf(nameEt.getText()), "saveChanges: ");
                        progressDialog.setMessage("Requesting Category");
                        saveChanges();

                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta = new HashMap<>();

                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_REQUEST_CATEGORY);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        counterItemFormat.setMeta(meta);

                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();
                    }
                    else{
                        Toast.makeText(RequestInfoneCat.this, "Category image cannot be empty!", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(RequestInfoneCat.this, "Category name cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RequestInfoneCat.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            RequestInfoneCat.this,
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

        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_REQUEST_CATEGORY_OPEN);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        counterItemFormat.setMeta(meta);

        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();
    }

    private void saveChanges() {
        progressDialog.show();
        String name = nameEt.getText().toString();

        /*catId = databaseReferenceInfone.child("categoriesInfo").push().getKey();
        newCategoryRef = databaseReferenceInfone.child("categoriesInfo").child(catId);
        newCategoryRef.child("name").setValue(name);
        newCategoryRef.child("admin").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newCategoryRef.child("catId").setValue(catId);
        newCategoryRef.child("totalContacts").setValue(0);*/

        final DatabaseReference newPush=requestInfoneCat.push();

        final HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("Type", RequestTypeUtilities.TYPE_INFONE_CAT);
        requestMap.put("key",newPush.getKey());
        requestMap.put("Name",name);
        requestMap.put("PostTimeMillis",System.currentTimeMillis());

        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final HashMap<String,Object> postedBy = new HashMap<>();
                postedBy.put("Username",dataSnapshot.child("username").getValue().toString());
                postedBy.put("ImageThumb",dataSnapshot.child("imageURLThumbnail").getValue().toString());
                postedBy.put("UID",dataSnapshot.child("userUID").getValue().toString());

                requestMap.put("PostedBy",postedBy);
                newPush.setValue(requestMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(RequestInfoneCat.this, "Your request has been sent to the admins. The Infone Category will be added soon.", Toast.LENGTH_SHORT).show();

        flag1=false;
        flag2=false;

        if (mImageUri != null) {
            final StorageReference filepath = mStorageRef.child("InfoneImage").child(mImageUri.getLastPathSegment() + catId);
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
                        catImageurl = downloadUri.toString();
                        newPush.child("imageurl").setValue(downloadUri.toString());
                        flag1=true;
                        //addContact();
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
            final StorageReference filepathThumb = mStorageRef.child("InfoneImageSmall").child(mImageUriSmall.getLastPathSegment() + catId + "Thumbnail");
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
                        newPush.child("thumbnail").setValue(downloadUriThumb.toString());
                        flag2=true;
                        //addContact();
                        finish();
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
            newPush.removeValue();
            Toast.makeText(this, "Fill all details including image", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    /*public void addContact(){
        if(flag1&&flag2){

            NotificationSender notificationSender = new NotificationSender(RequestInfoneCat.this, FirebaseAuth.getInstance().getCurrentUser().getUid());
            NotificationItemFormat addInfoneCategoryNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_CATEGORY_ADD,FirebaseAuth.getInstance().getCurrentUser().getUid());
            addInfoneCategoryNotification.setCommunityName(communityTitle);

            addInfoneCategoryNotification.setItemKey(catId);
            addInfoneCategoryNotification.setItemImage(catImageurl);
            addInfoneCategoryNotification.setItemName(nameEt.getText().toString());
            addInfoneCategoryNotification.setItemCategoryAdmin(FirebaseAuth.getInstance().getCurrentUser().getUid());

            notificationSender.execute(addInfoneCategoryNotification);

            GlobalFunctions.addPoints(10);

            Toast.makeText(RequestInfoneCat.this, "Add a contact in your new category",
                    Toast.LENGTH_SHORT).show();
            final Intent addContactIntent = new Intent(RequestInfoneCat.this,
                    AddInfoneContact.class);
            addContactIntent.putExtra("catId", catId);
            addContactIntent.putExtra("catName",nameEt.getText().toString());
            startActivity(addContactIntent);

            progressDialog.dismiss();
            finish();
        }
    }*/


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
                    String path = MediaStore.Images.Media.insertImage(RequestInfoneCat.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);
                    String pathSmall = MediaStore.Images.Media.insertImage(RequestInfoneCat.this.getContentResolver(), bitmap2Small, mImageUri.getLastPathSegment(), null);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtilities.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
