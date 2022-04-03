package edu.jakubkt.soundpressurelevelmeter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import edu.jakubkt.soundpressurelevelmeter.databinding.ActivitySplgraphBinding

class SPLGraphActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplgraphBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplgraphBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.SPLGraphToolbar)
    }
    /*
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return false
    }
    */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.splgraph_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_return_to_main_menu -> {
                finish()
                return true
            }
            R.id.action_view_screenshots -> {
                Toast.makeText(applicationContext, "Work in progress", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}