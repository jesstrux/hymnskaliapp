package com.akilsw.hymns;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.util.Log;

import com.akilsw.hymns.data.HymnsContract.*;
import com.akilsw.hymns.data.HymnsDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by WAKY on 9/4/2016.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(HymnsDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new HymnsDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();

    }

    String TEST_LANGUAGE_NAME = "Swahili";
    ContentValues getLanguageContentValues(){
        String testLanguageShortCode = "sw";

        ContentValues values = new ContentValues();
        values.put(LanguageEntry.COLUMN_LANG_SHORT_CODE, testLanguageShortCode);
        values.put(LanguageEntry.COLUMN_LANG_NAME, TEST_LANGUAGE_NAME);

        return values;
    }

    ContentValues getHymnsValues(long languageRowId){
        ContentValues hymnvalues = new ContentValues();
        hymnvalues.put(HymnsEntry.COLUMN_LANG_KEY, languageRowId);
        hymnvalues.put(HymnsEntry.COLUMN_NAME, "Kwa Kalvari");
        hymnvalues.put(HymnsEntry.COLUMN_SRC, "at_calvary.mp3");

        return hymnvalues;
    }

    ContentValues getBetiValues(long hymnRowId){
        ContentValues betivalues = new ContentValues();
        betivalues.put(BetiEntry.COLUMN_HYMN_KEY, hymnRowId);
        betivalues.put(BetiEntry.COLUMN_BETI_TYPE, "chorus");
        betivalues.put(BetiEntry.COLUMN_BETI_CONTENT, "Neema bure na rehema, samaha nayo tulipewa, ndipo alipotufilia kwa kalvari.");

        return betivalues;
    }

    public void testInsertReadDb(){
        HymnsDbHelper dbHelper = new HymnsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = getLanguageContentValues();

        long languageRowid;
        languageRowid = db.insert(LanguageEntry.TABLE_NAME, null, values);

        assertTrue(languageRowid != -1);
        Log.d(LOG_TAG, "New row id: " + languageRowid);

        Cursor cursor = db.query(
                LanguageEntry.TABLE_NAME,
                null,
                null, //Columns for the "where" clause
                null, //Values for the "where" clause
                null, //columsn to group by
                null, //columns to filter by row groups
                null //sort order
        );

        if(cursor.moveToFirst()){
            validateCursor(values, cursor);

            ContentValues hymnvalues = getHymnsValues(languageRowid);
            long hymnRowId;
            hymnRowId = db.insert(HymnsEntry.TABLE_NAME, null, hymnvalues);
            assertTrue(hymnRowId != -1);

            Cursor hymnCursor = db.query(
                    HymnsEntry.TABLE_NAME,
                    null, //leaving columns field as null returns all columns
                    null, //Columns for the "where" clause
                    null, //Values for the "where" clause
                    null, //columsn to group by
                    null, //columns to filter by row groups
                    null //sort order
            );

            if(hymnCursor.moveToFirst()){
                validateCursor(hymnvalues, hymnCursor);

                ContentValues betivalues = getBetiValues(hymnRowId);
                long betiRowId;
                betiRowId = db.insert(BetiEntry.TABLE_NAME, null, betivalues);
                assertTrue(betiRowId != -1);

                Cursor betiCursor = db.query(
                        BetiEntry.TABLE_NAME,
                        null, //leaving columns field as null returns all columns
                        null, //Columns for the "where" clause
                        null, //Values for the "where" clause
                        null, //columsn to group by
                        null, //columns to filter by row groups
                        null //sort order
                );
            }else{
                fail("No betis data returned!");
            }

        }else{
            fail("No values returned :(");
        }
    }

    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor){

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry: valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);

            assertFalse(-1 == idx);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
    }
}
