package com.akilsw.hymns.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by WAKY on 9/4/2016.
 */
public class HymnsProvider extends ContentProvider{
    private HymnsDbHelper hymnsDbHelper;
    private static final SQLiteQueryBuilder sHymnsByLanguageQueryBuilder;
    public static final int HYMN = 100;
    public static final int HYMN_ID = 101;
    public static final int BETI = 300;
    public static final int BETI_ID = 301;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    private HymnsDbHelper mOpenHelper;

    static {
        sHymnsByLanguageQueryBuilder = new SQLiteQueryBuilder();
        sHymnsByLanguageQueryBuilder.setTables(
                HymnsContract.HymnsEntry.TABLE_NAME + " INNER JOIN " +
                        HymnsContract.LanguageEntry.TABLE_NAME +
                        " ON " + HymnsContract.HymnsEntry.TABLE_NAME +
                        "." + HymnsContract.HymnsEntry.COLUMN_LANG_KEY +
                        " = " + HymnsContract.LanguageEntry.TABLE_NAME +
                        "." + HymnsContract.LanguageEntry._ID);
    }

    private final String sLanguageSettingSelection =
            HymnsContract.LanguageEntry.TABLE_NAME+
                    "." + HymnsContract.LanguageEntry.COLUMN_LANG_SHORT_CODE + " = ? ";

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HymnsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, HymnsContract.PATH_BETI, BETI);
        matcher.addURI(authority, HymnsContract.PATH_BETI + "/#", BETI_ID);

        matcher.addURI(authority, HymnsContract.PATH_HYMNS, HYMN);
        matcher.addURI(authority, HymnsContract.PATH_HYMNS + "/#", HYMN_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new HymnsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)){
            case HYMN_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HymnsContract.HymnsEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder
                );
                break;
            }
            case HYMN:{
                retCursor = null;
                break;
            }
            case BETI_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HymnsContract.HymnsEntry.TABLE_NAME,
                        projection, HymnsContract.HymnsEntry._ID + " = '" + ContentUris.parseId(uri) + "'", selectionArgs, null, null, sortOrder
                );
                break;
            }
            case BETI:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HymnsContract.HymnsEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case HYMN_ID:
                return HymnsContract.HymnsEntry.CONTENT_ITEM_TYPE;
            case HYMN:
                return HymnsContract.HymnsEntry.CONTENT_TYPE;
            case BETI_ID:
                return HymnsContract.BetiEntry.CONTENT_ITEM_TYPE;
            case BETI:
                return HymnsContract.BetiEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor;
        Uri retUri = null;

        switch (sUriMatcher.match(uri)){
            case HYMN:{
                long _id =  mOpenHelper.getReadableDatabase().insert(HymnsContract.HymnsEntry.TABLE_NAME, null, contentValues);

                if (_id > 0)
                    retUri = HymnsContract.HymnsEntry.buildHymnUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            case BETI:{
                long _id =  mOpenHelper.getReadableDatabase().insert(HymnsContract.BetiEntry.TABLE_NAME, null, contentValues);

                if (_id > 0)
                    retUri = HymnsContract.BetiEntry.buildBetiUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return retUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
