package com.zconnect.zutto.zconnect.addActivities;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;

import java.util.Calendar;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;


public class AddForumTab extends BaseActivity {

    private MaterialEditText inputTabName;
    private FrameLayout doneLayout;
    private DatabaseReference mForumTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forum_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
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

        inputTabName = findViewById(R.id.input_tab_name_content_add_forum_tab);
        doneLayout = findViewById(R.id.layout_done_content_add_forum_tab);

        mForumTabs = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/forums/tabs");

        doneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputTabName.getText().toString().length()>0)
                {
                    Log.i("chasing parties", "sending to db!");
                    String tabName = inputTabName.getText().toString();
                    String arr[] = tabName.split(" ");
                    String tabUID = "";
                    for(int i=0; i<arr.length; i++)
                    {
                        if(i==arr.length-1)
                        {
                            String str2 = arr[i];
                            str2 = str2.toLowerCase();
                            str2 = Character.toUpperCase(str2.charAt(0)) + str2.substring(1);
                            tabUID+=str2;
                        }
                        else
                        {
                            tabUID+=arr[i].toLowerCase();
                        }
                    }
                    mForumTabs.child(tabUID).child("name").setValue(tabName);
                    mForumTabs.child(tabUID).child("UID").setValue(tabUID);
                    finish();
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(inputTabName, "Tab name cannot be empty", Snackbar.LENGTH_SHORT);
                    snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                    snackbar.show();
                }
            }
        });
    }
}
