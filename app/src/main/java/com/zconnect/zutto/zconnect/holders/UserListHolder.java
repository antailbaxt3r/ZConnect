package com.zconnect.zutto.zconnect.holders;

import android.content.Context;
import android.opengl.Visibility;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.R;

import org.w3c.dom.Text;

import mabbas007.tagsedittext.TagsEditText;

import static android.graphics.Typeface.BOLD;

public class UserListHolder extends RecyclerView.ViewHolder {

    private String TAG = UserListHolder.class.getSimpleName();

    private EditText mTyper;
    private View mView;
    private Context mContext;
    public UserListHolder(View itemView, EditText typer, Context context)
    {
        super(itemView);
        mContext = context;
        mTyper = typer;
        mView = itemView;
    }

    public void setUsername(String username) {
        TextView usernameTextView = (TextView)mView.findViewById(R.id.username_users_mention_row);
        usernameTextView.setText(username);
    }

    public void onClickItem(final String username, final String userUID, final RecyclerView usersListRV, final int pos_of_at_rate) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, mTyper.getText().toString());
                String pattern1 = mContext.getResources().getString(R.string.user_mentions_pattern_username_start);
                String pattern2 = mContext.getResources().getString(R.string.user_mentions_pattern_uid_start);
                String text = mTyper.getText().toString().substring(0, pos_of_at_rate + 1) + pattern1 + username + pattern2 + userUID + " ";
                String visibleText = mTyper.getText().toString().substring(0, pos_of_at_rate + 1) + username + " ";
//                String typerText = mTyper.getText().toString().substring(0, mTyper.getText().toString().length()-1) + username;
//                SpannableString spannableString = new SpannableString(typerText);
//                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.lightblue500));
//                StyleSpan styleSpan = new StyleSpan(BOLD);
//                spannableString.setSpan(foregroundColorSpan, typerText.length() - 1, typerText.indexOf(username) + username.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//                spannableString.setSpan(styleSpan, typerText.length() - 1, typerText.indexOf(username) + username.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//                mTyper.setText(spannableString);
//                ChatActivity.tag_flag = true;
//                ChatActivity.text_with_tag = text;
                mTyper.setText(visibleText);
                mTyper.setSelection(mTyper.getText().toString().length());
                usersListRV.setVisibility(View.GONE);
            }
        });
    }
}
