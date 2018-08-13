package com.zconnect.zutto.zconnect.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.commonModules.viewImage;
import com.zconnect.zutto.zconnect.itemFormats.NewUserItemFormat;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.newUserViewHolder;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.VerificationUtilities;

import java.util.Vector;

public class NewUserRVAdapter extends RecyclerView.Adapter<newUserViewHolder> {

    private Context context;
    private Vector<NewUserItemFormat> newUserItemFormats;


    public NewUserRVAdapter(Context context, Vector<NewUserItemFormat> newUserItemFormats) {
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
        holder.openImage(context,newUserItemFormats.get(position).getName(),newUserItemFormats.get(position).getIdImageURL());
        holder.aboutTextView.setText(newUserItemFormats.get(position).getAbout());
        holder.newUserName.setText(newUserItemFormats.get(position).getName());
        try {
            if (!newUserItemFormats.get(position).getApprovedRejectedBy().getUsername().equals("none")) {
                holder.setCardUI(newUserItemFormats.get(position).getStatusCode(), newUserItemFormats.get(position).getApprovedRejectedBy().getUsername());
            } else {
                holder.adminApprovedByTextView.setVisibility(View.GONE);
            }
        }catch (Exception e){}

        holder.setAcceptDeclineButton(newUserItemFormats.get(position).getUID());


    }

    @Override
    public int getItemCount() {
        return newUserItemFormats.size();
    }


}
