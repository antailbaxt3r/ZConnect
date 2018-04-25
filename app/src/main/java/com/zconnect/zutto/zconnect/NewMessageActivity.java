package com.zconnect.zutto.zconnect;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.ItemFormats.Event;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

public class NewMessageActivity extends BaseActivity {

    Button submit;
    CheckBox anonymousCheck;
    MaterialEditText messageInput;
    View.OnClickListener submitlistener;
    Event event;
    Boolean a;
    String anonymous;
    DatabaseReference mPostedByDetails;



    /*public void onCheckboxClicked (View view) {
        a = ((CheckBox) view).isChecked();
        if(a)
        else
            anonymous = "n";
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        final DatabaseReference home;
        home= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");

        submit = (Button) findViewById(R.id.button_newmessage_submit);
        anonymousCheck = (CheckBox) findViewById(R.id.checkbox_newmessage_anonymous);
        messageInput = (MaterialEditText) findViewById(R.id.edittext_newmessage_input);

        submitlistener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = messageInput.getText().toString();
                if(anonymousCheck.isChecked())
                    anonymous = "y";
                else
                    anonymous = "n";

                final DatabaseReference newMessage = home.push();
                String key = newMessage.getKey();
                newMessage.child("Key").setValue(key);
                newMessage.child("desc").setValue(messageText);
                newMessage.child("desc2").setValue(anonymous);
                newMessage.child("feature").setValue("Message");
                newMessage.child("name").setValue("Message");
                newMessage.child("imageurl").setValue("https://www.iconexperience.com/_img/o_collection_png/green_dark_grey/512x512/plain/message.png");
                newMessage.child("id").setValue(key);
                newMessage.child("PostTimeMillis").setValue(System.currentTimeMillis());
                mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                        if (anonymousCheck.isChecked()){
                            newMessage.child("PostedBy").child("Username").setValue("Anonymous");
                        }else {
                            newMessage.child("PostedBy").child("Username").setValue(user.getUsername());
                        }
                        newMessage.child("PostedBy").child("UID").setValue(user.getUserUID());
                        newMessage.child("PostedBy").child("ImageThumb").setValue(user.getImageURLThumbnail());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        submit.setOnClickListener(submitlistener);

    }

}
