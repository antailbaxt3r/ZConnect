package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;

/**
 * Created by tanmay on 25/3/18.
 */

public class ChatTabRVViewHolder extends RecyclerView.ViewHolder {

    public TextView nametv;
    public LinearLayout linearLayout;
    public Button del;

    public ChatTabRVViewHolder(View itemView) {
        super(itemView);
        nametv = (TextView) itemView.findViewById(R.id.chat_name_message);
        del=(Button)itemView.findViewById(R.id.delete_btn_chat);
        linearLayout=(LinearLayout) itemView.findViewById(R.id.ll_chat_message);
    }
}
