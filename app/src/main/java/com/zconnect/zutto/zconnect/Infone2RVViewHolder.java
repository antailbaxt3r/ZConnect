package com.zconnect.zutto.zconnect;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tanmay on 24/3/18.
 */

public class Infone2RVViewHolder extends RecyclerView.ViewHolder {

    TextView nametv;
    LinearLayout linearLayout;

    public Infone2RVViewHolder(View itemView) {
        super(itemView);

        nametv = (TextView) itemView.findViewById(R.id.tv_name_infone);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_cat_infone);
    }
}
