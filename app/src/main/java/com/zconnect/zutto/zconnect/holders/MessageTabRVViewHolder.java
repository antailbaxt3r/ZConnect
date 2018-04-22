package com.zconnect.zutto.zconnect.holders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.R;

import java.util.Calendar;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

/**
 * Created by tanmay on 25/3/18.
 */

public class MessageTabRVViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public ImageButton del;
    public TextView message;
    public LinearLayout linearLayout;
    DatabaseReference UsersReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("messages");
    public MessageTabRVViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        message = (TextView) itemView.findViewById(R.id.name_tv_message);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_message);
        del=(ImageButton)itemView.findViewById(R.id.delete_btn_message);

    }

    public void openAlert(final String message, final String senderUID, final String chatID){
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mView.getContext());
                builder1.setTitle("Message");
                builder1.setMessage(message);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Chat",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startChat(chatID,message,senderUID);
                            }
                        });

                builder1.setNeutralButton(
                        "Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteMessage(senderUID,chatID);
                            }
                        });

                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                 AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    public void startChat(String chatID,String message,String senderUID) {
        Calendar calendar;
        calendar = Calendar.getInstance();

        Toast.makeText(mView.getContext(), "Start Chat", Toast.LENGTH_SHORT).show();

        UsersReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chats").child(chatID).child("message").setValue(message);
        UsersReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chats").child(chatID).child("type").setValue("recieved");
        UsersReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chats").child(chatID).child("chatUID").setValue(chatID);
        UsersReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chats").child(chatID).child("sender").setValue(senderUID);
        UsersReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chats").child(chatID).child("timeStamp").setValue(calendar.getTimeInMillis());

        UsersReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(chatID).removeValue();
        //UsersReference.child("users").child(senderUID).child("messages").child(chatID).removeValue();

        Intent i = new Intent(mView.getContext(), ChatActivity.class);
        i.putExtra("ref",UsersReference.child("chats").child(chatID).toString());
        mView.getContext().startActivity(i);
    }

    public void deleteMessage(String senderUID,String chatID){
        Toast.makeText(mView.getContext(), "delete Message", Toast.LENGTH_SHORT).show();
        UsersReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chats").child(chatID).removeValue();
        UsersReference.child("users").child(senderUID).child("messages").child(chatID).removeValue();
        UsersReference.child("chats").child(chatID).removeValue();
    }
}
