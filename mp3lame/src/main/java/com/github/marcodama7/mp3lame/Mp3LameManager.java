package com.github.marcodama7.mp3lame;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.format.Time;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Mp3LameManager {

    static {
        System.loadLibrary("mp3lame");
    }

    public static Mp3LameManager instance;


    private native void initEncoder(int numChannels, int sampleRate, int bitRate, int mode, int quality);

    private native void destroyEncoder();

    private native int encodeFile(String sourcePath, String targetPath);

    public static void sInitEncoder() {
        if (instance == null) {
            instance = new Mp3LameManager();
        }
        instance.initEncoder(NUM_CHANNELS, SAMPLE_RATE, BITRATE, MODE, QUALITY);
    }

    public static void sDestroyEncoder() {
        if (instance == null) {
            instance = new Mp3LameManager();
        }
        instance.destroyEncoder();
    }

    public static void sEncodeFile(String sourcePath, String targetPath) {
        if (instance == null) {
            instance = new Mp3LameManager();
        }
        instance.encodeFile(sourcePath, targetPath);
    }

    public static final int NUM_CHANNELS = 1;
    public static final int SAMPLE_RATE = 16000;
    public static final int BITRATE = 128;
    public static final int MODE = 1;
    public static final int QUALITY = 2;
    private AudioRecord mRecorder;
    private short[] mBuffer;
    private final String startRecordingLabel = "Start recording";
    private final String stopRecordingLabel = "Stop recording";
    private boolean mIsRecording = false;
    private File mRawFile;
    private File mEncodedFile;


    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lame);

        initRecorder();
        initEncoder(NUM_CHANNELS, SAMPLE_RATE, BITRATE, MODE, QUALITY);

        final Button button = (Button) findViewById(R.id.button);
        button.setText(startRecordingLabel);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!mIsRecording) {
                    button.setText(stopRecordingLabel);
                    mIsRecording = true;
                    mRecorder.startRecording();
                    mRawFile = getFile("raw");
                    startBufferedWrite(mRawFile);
                } else {
                    button.setText(startRecordingLabel);
                    mIsRecording = false;
                    mRecorder.stop();
                    mEncodedFile = getFile("mp3");
                    int result = encodeFile(mRawFile.getAbsolutePath(), mEncodedFile.getAbsolutePath());
                    if (result == 0) {
                        Toast.makeText(LameActivity.this, "Encoded to " + mEncodedFile.getName(), Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_lame, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        mRecorder.release();
        destroyEncoder();
        super.onDestroy();
    }

*/
    private void initRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    }

    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                    while (mIsRecording) {
                        int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);
                        }
                    }
                } catch (IOException e) {
                    //Toast.makeText(Mp3LameManager.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            //Toast.makeText(Mp3LameManager.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                //Toast.makeText(Mp3LameManager.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private File getFile(final String suffix) {
        Time time = new Time();
        time.setToNow();
        return new File(Environment.getExternalStorageDirectory(), time.format("%Y%m%d%H%M%S") + "." + suffix);
    }




}
