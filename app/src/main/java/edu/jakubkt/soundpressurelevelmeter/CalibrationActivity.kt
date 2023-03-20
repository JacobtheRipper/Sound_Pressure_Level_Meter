package edu.jakubkt.soundpressurelevelmeter

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import edu.jakubkt.soundpressurelevelmeter.databinding.ActivityCalibrationBinding
import edu.jakubkt.soundpressurelevelmeter.logic.AudioBufferProcessing
import edu.jakubkt.soundpressurelevelmeter.logic.MicrophoneRecorder
import edu.jakubkt.soundpressurelevelmeter.logic.SPLCalculations

import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class CalibrationActivity : AppCompatActivity(), AudioBufferProcessing {
    private lateinit var binding: ActivityCalibrationBinding
    private  lateinit var calculation: SPLCalculations
    private lateinit var recorder: MicrophoneRecorder

    private lateinit var windowType: String
    private lateinit var weightingsType: String

    //TODO remove after testing
    private lateinit var random: Random
    private val RANDOM_MAX_RANGE: Int = 120

    // Manage updating UI TextViews on a UI thread
    @Volatile
    private var updateUI: Boolean = true

    //TODO retrieve calibration values from Settings Activity
    private val octaveBandCalibrationValues = IntArray(8) { 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalibrationBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.CalibrationToolbar)

        windowType = "flat_top"
        weightingsType = "z"

        calculation = SPLCalculations()

        recorder = MicrophoneRecorder(this, applicationContext, this)
        recorder.startRecording()

        setOnClickListeners()

        //TODO remove after testing
        random = Random()

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
        //TODO Finish implementing
        if(updateUI) {
            updateUI = false

            val linstValueOctaveBand125Hz = random.nextDouble() * RANDOM_MAX_RANGE
            val linstValueOctaveBand250Hz = random.nextDouble() * RANDOM_MAX_RANGE
            val linstValueOctaveBand500Hz = random.nextDouble() * RANDOM_MAX_RANGE
            val linstValueOctaveBand1000Hz = random.nextDouble() * RANDOM_MAX_RANGE
            val linstValueOctaveBand2000Hz = random.nextDouble() * RANDOM_MAX_RANGE
            val linstValueOctaveBand4000Hz = random.nextDouble() * RANDOM_MAX_RANGE
            val linstValueOctaveBand8000Hz = random.nextDouble() * RANDOM_MAX_RANGE
            val linstValueOctaveBand16000Hz = random.nextDouble() * RANDOM_MAX_RANGE

            runOnUiThread {
                // Rounding floating-point number to 1 decimal place
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.HALF_UP

                binding.linstValueOctaveBand125Hz.text = df.format(linstValueOctaveBand125Hz + octaveBandCalibrationValues[0])
                binding.linstValueOctaveBand250Hz.text = df.format(linstValueOctaveBand250Hz)
                binding.linstValueOctaveBand500Hz.text = df.format(linstValueOctaveBand500Hz)
                binding.linstValueOctaveBand1000Hz.text = df.format(linstValueOctaveBand1000Hz)
                binding.linstValueOctaveBand2000Hz.text = df.format(linstValueOctaveBand2000Hz)
                binding.linstValueOctaveBand4000Hz.text = df.format(linstValueOctaveBand4000Hz)
                binding.linstValueOctaveBand8000Hz.text = df.format(linstValueOctaveBand8000Hz)
                binding.linstValueOctaveBand16000Hz.text = df.format(linstValueOctaveBand16000Hz)

                binding.calibrationValueOctaveBand125Hz.text = octaveBandCalibrationValues[0].toString()

                binding.progressbarOctaveBand125Hz.progress = (linstValueOctaveBand125Hz).toInt() + octaveBandCalibrationValues[0]
                updateUI = true
            }
        }
    }

    fun setOnClickListeners() {
        //TODO set button listeners for every button in every octave band
        binding.buttonUpOctaveBand125Hz.setOnClickListener {
            octaveBandCalibrationValues[0]++
        }

        binding.buttonDownOctaveBand125Hz.setOnClickListener {
            octaveBandCalibrationValues[0]--
        }
    }
}