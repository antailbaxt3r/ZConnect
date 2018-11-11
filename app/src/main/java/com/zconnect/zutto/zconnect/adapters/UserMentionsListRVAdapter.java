package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.InfoneContactsRVViewHolder;
import com.zconnect.zutto.zconnect.holders.UserListHolder;
import com.zconnect.zutto.zconnect.itemFormats.UserMentionsFormat;

import java.util.ArrayList;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class UserMentionsListRVAdapter extends RecyclerView.Adapter<UserListHolder> {
    ArrayList<UserMentionsFormat> usersList;
    private EditText mTyper;
    private RecyclerView mUsersListRV;
    private int pos_of_at_rate;

    public UserMentionsListRVAdapter(ArrayList<UserMentionsFormat> usersList, EditText typer, RecyclerView usersListRV, int pos_of_at_rate)
    {
           this.usersList = usersList;
           mTyper = typer;
           mUsersListRV = usersListRV;
           this.pos_of_at_rate = pos_of_at_rate;
    }


    @Override
    public UserListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.users_mention_row, viewGroup, false);

        return new UserListHolder(view, mTyper, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(UserListHolder userListHolder, int position) {
        userListHolder.setUsername(usersList.get(position).getUsername());
        userListHolder.onClickItem(usersList.get(position).getUsername(), usersList.get(position).getUserUID(), mUsersListRV, pos_of_at_rate);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
