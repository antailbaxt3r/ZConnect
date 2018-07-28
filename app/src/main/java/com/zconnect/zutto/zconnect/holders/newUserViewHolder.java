package com.zconnect.zutto.zconnect.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.PostedByDetails;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.VerificationUtilities;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityTitle;

public class newUserViewHolder extends RecyclerView.ViewHolder{

    public SimpleDraweeView idImageSDV;
    public TextView aboutTextView,newUserName;
    public TextView adminApprovedByTextView;
    public Button acceptUserButton, declineUserButton;

    public newUserViewHolder(View itemView) {
        super(itemView);
        idImageSDV = (SimpleDraweeView) itemView.findViewById(R.id.id_image_new_user);
        aboutTextView = (TextView) itemView.findViewById(R.id.about_new_user);
        newUserName = (TextView) itemView.findViewById(R.id.name_new_user);
        adminApprovedByTextView = (TextView) itemView.findViewById(R.id.admin_approved_by);
        acceptUserButton = (Button) itemView.findViewById(R.id.accept_new_user);
        declineUserButton = (Button) itemView.findViewById(R.id.decline_new_user);
    }


    public void setAcceptDeclineButton(final String UID){

        acceptUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(UID).child("userType");
                userReference.setValue(UsersTypeUtilities.KEY_VERIFIED);

                final DatabaseReference newUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers").child(UID);
                newUserReference.child("statusCode").setValue(VerificationUtilities.KEY_APPROVED);



                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);

                        PostedByDetails currentAdmin = new PostedByDetails();
                        currentAdmin.setUsername(userItemFormat.getUsername());
                        currentAdmin.setImageThumb(userItemFormat.getImageURL());
                        currentAdmin.setUID(userItemFormat.getUserUID());

                        newUserReference.child("approvedRejectedBy").setValue(currentAdmin);

                        NotificationSender notificationSender = new NotificationSender(itemView.getContext(),userItemFormat.getUserUID());

                        NotificationItemFormat newUserAcceptNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_ACCEPT,userItemFormat.getUserUID());
                        newUserAcceptNotification.setCommunityName(communityTitle);
                        newUserAcceptNotification.setItemKey(UID);

                        notificationSender.execute(newUserAcceptNotification);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        });

        declineUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(UID).child("userType");
                userReference.setValue(UsersTypeUtilities.KEY_NOT_VERIFIED);

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);


                        DatabaseReference newUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers").child(UID);

                        newUserReference.child("statusCode").setValue(VerificationUtilities.KEY_NOT_APPROVED);

                        PostedByDetails currentAdmin = new PostedByDetails();

                        currentAdmin.setUsername(userItemFormat.getUsername());
                        currentAdmin.setImageThumb(userItemFormat.getImageURL());
                        currentAdmin.setUID(userItemFormat.getUserUID());

                        newUserReference.child("approvedRejectedBy").setValue(currentAdmin);

                        NotificationSender notificationSender = new NotificationSender(itemView.getContext(), userItemFormat.getUserUID());

                        NotificationItemFormat newUserRejectNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_REJECT, userItemFormat.getUserUID());
                        newUserRejectNotification.setCommunityName(communityTitle);
                        newUserRejectNotification.setItemKey(UID);

                        notificationSender.execute(newUserRejectNotification);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }


    public void setCardUI(String statusCode,String approvedRejectedBy) {

        if(statusCode.equals(VerificationUtilities.KEY_APPROVED)){
            acceptUserButton.setVisibility(View.GONE);
            adminApprovedByTextView.setText("Approved By " + approvedRejectedBy);

        }if(statusCode.equals(VerificationUtilities.KEY_NOT_APPROVED)){
            declineUserButton.setVisibility(View.GONE);
            adminApprovedByTextView.setText("Rejected By " + approvedRejectedBy);
        }if(statusCode.equals(VerificationUtilities.KEY_PENDING)){

        }
    }
}
