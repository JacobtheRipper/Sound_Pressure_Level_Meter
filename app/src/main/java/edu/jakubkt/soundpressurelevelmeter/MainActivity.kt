package edu.jakubkt.soundpressurelevelmeter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import edu.jakubkt.soundpressurelevelmeter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
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

    private fun setOnClickListeners() {
        binding.mainMenuLayout.buttonSplGraph.setOnClickListener {
            Intent(this, SPLGraphActivity::class.java).also {
                startActivity(it)
            }
        }
        //TODO do the same for all buttons in the main menu layout
    }
}