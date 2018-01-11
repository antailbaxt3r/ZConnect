package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by tanmay on 20/8/17.
 */

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public String Url;
    public Dialog d;
    public Button updateButton, skipButton;
    public SimpleDraweeView popUpImage;
    String url = "https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect";
    private String buttonName = "";

    public CustomDialogClass(Activity a, String Url) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.Url=Url;
        this.buttonName="";
    }

    public CustomDialogClass(Activity a, String Url, String buttonName) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.Url = Url;
        this.buttonName = buttonName;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        Fresco.initialize(getContext());

        updateButton = (Button) findViewById(R.id.btn_update);
        skipButton = (Button) findViewById(R.id.btn_skip);
        popUpImage=(SimpleDraweeView) findViewById(R.id.popUpImage);

        popUpImage.setImageURI(Uri.parse(Url));

        if (buttonName.equals("")) {
            //skipButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
        }
        else {
            updateButton.setText(buttonName);
        }

        updateButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                //c.finish();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                c.startActivity(browserIntent);

                break;
            case R.id.btn_skip:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}