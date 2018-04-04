package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.R;

/**
 * Created by Lokesh Garg on 02-04-2018.
 */

public class CabPoolLocationRVViewHolder extends RecyclerView.ViewHolder {

    private TextView name;

    public CabPoolLocationRVViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.location_name);
    }

    public void setLocationName(String locationName){
        name.setText(locationName);
    }
}


