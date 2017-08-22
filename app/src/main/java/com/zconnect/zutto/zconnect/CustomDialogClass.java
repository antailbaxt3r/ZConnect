package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.app.Dialog;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

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
    public Button yes, no;
    public SimpleDraweeView popUpImage;

    public CustomDialogClass(Activity a, String Url) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.Url=Url;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        Fresco.initialize(getContext());

        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        popUpImage=(SimpleDraweeView) findViewById(R.id.popUpImage);

        popUpImage.setImageURI(Uri.parse(Url));

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}