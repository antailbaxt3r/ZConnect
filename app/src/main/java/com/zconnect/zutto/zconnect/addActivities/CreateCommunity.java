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
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;

import java.io.IOException;

public class CreateCommunity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 7;
    IntentHandle intentHandle;
    ImageButton communityImage;
    MaterialEditText communityName,communityEmail,communityCode;
    Button createCommunity;
    Intent galleryIntent;
    StorageReference mStorage;
    DatabaseReference mDatabase;
    Uri mImageUri;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.zconnect.zutto.zconnect.R.layout.activity_create_community);
        Toolbar toolbar = (Toolbar) findViewById(com.zconnect.zutto.zconnect.R.id.toolbar);
        setSupportActionBar(toolbar);

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
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        communityEmail= (MaterialEditText) findViewById(com.zconnect.zutto.zconnect.R.id.community_email);
        communityName= (MaterialEditText) findViewById(com.zconnect.zutto.zconnect.R.id.community_name);
        communityCode = (MaterialEditText) findViewById(com.zconnect.zutto.zconnect.R.id.community_code);
        communityImage = (ImageButton) findViewById(com.zconnect.zutto.zconnect.R.id.community_image);
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

        mProgress.setMessage("Posting Product..");
        mProgress.show();

        final String communityNameString,communityEmailString,communityImageString,communityCodeString;


        communityEmailString= communityEmail.getText().toString().trim();
        communityImageString= mImageUri.toString();
        communityNameString= communityName.getText().toString().trim();
        communityCodeString= communityCode.getText().toString().trim();



        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("communitiesInfo");

        if(!TextUtils.isEmpty(communityEmailString)&&!TextUtils.isEmpty(communityImageString)&&!TextUtils.isEmpty(communityNameString)){
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
                        newPost.child("uid").setValue(communityEmailString);
                        newPost.child("image").setValue(downloadUri.toString());
                        mProgress.dismiss();
                    }
                    else {
                        // Handle failures
                        // ...
                        Snackbar snackbar = Snackbar.make(communityName, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });
            Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Fields Empty", Toast.LENGTH_SHORT).show();
        }
    }
}
