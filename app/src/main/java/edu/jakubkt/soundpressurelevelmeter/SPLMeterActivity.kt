package edu.jakubkt.soundpressurelevelmeter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat

import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.REQUEST_CODE_MICROPHONE
import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.REQUEST_CODE_EXTERNAL_STORAGE
import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.EXTRA_WEIGHTINGS_TYPE
import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.EXTRA_WINDOW_TYPE
import edu.jakubkt.soundpressurelevelmeter.MainActivity.AppConstants.EXTRA_CALIBRATION_VALUES

import edu.jakubkt.soundpressurelevelmeter.databinding.ActivitySplmeterBinding
import edu.jakubkt.soundpressurelevelmeter.logic.AudioBufferProcessing
import edu.jakubkt.soundpressurelevelmeter.logic.MicrophoneRecorder
import edu.jakubkt.soundpressurelevelmeter.logic.SPLCalculations

import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Date
import java.io.File
import java.io.FileOutputStream

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
                takeScreenshot(applicationContext)
                Toast.makeText(applicationContext, R.string.screenshot_captured, Toast.LENGTH_SHORT).show()
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
        val permissionGranted: Boolean = if (requestCode == REQUEST_CODE_MICROPHONE || requestCode == REQUEST_CODE_EXTERNAL_STORAGE) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        // a message explaining the need to use the microphone or writing to storage would be preferred
        if(!permissionGranted) finish()
    }

    override fun processAudioBuffer(audioBuffer: ShortArray?) {
        if(updateUI) {
            updateUI = false
            val linstFieldValue: Double = calculation.calculateLinst(windowType, weightingsType, audioBuffer)
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

    private fun takeScreenshot(context: Context) {
        // Request user permission to write image files to external storage
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_EXTERNAL_STORAGE)
        // Set date format
        val currentDate = Date()
        android.text.format.DateFormat.format("dd-MM-yyyy_hh:mm:ss", currentDate)

        try {
            val screenShotPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            screenShotPath.mkdirs()
            // Current screen capture
            val activityCurrentView = window.decorView.rootView
            activityCurrentView.isDrawingCacheEnabled = true
            val viewBitmap = Bitmap.createBitmap(activityCurrentView.drawingCache)
            activityCurrentView.isDrawingCacheEnabled = false

            // Save image to file
            //TODO: Correct the filepath - the *.png extension returns a subfolder
            val imageFile = File(screenShotPath, "${resources.getString(R.string.app_name)}/$currentDate.png")
            imageFile.mkdirs()
            val fileOutputStream = FileOutputStream(imageFile)
            viewBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }
}
