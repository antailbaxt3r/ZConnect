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
import android.support.annotation.NonNull;
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
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.Timer;
import java.util.TimerTask;

public class AddContact extends AppCompatActivity {
    public static final int SELECT_PICTURE = 1;
    private static final int GALLERY_REQUEST = 7;
    SimpleDraweeView image;
    private Context context = this;
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
    private RadioButton radioButtonS, radioButtonA, radioButtonO;
    private String name, email, details, number, hostel, category = null;
    private CustomSpinner spinner;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        mProgress = new ProgressDialog(this);
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
        image = (SimpleDraweeView) findViewById(R.id.contact_image);
        editTextDetails = (TextInputEditText) findViewById(R.id.contact_details_editText);
        editTextEmail = (TextInputEditText) findViewById(R.id.contact_email_editText);
        editTextName = (TextInputEditText) findViewById(R.id.contact_name_editText);
        //Set name of person
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();
        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editTextName.setText(dataSnapshot.child("Username").getValue().toString());
                editTextEmail.setText((dataSnapshot.child("Email").getValue().toString()));
                Uri downloadUri = Uri.parse(dataSnapshot.child("Image").getValue().toString());
                image.setImageURI(downloadUri);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userRef.child(userId).addValueEventListener(listener);
        editTextNumber = (TextInputEditText) findViewById(R.id.contact_number_editText);
        radioButtonS = (RadioButton) findViewById(R.id.radioButton);
        radioButtonA = (RadioButton) findViewById(R.id.radioButton2);
        radioButtonO = (RadioButton) findViewById(R.id.radioButton3);
        spinner = (CustomSpinner) findViewById(R.id.spinner);
        radioButtonS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "S";
                cat = "Student";
                hostel = "hostel";
                spinner.setVisibility(View.VISIBLE);
            }
        });

        radioButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "A";
                cat = "Admin";
                hostel = "none";
                spinner.setVisibility(View.INVISIBLE);
            }
        });


        radioButtonO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "O";
                cat = "Others";
                hostel = "none";
                spinner.setVisibility(View.INVISIBLE);

            }
        });

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
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

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
                mProgress.setMessage("Adding...");
                mProgress.show();
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
        name = editTextName.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        details = editTextDetails.getText().toString().trim();
        number = editTextNumber.getText().toString().trim();
        if (name != null && number != null && email != null && details != null && cat != null && category != null && hostel != null) {
            if (mImageUri == null) {
                userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Uri downloadUri = Uri.parse(dataSnapshot.child("Image").getValue().toString());
                        storeData(downloadUri);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {

                StorageReference filepath = mStorage.child("PhonebookImage").child(mImageUri.getLastPathSegment() + mAuth.getCurrentUser().getUid());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storeData(taskSnapshot.getDownloadUrl());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snack = Snackbar.make(editTextDetails, "Error , please retry.", Snackbar.LENGTH_LONG);
                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackBarText.setTextColor(Color.WHITE);
                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                        snack.show();
                    mProgress.dismiss();

                }
            });
            }
        } else {
            Snackbar snack = Snackbar.make(editTextDetails, "Fields are empty. Can't add contact.", Snackbar.LENGTH_LONG);
            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
            snack.show();
            mProgress.dismiss();
        }


    }

    void storeData(Uri downloadUri) {
        DatabaseReference newPost = ref.child(number);
        newPost.child("name").setValue(name);
        newPost.child("desc").setValue(details);
        newPost.child("imageurl").setValue(downloadUri.toString());
        newPost.child("number").setValue(number);
        newPost.child("category").setValue(category);
        newPost.child("email").setValue(email);
        if (hostel.equals("none")) {
            newPost.child("hostel").setValue(hostel);
        } else {
            hostel = String.valueOf(spinner.getSelectedItem());
            newPost.child("hostel").setValue(hostel);
        }

        new Timer().schedule(new TimerTask() {
            public void run() {
                mProgress.dismiss();
                Snackbar snack = Snackbar.make(editTextDetails, "Contact Added.", Snackbar.LENGTH_INDEFINITE);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        startActivity(new Intent(AddContact.this, Phonebook.class));
                        finish();
                    }
                }, 1400);

                finish();
            }
        }, 1000);

    }
}
