package com.akilsw.hymns.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import android.net.Uri;
/**
 * Created by WAKY on 9/4/2016.
 */
public class HymnsContract {
    public static final String CONTENT_AUTHORITY = "com.akilsw.hymns";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LANGUAGE = "language";
    public static final String PATH_HYMNS = "hymns";
    public static final String PATH_BETI = "beti";

    public static final class LanguageEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LANGUAGE).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LANGUAGE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LANGUAGE;

        public static final String TABLE_NAME = "languages";
        public static final String COLUMN_LANG_NAME = "name";
        public static final String COLUMN_LANG_SHORT_CODE = "short_code";

        public static Uri buildLanguageUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class HymnsEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_HYMNS).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_HYMNS;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_HYMNS;

        public static final String TABLE_NAME = "hymns";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_MELODY = "melody";
        public static final String COLUMN_SRC = "src";
        public static final String COLUMN_LANG_KEY = "lang_code";

        public static Uri buildHymnUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class BetiEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BETI).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_BETI;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_BETI;

        public static final String TABLE_NAME = "beti";

        public static final String COLUMN_BETI_NUM = "beti_num";
        public static final String COLUMN_BETI_TYPE = "type";
        public static final String COLUMN_BETI_CONTENT = "content";
        public static final String COLUMN_HYMN_KEY = "hymn_id";

        public static Uri buildBetiUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
