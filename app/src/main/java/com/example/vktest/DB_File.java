package com.example.vktest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB_File extends SQLiteOpenHelper {
    public DB_File(Context context) {
        super(context, "hashFile.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create Table File(" +
                "name TEXT primary key," +
                "hash TEXT not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists File");
    }

    public Boolean insert(String name, byte[] hash){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put("name", name);
        contentValues.put("hash", hash);
        long result = DB.insert("File", null, contentValues);
        return result != -1;
    }
}
