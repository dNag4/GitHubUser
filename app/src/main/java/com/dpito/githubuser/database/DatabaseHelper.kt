package com.dpito.githubuser.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dpito.githubuser.database.DatabaseHelper.Companion.TABLE_NAME

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){


    @Throws(SQLException::class)
    fun open() {
        this.writableDatabase
    }

    override fun close() {
        val db = this.writableDatabase
        db.close()

        if (db.isOpen)
            db.close()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NAME (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    fun insertData(username: String): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, username)
        val success = db.insert(TABLE_NAME, null, contentValues)
        //db.execSQL("INSERT INTO $TABLE_NAME ($COL_2) VALUES ('$username')")
        return success
    }

    fun deleteData(username: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COL_2 = ?", arrayOf(username))
    }

    fun queryByUsername(username: String): Cursor {
        val db = this.writableDatabase
        return db.query(TABLE_NAME, null, "$COL_2 = ?", arrayOf(username), null, null, null, null)
    }

    fun getAllFav() : ArrayList<Any>{
        val ulist : ArrayList<Any> = ArrayList()
        val selectQuery = "SELECT username FROM $TABLE_NAME ORDER BY id"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if(cursor.moveToFirst()){
            do{
                ulist.add(cursor.getString(0))
            }while (cursor.moveToNext())
        }

        return ulist
    }

    companion object{
        private const val DATABASE_NAME = "gitty"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "userfav"
        private const val COL_2 = "username"
    }
}