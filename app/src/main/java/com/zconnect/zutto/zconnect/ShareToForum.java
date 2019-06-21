package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.zconnect.zutto.zconnect.fragments.JoinedForums;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;

public class ShareToForum extends AppCompatActivity {

    JoinedForums joinedForums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_forum);
        Bundle bundle = new Bundle();
        Intent receivedIntent = getIntent();
        String receivedType = receivedIntent.getType();
        String receivedAction = receivedIntent.getAction();

//        For future in app sharing
//        if(receivedAction.equals(Intent.ACTION_SEND)){
//            //content is being shared
//        }
//        else if(receivedAction.equals(Intent.ACTION_MAIN)){
//            //app has been launched directly, not from share list
//        }

        if(receivedType.startsWith("text/")){
            String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
            if(receivedText != null){
                bundle.putString(ForumUtilities.KEY_MESSAGE_TYPE_STR, ForumUtilities.VALUE_MESSAGE_TEXT_MESSAGE);
                bundle.putString(ForumUtilities.KEY_MESSAGE,receivedText);
            }
        }
        else if(receivedType.startsWith("image/")){
            Uri receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            String receivedURL = receivedUri.toString();
            bundle.putString(ForumUtilities.KEY_MESSAGE_TYPE_STR, ForumUtilities.VALUE_MESSAGE_IMAGE);
            bundle.putString(ForumUtilities.KEY_MESSAGE,receivedURL);
        }


//        toolbarSetup();
//        toolbar.setTitle("Share To:");
        bundle.putString(ForumUtilities.KEY_ACTIVITY_TYPE_STR, ForumUtilities.VALUE_SHARE_FORUM_STR);
        joinedForums = new JoinedForums();
        joinedForums.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,joinedForums).commit();
    }
    private void toolbarSetup() {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }


}
