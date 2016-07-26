package com.github.marcodama7.audiotest;

import android.app.Application;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class App  extends Application{


    private static FFmpeg ffmpeg;
    public static final String TAG = "audiotest";
    public boolean ffmpegSupported = false;

    public static boolean libsLoaded = false;


    @Override
    public void onCreate() {
        super.onCreate();

        if (ffmpeg == null) {
            //new AndroidLame();
            ffmpeg = FFmpeg.getInstance(getApplicationContext());
            try {
                ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                    @Override
                    public void onStart() {
                        Log.d(TAG, "¶¶ ffmpeg: onStart");

                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "¶¶ ffmpeg: onFailure :(");
                        ffmpegSupported = false;
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "¶¶ ffmpeg: onSuccess :)");
                        ffmpegSupported = true;
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "¶¶ ffmpeg: onFinish");
                    }
                });
            } catch (FFmpegNotSupportedException e) {
                // Handle if FFmpeg is not supported by device
                Log.d(TAG, "¶¶ ffmpeg: FFmpegNotSupportedException :(");
            }
        }


    }



}
