package com.zconnect.zutto.zconnect;


import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;

import java.util.Vector;

public class ForumsDiffCallback extends DiffUtil.Callback{

    Vector<ForumCategoriesItemFormat> oldList;
    Vector<ForumCategoriesItemFormat> newList;

    public ForumsDiffCallback(Vector<ForumCategoriesItemFormat> newPersons, Vector<ForumCategoriesItemFormat> oldPersons) {
        this.newList = newPersons;
        this.oldList = oldPersons;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
