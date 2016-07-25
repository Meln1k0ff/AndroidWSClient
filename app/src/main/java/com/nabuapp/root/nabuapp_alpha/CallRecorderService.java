package com.nabuapp.root.nabuapp_alpha;

import android.app.PendingIntent;
import android.app.Service;
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

public class CallRecorderService extends RecorderServiceFactory {
    private MediaRecorder recorder;

//    public static final String DEFAULT_STORAGE_LOCATION = "/sdcard/.nabuApp/callrec";

    private String phoneNumber = null;

    private boolean onCall = false;
    private boolean recording = false;
    private boolean silentMode = false;
    private boolean onForeground = false;

    File tempAudioFile;
    private RecorderServiceFactory recorderServiceFactory;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent != null) {
            int commandType = intent.getIntExtra("commandType", 0);
            if (commandType != 0) {
                if (commandType == CallRecorderConstants.RECORDING_ENABLED) {
                    Log.d("Call recorder", "RecordService RECORDING_ENABLED");
                    silentMode = intent.getBooleanExtra("silentMode", true);
                    if (!silentMode && phoneNumber != null && onCall
                            && !recording)
                        commandType = CallRecorderConstants.STATE_START_RECORDING;

                } else if (commandType == CallRecorderConstants.RECORDING_DISABLED) {
                    Log.d("", "RecordService RECORDING_DISABLED");
                    silentMode = intent.getBooleanExtra("silentMode", true);
                    if (onCall && phoneNumber != null && recording)
                        commandType = CallRecorderConstants.STATE_STOP_RECORDING;
                }

                if (commandType == CallRecorderConstants.STATE_INCOMING_NUMBER) {
                    Log.d("", "RecordService STATE_INCOMING_NUMBER");
                    startService();
                    if (phoneNumber == null)
                        phoneNumber = intent.getStringExtra("phoneNumber");

                    silentMode = intent.getBooleanExtra("silentMode", true);
                } else if (commandType == CallRecorderConstants.STATE_CALL_START) {
                    Log.d(CallRecorderConstants.TAG, "RecordService STATE_CALL_START");
                    onCall = true;

                    if (!silentMode && phoneNumber != null && onCall
                            && !recording) {
                        startService();
//                        recorderServiceFactory.launchRecorder();
                        try {
                            startRecording(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (commandType == CallRecorderConstants.STATE_CALL_END) {
                    Log.d(CallRecorderConstants.TAG, "RecordService STATE_CALL_END");
                    onCall = false;
                    phoneNumber = null;

//                    stopAndReleaseRecorder();
                    recording = false;
                    stopService();
                } else if (commandType == CallRecorderConstants.STATE_START_RECORDING) {
                    Log.d(CallRecorderConstants.TAG, "RecordService STATE_START_RECORDING");
                    if (!silentMode && phoneNumber != null && onCall) {
                        startService();


                        try {
                            startRecording(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (commandType == CallRecorderConstants.STATE_STOP_RECORDING) {
                    Log.d("", "RecordService STATE_STOP_RECORDING");
                    stopAndReleaseRecorder();
                    recording = false;
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startRecording(Intent intent) throws IOException, InterruptedException {
        int duration = (int) intent.getExtras().get("duration");
        launchRecorder(MediaRecorder.AudioSource.VOICE_CALL,AppDirsPath.DEFAULT_STORAGE_LOCATION_CALLREC,AppDirsPath.CALL_FILE_NAME,duration);


        Log.d("", "RecordService startRecording");
        boolean exception = false;
//        recorder = new MediaRecorder();

        try {

            Log.d("tempAudioFile:",tempAudioFile.getAbsolutePath());



            //TODO - implement error listener
            MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
                public void onError(MediaRecorder arg0, int arg1, int arg2) {
//                    Log.e(Constants.TAG, "OnErrorListener " + arg1 + "," + arg2);
//                    terminateAndEraseFile();
                }
            };
//
//            recorder.setOnErrorListener(errorListener);

//            OnInfoListener infoListener = new OnInfoListener() {
//                public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
//                    Log.e(Constants.TAG, "OnInfoListener " + arg1 + "," + arg2);
//                    terminateAndEraseFile();
//                }
//            };
//            recorder.setOnInfoListener(infoListener);


            Log.d(CallRecorderConstants.TAG, "RecordService recorderStarted");
        } catch (IllegalStateException e) {
            Log.e(CallRecorderConstants.TAG, "IllegalStateException");
            e.printStackTrace();
            exception = true;
        } catch (Exception e) {
            Log.e(CallRecorderConstants.TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }

//        if (exception) {
//            terminateAndEraseFile();
//        }

        if (recording) {
            Log.d("call","recording");
//            Toast toast = Toast.makeText(this,
//                    "Call is Recording",
//                    Toast.LENGTH_SHORT);
//            toast.show();
        } else {
//            Toast toast = Toast.makeText(this,
//                    "Call is not recording",
//                    Toast.LENGTH_LONG);
//            toast.show();
        }
    }

    private void stopAndReleaseRecorder() {
        if (recorder == null)
            return;
        Log.d(CallRecorderConstants.TAG, "RecordService stopAndReleaseRecorder");
        boolean recorderStopped = false;
        boolean exception = false;

        try {
            stopRecorder();
            recorderStopped = true;
        } catch (IllegalStateException e) {
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
        try {
            recorder.reset();
        } catch (Exception e) {
            Log.e(CallRecorderConstants.TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }
        try {
            recorder.release();
        } catch (Exception e) {
            Log.e(CallRecorderConstants.TAG, "Exception");
            e.printStackTrace();
            exception = true;
        }

        recorder = null;
        if (exception) {

        }
        if (recorderStopped) {
//            Toast toast = Toast.makeText(this,
//                    "Call finished",
//                    Toast.LENGTH_SHORT);
//            toast.show();
        }
    }


    private void startService() {
        if (!onForeground) {
            Log.d("", "RecordService startService");
            Intent intent = new Intent(this, MainActivity.class);
            // intent.setAction(Intent.ACTION_VIEW);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getBaseContext(), 0, intent, 0);

//            Notification notification = new NotificationCompat.Builder(
//                    getBaseContext())
//                    .setContentTitle(
//                            this.getString(R.string.notification_title))
//                    .setTicker(this.getString(R.string.notification_ticker))
//                    .setContentText(this.getString(R.string.notification_text))
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setContentIntent(pendingIntent).setOngoing(true)
//                    .getNotification();

//            notification.flags = Notification.FLAG_NO_CLEAR;

//            startForeground(1337, notification);
            onForeground = true;
            double  a= 1d;
        }
    }

    private void stopService() {
        Log.d(CallRecorderConstants.TAG, "RecordService stopService");
        stopForeground(true);
        onForeground = false;
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d("", "RecordService onDestroy");
        stopAndReleaseRecorder();
        stopService();
        super.onDestroy();
    }



}
