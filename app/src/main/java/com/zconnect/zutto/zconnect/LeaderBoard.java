package com.zconnect.zutto.zconnect;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceGroup;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.zconnect.zutto.zconnect.interfaces.OnLoadMoreListener;
import com.zconnect.zutto.zconnect.itemFormats.LeaderBoardItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.RecentsItemFormat;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.R.drawable.ic_arrow_back_black_24dp;

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
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout leaderBoardContent;
    private String lastUserUID;
    private int lastUserPointsNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        Toolbar mActionBarToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mActionBarToolbar.setNavigationIcon(ic_arrow_back_black_24dp);
        mActionBarToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));

        mActionBarToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));

        mActionBarToolbar.setTitle("Leader Board");

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
//            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container_leaderboard);
        leaderBoardContent = findViewById(R.id.leader_board_content);

        leaderBoardContent.setVisibility(View.INVISIBLE);
        shimmerFrameLayout.startShimmerAnimation();

        currentUserName = findViewById(R.id.current_user_name);
        currentUserPoints = findViewById(R.id.current_user_points);
        currentUserRank = findViewById(R.id.current_user_rank);
        currentUserImage = findViewById(R.id.current_user_photo);


        currentUserLayout = findViewById(R.id.current_user_layout);
        leaderBoardRV = findViewById(R.id.leader_board_rv);
        linearLayoutManager = new LinearLayoutManager(this);

        leaderBoardRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
        leaderBoardQuery = leaderBoardRef.orderByChild("userPointsNum").limitToLast(20);

        leaderBoardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer i=0;
                if(lastUserUID==null)
                {
                    leaderBoardItemFormats.clear();
                }
                Vector<LeaderBoardItemFormat> leaderBoardItemFormatsTemp = new Vector<>();
                for (DataSnapshot shot: dataSnapshot.getChildren()) {
                    try {
                        i++;
                        if(i==1)
                        {
                            lastUserUID = shot.child("userUID").getValue().toString();
                            lastUserPointsNum = shot.child("userPointsNum").getValue(Integer.class);
                            continue;
                        }
                        LeaderBoardItemFormat tempLeaderBoardItemFormat = new LeaderBoardItemFormat();
                        if (shot.hasChild("userUID") && shot.hasChild("userPointsNum")) {
                            tempLeaderBoardItemFormat.setUserPointsNum(shot.child("userPointsNum").getValue(Integer.class));
                            tempLeaderBoardItemFormat.setUserUID(shot.child("userUID").getValue().toString());
                            tempLeaderBoardItemFormat.setName(shot.child("username").getValue().toString());
                            tempLeaderBoardItemFormat.setImage(shot.child("imageURLThumbnail").getValue().toString());
                            leaderBoardItemFormatsTemp.add(tempLeaderBoardItemFormat);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Collections.reverse(leaderBoardItemFormatsTemp);
                leaderBoardItemFormats.addAll(leaderBoardItemFormatsTemp);
                if(currentUserLayout.getVisibility() != View.VISIBLE)
                {
                    currentUserLayout.setVisibility(View.GONE);
                    for (int j=0;j<leaderBoardItemFormats.size();j++){
                        leaderBoardItemFormats.get(j).setRank("#"+(j+1));
                        if(leaderBoardItemFormats.get(j).getUserUID().equals(FirebaseAuth.getInstance().getUid())){
                            currentUserLayout.setVisibility(View.VISIBLE);
                            currentUserName.setText(leaderBoardItemFormats.get(j).getName());
                            currentUserPoints.setText(leaderBoardItemFormats.get(j).getUserPointsNum() + "");
                            currentUserRank.setText(leaderBoardItemFormats.get(j).getRank());
                            currentUserImage.setImageURI(leaderBoardItemFormats.get(j).getImage());
                        }
                    }
                }

                leaderBoardContent.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.INVISIBLE);
                shimmerFrameLayout.stopShimmerAnimation();
                leaderBoardRVAdapter.notifyDataSetChanged();
                if(lastUserUID!=null)
                    leaderBoardRVAdapter.setLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        leaderBoardRV.setLayoutManager(linearLayoutManager);
        leaderBoardRVAdapter = new LeaderBoardRVAdapter(leaderBoardItemFormats,currentUserLayout,this, leaderBoardRV);
        leaderBoardRVAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                leaderBoardItemFormats.add(null);
                leaderBoardRVAdapter.notifyItemInserted(leaderBoardItemFormats.size() - 1);

                //load more data
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //removing loading item
                        leaderBoardItemFormats.remove(leaderBoardItemFormats.size()-1);
                        leaderBoardRVAdapter.notifyItemRemoved(leaderBoardItemFormats.size());

                        //load data
                        leaderBoardQuery = leaderBoardRef.orderByChild("userPointsNum").endAt(lastUserPointsNum,lastUserUID).limitToLast(20);
                        leaderBoardQuery.addValueEventListener(leaderBoardListener);
                    }
                }, 3000);
            }
        });
        leaderBoardRV.setAdapter(leaderBoardRVAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_leader_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_info) {

            AlertDialog alertDialog = new AlertDialog.Builder(LeaderBoard.this).create();
            alertDialog.setMessage("The more you contribute to your community by adding content, the more points you earn and the higher position you go in the leader board." +
                    "\n\nComing soon: Use the points to redeem special, exclusive and exciting offers!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
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


    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar loadMoreBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            loadMoreBar = (ProgressBar) itemView.findViewById(R.id.more_loader);
        }

        public void setState() {
            loadMoreBar.setIndeterminate(true);
        }
    }
}
