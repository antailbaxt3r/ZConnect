package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;

/**
 * Created by tanmay on 25/3/18.
 */

public class MessageTabRVViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public LinearLayout linearLayout;

    public MessageTabRVViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name_tv_message);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_message);
    }
}
