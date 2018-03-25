package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.R;

/**
 * Created by tanmay on 24/3/18.
 */

public class Infone2RVViewHolder extends RecyclerView.ViewHolder {

    public TextView nametv;
    public SimpleDraweeView catImage;
    public LinearLayout linearLayout;

    public Infone2RVViewHolder(View itemView) {
        super(itemView);

        nametv = (TextView) itemView.findViewById(R.id.tv_name_infone);
        catImage = (SimpleDraweeView) itemView.findViewById(R.id.image_infone_cat);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_cat_infone);
    }
}
