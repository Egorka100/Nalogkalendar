package ru.nalogkalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by denis on 27.11.2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context){ super(context,"CalendarDB",null,1);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       // sqLiteDatabase.execSQL("create table Calendar("+"id integer primary key autoincrement,"+"year text,"+"month text,"+"day text,"+"nametaxe text,"+"whopay text"+");");
        sqLiteDatabase.execSQL("create table calendarhtml("+"id integer primary key autoincrement,"+"date text,"+"cdata text"+");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
