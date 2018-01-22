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
import android.support.v7.widget.CardView;
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
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private CardView thankuCard;
    private CardView sorryCard;
    private EditText textMessage;
    private LinearLayout anonymMessageLayout;
    private ImageButton sendButton;
    private Boolean flagforNull=false;
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

        sorryCard = (CardView) findViewById(R.id.contact_details_sorry_card);
        thankuCard = (CardView) findViewById(R.id.contact_details_thankyou_card);
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

        Log.e("msg",name);
        Log.e("msg",desc);
        Log.e("msg",number);
        Log.e("msg",imagelink);
        Log.e("msg",email);
        Log.e("msg",skills);
        Log.e("msg",category);
        Log.e("msg",Uid);

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
                    DatabaseReference UsersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("Messages").push();
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

        if (name != null && desc != null && number != null && imagelink != null && email != null) {
            editTextName.setText(name);
            editTextDetails.setText(desc);
            editTextNumber.setText(number);

            if (!skills.equals(""))
                skillsArray = skills.split(",");

            editTextSkills.setTags(skillsArray);
            editTextNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
                }
            });
            editTextEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
                }
            });
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CounterManager.InfoneCallAfterProfile(number);
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
                }
            });
            mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CounterManager.email(number);
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
                }
            });

            if(imagelink.equals("https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/PhonebookImage%2FdefaultprofilePhone.png?alt=media&token=5f814762-16dc-4dfb-ba7d-bcff0de7a336")){

                image.setBackgroundResource(R.drawable.ic_profile_icon);

            }
            else {

                image.setImageURI((Uri.parse(imagelink)));

            }

            //image.setImageURI((Uri.parse(imagelink)));
            editTextEmail.setText(email);
        }

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
        }
        return super.onOptionsItemSelected(item);
    }

}

