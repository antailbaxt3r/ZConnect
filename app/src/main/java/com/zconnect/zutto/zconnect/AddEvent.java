package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class AddEvent extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 7;
    String eventDate;
    String dateString;
    boolean flag = false;
    private Uri mImageUri = null;
    private SimpleDraweeView mAddImage;
    private EditText mEventName;
    private EditText mEventDescription;
    // private Button mPostBtn;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseVerified;
    private DatabaseReference mDatabase;
    private FrameLayout CalendarButton;
    private ProgressDialog mProgress;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            eventDate = date.toString();
            dateString = String.valueOf(date.getTime());

        }
    };

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
            getWindow().setStatusBarColor(colorPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        mAddImage = (SimpleDraweeView) findViewById(R.id.imageButton);
        mEventName = (EditText) findViewById(R.id.name);
        mEventDescription = (EditText) findViewById(R.id.description);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseVerified = FirebaseDatabase.getInstance().getReference().child("Event/VerifiedPosts");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Event/NotVerifiedPosts");

        mAddImage.setImageURI(Uri.parse("res:///" + R.drawable.addimage));
        CalendarButton = (FrameLayout)findViewById(R.id.dateAndTime);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        AddEvent.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AddEvent.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                }
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
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference mPrivileges;
        mPrivileges = FirebaseDatabase.getInstance().getReference().child("Event/Privileges/");

        final String emailId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mPrivileges.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getValue().equals(emailId)) {
                        flag = true;
                        Toast.makeText(AddEvent.this, "Its True", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_done) {
            if (!isNetworkAvailable(getApplicationContext())) {

                Snackbar snack = Snackbar.make(mEventDescription, "No Internet. Can't Add Event.", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();

            } else {

                startPosting(flag);

//                if (flag){
//                    startPosting(flag);}
//
//                else {
//                    // 1. Instantiate an AlertDialog.Builder with its constructor
//                    AlertDialog.Builder builder = new AlertDialog.Builder(AddEvent.this);
//
//                    // 2. Chain together various setter methods to set the dialog characteristics
//                    builder.setMessage(R.string.dialog_message);
////                            .setTitle(R.string.dialog_title);
//
//                    // Add the buttons
//                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//
////                        }
////                    });
////                    builder.setNegativeButton(R.string.request, new DialogInterface.OnClickListener() {
////                        public void onClick(DialogInterface dialog, int id) {
////                            // User cancelled the dialog
//
//
//                            //checks if user is online
//                            if (!isOnline()) {
//                                Snackbar snack = Snackbar.make(mEventDescription, "Request not Sent. Check Internet Connection", Snackbar.LENGTH_LONG);
//                                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
//                                snackBarText.setTextColor(Color.WHITE);
//                                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
//                                snack.show();
//                            } else {
//
//                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                                        "mailto", "zconnectinc@gmail.com", null));
//                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Request Permission to add Events");
//                                startActivity(Intent.createChooser(emailIntent, "Send email..."));
//
//
//                            }
//
//                            dialog.dismiss();
//                        }
//                    });
//                    // Set other dialog properties
//
//
//                    // Create the AlertDialog
//                    AlertDialog dialog = builder.create();
//                    startPosting(flag);
//                    dialog.show();

                // }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void startPosting(final boolean flag) {

        mProgress.setMessage("Posting Event..");
        mProgress.show();
        final String eventNameValue = mEventName.getText().toString().trim();
        final String eventDescriptionValue = mEventDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(eventNameValue) && !TextUtils.isEmpty(eventDescriptionValue) && mImageUri != null && eventDate != null && dateString != null) {
            //1
            final StorageReference filepath = mStorage.child("EventImage").child(mImageUri.getLastPathSegment() + mAuth.getCurrentUser().getUid());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    if (flag) {
                        DatabaseReference newPost = mDatabaseVerified.push();
                        String key = newPost.getKey();
                        newPost.child("Key").setValue(key);
                        newPost.child("EventName").setValue(eventNameValue);
                        newPost.child("EventDescription").setValue(eventDescriptionValue);
                        newPost.child("EventImage").setValue(downloadUri.toString());
                        newPost.child("EventDate").setValue(eventDate);
                        newPost.child("FormatDate").setValue(dateString);

                        //For Everything
                        DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("everything").push();
                        newPost2.child("Title").setValue(eventNameValue);
                        newPost2.child("Description").setValue(eventDescriptionValue);
                        newPost2.child("Url").setValue(downloadUri.toString());
                        newPost2.child("multiUse2").setValue(eventDate);
                        newPost2.child("multiUse1").setValue(dateString);
                        newPost2.child("type").setValue("E");

                    } else {
                        DatabaseReference newPost = mDatabase.push();
                        String key = newPost.getKey();
                        newPost.child("Key").setValue(key);
                        newPost.child("EventName").setValue(eventNameValue);
                        newPost.child("EventDescription").setValue(eventDescriptionValue);
                        newPost.child("EventImage").setValue(downloadUri.toString());
                        newPost.child("EventDate").setValue(eventDate);
                        newPost.child("FormatDate").setValue(dateString);
                    }

                    mProgress.dismiss();
                    if (!flag) {
                        Snackbar snack = Snackbar.make(mEventDescription, "Event sent for verification !!", Snackbar.LENGTH_LONG);
                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackBarText.setTextColor(Color.WHITE);
                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                        snack.show();
                    }
                    Intent intent = new Intent(AddEvent.this, AllEvents.class);
                    if (!flag) {
                        intent.putExtra("snackbar", "true");
                    }
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Snackbar snack = Snackbar.make(mEventDescription, "Fields are empty. Can't Add Event.", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
            snack.show();
            mProgress.dismiss();
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setSnapRadius(2)
                    .setAspectRatio(3, 2)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {
                    mImageUri = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 600, 400, true);
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



