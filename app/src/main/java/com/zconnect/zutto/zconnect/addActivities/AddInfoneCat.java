package com.zconnect.zutto.zconnect.addActivities;

import android.app.ProgressDialog;
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
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.ZConnectDetails;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddInfoneCat extends AppCompatActivity {


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

    Boolean flag1,flag2;
    private final String TAG = getClass().getSimpleName();

    /*uploading elements*/
    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 7;
    private Uri mImageUriSmall;
    private StorageReference mStorageRef;

    FirebaseAuth mAuth;
    private String catImageurl;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Infone Category");
        setContentView(R.layout.activity_infone_add_cat);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        nameEt = (MaterialEditText) findViewById(R.id.et_name_cat_add_infone);
        addImage = (SimpleDraweeView) findViewById(R.id.image_add_cat_infone);
        saveButton = (Button) findViewById(R.id.save_image_add_cat_infone);

        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceInfone = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child(ZConnectDetails.INFONE_DB_NEW);
        mStorageRef = FirebaseStorage.getInstance().getReference();

//        try{
//            if (getIntent().getExtras().getString("catId") != null) {
//                catId = getIntent().getExtras().getString("catId");
//                catName = getIntent().getExtras().getString("catName");
//                catImageurl=getIntent().getExtras().getString("catImageurl");
//
//                Log.e(TAG,"Exception : "+catName+" "+catId+" "+catImageurl);
//
//                Uri imageUri= Uri.parse(catImageurl);
//                addImage.setImageURI(imageUri);
//
//                nameEt.setText(catName);
//
//                toAdd = false;
//            } else {
//                catId = "";
//            }
//        }
//        catch (Exception e){
//            Log.e(TAG,"Exception : "+e.toString());
//            toAdd=true;
//            //finish();
//        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Creating category");
                saveChanges();
                CounterManager.infoneAddCategory();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddInfoneCat.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AddInfoneCat.this,
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
        progressDialog.show();
        String name = nameEt.getText().toString();

        catId = databaseReferenceInfone.child("categoriesInfo").push().getKey();
        newCategoryRef = databaseReferenceInfone.child("categoriesInfo").child(catId);
        newCategoryRef.child("name").setValue(name);
        newCategoryRef.child("admin").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newCategoryRef.child("catId").setValue(catId);
        newCategoryRef.child("totalContacts").setValue(0);

        flag1=false;
        flag2=false;

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
                    catImageurl = downloadUri.toString();
                    newCategoryRef.child("imageurl").setValue(downloadUri.toString());
                    flag1=true;
                    addContact();
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
                    flag2=true;
                    addContact();
                }
            });
        } else {
            newCategoryRef.removeValue();
            Toast.makeText(this, "Fill all details including image", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    public void addContact(){
        if(flag1&&flag2){

            NotificationSender notificationSender = new NotificationSender(AddInfoneCat.this, UserUtilities.currentUser.getUserUID());
            NotificationItemFormat addInfoneCategoryNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_CATEGORY_ADD,UserUtilities.currentUser.getUserUID());
            addInfoneCategoryNotification.setCommunityName(UserUtilities.CommunityName);

            addInfoneCategoryNotification.setItemKey(catId);
            addInfoneCategoryNotification.setItemImage(catImageurl);
            addInfoneCategoryNotification.setItemName(nameEt.getText().toString());
            addInfoneCategoryNotification.setItemCategoryAdmin(UserUtilities.currentUser.getUserUID());

            notificationSender.execute(addInfoneCategoryNotification);

            Toast.makeText(AddInfoneCat.this, "Add a contact in your new category",
                    Toast.LENGTH_SHORT).show();
            final Intent addContactIntent = new Intent(AddInfoneCat.this,
                    AddInfoneContact.class);
            addContactIntent.putExtra("catId", catId);
            startActivity(addContactIntent);
            progressDialog.dismiss();
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
                    String path = MediaStore.Images.Media.insertImage(AddInfoneCat.this.getContentResolver(), bitmap2, mImageUri.getLastPathSegment(), null);
                    String pathSmall = MediaStore.Images.Media.insertImage(AddInfoneCat.this.getContentResolver(), bitmap2Small, mImageUri.getLastPathSegment(), null);
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