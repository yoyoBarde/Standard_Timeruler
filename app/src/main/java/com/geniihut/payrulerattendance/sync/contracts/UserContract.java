package com.geniihut.payrulerattendance.sync.contracts;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;

import com.geniihut.payrulerattendance.sync.DBHelper;
import com.geniihut.payrulerattendance.sync.SelectionBuilder;

/**
 * Created by macmini3 on 9/16/15.
 */
public class UserContract {

    private UserContract() {
        super();
    }

    public static final String TAG = UserContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.geniihut.payrulerattendance.user";

    public static final String SCHEME = "content://";

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + CONTENT_AUTHORITY);

    public static class User implements Columns {

        public static final String TAG = UserContract.TAG + "." + User.class.getSimpleName();

        public static final String TABLE = "User";

        public static final String URI_PATH = "User";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(URI_PATH).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.geniihut.user";

        public static final String CONTENT_ID_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.geniihut.user_id";

        public static final String[] PROJECTION_ALL = {_ID, IDNO, LNAME, FNAME, MNAME, PIN, SYSTEM_USER};

        public static final String DEFAULT_SORT_ORDER = _ID + " " + DBHelper.ORDER_ASC;

        public static Uri getUri(long _id){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(_id)).build();
        }

        public static ContentValues getContentValues(com.geniihut.payrulerattendance.model.User user) {
            ContentValues values = new ContentValues();

            values.put(IDNO, user.getIdno());
            values.put(LNAME, user.getLastName());
            values.put(FNAME, user.getFirstName());
            values.put(MNAME, user.getMiddleName());
            values.put(PIN, user.getPin());
            values.put(SYSTEM_USER, user.getSystemUser());

            return values;
        }

        private static SelectionBuilder getQuerySelectionBuilder(String idno) {
            SelectionBuilder selectionBuilder = new SelectionBuilder();
            if (idno != null) {
                selectionBuilder.where(IDNO + "=?", idno);
            }
            return selectionBuilder;
        }

        public static Cursor query(Context context, Uri uri, String idno, String sortOrder) {
            SelectionBuilder selectionBuilder = new SelectionBuilder();
            if (idno != null) {
                selectionBuilder.where(IDNO + "=?", idno);
            }
            sortOrder = sortOrder == null ? DEFAULT_SORT_ORDER : sortOrder;
            return context.getContentResolver().query(uri == null ? CONTENT_URI : uri, PROJECTION_ALL, selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs(), sortOrder);
        }

        public static Uri insert(Context context, Uri uri, com.geniihut.payrulerattendance.model.User user) {
            return context.getContentResolver().insert(uri == null ? CONTENT_URI : uri, getContentValues(user));
        }

        public static int delete(Context context, Uri uri, String idno) {
            SelectionBuilder selectionBuilder = new SelectionBuilder();
            if (idno != null) {
                selectionBuilder.where(IDNO + "=?", idno);
            }
            return context.getContentResolver().delete(uri == null ? CONTENT_URI : uri, selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs());
        }

        public static int update(Context context, Uri uri, com.geniihut.payrulerattendance.model.User user) {
            SelectionBuilder selectionBuilder = new SelectionBuilder();
            if (user.getIdno() != null) {
                selectionBuilder.where(IDNO + "=?", user.getIdno());
            }
            return context.getContentResolver().update(uri == null ? CONTENT_URI : uri, getContentValues(user), selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs());
        }

        public static CursorLoader getCursorLoader(Context context, Uri uri, String idno, String sortOrder) {
            SelectionBuilder selectionBuilder = new SelectionBuilder();
            if (idno != null) {
                selectionBuilder.where(IDNO + "=?", idno);
            }
            sortOrder = sortOrder == null ? DEFAULT_SORT_ORDER : sortOrder;
            return new CursorLoader(context, uri == null ? CONTENT_URI : uri, PROJECTION_ALL, selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs(), sortOrder);
        }

        public static com.geniihut.payrulerattendance.model.User getUser(Cursor cursor) {
            com.geniihut.payrulerattendance.model.User user = new com.geniihut.payrulerattendance.model.User();

            user.set_id(cursor.getLong(cursor.getColumnIndex(_ID)));
            user.setIdno(cursor.getString(cursor.getColumnIndex(IDNO)));
            user.setFirstName(cursor.getString(cursor.getColumnIndex(FNAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndex(LNAME)));
            user.setMiddleName(cursor.getString(cursor.getColumnIndex(MNAME)));
            user.setPin(cursor.getString(cursor.getColumnIndex(PIN)));
            user.setSystemUser(cursor.getString(cursor.getColumnIndex(SYSTEM_USER)));

            return user;
        }

        public static com.geniihut.payrulerattendance.model.User getUser(Context context, String idno) {
            Cursor cursor = query(context, null, idno, null);

            return cursor != null && cursor.moveToFirst() ? getUser(cursor) : null;
        }
    }

    public interface Columns extends BaseColumns {

        /*
        All field members declared in an interface are by default public, static and final
        */

        String IDNO = "idno";
        String LNAME = "lname";
        String FNAME = "fname";
        String MNAME = "mname";
        String PIN = "pin";
        String SYSTEM_USER = "system_user";
    }
}
