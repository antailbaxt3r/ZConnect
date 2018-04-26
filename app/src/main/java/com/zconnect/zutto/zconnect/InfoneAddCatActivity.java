package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRouter;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InfoneAddCatActivity extends AppCompatActivity {


    private SharedPreferences communitySP;
    public String communityReference;

    MaterialEditText nameEt;
    Button saveButton;
    SimpleDraweeView addImage;

    DatabaseReference databaseReferenceInfone;
    DatabaseReference newCategoryRef;
    DatabaseReference editCategoryRef;
    String categoryName;
    String catId;
    String catName;
    boolean toAdd;
    private final String TAG = getClass().getSimpleName();

    /*uploading elements*/
    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 7;
    private Uri mImageUriSmall;
    private StorageReference mStorageRef;

    FirebaseAuth mAuth;
    private String catImageurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone_add_cat);

        nameEt = (MaterialEditText) findViewById(R.id.et_name_cat_add_infone);
        addImage = (SimpleDraweeView) findViewById(R.id.image_add_cat_infone);
        saveButton = (Button) findViewById(R.id.save_image_add_cat_infone);

        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceInfone = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        try{
            if (getIntent().getExtras().getString("catId") != null) {
                catId = getIntent().getExtras().getString("catId");
                catName = getIntent().getExtras().getString("catName");
                catImageurl=getIntent().getExtras().getString("catImageurl");

                Log.e(TAG,"Exception : "+catName+" "+catId+" "+catImageurl);

                Uri imageUri= Uri.parse(catImageurl);
                addImage.setImageURI(imageUri);

                nameEt.setText(catName);

                toAdd = false;
            } else {
                catId = "";
            }
        }
        catch (Exception e){
            Log.e(TAG,"Exception : "+e.toString());
            toAdd=true;
            //finish();
        }


        if (mImageUri == null && toAdd) {

//            addImage.setActualImageResource(R.mipmap.ic_launcher);

        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(InfoneAddCatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            InfoneAddCatActivity.this,
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

    private void saveChanges() {

        String name = nameEt.getText().toString();
        //String im = phone1Et.getText().toString();

        //Log.e(TAG, "data cat name:" + categoryName);
        SharedPreferences sharedPref = this.getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (toAdd && !name.isEmpty() && !status) {

            catId = databaseReferenceInfone.child("categoriesInfo").push().getKey();
            newCategoryRef = databaseReferenceInfone.child("categoriesInfo").child(catId);
            newCategoryRef.child("name").setValue(name);
            newCategoryRef.child("admin").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

            uploadImage();

            Toast.makeText(InfoneAddCatActivity.this, "Add a contact in your new category",
                    Toast.LENGTH_SHORT).show();
            Intent addContactIntent = new Intent(InfoneAddCatActivity.this,
                    InfoneAddContactActivity.class);
            addContactIntent.putExtra("catId", catId);
            startActivity(addContactIntent);

        } else if (!toAdd && !status) {

            newCategoryRef = databaseReferenceInfone.child("categoriesInfo").child(catId);
            newCategoryRef.child("name").setValue(name);


            uploadImage();

        } else {
            Toast.makeText(InfoneAddCatActivity.this, "Details in complete",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {

        if (mImageUri != null) {
            StorageReference filepath = mStorageRef.child("InfoneImage").child(mImageUri.getLastPathSegment() + catId);
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    if (downloadUri == null) {
                        Log.e(TAG, "onSuccess: error got empty downloadUri");
                        return;
                    }
                    newCategoryRef.child("imageurl").setValue(downloadUri.toString());
                    finish();
                }
            });
            StorageReference filepathThumb = mStorageRef.child("InfoneImageSmall").child(mImageUriSmall.getLastPathSegment() + catId + "Thumbnail");
            filepathThumb.putFile(mImageUriSmall).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUriThumb = taskSnapshot.getDownloadUrl();
                    if (downloadUriThumb == null) {
                        Log.e(TAG, "onSuccess: error got empty downloadUri");
                        return;
                    }
                    newCategoryRef.child("thumbnail").setValue(downloadUriThumb.toString());
                    finish();
                }
            });
        } else {
            newCategoryRef.child("imageurl").setValue("default");
            newCategoryRef.child("thumbnail").setValue("default");
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
                    String path = MediaStore.Images.Media.insertImage(InfoneAddCatActivity.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);
                    String pathSmall = MediaStore.Images.Media.insertImage(InfoneAddCatActivity.this.getContentResolver(), bitmap2Small, mImageUri.getLastPathSegment(), null);
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
