package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InfoneAddContactActivity extends AppCompatActivity {

    String catId,catName;
    private SharedPreferences communitySP;
    public String communityReference;
    DatabaseReference databaseReferenceInfone;
    DatabaseReference databaseRecents;
    DatabaseReference newContactRef;
    EditText nameEt;
    EditText phone1Et, phone2Et;
    Button saveButton;
    SimpleDraweeView addImage;
    String key;
    private Long postTimeMillis;
    private final String TAG = getClass().getSimpleName();

    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 7;
    private Uri mImageUriSmall;
    private StorageReference mStorageRef;
    private DatabaseReference newContactNumRef;
    private DatabaseReference mPostedByDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_infone_add_contact);

        catId = getIntent().getExtras().getString("catId");
        catName = getIntent().getExtras().getString("catName");

        Toast.makeText(this, catName, Toast.LENGTH_SHORT).show();

        nameEt = (EditText) findViewById(R.id.name_et_infone_add);
        phone1Et = (EditText) findViewById(R.id.phone_et_infone_add);
        phone2Et = (EditText) findViewById(R.id.phone2_et_infone_add);
        saveButton = (Button) findViewById(R.id.btn_save_infone_add);
        addImage = (SimpleDraweeView) findViewById(R.id.image_add_infone_add);

        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceInfone = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("infone");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        databaseRecents = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");
        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String name = nameEt.getText().toString();
            String phoneNum1 = phone1Et.getText().toString();
            String phoneNum2 = phone2Et.getText().toString();

            if (phoneNum1.isEmpty() && !phoneNum2.isEmpty()) {
                phoneNum1 = phoneNum2;
                phoneNum2 = "";
            }

            Log.e(TAG, "data cat name:" + catId);

            if (!name.isEmpty() && !phoneNum1.isEmpty()) {
                postTimeMillis = System.currentTimeMillis();
                key = databaseReferenceInfone.child("numbers").push().getKey();
                newContactNumRef = databaseReferenceInfone.child("numbers").child(key);
                newContactRef = databaseReferenceInfone.child("categories").child(catId).child(key);

                Log.e(TAG, "data phone:" + key + " " + phoneNum1);

                //Inside Categories
                newContactRef.child("name").setValue(name);
                newContactRef.child("phone").child("0").setValue(phoneNum1);
                newContactRef.child("phone").child("1").setValue(phoneNum2);
                newContactRef.child("key").child(key);

                //Inside Contacts
                newContactNumRef.child("category").setValue(catId);
                newContactNumRef.child("key").child(key);
                newContactNumRef.child("name").setValue(name);
                newContactNumRef.child("phone").child("0").setValue(phoneNum1);
                newContactNumRef.child("phone").child("1").setValue(phoneNum2);
                newContactNumRef.child("type").setValue("NotUser");
                newContactNumRef.child("validCount").setValue(0);
                newContactNumRef.child("verifiedDate").setValue(postTimeMillis);
                newContactNumRef.child("PostTimeMillis").setValue(postTimeMillis);
                newContactNumRef.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                uploadImage();

                //Inside Recents
                final DatabaseReference recentsPost = databaseRecents.push();
                final DatabaseReference recentsPostPostedBy = recentsPost.child("PostedBy");

                recentsPost.child("infoneContactName").setValue(name);
                recentsPostPostedBy.setValue(null);
                recentsPost.child("infoneContactCategoryName").setValue(catName);
                recentsPost.child("id").setValue(key);
                recentsPost.child("feature").setValue("Infone");
                recentsPost.child("PostTimeMillis").setValue(postTimeMillis);
                recentsPostPostedBy.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        recentsPostPostedBy.child("Username").setValue(dataSnapshot.child("Username").getValue().toString());
                        newContactNumRef.child("PostedBy").child("Username").setValue(dataSnapshot.child("Username").getValue().toString());
                        //needs to be changed after image thumbnail is put
                        recentsPostPostedBy.child("ImageThumb").setValue(dataSnapshot.child("Image").getValue().toString());
                        newContactNumRef.child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child("Image").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            }
        });

        if (mImageUri == null) {

            addImage.setActualImageResource(R.mipmap.ic_launcher);

        }

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(InfoneAddContactActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            InfoneAddContactActivity.this,
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

    private void uploadImage() {

        if (mImageUri != null) {
            StorageReference filepath = mStorageRef.child("InfoneImage").child(mImageUri.getLastPathSegment() + key);
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    if (downloadUri == null) {
                        Log.e(TAG, "onSuccess: error got empty downloadUri");
                        return;
                    }
                    //newContactRef.child("imageurl").setValue(downloadUri.toString());
                    newContactNumRef.child("imageurl").setValue(downloadUri.toString());
                    finish();
                }
            });
            StorageReference filepathThumb = mStorageRef.child("InfoneImageSmall").child(mImageUriSmall.getLastPathSegment() + key + "Thumbnail");
            filepathThumb.putFile(mImageUriSmall).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUriThumb = taskSnapshot.getDownloadUrl();
                    if (downloadUriThumb == null) {
                        Log.e(TAG, "onSuccess: error got empty downloadUri");
                        return;
                    }
                    newContactRef.child("thumbnail").setValue(downloadUriThumb.toString());
                    newContactNumRef.child("thumbnail").setValue(downloadUriThumb.toString());
                    finish();
                }
            });
        } else {
            newContactNumRef.child("imageurl").setValue("default");
            newContactRef.child("thumbnail").setValue("default");
            newContactNumRef.child("thumbnail").setValue("default");
            finish();
        }
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
                    mImageUriSmall = result.getUri();
                    if (mImageUri == null) {
                        Log.e(TAG, "onActivityResult: got empty imageUri");
                        return;
                    }
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    Bitmap bitmapSmall = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUriSmall);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    bitmapSmall.compress(Bitmap.CompressFormat.JPEG, 5, out);
                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    Bitmap bitmap2Small = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                    String path = MediaStore.Images.Media.insertImage(InfoneAddContactActivity.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);
                    String pathSmall = MediaStore.Images.Media.insertImage(InfoneAddContactActivity.this.getContentResolver(), bitmap2Small, mImageUri.getLastPathSegment(), null);
                    mImageUri = Uri.parse(path);
                    mImageUriSmall = Uri.parse(pathSmall);
                    addImage.setImageURI(mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: ", result.getError());
            }
        }
    }
}
