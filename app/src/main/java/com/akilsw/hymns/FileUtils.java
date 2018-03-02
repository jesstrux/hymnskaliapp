package com.akilsw.hymns;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Kileha on 8/28/16.
 */

public class FileUtils {
    Context context;
    String directory="Hymnskali";
    FileUtils(Context context){
        this.context=context;
    }

    public boolean createFolder(){
        File myFolder=new File(Environment.getExternalStorageDirectory()+File.separator+directory);

        if(!myFolder.exists()){
            myFolder.mkdir();
            return true;
        }else{
            return false;
        }
    }

    public boolean checkFile(String file){
        File myFile=new File(Environment.getExternalStorageDirectory()+File.separator+directory+File.separator+file);
        return myFile.exists();
    }


    public String getFilePath(String file){
        File myFile=new File(Environment.getExternalStorageDirectory()+File.separator+directory+File.separator+file);
        return myFile.getAbsolutePath();
    }

    public String getAppFolder(){
//        File myFile=new File(Environment.getExternalStorageDirectory()+File.separator+directory);
        return directory;
    }

}
