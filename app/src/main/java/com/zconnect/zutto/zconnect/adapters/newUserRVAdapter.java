package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.ItemFormats.NewUserItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PostedByDetails;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.newUserViewHolder;

import java.util.Vector;

public class newUserRVAdapter extends RecyclerView.Adapter<newUserViewHolder> {

    private Context context;
    private Vector<NewUserItemFormat> newUserItemFormats;

    public newUserRVAdapter(Context context, Vector<NewUserItemFormat> newUserItemFormats) {
        this.context = context;
        this.newUserItemFormats = newUserItemFormats;
    }

    @Override
    public newUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_new_user_admin_page, parent, false);

        return new newUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(newUserViewHolder holder, int position) {
        holder.idImageSDV.setImageURI(Uri.parse(newUserItemFormats.get(position).getIdImageURL()));
        holder.aboutTextView.setText(newUserItemFormats.get(position).getAbout());
        try {
            holder.adminApprovedByTextView.setText(newUserItemFormats.get(position).getApprovedBy().getUsername());
        }catch (Exception e){
            holder.adminApprovedByTextView.setVisibility(View.GONE);
        }
        holder.setAcceptDeclineButton(newUserItemFormats.get(position).getUID());
    }

    @Override
    public int getItemCount() {
        return newUserItemFormats.size();
    }
}
