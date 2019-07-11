package com.zconnect.zutto.zconnect;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;

import java.util.ArrayList;

public class JoinedForumsDiffCallback extends DiffUtil.Callback {
    private final ArrayList<ForumCategoriesItemFormat> oldJoinedForumsList;

    public JoinedForumsDiffCallback(ArrayList<ForumCategoriesItemFormat> oldJoinedForumsList, ArrayList<ForumCategoriesItemFormat> newJoinedForumsList) {
        this.oldJoinedForumsList = oldJoinedForumsList;
        this.newJoinedForumsList = newJoinedForumsList;
    }

    private final ArrayList<ForumCategoriesItemFormat> newJoinedForumsList;

    @Override
    public int getOldListSize() {
        return oldJoinedForumsList.size();
    }


    @Override
    public int getNewListSize() {
        return newJoinedForumsList.size();
    }


    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if(oldJoinedForumsList.get(oldItemPosition).getCatUID().equals(newJoinedForumsList.get(newItemPosition).getCatUID())) {
            return true;
        }
        return false;
    }


    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldJoinedForumsList.get(oldItemPosition).equals(newJoinedForumsList.get(newItemPosition));
    }


    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
