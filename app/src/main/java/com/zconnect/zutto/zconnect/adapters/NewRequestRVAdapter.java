package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.newRequestViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.NewRequestItemFormat;

import java.util.Vector;

public class NewRequestRVAdapter extends RecyclerView.Adapter<newRequestViewHolder> {

    private Context context;
    private Vector<NewRequestItemFormat> newRequestItemFormats;

    public NewRequestRVAdapter(Context context, Vector<NewRequestItemFormat> newRequestItemFormats)
    {
        this.context = context;
        this.newRequestItemFormats = newRequestItemFormats;
    }

    @Override
    public newRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_new_request_admin_page, parent, false);

        return new newRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(newRequestViewHolder holder, int position) {

        holder.newRequestName.setText(newRequestItemFormats.get(position).getLocationName());
        holder.setAcceptDeclineButton(newRequestItemFormats.get(position).getKey());
    }

    @Override
    public int getItemCount() {
        return newRequestItemFormats.size();
    }
}
