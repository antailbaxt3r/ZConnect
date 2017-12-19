package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import mabbas007.tagsedittext.TagsEditText;

public class EditProfileActivity extends BaseActivity implements TagsEditText.TagsEditListener, View.OnClickListener {

    private static final int GALLERY_REQUEST = 7;
    private static final String DEFAULT_PHOTO_URL = "https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/PhonebookImage%2FdefaultprofilePhone.png?alt=media&token=5f814762-16dc-4dfb-ba7d-bcff0de7a336";
    private final String TAG = getClass().getSimpleName();
    public ProgressDialog mProgress;
    @BindView(R.id.toolbar_app_bar_home)
    Toolbar toolbar;
    @BindView(R.id.contact_edit_details_editText)
    android.support.design.widget.TextInputEditText editTextDetails;
    @BindView(R.id.contact_edit_email_editText)
    android.support.design.widget.TextInputEditText editTextEmail;
    @BindView(R.id.contact_edit_name_editText)
    android.support.design.widget.TextInputEditText editTextName;
    @BindView(R.id.edit_text_number_content_edit_prof)
    EditText numberEt;
    @BindView(R.id.sdv_profile_pic_content_edit_prof)
    SimpleDraweeView profileImageSdv;
    @BindView(R.id.skillsTags)
    TagsEditText skillTags;
    @BindView(R.id.spinner1)
    CustomSpinner spinner;
    private String userEmail;
    private String userName;
    private String desc;
    private String imageUrl;
    private String number;
    private String hostel;
    private String host;
    private String category;
    private String skills;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private FirebaseUser mUser;
    private DatabaseReference phoneBookDbRef;
    private boolean newContact = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProgress = new ProgressDialog(this);

