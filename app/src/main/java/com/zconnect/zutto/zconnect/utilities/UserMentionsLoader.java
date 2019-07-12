package com.zconnect.zutto.zconnect.utilities;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.linkedin.android.spyglass.mentions.Mentionable;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.zconnect.zutto.zconnect.itemFormats.UserMentionsFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple class to get suggestions from a JSONArray (represented as a file on disk), which can then
 * be mentioned by the user by tapping on the suggestion.
 */
public  abstract class UserMentionsLoader<T extends Mentionable> {

    protected T[] mData;
    private static final String TAG = UserMentionsLoader.class.getSimpleName();

    public UserMentionsLoader(ArrayList<UserMentionsFormat> joinedUsersList) {

        new LoadJSONArray(joinedUsersList).execute();
    }


    public abstract T[] loadData(ArrayList<UserMentionsFormat> arr);

    // Returns a subset
    public List<T> getSuggestions(QueryToken queryToken) {
        String prefix = queryToken.getKeywords().toLowerCase();
        List<T> suggestions = new ArrayList<>();
        if (mData != null) {
            for (T suggestion : mData) {
                String name = suggestion.getSuggestiblePrimaryText().toLowerCase();
                if (name.startsWith(prefix)) {
                    suggestions.add(suggestion);
                }
            }
        }
        return suggestions;
    }

    // Loads data from JSONArray file, defined in the raw resources folder
    private class LoadJSONArray extends AsyncTask<Void, Void, ArrayList<UserMentionsFormat>> {


        private ArrayList<UserMentionsFormat> joinedUsersList;

        public LoadJSONArray(ArrayList<UserMentionsFormat> joinedUsersReference) {
           this.joinedUsersList = joinedUsersReference;
        }

        @Override
        protected ArrayList<UserMentionsFormat> doInBackground(Void... params) {

            return joinedUsersList;
        }

        @Override
        protected void onPostExecute(ArrayList<UserMentionsFormat> arr) {
            super.onPostExecute(arr);
            mData = loadData(arr);
        }
    }
}