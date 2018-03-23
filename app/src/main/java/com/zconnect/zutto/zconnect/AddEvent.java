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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zconnect.zutto.zconnect.KeyHelper.KEY_EVENT;

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
    private ImageView mAddImage;
    private EditText mEventName;
    private EditText mEventDescription;
    private AutoCompleteTextView mVenue;
    private ImageView mDirections;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseVerified;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseVenues;
    private LinearLayout CalendarButton;
    private ProgressDialog mProgress;
    private TextView dateTime;
    private Boolean editImageflag = false;
    private Long eventTimeMillis;
    private CheckBox gmapLocationTaken;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            eventDate = date.toString();
            dateString = String.valueOf(date.getTime());
            dateTime.setText(eventDate);

        }
    };

    ArrayList<String> venueOptions = new ArrayList<>();

    private IntentHandle intentHandle = new IntentHandle();

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
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
        mAddImage = (ImageView) findViewById(R.id.imageButton);
        mEventName = (EditText) findViewById(R.id.name);
        mEventDescription = (EditText) findViewById(R.id.description);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAddImage.setImageURI(Uri.parse("res:///" + R.drawable.addimage));
        gmapLocationTaken=(CheckBox) findViewById(R.id.add_events_location_checkbox);
        mDatabaseVerified = FirebaseDatabase.getInstance().getReference().child("Event/VerifiedPosts");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Event/NotVerifiedPosts");

        gmapLocationTaken.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
        String EventID = null;
        if (bundle != null) {
            EventID = bundle.getString("eventID");
        }

        Toast.makeText(this, EventID, Toast.LENGTH_SHORT).show();

        mAddImage.setImageURI(Uri.parse("res:///" + R.drawable.addimage));
        CalendarButton = (LinearLayout) findViewById(R.id.dateAndTime);
        mVenue = (AutoCompleteTextView) findViewById(R.id.VenueText);
        mDirections = (ImageView) findViewById(R.id.venuePicker);
        dateTime = (TextView) findViewById(R.id.dateText);

        mProgress = new ProgressDialog(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
                Intent galleryIntent = intentHandle.getPickImageIntent(AddEvent.this);
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
        if (EventID != null) {
            mDatabaseVerified.child(EventID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mEventName.setText(dataSnapshot.child("EventName").getValue().toString());
                    mEventDescription.setText(dataSnapshot.child("EventDescription").getValue().toString());
                    mVenue.setText(dataSnapshot.child("Venue").getValue().toString());
                    dateTime.setText(dataSnapshot.child("EventDate").getValue().toString());
                    eventDate = dataSnapshot.child("EventDate").getValue().toString();
                    Picasso.with(getApplicationContext()).load(dataSnapshot.child("EventImage").getValue().toString()).into(mAddImage);
                    mImageUri = Uri.parse(dataSnapshot.child("EventImage").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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
                    if (child.getValue()!=null && child.getValue().equals(emailId)) {
                        flag = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        // to get venues from firebase depending on college location( venue names only)
        // which will then be given as auto correct options in the edit text for event venue
        mDatabaseVenues= FirebaseDatabase.getInstance().getReference().child("Event/Venues/");

        mDatabaseVenues.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                int i=0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    if(child.getValue()!=null){
                        venueOptions.add(i,child.getValue().toString());
                        i++;
                    }
                    else {
                        venueOptions.add("Auditorium");
                        venueOptions.add("Main lawns");
                        break;
                    }
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddEvent.this,
                        android.R.layout.select_dialog_item, venueOptions);
                mVenue.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.e("AddEvent"," Venue database fetch Cancelled");
                venueOptions.add("Auditorium");
                venueOptions.add("Main lawns");

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
        Bundle bundle = getIntent().getExtras();
        String EventID = null;
        if (bundle != null) {
            EventID = bundle.getString("eventID");
        }



        int id = item.getItemId();
        if (id == R.id.action_done) {
            if (!isNetworkAvailable(getApplicationContext())) {

                Snackbar snack = Snackbar.make(mEventDescription, "No Internet. Can't Add Event.", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();

            } else {
                if (EventID == null) {
                    startPosting(flag, false);
                } else {
                    startPosting(flag, true);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void startPosting(final boolean flag, final boolean edit) {

        Bundle bundle = getIntent().getExtras();
        String eventID = null;
        if (bundle != null) {
            eventID = bundle.getString("eventID");
        }

        final String EventID = eventID;

        mProgress.setMessage("Posting Event..");
        mProgress.show();
        mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("Stats");
        final String eventNameValue = mEventName.getText().toString().trim();
        final String eventDescriptionValue = mEventDescription.getText().toString().trim();
        final String eventVenue = mVenue.getText().toString();

        if (!edit) {
            if (!TextUtils.isEmpty(eventNameValue) && !TextUtils.isEmpty(eventDescriptionValue) && mImageUri != null && eventDate != null) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                String formattedDate = df.format(c.getTime());
                final StorageReference filepath = mStorage.child("EventImage").child(formattedDate + mImageUri.getLastPathSegment() + mAuth.getCurrentUser().getUid());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        if (downloadUri == null)
                            downloadUri = Uri.parse("");
                        if (flag) {
                            DatabaseReference newPost = mDatabaseVerified.push();
                            String key = newPost.getKey();
                            newPost.child("Key").setValue(key);
                            newPost.child("Creator").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                            newPost.child("UserID").setValue(mAuth.getCurrentUser().getUid());
                            newPost.child("Verified").setValue("true");
                            newPost.child("BoostCount").setValue(0);

                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                            try {
                                eventTimeMillis = sdf.parse(eventDate).getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            newPost.child("EventTimeMillis").setValue(eventTimeMillis);


                            //For Everything
                            DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("home").push();
                            newPost2.child("name").setValue(eventNameValue);
                            newPost2.child("desc").setValue(eventDescriptionValue);
                            newPost2.child("imageurl").setValue(downloadUri.toString());
                            newPost2.child("feature").setValue("Event");
                            newPost2.child("id").setValue(key);
                            newPost2.child("desc2").setValue(eventDate);

                            // Adding stats
                            CounterManager.addEventVerified(key, eventNameValue);
                            mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Object o = dataSnapshot.child("TotalEvents").getValue();
                                    if (o == null)
                                        o = "0";
                                    Integer TotalEvents = Integer.parseInt(o.toString());
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

                            //Sending Notifications
                            NotificationSender notificationSender=new NotificationSender(key,null,eventNameValue,String.valueOf(System.currentTimeMillis()),null,null,KEY_EVENT,false,false);
                            notificationSender.execute();


                        } else {
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
                            newPost.child("Verified").setValue("false");
                            newPost.child("UserID").setValue(mAuth.getCurrentUser().getUid());
                            newPost.child("BoostCount").setValue(0);

                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                            try {
                                eventTimeMillis = sdf.parse(eventDate).getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            newPost.child("EventTimeMillis").setValue(eventTimeMillis);

                            CounterManager.addEventUnVerified(key, eventNameValue);

                            //For Everything
                            DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("home").push();
                            newPost2.child("name").setValue(eventNameValue);
                            newPost2.child("desc").setValue(eventDescriptionValue);
                            newPost2.child("imageurl").setValue(downloadUri.toString());
                            newPost2.child("feature").setValue("Event");
                            newPost2.child("id").setValue(key);
                            newPost2.child("desc2").setValue(eventDate);

                            //Sending Notifications
                            NotificationSender notificationSender=new NotificationSender(key,null,eventNameValue,String.valueOf(System.currentTimeMillis()),null,null,KEY_EVENT,false,false);
                            notificationSender.execute();

                        }

                        mProgress.dismiss();
                        if (!flag) {
                            Snackbar snack = Snackbar.make(mEventDescription, "Event sent for verification !!", Snackbar.LENGTH_LONG);
                            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.WHITE);
                            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                            snack.show();
                        }
                        Intent intent = new Intent(AddEvent.this, TabbedEvents.class);
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

        } else {
            if (editImageflag) {
                if (!TextUtils.isEmpty(eventNameValue) && !TextUtils.isEmpty(eventDescriptionValue) && mImageUri != null && eventDate != null) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String formattedDate = df.format(c.getTime());
                    final StorageReference filepath = mStorage.child("EventImage").child(formattedDate + mImageUri.getLastPathSegment() + mAuth.getCurrentUser().getUid());
                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            if (downloadUri == null)
                                downloadUri = Uri.parse("");

                            DatabaseReference mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event/VerifiedPosts").child(EventID);
                            Map<String, Object> taskMap = new HashMap<String, Object>();

                            taskMap.put("EventName", eventNameValue);
                            taskMap.put("EventDescription", eventDescriptionValue);
                            taskMap.put("EventImage", downloadUri.toString());
                            taskMap.put("EventDate", eventDate);
                            taskMap.put("FormatDate", dateString);
                            taskMap.put("Venue", eventVenue);
                            LatLng latLng = selectedFromMap ? Venue.getLatLng() : new LatLng(0, 0);
                            taskMap.put("log", latLng.longitude);
                            taskMap.put("lat", latLng.latitude);

                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                            try {
                                eventTimeMillis = sdf.parse(eventDate).getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            taskMap.put("EventTimeMillis", eventTimeMillis);

                            //Sending Notifications
                            NotificationSender notificationSender=new NotificationSender(EventID,null,eventNameValue,String.valueOf(System.currentTimeMillis()),null,null,KEY_EVENT,false,false);
                            notificationSender.execute();

                            mEventDatabase.updateChildren(taskMap);
                            mProgress.dismiss();
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
            } else {
                if (!TextUtils.isEmpty(eventNameValue) && !TextUtils.isEmpty(eventDescriptionValue) && mImageUri != null && eventDate != null) {

                    DatabaseReference mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event/VerifiedPosts").child(EventID);
                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put("EventName", eventNameValue);
                    taskMap.put("EventDescription", eventDescriptionValue);
                    taskMap.put("EventDate", eventDate);
                    taskMap.put("FormatDate", dateString);
                    taskMap.put("Venue", eventVenue);
                    LatLng latLng = selectedFromMap ? Venue.getLatLng() : new LatLng(0, 0);
                    taskMap.put("log", latLng.longitude);
                    taskMap.put("lat", latLng.latitude);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                    try {
                        eventTimeMillis = sdf.parse(eventDate).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    taskMap.put("EventTimeMillis", eventTimeMillis);


                    //Sending Notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(KEY_EVENT);
                    NotificationSender notificationSender=new NotificationSender(EventID,null,eventNameValue,String.valueOf(System.currentTimeMillis()),null,null,KEY_EVENT,false,false);
                    notificationSender.execute();


                    mEventDatabase.updateChildren(taskMap);
                    mProgress.dismiss();
                }

            }

            Intent intent = new Intent(AddEvent.this, TabbedEvents.class);
            if (!flag) {
                intent.putExtra("snackbar", "true");
            }
            startActivity(intent);
            finish();

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
            Uri imageUri = intentHandle.getPickImageResultUri(data);
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
                    String path = MediaStore.Images.Media.insertImage(AddEvent.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    editImageflag = true;
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

        if (requestCode == 124 && resultCode == RESULT_OK) {
            Venue = PlacePicker.getPlace(this, data);
            String addr = Venue.getName().toString() + Venue.getAddress();
            //mVenue.setText(addr);
            gmapLocationTaken.setVisibility(View.VISIBLE);
            gmapLocationTaken.setChecked(true);
            selectedFromMap = true;
        }

    }
}

