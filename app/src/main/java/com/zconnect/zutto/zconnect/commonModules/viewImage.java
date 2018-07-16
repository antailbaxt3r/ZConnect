package com.zconnect.zutto.zconnect.commonModules;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.saveImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.senab.photoview.PhotoViewAttacher;

public class viewImage extends BaseActivity {
    LinearLayout titleBar, actionButtonBar;
    View viewButtonLayout;
    View.OnClickListener buttonListener;
    ImageView imageView;
    String localAbsoluteFilePath;
    Bitmap event_image;
    String name;
    PhotoViewAttacher mAttacher;
    View mViewLayout;
    View.OnClickListener handleClickOnView;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");
        mProgress.show();

        Bundle extra = getIntent().getExtras();
        if (extra == null)
            finish();

        name = extra.getString("currentEvent");
        final String imageUrl = getIntent().getStringExtra("eventImage");


        viewButtonLayout = findViewById(R.id.viewButtonLayout);
        initializeListener();
        initializeLayout();

        getImage myTask = new getImage(this);
        myTask.execute(imageUrl);
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
        ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setTitle("Saving......");
        mProgress.setMessage("Saving to gallery...");
        mProgress.show();
        saveImage saveImage = new saveImage();
        localAbsoluteFilePath = saveImage.saveImageLocally(event_image, name);
        mProgress.dismiss();
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
        }


    }


    void initializeLayout() {
        mViewLayout = findViewById(R.id.viewImageLayout);
        onTouchListner();
        mViewLayout.setOnClickListener(handleClickOnView);
        int id[] = {R.id.backButton, R.id.button_share, R.id.button_save};
        titleBar = (LinearLayout) viewButtonLayout.findViewById(R.id.titleBar);
        actionButtonBar = (LinearLayout) viewButtonLayout.findViewById(R.id.buttonActionBar);
        for (int i : id) {
            ImageView button = (ImageView) viewButtonLayout.findViewById(i);
            button.setOnClickListener(buttonListener);
        }
        imageView = (ImageView) findViewById(R.id.iv_image);
        TextView titleText = (TextView) viewButtonLayout.findViewById(R.id.TitileTextView);
        titleText.setText(name);


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

    void onTouchListner() {
        handleClickOnView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewButtonLayout.getVisibility() == View.INVISIBLE) {
                    viewButtonLayout.setVisibility(View.VISIBLE);
                    new Timer().schedule(new TimerTask() {
                        public void run() {

                            viewButtonLayout.setVisibility(View.INVISIBLE);

                        }
                    }, 1000);
                }
            }
        };
    }

    void setImageView() {
        imageView.setImageBitmap(event_image);
        mAttacher = new PhotoViewAttacher(imageView);
        mProgress.dismiss();
    }

    class getImage extends AsyncTask<String, Integer, Integer> {
        Context ctx;

        public getImage(Context context) {
            ctx = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                event_image = BitmapFactory.decodeStream(input);
                event_image.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Cannot load image", Toast.LENGTH_LONG).show();
                mProgress.dismiss();
                onBackPressed();
            }
            return 0;

        }


        @Override
        protected void onCancelled() {
            Toast.makeText(ctx, "Cannot load image", Toast.LENGTH_LONG).show();
            mProgress.dismiss();
            onBackPressed();
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            setImageView();
            super.onPostExecute(integer);
        }
    }

}