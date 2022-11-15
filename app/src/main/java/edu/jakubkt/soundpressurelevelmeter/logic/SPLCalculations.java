package edu.jakubkt.soundpressurelevelmeter.logic;

import static java.lang.Math.cos;
import static java.lang.Math.PI;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

import static edu.jakubkt.soundpressurelevelmeter.MainActivity.AUDIO_BUFFER_SIZE;
import static edu.jakubkt.soundpressurelevelmeter.MainActivity.SAMPLE_RATE;

import androidx.annotation.NonNull;
import android.util.Log;

import org.jtransforms.fft.DoubleFFT_1D;

public class SPLCalculations {

    private static final String TAG = "SPLCalculations";

    private final double[] signalBuffer = new double[AUDIO_BUFFER_SIZE];
    private final double[] fftBuffer = new double[2*AUDIO_BUFFER_SIZE]; // contains both real and imaginary part according to JTransforms JavaDoc
    private final double[] fftAmplitudeBuffer = new double[AUDIO_BUFFER_SIZE/2]; // FFT amplitudes for frequencies below Nyquist frequency
    private final double[] octaveBandAmplitudeBuffer = new double[AUDIO_BUFFER_SIZE/2];

    private final int[] octaveBandsCenterFrequencies = {125, 250, 500, 1000, 2000, 4000, 8000, 16000};
    private final double[] aWeightings = {-16.1, -8.6, -3.2, 0, 1.2, 1.0, -1.1, -6.6};
    private final double[] cWeightings = {-0.2, 0, 0, 0, -0.2, -0.8, -3.0, -8.5};

    private final DoubleFFT_1D fft_1D = new DoubleFFT_1D(AUDIO_BUFFER_SIZE);

    private double lMax;
    private double lMin;
    private double totalSoundIntensity = 0.0d;
    private long numberOfMeasurementsTaken = 0;

    public double calculateLinst(String windowFunction, String weightingType, short[] buffer) {
        numberOfMeasurementsTaken++;

        // Calculate Linst
        double outputLinst = 0.0;
        applyWindowFunction(windowFunction, buffer);
        calculateFFTAmplitude(signalBuffer);

        // Calculate SPL per octave band
        for (int i = 0; i < octaveBandsCenterFrequencies.length; i++) {
            octaveBandFilter(octaveBandsCenterFrequencies[i], fftAmplitudeBuffer);
            double lInstPerOctave = calculateSignalEnergy(octaveBandAmplitudeBuffer);
            lInstPerOctave = convertEnergyToDB(lInstPerOctave);
            lInstPerOctave += applyFrequencyWeightingPerOctaveBand(weightingType, i);
            lInstPerOctave += applyCalibrationCorrectionPerOctaveBand(i);

            outputLinst += pow(10, 0.1*lInstPerOctave);
        }
        //double weightingValue = applyFrequencyWeightingPerOctaveBand(weightingType, 0); // used for later
        outputLinst = convertEnergyToDB(outputLinst);

        /*
        // TODO move commented code for later usage
        // Calculate Linst
        double outputLinst;
        applyWindowFunction(windowFunction, buffer);
        calculateFFTAmplitude(signalBuffer);
        outputLinst = calculateSignalEnergy(fftAmplitudeBuffer);
        outputLinst = convertEnergyToDB(outputLinst);
        */

        // Set lMax and lMin values, wait 8 measurements cycles until outputLinst value stabilises
        if (numberOfMeasurementsTaken <= 8) {
            lMax = outputLinst;
            lMin = outputLinst;
        }
        else {
            if (lMax < outputLinst) lMax = outputLinst;
            if (lMin > outputLinst) lMin = outputLinst;
        }

        // Increase totalSoundIntensity value for Leq calculation
        totalSoundIntensity += pow(10, 0.1*outputLinst);

        return outputLinst;
    }

    public double calculateLeq() {
        return convertEnergyToDB(totalSoundIntensity/numberOfMeasurementsTaken);
    }

    public double calculateLmax() {
        return lMax;
    }

    public double calculateLmin() {
        return lMin;
    }

