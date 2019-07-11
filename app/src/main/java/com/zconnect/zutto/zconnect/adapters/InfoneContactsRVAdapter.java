package com.zconnect.zutto.zconnect.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.InfoneContactListActivity;
import com.zconnect.zutto.zconnect.ZConnectDetails;
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

public class InfoneContactsRVAdapter extends RecyclerView.Adapter<InfoneContactsRVViewHolder> {

    private String TAG = InfoneContactsRVAdapter.class.getSimpleName();

    Context context;
    ArrayList<InfoneContactsRVItem> infoneContactsRVItems = new ArrayList<InfoneContactsRVItem>();
    String catId;
    DatabaseReference forumReference;
    final DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    public InfoneContactsRVAdapter(Context context, ArrayList<InfoneContactsRVItem> infoneContactsRVItems, String catId) {
        this.context = context;
        this.infoneContactsRVItems = infoneContactsRVItems;
        this.catId = catId;

    }


    @Override
    public InfoneContactsRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_infone_contacts, parent, false);

        return new InfoneContactsRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InfoneContactsRVViewHolder holder, final int position) {

        holder.nametv.setText(infoneContactsRVItems.get(position).getName());
        holder.dialogVerifyNameEt.setText(infoneContactsRVItems.get(position).getName());
        holder.viewstv.setText(infoneContactsRVItems.get(position).getViews());
        if (infoneContactsRVItems.get(position).getDesc() != null) {
            holder.desctv.setText(infoneContactsRVItems.get(position).getDesc());
            holder.dialogVerifyNameEt.setText(infoneContactsRVItems.get(position).getName());
            holder.dialogRequestCallNameEt.setText(infoneContactsRVItems.get(position).getName());

        }
        if (infoneContactsRVItems.get(position).getImageThumb() != null) {
            Uri imageuri = Uri.parse(infoneContactsRVItems.get(position).getImageThumb());
            holder.userAvatar.setImageURI(imageuri);
            holder.dialogVerifyProfileImg.setImageURI(imageuri);
            holder.dialogRequestCallProfileImg.setImageURI(imageuri);

        }
        final ArrayList<String> phoneNums = infoneContactsRVItems.get(position).getPhoneNums();




            holder.dialogRequestCall1btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.dialogVerifyphoneEt.setText(holder.dialogRequestCall1btn.getText().toString());
                    makeCall(holder.dialogRequestCall1btn.getText().toString(), holder.verifyDialog, holder.requestCallDialog);
                }
            });
            holder.dialogRequestCall2btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.dialogVerifyphoneEt.setText(holder.dialogRequestCall2btn.getText().toString());

                    makeCall(holder.dialogRequestCall2btn.getText().toString(), holder.verifyDialog, holder.requestCallDialog);
                }
            });
            try {
                final String phoneNum = phoneNums.get(1);
                if (phoneNum.length() < 9) {
                    throw new Exception();
                }
                holder.whatsAppImageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redirectToWhatsApp(phoneNum);
                    }
                });


            } catch (Exception e) {
                holder.whatsAppImageBtn.setVisibility(View.GONE);
            }

            holder.callImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (infoneContactsRVItems.get(position).getContactHidden()) {
                        createAlertForRequest(infoneContactsRVItems.get(position).getInfoneUserId(), currentUser);
                    } else {
                        String phoneNum1, phoneNum2;
                        try {
                            phoneNum1 = phoneNums.get(0);
                            if (phoneNum1.length() < 9) {
                                throw new Exception();
                            }
                            holder.dialogRequestCall1btn.setText(phoneNum1);
                        } catch (Exception e) {
                            holder.dialogRequestCall1btn.setVisibility(View.GONE);
                        }
                        try {
                            phoneNum2 = phoneNums.get(1);
                            if (phoneNum2.length() < 9) {
                                throw new Exception();
                            }
                            holder.dialogRequestCall2btn.setText(phoneNum2);
                        } catch (Exception e) {
                            holder.dialogRequestCall2btn.setVisibility(View.GONE);
                        }
                        holder.requestCallDialog.show();

                    }

                }
            });

            final DatabaseReference databaseReferenceInfone = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                    .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW);
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final DatabaseReference databaseReferenceContact = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                    .child(communityReference).child(ZConnectDetails.INFONE_DB_NEW).child("numbers").child(infoneContactsRVItems.get(position).getInfoneUserId());

            holder.dialogVerifyYesbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReferenceInfone.child("numbers").child(infoneContactsRVItems.get(position).getInfoneUserId()).child("invalid").child(mAuth.getCurrentUser().getUid()).removeValue();
                    databaseReferenceInfone.child("numbers").child(infoneContactsRVItems.get(position).getInfoneUserId()).child("valid").child(mAuth.getCurrentUser().getUid()).setValue("true");
                    long postTimeMillis = System.currentTimeMillis();
                    databaseReferenceContact.child("verifiedDate").setValue(postTimeMillis);
                    holder.verifyDialog.dismiss();

                }
            });
            holder.dialogVerifyNobtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReferenceInfone.child("numbers").child(infoneContactsRVItems.get(position).getInfoneUserId()).child("valid").child(mAuth.getCurrentUser().getUid()).removeValue();
                    databaseReferenceInfone.child("numbers").child(infoneContactsRVItems.get(position).getInfoneUserId()).child("invalid").child(mAuth.getCurrentUser().getUid()).setValue("true");
                    long postTimeMillis = System.currentTimeMillis();
                    databaseReferenceContact.child("verifiedDate").setValue(postTimeMillis);
                    holder.verifyDialog.dismiss();

                }
            });

            holder.hiddentv.setVisibility(View.GONE);
            holder.callImageBtn.setVisibility(View.VISIBLE);
            holder.nametv.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primaryText));
            holder.desctv.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primaryText));

            if (infoneContactsRVItems.get(position).getContactHidden()) {
                holder.hiddentv.setVisibility(View.VISIBLE);
                holder.whatsAppImageBtn.setVisibility(View.GONE);
                holder.callImageBtn.setVisibility(View.GONE);
                holder.nametv.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_holo_light));
                holder.desctv.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_holo_light));
                holder.hiddentv.setText("hidden");
            }

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (infoneContactsRVItems.get(position).getContactHidden()) {
                        createAlertForRequest(infoneContactsRVItems.get(position).getInfoneUserId(), currentUser);
                    } else {
                        Intent profileIntent = new Intent(context, InfoneProfileActivity.class);
                        profileIntent.putExtra("infoneUserId", infoneContactsRVItems.get(position).getInfoneUserId());
                        profileIntent.putExtra("catID", catId);

                        profileIntent.putExtra("infoneUserImageThumb",infoneContactsRVItems.get(position).getImageThumb());
                        context.startActivity(profileIntent);

                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta = new HashMap<>();

                        meta.put("catID", catId);

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


    private void makeCall(final String phoneNum, final Dialog verifyDialog, final Dialog dialogCallRequest) {

        CounterItemFormat counterItemFormat = new CounterItemFormat();
        HashMap<String, String> meta = new HashMap<>();

        meta.put("type", "fromContactList");
        meta.put("catID", catId);

        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_CALL);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        counterItemFormat.setMeta(meta);

        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();
        String strName = phoneNum;


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
        InfoneContactListActivity.hasCalled = true;
        context.startActivity(intent);
        dialogCallRequest.dismiss();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                verifyDialog.show();
            }
        }, 5000);


        Toast.makeText(context, "call being made to " + strName, Toast.LENGTH_SHORT).show();


    }


    @Override
    public int getItemCount() {
        return infoneContactsRVItems.size();
    }

    void createAlertForRequest(final String itemUID, final DatabaseReference currentUser) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("Contact is hidden. Do you want to request a call?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        requestCallFunction(itemUID, currentUser);

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

    public void requestCallFunction(final String itemUID, DatabaseReference currentUser) {

        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                NotificationSender notificationSender = new NotificationSender(context, userItemFormat.getUserUID());

                NotificationItemFormat requestCallNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_REQUEST_CALL, userItemFormat.getUserUID());
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

    private void redirectToWhatsApp(String number){
        try {
            String text = "";

            String toNumber = "91"+number;


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
            context.startActivity(intent);
        }
        catch (Exception e){
            Log.d("InfoneProfileActivity",e.toString());
        }

    }

}
