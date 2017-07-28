package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class setDetails extends BaseActivity {

    private static final int GALLERY_REQUEST = 1;
    String downloadUri; //Stores Image Url
    private SimpleDraweeView userProfile;
    private EditText userName;
    private Button submit;
    private Uri mImageUri=null;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorageProfile;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private IntentHandle intentHandle = new IntentHandle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_details);
        String call = getIntent().getStringExtra("caller");
        Toast.makeText(this,call,Toast.LENGTH_LONG).show();

        userProfile = (SimpleDraweeView) findViewById(R.id.profileImage);
        userName = (EditText) findViewById(R.id.username);
        submit = (Button) findViewById(R.id.submitDetails);

        Typeface ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        userName.setTypeface(ralewayRegular);
        submit.setTypeface(ralewayRegular);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageProfile = FirebaseStorage.getInstance().getReference().child("Profile");
        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccountSetup();
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intentHandle.getPickImageIntent(setDetails.this), GALLERY_REQUEST);

            }
        });

    }

    private void startAccountSetup() {
        final String username = userName.getText().toString().trim();
        final String userId = mAuth.getCurrentUser().getUid();
        final String userEmail = mAuth.getCurrentUser().getEmail();

        mProgress.setMessage("Setting Account..");
        if (!TextUtils.isEmpty(username)) {
            final DatabaseReference currentUser = mDatabaseUsers.child(userId);
            if (mImageUri == null) {
                downloadUri = "https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/PhonebookImage%2FdefaultprofilePhone.png?alt=media&token=5f814762-16dc-4dfb-ba7d-bcff0de7a336"; //sets default download Image url
                Snackbar snack = Snackbar.make(userName, R.string.noImage, Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.setAction("Select", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(intentHandle.getPickImageIntent(setDetails.this));
                    }
                });
                snack.setAction("Skip", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentUser.child("Image").setValue(downloadUri); //Sets
                        currentUser.child("Username").setValue(username); //User
                        currentUser.child("Email").setValue(userEmail); //Details
                        mProgress.dismiss();
                        Intent setDetailsIntent = new Intent(setDetails.this, home.class);
                        setDetailsIntent.putExtra("type", "new");
                        setDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setDetailsIntent);
                        finish();


                    }
                });
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal900));
                snack.show();
            } else {
                final StorageReference filePath = mStorageProfile.child(mAuth.getCurrentUser().getUid());
                mProgress.show();
                filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        downloadUri = taskSnapshot.getDownloadUrl().toString();
                        currentUser.child("Image").setValue(downloadUri); //Sets
                        currentUser.child("Username").setValue(username); //User
                        currentUser.child("Email").setValue(userEmail); //Details
                        mProgress.dismiss();
                        Intent setDetailsIntent = new Intent(setDetails.this, home.class);
                        setDetailsIntent.putExtra("type", "new");
                        setDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setDetailsIntent);
                        finish();
                    }
                });
            }

        } else {
            String message;
            {
                message = "Enter all fields";
                if (mImageUri == null) {
                    message = "Please select image";
                } else {
                    message = "Enter all fields";
                }
                Snackbar snack = Snackbar.make(userName, message, Snackbar.LENGTH_INDEFINITE);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal900));
                snack.show();
                // Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            }

        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data); //Get data
            CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.OVAL)
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
                    userProfile.setImageURI(mImageUri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    Double ratio = ((double) bitmap.getWidth()) / bitmap.getHeight();

                    if (bitmap.getByteCount() > 250000) {

                        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);
                    }
                    String path = MediaStore.Images.Media.insertImage(setDetails.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    userProfile.setImageURI(mImageUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
