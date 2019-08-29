package com.zconnect.zutto.zconnect.addActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.utilities.PermissionUtilities;

import java.io.IOException;

public class CreateCommunity extends BaseActivity {

    private static final int GALLERY_REQUEST = 7;
    IntentHandle intentHandle;
    SimpleDraweeView communityImage;
    MaterialEditText communityName;
    Button createCommunity;
    Intent galleryIntent;
    StorageReference mStorage;
    DatabaseReference mDatabase;
    Uri mImageUri;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Double lat,lon;
    private PermissionUtilities permissionUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.zconnect.zutto.zconnect.R.layout.activity_create_community);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));


        intentHandle = new IntentHandle();

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
            int colorPrimary = ContextCompat.getColor(this, com.zconnect.zutto.zconnect.R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, com.zconnect.zutto.zconnect.R.color.colorPrimaryDark);
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        try {
            lat = getIntent().getExtras().getDouble("lat");
            lon = getIntent().getExtras().getDouble("lon");
        }catch (Exception e){
            lat = 0.0;
            lon = 0.0;
        }
        permissionUtilities = new PermissionUtilities(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!permissionUtilities.isEnabled(PermissionUtilities.READ_EXTERNAL_STORAGE))
                permissionUtilities.request(permissionUtilities.READ_EXTERNAL_STORAGE);
        }

        communityName= (MaterialEditText) findViewById(com.zconnect.zutto.zconnect.R.id.community_name);
        communityImage = (SimpleDraweeView) findViewById(com.zconnect.zutto.zconnect.R.id.community_image);
        createCommunity = (Button) findViewById(com.zconnect.zutto.zconnect.R.id.create_community);
        mProgress = new ProgressDialog(this);

        communityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        CreateCommunity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateCommunity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                } else {
                    galleryIntent = intentHandle.getPickImageIntent(CreateCommunity.this);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        });

        createCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data, CreateCommunity.this); //Get data
            CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {
                    communityImage.setImageURI(mImageUri);
                    mImageUri = result.getUri();

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    Double ratio = ((double) bitmap.getWidth()) / bitmap.getHeight();

                    if (bitmap.getByteCount() > 350000) {

                        bitmap = Bitmap.createScaledBitmap(bitmap, 960, (int) (960 / ratio), false);
                    }
                    String path = MediaStore.Images.Media.insertImage(CreateCommunity.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    communityImage.setImageURI(mImageUri);

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

    private void startPosting(){

        mProgress.setMessage("Posting Request..");

        final String communityNameString,communityEmailString,communityImageString,communityCodeString;

        if(mImageUri!=null) {
            communityImageString = mImageUri.toString();
        }else {
            communityImageString = null;
        }
        communityNameString = communityName.getText().toString().trim();



        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("newRequestCommunities");

        if(!TextUtils.isEmpty(communityImageString)&&!TextUtils.isEmpty(communityNameString)){
            mProgress.show();
            final StorageReference filepath = mStorage.child("CommunityImages").child((mImageUri.getLastPathSegment()));
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
                        DatabaseReference newPost = mDatabase.push();
                        String key = newPost.getKey();
                        newPost.child("name").setValue(communityNameString);
                        newPost.child("key").setValue(key);
                        newPost.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        newPost.child("image").setValue(downloadUri.toString());
                        newPost.child("location").child("lat").setValue(lat);
                        newPost.child("location").child("lon").setValue(lon);
                        mProgress.dismiss();
                        finish();
                    }
                    else {
                        // Handle failures
                        // ...

                        mProgress.dismiss();
                        Snackbar snackbar = Snackbar.make(communityName, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });
        }else{
            if(mImageUri!=null) {
                Toast.makeText(this, "Some fields are empty", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please select a community image", Toast.LENGTH_SHORT).show();
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
