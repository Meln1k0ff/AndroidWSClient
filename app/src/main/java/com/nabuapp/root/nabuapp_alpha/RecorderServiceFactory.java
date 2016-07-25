package com.nabuapp.root.nabuapp_alpha;

import android.app.Service;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by newservice_sc on 24.07.2016.
 */
public abstract class RecorderServiceFactory extends Service {


    private MediaRecorder mediaRecorder;
    File tempAudioFile;

    boolean isRecording;
    boolean recorderStopped;

    public RecorderServiceFactory() {
    }

    /**
     * class launchRecorder
     * @param source -
     */
    protected void launchRecorder(int source,String fileSourcePath,String filename,int duration) throws IOException, InterruptedException {
        mediaRecorder = new MediaRecorder();
        tempAudioFile = makeOutputFile(fileSourcePath,filename);
        mediaRecorder.setAudioSource(source);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(tempAudioFile.getAbsolutePath());
        mediaRecorder.setMaxDuration(60 * 1000 * duration);
        mediaRecorder.prepare();
        // Sometimes prepare takes some time to complete
        Thread.sleep(2000);
        mediaRecorder.start();
        isRecording = true;
    }

    protected void stopRecorder(){
        if (mediaRecorder==null) return;
        boolean exception = false;

        try {
            mediaRecorder.stop();
            recorderStopped = true;
        }
            catch (IllegalStateException e) {
                Log.e(CallRecorderConstants.TAG, "IllegalStateException");
                e.printStackTrace();
                exception = true;
            } catch (RuntimeException e) {
                Log.e(CallRecorderConstants.TAG, "RuntimeException");
                exception = true;
            } catch (Exception e) {
                Log.e(CallRecorderConstants.TAG, "Exception");
                e.printStackTrace();
                exception = true;
            }

    }
    protected void resetRecorder(){
        boolean exception = false;

        try {
            mediaRecorder.reset();
        }
        catch (IllegalStateException e) {
            Log.e(CallRecorderConstants.TAG, "IllegalStateException");
            e.printStackTrace();
            exception = true;
        } catch (RuntimeException e) {
            Log.e(CallRecorderConstants.TAG, "RuntimeException");
            exception = true;
        } catch (Exception e) {
            Log.e(CallRecorderConstants.TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }
    }
    protected void releaseRecorder(){
        boolean exception = false;
        try {
            mediaRecorder.release();
        } catch (Exception e) {
            Log.e(CallRecorderConstants.TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }

        mediaRecorder = null;
        if (exception) {

        }
        if (recorderStopped) {
            Log.d("recorder state","stopped");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private File makeOutputFile (String DEFAULT_STORAGE_LOCATION,String type) {
        File dir = new File(DEFAULT_STORAGE_LOCATION);

        // test dir for existence and writeability
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                Log.e("CallRecorder", "RecordService::makeOutputFile unable to create directory " + dir + ": " + e);
//                Toast t = Toast.makeText(getApplicationContext(), "CallRecorder was unable to create the directory " + dir + " to store recordings: " + e, Toast.LENGTH_LONG);
//                t.show();
                return null;
            }
        } else {
            if (!dir.canWrite()) {
                Log.e("CallRecorder", "RecordService::makeOutputFile does not have write permission for directory: " + dir);
//                Toast t = Toast.makeText(getApplicationContext(), "CallRecorder does not have write permission for the directory directory " + dir + " to store recordings", Toast.LENGTH_LONG);
//                t.show();
                return null;
            }
        }


        // create filename based on call data
        //String prefix = "call";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SS");
        String prefix = sdf.format(new Date());

        // add info to file name about what audio channel we were recording

        // int audiosource = Integer.parseInt(prefs.getString(Preferences.PREF_AUDIO_SOURCE, "1"));
        prefix += "-"+type+"-";

        // create suffix based on format
        String suffix = ".3gpp";


        try {
            return File.createTempFile(prefix, suffix, dir);
        } catch (IOException e) {
            Log.e("CallRecorder", "RecordService::makeOutputFile unable to create temp file in " + dir + ": " + e);
//            Toast t = Toast.makeText(getApplicationContext(), "CallRecorder was unable to create temp file in " + dir + ": " + e, Toast.LENGTH_LONG);
//            t.show();
            return null;
        }
    }
}
