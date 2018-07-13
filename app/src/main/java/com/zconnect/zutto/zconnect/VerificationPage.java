package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.Utilities.VerificationUtilities;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

public class VerificationPage extends BaseActivity {

    private static final int GALLERY_REQUEST = 7;
    private TextView statusTextView;
    private ImageButton idImageButton;
    private EditText aboutNewUserEditText;
    private Button submitUserIDButton;
    private DatabaseReference newUsersDatabaseReference;
    private FirebaseAuth mAuth;

    private IntentHandle intentHandle;
    private Intent galleryIntent;
    private Uri mImageUri = null;

    private StorageReference mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verfication_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        statusTextView = (TextView) findViewById(R.id.verification_status);
        idImageButton = (ImageButton) findViewById(R.id.verication_image);
        aboutNewUserEditText = (EditText) findViewById(R.id.about_new_user);
        submitUserIDButton = (Button) findViewById(R.id.submit_verification_button);

        intentHandle = new IntentHandle();

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("verificationID").child(communityReference);
        newUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers");

        newUsersDatabaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("statusCode")){
                        if (Objects.requireNonNull(dataSnapshot.child("statusCode").getValue(String.class)).equals(VerificationUtilities.KEY_APPROVED)) {
                            statusTextView.setText("Your Account have been approved you are a verified user now.");
                            idImageButton.setOnClickListener(null);
                            aboutNewUserEditText.setFocusable(false);
                            submitUserIDButton.setVisibility(View.GONE);

                        } else if (Objects.requireNonNull(dataSnapshot.child("statusCode").getValue(String.class)).equals(VerificationUtilities.KEY_NOT_APPROVED)) {
                            statusTextView.setText("Your Account has been disapproved, please add relevant community id.");
                        } else if (Objects.requireNonNull(dataSnapshot.child("statusCode").getValue(String.class)).equals(VerificationUtilities.KEY_PENDING)) {
                            statusTextView.setText("Verification Pending...");
                            aboutNewUserEditText.setText(dataSnapshot.child("about").getValue(String.class));
                            Picasso.with(getApplicationContext()).load(dataSnapshot.child("imageURL").getValue(String.class)).error(R.drawable.defaultevent).placeholder(R.drawable.defaultevent).into(idImageButton);
                        }
                    statusTextView.setText(dataSnapshot.child("status").getValue(String.class));
                }else {
                    statusTextView.setText("Submit Details");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        idImageButton.setOnClickListener(new View.OnClickListener() {
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
        });

        submitUserIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void startPosting(){

        String aboutText = statusTextView.getText().toString().trim();
        if(aboutText.equals("")){
            aboutText = " ";
        }

        if(mImageUri != null){
            final DatabaseReference newPost =  newUsersDatabaseReference.child(mAuth.getCurrentUser().getUid());
            StorageReference filepath = mStorage.child((mImageUri.getLastPathSegment()) + mAuth.getCurrentUser().getUid() + System.currentTimeMillis());
            final String finalAboutText = aboutText;
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    newPost.child("idImageURL").setValue(downloadUri != null ? downloadUri.toString() : null);
                    newPost.child("statusCode").setValue(VerificationUtilities.KEY_PENDING);
                    newPost.child("about").setValue(finalAboutText);
                    newPost.child("UID").setValue(mAuth.getCurrentUser().getUid());
                }
            });

        }


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