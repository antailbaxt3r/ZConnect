package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class AddEvent extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 7;
    String eventDate;
    String dateString;
    private Uri mImageUri = null;
    private ImageButton mAddImage;
    private android.support.design.widget.TextInputEditText mEventName;
    private android.support.design.widget.TextInputEditText mEventDescription;
    private Button mPostBtn;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FrameLayout CalendarButton;
    private ProgressDialog mProgress;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            eventDate = date.toString();
            Toast.makeText(AddEvent.this, eventDate, Toast.LENGTH_SHORT).show();
            dateString = String.valueOf(date.getTime());
            Toast.makeText(AddEvent.this, dateString, Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mAddImage = (ImageButton) findViewById(R.id.imageButton);
        mEventName = (TextInputEditText) findViewById(R.id.name);
        mEventDescription = (TextInputEditText) findViewById(R.id.description);
        mPostBtn = (Button) findViewById(R.id.post);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ZConnect/Event/Posts");

        CalendarButton = (FrameLayout)findViewById(R.id.dateAndTime);

        mProgress = new ProgressDialog(this);

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        CalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();

            }
        });
    }

    private void startPosting() {
        mProgress.setMessage("Posting Event..");
        mProgress.show();
        final String eventNameValue = mEventName.getText().toString().trim();
        final String eventDescriptionValue = mEventDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(eventNameValue) && !TextUtils.isEmpty(eventDescriptionValue) && mImageUri != null) {
            //1
            final StorageReference filepath = mStorage.child("EventImage").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost = mDatabase.push();
                    String key = newPost.getKey();
                    newPost.child("Key").setValue(key);
                    newPost.child("EventName").setValue(eventNameValue);
                    newPost.child("EventDescription").setValue(eventDescriptionValue);
                    newPost.child("EventImage").setValue(downloadUri.toString());
                    newPost.child("EventDate").setValue(eventDate);
                    newPost.child("FormatDate").setValue(dateString);

                    DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("ZConnect/everything").push();
                    newPost2.child("Title").setValue(eventNameValue);
                    newPost2.child("Description").setValue(eventDescriptionValue);
                    newPost2.child("Url").setValue(downloadUri.toString());
                    newPost2.child("multiUse2").setValue(eventDate);
                    newPost2.child("multiUse1").setValue(dateString);
                    newPost2.child("type").setValue("E");


                    mProgress.dismiss();
                    startActivity(new Intent(AddEvent.this, AllEvents.class));
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    String path = MediaStore.Images.Media.insertImage(AddEvent.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);
                    mImageUri = Uri.parse(path);
                    mAddImage.setImageURI(mImageUri);


                } catch (IOException e) {
                    e.printStackTrace();
                }





            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
