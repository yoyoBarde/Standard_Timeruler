package com.geniihut.payrulerattendance.sync

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.nfc.Tag
import android.util.Log

import com.geniihut.payrulerattendance.model.LoginData
import com.geniihut.payrulerattendance.model.OfflineData
import com.geniihut.payrulerattendance.model.User

import java.io.ByteArrayOutputStream
import java.util.ArrayList

class OfflineDBHELPER(context: Context) : SQLiteOpenHelper(context, TABLE_NAME, null, 1) {

    val data: List<OfflineData>
        get() {
            val OfflineList = ArrayList<OfflineData>()
            val db = this.readableDatabase
            val query = "SELECT * FROM $TABLE_NAME"
            val data = db.rawQuery(query, null)

            if (data.moveToFirst()) {
                do {

                    val myOfflineData = OfflineData("", " ", " ", " ", " ", null)
                    myOfflineData.userID = data.getString(1)
                    myOfflineData.userPassword = data.getString(2)
                    myOfflineData.loginTime = data.getString(3)
                    myOfflineData.timeInOrOut = data.getString(4)
                    myOfflineData.image = getImage(data.getBlob(6))
                    myOfflineData.loginDate = data.getString(7)

                    OfflineList.add(myOfflineData)

                } while (data.moveToNext())
            }

            return OfflineList
        }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {


        val CREATE_TABLE_OFFLINE_DATA = ("CREATE TABLE " + TABLE_NAME + "("
                + COL1 + " INTEGER PRIMARY KEY  ,"
                + COL2 + " TEXT, "
                + COL3 + " TEXT, "
                + COL4 + " TEXT, "
                + COL5 + " TEXT, "
                + COL6 + " TEXT, "
                + COL7 + " TEXT, "
                + COL8 + " TEXT )")

        sqLiteDatabase.execSQL(CREATE_TABLE_OFFLINE_DATA)
        Log.e(TAG, "onCREATE")

    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS $TABLE_NAME")
        onCreate(sqLiteDatabase)


    }

    fun addOFFlineDATA(offlineData: OfflineData): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL2, offlineData.userID)
        contentValues.put(COL3, offlineData.userPassword)
        contentValues.put(COL4, offlineData.loginTime)
        contentValues.put(COL5, offlineData.timeInOrOut)
        contentValues.put(COL6, getBytes(offlineData.image))
        contentValues.put(COL7, offlineData.loginDate)
        val result = db.insert(TABLE_NAME, null, contentValues)

        return if (result == -1) {
            false
        } else {
            true
        }

    }

    fun dropTable() {
        val db = this.writableDatabase
        val query = "DROP TABLE $TABLE_NAME"
        db.execSQL(query)
    }

    fun dropTable2() {
        val db = this.writableDatabase
        val query = "DROP TABLE $TABLE_NAME2"
        db.execSQL(query)
    }


    fun deleteName() {
        val db = this.writableDatabase
        val query = "DROP"


    }

    companion object {
        private val COL1 = "ID"
        private val COL2 = "userName"
        private val COL3 = "userPassword"
        private val COL4 = "dateTime"
        private val COL5 = "timeInOrOut"
        private val COL6 = "image"

        private val TABLE_NAME = "OFFLINEDATATABLE"
        private val COL7 = "loginDate"
        private val TAG = "OfflineDBHELPER"
        private val COL8 = "gago"


        private val COL11 = "USERNAME"
        private val COL22 = "PASSWORD"
        private val TABLE_NAME2 = "LOGINTABLE"
        fun getBytes(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
            return stream.toByteArray()
        }

        fun getImage(image: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(image, 0, image.size)
        }
    }


}
