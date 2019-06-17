package com.zconnect.zutto.zconnect.adapters;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.AdminHome;
import com.zconnect.zutto.zconnect.R;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;

public class MakeAdminRVAdapter extends RecyclerView.Adapter<MakeAdminRVAdapter.ViewHolder> {
    Context context;
    private SharedPreferences communitySP;
    public String communityReference;
    DatabaseReference databaseReference;
    Vector<String> admname;
    Vector<String> admimg;
    Vector<String> uid;
    public MakeAdminRVAdapter(Context context, Vector<String> admimg, Vector<String> admname, Vector<String> uid) {
        this.context=context;
        this.admname=admname;
        this.admimg=admimg;
        this.uid=uid;
    }



    @Override
    public MakeAdminRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("oncreatevh", "onCreateViewHolder: ");
        LayoutInflater inflater = LayoutInflater.from(context);
        View adminView = inflater.inflate(R.layout.item_make_admin, parent, false);

        return new MakeAdminRVAdapter.ViewHolder(adminView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        communitySP = context.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference);
        Uri uri = Uri.parse(admimg.get(position));
        //Picasso.with(context).load(uri).into(holder.adminimage);
        holder.adminimage.setImageURI(uri);
        Log.d("img-set", "onBindViewHolder: ");
        holder.adminname.setText(admname.get(position));
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Message")
                        .setMessage("Are you want to make "+ holder.adminname.getText()+" as an admin")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("clii", String.valueOf(holder.getAdapterPosition()));
                                databaseReference.child("Users1").child(uid.get(holder.getAdapterPosition())).child("userType").setValue("admin");
                              databaseReference.child("admins").child(uid.get(holder.getAdapterPosition())).child("ImageThumb").setValue(admimg.get(holder.getAdapterPosition()));
                                databaseReference.child("admins").child(uid.get(holder.getAdapterPosition())).child("UID").setValue(uid.get(holder.getAdapterPosition()));
                                databaseReference.child("admins").child(uid.get(holder.getAdapterPosition())).child("Username").setValue(admname.get(holder.getAdapterPosition())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "User promoted successfully", Toast.LENGTH_SHORT).show();
                                       context.startActivity(new Intent(context,AdminHome.class));

                                   }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error: user promotion failed"+e, Toast.LENGTH_SHORT).show();
                                    }

                               });

                            }
                        }).create().show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return admname.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView adminname;
        SimpleDraweeView adminimage;
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            adminname =(TextView)itemView.findViewById(R.id.tv_name_make_admin);
            adminimage =(SimpleDraweeView)itemView.findViewById(R.id.image_make_admin);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.make_admin_ll);
        }
    }

}
