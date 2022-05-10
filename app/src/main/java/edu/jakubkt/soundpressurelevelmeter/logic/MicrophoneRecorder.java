package edu.jakubkt.soundpressurelevelmeter.logic;

import static edu.jakubkt.soundpressurelevelmeter.MainActivity.REQUEST_CODE_MICROPHONE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.AudioRecord;

import android.util.Log;
import androidx.core.app.ActivityCompat;


public class MicrophoneRecorder implements Runnable {

    private final int AUDIO_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION; // used for raw audio, MediaRecorder.AudioSource.UNPROCESSED can also be used if possible
    private final int SAMPLE_RATE = 44100;
    private final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private final int BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private static final String TAG = "MicrophoneRecorder";

    private boolean running;

    private final AudioBufferProcessing audioBufferProcessing;
    private AudioRecord audioRecord;
    private Thread thread;


    public MicrophoneRecorder(Activity activity, Context context, AudioBufferProcessing processing) {

        // Grant user permission to use a microphone
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO handle user permission
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_MICROPHONE);
        }
        audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE_RECORDING);
        running = false;
        audioBufferProcessing = processing;
    }

    public void startRecording() {
        if (!running) {
            running = true;
            thread = new Thread(this);
            thread.start();
            Log.d(TAG, "MicrophoneRecorder thread initialised successfully");
        }
    }

    public void stopRecording() {
        try {
            if (running) {
                running = false;
                thread.join();
                // Clean up AudioRecorder resources
                if (audioRecord != null && audioRecord.getState() != AudioRecord.STATE_UNINITIALIZED) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
                Log.d(TAG, "MicrophoneRecorder thread finished successfully");
            }
        }
        catch (InterruptedException e) {
            Log.v(TAG, "Interrupted Exception", e);
        }
    }

    @Override
    public void run() {
        // TODO implement run() method
        // buffer containing 100 milliseconds of audio data
        short[] audioBuffer = new short[SAMPLE_RATE/10];
        int numberOfSamples;
        try {
            audioRecord.startRecording();
            while (running) {
                numberOfSamples = audioRecord.read(audioBuffer, 0, audioBuffer.length);
                audioBufferProcessing.processAudioBuffer(audioBuffer);
            }
        }
        catch (Throwable throwable) {
            Log.v(TAG, "Error while recording audio", throwable);
        }
    }
}
