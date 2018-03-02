package com.akilsw.hymns.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.akilsw.hymns.data.HymnsContract.HymnsEntry;
import com.akilsw.hymns.data.HymnsContract.LanguageEntry;
import com.akilsw.hymns.data.HymnsContract.BetiEntry;
/**
 * Created by WAKY on 9/4/2016.
 */
public class HymnsDbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 15;
    public static final String DATABASE_NAME = "test.db";


    public HymnsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LANGUAGES_TABLE =
                "CREATE TABLE " + LanguageEntry.TABLE_NAME + " ( " +
                        LanguageEntry._ID + " INTEGER PRIMARY KEY, " +

                        LanguageEntry.COLUMN_LANG_NAME + " TEXT UNIQUE NOT NULL," +

                        LanguageEntry.COLUMN_LANG_SHORT_CODE + " TEXT UNIQUE NOT NULL," +

                        " UNIQUE (" + LanguageEntry.COLUMN_LANG_SHORT_CODE + ") ON CONFLICT REPLACE" +
                        " );";

        final String SQL_CREATE_HYMNS_TABLE =
                "CREATE TABLE " + HymnsEntry.TABLE_NAME + " ( " +
                        HymnsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        HymnsEntry.COLUMN_NAME + " TEXT NOT NULL, " +

                        HymnsEntry.COLUMN_SRC + " TEXT NOT NULL, " +

                        HymnsEntry.COLUMN_AUTHOR + " VARCHAR(50) NOT NULL, " +

                        HymnsEntry.COLUMN_MELODY + " VARCHAR(50) NOT NULL, " +

                        HymnsEntry.COLUMN_LANG_KEY + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + HymnsEntry.COLUMN_LANG_KEY + ") REFERENCES " +
                        LanguageEntry.TABLE_NAME + " (" + LanguageEntry.COLUMN_LANG_SHORT_CODE + "));"
                ;

        final String SQL_CREATE_UBETI_TABLE =
                "CREATE TABLE " + BetiEntry.TABLE_NAME + " ( " +
                        BetiEntry._ID + " INTEGER PRIMARY KEY, " +

                        BetiEntry.COLUMN_BETI_NUM + " INTEGER NOT NULL ," +

                        BetiEntry.COLUMN_BETI_TYPE + " VARCHAR(15) NOT NULL ," +

                        BetiEntry.COLUMN_BETI_CONTENT + " TEXT NOT NULL, " +

                        BetiEntry.COLUMN_HYMN_KEY + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + BetiEntry.COLUMN_HYMN_KEY + ") REFERENCES " +
                        HymnsEntry.TABLE_NAME + " (" + HymnsEntry._ID + "));";
//                        +
//
//                        " UNIQUE (" + BetiEntry.COLUMN_HYMN_KEY + ") ON CONFLICT REPLACE" +
//                        " );";

        sqLiteDatabase.execSQL(SQL_CREATE_HYMNS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LANGUAGES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_UBETI_TABLE);

        Log.i("WOURA", "Tables created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ LanguageEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ HymnsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ BetiEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
