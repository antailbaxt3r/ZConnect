package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;

/**
 * Created by tanmay on 24/3/18.
 */

public class InfoneContactsRVViewHolder extends RecyclerView.ViewHolder {

    public TextView nametv;
    public TextView viewstv;
    public ImageButton callImageBtn;
    public LinearLayout linearLayout;
    public SimpleDraweeView userAvatar;
    public TextView desctv;

    public InfoneContactsRVViewHolder(View itemView) {
        super(itemView);

        nametv = (TextView) itemView.findViewById(R.id.tv_name_infone_contacts);
        viewstv = (TextView) itemView.findViewById(R.id.tv_views_infone_contacts);
        callImageBtn = (ImageButton) itemView.findViewById(R.id.image_btn_call_infone_contacts);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_hori_infone_contacts);
        userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.image_infone_contacts);
        desctv = (TextView) itemView.findViewById(R.id.tv_desc_infone_contacts);
    }
}
