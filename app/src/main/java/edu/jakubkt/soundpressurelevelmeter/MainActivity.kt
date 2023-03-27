package edu.jakubkt.soundpressurelevelmeter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.preference.PreferenceManager
import edu.jakubkt.soundpressurelevelmeter.databinding.ActivityMainBinding
import org.apache.commons.math3.util.ArithmeticUtils.pow

class MainActivity : AppCompatActivity() {

    companion object AppConstants {
        // requestCode for permissions
        const val REQUEST_CODE_MICROPHONE: Int = 1
        const val SAMPLE_RATE: Int = 44100
        // buffer containing 125 milliseconds of audio data
        const val AUDIO_BUFFER_SIZE: Int = 5513
        // for passing settings data between activities
        const val EXTRA_WINDOW_TYPE: String = "EXTRA_WINDOW_TYPE"
        const val EXTRA_WEIGHTINGS_TYPE: String = "EXTRA_WEIGHTINGS_TYPE"
        const val EXTRA_CALIBRATION_VALUES: String = "EXTRA_CALIBRATION_VALUES"
    }

    private val TAG: String = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setOnClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_access_screenshots -> {
                Toast.makeText(applicationContext, R.string.placeholder_string, Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_access_settings_activity -> {
                Intent(this, SettingsActivity::class.java).also {
                    startActivity(it)
                }
                return true
            }
            R.id.action_access_help_activity -> {
                Toast.makeText(applicationContext, R.string.placeholder_string, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setOnClickListeners() {
        // Switch to a different activity after pressing a button
        binding.mainMenuLayout.buttonSplGraph.setOnClickListener {
            Intent(this, SPLGraphActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.mainMenuLayout.buttonSplMeter.setOnClickListener {
            val currentSettings = retrieveSettings()
            val windowType = currentSettings[0]
            val weightingsType = currentSettings[1]
            Intent(this, SPLMeterActivity::class.java).also {
                it.putExtra(EXTRA_WINDOW_TYPE, windowType)
                it.putExtra(EXTRA_WEIGHTINGS_TYPE, weightingsType)
                startActivity(it)
            }
        }

        binding.mainMenuLayout.buttonRawDataGraph.setOnClickListener {
            Intent(this, RawDataActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.mainMenuLayout.buttonCalibration.setOnClickListener {
            val currentSettings = retrieveSettings()
            val calibrationValues = IntArray(8) {0}
            for (i in 2..9)
                calibrationValues[i-2] = currentSettings[i].toInt()

            for ((index, value) in calibrationValues.withIndex())
                Log.d(TAG, "Calibration value being sent for ${pow(2, index)*125} Hz: $value")

            Intent(this, CalibrationActivity::class.java).also {
                it.putExtra(EXTRA_CALIBRATION_VALUES, calibrationValues)
                startActivity(it)
            }
        }
    }

    private fun retrieveSettings(): Array<String> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val windowType: String = preferences.getString("window", "hann").toString()
        val weightingsType: String = preferences.getString("weightings", "a").toString()
        val calibrationValues: Array<String> = Array(8) {"0"}

        calibrationValues[0] = preferences.getString("125Hz", "0").toString()
        calibrationValues[1] = preferences.getString("250Hz", "0").toString()
        calibrationValues[2] = preferences.getString("500Hz", "0").toString()
        calibrationValues[3] = preferences.getString("1000Hz", "0").toString()
        calibrationValues[4] = preferences.getString("2000Hz", "0").toString()
        calibrationValues[5] = preferences.getString("4000Hz", "0").toString()
        calibrationValues[6] = preferences.getString("8000Hz", "0").toString()
        calibrationValues[7] = preferences.getString("16000Hz", "0").toString()

        for ((index, value) in calibrationValues.withIndex())
            Log.d(TAG, "Calibration value received for ${pow(2, index)*125} Hz: $value")

        // Multiple return types are not possible in JVM
        return arrayOf(windowType, weightingsType, calibrationValues[0], calibrationValues[1],
            calibrationValues[2], calibrationValues[3], calibrationValues[4], calibrationValues[5],
            calibrationValues[6], calibrationValues[7])
    }
}
