package com.zconnect.zutto.zconnect.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.EditProfileActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.VerificationPage;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.util.HashMap;

import mabbas007.tagsedittext.TagsEditText;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class MyProfileFragment extends Fragment {
    String name, mobileNumber,whatsAppNumber, email, desc, imagelink ,skills ,category, Uid;
    private TextView editTextName;
    private TextView editTextEmail;
    private TextView editTextDetails;
    private TextView editTextNumber;
    Boolean contactHidden = false;
    TextView whatsAppNumberText;
    //private android.support.design.widget.TextInputEditText editTextSkills;
    private TagsEditText editTextSkills;
    private SimpleDraweeView image;
    private ImageView mail, call;
//    private EditText textMessage;
//    private LinearLayout anonymMessageLayout;
//    private ImageButton sendButton;
    private ImageButton btn_love,btn_like;
    private Boolean flagforNull=false;
    private TextView points_num, like_text, like_num, love_text, love_num;
    private boolean love_status = false,like_status=false;
    private FirebaseAuth mAuth;
    private UserItemFormat userProfile;
    private LinearLayout content;
    private ProgressBar progressBar;
    private Menu menu;
    private android.support.design.widget.AppBarLayout appBarLayout;
    private Button userTypeText, showContact;

    private DatabaseReference infoneContact,usersReference, mUserDetails;
    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_open_user_detail, container, false);
        content = (LinearLayout) view.findViewById(R.id.phonebook_details_content);
        appBarLayout = (android.support.design.widget.AppBarLayout ) view.findViewById(R.id.topToolbar);
        appBarLayout.setVisibility(View.GONE);
//        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar_app_bar_home);
//        toolbar.setVisibility(View.GONE);
        progressBar = (ProgressBar) view.findViewById(R.id.phonebook_details_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        content.setVisibility(View.INVISIBLE);
        image = (SimpleDraweeView) view.findViewById(R.id.contact_details_display_image);
        editTextDetails = (TextView) view.findViewById(R.id.contact_details_editText_1);
        editTextEmail = (TextView) view.findViewById(R.id.contact_details_email_editText);
        editTextName = (TextView) view.findViewById(R.id.contact_details_name_editText);
        editTextNumber = (TextView) view.findViewById(R.id.contact_details_number_editText);
        //editTextSkills = (TextInputEditText) findViewById(R.id.contact_details_editText_skills);
        editTextSkills = (TagsEditText) view.findViewById(R.id.contact_details_editText_skills);
        whatsAppNumberText = (TextView) view.findViewById(R.id.whatsapp_number);

        btn_like = (ImageButton) view.findViewById(R.id.btn_like);
        btn_love = (ImageButton) view.findViewById(R.id.btn_love);
        //btn_love.setEnabled(false);
        //btn_like.setEnabled(false);
        points_num = (TextView) view.findViewById(R.id.point_num);
        like_text = (TextView) view.findViewById(R.id.like_text);
        like_num = (TextView) view.findViewById(R.id.like_num);
        love_text = (TextView) view.findViewById(R.id.love_text);
        love_num = (TextView) view.findViewById(R.id.love_num);
        userTypeText = (Button) view.findViewById(R.id.user_type_content_phonebook_details);
        showContact = (Button) view.findViewById(R.id.show_cum_request_contact_button);

//        textMessage = (EditText) view.findViewById(R.id.textInput);
//        anonymMessageLayout = (LinearLayout) view.findViewById(R.id.anonymTextInput);
//        anonymMessageLayout.setVisibility(View.GONE);
//        sendButton = (ImageButton) view.findViewById(R.id.send);

        call = (ImageView) view.findViewById(R.id.ib_call_contact_item);
        mail = (ImageView) view.findViewById(R.id.mailbutton);
        mAuth = FirebaseAuth.getInstance();
        mUserDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);
                Uid = userItem.getUserUID();

                //Like and Love data reader
                final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(Uid);
                final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                //        final DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(myUID);
                //Value fill listener
                Log.d("EDITTT", Uid);
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        userProfile = dataSnapshot.getValue(UserItemFormat.class);
                        if(!dataSnapshot.hasChild("contactHidden")){
                            userProfile.setContactHidden(false);
                            userItem.setContactHidden(false);
                        }else {
                            userItem.setContactHidden(userProfile.getContactHidden());
                        }
                        setUserDetails();
                        progressBar.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                    }
                });

                usersReference = db.child("contactHidden");
                infoneContact = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("infone").child("categories").child(userItem.getInfoneType()).child(userItem.getUserUID()).child("contactHidden");
                showContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        usersReference.setValue(false);
                        infoneContact.setValue(false);
                        menu.findItem(R.id.action_privacy).setTitle("Hide Contact");
                    }
                });

                final DatabaseReference db_like = db.child("Likes");
                final DatabaseReference db_love = db.child("Loves");
                final DatabaseReference db_point = db.child("userPoints");
                if(db_point != null)
                {
                    db_point.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                String points = dataSnapshot.getValue().toString();
                                points = points==null ? "0" : points;
                                points_num.setText(points);
                            }
                            else
                            {
                                points_num.setText("0");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    points_num.setText("0");
                }
                if(db_love != null){
                    db_love.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long loves = dataSnapshot.getChildrenCount();
                            love_text.setText("Loves");
                            love_num.setText(String.valueOf(loves));
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
                    love_text.setText("Loves");
                    love_num.setText("0");
                }

                if(db_like != null){
                    db_like.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long like = dataSnapshot.getChildrenCount();
                            like_text.setText("Likes");
                            like_num.setText(String.valueOf(like));
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
                    like_text.setText("Likes");
                    like_num.setText("0");
                }
                //seting onclickListener for togelling the likes and loves

