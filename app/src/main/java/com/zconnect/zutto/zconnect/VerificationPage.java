package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.Drawables;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.commonModules.NumberNotificationForFeatures;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.FeatureDBName;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.VerificationUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class VerificationPage extends BaseActivity {

    private static final int GALLERY_REQUEST = 7;
    private TextView statusTextView;
    private ImageButton idImageButton;
    private EditText aboutNewUserEditText;
    private Button submitUserIDButton;
    private DatabaseReference newUsersDatabaseReference,userReference, currentUser;
    private FirebaseAuth mAuth;

    private IntentHandle intentHandle;
    private Intent galleryIntent;
    private Uri mImageUri = null;

    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    private View.OnClickListener imageClickListener;

    private ProgressDialog progressDialogInitial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verfication_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialogInitial = new ProgressDialog(this);
        progressDialogInitial.setMessage("Loading");

        progressDialogInitial.show();

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

        statusTextView = (TextView) findViewById(R.id.verification_status);
        idImageButton = (ImageButton) findViewById(R.id.verication_image);
        aboutNewUserEditText = (EditText) findViewById(R.id.about_new_user);
        submitUserIDButton = (Button) findViewById(R.id.submit_verification_button);

        intentHandle = new IntentHandle();

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("verificationID").child(communityReference);
        userReference  =FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
        newUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers");
        currentUser = userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        VerificationPage.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            VerificationPage.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                } else {
                    galleryIntent = intentHandle.getPickImageIntent(VerificationPage.this);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        };

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        newUsersDatabaseReference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("statusCode")){
                    if (Objects.requireNonNull(dataSnapshot.child("statusCode").getValue(String.class)).equals(VerificationUtilities.KEY_APPROVED)) {
                        statusTextView.setText("Your account has been approved you are a verified user now.");
                        idImageButton.setOnClickListener(null);
                        Bitmap bitmap = null;

                        try {
                            URL url = new URL(dataSnapshot.child("idImageURL").getValue(String.class));
                            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch(IOException e) {
                            System.out.println(e);
                        }
                        if(bitmap!=null){
                            idImageButton.setImageBitmap(bitmap);
                        }

                        aboutNewUserEditText.setFocusable(false);
                        submitUserIDButton.setVisibility(View.GONE);

                    } else if (Objects.requireNonNull(dataSnapshot.child("statusCode").getValue(String.class)).equals(VerificationUtilities.KEY_NOT_APPROVED)) {
                        statusTextView.setText("Your account has been disapproved, please add relevant college admission related id.");
                        idImageButton.setOnClickListener(imageClickListener);
                        aboutNewUserEditText.setFocusable(true);
                        aboutNewUserEditText.setText("");
                        submitUserIDButton.setVisibility(View.VISIBLE);
                    } else if (Objects.requireNonNull(dataSnapshot.child("statusCode").getValue(String.class)).equals(VerificationUtilities.KEY_PENDING)) {
                        statusTextView.setText("Verification pending! You can update your verification details.");
                        idImageButton.setOnClickListener(imageClickListener);
                        aboutNewUserEditText.setText(dataSnapshot.child("about").getValue(String.class));
                        aboutNewUserEditText.setFocusable(true);
                        submitUserIDButton.setVisibility(View.VISIBLE);

                        Bitmap bitmap = null;

                        try {
                            URL url = new URL(dataSnapshot.child("idImageURL").getValue(String.class));
                            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch(IOException e) {
                            System.out.println(e);
                        }
                       if(bitmap!=null){
                           idImageButton.setImageBitmap(bitmap);
                       }


                    }
                    progressDialogInitial.dismiss();
                }else {
                    statusTextView.setText("Submit any document related to college admission!");
                    idImageButton.setOnClickListener(imageClickListener);
                    aboutNewUserEditText.setFocusable(true);
                    submitUserIDButton.setVisibility(View.VISIBLE);
                    progressDialogInitial.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        submitUserIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    private void startPosting(){

        progressDialog.setMessage("Submitting details..");
        progressDialog.show();


        String aboutText = aboutNewUserEditText.getText().toString().trim();
        if(aboutText.equals("")){
            aboutText = " ";
        }

        if(mImageUri != null){

            final DatabaseReference newPost =  newUsersDatabaseReference.child(mAuth.getCurrentUser().getUid());
            final StorageReference filepath = mStorage.child((mImageUri.getLastPathSegment()) + mAuth.getCurrentUser().getUid() + System.currentTimeMillis());
            final String finalAboutText = aboutText;
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
                public void onComplete(@NonNull final Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                                Uri downloadUri = task.getResult();
                                newPost.child("idImageURL").setValue(downloadUri != null ? downloadUri.toString() : null);
                                newPost.child("statusCode").setValue(VerificationUtilities.KEY_PENDING);
                                newPost.child("about").setValue(finalAboutText);
                                newPost.child("UID").setValue(mAuth.getCurrentUser().getUid());
                                newPost.child("name").setValue(userItemFormat.getUsername());
                                userReference.child(mAuth.getCurrentUser().getUid()).child("userType").setValue(UsersTypeUtilities.KEY_PENDING);
                                userItemFormat.setUserType(UsersTypeUtilities.KEY_PENDING);
                                progressDialog.dismiss();
                                Toast.makeText(VerificationPage.this, "Your details are submitted, you will be notified once verification is done.", Toast.LENGTH_LONG).show();
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_ADMIN_PANEL);
                        numberNotificationForFeatures.setCount();
                        Log.d("NumberNoti setting for ", FeatureDBName.KEY_ADMIN_PANEL);
                    }
                    else {
                        // Handle failures
                        // ...
                        Snackbar snackbar = Snackbar.make(statusTextView, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });

        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "You haven't added any ID proof.", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data, VerificationPage.this); //Get data
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
                    String path = MediaStore.Images.Media.insertImage(VerificationPage.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    idImageButton.setImageURI(mImageUri);

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