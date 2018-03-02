package com.akilsw.hymns;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class LyricsAdapter extends CursorRecyclerViewAdapter<LyricsAdapter.ViewHolder> {
    Context mContext;
    Cursor mCursor;
    BetiItem mBetiItem;

    public LyricsAdapter(Context context, Cursor cursor){
        super(context,cursor);
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public int getItemViewType(int position) {
        mCursor.moveToPosition(position);
        mBetiItem = BetiItem.fromCursor(mCursor);
        String type = mBetiItem.getType();

        if(type.equals("chorus"))
            return 1;
        else
            return  0;
    }

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback{
        void onAddClick(int p);
        void onMinusClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beti_item, parent, false);

        switch (viewType){
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.beti_item, parent, false);
                break;
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.beti_item_chorus, parent, false);
                break;
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        mCursor.moveToPosition(cursor.getPosition());
        mBetiItem = BetiItem.fromCursor(mCursor);
        viewHolder.mTextView.setText(mBetiItem.getContent());
//        Log.i("WOURA", "Hello from the lyrics adapter..." + cursor.getPosition());
//        if(mBetiItem.getType().equals("chorus"))
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.beti_content);
        }
    }
}
