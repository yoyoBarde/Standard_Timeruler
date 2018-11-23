package com.geniihut.payrulerattendance.sync.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.geniihut.payrulerattendance.sync.DBHelper;
import com.geniihut.payrulerattendance.sync.SelectionBuilder;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;

/**
 * Created by macmini3 on 9/15/15.
 */
public class UserProvider extends ContentProvider {
    private static final String TAG = UserProvider.class.getSimpleName();

    // helper constants for use with the UriMatcher
    private static final int USER_LIST = 1;
    private static final int USER_ID = 2;
    private static final UriMatcher sUriMatcher;

    private DBHelper mHelper = null;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.User.URI_PATH, USER_LIST);
        sUriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.User.URI_PATH + "/#", USER_ID);
    }

    @Override
    public boolean onCreate() {
        mHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
//        Log.e(TAG, "getType\n" + uri);
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USER_LIST:
                TIME_IN_LIST:
                return UserContract.User.CONTENT_TYPE;
            case USER_ID:
                return UserContract.User.CONTENT_ID_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        Log.e(TAG, "query\n" + uri + "\n" + projection + "\n" + selection + "\n" + selectionArgs + "\n" + sortOrder);
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USER_LIST:
                builder.table(UserContract.User.TABLE);
                builder.where(selection, selectionArgs);
                break;
            case USER_ID:
                String id = uri.getLastPathSegment();
                builder.table(UserContract.User.TABLE)
                        .where(UserContract.User._ID + "=?", id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Cursor cursor = builder.query(db, projection, null, null, sortOrder, null);
        // Note: Notification URI must be manually set here for loaders to correctly
        // register ContentObservers.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //        Log.e(TAG, "insert\n" + uri + "\n" + values);
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case USER_LIST:
                long id = db.insertOrThrow(UserContract.User.TABLE, null, values);
                result = Uri.parse(UserContract.User.CONTENT_URI + "/" + id);
                break;
            case USER_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        getContext().getContentResolver().notifyChange(uri, null, true);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        Log.e(TAG, "delete\n" + uri + "\n" + selection + "\n" + selectionArgs);
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case USER_LIST:
                count = builder.table(UserContract.User.TABLE)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case USER_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(UserContract.User.TABLE)
                        .where(UserContract.User._ID + "=?", id)
                        .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        getContext().getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        Log.e(TAG, "update\n" + uri + "\n" + values + "\n" + selection + "\n" + selectionArgs);
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case USER_LIST:
                count = builder.table(UserContract.User.TABLE)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case USER_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(UserContract.User.TABLE)
                        .where(UserContract.User._ID + "=?", id)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        getContext().getContentResolver().notifyChange(uri, null, false);
        return count;
    }
}
