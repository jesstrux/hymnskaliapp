package com.akilsw.hymns;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ListView;
import android.widget.Toast;

import com.akilsw.hymns.data.HymnsContract.BetiEntry;
import com.akilsw.hymns.data.HymnsDbHelper;

public class HymnsDetailActivity extends AppCompatActivity {
    private static final int PERMISSIONS_FILE_EXISTS = 7;
    /*
    * FEATURES
    * 1. Click verse to share
    * 2.
    * */

    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;
    private final int STATE_NOT_FOUND = 0;
    private final int STATE_FOUND_PAUSED = 1;
    private final int STATE_PLAYING = 2;
    private final int STATE_DOWNLOADING = 3;

    private static final int PERMISSIONS_INTERNET = 0;
    private static final int PERMISSIONS_EXTENAL_SD = 1;
    private static final int PERMISSIONS_EXTENAL_SD_READ = 2;
    private LyricsAdapter mBetiAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Cursor mLyricsCursor;

    public static int COLUMN_ID = 0;
    public static int COLUMN_CONTENT = 1;
    public static int COLUMN_TYPE = 2;

    private int audio_state = 0;
    private String base_url = Constants.SRC_URL;
    private String http_url;
    private Uri audio_uri;
    private String item_src;
    private Boolean fileExists = false;
    private Boolean isPlaying=false;
    String song;
    int song_id;

    ProgressDialog progressDialog;
    MediaPlayer mediaPlayer;
    FloatingActionButton fab;
    private Menu menu;

    String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //create app folder
        progressDialog = new ProgressDialog(this);
        mediaPlayer = new MediaPlayer();

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        song_id = args.getInt("id");
        song = args.getString("name");
        item_src = args.getString("src");
        http_url = base_url + item_src;

        if(hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Log.i("WOURA", "Hapa mwanzao kabisa!");
            Log.e("Creating app folder",String.valueOf(new FileUtils(this).createFolder()));
//            fileExists = new FileUtils(this).checkFile(item_src);

            if(fileExists){
                setAudioState(1);
            }else{
                setAudioState(0);
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.INTERNET)) {
                    Toast.makeText(HymnsDetailActivity.this, "Internet permission required", Toast.LENGTH_SHORT).show();

                } else {
                    ActivityCompat.requestPermissions(HymnsDetailActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_FILE_EXISTS);
                }
            }
        }
        //check file and set correct resource

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(song);
        }

        dbhelper = new HymnsDbHelper(this);
        database = dbhelper.getWritableDatabase();
        mLyricsCursor = queryLyrics();

