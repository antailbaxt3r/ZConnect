package com.zconnect.zutto.zconnect;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceGroup;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.adapters.LeaderBoardRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.LeaderBoardItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.RecentsItemFormat;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class LeaderBoard extends BaseActivity {

    private RecyclerView leaderBoardRV;
    private LinearLayoutManager linearLayoutManager;
    private LeaderBoardRVAdapter leaderBoardRVAdapter;
    private Vector<LeaderBoardItemFormat> leaderBoardItemFormats = new Vector<>();

    private DatabaseReference leaderBoardRef;
    private Query leaderBoardQuery;
    private ValueEventListener leaderBoardListener;
    private LinearLayout currentUserLayout;

    private TextView currentUserName,currentUserPoints,currentUserRank;
    private SimpleDraweeView currentUserImage;
    private ProgressBar progressBar;
    private LinearLayout leaderBoardContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);

        mActionBarToolbar.setTitle("Community Leaders ");

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        progressBar = findViewById(R.id.progress_bar);
        leaderBoardContent = findViewById(R.id.leader_board_content);

        leaderBoardContent.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        currentUserName = findViewById(R.id.current_user_name);
        currentUserPoints = findViewById(R.id.current_user_points);
        currentUserRank = findViewById(R.id.current_user_rank);
        currentUserImage = findViewById(R.id.current_user_photo);


        currentUserLayout = findViewById(R.id.current_user_layout);
        leaderBoardRV = findViewById(R.id.leader_board_rv);
        linearLayoutManager = new LinearLayoutManager(this);

        leaderBoardRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
        leaderBoardQuery = leaderBoardRef.orderByChild("points");

        leaderBoardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                leaderBoardItemFormats.clear();
                Integer i=0;

                for (DataSnapshot shot: dataSnapshot.getChildren()) {
                    try {
                        i++;
                        LeaderBoardItemFormat tempLeaderBoardItemFormat = new LeaderBoardItemFormat();
                        if (shot.hasChild("userUID") && shot.hasChild("points")) {
                            tempLeaderBoardItemFormat.setPoints(shot.child("points").getValue().toString());
                            tempLeaderBoardItemFormat.setUserUID(shot.child("userUID").getValue().toString());
                            tempLeaderBoardItemFormat.setName(shot.child("username").getValue().toString());
                            tempLeaderBoardItemFormat.setImage(shot.child("imageURLThumbnail").getValue().toString());
                            leaderBoardItemFormats.add(tempLeaderBoardItemFormat);


                        }

                    } catch (Exception e) {

                    }

                }

                Collections.reverse(leaderBoardItemFormats);
                for (int j=0;j<leaderBoardItemFormats.size();j++){
                    leaderBoardItemFormats.get(j).setRank("#"+(j+1));
                    if(leaderBoardItemFormats.get(j).getUserUID().equals(FirebaseAuth.getInstance().getUid())){
                        currentUserName.setText(leaderBoardItemFormats.get(j).getName());
                        currentUserPoints.setText(leaderBoardItemFormats.get(j).getPoints());
                        currentUserRank.setText(leaderBoardItemFormats.get(j).getRank());
                        currentUserImage.setImageURI(leaderBoardItemFormats.get(j).getImage());
                    }
                }

                leaderBoardContent.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                leaderBoardRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        leaderBoardRV.setLayoutManager(linearLayoutManager);
        leaderBoardRVAdapter = new LeaderBoardRVAdapter(leaderBoardItemFormats,currentUserLayout,this);
        leaderBoardRV.setAdapter(leaderBoardRVAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        leaderBoardQuery.addValueEventListener(leaderBoardListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        leaderBoardQuery.removeEventListener(leaderBoardListener);
    }
}
