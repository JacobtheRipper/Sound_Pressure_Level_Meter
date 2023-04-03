package edu.jakubkt.soundpressurelevelmeter

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast

import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.REQUEST_CODE_MICROPHONE
import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.EXTRA_WEIGHTINGS_TYPE
import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.EXTRA_WINDOW_TYPE
import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.EXTRA_CALIBRATION_VALUES

import edu.jakubkt.soundpressurelevelmeter.databinding.ActivitySplmeterBinding
import edu.jakubkt.soundpressurelevelmeter.logic.AudioBufferProcessing
import edu.jakubkt.soundpressurelevelmeter.logic.MicrophoneRecorder
import edu.jakubkt.soundpressurelevelmeter.logic.SPLCalculations
import org.apache.commons.math3.util.ArithmeticUtils

import java.math.RoundingMode
import java.text.DecimalFormat

class SPLMeterActivity : AppCompatActivity(), AudioBufferProcessing {
    private lateinit var binding: ActivitySplmeterBinding
    private lateinit var calculation: SPLCalculations
    private lateinit var recorder: MicrophoneRecorder

    private lateinit var windowType: String
    private lateinit var weightingsType: String

    // Manage updating UI TextViews on a UI thread
    @Volatile
    private var updateUI: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplmeterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.SPLMeterToolbar)

        windowType = intent.getStringExtra(EXTRA_WINDOW_TYPE) ?: "hann"
        weightingsType = intent.getStringExtra(EXTRA_WEIGHTINGS_TYPE) ?: "a"
        val octaveBandsCalibrationValues = intent.getDoubleArrayExtra(EXTRA_CALIBRATION_VALUES) ?: DoubleArray(8) { 0.0 }

        calculation = SPLCalculations(octaveBandsCalibrationValues)

        recorder = MicrophoneRecorder(this, applicationContext, this)
        recorder.startRecording()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.measurement_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_return_to_main_menu -> {
                finish()
                return true
            }
            R.id.action_take_screenshot -> {
                Toast.makeText(applicationContext, R.string.placeholder_string, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        recorder.stopRecording()
        updateUI = false
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionToRecordAudio: Boolean = if (requestCode == REQUEST_CODE_MICROPHONE) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        // a message explaining the need to use the microphone would be preferred
        if(!permissionToRecordAudio) finish()
    }

    override fun processAudioBuffer(audioBuffer: ShortArray?) {
        if(updateUI) {
            updateUI = false
            val linstFieldValue = calculation.calculateLinst(windowType, weightingsType, audioBuffer)
            val leqFieldValue: Double = calculation.calculateLeq()
            val lmaxFieldValue: Double = calculation.calculateLmax()
            val lminFieldValue: Double = calculation.calculateLmin()

            runOnUiThread {
                // Rounding floating-point number to 1 decimal place
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.HALF_UP

                binding.textViewLinstFieldValue.text = df.format(linstFieldValue)
                binding.textViewLeqFieldValue.text = df.format(leqFieldValue)
                binding.textViewLmaxFieldValue.text = df.format(lmaxFieldValue)
                binding.textViewLminFieldValue.text = df.format(lminFieldValue)
                updateUI = true
            }
        }
    }
}
