package com.rifqi.sipalingstoryapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private val loginViewModel: LoginViewModel by viewModel<LoginViewModel>()

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
            val imgView = ivLogo
            val tv1 = tvLoginTitle
            val tv2 = tvLoginSubtitle
            val tv3 = tvEmailLabel
            val tv4 = tvPasswordLabel
            val email = edtEmail
            val pass = edtPassword
            val btnLogin = btnLogin
            val tv5 = tvRegisterHere
            val tvRegister = btnRegister

            val anim1 = ObjectAnimator.ofFloat(imgView, View.TRANSLATION_Y, -600f, 0f)
            val anim2 = ObjectAnimator.ofFloat(tv1, View.TRANSLATION_Y, -600f, 0f)
            val anim3 = ObjectAnimator.ofFloat(tv2, View.TRANSLATION_Y, -600f, 0f)
            val anim4 = ObjectAnimator.ofFloat(tv3, View.TRANSLATION_Y, -600f, 0f)
            val anim5 = ObjectAnimator.ofFloat(tv4, View.TRANSLATION_Y, -600f, 0f)
            val anim6 = ObjectAnimator.ofFloat(email, View.TRANSLATION_Y, -600f, 0f)
            val anim7 = ObjectAnimator.ofFloat(pass, View.TRANSLATION_Y, -600f, 0f)
            val anim8 = ObjectAnimator.ofFloat(btnLogin, View.TRANSLATION_Y, -600f, 0f)
            val anim9 = ObjectAnimator.ofFloat(tv5, View.TRANSLATION_Y, -600f, 0f)
            val anim10 = ObjectAnimator.ofFloat(tvRegister, View.TRANSLATION_Y, -600f, 0f)


            val duration = 3000L
            val interpolator = DecelerateInterpolator()

            anim1.duration = duration
            anim1.interpolator = interpolator

            anim2.duration = duration
            anim2.interpolator = interpolator

            anim3.duration = duration
            anim3.interpolator = interpolator

            anim4.duration = duration
            anim4.interpolator = interpolator

            anim5.duration = duration
            anim5.interpolator = interpolator

            anim6.duration = duration
            anim6.interpolator = interpolator

            anim7.duration = duration
            anim7.interpolator = interpolator

            anim8.duration = duration
            anim8.interpolator = interpolator

            anim9.duration = duration
            anim9.interpolator = interpolator

            anim10.duration = duration
            anim10.interpolator = interpolator

            val set = AnimatorSet()
            set.playTogether(
                anim1, anim2, anim3, anim4, anim5, anim6, anim7, anim8, anim9, anim10
            )
            set.start()

        }
    }



    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}