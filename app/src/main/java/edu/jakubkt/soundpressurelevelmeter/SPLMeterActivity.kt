package edu.jakubkt.soundpressurelevelmeter

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast

import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.REQUEST_CODE_MICROPHONE
import edu.jakubkt.soundpressurelevelmeter.databinding.ActivitySplmeterBinding
import edu.jakubkt.soundpressurelevelmeter.logic.AudioBufferProcessing
import edu.jakubkt.soundpressurelevelmeter.logic.MicrophoneRecorder

class SPLMeterActivity : AppCompatActivity(), AudioBufferProcessing {
    private lateinit var binding: ActivitySplmeterBinding
    private lateinit var recorder: MicrophoneRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplmeterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.SPLMeterToolbar)

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
        //TODO("Not yet implemented")
    }
}