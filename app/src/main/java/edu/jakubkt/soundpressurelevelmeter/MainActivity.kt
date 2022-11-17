package edu.jakubkt.soundpressurelevelmeter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.preference.PreferenceManager
import edu.jakubkt.soundpressurelevelmeter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object AppConstants {
        // TODO move constants to a different file
        // requestCode for permissions
        const val REQUEST_CODE_MICROPHONE: Int = 1
        const val SAMPLE_RATE: Int = 44100
        // buffer containing 125 milliseconds of audio data
        const val AUDIO_BUFFER_SIZE: Int = 5513
        // for passing settings data between activities
        const val EXTRA_WINDOW_TYPE: String = "EXTRA_WINDOW_TYPE"
        const val EXTRA_WEIGHTINGS_TYPE: String = "EXTRA_WEIGHTINGS_TYPE"
    }
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
            Intent(this, CalibrationActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun retrieveSettings(): Array<String?> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val windowType: String? = preferences.getString("window", "hann")
        val weightingsType: String? = preferences.getString("weightings", "a")

        return arrayOf(windowType, weightingsType)
    }
}
