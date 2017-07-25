package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mabbas007.tagsedittext.TagsEditText;

public class AddContact extends BaseActivity implements TagsEditText.TagsEditListener{
    public static final int SELECT_PICTURE = 1;
    private static final int GALLERY_REQUEST = 7;
    SimpleDraweeView image;
    Intent galleryIntent;  //Intent to get image
    IntentHandle intentHandle; //Object which makes camera intent
    DatabaseReference mFeaturesStats;
    private android.support.design.widget.TextInputEditText editTextName;
    private android.support.design.widget.TextInputEditText editTextEmail;
    private android.support.design.widget.TextInputEditText editTextDetails;
    private android.support.design.widget.TextInputEditText editTextNumber;
    private String cat;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Phonebook");
    private Uri mImageUri = null;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    private ProgressDialog mProgress;
    // private RadioButton radioButtonS, radioButtonA, radioButtonO;
    private String name, email, details, number, hostel, category = null,skills;
    private CustomSpinner spinner;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TagsEditText skillTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        mProgress = new ProgressDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intentHandle = (new IntentHandle()); //Init intent object
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
        editTextDetails = (TextInputEditText) findViewById(R.id.contact_details_editText);
        editTextEmail = (TextInputEditText) findViewById(R.id.contact_email_editText);
        editTextName = (TextInputEditText) findViewById(R.id.contact_name_editText);
        //Set name of person
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String userId = mUser.getUid();
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editTextName.setText(dataSnapshot.child("Username").getValue().toString());
                editTextEmail.setText((dataSnapshot.child("Email").getValue().toString()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        editTextNumber = (TextInputEditText) findViewById(R.id.contact_number_editText);
        image = (SimpleDraweeView) findViewById(R.id.contact_image);
        spinner = (CustomSpinner) findViewById(R.id.spinner);

        category = "S";
        cat = "Student";
        hostel = "hostel";
        spinner.setVisibility(View.VISIBLE);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        AddContact.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AddContact.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                }
                galleryIntent = intentHandle.getPickImageIntent(getApplicationContext()); //Get intent to create chooser .
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        skillTags =(TagsEditText) findViewById(R.id.skillsTags);
        skillTags.setTagsListener(this);
        skillTags.setTagsWithSpacesEnabled(true);

        skillTags.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.skills)));
        skillTags.setThreshold(1);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data); //Get data
            CropImage.activity(imageUri)
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                    String path = MediaStore.Images.Media.insertImage(AddContact.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    image.setImageURI(mImageUri);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if (!isNetworkAvailable(getApplicationContext())) {

                Snackbar snack = Snackbar.make(editTextDetails, "No Internet. Can't Add Contact.", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();

            } else {
                startposting();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void startposting() {
        mProgress.setMessage("Adding...");
        mProgress.show();
        name = editTextName.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        details = editTextDetails.getText().toString().trim();
        number = editTextNumber.getText().toString().trim();
        hostel = String.valueOf(spinner.getSelectedItem());
        skills= skillTags.getTags().toString().trim();
        //  Log.v("tag",hostel);
        mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("Stats");

        if (name != null && number != null && email != null && details != null && cat != null && category != null && spinner.getSelectedItem() != null && mImageUri != null && !skills.equals("")) {
            StorageReference filepath = mStorage.child("PhonebookImage").child(mImageUri.getLastPathSegment() + mAuth.getCurrentUser().getUid());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = ref.child(number);

                    newPost.child("name").setValue(name);
                    newPost.child("desc").setValue(details);
                    newPost.child("imageurl").setValue(downloadUri.toString());
                    newPost.child("number").setValue(number);
                    newPost.child("category").setValue(category);
                    newPost.child("email").setValue(email);
                    newPost.child("hostel").setValue(hostel);
                    newPost.child("skills").setValue(skills);


                    mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Integer TotalNumbers = Integer.parseInt(dataSnapshot.child("TotalNumbers").getValue().toString());
                            TotalNumbers = TotalNumbers + 1;
                            DatabaseReference newPost = mFeaturesStats;
                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("TotalNumbers", TotalNumbers);
                            newPost.updateChildren(taskMap);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    mProgress.dismiss();
                    startActivity(new Intent(AddContact.this, Phonebook.class));
                    finish();
                }
            });
        } else {
            Snackbar snack = Snackbar.make(editTextDetails, "Fields are empty. Can't add contact.", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
            snack.show();
            mProgress.dismiss();


        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            skillTags.showDropDown();
        }
    }

    @Override
    public void onTagsChanged(Collection<String> tags) {

    }

    @Override
    public void onEditingFinished() {
        //Log.d(TAG,"OnEditing finished");
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(skillTags.getWindowToken(), 0);
//        //skillTags.clearFocus();
    }
}
