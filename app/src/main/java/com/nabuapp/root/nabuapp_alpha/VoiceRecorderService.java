package com.nabuapp.root.nabuapp_alpha;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ROOT on 13.06.2016.
 */
public class VoiceRecorderService extends RecorderServiceFactory {


    public boolean isRecording = false;
    private int recDuration;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    //TODO - Добавить запуск диктофона и передачу длительности (Duration)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Integer duration =  (Integer)intent.getExtras().get("duration");
        try {
            launchRecorder(MediaRecorder.AudioSource.MIC,AppDirsPath.DEFAULT_STORAGE_LOCATION_VOICEREC,AppDirsPath.VOICE_FILE_NAME,duration);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return START_STICKY;

    }


    public void onStart(Intent intent, int startId) {
        Log.i("CallRecorder", "RecordService::onStartCommand called while isRecording:" + isRecording);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public  void onDestroy() {

        stopRecorder();
        resetRecorder();
        releaseRecorder();
        super.onDestroy();

    }


}
