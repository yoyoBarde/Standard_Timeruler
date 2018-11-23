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

/**
 * Created by macmini3 on 9/16/15.
 */
public class TimeInProvider extends ContentProvider {
    private static final String TAG = TimeInProvider.class.getSimpleName();

    // helper constants for use with the UriMatcher
    private static final int TIME_IN_LIST = 1;
    private static final int TIME_IN_ID = 2;
    private static final int TIME_IN_USER = 3;
    private static final int TIME_IN_USER_ID = 4;
    private static final UriMatcher sUriMatcher;

    private DBHelper mHelper = null;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TimeInContract.CONTENT_AUTHORITY, TimeInContract.TimeIn.URI_PATH, TIME_IN_LIST);
        sUriMatcher.addURI(TimeInContract.CONTENT_AUTHORITY, TimeInContract.TimeIn.URI_PATH + "/#", TIME_IN_ID);
        sUriMatcher.addURI(TimeInContract.CONTENT_AUTHORITY, TimeInContract.TimeInUser.URI_PATH, TIME_IN_USER);
        sUriMatcher.addURI(TimeInContract.CONTENT_AUTHORITY, TimeInContract.TimeInUser.URI_PATH + "/#", TIME_IN_USER_ID);
    }

    @Override
    public boolean onCreate() {
        mHelper = new DBHelper(getContext());
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
//        Log.e(TAG, "getType\n" + uri);
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TIME_IN_LIST:
                TIME_IN_LIST:
                return TimeInContract.TimeIn.CONTENT_TYPE;
            case TIME_IN_ID:
                return TimeInContract.TimeIn.CONTENT_ID_TYPE;
            case TIME_IN_USER:
                return TimeInContract.TimeInUser.CONTENT_TYPE;
            case TIME_IN_USER_ID:
                return TimeInContract.TimeInUser.CONTENT_ID_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        Log.e(TAG, "query\n" + uri + "\n" + projection + "\n" + selection + "\n" + selectionArgs + "\n" + sortOrder);
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        String id;
        switch (match) {
            case TIME_IN_LIST:
                builder.table(TimeInContract.TimeIn.TABLE);
                builder.where(selection, selectionArgs);
                break;
            case TIME_IN_ID:
                id = uri.getLastPathSegment();
                builder.table(TimeInContract.TimeIn.TABLE)
                        .where(TimeInContract.TimeIn._ID + "=?", id);
                break;
            case TIME_IN_USER:
                builder.table(TimeInContract.TimeInUser.TABLE);
                builder.where(selection, selectionArgs);
                break;
            case TIME_IN_USER_ID:
                id = uri.getLastPathSegment();
                builder.table(TimeInContract.TimeInUser.TABLE)
                        .where(TimeInContract.TimeInUser._ID + "=?", id);
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
            case TIME_IN_LIST:
                long id = db.insertOrThrow(TimeInContract.TimeIn.TABLE, null, values);
                result = Uri.parse(TimeInContract.TimeIn.CONTENT_URI + "/" + id);
                break;
            case TIME_IN_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            case TIME_IN_USER:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            case TIME_IN_USER_ID:
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
            case TIME_IN_LIST:
                count = builder.table(TimeInContract.TimeIn.TABLE)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case TIME_IN_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(TimeInContract.TimeIn.TABLE)
                        .where(TimeInContract.TimeIn._ID + "=?", id)
                        .delete(db);
                break;
            case TIME_IN_USER:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            case TIME_IN_USER_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
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
            case TIME_IN_LIST:
                count = builder.table(TimeInContract.TimeIn.TABLE)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case TIME_IN_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(TimeInContract.TimeIn.TABLE)
                        .where(TimeInContract.TimeIn._ID + "=?", id)
                        .update(db, values);
                break;
            case TIME_IN_USER:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            case TIME_IN_USER_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        getContext().getContentResolver().notifyChange(uri, null, false);
        return count;
    }
}
