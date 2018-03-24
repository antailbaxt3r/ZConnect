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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.ItemFormats.Event;

public class NewMessageActivity extends AppCompatActivity {

    Button submit;
    CheckBox anonymousCheck;
    EditText messageInput;
    View.OnClickListener submitlistener;
    Event event;
    Boolean a;
    String anonymous;

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

        final DatabaseReference home;
        home= FirebaseDatabase.getInstance().getReference().child("home");

        submit = (Button) findViewById(R.id.button_newmessage_submit);
        anonymousCheck = (CheckBox) findViewById(R.id.checkbox_newmessage_anonymous);
        messageInput = (EditText) findViewById(R.id.edittext_newmessage_input);

        submitlistener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = messageInput.getText().toString();
                if(anonymousCheck.isChecked())
                    anonymous = "y";
                else
                    anonymous = "n";

                DatabaseReference newMessage = home.push();
                String key = newMessage.getKey();
                newMessage.child("Key").setValue(key);
                newMessage.child("desc").setValue(messageText);
                newMessage.child("desc2").setValue(anonymous);
                newMessage.child("feature").setValue("Message");
                newMessage.child("name").setValue("Message");
                newMessage.child("imageurl").setValue("https://www.iconexperience.com/_img/o_collection_png/green_dark_grey/512x512/plain/message.png");
                newMessage.child("id").setValue(key);
            }
        };

        submit.setOnClickListener(submitlistener);

    }

}
