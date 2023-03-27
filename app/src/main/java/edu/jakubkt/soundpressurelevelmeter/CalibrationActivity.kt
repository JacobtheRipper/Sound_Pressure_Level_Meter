package edu.jakubkt.soundpressurelevelmeter

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.EXTRA_CALIBRATION_VALUES

import edu.jakubkt.soundpressurelevelmeter.databinding.ActivityCalibrationBinding
import edu.jakubkt.soundpressurelevelmeter.logic.AudioBufferProcessing
import edu.jakubkt.soundpressurelevelmeter.logic.MicrophoneRecorder
import edu.jakubkt.soundpressurelevelmeter.logic.SPLCalculations
import org.apache.commons.math3.util.ArithmeticUtils.pow

import java.math.RoundingMode
import java.text.DecimalFormat

class CalibrationActivity : AppCompatActivity(), AudioBufferProcessing {
    private val TAG: String = "CalibrationActivity"

    private lateinit var binding: ActivityCalibrationBinding
    private lateinit var calculation: SPLCalculations
    private lateinit var recorder: MicrophoneRecorder

    private lateinit var windowType: String
    private lateinit var weightingsType: String
    private lateinit var octaveBandsCalibrationValues: IntArray

    // Manage updating UI TextViews on a UI thread
    @Volatile
    private var updateUI: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalibrationBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.CalibrationToolbar)

        windowType = "flat_top"
        weightingsType = "z"
        octaveBandsCalibrationValues = intent.getIntArrayExtra(EXTRA_CALIBRATION_VALUES) ?: IntArray(8) { 0 }

        for ((index, value) in octaveBandsCalibrationValues.withIndex())
            Log.d(TAG, "Calibration value received for ${pow(2, index)*125} Hz: $value")

        calculation = SPLCalculations()

        recorder = MicrophoneRecorder(this, applicationContext, this)
        recorder.startRecording()

        setOnClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.calibration_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_return_to_main_menu_from_calibration_activity -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //TODO send calibration values back to Settings Activity
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
        val permissionToRecordAudio: Boolean = if (requestCode == MainActivity.REQUEST_CODE_MICROPHONE) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        // a message explaining the need to use the microphone would be preferred
        if(!permissionToRecordAudio) finish()
    }

    override fun processAudioBuffer(audioBuffer: ShortArray?) {
        //TODO For every octave band calculate Leq, then apply calibration correction
        if(updateUI) {
            updateUI = false

            val linstValueOctaveBand125Hz = 50.0
            val linstValueOctaveBand250Hz = 50.0
            val linstValueOctaveBand500Hz = 50.0
            val linstValueOctaveBand1000Hz = 50.0
            val linstValueOctaveBand2000Hz = 50.0
            val linstValueOctaveBand4000Hz = 50.0
            val linstValueOctaveBand8000Hz = 50.0
            val linstValueOctaveBand16000Hz = 50.0

            runOnUiThread {
                // Rounding floating-point number to 1 decimal place
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.HALF_UP

                binding.linstValueOctaveBand125Hz.text = df.format(linstValueOctaveBand125Hz + octaveBandsCalibrationValues[0])
                binding.linstValueOctaveBand250Hz.text = df.format(linstValueOctaveBand250Hz + octaveBandsCalibrationValues[1])
                binding.linstValueOctaveBand500Hz.text = df.format(linstValueOctaveBand500Hz + octaveBandsCalibrationValues[2])
                binding.linstValueOctaveBand1000Hz.text = df.format(linstValueOctaveBand1000Hz + octaveBandsCalibrationValues[3])
                binding.linstValueOctaveBand2000Hz.text = df.format(linstValueOctaveBand2000Hz + octaveBandsCalibrationValues[4])
                binding.linstValueOctaveBand4000Hz.text = df.format(linstValueOctaveBand4000Hz + octaveBandsCalibrationValues[5])
                binding.linstValueOctaveBand8000Hz.text = df.format(linstValueOctaveBand8000Hz + octaveBandsCalibrationValues[6])
                binding.linstValueOctaveBand16000Hz.text = df.format(linstValueOctaveBand16000Hz + octaveBandsCalibrationValues[7])

                binding.calibrationValueOctaveBand125Hz.text = octaveBandsCalibrationValues[0].toString()
                binding.progressbarOctaveBand125Hz.progress = (linstValueOctaveBand125Hz).toInt() + octaveBandsCalibrationValues[0]

                binding.calibrationValueOctaveBand250Hz.text = octaveBandsCalibrationValues[1].toString()
                binding.progressbarOctaveBand250Hz.progress = (linstValueOctaveBand250Hz).toInt() + octaveBandsCalibrationValues[1]

                binding.calibrationValueOctaveBand500Hz.text = octaveBandsCalibrationValues[2].toString()
                binding.progressbarOctaveBand500Hz.progress = (linstValueOctaveBand500Hz).toInt() + octaveBandsCalibrationValues[2]

                binding.calibrationValueOctaveBand1000Hz.text = octaveBandsCalibrationValues[3].toString()
                binding.progressbarOctaveBand1000Hz.progress = (linstValueOctaveBand1000Hz).toInt() + octaveBandsCalibrationValues[3]

                binding.calibrationValueOctaveBand2000Hz.text = octaveBandsCalibrationValues[4].toString()
                binding.progressbarOctaveBand2000Hz.progress = (linstValueOctaveBand2000Hz).toInt() + octaveBandsCalibrationValues[4]

                binding.calibrationValueOctaveBand4000Hz.text = octaveBandsCalibrationValues[5].toString()
                binding.progressbarOctaveBand4000Hz.progress = (linstValueOctaveBand4000Hz).toInt() + octaveBandsCalibrationValues[5]

                binding.calibrationValueOctaveBand8000Hz.text = octaveBandsCalibrationValues[6].toString()
                binding.progressbarOctaveBand8000Hz.progress = (linstValueOctaveBand8000Hz).toInt() + octaveBandsCalibrationValues[6]

                binding.calibrationValueOctaveBand16000Hz.text = octaveBandsCalibrationValues[7].toString()
                binding.progressbarOctaveBand16000Hz.progress = (linstValueOctaveBand16000Hz).toInt() + octaveBandsCalibrationValues[7]

                updateUI = true
            }
        }
    }

    private fun setOnClickListeners() {
        binding.buttonUpOctaveBand125Hz.setOnClickListener {
            octaveBandsCalibrationValues[0]++
        }

        binding.buttonDownOctaveBand125Hz.setOnClickListener {
            octaveBandsCalibrationValues[0]--
        }

        binding.buttonUpOctaveBand250Hz.setOnClickListener {
            octaveBandsCalibrationValues[1]++
        }

        binding.buttonDownOctaveBand250Hz.setOnClickListener {
            octaveBandsCalibrationValues[1]--
        }

        binding.buttonUpOctaveBand500Hz.setOnClickListener {
            octaveBandsCalibrationValues[2]++
        }

        binding.buttonDownOctaveBand500Hz.setOnClickListener {
            octaveBandsCalibrationValues[2]--
        }

        binding.buttonUpOctaveBand1000Hz.setOnClickListener {
            octaveBandsCalibrationValues[3]++
        }

        binding.buttonDownOctaveBand1000Hz.setOnClickListener {
            octaveBandsCalibrationValues[3]--
        }

        binding.buttonUpOctaveBand2000Hz.setOnClickListener {
            octaveBandsCalibrationValues[4]++
        }

        binding.buttonDownOctaveBand2000Hz.setOnClickListener {
            octaveBandsCalibrationValues[4]--
        }

        binding.buttonUpOctaveBand4000Hz.setOnClickListener {
            octaveBandsCalibrationValues[5]++
        }

        binding.buttonDownOctaveBand4000Hz.setOnClickListener {
            octaveBandsCalibrationValues[5]--
        }

        binding.buttonUpOctaveBand8000Hz.setOnClickListener {
            octaveBandsCalibrationValues[6]++
        }

        binding.buttonDownOctaveBand8000Hz.setOnClickListener {
            octaveBandsCalibrationValues[6]--
        }

        binding.buttonUpOctaveBand16000Hz.setOnClickListener {
            octaveBandsCalibrationValues[7]++
        }

        binding.buttonDownOctaveBand16000Hz.setOnClickListener {
            octaveBandsCalibrationValues[7]--
        }
    }
}
