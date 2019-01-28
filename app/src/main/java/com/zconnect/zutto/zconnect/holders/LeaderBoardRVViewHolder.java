package com.zconnect.zutto.zconnect.holders;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.LeaderBoardItemFormat;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;



public class LeaderBoardRVViewHolder extends RecyclerView.ViewHolder {
    public String userUID;
    public TextView userRank,userName,userPoints;
    public ValueEventListener nameEventListener, imageEventListener;
    public LinearLayout leaderBoardLayout;

    public SimpleDraweeView userImage;
    private DatabaseReference userNameRef,userImageRef;
    public LeaderBoardRVViewHolder(View itemView) {
        super(itemView);
        userRank= itemView.findViewById(R.id.user_rank);
        userName = itemView.findViewById(R.id.user_name);
        userImage = itemView.findViewById(R.id.user_photo);
        userPoints = itemView.findViewById(R.id.user_points);
        leaderBoardLayout = itemView.findViewById(R.id.leader_board_layout);

    }

    public void setDetails(LeaderBoardItemFormat leaderBoardItemFormat) {
        userUID = leaderBoardItemFormat.getUserUID();
        userName.setText(leaderBoardItemFormat.getName());
        userPoints.setText(leaderBoardItemFormat.getUserPointsNum() + "");
        userRank.setText(leaderBoardItemFormat.getRank());
        userImage.setImageURI(leaderBoardItemFormat.getImage());
        if(leaderBoardItemFormat.getUserUID().equals(FirebaseAuth.getInstance().getUid())){
            leaderBoardLayout.setBackgroundResource(R.color.colorPrimaryDark);
            userName.setTextColor(itemView.getResources().getColor(R.color.white));
            userPoints.setTextColor(itemView.getResources().getColor(R.color.white));
            userRank.setTextColor(itemView.getResources().getColor(R.color.white));
        }else{
            leaderBoardLayout.setBackgroundResource(R.color.white);
            userName.setTextColor(itemView.getResources().getColor(R.color.secondaryText));
            userPoints.setTextColor(itemView.getResources().getColor(R.color.secondaryText));
            userRank.setTextColor(itemView.getResources().getColor(R.color.secondaryText));
        }
    }
}
