package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;

import java.util.Calendar;

import mabbas007.tagsedittext.TagsEditText;

public class OpenUserDetail extends BaseActivity {
    String name, mobileNumber,whatsAppNumber, email, desc, imagelink ,skills ,category, Uid;
    private MaterialEditText editTextName;
    private android.support.design.widget.TextInputEditText editTextEmail;
    private android.support.design.widget.TextInputEditText editTextDetails;
    private android.support.design.widget.TextInputEditText editTextNumber;

    TextView whatsAppNumberText;
    //private android.support.design.widget.TextInputEditText editTextSkills;
    private TagsEditText editTextSkills;
    private SimpleDraweeView image;
    private ImageView mail, call;
    private EditText textMessage;
    private LinearLayout anonymMessageLayout;
    private ImageButton sendButton,btn_love,btn_like;
    private Boolean flagforNull=false;
    private TextView like_text,love_text;
    private boolean love_status = false,like_status=false;
    private FirebaseAuth mAuth;
    private UserItemFormat userProfile;
    private LinearLayout content;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_user_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        content = (LinearLayout) findViewById(R.id.phonebook_details_content);
        progressBar = (ProgressBar) findViewById(R.id.phonebook_details_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        content.setVisibility(View.INVISIBLE);
        image = (SimpleDraweeView) findViewById(R.id.contact_details_display_image);
        editTextDetails = (TextInputEditText) findViewById(R.id.contact_details_editText_1);
        editTextEmail = (TextInputEditText) findViewById(R.id.contact_details_email_editText);
        editTextName = (MaterialEditText) findViewById(R.id.contact_details_name_editText);
        editTextNumber = (TextInputEditText) findViewById(R.id.contact_details_number_editText);
        //editTextSkills = (TextInputEditText) findViewById(R.id.contact_details_editText_skills);
        editTextSkills = (TagsEditText) findViewById(R.id.contact_details_editText_skills);
        whatsAppNumberText = (TextView) findViewById(R.id.whatsapp_number);

        btn_like = (ImageButton) findViewById(R.id.btn_like);
        btn_love = (ImageButton) findViewById(R.id.btn_love);
        //btn_love.setEnabled(false);
        //btn_like.setEnabled(false);
        like_text = (TextView) findViewById(R.id.like_text);
        love_text = (TextView) findViewById(R.id.love_text);

        textMessage = (EditText) findViewById(R.id.textInput);
        anonymMessageLayout = (LinearLayout) findViewById(R.id.anonymTextInput);
        sendButton = (ImageButton) findViewById(R.id.send);

        call = (ImageView) findViewById(R.id.ib_call_contact_item);
        mail = (ImageView) findViewById(R.id.mailbutton);
        mAuth = FirebaseAuth.getInstance();
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
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
//
//        name = getIntent().getStringExtra("name");
//        desc = getIntent().getStringExtra("desc");
//        mobileNumber = getIntent().getStringExtra("contactDescTv");
//        imagelink = getIntent().getStringExtra("image");
//        email = getIntent().getStringExtra("uid");
//        skills=getIntent().getStringExtra("skills");
//        category=getIntent().getStringExtra("category");
        Uid=getIntent().getStringExtra("Uid");


        //Like and Love data reader
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(Uid);
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(myUID);
        //Value fill listener

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    userProfile = dataSnapshot.getValue(UserItemFormat.class);
                    setUserDetails();
                    progressBar.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
            }
        });

        final DatabaseReference db_like = db.child("Likes");
        final DatabaseReference db_love = db.child("Loves");

        if(db_love != null){
            db_love.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long loves = dataSnapshot.getChildrenCount();
                    love_text.setText(loves+" Loves");
                    if (dataSnapshot.hasChild(myUID)){
                        //I already liked him
                        btn_love.setImageResource(R.drawable.heart_red);
                        love_status = true;
                    }else {
                        love_status= false;
                        btn_love.setImageResource(R.drawable.heart);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            //no one loves him
            love_text.setText("0 Loves");
        }

        if(db_like != null){
            db_like.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long like = dataSnapshot.getChildrenCount();
                    like_text.setText(like+" Likes");
                    if (dataSnapshot.hasChild(myUID)){
                        //I already liked him
                        btn_like.setImageResource(R.drawable.like_blue);
                        like_status = true;
                    }else {
                        like_status = false;
                        btn_like.setImageResource(R.drawable.like);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            //no one likes him
            like_text.setText("0 Likes");
        }
        //seting onclickListener for togelling the likes and loves

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(like_status){
                    db_like.child(myUID).setValue(null);
                    like_status = false;
                }else {
                    db_like.child(myUID).setValue(true);
                    like_status = true;
                    currentUser.child("Likes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(Uid)){
                                Toast.makeText(OpenUserDetail.this, "Congrats, now you both like each other, we recommend you to start a conversation", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    NotificationSender notificationSender=new NotificationSender(userProfile.getUserUID(),null,null,null,null,mAuth.getCurrentUser().getEmail(),null,KeyHelper.KEY_LIKE,false,true,OpenUserDetail.this);
                    notificationSender.execute();
                }
            }
        });

        btn_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(love_status) {
                    db_love.child(myUID).setValue(null);
                    love_status = false;
                } else{
                    db_love.child(myUID).setValue(true);
                    love_status = true;
                    currentUser.child("Loves").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(Uid)){
                                Toast.makeText(OpenUserDetail.this, "WOW, now you both love each other, we recommend you to start a conversation", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    NotificationSender notificationSender=new NotificationSender(userProfile.getUserUID(),null,null,null,null,mAuth.getCurrentUser().getEmail(),null,KeyHelper.KEY_LOVE,false,true, OpenUserDetail.this);
                    notificationSender.execute();
                }
            }
        });

        try {
            Log.e("msg", name);

            Log.e("msg", desc);
            Log.e("msg", mobileNumber);
            Log.e("msg", imagelink);
            Log.e("msg", email);
            Log.e("msg", skills);
            Log.e("msg", category);
            Log.e("msg", Uid);
        }catch (Exception e){

        }

        if (Uid.equals("null"))
        {
            anonymMessageLayout.setVisibility(View.GONE);
            flagforNull=true;
        }



        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            String textMessageString;
            Calendar calendar;
            calendar = Calendar.getInstance();
            textMessageString = textMessage.getText().toString();
            if (textMessageString!=null && !flagforNull){
                DatabaseReference UsersReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("messages");
                String s = UsersReference.child("chats").push().getKey();

                DatabaseReference chatReference = UsersReference.child("chats").child(s).push();
                chatReference.child("message").setValue("\""+textMessageString+"\"");
                chatReference.child("sender").setValue(mAuth.getCurrentUser().getUid());
                chatReference.child("senderName").setValue("Anonymous");
                chatReference.child("timeStamp").setValue(calendar.getTimeInMillis());

                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("name").setValue(name);
                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("message").setValue(textMessageString);
                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("type").setValue("sent");
                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("chatUID").setValue(mAuth.getCurrentUser().getUid());
                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("sender").setValue(s);

                UsersReference.child("users").child(Uid).child("messages").child(s).child("message").setValue(textMessageString);
                UsersReference.child("users").child(Uid).child("messages").child(s).child("sender").setValue(mAuth.getCurrentUser().getUid());
                UsersReference.child("users").child(Uid).child("messages").child(s).child("type").setValue("recieved");
                UsersReference.child("users").child(Uid).child("messages").child(s).child("chatUID").setValue(s);
                UsersReference.child("users").child(Uid).child("messages").child(s).child("timeStamp").setValue(calendar.getTimeInMillis());

                FirebaseMessaging.getInstance().subscribeToTopic(s);


                textMessage.setText(null);
                Toast.makeText(OpenUserDetail.this, "Encrypted message sent", Toast.LENGTH_SHORT).show();
                CounterManager.anonymousMessageSend();
            }
            }
        });

        //changing fonts
        Typeface ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        Typeface ralewaySemiBold = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        editTextName.setTypeface(ralewaySemiBold);
        editTextDetails.setTypeface(ralewayRegular);
        editTextNumber.setTypeface(ralewayRegular);
        editTextSkills.setTypeface(ralewayRegular);
        editTextEmail.setTypeface(ralewayRegular);

    }

    public void setUserDetails(){
        //        name = getIntent().getStringExtra("name");
//        desc = getIntent().getStringExtra("desc");
//        mobileNumber = getIntent().getStringExtra("contactDescTv");
//        imagelink = getIntent().getStringExtra("image");
//        email = getIntent().getStringExtra("uid");
//        skills=getIntent().getStringExtra("skills");
//        category=getIntent().getStringExtra("category");

        name = userProfile.getUsername();
        desc = userProfile.getAbout();
        mobileNumber = userProfile.getMobileNumber();
        whatsAppNumber = userProfile.getWhatsAppNumber();
        imagelink = userProfile.getImageURL();
        email = userProfile.getEmail();
        skills = userProfile.getSkillTags();


        if(skills==null)
            skills="";

        if(skills.length()>1)
            skills=skills.substring(1,skills.length()-1);

        String[] skillsArray = {""};
        if (name != null) {
            editTextName.setText(name);
        }
        if (desc != null) {
            editTextDetails.setText(desc);
        }
        if (mobileNumber != null) {
            editTextNumber.setText(mobileNumber);
            editTextNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
                }
            });
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    CounterManager.InfoneCallAfterProfile(mobileNumber);
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
                }
            });
        }
        if(whatsAppNumber != null) {
            whatsAppNumberText.setText(whatsAppNumber);
        }
        if (imagelink != null) {
            if (imagelink.equals("https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/PhonebookImage%2FdefaultprofilePhone.png?alt=media&token=5f814762-16dc-4dfb-ba7d-bcff0de7a336")) {

                image.setBackgroundResource(R.drawable.ic_profile_icon);

            } else {

                image.setImageURI((Uri.parse(imagelink)));

            }

        }
        if (email != null) {
            mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CounterManager.email(mobileNumber);
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
                }
            });


            //image.setImageURI((Uri.parse(imagelink)));
            editTextEmail.setText(email);
            editTextEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
                }
            });
        }
        if (!skills.equals(""))
            skillsArray = skills.split(",");

        editTextSkills.setTags(skillsArray);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phonebook_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_report) {

            CounterManager.report(mobileNumber);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "zconnectinc@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Problem with the content displayed");
            // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send uid..."));

            return true;
        }else if (id==R.id.menu_share_conatct) {
            String send = "";
            String format1 = "%1$-20s %2$-20s\n";
            String format2 = "%1$-40s\n";
            send =
                    String.format(format1,"Name :",name)+
                    String.format(format1,"Number :", mobileNumber)+
                    "\n               \t\t\t  Zconnect";
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/*");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, send);
            startActivity(sharingIntent);

        }
        return super.onOptionsItemSelected(item);
    }

}

