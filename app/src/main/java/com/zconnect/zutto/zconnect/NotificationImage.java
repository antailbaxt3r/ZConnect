package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.addActivities.AddProduct;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CustomSpinner;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

import java.io.IOException;

import mabbas007.tagsedittext.TagsEditText;

public class NotificationImage extends BaseActivity{

    Intent galleryIntent;
    private static final int GALLERY_REQUEST = 7;
    IntentHandle intentHandle;
    private Uri mImageUri = null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsername;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    EditText notificationDescription=(EditText)findViewById(R.id.notif_description);
    EditText notificationTitle=(EditText)findViewById(R.id.notif_title);
    EditText nottificationURL=(EditText)findViewById(R.id.notif_url);



    Button submit=(Button)findViewById(R.id.button2);
    ImageButton mAddImage=(ImageButton)findViewById(R.id.imageButton);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_image);
        Button submit=(Button)findViewById(R.id.button2);
        ImageButton mAddImage=(ImageButton)findViewById(R.id.imageButton);


        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        NotificationImage.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            NotificationImage.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                } else {
                    galleryIntent = intentHandle.getPickImageIntent(NotificationImage.this);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable(getApplicationContext())) {

                    Toast toast=Toast.makeText(getApplicationContext(),"No internet connection", Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    startPosting();
                }
            }
        });

        mProgress.cancel();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data, NotificationImage.this); //Get data
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
                    String path = MediaStore.Images.Media.insertImage(NotificationImage.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    mAddImage.setImageURI(mImageUri);

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


    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void startPosting(){

        mProgress.setMessage("Posting Notification..");
        mProgress.show();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        final String userId = user.getUid();
        mUsername = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");

        if (!TextUtils.isEmpty(notificationDescription.getText()) && mImageUri != null && !TextUtils.isEmpty(notificationTitle.getText()) && !TextUtils.isEmpty(nottificationURL.getText()) ) {
            final StorageReference filepath = mStorage.child("NotificationImage").child((mImageUri.getLastPathSegment()) + mAuth.getCurrentUser().getUid());
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
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();
                        final DatabaseReference newPost = mDatabase.push();
                        NotificationSender notificationSender = new NotificationSender(NotificationImage.this, userId);
                        NotificationItemFormat addImageNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_IMAGE_URL, userId);
                        addImageNotification.setItemMessage(notificationDescription.getText().toString());
                        addImageNotification.setItemTitle(notificationTitle.getText().toString());
                        addImageNotification.setItemImage(mImageUri.toString());
                        addImageNotification.setItemURL(nottificationURL.getText().toString());

                        notificationSender.execute(addImageNotification);
                    }
                }
            });


        }
    }


}
