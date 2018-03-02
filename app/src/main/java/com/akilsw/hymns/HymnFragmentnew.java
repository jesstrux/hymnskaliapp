//package com.akilsw.hymns;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.v4.app.Fragment;
//import android.support.v4.widget.SimpleCursorAdapter;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URI;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class HymnFragment extends Fragment {
//    private SimpleCursorAdapter mHymnsAdapter;
//
//    private String[] getHymnsListFromJson(String json)
//            throws JSONException {
//        Log.v(LOG_TAG, "Reached here!");
//        JSONArray hymnsArray = new JSONArray(json);
//
//        String[] resultStrs = new String[hymnsArray.length()];
//        for(int i = 0; i < hymnsArray.length(); i++) {
//            JSONObject song = hymnsArray.getJSONObject(i);
//            resultStrs[i] = song.getString("name");
////                resultStrs[i] = hymnsArray.getString(i);
//        }
//
//        for (String s: resultStrs) {
//            Log.v(LOG_TAG, s);
//        }
//        return resultStrs;
//    }
//    /**
//     * Mandatory empty constructor for the fragment manager to instantiate the
//     * fragment (e.g. upon screen orientation changes).
//     */
//    public HymnFragment() {
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.list_fragment_menu, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.list_fragment_menu){
//            loadData();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.hymn_list_fragment, container, false);
//
//        List<String> hymnsList = new ArrayList<String>(new ArrayList<String>());
//
////        mHymnsAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_hymn, R.id.list_item_hymn_texview, hymnsList);
//
//        mHymnsAdapter = new SimpleCursorAdapter(
//                getActivity(),
//                R.layout.list_item_hymn,
//                null,
//                new String[]{"Song name"},
//                new int[]{R.id.list_item_hymn_texview},
//                0);
//
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_hymn);
//        if(listView != null){
////            listView.setAdapter(mHymnsAdapter);
////            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////                @Override
////                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                    String hymn = mHymnsAdapter.getItem(i);
////                    Intent intent = new Intent(getActivity(), HymnsDetailActivity.class)
////                            .putExtra(Intent.EXTRA_TEXT, hymn);
////                    startActivity(intent);
////                }
////            });
//        }
//
//        return rootView;
//    }
//
//    private void loadData(){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String lang = prefs.getString(getString(R.string.pref_language_key), getString(R.string.pref_language_default));
////        String ismus = prefs.getString(getString(R.string.pref_is_musician_key), "false");
//
//        FetchTask fetchHymnsTask = new FetchTask();
//        fetchHymnsTask.execute(lang, "False");
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        loadData();
//    }
//}
