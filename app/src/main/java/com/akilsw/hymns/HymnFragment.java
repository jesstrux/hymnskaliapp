package com.akilsw.hymns;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.akilsw.hymns.data.FetchTask;
import com.akilsw.hymns.data.HymnsContract.*;
import com.akilsw.hymns.data.HymnsDbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HymnFragment extends Fragment {
    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;
    HymnsAdapter mHymnsAdapter;
    public static int COLUMN_ID = 0;
    public static int COLUMN_NAME = 1;
    public static int COLUMN_AUTHOR = 2;
    public static int COLUMN_MELODY = 3;
    public static int COLUMN_SRC = 4;

    SharedPreferences prefs;
    String lang;
//        String ismus = prefs.getString(getString(R.string.pref_is_musician_key), "false");

    public HymnFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dbhelper = new HymnsDbHelper(getContext());
        database = dbhelper.getWritableDatabase();

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lang = prefs.getString(getString(R.string.pref_language_key), getString(R.string.pref_language_default));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hymn_list_fragment, container, false);

        mHymnsAdapter = new HymnsAdapter(getActivity().getBaseContext(), null, 0);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_hymn);

        fetchHymns();

        if(listView != null){
            listView.setAdapter(mHymnsAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    HymnsAdapter adpter = (HymnsAdapter) adapterView.getAdapter();
                    Cursor cursor = adpter.getCursor();
                    if(null != cursor && cursor.moveToPosition(position)){
                        String[] song= {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)};
                        Intent intent = new Intent(getContext(), HymnsDetailActivity.class);
                        Bundle b = toBundle(song);
                        Log.i("WOURA", "Error: "+ b.getString("src"));

                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
            });
        }

        return rootView;
    }

    public Bundle toBundle(String[] args) {
        Bundle b = new Bundle();
        b.putInt("id", Integer.parseInt(args[COLUMN_ID]));
        b.putString("name", args[COLUMN_NAME]);
        b.putString("author", args[COLUMN_AUTHOR]);
        b.putString("melody", args[COLUMN_MELODY]);
        b.putString("src", args[COLUMN_SRC]);

        return b;
    }

    private Boolean queryHymns(){
        Cursor newc = database.query(
                HymnsEntry.TABLE_NAME,
                new String[] { "_ID AS _id",
                        HymnsEntry.COLUMN_NAME,
                        HymnsEntry.COLUMN_AUTHOR,
                        HymnsEntry.COLUMN_MELODY,
                        HymnsEntry.COLUMN_SRC
                },
                HymnsEntry.COLUMN_LANG_KEY + " = '" + lang + "'", null, null, null, null
        );
        mHymnsAdapter.swapCursor(newc);

        return newc.getCount() > 0;
    }

    private void fetchHymns() {

        if(queryHymns()){
            Log.i("WOURA", "Found some hymns, populating them.");
            return;
        }

        Log.i("WOURA", "Found nothing, doing the fetch.");


//        Uri uri = Uri.parse(Constants.BASE_URL).buildUpon()
//                .appendQueryParameter("lang", lang)
//                .appendQueryParameter("is_musical", "false")
//                .build();

        String base_url = Constants.BASE_URL + "hymns_"+lang+"_not_musical_long.json";
        Uri uri = Uri.parse(base_url);

        FetchTask fetchTask = new FetchTask();
        String json = null;

        try {
            json = fetchTask.execute(uri.toString()).get();
            JSONArray hymns = new JSONArray(json);
            Log.i("WOURA", "Fetched: " + hymns.length() + " hymns");

            for (int i = 0; i < hymns.length(); i++) {
                JSONObject hymn = hymns.getJSONObject(i);
                Log.i("WOURA", "Result " + i + ": " + hymn);
                String name = hymn.getString("title");
                String author = hymn.getString("author");
                String composer = hymn.getString("composer");
                String src = hymn.getString("src");

                long newHymn;
                ContentValues values = new ContentValues();
                values.put(HymnsEntry.COLUMN_NAME, name);
                values.put(HymnsEntry.COLUMN_AUTHOR, author);
                values.put(HymnsEntry.COLUMN_MELODY, composer);
                values.put(HymnsEntry.COLUMN_SRC, src);
                values.put(HymnsEntry.COLUMN_LANG_KEY, lang);
                newHymn = database.insert(HymnsEntry.TABLE_NAME, null, values);

                if (newHymn != -1){
                    Log.i("WOURA", " Inserted hymn " + newHymn);
                    JSONArray beti = hymn.getJSONArray("lyrics");

                    for (int j = 0; j < beti.length(); j++){
                        JSONObject ubeti = beti.getJSONObject(j);
                        String type = ubeti.getString("type");
                        String content = ubeti.getString("content");

                        long newUbeti;
                        ContentValues ubetiValues = new ContentValues();
                        ubetiValues.put(BetiEntry.COLUMN_BETI_NUM, j+1);
                        ubetiValues.put(BetiEntry.COLUMN_BETI_CONTENT, content);
                        ubetiValues.put(BetiEntry.COLUMN_BETI_TYPE, type);
                        ubetiValues.put(BetiEntry.COLUMN_HYMN_KEY, newHymn);
                        newUbeti = database.insert(BetiEntry.TABLE_NAME, null, ubetiValues);

                        if(newUbeti != -1)
                            Log.i("WOURA", " Inserted ubeti " + newUbeti + "type: " + type + " hymn: " + newHymn);
                        else
                            Log.i("WOURA", " Failed to insert ubeti " + newUbeti);
                    }
                }
                else
                    Log.i("WOURA", "Hymn insertion failed");
            }
        } catch (Exception e) {
//            e.printStackTrace();
            Log.i("WOURA", "Something went wrong" + e.getMessage());
        }
    }
}
