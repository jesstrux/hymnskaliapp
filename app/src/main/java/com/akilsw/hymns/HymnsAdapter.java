package com.akilsw.hymns;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.github.lzyzsd.randomcolor.RandomColor;

/**
 * Created by WAKY on 9/25/2016.
 */
public class HymnsAdapter extends CursorAdapter {
    public HymnsAdapter(Context context, Cursor c, int flags) {super(context, c, flags);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.color_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String name = cursor.getString(HymnFragment.COLUMN_NAME);
        String author = cursor.getString(HymnFragment.COLUMN_AUTHOR);
        String melody = cursor.getString(HymnFragment.COLUMN_MELODY);
        String short_desc = "Author: " + author + "" + ", Melody: " + melody;

        RandomColor randomColor = new RandomColor();
        int color = randomColor.randomColor(0, RandomColor.SaturationType.RANDOM, RandomColor.Luminosity.DARK);
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Drawable bg = context.getResources().getDrawable(R.drawable.oval_shape, null);
        assert bg != null;
        bg.setTint(color);

//        Log.i("WOURA", "Post: " + );
        viewHolder.bgView.setText(String.valueOf(cursor.getPosition() + 1)+".");

        viewHolder.nameView.setText(name);
        viewHolder.colorView.setText(short_desc);
    }

    public static class ViewHolder {
        public final TextView bgView;
        public final TextView nameView;
        public final TextView colorView;

        public ViewHolder(View view){
            bgView = (TextView) view.findViewById(R.id.color_bg);
            nameView = (TextView) view.findViewById(R.id.color_name);
            colorView = (TextView) view.findViewById(R.id.color_code);
        }
    }
}

