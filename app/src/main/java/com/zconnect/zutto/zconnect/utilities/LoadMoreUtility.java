package com.zconnect.zutto.zconnect.utilities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.interfaces.OnLoadMoreListener;

public class LoadMoreUtility {
    public final int VIEW_TYPE_NORMAL = 0;
    public final int VIEW_TYPE_LOADER = 1;

    private boolean isLoading;
    private int visibleThreshold;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerView;
    private OnLoadMoreListener onLoadMoreListener;
    public LoadMoreUtility() {
    }

    public LoadMoreUtility(int visibleThreshold, RecyclerView recyclerView) {
        this.visibleThreshold = visibleThreshold;
        this.recyclerView = recyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold))
                {
                    if(onLoadMoreListener != null)
                    {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }


    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar loadMoreBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            loadMoreBar = itemView.findViewById(R.id.more_loader);
        }

        public void setState() {
            loadMoreBar.setIndeterminate(true);
        }
    }

}