    private void applyWindowFunction(String windowFunction, @NonNull short[] buffer) {
        double[] windowedSignal = signalBuffer;
        int bufferLength = buffer.length;

        // Hamming Window coefficients
        double hammingA0 = 0.53836;
        double hammingA1 = 0.46164;

        // FlatTop Window MATLAB coefficients
        double flatTopA0 = 0.21557895;
        double flatTopA1 = 0.41663158;
        double flatTopA2 = 0.277263158;
        double flatTopA3 = 0.083578947;
        double flatTopA4 = 0.006947368;

        switch (windowFunction) {
            // Hann Window
            case "hann":
                Log.d(TAG, "Applied Hann Window");
                for (int i = 0; i < bufferLength; i++) {
                    double hannEquation = 0.5 * (1 - cos(2*PI*i / (bufferLength - 1)));
                    windowedSignal[i] = buffer[i] * hannEquation;
                }
                break;
            // Hamming Window
            case "hamming":
                Log.d(TAG, "Applied Hamming Window");
                for (int i = 0; i < bufferLength; i++) {
                    double hammingEquation = hammingA0 - hammingA1 * cos(2*PI*i / (bufferLength - 1));
                    windowedSignal[i] = buffer[i] * hammingEquation;
                }
                break;
            // FlatTop Window
            case "flat_top":
                Log.d(TAG, "Applied FlatTop Window");
                for (int i = 0; i < bufferLength; i++) {
                    double cosine1 = flatTopA1 * cos(2*PI*i / (bufferLength - 1));
                    double cosine2 = flatTopA2 * cos(4*PI*i / (bufferLength - 1));
                    double cosine3 = flatTopA3 * cos(6*PI*i / (bufferLength - 1));
                    double cosine4 = flatTopA4 * cos(8*PI*i / (bufferLength - 1));

                    double flatTopEquation = flatTopA0 - cosine1 + cosine2 - cosine3 + cosine4;
                    windowedSignal[i] = buffer[i] * flatTopEquation;
                }
                break;
            // Else if condition failed use Hann Window or return outputSignal by casting buffer to double (Rectangular Window)
            default:
                Log.d(TAG, "Error occurred. Applied Hann Window");
                for (int i = 0; i < bufferLength; i++) {
                    double hannEquation = 0.5 * (1 - cos(2*PI*i / (bufferLength - 1)));
                    windowedSignal[i] = buffer[i] * hannEquation;
                }
                break;
        }
    }

    private void calculateFFTAmplitude(double[] buffer) {
        double[] fftValues = fftBuffer;
        double[] outputFFTAmplitude = fftAmplitudeBuffer;

        int bufferLength = buffer.length;
        int fftAmplitudeBufferLength = outputFFTAmplitude.length;

        // Create complex data buffer to use with DoubleFFT_1D object
        for (int i = 0; i < bufferLength; i++) {
            // Real part
            fftValues[2*i] = buffer[i];
            // Imaginary part
            fftValues[2*i+1] = 0.0;
        }
        // Calculate FFT
        fft_1D.realForwardFull(fftValues);

        // Calculate FFT amplitude
        for (int i = 0; i < fftAmplitudeBufferLength; i++) {
            double realPart = fftValues[2*i];
            double imaginaryPart = fftValues[2*i+1];
            double fftMagnitude = sqrt(pow(realPart, 2) + pow(imaginaryPart, 2));

            outputFFTAmplitude[i] = fftMagnitude;
        }
    }

    private double calculateSignalEnergy(double[] buffer) {
        int bufferLength = buffer.length;
        double totalEnergy = 0;
        // Use Parseval's Theorem on a vector with FFT Amplitude to calculate signal's energy
        for (int i = 0; i < bufferLength; i++) {
            totalEnergy += pow(buffer[i], 2);
        }
        // TODO If the measurement is inaccurate calculate signal's power over observation time
        totalEnergy = totalEnergy/bufferLength;
        return totalEnergy;
    }

    private double convertEnergyToDB (double energyValue) {
        return 10 * log10(energyValue);
    }

    private void octaveBandFilter(int centerFrequency, double[] buffer) {
        // Convert FFT Amplitude to an octave band using equation found at https://www.ap.com/technical-library/deriving-fractional-octave-spectra-from-the-fft-with-apx/
        int octaveBandAmplitudeBufferLength = octaveBandAmplitudeBuffer.length;
        double frequencyResolution = (double)SAMPLE_RATE/AUDIO_BUFFER_SIZE;

        for (int i = 0; i < octaveBandAmplitudeBufferLength; i++) {
            double currentFrequency = frequencyResolution * i;
            double frequencyParameter = 1.507 * ((currentFrequency / centerFrequency) - (centerFrequency / currentFrequency));
            double denominator = 1 + pow(frequencyParameter, 6);
            double octaveBandFilterEquation = sqrt(1 / denominator);
            octaveBandAmplitudeBuffer[i] = octaveBandFilterEquation * buffer[i];
        }
    }

    private double applyFrequencyWeightingPerOctaveBand(String weightingType, int weightingArrayIndex) {
        double outputWeightingValue;
        switch (weightingType) {
            case "a":
                Log.d(TAG, "Applied A-weighting");
                outputWeightingValue = aWeightings[weightingArrayIndex];
                break;
            case "c":
                Log.d(TAG, "Applied C-weighting");
                outputWeightingValue = cWeightings[weightingArrayIndex];
                break;
            case "z":
                Log.d(TAG, "Applied no weighting");
                outputWeightingValue = 0.0;
                break;
            default:
                Log.d(TAG, "Error occurred. Applied A-weighting");
                outputWeightingValue = aWeightings[weightingArrayIndex];
                break;
        }
        return outputWeightingValue;
    }

    private double applyCalibrationCorrectionPerOctaveBand(int octaveBandArrayIndex) {
        // TODO return an array using an appropriate calibration value based on settings in CalibrationActvity
        return 0.0;
    }
}
