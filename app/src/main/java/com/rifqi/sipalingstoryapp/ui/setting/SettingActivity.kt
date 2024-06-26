package com.rifqi.sipalingstoryapp.ui.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rifqi.sipalingstoryapp.databinding.ActivitySettingBinding
import com.rifqi.sipalingstoryapp.preferences.UserPreferences
import com.rifqi.sipalingstoryapp.ui.home.HomeActivity
import com.rifqi.sipalingstoryapp.ui.login.LoginActivity
import kotlinx.coroutines.launch

@Suppress("UNUSED_EXPRESSION")
class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun    onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@SettingActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        logout()
        language()
        setPageHome()

    }
    private fun setPageHome() {
        binding.apply {
            toolBar.setOnClickListener {
                val intent = Intent(this@SettingActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun language() {
        binding.apply {
            btnChangeLanguange.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
        }
    }

    private fun logout() {
        binding.apply {
            btnlogout.setOnClickListener {
                lifecycleScope.launch {
                    val tokenManager = UserPreferences.getInstance(this@SettingActivity)
                    tokenManager.clearTokenAndSession()
                    val intent = Intent(this@SettingActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                true
            }
        }
    }
}