package com.geniihut.payrulerattendance.sync.contracts;


import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;

import com.geniihut.payrulerattendance.helpers.AppUtils;
import com.geniihut.payrulerattendance.sync.DBHelper;
import com.geniihut.payrulerattendance.sync.SelectionBuilder;

/**
 * Created by macmini3 on 9/16/15.
 */
public class TimeInContract {

    public TimeInContract() {
    }

    public static final String TAG = TimeInContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.geniihut.payrulerattendance.timein";

    public static final String SCHEME = "content://";

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + CONTENT_AUTHORITY);

    public static class TimeIn implements Columns {
        public static final String TAG = TimeInContract.TAG + "." + TimeIn.class.getSimpleName();

        public static final String TABLE = "TimeIn";

        public static final String URI_PATH = "TimeIn";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(URI_PATH).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.geniihut.time_in";

        public static final String CONTENT_ID_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.geniihut.time_in_id";

        public static final String[] PROJECTION_ALL = {_ID, IDNO, DATE, TIME, INOUT, LATITUDE, LONGITUDE, DATE_TIME, IMAGE};

        public static final String DEFAULT_SORT_ORDER = _ID + " " + DBHelper.ORDER_DESC;
        public static final String SYNC_SORT_ORDER = _ID + " " + DBHelper.ORDER_ASC;

        public static final String EXTRAS_IDNO = "extras_idno";
        public static final String EXTRAS_FILTER = "extras_filter";

        public static Uri getUri(long _id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(_id)).build();
        }

        public static ContentValues getContentValues(com.geniihut.payrulerattendance.model.TimeIn timeIn) {
            ContentValues values = new ContentValues();

            values.put(IDNO, timeIn.getIdno());
            values.put(DATE, timeIn.getDate());
            values.put(TIME, timeIn.getTime());
            values.put(INOUT, timeIn.getInout());
            values.put(LATITUDE, timeIn.getLatitude());
            values.put(LONGITUDE, timeIn.getLongitude());
            values.put(DATE_TIME, timeIn.getDateTime());
            if(timeIn.getImage() != null)
            values.put(IMAGE, AppUtils.getBitmapAsByteArray(timeIn.getImage()));

            return values;
        }

        public static Cursor query(Context context, Uri uri, String idno, String filter, String sortOrder) {
            SelectionBuilder selectionBuilder = new SelectionBuilder();
            if (idno != null) {
                selectionBuilder.where(IDNO + "=?", idno);
            }
            if (filter != null && !filter.equalsIgnoreCase("ALL")) {
                selectionBuilder.where(INOUT + "=?", filter);
            }
            sortOrder = sortOrder == null ? DEFAULT_SORT_ORDER : sortOrder;
            return context.getContentResolver().query(uri == null ? CONTENT_URI : uri, PROJECTION_ALL, selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs(), sortOrder);
        }

        public static Uri insert(Context context, Uri uri, com.geniihut.payrulerattendance.model.TimeIn timeIn) {
            return context.getContentResolver().insert(uri == null ? CONTENT_URI : uri, getContentValues(timeIn));
        }

        public static int delete(Context context, Uri uri, String idno) {
            SelectionBuilder selectionBuilder = new SelectionBuilder();
            if (idno != null) {
                selectionBuilder.where(IDNO + "=?", idno);
            }
            return context.getContentResolver().delete(uri == null ? CONTENT_URI : uri, selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs());
        }

        public static ContentProviderOperation newDelete(Uri uri, long _id) {
            Uri newUri = TimeIn.CONTENT_URI.buildUpon().appendPath(Long.toString(_id)).build();
            return ContentProviderOperation.newDelete(uri == null ? newUri : uri).build();
        }

        public static CursorLoader getCursorLoader(Context context, Uri uri, String idno, String filter, String sortOrder) {
            SelectionBuilder selectionBuilder = new SelectionBuilder();
            if (idno != null) {
                selectionBuilder.where(IDNO + "=?", idno);
            }
            if (filter != null && !filter.equalsIgnoreCase("ALL")) {
                selectionBuilder.where(INOUT + "=?", filter);
            }
            sortOrder = sortOrder == null ? DEFAULT_SORT_ORDER : sortOrder;
            return new CursorLoader(context, uri == null ? CONTENT_URI : uri, PROJECTION_ALL, selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs(), sortOrder);
        }

        public static com.geniihut.payrulerattendance.model.TimeIn getTimeIn(Cursor cursor) {
            com.geniihut.payrulerattendance.model.TimeIn timeIn = new com.geniihut.payrulerattendance.model.TimeIn();

            timeIn.set_id(cursor.getLong(cursor.getColumnIndex(_ID)));
            timeIn.setIdno(cursor.getString(cursor.getColumnIndex(IDNO)));
            timeIn.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
            timeIn.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
            timeIn.setInout(cursor.getString(cursor.getColumnIndex(INOUT)));
            timeIn.setLatitude(cursor.getString(cursor.getColumnIndex(LATITUDE)));
            timeIn.setLongitude(cursor.getString(cursor.getColumnIndex(LONGITUDE)));
            timeIn.setDateTime(cursor.getString(cursor.getColumnIndex(DATE_TIME)));
            if(cursor.getBlob(cursor.getColumnIndex(IMAGE)) != null)
                timeIn.setImage(AppUtils.getBitmapFromByteArray(cursor.getBlob(cursor.getColumnIndex(IMAGE))));

            return timeIn;
        }
    }

    /**
     * Constants for a joined view of TimeIn and User. The _id of this
     * joined view is the _id of the TimeIn table.
     */
    public static class TimeInUser implements Columns, UserContract.Columns {
        public static final String TAG = TimeInUser.TAG + "." + TimeIn.class.getSimpleName();

        public static final String TIME_IN_ID = TimeIn.TABLE + "." + _ID;

        public static final String TIME_IN_IDNO = TimeIn.TABLE + "." + Columns.IDNO;

        public static final String USER_IDNO = UserContract.User.TABLE + "." + UserContract.Columns.IDNO;

        public static final String TABLE = TimeIn.TABLE + " LEFT JOIN " + UserContract.User.TABLE +
                " ON(" + TIME_IN_IDNO + " = "  + USER_IDNO + ")";

        public static final String URI_PATH = "TimeInUser";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(URI_PATH).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.geniihut.time_in_user";

        public static final String CONTENT_ID_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.geniihut.time_in_user_id";

        public static final String[] PROJECTION_ALL = {TIME_IN_ID, TIME_IN_IDNO, DATE, TIME, INOUT, LATITUDE, LONGITUDE, DATE_TIME, IMAGE, LNAME, FNAME, MNAME, PIN, SYSTEM_USER};

        public static final String DEFAULT_SORT_ORDER = TIME_IN_ID + " " + DBHelper.ORDER_ASC;
    }

    public interface Columns extends BaseColumns {

        /*
        All field members declared in an interface are by default public, static and final
        */

        String IDNO = "idno";//used as foreign key
        String DATE = "date";
        String TIME = "time";
        String INOUT = "inout";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String DATE_TIME = "dateTime";
        String IMAGE = "image";
    }
}
