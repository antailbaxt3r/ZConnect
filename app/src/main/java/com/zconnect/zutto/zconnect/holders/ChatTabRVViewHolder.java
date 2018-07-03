package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;

/**
 * Created by tanmay on 25/3/18.
 */

public class ChatTabRVViewHolder extends RecyclerView.ViewHolder {

    public TextView nametv;
    public LinearLayout linearLayout;

    public ChatTabRVViewHolder(View itemView) {
        super(itemView);
        nametv = (TextView) itemView.findViewById(R.id.chat_name_message);
//If the edit text contains previous text with potential links
//        Linkify.addLinks(nametv, Linkify.WEB_URLS);

//        Linkify.addLinks(nametv, Linkify.EMAIL_ADDRESSES);
        linearLayout=(LinearLayout) itemView.findViewById(R.id.ll_chat_message);
    }
}
