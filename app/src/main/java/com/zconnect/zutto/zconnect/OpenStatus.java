package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.adapters.ChatRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.commonModules.newUserVerificationAlert;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.PostedByDetails;
import com.zconnect.zutto.zconnect.itemFormats.RecentsItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.zconnect.zutto.zconnect.utilities.RequestCodes.GALLERY_REQUEST;

public class OpenStatus extends BaseActivity {

    private SimpleDraweeView userImage;
    private TextView username, timePosted, content, likes, comments;
    private ImageView postedImage, likeButton, commentButton;

    private String key, currentUserID;
    private ArrayList<ChatItemFormats> messages  = new ArrayList<>();
    private String usernameText, timePostedText, contentText, userImageURL, postedImageURL, likeText, commentText;
    private int likeCount, commentCount;
    private long timeInMillis;
    private boolean statusLikeFlag;
    private Uri mImageUri = null;

    private DatabaseReference ref;
    private FirebaseUser user;
    private DatabaseReference mUserReference;
    private StorageReference mStorage;
    private IntentHandle intentHandle;
    private Intent galleryIntent;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private Context context;

    private ValueEventListener loadMessagesListener;
    private Calendar calendar;
    private ImageView anonymousSendBtn;

    @Override
    protected void onPause() {
        super.onPause();
        ref.child("Chat").removeEventListener(loadMessagesListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ref.child("Chat").addValueEventListener(loadMessagesListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_status);

        setToolbar();
        setTitle("Status");
        user = FirebaseAuth.getInstance().getCurrentUser();
        anonymousSendBtn = findViewById(R.id.sendAnonymousButton);
        currentUserID = user.getUid();
        intentHandle = new IntentHandle();

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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        context = getApplicationContext();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(currentUserID);
        attachID();

        key  = getIntent().getStringExtra("key");
        ref= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(key);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //attach basic details in card
                usernameText = dataSnapshot.child("PostedBy").child("Username").getValue().toString();
                userImageURL = dataSnapshot.child("PostedBy").child("ImageThumb").getValue().toString();
                timeInMillis = Long.parseLong(dataSnapshot.child("PostTimeMillis").getValue().toString());
                contentText = dataSnapshot.child("desc").getValue().toString();
                postedImageURL = dataSnapshot.child("imageurl").getValue().toString();

                username.setText(usernameText);
                content.setText(contentText);

                if (timeInMillis > 0){
                    TimeUtilities ta = new TimeUtilities(timeInMillis, System.currentTimeMillis());
                    timePostedText = ta.calculateTimeAgo();
                    timePosted.setText(timePostedText);
                }

                System.out.println("likeCount is");
                if (dataSnapshot.hasChild("likeCount")){
                    likeCount = Integer.parseInt(dataSnapshot.child("likeCount").getValue().toString());
                    if (likeCount > 0){
                        likes.setVisibility(View.VISIBLE);
                        likeText =  Integer.toString(likeCount);
                        likes.setText(likeText);
                    }else {
                        likes.setVisibility(View.GONE);
                    }
                }

                if (dataSnapshot.hasChild("msgComments")){
                    commentCount = Integer.parseInt(dataSnapshot.child("msgComments").getValue().toString());
                    if (commentCount > 0){
                        comments.setVisibility(View.VISIBLE);
                        commentText = Integer.toString(commentCount);
                        comments.setText(commentText);
                    }else {
                        comments.setVisibility(View.GONE);
                    }
                }

                userImage.setImageURI(userImageURL);

                if (!(postedImageURL.equals("No Image") || postedImageURL.isEmpty())){
                    postedImage.setVisibility(View.VISIBLE);
                    Picasso.with(getApplicationContext()).load(postedImageURL).into(postedImage);
                }else{
                    postedImage.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("likeUids").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                
                if(dataSnapshot.hasChild(user.getUid())){

                    if(dataSnapshot.getChildrenCount()>0)
                        likes.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    else
                        likes.setText("");
                    likes.setTextColor(context.getResources().getColor(R.color.black));
                    likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_thumb_up_alt_white_24));
                    likeButton.setColorFilter(context.getResources().getColor(R.color.deepPurple500));
                    statusLikeFlag=true;
                }else {
                    if(dataSnapshot.getChildrenCount()>0)
                        likes.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    else
                        likes.setText("");
                    likes.setTextColor(context.getResources().getColor(R.color.icon_color));
                    likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.outline_thumb_up_alt_white_24));
                    likeButton.setColorFilter(content.getResources().getColor(R.color.icon_color));
                    statusLikeFlag=false;
                }

                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(statusLikeFlag == true){
                            statusLikeFlag = false;
                            likes.setText(String.valueOf(Integer.valueOf(likes.getText().toString())-1));
                            if(likes.getText().toString().equals("0")){
                                likes.setText("");
                            }
                            ref.child("likeUids").child(user.getUid()).removeValue();
                            likeButton.setColorFilter(context.getResources().getColor(R.color.icon_color));
                            likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.outline_thumb_up_alt_white_24));
                        }else{
                            statusLikeFlag = true;
                            likes.setTextColor(context.getResources().getColor(R.color.black));
                            likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_thumb_up_alt_white_24));
                            likeButton.setColorFilter(context.getResources().getColor(R.color.deepPurple500));
                            if(likes.getText().toString().equals("")){
                                likes.setText("1");
                            }else {
                                likes.setText(String.valueOf(Integer.valueOf(likes.getText().toString()) + 1));
                            }
                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put(user.getUid(), user.getUid());
                            ref.child("likeUids").updateChildren(taskMap);
                            try {
                                final NotificationSender notificationSender = new NotificationSender(context, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                final NotificationItemFormat statusLikeNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_STATUS_LIKED, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                // HashMap<String,Object> hashmap=new HashMap<>();
                                // hashmap.put("meta",1);
                                statusLikeNotification.setItemKey(key);
                                DatabaseReference mUserDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                mUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);
                                        statusLikeNotification.setUserImage(userItem.getImageURLThumbnail());
                                        statusLikeNotification.setUserName(userItem.getUsername());
                                        statusLikeNotification.setUserKey(userItem.getUserUID());
                                        statusLikeNotification.setCommunityName(communityTitle);
                                        statusLikeNotification.setItemLikeCount(Integer.parseInt(likes.getText().toString()));
                                        GlobalFunctions.inAppNotifications("liked your status", getIntent().getStringExtra("desc"), userItem, false, "status", null, getIntent().getStringExtra("uid"));
                                        notificationSender.execute(statusLikeNotification);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            catch (Exception e){
                                Toast.makeText(context,"Could not send notification",Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        joinedForumsAdapter = new CommentsRVAdapter(messages, ref, ref, this);
        adapter = new ChatRVAdapter(messages, ref, ref, this, ForumUtilities.VALUE_COMMENTS);
        recyclerView.setAdapter(adapter);

        //posting message
        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                        if (dataSnapshot.hasChild("userType")) {
                            if (userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                                newUserVerificationAlert.buildAlertCheckNewUser(userItemFormat.getUserType(), "Chat", OpenStatus.this);
                            } else {

                                postMessage(false);
                            }
                        } else {
                            postMessage(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        anonymousSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                        if (dataSnapshot.hasChild("userType")) {
                            if (userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                                newUserVerificationAlert.buildAlertCheckNewUser(userItemFormat.getUserType(), "Chat", OpenStatus.this);
                            } else {

                                postMessage(true);
                            }
                        } else {
                            postMessage(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        //Posting Photo
        findViewById(R.id.chat_photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        OpenStatus.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            OpenStatus.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                } else {
                    galleryIntent = intentHandle.getPickImageIntent(OpenStatus.this);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        });

        loadMessagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatItemFormats temp = new ChatItemFormats();

                    temp = snapshot.getValue(ChatItemFormats.class);

                    temp.setKey(snapshot.getKey());

                    if (!snapshot.hasChild("messageType")) {
                        temp.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);
                    }
                    messages.add(temp);
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        ref.child("Chat").addValueEventListener(loadMessagesListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data, OpenStatus.this); //Get data
            CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {
                    String path2 = compressImage(result.getUri().toString());
//                    mImageUri = Uri.parse(path);
//                    File original = new File(result.getUri().toString());
//                    File file1 = new File(mImageUri.toString());
//                    File file2 = new File(path2);
                    mImageUri =Uri.fromFile(new File(path2));
//                    Log.d("Upload Activity","Size of Original File:"+Double.toString(bitmap.getByteCount()));
//                    Log.d("Upload Activity","Size of previous Comptession:"+Double.toString(bitmap.getByteCount()));
//                    Log.d("Upload Activity","Size of New Comptession:"+Double.toString(file2.length()));



//                    ChatItemFormats temproraryChat = new ChatItemFormats();
//                    temproraryChat.setPhotoURL(mImageUri.toString());
//                    temproraryChat.setMessageType(MessageTypeUtilities.KEY_PHOTO_SENDING_STR);
//                    messages.add(temproraryChat);

                    postPhoto();

                } catch (Exception e) {
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

    }

    private void attachID(){
        username = findViewById(R.id.sentence_open_status_item_format);
        timePosted = findViewById(R.id.postTime_open_status);
        userImage = findViewById(R.id.user_image_open_status);
        content = findViewById(R.id.content_open_status);
        likeButton = findViewById(R.id.like_image_open_status);
        commentButton = findViewById(R.id.comment_image_open_status);
        postedImage = findViewById(R.id.open_status_image);
        likes = findViewById(R.id.like_text_open_status);
        comments = findViewById(R.id.comment_text_open_status);
        recyclerView = findViewById(R.id.open_status_comments_RV);
        calendar = Calendar.getInstance();
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.typer).requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(findViewById(R.id.typer), InputMethodManager.SHOW_IMPLICIT);

            }
        });
    }

    private void postMessage(boolean isAnonymous){

        final EditText typer = ((EditText) findViewById(R.id.typer));
        final String text = typer.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            showToast("Message is empty.");
            return;
        }
        final ChatItemFormats message = new ChatItemFormats();
        message.setTimeDate(calendar.getTimeInMillis());
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);
                message.setUuid(userItem.getUserUID());
                message.setName(userItem.getUsername());
                message.setImageThumb(userItem.getImageURLThumbnail());
                message.setMessage("\""+text+"\"");
                if(isAnonymous){
                    message.setMessageType(MessageTypeUtilities.KEY_ANONYMOUS_MESSAGE_STR);

                }
                else {
                    message.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);
                }
                GlobalFunctions.addPoints(2);
                String messagePushID = ref.child("Chat").push().getKey();
                message.setKey(messagePushID);
                ref.child("Chat").child(messagePushID).setValue(message);
                Log.d("AINTNO", "POP");
                ref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                HashMap<String,Object> metadata = new HashMap<>();
                metadata.put("key",key);
                GlobalFunctions.inAppNotifications("commented on your status","Comment: "+text,userItem,false,"statusComment",metadata,getIntent().getStringExtra("uid"));
                ref.child("Chat").child(messagePushID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserItemFormat userItemFormat = new UserItemFormat();
                        HashMap<String,Object> meta = new HashMap<>();
                        meta.put("ref",ref);
                        meta.put("key",key);
                        Log.d("reference", ref+"");
                        Log.d("ImageThumb",(String) dataSnapshot.child("imageThumb").getValue()+"");
                        Log.d("Username",(String) dataSnapshot.child("name").getValue()+"");
                        Log.d("UID",(String) dataSnapshot.child("uuid").getValue()+"");
                        userItemFormat.setUserUID((String) dataSnapshot.child("uuid").getValue());
                        userItemFormat.setImageURL((String) dataSnapshot.child("imageThumb").getValue());
                        userItemFormat.setUsername((String) dataSnapshot.child("name").getValue());
                        GlobalFunctions.inAppNotifications("commented on a status you commented","Comment: "+text,userItemFormat,true,"statusNestedComment",meta,null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        typer.setText(null);
        // chatView.scrollToPosition(chatView.getChildCount());

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            Log.d("Upload Activity","Size of New Comptession Original:"+Double.toString(scaledBitmap.getByteCount()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    private void postPhoto(){

        mStorage = FirebaseStorage.getInstance().getReference();

        final ChatItemFormats message = new ChatItemFormats();
        message.setTimeDate(calendar.getTimeInMillis());

        if(mImageUri!=null){
            Log.d("Try","Image Posting");
//            message1.setPhotoURL(mImageUri.toString());
//            message1.setMessageType(MessageTypeUtilities.KEY_PHOTO_SENDING_STR);
//            messages.add(message1);
//            joinedForumsAdapter.notifyDataSetChanged();
//            chatView.scrollToPosition(messages.size()-1);


            final StorageReference filePath = mStorage.child(communityReference).child("features").child("message").child((mImageUri.getLastPathSegment()) + currentUserID);
            UploadTask uploadTask = filePath.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        final Uri downloadUri = task.getResult();
                        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);
                                message.setUuid(userItem.getUserUID());
                                message.setName(userItem.getUsername());
                                message.setPhotoURL(downloadUri != null ? downloadUri.toString() : null);
                                message.setImageThumb(userItem.getImageURLThumbnail());
                                message.setMessage(" \uD83D\uDCF7 Image ");
                                message.setMessageType(MessageTypeUtilities.KEY_PHOTO_STR);
                                GlobalFunctions.addPoints(5);
                                String messagePushID = ref.child("Chat").push().getKey();
                                message.setKey(messagePushID);
                                ref.child("Chat").child(messagePushID).setValue(message);
                                Log.d("AINTNO", "POP2");
                                ref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                HashMap<String,Object> metadata = new HashMap<>();
                                metadata.put("key",key);

                                GlobalFunctions.inAppNotifications("commented on your status","Comment: "+"Photo",userItem,false,"statusComment",metadata,getIntent().getStringExtra("uid"));
                                ref.child("Chat").child(messagePushID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserItemFormat userItemFormat = new UserItemFormat();
                                        HashMap<String,Object> meta = new HashMap<>();
                                        meta.put("ref",ref);
                                        meta.put("key",key);
                                        Log.d("reference", ref+"");
                                        Log.d("ImageThumb",(String) dataSnapshot.child("imageThumb").getValue()+"");
                                        Log.d("Username",(String) dataSnapshot.child("name").getValue()+"");
                                        Log.d("UID",(String) dataSnapshot.child("uuid").getValue()+"");
                                        userItemFormat.setUserUID((String) dataSnapshot.child("uuid").getValue());
                                        userItemFormat.setImageURL((String) dataSnapshot.child("imageThumb").getValue());
                                        userItemFormat.setUsername((String) dataSnapshot.child("name").getValue());
                                        GlobalFunctions.inAppNotifications("commented on a status you commented","Comment: "+"Photo",userItemFormat,true,"statusNestedComment",meta,null);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        // Handle failures
                        // ...
                        Snackbar snackbar = Snackbar.make(recyclerView, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });
        }
        // chatView.scrollToPosition(chatView.getChildCount());
    }

}
