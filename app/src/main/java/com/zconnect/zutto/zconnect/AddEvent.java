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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.util.HashMap;
import java.util.Map;

public class AddEvent extends BaseActivity {
    private static final int GALLERY_REQUEST = 7;
    String eventDate;
    String dateString;
    // private Button mPostBtn;
    Intent eventVenue;
    Place Venue;
    Boolean selectedFromMap = false;
    boolean flag = false;
    DatabaseReference mFeaturesStats;
    private Uri mImageUri = null;
    private SimpleDraweeView mAddImage;
    private EditText mEventName;
    private EditText mEventDescription;
    private EditText mVenue;
    private ImageView mDirections;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseVerified;
    private DatabaseReference mDatabase;
    private LinearLayout CalendarButton;
    private ProgressDialog mProgress;
    private TextView dateTime;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            eventDate = date.toString();
            dateString = String.valueOf(date.getTime());
            dateTime.setText(eventDate);

        }
    };

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        LatLngBounds bitsGoa = new LatLngBounds(new LatLng(15.386095, 73.876165), new LatLng(15.396108, 73.878407));
        builder.setLatLngBounds(bitsGoa);
        try {
            eventVenue = builder.build(this);
        } catch (Exception e) {
            Snackbar snack = Snackbar.make(mEventDescription, "Cannot open Maps , Please input your venue.", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
            snack.show();
            e.printStackTrace();
        }

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
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        mAddImage = (SimpleDraweeView) findViewById(R.id.imageButton);
        mEventName = (EditText) findViewById(R.id.name);
        mEventDescription = (EditText) findViewById(R.id.description);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAddImage.setImageURI(Uri.parse("res:///" + R.drawable.addimage));
        mDatabaseVerified = FirebaseDatabase.getInstance().getReference().child("Event/VerifiedPosts");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Event/NotVerifiedPosts");

        mAddImage.setImageURI(Uri.parse("res:///" + R.drawable.addimage));
        CalendarButton = (LinearLayout) findViewById(R.id.dateAndTime);
        mVenue = (EditText) findViewById(R.id.VenueText);
        mDirections = (ImageView) findViewById(R.id.venuePicker);
        dateTime = (TextView) findViewById(R.id.dateText);

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

        mDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(eventVenue, 124);
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
        mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("Stats");
        final String eventNameValue = mEventName.getText().toString().trim();
        final String eventDescriptionValue = mEventDescription.getText().toString().trim();
        final String eventVenue = mVenue.getText().toString();

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
                        newPost.child("Venue").setValue(eventVenue);
                        LatLng latLng = selectedFromMap ? Venue.getLatLng() : new LatLng(0, 0);
                        newPost.child("Key").setValue(newPost.getKey());
                        newPost.child("log").setValue(latLng.longitude);
                        newPost.child("lat").setValue(latLng.latitude);

                        //For Everything
                        DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("home").push();
                        newPost2.child("name").setValue(eventNameValue);
                        newPost2.child("desc").setValue(eventDescriptionValue);
                        newPost2.child("imageurl").setValue(downloadUri.toString());
                        newPost2.child("feature").setValue("Event");
                        newPost2.child("id").setValue(key);
                        newPost2.child("desc2").setValue(eventDate);

                        // Adding stats

                        mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Integer TotalEvents = Integer.parseInt(dataSnapshot.child("TotalEvents").getValue().toString());
                                TotalEvents = TotalEvents + 1;
                                DatabaseReference newPost = mFeaturesStats;
                                Map<String, Object> taskMap = new HashMap<String, Object>();
                                taskMap.put("TotalEvents", TotalEvents);
                                newPost.updateChildren(taskMap);
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {
                        DatabaseReference newPost = mDatabase.push();
                        String key = newPost.getKey();
                        newPost.child("Key").setValue(key);
                        newPost.child("EventName").setValue(eventNameValue);
                        newPost.child("EventDescription").setValue(eventDescriptionValue);
                        newPost.child("EventImage").setValue(downloadUri.toString());
                        newPost.child("EventDate").setValue(eventDate);
                        newPost.child("FormatDate").setValue(dateString);
                        newPost.child("Venue").setValue(eventVenue);
                        LatLng latLng = selectedFromMap ? Venue.getLatLng() : new LatLng(0, 0);
                        newPost.child("Key").setValue(newPost.getKey());
                        newPost.child("log").setValue(latLng.longitude);
                        newPost.child("lat").setValue(latLng.latitude);
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
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    Double ratio = Math.ceil(250000.0 / bitmap.getByteCount());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, (int) Math.min(ratio, 100), out);
                    String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    mAddImage.setImageURI(mImageUri);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == 124 && resultCode == RESULT_OK) {
            Venue = PlacePicker.getPlace(this, data);
            mVenue.setText(Venue.getName().toString() + Venue.getAddress());
            selectedFromMap = true;
        }

    }
}



