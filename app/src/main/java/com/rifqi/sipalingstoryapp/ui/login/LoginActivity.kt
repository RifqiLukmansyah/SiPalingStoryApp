package com.rifqi.sipalingstoryapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.rifqi.sipalingstoryapp.R
import com.rifqi.sipalingstoryapp.data.api.ApiConfig
import com.rifqi.sipalingstoryapp.data.model.LoginResult
import com.rifqi.sipalingstoryapp.databinding.ActivityLoginBinding
import com.rifqi.sipalingstoryapp.preferences.ClientState
import com.rifqi.sipalingstoryapp.preferences.UserPreferences
import com.rifqi.sipalingstoryapp.ui.customview.showError
import com.rifqi.sipalingstoryapp.ui.home.HomeActivity
import com.rifqi.sipalingstoryapp.ui.register.RegisterActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setPageRegister()
        setLoginBtn()
        setView()
        applyAnimation()
    }

    override fun onResume() {
        super.onResume()
        checkSession()
    }

    private fun checkSession() {
        lifecycleScope.launch {
            val userPreferences = UserPreferences.getInstance(this@LoginActivity)
            val (token, session) = userPreferences.getTokenAndSession()
            if (token != null && session == true) {
                ApiConfig.setAuthToken(token)
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setLoginBtn() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = binding.edtEmail.text.toString()
                val password = binding.edtPassword.text.toString()
                when {
                    email.isEmpty() -> {
                        binding.edtEmail.showError(getString(R.string.validation_must_not_empty))
                    }
                    password.isEmpty() -> {
                        binding.edtPassword.showError(getString(R.string.validation_must_not_empty))
                    }
                    password.length < 8 -> {
                        binding.edtPassword.showError(getString(R.string.validation_password))
                    }
                    else -> {
                        loginViewModel.performLogin(email, password)
                    }
                }
            }
        }
    }

    private fun setView() {
        binding.apply {
            loginViewModel.loginResult.observe(this@LoginActivity) { resources ->
                when (resources) {
                    is ClientState.Success -> {
                        binding.loadingLayout.root.visibility = View.GONE
                        resources.data?.let { handleLoginSuccess(it) }
                    }
                    is ClientState.Error -> {
                        binding.loadingLayout.root.visibility = View.GONE
                    }
                    is ClientState.Loading -> {
                        binding.loadingLayout.root.visibility = View.VISIBLE
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleLoginSuccess(loginResult: LoginResult) {
        lifecycleScope.launch {
            val auth = loginResult.token
            val tokenManager = UserPreferences.getInstance(this@LoginActivity)
            tokenManager.saveTokenAndSession(auth, true)
            ApiConfig.setAuthToken(auth)

            val (token) = tokenManager.getTokenAndSession()
            if (token != null) {
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                showToast("Session has expired")
            }
        }
    }

    private fun setPageRegister() {
        binding.apply {
            btnRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun applyAnimation() {
        binding.apply {
            val duration = 3000L
            val interpolator = DecelerateInterpolator()

            val animations = listOf(
                ObjectAnimator.ofFloat(ivLogo, View.TRANSLATION_Y, -600f, 0f),
                ObjectAnimator.ofFloat(tvLoginTitle, View.TRANSLATION_Y, -600f, 0f),
                ObjectAnimator.ofFloat(tvLoginSubtitle, View.TRANSLATION_Y, -600f, 0f),
                ObjectAnimator.ofFloat(tvEmailLabel, View.TRANSLATION_Y, -600f, 0f),
                ObjectAnimator.ofFloat(tvPasswordLabel, View.TRANSLATION_Y, -600f, 0f),
                ObjectAnimator.ofFloat(edtEmail, View.TRANSLATION_Y, -600f, 0f),
                ObjectAnimator.ofFloat(edtPassword, View.TRANSLATION_Y, -600f, 0f),
                ObjectAnimator.ofFloat(btnLogin, View.TRANSLATION_Y, -600f, 0f),
                ObjectAnimator.ofFloat(tvRegisterHere, View.TRANSLATION_Y, -600f, 0f),
                ObjectAnimator.ofFloat(btnRegister, View.TRANSLATION_Y, -600f, 0f)
            ).onEach {
                it.duration = duration
                it.interpolator = interpolator
            }

            AnimatorSet().apply {
                playTogether(animations)
                start()
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