//        btn_like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(like_status){
//                    db_like.child(myUID).setValue(null);
//                    like_status = false;
//                }else {
//                    db_like.child(myUID).setValue(true);
//                    like_status = true;
//                    currentUser.child("Likes").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.hasChild(Uid)){
//                                Toast.makeText(getActivity().getApplicationContext(), "Congrats, now you both like each other, we recommend you to start a conversation", Toast.LENGTH_LONG).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                    NotificationSender notificationSender=new NotificationSender(userProfile.getUserUID(),null,null,null,null,mAuth.getCurrentUser().getEmail(),null, OtherKeyUtilities.KEY_LIKE,false,true,getActivity().getApplicationContext());
//                    notificationSender.execute();
//                }
//            }
//        });

//        btn_love.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(love_status) {
//                    db_love.child(myUID).setValue(null);
//                    love_status = false;
//                } else{
//                    db_love.child(myUID).setValue(true);
//                    love_status = true;
//                    currentUser.child("Loves").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.hasChild(Uid)){
//                                Toast.makeText(getActivity().getApplicationContext(), "WOW, now you both love each other, we recommend you to start a conversation", Toast.LENGTH_LONG).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                    NotificationSender notificationSender=new NotificationSender(userProfile.getUserUID(),null,null,null,null,mAuth.getCurrentUser().getEmail(),null, OtherKeyUtilities.KEY_LOVE,false,true, getActivity().getApplicationContext());
//                    notificationSender.execute();
//                }
//            }
//        });


                if (Uid.equals("null"))
                {
//            anonymMessageLayout.setVisibility(View.GONE);
                    flagforNull=true;
                }

