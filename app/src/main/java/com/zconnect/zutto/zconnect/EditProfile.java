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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
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
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfile extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 7;
    String email;
    String name, details, imageurl, number = null, hostel = null, host, category;
    SimpleDraweeView simpleDraweeView;
    Boolean flag = false;
    private FirebaseAuth mAuth;
    private Uri mImageUri = null;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private android.support.design.widget.TextInputEditText editTextName;
    private android.support.design.widget.TextInputEditText editTextEmail;
    private android.support.design.widget.TextInputEditText editTextDetails;
    private android.support.design.widget.TextInputEditText editTextNumber;
    private ProgressDialog mProgress;
    private CustomSpinner spinner;
    // private RadioButton radioButtonS, radioButtonA, radioButtonO;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Phonebook");
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Phonebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        editTextDetails = (TextInputEditText) findViewById(R.id.contact_edit_details_editText);
        editTextEmail = (TextInputEditText) findViewById(R.id.contact_edit_email_editText);
        editTextName = (TextInputEditText) findViewById(R.id.contact_edit_name_editText);
        editTextNumber = (TextInputEditText) findViewById(R.id.contact_edit_number_editText);
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.contact_edit_image);

        spinner = (CustomSpinner) findViewById(R.id.spinner1);
//        Log.v("tag",email);
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
        editTextNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "To add contact number go to Infone.", Toast.LENGTH_SHORT).show();
            }
        });
        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        EditProfile.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            EditProfile.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                }
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    PhonebookDisplayItem phonebookDisplayItem = shot.getValue(PhonebookDisplayItem.class);
                    if (email != null) {
                        if (phonebookDisplayItem.getEmail().equals(email)) {
                            name = phonebookDisplayItem.getName();
                            details = phonebookDisplayItem.getDesc();
                            number = phonebookDisplayItem.getNumber();
                            imageurl = phonebookDisplayItem.getImageurl();
                            hostel = phonebookDisplayItem.getHostel();
                            category = phonebookDisplayItem.getCategory();
                            editTextName.setText(name);
                            editTextNumber.setText(number);
                            simpleDraweeView.setImageURI(Uri.parse(imageurl));
                            editTextDetails.setText(details);
                            spinner.setSelection(getIndex(spinner, hostel));

                            //private method of your class

                            if (category.equals("S")) {
                                //radioButtonS.setChecked(true);
                                spinner.setVisibility(View.VISIBLE);
                                host = "hostel";
                            } else if (category.equals("A")) {
                                //radioButtonA.setChecked(true);
                                spinner.setVisibility(View.INVISIBLE);
                                host = "none";
                            } else if (category.equals("O")) {
                                //radioButtonO.setChecked(true);
                                spinner.setVisibility(View.INVISIBLE);
                                host = "none";
                            }
                        }
                    }
                    if (number == null) {
                        flag = true;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.keepSynced(true);
        category = "S";
        host = "hostel";
        spinner.setVisibility(View.VISIBLE);

    }

    private int getIndex(CustomSpinner spinner, String strin) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(strin)) {
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    String path = MediaStore.Images.Media.insertImage(EditProfile.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);
                    simpleDraweeView.setImageURI(mImageUri);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        email = mAuth.getCurrentUser().getEmail();
        if (email != null)
        editTextEmail.setText(email);

    }

    public void startposting() {
        mProgress.setMessage("Adding...");
        mProgress.show();
        name = editTextName.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        details = editTextDetails.getText().toString().trim();
        number = editTextNumber.getText().toString().trim();
        if (flag) {
            Snackbar snack = Snackbar.make(editTextDetails, "Fields are empty. Can't Update details.", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
            snack.show();
            mProgress.dismiss();
        } else {
        if (name != null && number != null && details != null && mImageUri != null) {
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
                    if (host.equals("none")) {
                        newPost.child("hostel").setValue(hostel);
                    } else {
                        host = String.valueOf(spinner.getSelectedItem());
                        newPost.child("hostel").setValue(host);
                    }

                    mProgress.dismiss();
                    startActivity(new Intent(EditProfile.this, home.class));
                }
            });
        } else if (name != null && number != null && details != null && imageurl != null) {
            DatabaseReference newPost = ref.child(number);

            newPost.child("name").setValue(name);
            newPost.child("desc").setValue(details);
            newPost.child("imageurl").setValue(imageurl);
            newPost.child("number").setValue(number);
            newPost.child("category").setValue(category);
            newPost.child("email").setValue(email);
            if (host.equals("none")) {
                newPost.child("hostel").setValue(hostel);
            } else {
                host = String.valueOf(spinner.getSelectedItem());
                newPost.child("hostel").setValue(host);
            }

            mProgress.dismiss();
            startActivity(new Intent(EditProfile.this, home.class));
        } else {
            Snackbar snack = Snackbar.make(editTextDetails, "Fields are empty. Can't Update details.", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
            snack.show();
            mProgress.dismiss();


        }
        }
    }

}
