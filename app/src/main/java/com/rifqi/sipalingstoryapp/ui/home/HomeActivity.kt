package com.rifqi.sipalingstoryapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rifqi.sipalingstoryapp.R
import com.rifqi.sipalingstoryapp.data.StoryAdapter
import com.rifqi.sipalingstoryapp.databinding.ActivityHomeBinding
import com.rifqi.sipalingstoryapp.preferences.ClientState
import com.rifqi.sipalingstoryapp.ui.location.LocationActivity
import com.rifqi.sipalingstoryapp.ui.setting.SettingActivity
import com.rifqi.sipalingstoryapp.ui.upload.UploadStoryActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewAdapter: StoryAdapter
    private val homeVM: HomeViewModel by viewModel<HomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showYesNoDialog(
                    title = getString(R.string.title_close_app),
                    message = getString(R.string.message_close_app),
                    onYes = {
                        closeApp()
                    }
                )
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        homeVM.getStories()
        setAdapter()
        setView()
        setPageUpload()
        onOptionsItemSelected()
    }
    private fun closeApp() {
        finishAffinity()
    }

    private fun showYesNoDialog(title: String, message: String, onYes: () -> Unit) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setNegativeButton(getString(R.string.label_no)) { p0, _ ->
                p0.dismiss()
            }
            setPositiveButton(getString(R.string.label_yes)) { _, _ ->
                onYes.invoke()
            }
        }.create().show()
    }


    private fun setAdapter() {
        binding.apply {
            viewAdapter = StoryAdapter(listOf())
            rvStory.layoutManager = LinearLayoutManager(this@HomeActivity)
            rvStory.adapter = viewAdapter
        }
    }

    private fun setView() {
        binding.apply {
            homeVM.stories.observe(this@HomeActivity) { resources ->
                when (resources) {
                    is ClientState.Success -> {
                        binding.loadingLayout.root.visibility = View.GONE
                        resources.data?.let { viewAdapter.updateItem(it) }
                    }

                    is ClientState.Error -> {
                        binding.loadingLayout.root.visibility = View.GONE
                        showToast("${resources.message}")
                    }

                    is ClientState.Loading -> {
                        binding.loadingLayout.root.visibility = View.VISIBLE
                    }

                    else -> {}
                }
            }
        }

    }



    private fun onOptionsItemSelected(){
        binding.apply {
            toolBar.apply {
                btnSetting.setOnClickListener {
                    val intent = Intent(this@HomeActivity, SettingActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                btnLocation.setOnClickListener {
                    val intent = Intent(this@HomeActivity, LocationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
    private fun setPageUpload() {
        binding.apply {
            fabAddStory.setOnClickListener {
                val intent = Intent(this@HomeActivity, UploadStoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@HomeActivity, message, Toast.LENGTH_LONG).show()
        Log.e("SA-HA Toast", message)
    }

}