//        sendButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                String textMessageString;
//                Calendar calendar;
//                calendar = Calendar.getInstance();
//                textMessageString = textMessage.getText().toString();
//                if (textMessageString!=null && !flagforNull){
//                    DatabaseReference UsersReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("messages");
//                    String s = UsersReference.child("chats").push().getKey();
//
//                    DatabaseReference chatReference = UsersReference.child("chats").child(s).push();
//                    chatReference.child("message").setValue("\""+textMessageString+"\"");
//                    chatReference.child("sender").setValue(mAuth.getCurrentUser().getUid());
//                    chatReference.child("senderName").setValue("Anonymous");
//                    chatReference.child("timeStamp").setValue(calendar.getTimeInMillis());
//
//                    UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("name").setValue(name);
//                    UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("message").setValue(textMessageString);
//                    UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("type").setValue("sent");
//                    UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("chatUID").setValue(s);
//                    UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("sender").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                    UsersReference.child("users").child(Uid).child("messages").child(s).child("message").setValue(textMessageString);
//                    UsersReference.child("users").child(Uid).child("messages").child(s).child("sender").setValue(mAuth.getCurrentUser().getUid());
//                    UsersReference.child("users").child(Uid).child("messages").child(s).child("type").setValue("recieved");
//                    UsersReference.child("users").child(Uid).child("messages").child(s).child("chatUID").setValue(s);
//                    UsersReference.child("users").child(Uid).child("messages").child(s).child("timeStamp").setValue(calendar.getTimeInMillis());
//
//                    FirebaseMessaging.getInstance().subscribeToTopic(s);
//
//
//                    textMessage.setText(null);
//                    Toast.makeText(getActivity().getApplicationContext(), "Encrypted message sent", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

                //changing fonts
                Typeface ralewayRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Regular.ttf");
                Typeface ralewaySemiBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-SemiBold.ttf");
                editTextName.setTypeface(ralewaySemiBold);
                editTextDetails.setTypeface(ralewayRegular);
                editTextNumber.setTypeface(ralewayRegular);
                editTextSkills.setTypeface(ralewayRegular);
                editTextEmail.setTypeface(ralewayRegular);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    public void setUserDetails(){

        name = userProfile.getUsername();
        desc = userProfile.getAbout();
        mobileNumber = userProfile.getMobileNumber();
        whatsAppNumber = userProfile.getWhatsAppNumber();
        imagelink = userProfile.getImageURL();
        email = userProfile.getEmail();
        skills = userProfile.getSkillTags();

        if(userProfile.getContactHidden()!=null){
            contactHidden = userProfile.getContactHidden();
        }

        if(skills==null)
            skills="";

        if (!skills.equals("") || skills.indexOf(',') > 0) {
            editTextSkills.setVisibility(View.VISIBLE);
            String[] skillsArray = skills.split(",");
            skillsArray[0] = skillsArray[0].substring(1);
            skillsArray[skillsArray.length - 1] = skillsArray[skillsArray.length - 1]
                    .substring(0, skillsArray[skillsArray.length - 1].length() - 1);
            editTextSkills.setTags(skillsArray);
        } else {
            editTextSkills.setVisibility(View.GONE);
        }

        if (name != null) {
            editTextName.setText(name);
        }
        if (desc != null) {
            editTextDetails.setText(desc);
        }
        if (mobileNumber != null) {
            editTextNumber.setText(mobileNumber);
//            editTextNumber.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
//                }
//            });
//            call.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
//                }
//            });
        }
        if(whatsAppNumber != null) {
            whatsAppNumberText.setText(whatsAppNumber);
        }

        if(contactHidden){
            //Define if contact hidden here also link the request call function with the button
            call.setVisibility(View.GONE);
            showContact.setVisibility(View.VISIBLE);
            try {
                editTextNumber.setText("******" + mobileNumber.substring(6));
                whatsAppNumberText.setText("******" + whatsAppNumber.substring(6));
                showContact.setText(getContext().getResources().getString(R.string.show_contact));
                editTextNumber.setOnClickListener(null);

            }catch (Exception e){

            }
        }
        else {
            editTextNumber.setText(mobileNumber);
            whatsAppNumberText.setText(whatsAppNumber);
            showContact.setVisibility(View.GONE);
            call.setVisibility(View.VISIBLE);
        }

        if (imagelink != null) {
            if (imagelink.equals("https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/PhonebookImage%2FdefaultprofilePhone.png?alt=media&token=5f814762-16dc-4dfb-ba7d-bcff0de7a336")) {

                image.setBackgroundResource(R.drawable.ic_profile_icon);

            } else {

                image.setImageURI((Uri.parse(imagelink)));

            }

        }
        if (email != null) {
//            mail.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
//                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
//                }
//            });


            //image.setImageURI((Uri.parse(imagelink)));
            editTextEmail.setText(email);
//            editTextEmail.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
//                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
//                }
//            });
        }

        if(userProfile.getUserType()!=null)
        {
            if(userProfile.getUserType().equals(UsersTypeUtilities.KEY_ADMIN)){
                userTypeText.setText("Admin");
            }else if(userProfile.getUserType().equals(UsersTypeUtilities.KEY_VERIFIED)){
                userTypeText.setText("Verfied Member");
            }else if(userProfile.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED)) {
                userTypeText.setText("Not Verified, Verify Now");
                userTypeText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), VerificationPage.class);
                        startActivity(i);
                    }
                });
            }
        }


    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_profile, menu);
        this.menu = menu;
        menu.findItem(R.id.action_edit_profile).setVisible(true);

        mUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                if(userItemFormat.getContactHidden()!=null){
                    if(userItemFormat.getContactHidden()){
                        menu.findItem(R.id.action_privacy).setTitle("Show Contact");
                    }
                    else {
                        menu.findItem(R.id.action_privacy).setTitle("Hide Contact");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

       if(id==R.id.action_edit_profile) {

           CounterItemFormat counterItemFormat = new CounterItemFormat();
           HashMap<String, String> meta= new HashMap<>();
           counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
           counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_EDIT_OPEN);
           counterItemFormat.setTimestamp(System.currentTimeMillis());
           counterItemFormat.setMeta(meta);
           CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
           counterPush.pushValues();

            Intent intent = new Intent(getActivity().getApplicationContext(), EditProfileActivity.class);
            intent.putExtra("newUser",false);
            startActivity(intent);
       }else if(id== R.id.action_privacy){
           mUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                   if(userItemFormat.getContactHidden()!=null) {
                       if (userItemFormat.getContactHidden()) {

                           usersReference.setValue(false);
                           infoneContact.setValue(false);
                           item.setTitle("Hide Contact");
                           Toast.makeText(getContext(), "Your contact is visible now!", Toast.LENGTH_SHORT).show();

                       } else {
                           hideContactAlert(item);

                       }
                   }else {
                       hideContactAlert(item);
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });

       }
        return super.onOptionsItemSelected(item);
    }

    private  void hideContactAlert(final MenuItem item)
    {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setMessage("Please confirm to hide your contact number!")
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        infoneContact.setValue(true);
                        usersReference.setValue(true);
                        item.setTitle("Show Contact");
                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        final android.app.AlertDialog dialog = builder.create();

        if(!(getActivity()).isFinishing())
        {
            if(!dialog.isShowing()) {
                dialog.setCancelable(false);
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorHighlight));
            }
        }

    }
}