        Typeface ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        editTextName.setTypeface(ralewayRegular);
        editTextDetails.setTypeface(ralewayRegular);
        numberEt.setTypeface(ralewayRegular);
        skillTags.setTypeface(ralewayRegular);
        editTextEmail.setTypeface(ralewayRegular);

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

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
            finish();
        }
        userEmail = mUser.getEmail();
        userName = mUser.getDisplayName();
        if (mUser.getPhotoUrl() != null) imageUrl = mUser.getPhotoUrl().toString();
        else imageUrl = DEFAULT_PHOTO_URL;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        phoneBookDbRef = FirebaseDatabase.getInstance().getReference().child("Phonebook");
        phoneBookDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child :
                        dataSnapshot.getChildren()) {
                    if (userEmail.equals(child.child("email").getValue(String.class))) {
                        newContact = false;
                        PhonebookDisplayItem item = child.getValue(PhonebookDisplayItem.class);
                        if (item == null) {
                            Log.e(TAG, "onDataChange: could not convert data to class");
                            continue;
                        }
                        if (item.getName() != null && "".equals(item.getName())) {
                            userName = item.getName();
                        }
                        desc = item.getDesc();
                        number = item.getNumber();
                        if (item.getImageurl() != null
                                && !DEFAULT_PHOTO_URL.equals(item.getImageurl())
                                && !"".equals(item.getImageurl())) {
                            imageUrl = item.getImageurl();
                        }
                        if (item.getHostel() != null
                                && !"".equals(item.getHostel())) {
                            hostel = item.getHostel();
                        }
                        if (item.getCategory() != null
                                && !"".equals(item.getCategory()))
                            category = item.getCategory();
                        skills = item.getSkills();
                        updateViews();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });

        updateViews();


        profileImageSdv.setOnClickListener(this);

        skillTags.setTagsListener(this);
        skillTags.setTagsWithSpacesEnabled(true);

        skillTags.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,/*TODO: load skills from firebase*/ getResources().getStringArray(R.array.skills)));
        skillTags.setThreshold(1);

        phoneBookDbRef.keepSynced(true);
        category = "S";
        host = "hostel";
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    private void updateViews() {
        if (getSupportActionBar() != null) {
            if (newContact) getSupportActionBar().setTitle("Add Contact");
            else getSupportActionBar().setTitle("Edit Profile");
        }
        editTextName.setText(userName);
        editTextEmail.setText(userEmail);
        numberEt.setText(number);
        if (imageUrl == null) imageUrl = DEFAULT_PHOTO_URL;
        if (mImageUri != null) profileImageSdv.setImageURI(mImageUri);
        else profileImageSdv.setImageURI(Uri.parse(imageUrl));

        editTextDetails.setText(desc);
        spinner.setSelection(getIndex(spinner, hostel));
        //TODO: save skills as array of strings
        if (skills == null || skills.equalsIgnoreCase("[]")) {
            skills = "";
        }
        if (!skills.equals("") || skills.indexOf(',') > 0) {
            String[] skillsArray = skills.split(",");
            skillsArray[0] = skillsArray[0].substring(1);
            skillsArray[skillsArray.length - 1] = skillsArray[skillsArray.length - 1]
                    .substring(0, skillsArray[skillsArray.length - 1].length() - 1);
            skillTags.setTags(skillsArray);
        } else {
            skillTags.setText(skills);
        }
        if (category != null) {
            switch (category) {
                case "S":
                    spinner.setVisibility(View.VISIBLE);
                    host = "hostel";
                    break;
                case "A":
                    spinner.setVisibility(View.GONE);
                    host = "none";
                    break;
                case "O":
                    spinner.setVisibility(View.GONE);
                    host = "none";
                    break;
            }
        }
    }

    private int getIndex(CustomSpinner spinner, String s) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(s)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if (!isNetworkAvailable(getApplicationContext())) {
                Snackbar snack = Snackbar.make(editTextDetails, "No Internet. Can't Add Contact.", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();
            } else {
                attemptUpdate();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setBackgroundColor(R.color.white)
                    .setSnapRadius(2)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    mImageUri = result.getUri();
                    if (mImageUri == null) {
                        Log.e(TAG, "onActivityResult: got empty imageUri");
                        return;
                    }
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    String path = MediaStore.Images.Media.insertImage(EditProfileActivity.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);
                    mImageUri = Uri.parse(path);
                    profileImageSdv.setImageURI(mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: ", result.getError());
            }
        }
    }

    public void attemptUpdate() {
        mProgress.setMessage("Updating...");
        mProgress.show();
        userName = editTextName.getText().toString();
        userEmail = editTextEmail.getText().toString();
        desc = editTextDetails.getText().toString();
        number = numberEt.getText().toString();
        skills = skillTags.getTags().toString();
        if (userName == null
                || number == null
                || number.length() == 0) {
            Snackbar snackbar = Snackbar.make(editTextDetails, "Fields are empty. Can't Update details.", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
            snackbar.show();
            mProgress.dismiss();
        } else {
            //TODO: verify contactDescTv [IMP]
            final DatabaseReference newPost = phoneBookDbRef.child(number);
            newPost.child("name").setValue(userName);
            newPost.child("desc").setValue(desc);
            newPost.child("contactDescTv").setValue(number);
            newPost.child("category").setValue(category);
            newPost.child("email").setValue(userEmail);
            newPost.child("skills").setValue(skills);
            newPost.child("Uid").setValue(mUser.getUid().toString());
            if (host.equals("none")) {
                newPost.child("hostel").setValue(hostel);
            } else {
                host = String.valueOf(spinner.getSelectedItem());
                newPost.child("hostel").setValue(host);
            }
            if (mImageUri != null && mImageUri != mUser.getPhotoUrl()) {
                StorageReference filepath = mStorageRef.child("PhonebookImage").child(mImageUri.getLastPathSegment() + mUser.getUid());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        if (downloadUri == null) {
                            Log.e(TAG, "onSuccess: error got empty downloadUri");
                            return;
                        }
                        newPost.child("imageurl").setValue(downloadUri.toString());
                        finish();
                    }
                });
            } else {
                newPost.child("imageurl").setValue(imageUrl);
                finish();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //skillTags.showDropDown();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdv_profile_pic_content_edit_prof: {
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                }
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                break;
            }
        }
    }

    @Override
    public void onTagsChanged(Collection<String> collection) {
        /*required*/
    }

    @Override
    protected void onDestroy() {
        mProgress.dismiss(); /*Fix for window leak*/
        super.onDestroy();
    }

    @Override
    public void onEditingFinished() {
        /*required*/
    }
}
