package com.geniihut.payrulerattendance.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.support.annotation.Nullable;

import com.geniihut.payrulerattendance.model.OfflineData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class OfflineDBHELPER extends SQLiteOpenHelper {
    private static final String COL1 = "ID";
    private static final String COL2 = "userName";
    private static final String COL3 = "userPassword";
    private static final String COL4 = "dateTime";
    private static final String COL5 = "timeInOrOut";
    private static final String COL6 = "image";

    private static final String TABLE_NAME = "OFFLINEDATATABLE";
    private static final String COL7 ="loginDate" ;

    public OfflineDBHELPER(Context context) {
        super(context, TABLE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_OFFLINE_DATA = "CREATE TABLE " + TABLE_NAME + "("
                + COL1 + " INTEGER PRIMARY KEY  ,"
                + COL2 + " TEXT, "
                + COL3 + " TEXT, "
                + COL4 + " TEXT, "
                + COL5 + " TEXT,"
                + COL6 + " TEXT,"
                + COL7 + " TEXT )";

        sqLiteDatabase.execSQL(CREATE_TABLE_OFFLINE_DATA);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);


    }
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public boolean addOFFlineDATA(OfflineData offlineData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL2, offlineData.getUserID());
        contentValues.put(COL3, offlineData.getUserPassword());
        contentValues.put(COL4, offlineData.getLoginTime());
        contentValues.put(COL5, offlineData.getTimeInOrOut());
        contentValues.put(COL6, getBytes(offlineData.getImage()));
        contentValues.put(COL7, offlineData.getLoginDate());
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public List<OfflineData> getData() {
        List<OfflineData> OfflineList = new ArrayList<OfflineData>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);

        if (data.moveToFirst()) {
            do {

                OfflineData myOfflineData = new OfflineData(""," ", " ", " ", " ", null);
                myOfflineData.setUserID(data.getString(1));
                myOfflineData.setUserPassword(data.getString(2));
                myOfflineData.setLoginTime(data.getString(3));
                myOfflineData.setTimeInOrOut(data.getString(4));
                myOfflineData.setImage(getImage(data.getBlob(6)));
                myOfflineData.setLoginDate(data.getString(7));
                OfflineList.add(myOfflineData);

            } while (data.moveToNext());
        }

        return OfflineList;
    }


    public void deleteName() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP";


    }


}
