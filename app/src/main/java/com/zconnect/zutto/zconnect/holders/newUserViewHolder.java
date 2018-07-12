package com.zconnect.zutto.zconnect.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.ItemFormats.NewUserItemFormat;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.Utilities.UsersTypeUtilities;
import com.zconnect.zutto.zconnect.Utilities.VerificationUtilities;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

public class newUserViewHolder extends RecyclerView.ViewHolder{

    public SimpleDraweeView idImageSDV;
    public TextView aboutTextView;
    public TextView adminApprovedByTextView;
    public Button acceptUserButton, declineUserButton;

    public newUserViewHolder(View itemView) {
        super(itemView);
        idImageSDV = (SimpleDraweeView) itemView.findViewById(R.id.id_image_new_user);
        aboutTextView = (TextView) itemView.findViewById(R.id.about_new_user);
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

                DatabaseReference newUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers").child(UID).child("statusCode");
                newUserReference.setValue(VerificationUtilities.KEY_APPROVED);
            }

        });

        declineUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference newUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers").child(UID).child("statusCode");
                newUserReference.setValue(VerificationUtilities.KEY_NOT_APPROVED);
            }
        });

    }


}
