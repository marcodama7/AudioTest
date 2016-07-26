/*
 * Copyright (C) 2016 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.androidlame;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class EncodeManager {

    private static final String TAG = "encode";
    private int PICKFILE_REQUEST_CODE = 123;
    

    static WaveReader waveReader;

    private static final int OUTPUT_STREAM_BUFFER = 8192;



    public static void encode(String fileInput, String fileoutput) {

        BufferedOutputStream outputStream = null;

        File input = new File(fileInput);
        final File output = new File(fileoutput);

        int CHUNK_SIZE = 8192;

        addLog("Initialising wav reader");
        waveReader = new WaveReader(input);

        try {
            waveReader.openWave();
        } catch (IOException e) {
            e.printStackTrace();
        }

        addLog("Intitialising encoder");
        AndroidLame androidLame = new LameBuilder()
                .setInSampleRate(waveReader.getSampleRate())
                .setOutChannels(waveReader.getChannels())
                .setOutBitrate(128)
                .setOutSampleRate(waveReader.getSampleRate())
                .setQuality(5)
                .build();

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(output), OUTPUT_STREAM_BUFFER);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int bytesRead = 0;

        short[] buffer_l = new short[CHUNK_SIZE];
        short[] buffer_r = new short[CHUNK_SIZE];
        byte[] mp3Buf = new byte[CHUNK_SIZE];

        int channels = waveReader.getChannels();

        addLog("started encoding");
        while (true) {
            try {
                if (channels == 2) {

                    bytesRead = waveReader.read(buffer_l, buffer_r, CHUNK_SIZE);
                    addLog("bytes read=" + bytesRead);

                    if (bytesRead > 0) {

                        int bytesEncoded = 0;
                        bytesEncoded = androidLame.encode(buffer_l, buffer_r, bytesRead, mp3Buf);
                        addLog("bytes encoded=" + bytesEncoded);

                        if (bytesEncoded > 0) {
                            try {
                                addLog("writing mp3 buffer to outputstream with " + bytesEncoded + " bytes");
                                outputStream.write(mp3Buf, 0, bytesEncoded);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } else break;
                } else {

                    bytesRead = waveReader.read(buffer_l, CHUNK_SIZE);
                    addLog("bytes read=" + bytesRead);

                    if (bytesRead > 0) {
                        int bytesEncoded = 0;

                        bytesEncoded = androidLame.encode(buffer_l, buffer_l, bytesRead, mp3Buf);
                        addLog("bytes encoded=" + bytesEncoded);

                        if (bytesEncoded > 0) {
                            try {
                                addLog("writing mp3 buffer to outputstream with " + bytesEncoded + " bytes");
                                outputStream.write(mp3Buf, 0, bytesEncoded);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } else break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        addLog("flushing final mp3buffer");
        int outputMp3buf = androidLame.flush(mp3Buf);
        addLog("flushed " + outputMp3buf + " bytes");

        if (outputMp3buf > 0) {
            try {
                addLog("writing final mp3buffer to outputstream");
                outputStream.write(mp3Buf, 0, outputMp3buf);
                addLog("closing output stream");
                outputStream.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

  

    private static void addLog(final String log) {
        Log.d(TAG, log);
    }
}
