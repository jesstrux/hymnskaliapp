package com.akilsw.hymns;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class HymnsDetailActivityFragment extends Fragment {
    private final String LOG_TAG = HymnsDetailActivityFragment.class.getSimpleName();
    private final String HASHTAG = "#HymnsKali";
    private String name;
    int id;
    private String base_url = "http://10.0.2.2/hymns/";
    private String http_url;
    String item_src;
    private int audio_state;
    MediaPlayer mediaPlayer;

    public HymnsDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        Bundle args = intent.getExtras();
        id = args.getInt("id");
        name = args.getString("name");

        item_src = args.getString("src");
        http_url = base_url + item_src;

        View rootView = inflater.inflate(R.layout.fragment_hymns_detail, container, false);
        if(intent != null){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String lang = prefs.getString(getString(R.string.pref_language_key), getString(R.string.pref_language_default));

            if(lang.equals("sw"))
                ((TextView) rootView.findViewById(R.id.detail_text)).setText(getString(R.string.lyrics_sw));
            else if(lang.equals("en"))
                ((TextView) rootView.findViewById(R.id.detail_text)).setText(getString(R.string.lyrics_en));

            Activity activity = this.getActivity();
            activity.setTitle(name);

            FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
            assert fab != null;

            //create app folder
            Log.e("Creating app folder",String.valueOf(new com.akilsw.hymns.FileUtils(getContext()).createFolder()));
            mediaPlayer = new MediaPlayer();

            //check file and set correct resource
            Boolean fileExists = new com.akilsw.hymns.FileUtils(getContext()).checkFile(item_src);

            if(fileExists){
                setAudioState(1);
            }else{
                setAudioState(0);
            }

            if(fab != null){
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(audio_state == 0){
                            downloadAudio(view);
                            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                            getActivity().registerReceiver(downloadReceiver, filter);
                        }
                        else if(audio_state == 1 || audio_state == 2){
                            togglePlayback(item_src);
                        }
                    }
                });
            }
        }
        return rootView;
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
        try {
            if(mediaPlayer!=null){
                mediaPlayer.setDataSource(new FileUtils(getContext()).getFilePath(song));
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
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
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

    private void downloadAudio(View view) {
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
        Long Audio_DownloadId = DownloadData (Uri.parse(http_url), view);

        DownloadManager.Query AudioDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        AudioDownloadQuery.setFilterById(Audio_DownloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(AudioDownloadQuery);
        if(cursor.moveToFirst()){
            DownloadStatus(cursor, Audio_DownloadId);
        }
    }

    private void setAudioState(int state){
//      0 not found, 1 found / paused, 2 playing
        audio_state = state;
        setFabSrc();
    }

    private void setFabSrc(){
//      0 not found, 1 found / paused, 2 playing, 3 downloading
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        assert fab != null;

//        if(audio_state == 1)
//            fab.setImageResource(R.drawable.ic_play);
//        else if(audio_state == 2)
//            fab.setImageResource(R.drawable.ic_stop);
//        else if(audio_state == 3)
//            fab.setImageResource(R.drawable.ic_download);
//        else
//            fab.setImageResource(R.drawable.ic_audio);
    }


    private long DownloadData (Uri uri, View v) {

        long downloadReference;

        // Create request for android download manager
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
//        request.setTitle(item_src);

        //Setting description of request
        request.setDescription("Downloading song.");
        request.allowScanningByMediaScanner();


        String fname = URLUtil.guessFileName(http_url, null, MimeTypeMap.getFileExtensionFromUrl(http_url));
        request.setDestinationInExternalPublicDir(new com.akilsw.hymns.FileUtils(getActivity()).getAppFolder(), fname);
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }


    private void DownloadStatus(Cursor cursor, long DownloadId){
//        setAudioState(3);
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

        Toast.makeText(getActivity(), "Download Status: " + statusText + " " + reasonText, Toast.LENGTH_LONG).show();
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

//            setAudioState(1);
            Toast toast = Toast.makeText(getActivity(),
                    "Download Complete", Toast.LENGTH_SHORT);
            toast.show();
        }
    };
}
