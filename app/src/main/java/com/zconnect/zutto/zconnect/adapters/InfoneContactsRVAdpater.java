package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.holders.InfoneContactsRVViewHolder;
import com.zconnect.zutto.zconnect.InfoneProfileActivity;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.InfoneContactsRVItem;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityTitle;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;


/**
 * Created by tanmay on 24/3/18.
 */

public class InfoneContactsRVAdpater extends RecyclerView.Adapter<InfoneContactsRVViewHolder> {

    Context context;
    ArrayList<InfoneContactsRVItem> infoneContactsRVItems;
    String catId;
    final DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    public InfoneContactsRVAdpater(Context context, ArrayList<InfoneContactsRVItem> infoneContactsRVItems, String catId) {
        this.context = context;
        this.infoneContactsRVItems = infoneContactsRVItems;
        this.catId=catId;

    }

    @Override
    public InfoneContactsRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_infone_contacts, parent, false);

        return new InfoneContactsRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InfoneContactsRVViewHolder holder, final int position) {

        holder.nametv.setText(infoneContactsRVItems.get(position).getName());
        holder.viewstv.setText(infoneContactsRVItems.get(position).getViews());
        if (infoneContactsRVItems.get(position).getImageThumb() != null) {
            Uri imageuri = Uri.parse(infoneContactsRVItems.get(position).getImageThumb());
            holder.userAvatar.setImageURI(imageuri);
        }
        final ArrayList<String> phoneNums = infoneContactsRVItems.get(position).getPhoneNums();

        holder.callImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(infoneContactsRVItems.get(position).getContactHidden()){
                    createAlertForRequest(infoneContactsRVItems.get(position).getInfoneUserId(),currentUser);
                }else {
                    CounterManager.infoneCallContact();
                    callOptionsDialog(phoneNums);
                }
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(infoneContactsRVItems.get(position).getContactHidden()){
                    createAlertForRequest(infoneContactsRVItems.get(position).getInfoneUserId(),currentUser);
                }else {
                    Intent profileIntent = new Intent(context, InfoneProfileActivity.class);
                    profileIntent.putExtra("infoneUserId", infoneContactsRVItems.get(position).getInfoneUserId());
                    profileIntent.putExtra("catID", catId);
                    context.startActivity(profileIntent);

                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();

                    meta.put("catID",catId);
                    meta.put("type","fromFeature");

                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_CONTACT_OPEN);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                }
            }
        });
    }

    private void callOptionsDialog(final ArrayList<String> phoneArrayList) {

        CounterItemFormat counterItemFormat = new CounterItemFormat();
        HashMap<String, String> meta= new HashMap<>();

        meta.put("type","fromContactList");
        meta.put("catID",catId);

        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_CALL);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        counterItemFormat.setMeta(meta);

        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(android.R.drawable.ic_menu_call);
        builderSingle.setTitle("Select to call: ");


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);

        arrayAdapter.addAll(phoneArrayList);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = phoneArrayList.get(which);

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + strName));
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider cal
                    // ling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);
                Toast.makeText(context, "call being made to " + strName, Toast.LENGTH_SHORT).show();
            }
        });
        builderSingle.show();

    }


    @Override
    public int getItemCount() {
        return infoneContactsRVItems.size();
    }

    void createAlertForRequest(final String itemUID, final DatabaseReference currentUser){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("Contact is hidden. Do you want to request a call?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       requestCallFunction(itemUID,currentUser);

                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        final android.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorHighlight));
    }

    public void requestCallFunction(final String itemUID, DatabaseReference currentUser){

        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                NotificationSender notificationSender = new NotificationSender(context,userItemFormat.getUserUID());

                NotificationItemFormat requestCallNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_REQUEST_CALL,userItemFormat.getUserUID());
                requestCallNotification.setItemKey(itemUID);

                requestCallNotification.setUserMobileNumber(userItemFormat.getMobileNumber());
                requestCallNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                requestCallNotification.setUserName(userItemFormat.getUsername());
                requestCallNotification.setCommunityName(communityTitle);

                notificationSender.execute(requestCallNotification);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
