package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.LeaderBoardRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.LeaderBoardItemFormat;

import java.util.Vector;

public class LeaderBoardRVAdapter extends RecyclerView.Adapter<LeaderBoardRVViewHolder> {
    private Vector<LeaderBoardItemFormat> leaderBoardItemFormats;
    private Context context;
    private LinearLayout currentUserRank;

    public LeaderBoardRVAdapter(Vector<LeaderBoardItemFormat> leaderBoardItemFormats, LinearLayout currentUserRank, Context context) {
        this.leaderBoardItemFormats = leaderBoardItemFormats;
        this.context = context;
        this.currentUserRank = currentUserRank;
    }

    @Override
    public LeaderBoardRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_leader_board, parent, false);
        return new LeaderBoardRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeaderBoardRVViewHolder holder, int position) {
        holder.setDetails(leaderBoardItemFormats.get(position));
    }

    @Override
    public void onViewAttachedToWindow(LeaderBoardRVViewHolder holder) {

        if(holder.userUID.equals(FirebaseAuth.getInstance().getUid())){
            currentUserRank.setVisibility(View.GONE);
        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(LeaderBoardRVViewHolder holder) {
        if(holder.userUID.equals(FirebaseAuth.getInstance().getUid())){
            currentUserRank.setVisibility(View.VISIBLE);
        }

        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return leaderBoardItemFormats.size();
    }
}
