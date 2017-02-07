package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class registerNewUser extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 7;
    private Uri mImageUri=null;
    private ImageButton userImage;
    private EditText usernameText;
    private EditText emailText;
    private EditText passwordText;
    private Button doneButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private StorageReference mStorageProfile;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageProfile = FirebaseStorage.getInstance().getReference().child("Profile");
        mProgress = new ProgressDialog(this);

        userImage = (ImageButton) findViewById(R.id.profileImg);
        usernameText = (EditText) findViewById(R.id.username);
        emailText = (EditText) findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);
        doneButton = (Button) findViewById(R.id.done);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void  startRegister()
    {

        final String username = usernameText.getText().toString().trim();
        final String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if(password.length()>=6) {
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                mProgress.setMessage("Adding User");
                mProgress.show();


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            StorageReference filePath = mStorageProfile.child(mImageUri.getLastPathSegment());
                            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    DatabaseReference currentUser = mDatabase.child(mAuth.getCurrentUser().getUid());

                                    currentUser.child("Username").setValue(username);

                                    currentUser.child("Email").setValue(email);
                                    currentUser.child("ProfileImage").setValue(downloadUri.toString());
                                    mProgress.dismiss();
                                    Intent homeIntent = new Intent(registerNewUser.this, home.class);
                                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    startActivity(homeIntent);
                                }
                            });


                        }
                    }
                });

            }else{
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Password should be greater than 6 letters", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setBackgroundColor(R.color.white)
                    .setBorderCornerColor(R.color.teal100)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUri= result.getUri();
                userImage.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
