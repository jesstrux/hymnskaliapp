package com.akilsw.hymns;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by WAKY on 2/19/2017.
 */
public class BetiItem {
    String content;
    String type;

    public BetiItem(String content, String type) {
        this.setContent(content);
        this.setType(type);
    }

    public BetiItem() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static BetiItem fromCursor(Cursor cursor) {
        return new BetiItem(cursor.getString(1), cursor.getString(2));
    }
}
