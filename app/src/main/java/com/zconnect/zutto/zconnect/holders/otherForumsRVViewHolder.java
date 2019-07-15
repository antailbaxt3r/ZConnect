package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;

public class otherForumsRVViewHolder  extends RecyclerView.ViewHolder {
    private TextView update_texttv;
    public otherForumsRVViewHolder(View itemView) {
        super(itemView);
        update_texttv = itemView.findViewById(R.id.click_to_updatetv);
    }

}
