package com.zconnect.zutto.zconnect.custom;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.R;

public class MentionsClickableSpan extends ClickableSpan {
    public Context context;
    public String userUID;
    public MentionsClickableSpan(Context context, String userUID){
        this.context = context;
        this.userUID = userUID;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ContextCompat.getColor(context, R.color.deepPurple500));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(@NonNull View widget) {
        Intent intent = new Intent(widget.getContext(), OpenUserDetail.class);
        intent.putExtra("Uid",userUID);
        widget.getContext().startActivity(intent);

    }

}
