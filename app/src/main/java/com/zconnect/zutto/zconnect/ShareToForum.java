package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.zconnect.zutto.zconnect.fragments.JoinedForums;
import com.zconnect.zutto.zconnect.utilities.ForumShareUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;

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
                bundle.putString(ForumShareUtilities.KEY_MESSAGE_TYPE_STR,ForumShareUtilities.VALUE_MESSAGE_TEXT_MESSAGE);
                bundle.putString(ForumShareUtilities.KEY_MESSAGE,receivedText);
            }
        }
        else if(receivedType.startsWith("image/")){
            Uri receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            String receivedURL = receivedUri.toString();
            bundle.putString(ForumShareUtilities.KEY_MESSAGE_TYPE_STR,ForumShareUtilities.VALUE_MESSAGE_IMAGE);
            bundle.putString(ForumShareUtilities.KEY_MESSAGE,receivedURL);
        }


//        toolbarSetup();
//        toolbar.setTitle("Share To:");
        bundle.putString(ForumShareUtilities.KEY_ACTIVITY_TYPE_STR, ForumShareUtilities.VALUE_SHARE_FORUM_STR);
        joinedForums = new JoinedForums();
        joinedForums.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,joinedForums).commit();
    }
    private void toolbarSetup() {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }
}
