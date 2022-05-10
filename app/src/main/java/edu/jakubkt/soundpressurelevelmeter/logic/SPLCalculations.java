package edu.jakubkt.soundpressurelevelmeter.logic;

public class SPLCalculations {
    public int[] calculateLinst(int[] buffer) {
            return buffer;
    }

    public int[] calculateLeq(int[] buffer) {
        return buffer;
    }

    public int[] calculateLmax(int[] buffer) {
        return buffer;
    }

    public int[] calculateLmin(int[] buffer) {
        return buffer;
    }

    private int[] applyWindowFunction(int windowFunction, int[] buffer) {
        //TODO Hann, Hamming or FlatTop
        return buffer;
    }

    private int[] calculateFFT(int[] buffer) {
        // TODO use JTransforms library
        return buffer;
    }

    private int[] calculateSignalEnergy(int[] buffer) {
        // TODO use Parseval Theorem on an FFT signal
        return buffer;
    }

    private int[] applyOctaveFilter(int filter, int[] buffer) {
        // TODO filter an FFT signal using filter with correct center frequency
        return buffer;
    }

    private int[] convertToDB (int[] buffer) {
        return buffer;
    }

    private int[] applyWeightings(int weightingType, int[] buffer) {
        // TODO return an array using an appropriate weighting based on settings in root_preferences
        return buffer;
    }

    private int[] applyCalibrationCorrection(int calibrationValue, int[] buffer) {
        // TODO return an array using an appropriate calibration value based on settings in CalibrationActvity
        return buffer;
    }
}
