package com.akilsw.hymns;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.github.lzyzsd.randomcolor.RandomColor;

/**
 * Created by WAKY on 9/25/2016.
 */
public class BetiAdapter extends CursorAdapter {
    public BetiAdapter(Context context, Cursor c, int flags) {super(context, c, flags);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        String type = cursor.getString(HymnsDetailActivity.COLUMN_TYPE);
        View view;

        if(type.equals("chorus")){
            view = LayoutInflater.from(context).inflate(R.layout.beti_item_chorus, viewGroup, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.beti_item, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String content = cursor.getString(HymnsDetailActivity.COLUMN_CONTENT);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.contentView.setText(content);
    }

    public static class ViewHolder {
        public final TextView contentView;

        public ViewHolder(View view){
            contentView = (TextView) view.findViewById(R.id.beti_content);
        }
    }
}

