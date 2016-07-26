package com.github.marcodama7.audiotest;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTest(getApplicationContext());
    }

    public static void startTest(Context context) {
        int audiores[] = new int[]{
                R.raw.audio_01,
                R.raw.audio_02,
        };
        String [] audioPaths = new String[] {
                context.getFilesDir() + "/audio_01.mp3",
                context.getFilesDir() + "/audio_02.mp3"
        };
        File file = null;
        for (int i = 0; i < audioPaths.length; i++) {
            file = new File(audioPaths[i]);
            if(!file.exists()) {
                try {
                    createFile(audioPaths[i], context, audiores[i]);
                } catch (IOException e) {
                    String message = (e != null && e.getMessage() != null) ? e.getMessage() : " ";
                    Log.e(TAG, message);
                }
            }
        }
        final String newFile = context.getFilesDir() + "/audio_output_"+(new java.util.Date().getTime())+".wav";
        mixAudioTracks(context, audioPaths[0], audioPaths[1], newFile, new OnFFMpegListener() {
            @Override
            public void onSuccess(String fileOutput) {
                Log.d(TAG, "¶¶ FINISHED :)");
                Log.d(TAG, "¶¶ output File = " + newFile);
            }

            @Override
            public void onError(Exception ex) {
                Log.d(TAG, "¶¶ ERROR :(");
            }
        });

    }


    public interface OnFFMpegListener {
        void onSuccess(String fileOutput);
        void onError(Exception ex);
    }

    public static void mixAudioTracks(final Context context, final String audioTrack1, final String audioTrack2, final String outputFile, final OnFFMpegListener onFFMpegListener) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final FFmpeg ffmpeg = FFmpeg.getNewInstance(context);
                try {
                    //String cmd = "-i {file_1} -i {file_2} -filter_complex amerge -ac 2 -c:a libmp3lame -q:a 4 {file_output}";
                    String cmd = "-i {file_1} -i {file_2} -filter_complex amerge -ac 2 -q:a 4 {file_output}";
                    //String cmd = " -i {file_1} -i {file_2} -filter_complex amix=inputs=2:duration=first:dropout_transition=3 {file_output}";
                    cmd = cmd.replaceAll("\\{file_1\\}", audioTrack1)
                            .replaceAll("\\{file_2\\}", audioTrack2)
                            .replaceAll("\\{file_output\\}", outputFile);


                    ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                        boolean result = true;
                        String errorMessage = "";
                        @Override
                        public void onStart() {
                            Log.d(TAG, "¶¶  - step1_0: onStart");
                        }

                        @Override
                        public void onProgress(String message) {
                            Log.d(TAG, " ¶¶ - step1_0: onProgress - " + message);
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.d(TAG, " ¶¶ - step1_0: onFailure - " + message);
                            result = false;
                            errorMessage = message;
                        }

                        @Override
                        public void onSuccess(String message) {
                            Log.d(TAG, " ¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶¶ - step1_0: onSuccess - " + message);
                            result = true;
                        }

                        @Override
                        public void onFinish() {
                            Log.d(TAG, " ¶¶  - step1_0: onFinish");
                            if (result) {
                                if (onFFMpegListener != null) {
                                    onFFMpegListener.onSuccess(outputFile);
                                }
                            }
                            else {
                                if (onFFMpegListener != null) {
                                    onFFMpegListener.onError(new Exception(errorMessage));
                                }
                            }
                            ffmpeg.killRunningProcesses();
                        }
                    });
                } catch (FFmpegCommandAlreadyRunningException e) {
                    Log.e(TAG, " ¶¶ - FFmpegCommandAlreadyRunningException e");
                    if (onFFMpegListener != null) {
                        onFFMpegListener.onError(e);
                    }
                }
            }
        });
        t.start();
    }


    /**
     * Create an output file from raw resources.
     *
     * @param outputFile
     * @param context
     * @param inputRawResources
     * @throws IOException
     */
    public static void createFile(final String outputFile, final Context context, final int inputRawResources) throws IOException {

        final OutputStream outputStream = new FileOutputStream(outputFile);
        final Resources resources = context.getResources();
        final byte[] largeBuffer = new byte[1024 * 4];
        int totalBytes = 0;
        int bytesRead = 0;
        final InputStream inputStream = resources.openRawResource(inputRawResources);
        while ((bytesRead = inputStream.read(largeBuffer)) > 0) {
            if (largeBuffer.length == bytesRead) {
                outputStream.write(largeBuffer);
            } else {
                final byte[] shortBuffer = new byte[bytesRead];
                System.arraycopy(largeBuffer, 0, shortBuffer, 0, bytesRead);
                outputStream.write(shortBuffer);
            }
            totalBytes += bytesRead;
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
        Log.d(TAG, "¶¶ audio file created ! "+ outputFile);
    }

}
