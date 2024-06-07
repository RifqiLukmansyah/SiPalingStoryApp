package com.rifqi.sipalingstoryapp.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rifqi.sipalingstoryapp.R
import com.rifqi.sipalingstoryapp.data.adapter.PagingAdapter
import com.rifqi.sipalingstoryapp.data.api.ApiConfig
import com.rifqi.sipalingstoryapp.databinding.ActivityHomeBinding
import com.rifqi.sipalingstoryapp.preferences.UserPreferences
import com.rifqi.sipalingstoryapp.ui.maps.MapActivity
import com.rifqi.sipalingstoryapp.ui.setting.SettingActivity
import com.rifqi.sipalingstoryapp.ui.upload.UploadStoryActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var pagingAdapter: PagingAdapter
    private val homeVM: HomeViewModel by viewModel<HomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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


        setAdapter()
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
            pagingAdapter = PagingAdapter()
            rvStory.layoutManager = LinearLayoutManager(this@HomeActivity)
            rvStory.adapter = pagingAdapter
        }
        homeVM.story.observe(this@HomeActivity) {
            lifecycleScope.launch {
                val tManager = UserPreferences.getInstance(this@HomeActivity).getToken() ?: ""
                ApiConfig.setAuthToken(tManager)
                pagingAdapter.submitData(lifecycle, it)
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
                    val intent = Intent(this@HomeActivity, MapActivity::class.java)
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

}