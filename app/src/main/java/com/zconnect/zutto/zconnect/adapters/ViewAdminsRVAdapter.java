package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.zconnect.zutto.zconnect.R;
import java.util.Vector;

/**
 * Created by shubhamk on 26/7/17.
 */


public class ViewAdminsRVAdapter extends RecyclerView.Adapter<ViewAdminsRVAdapter.ViewHolder> {
    Context context;
    Vector<String> admname;
    Vector<String> admimg;
    public ViewAdminsRVAdapter(Context context, Vector<String> admimg, Vector<String> admname) {
        this.context=context;
        this.admname=admname;
        this.admimg=admimg;
    }


    @Override
    public ViewAdminsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("oncreatevh", "onCreateViewHolder: ");
        LayoutInflater inflater = LayoutInflater.from(context);
        View adminView = inflater.inflate(R.layout.view_admins_item_format, parent, false);

        return new ViewAdminsRVAdapter.ViewHolder(adminView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Uri uri = Uri.parse(admimg.get(position));
        holder.adminimage.setImageURI(uri);
        holder.adminname.setText(admname.get(position));
    }


    @Override
    public int getItemCount() {
   return admname.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView adminname;
        ImageView adminimage;
        public ViewHolder(View itemView) {
            super(itemView);
            adminname =(TextView)itemView.findViewById(R.id.admin_name);
            adminimage =(ImageView)itemView.findViewById(R.id.admin_image);

        }
    }
}
