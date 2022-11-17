package edu.jakubkt.soundpressurelevelmeter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import edu.jakubkt.soundpressurelevelmeter.databinding.ActivityCalibrationBinding

class CalibrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalibrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalibrationBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.CalibrationToolbar)
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
}