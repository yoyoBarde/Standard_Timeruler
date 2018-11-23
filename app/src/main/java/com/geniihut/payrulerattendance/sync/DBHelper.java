package com.geniihut.payrulerattendance.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.geniihut.payrulerattendance.model.TimeIn;
import com.geniihut.payrulerattendance.model.User;
import com.geniihut.payrulerattendance.sync.contracts.TimeInContract;
import com.geniihut.payrulerattendance.sync.contracts.UserContract;

import java.io.ByteArrayOutputStream;
import java.net.IDN;

/**
 * Created by macmini4 on 8/12/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 4;
    // Database Name
    private static final String DATABASE_NAME = "crud.db";
    public static final String ORDER_ASC = "ASC";
    public static final String ORDER_DESC = "DESC";
    private static final String TABLE_NAME ="OFFLINEDATA" ;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        String CREATE_TABLE_USER = "CREATE TABLE " + UserContract.User.TABLE  + "("
                + User._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + User.IDNO + " TEXT, "
                + User.FNAME + " TEXT, "
                + User.MNAME + " TEXT, "
                + User.LNAME + " TEXT, "
                + User.PIN + " TEXT, "
                + User.SYSTEM_USER + " TEXT )";
//                + User.EMAIL + " TEXT, "
//                + User.BIRTHDATE + " TEXT, "
//                + User.AGE + " TEXT )";

        String CREATE_TABLE_TIMEIN = "CREATE TABLE " + TimeInContract.TimeIn.TABLE  + "("
                + TimeIn._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + TimeIn.IDNO + " TEXT, "
                + TimeIn.DATE + " TEXT, "
                + TimeIn.TIME + " TEXT, "
                + TimeIn.INOUT + " TEXT, "
                + TimeIn.LATITUDE + " TEXT, "
                + TimeIn.LONGITUDE + " TEXT, "
                + TimeIn.DATE_TIME +" TEXT, "
                + TimeIn.IMAGE +" BLOB )";





        Log.e("DBHelper", "onCreate \n" + CREATE_TABLE_USER + "\n" + CREATE_TABLE_TIMEIN);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_TIMEIN);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
//        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE);

        // Create tables again
        onCreate(db);

    }


}
