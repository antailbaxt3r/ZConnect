package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.PostedByDetails;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.VerificationUtilities;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

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

                DatabaseReference newUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers").child(UID);
                newUserReference.child("statusCode").setValue(VerificationUtilities.KEY_APPROVED);

                PostedByDetails currentAdmin = new PostedByDetails();

                currentAdmin.setUsername(UserUtilities.currentUser.getUsername());
                currentAdmin.setImageThumb(UserUtilities.currentUser.getImageURL());
                currentAdmin.setUID(UserUtilities.currentUser.getUserUID());

                newUserReference.child("approvedRejectedBy").setValue(currentAdmin);

                NotificationSender notificationSender = new NotificationSender(itemView.getContext(),UserUtilities.currentUser.getUserUID());

                NotificationItemFormat newUserAcceptNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_ACCEPT,UserUtilities.currentUser.getUserUID());
                newUserAcceptNotification.setCommunityName(UserUtilities.CommunityName);
                newUserAcceptNotification.setItemKey(UID);

                notificationSender.execute(newUserAcceptNotification);
            }

        });

        declineUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(UID).child("userType");
                userReference.setValue(UsersTypeUtilities.KEY_NOT_VERIFIED);

                DatabaseReference newUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers").child(UID);

                newUserReference.child("statusCode").setValue(VerificationUtilities.KEY_NOT_APPROVED);

                PostedByDetails currentAdmin = new PostedByDetails();

                currentAdmin.setUsername(UserUtilities.currentUser.getUsername());
                currentAdmin.setImageThumb(UserUtilities.currentUser.getImageURL());
                currentAdmin.setUID(UserUtilities.currentUser.getUserUID());

                newUserReference.child("approvedRejectedBy").setValue(currentAdmin);

                NotificationSender notificationSender = new NotificationSender(itemView.getContext(),UserUtilities.currentUser.getUserUID());

                NotificationItemFormat newUserRejectNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_NEW_USER_REJECT,UserUtilities.currentUser.getUserUID());
                newUserRejectNotification.setCommunityName(UserUtilities.CommunityName);
                newUserRejectNotification.setItemKey(UID);

                notificationSender.execute(newUserRejectNotification);
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
