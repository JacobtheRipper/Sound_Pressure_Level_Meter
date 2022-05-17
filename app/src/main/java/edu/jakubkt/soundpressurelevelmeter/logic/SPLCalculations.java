package edu.jakubkt.soundpressurelevelmeter.logic;

import static java.lang.Math.cos;
import static java.lang.Math.PI;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

import static edu.jakubkt.soundpressurelevelmeter.MainActivity.AUDIO_BUFFER_SIZE;

import androidx.annotation.NonNull;

import org.jtransforms.fft.DoubleFFT_1D;

public class SPLCalculations {

    private final double[] signalBuffer = new double[AUDIO_BUFFER_SIZE];
    private final double[] fftBuffer = new double[2*AUDIO_BUFFER_SIZE]; // contains both real and imaginary part according JTransforms JavaDoc
    private final double[] fftAmplitudeBuffer = new double[AUDIO_BUFFER_SIZE/2]; // FFT amplitudes for frequencies below Nyquist frequency

    private final DoubleFFT_1D fft_1D = new DoubleFFT_1D(AUDIO_BUFFER_SIZE);

    public short[] calculateLinst(short[] buffer) {
            return buffer;
    }

    public short[] calculateLeq(short[] buffer) {
        return buffer;
    }

    public short[] calculateLmax(short[] buffer) {
        return buffer;
    }

    public short[] calculateLmin(short[] buffer) {
        return buffer;
    }

    @NonNull
    private double[] applyWindowFunction(int windowFunction, @NonNull short[] buffer) {
        double[] outputSignal = signalBuffer;
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

        //TODO Hann, Hamming or FlatTop
        switch (windowFunction) {
            // Hann Window
            case 0:
                for (int i = 0; i < bufferLength; i++) {
                    double hannEquation = 0.5 * (1 - cos(2*PI*i / (bufferLength - 1)));
                    outputSignal[i] = buffer[i] * hannEquation;
                }
                break;
            // Hamming Window
            case 1:
                for (int i = 0; i < bufferLength; i++) {
                    double hammingEquation = hammingA0 - hammingA1 * cos(2*PI*i / (bufferLength - 1));
                    outputSignal[i] = buffer[i] * hammingEquation;
                }
                break;
            // FlatTop Window
            case 2:
                for (int i = 0; i < bufferLength; i++) {
                    double cosine1 = flatTopA1 * cos(2*PI*i / (bufferLength - 1));
                    double cosine2 = flatTopA2 * cos(4*PI*i / (bufferLength - 1));
                    double cosine3 = flatTopA3 * cos(6*PI*i / (bufferLength - 1));
                    double cosine4 = flatTopA4 * cos(8*PI*i / (bufferLength - 1));

                    double flatTopEquation = flatTopA0 - cosine1 + cosine2 - cosine3 + cosine4;
                    outputSignal[i] = buffer[i] * flatTopEquation;
                }
                break;
            // Else return outputSignal by casting buffer to double (Rectangular Window)
            default:
                for (int i = 0; i < bufferLength; i++) {
                    outputSignal[i] = buffer[i];
                }
                break;
        }
        return outputSignal;
    }

    private double[] calculateFFTAmplitude(double[] buffer) {
        // TODO use JTransforms library
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
        return outputFFTAmplitude;
    }

    private double calculateSignalEnergy(double[] buffer) {
        // TODO use Parseval Theorem on an FFT amplitude vector
        int bufferLength = buffer.length;
        double totalEnergy = 0;
        for (int i = 0; i < bufferLength; i++) {
            totalEnergy += pow(buffer[i], 2);
        }
        // For a given Sample Rate total energy is equal to instantaneous signal energy
        totalEnergy = totalEnergy/bufferLength;
        return totalEnergy;
    }

    private short[] splitIntoOctaveBands(int filter, short[] buffer) {
        // TODO split an FFT signal into octave bands with correct center frequency for further signal processing
        return buffer;
    }

    private double convertEnergyToDB (double energyValue) {
        return 10 * log10(energyValue);
    }

    private short[] applyFrequencyWeightings(int weightingType, short[] buffer) {
        // TODO return an array using an appropriate frequency weighting based on settings in root_preferences
        return buffer;
    }

    private short[] applyCalibrationCorrection(int calibrationValue, short[] buffer) {
        // TODO return an array using an appropriate calibration value based on settings in CalibrationActvity
        return buffer;
    }
}
