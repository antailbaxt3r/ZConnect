package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

public class viewImage extends AppCompatActivity {
    LinearLayout titleBar, actionButtonBar;
    View viewImageLayout;
    View.OnClickListener buttonListener;
    ImageView imageView;
    String localAbsoluteFilePath;
    Bitmap event_image;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Bundle extra = getIntent().getExtras();
        if (extra == null)
            finish();
        name = extra.getString("currentEvent");
        event_image = (Bitmap) extra.get("eventImage");
        initializeLayout();
        initializeListener();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        }
        super.onWindowFocusChanged(hasFocus);
    }

    void initializeLayout() {
        viewImageLayout = findViewById(R.id.viewImageLayout);
        titleBar = (LinearLayout) viewImageLayout.findViewById(R.id.titleBar);
        actionButtonBar = (LinearLayout) viewImageLayout.findViewById(R.id.buttonActionBar);
        imageView.setImageBitmap(event_image);

    }

    void initializeListener() {
        buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.backButton:
                        finish();
                        break;
                    case R.id.button_share:
                        shareImage();
                        break;
                    case R.id.button_save:
                        saveToGallery();
                        break;
                }
            }
        };
    }

    void shareImage() {
        saveImage saveImage = new saveImage();
        localAbsoluteFilePath = saveImage.saveImageLocally(event_image, name);
        if (localAbsoluteFilePath != null && localAbsoluteFilePath != "") {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            Uri phototUri = Uri.parse(localAbsoluteFilePath);
            shareIntent.setData(phototUri);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, phototUri);
            this.startActivityForResult(Intent.createChooser(shareIntent, "Share Via"), 0);
        }
    }

    void saveToGallery() {
        saveImage saveImage = new saveImage();
        localAbsoluteFilePath = saveImage.saveImageLocally(event_image, name);
        if (localAbsoluteFilePath != null)
            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            // delete temp file
            File file = new File(localAbsoluteFilePath);
            file.delete();
            Toast.makeText(this, "Successfully Shared", Toast.LENGTH_LONG).show();
        }


    }

}

