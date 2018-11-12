package com.zconnect.zutto.zconnect.commonModules;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Forums.db";
    public static final String FORUMS_TABLE_NAME = "ForumsCardNotifications";
    public static final String FORUMS_COLUMN_FORUM_NAME = "forumName";
    public static final String FORUMS_COLUMN_FORUM_ID = "forumID";
    public static final String FORUMS_COLUMN_FORUM_CATEGORY = "forumCategory";
    public static final String FORUMS_COLUMN_FORUM_TOTAL_MESSAGES = "forumTotalMessages";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table ForumsCardNotifications " +
                        "(forumName text, forumID text primary key, forumCategory text, forumTotalMessages integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ForumsCardNotifications");
        onCreate(db);
    }

    public boolean replaceForum (String forumName, String forumID, String forumCategory, Integer forumMessages) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("forumName", forumName);
        contentValues.put("forumID", forumID);
        contentValues.put("forumCategory", forumCategory);
        contentValues.put("forumTotalMessages", forumMessages);

        db.replace("ForumsCardNotifications", null, contentValues);
        return true;
    }

    public Cursor getData(int forumID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ForumsCardNotifications where forumID="+forumID+"", null );
        return res;
    }

    public boolean updateForum (String forumName, String forumID, String forumCategory, Integer forumMessages) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("forumName", forumName);
        contentValues.put("forumID", forumID);
        contentValues.put("forumCategory", forumCategory);
        contentValues.put("forumTotalMessages", forumMessages);

        db.update("ForumsCardNotifications", contentValues, "forumID = ? ", new String[] { forumID });
        return true;
    }

    public Map getAllForums(){
        Map<String,Integer> map = new HashMap();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ForumsCardNotifications",null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            map.put(res.getString(res.getColumnIndex(FORUMS_COLUMN_FORUM_ID)),res.getInt(res.getColumnIndex(FORUMS_COLUMN_FORUM_TOTAL_MESSAGES)));
            res.moveToNext();
        }

        map.put("key1",2);
        map.get("key1");
        return map;
    }
    public Vector<ForumCategoriesItemFormat> getTabForums(String tabUID) {
        Vector<ForumCategoriesItemFormat> forums_list = new Vector<ForumCategoriesItemFormat>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ForumsCardNotifications where forumCategory = '" + tabUID + "'",null );
        res.moveToFirst();

        ForumCategoriesItemFormat temp;

        while(res.isAfterLast() == false){
            temp = new ForumCategoriesItemFormat();

            temp.setTabUID(res.getString(res.getColumnIndex(FORUMS_COLUMN_FORUM_CATEGORY)));
            temp.setCatUID(res.getString(res.getColumnIndex(FORUMS_COLUMN_FORUM_ID)));
            temp.setSeenMessages(res.getInt(res.getColumnIndex(FORUMS_COLUMN_FORUM_TOTAL_MESSAGES)));
            temp.setName(res.getString(res.getColumnIndex(FORUMS_COLUMN_FORUM_NAME)));

            forums_list.add(temp);
            res.moveToNext();
        }
        return forums_list;
    }


}
