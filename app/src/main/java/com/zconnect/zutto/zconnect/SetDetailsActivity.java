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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetDetailsActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    public static final String DEFAULT_PHOTO_URL = "https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/PhonebookImage%2FdefaultprofilePhone.png?alt=media&token=5f814762-16dc-4dfb-ba7d-bcff0de7a336";
    private static final int GALLERY_REQUEST = 1;
    private String downloadUrl; //Stores Image Url
    @BindView(R.id.profileImage)
    SimpleDraweeView userProfileSdv;
    @BindView(R.id.username)
    EditText userNameTv;
    @BindView(R.id.submitDetails)
    Button submitDetailsBtn;
    @SuppressWarnings("FieldCanBeLocal")
    private Typeface ralewayRegularTf;
    private Uri mImageUri = null;
    private DatabaseReference usersDbRef;
    private StorageReference profileSRef;
    private ProgressDialog mProgressDialog;
    private IntentHandle mIntentHandle = new IntentHandle();
    private FirebaseUser mUser;
    private boolean skipImage = false;
    private Uri downLoadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_details);
        ButterKnife.bind(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        String call = getIntent().getStringExtra("caller");
        Toast.makeText(this, call, Toast.LENGTH_LONG).show();

        ralewayRegularTf = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");

        userNameTv.setText(mUser.getDisplayName());

        userNameTv.setTypeface(ralewayRegularTf);
        submitDetailsBtn.setTypeface(ralewayRegularTf);

        usersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        profileSRef = FirebaseStorage.getInstance().getReference().child("Profile");

        mProgressDialog = new ProgressDialog(this);

        submitDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccountSetup();
            }
        });

        userProfileSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipImage = false;
                startActivityForResult(mIntentHandle.getPickImageIntent(SetDetailsActivity.this), GALLERY_REQUEST);
            }
        });
    }

    private void startAccountSetup() {
        final String username = userNameTv.getText().toString();

        mProgressDialog.setMessage("Setting Account..");

        if (username.length() == 0) {
            String message = "Please enter your name";
            Snackbar snackbar = Snackbar.make(userNameTv, message, Snackbar.LENGTH_INDEFINITE);
            TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal900));
            snackbar.show();
            return;
        }
        if (mImageUri == null && !skipImage) {
            Snackbar snackbar = Snackbar.make(userNameTv, R.string.noImage, Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snackbar.setAction("Skip", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipImage = true;
                    startAccountSetup();
                }
            });
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal900));
            snackbar.show();
            return;
        }
        final DatabaseReference currentUserDbRef = usersDbRef.child(mUser.getUid());
        if (mImageUri == null) {
            downLoadUri = mUser.getPhotoUrl();
        } else  {
            final StorageReference filePath = profileSRef.child(mUser.getUid());
            mProgressDialog.show();
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downLoadUri = taskSnapshot.getDownloadUrl();
                    if (downLoadUri == null) downLoadUri = mUser.getPhotoUrl();
                }
            });
        }
        if (downLoadUri != null) downloadUrl = downLoadUri.toString();
        else downloadUrl = DEFAULT_PHOTO_URL;
        currentUserDbRef.child("Image").setValue(downloadUrl);
        currentUserDbRef.child("Username").setValue(username);
        currentUserDbRef.child("Email").setValue(mUser.getEmail());
        mProgressDialog.dismiss();
        Intent homeIntent = new Intent(SetDetailsActivity.this, HomeActivity.class);
        homeIntent.putExtra("type", "new");
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = mIntentHandle.getPickImageResultUri(data); //Get data
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
                    userProfileSdv.setImageURI(mImageUri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    if (bitmap.getByteCount() > 250000) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);
                    }
                    String path = MediaStore.Images.Media.insertImage(SetDetailsActivity.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);
                    mImageUri = Uri.parse(path);
                    userProfileSdv.setImageURI(mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: crop image activity result error", result.getError());
            }
        }

    }
}
