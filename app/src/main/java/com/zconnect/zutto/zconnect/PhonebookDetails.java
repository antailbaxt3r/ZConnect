package com.zconnect.zutto.zconnect;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import mabbas007.tagsedittext.TagsEditText;

public class PhonebookDetails extends BaseActivity {
    String name, number, email, desc, imagelink ,skills ,category, Uid;
    private android.support.design.widget.TextInputEditText editTextName;
    private android.support.design.widget.TextInputEditText editTextEmail;
    private android.support.design.widget.TextInputEditText editTextDetails;
    private android.support.design.widget.TextInputEditText editTextNumber;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        image = (SimpleDraweeView) findViewById(R.id.contact_details_display_image);
        editTextDetails = (TextInputEditText) findViewById(R.id.contact_details_editText_1);
        editTextEmail = (TextInputEditText) findViewById(R.id.contact_details_email_editText);
        editTextName = (TextInputEditText) findViewById(R.id.contact_details_name_editText);
        editTextNumber = (TextInputEditText) findViewById(R.id.contact_details_number_editText);
        //editTextSkills = (TextInputEditText) findViewById(R.id.contact_details_editText_skills);
        editTextSkills = (TagsEditText) findViewById(R.id.contact_details_editText_skills);

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

        name = getIntent().getStringExtra("name");
        desc = getIntent().getStringExtra("desc");
        number = getIntent().getStringExtra("contactDescTv");
        imagelink = getIntent().getStringExtra("image");
        email = getIntent().getStringExtra("email");
        skills=getIntent().getStringExtra("skills");
        category=getIntent().getStringExtra("category");
        Uid=getIntent().getStringExtra("Uid");


        //Like and Love data reader
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                }
            }
        });

        try {
            Log.e("msg", name);

            Log.e("msg", desc);
            Log.e("msg", number);
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
                textMessageString = textMessage.getText().toString();
                if (textMessageString!=null && !flagforNull){
                    DatabaseReference UsersReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users").child(Uid).child("Messages").push();
                    UsersReference.child("Message").setValue(textMessageString);
                    UsersReference.child("MessageId").setValue(UsersReference.getKey());
                    UsersReference.child("PostedBy").setValue(mAuth.getCurrentUser().getUid());
                    textMessage.setText(null);
                    Toast.makeText(PhonebookDetails.this, "Encrypted secret message sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (category.contains("S")) {
            editTextSkills.setVisibility(View.VISIBLE);
            editTextSkills.setEnabled(false);
        }
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
        if (number != null) {
            editTextNumber.setText(number);
            editTextNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
                }
            });
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CounterManager.InfoneCallAfterProfile(number);
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
                }
            });
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
                    CounterManager.email(number);
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



        //changing fonts
        Typeface ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        editTextName.setTypeface(ralewayRegular);
        editTextDetails.setTypeface(ralewayRegular);
        editTextNumber.setTypeface(ralewayRegular);
        editTextSkills.setTypeface(ralewayRegular);
        editTextEmail.setTypeface(ralewayRegular);

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

            CounterManager.report(number);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "zconnectinc@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Problem with the content displayed");
            // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

            return true;
        }else if (id==R.id.menu_share_conatct) {
            String send = "";
            String format1 = "%1$-20s %2$-20s\n";
            String format2 = "%1$-40s\n";
            send =
                    String.format(format1,"Name :",name)+
                    String.format(format1,"Number :",number)+
                    "\n               \t\t\t  Zconnect";
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/*");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, send);
            startActivity(sharingIntent);

        }
        return super.onOptionsItemSelected(item);
    }

}

