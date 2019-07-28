package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.LeaderBoard;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.LeaderBoardRVViewHolder;
import com.zconnect.zutto.zconnect.interfaces.OnLoadMoreListener;
import com.zconnect.zutto.zconnect.itemFormats.LeaderBoardItemFormat;

import java.util.Vector;

public class LeaderBoardRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Vector<LeaderBoardItemFormat> leaderBoardItemFormats;
    private Context context;
    private LinearLayout currentUserRank;
    private OnLoadMoreListener mOnLoadMoreListener;
    private final int VIEW_TYPE_NORMAL = 0;
    private final int VIEW_TYPE_LOADER = 1;

    private boolean isLoading;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    public LeaderBoardRVAdapter(Vector<LeaderBoardItemFormat> leaderBoardItemFormats, LinearLayout currentUserRank, Context context, RecyclerView recyclerView) {
        this.leaderBoardItemFormats = leaderBoardItemFormats;
        this.context = context;
        this.currentUserRank = currentUserRank;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if(!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold))
                {
                    if(mOnLoadMoreListener != null)
                    {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == VIEW_TYPE_LOADER)
        {
            view = layoutInflater.inflate(R.layout.row_more_loader, parent, false);
            return new LeaderBoard.LoadingViewHolder(view);
        }
        else if(viewType == VIEW_TYPE_NORMAL)
        {
            view = layoutInflater.inflate(R.layout.row_leader_board, parent, false);
            return new LeaderBoardRVViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LeaderBoardRVViewHolder)
        {
            LeaderBoardRVViewHolder itemHolder = (LeaderBoardRVViewHolder)holder;
            itemHolder.setDetails(leaderBoardItemFormats.get(position));
        }
        else if(holder instanceof LeaderBoard.LoadingViewHolder)
        {
            LeaderBoard.LoadingViewHolder itemHolder = (LeaderBoard.LoadingViewHolder) holder;
            itemHolder.setState();
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if(holder instanceof LeaderBoardRVViewHolder)
        {
            LeaderBoardRVViewHolder itemHolder = (LeaderBoardRVViewHolder)holder;
            if(itemHolder.userUID.equals(FirebaseAuth.getInstance().getUid())){
                currentUserRank.setVisibility(View.GONE);
            }
        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if(holder instanceof LeaderBoardRVViewHolder)
        {
            LeaderBoardRVViewHolder itemHolder = (LeaderBoardRVViewHolder)holder;
            if(itemHolder.userUID.equals(FirebaseAuth.getInstance().getUid())){
                currentUserRank.setVisibility(View.VISIBLE);
            }
        }
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return leaderBoardItemFormats.size();
    }

    @Override
    public int getItemViewType(int position) {
        return leaderBoardItemFormats.get(position) == null ? VIEW_TYPE_LOADER : VIEW_TYPE_NORMAL;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

}