//        Log.i("WOURA", "Found beti: "+ mLyricsCursor.moveToFirst());
        mBetiAdapter = new LyricsAdapter(this, mLyricsCursor);

        mRecyclerView = (RecyclerView) findViewById(R.id.betiList);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mBetiAdapter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopSong();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSong();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.menu_hymns_detail, menu);

        setFabSrc();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }else if(id == R.id.action_play_melody){
//            Toast.makeText(this, "You click play", Toast.LENGTH_SHORT).show();
            if(audio_state == 0){
                downloadAudio();
                IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                registerReceiver(downloadReceiver, filter);
            }
            else if(audio_state == 1 || audio_state == 2){
                togglePlayback(item_src);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Cursor queryLyrics(){
        Cursor newc = database.query(
                BetiEntry.TABLE_NAME,
                new String[] { "_ID AS _id",
                        BetiEntry.COLUMN_BETI_CONTENT,
                        BetiEntry.COLUMN_BETI_TYPE
                },
                BetiEntry.COLUMN_HYMN_KEY + " = '" + song_id + "'", null, null, null, null
        );
        return newc;

//        Cursor c = database.rawQuery("SELECT * FROM beti WHERE " + BetiEntry.COLUMN_HYMN_KEY + " = '" + song_id + "'", null);
//        return c;
    }

    private void setAudioState(int state){
//      0 not found, 1 found / paused, 2 playing
        audio_state = state;
        setFabSrc();
    }

    private void setFabSrc(){
//      0 not found, 1 found / paused, 2 playing, 3 downloading
        int icon;
        if(audio_state == 1)
            icon = R.drawable.ic_play_circle;
        else if(audio_state == 2)
            icon = R.drawable.ic_stop;
        else if(audio_state == 3)
            icon = R.drawable.ic_download;
        else
            icon = R.drawable.ic_audio;

        if(menu != null){
//            menu.getItem(0) != null
            menu.getItem(0).setIcon(getResources().getDrawable(icon, null));
        }
    }

    private void downloadAudio(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!hasPermissions(this, PERMISSIONS)){
                cueDownload();
            }else{
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.INTERNET)) {
                    Toast.makeText(HymnsDetailActivity.this, "Internet permission required", Toast.LENGTH_SHORT).show();

                } else {
                    ActivityCompat.requestPermissions(HymnsDetailActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_EXTENAL_SD);
                }
            }
        }else
            cueDownload();

    }

    private void cueDownload(){
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        Long Audio_DownloadId = DownloadData (Uri.parse(http_url));


        DownloadManager.Query AudioDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        AudioDownloadQuery.setFilterById(Audio_DownloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(AudioDownloadQuery);
        if(cursor.moveToFirst()){
            DownloadStatus(cursor, Audio_DownloadId);
        }
    }

    void togglePlayback(String song){
        if(audio_state == 1){
            playSong(song);
            setAudioState(2);
        }
        else if(audio_state == 2){
            stopSong();
            setAudioState(1);
        }
    }

    void playSong(String song){
//        Log.i("WOURA", "Path: "+ new FileUtils(this).getFilePath(song));
        try {
            if(mediaPlayer!=null){
                mediaPlayer.setDataSource(new FileUtils(this).getFilePath(song));
                mediaPlayer.prepare();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopSong();
                    }
                });
            }else{
                mediaPlayer=new MediaPlayer();
                playSong(song);
            }
        } catch (Exception e) {
            Log.i("WOURA", "Error: "+ e);
        }
    }

    void stopSong(){
        if(audio_state == 2){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            setAudioState(1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_FILE_EXISTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Creating app folder",String.valueOf(new FileUtils(this).createFolder()));
                    fileExists = new FileUtils(this).checkFile(item_src);

                    if (fileExists) {
                        setAudioState(1);
                    } else {
                        setAudioState(0);
                    }
                    Toast.makeText(HymnsDetailActivity.this, "SD permission allowed", Toast.LENGTH_SHORT).show();
                } else {
                    //SD ACCESS DENIED
                    Toast.makeText(HymnsDetailActivity.this, "SD permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSIONS_EXTENAL_SD: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //SD ACCESS ALLOWED
                    Toast.makeText(HymnsDetailActivity.this, "SD permission required", Toast.LENGTH_SHORT).show();
                } else {
                    //SD ACCESS DENIED
                    Toast.makeText(HymnsDetailActivity.this, "SD permission denied", Toast.LENGTH_SHORT).show();
                }
            }

            case PERMISSIONS_EXTENAL_SD_READ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //SD ACCESS ALLOWED
                    Toast.makeText(HymnsDetailActivity.this, "SD read permission required", Toast.LENGTH_SHORT).show();
                } else {
                    //SD ACCESS DENIED
                    Toast.makeText(HymnsDetailActivity.this, "SD read permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private long DownloadData (Uri uri) {

        long downloadReference;

        // Create request for android download manager
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(item_src);

        //Setting description of request
        request.setDescription("Downloading song.");
        request.allowScanningByMediaScanner();

        String folder = new FileUtils(this).getAppFolder();
        String fname = URLUtil.guessFileName(http_url, null, MimeTypeMap.getFileExtensionFromUrl(http_url));
        request.setDestinationInExternalPublicDir(new FileUtils(getParent()).getAppFolder(), fname);
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        Log.i("WOURA", "Path: "+ folder + "/" + fname);

        return downloadReference;
    }


    private void DownloadStatus(Cursor cursor, long DownloadId){
        setAudioState(3);
        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }

        Toast.makeText(HymnsDetailActivity.this, "Download Status: " + statusText + " " + reasonText, Toast.LENGTH_LONG).show();
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            setAudioState(1);
        }
    };

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (hasPermission(context, permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}