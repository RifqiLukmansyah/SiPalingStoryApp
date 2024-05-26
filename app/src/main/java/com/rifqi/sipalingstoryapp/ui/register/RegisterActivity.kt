package com.rifqi.sipalingstoryapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rifqi.sipalingstoryapp.R
import com.rifqi.sipalingstoryapp.databinding.ActivityLoginBinding
import com.rifqi.sipalingstoryapp.databinding.ActivityRegisterBinding
import com.rifqi.sipalingstoryapp.preferences.ClientState
import com.rifqi.sipalingstoryapp.ui.customview.showError
import com.rifqi.sipalingstoryapp.ui.register.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.rifqi.sipalingstoryapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel  by viewModel<RegisterViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setRegisterBtn()
        setView()
        setPageLogin()
        applyAnimation()

    }

    private fun setRegisterBtn() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = edtName.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val pass = edtPassword.text.toString().trim()
                when {
                    name.isEmpty() -> {
                        binding.edtName.showError(getString(R.string.validation_must_not_empty))
                    }
                    email.isEmpty() -> {
                        binding.edtEmail.showError(getString(R.string.validation_must_not_empty))
                    }
                    pass.isEmpty() -> {
                        binding.edtPassword.showError(getString(R.string.validation_must_not_empty))
                    }
                    pass.length < 8 -> {
                        binding.edtPassword.showError(getString(R.string.validation_password))
                    }
                    else -> {
                        registerViewModel.performRegister(name, email, pass)
                    }
                }
            }
        }
    }

    private fun setView() {
        binding.apply {
            registerViewModel.register.observe(this@RegisterActivity) { resources ->
                when (resources) {
                    is ClientState.Success -> {
                        binding.loadingLayout.root.visibility = View.GONE
                        resources.data?.let { handleSuccess() }
                    }

                    is ClientState.Error -> {
                        binding.loadingLayout.root.visibility = View.GONE
                    }

                    is ClientState.Loading -> {
                        binding.loadingLayout.root.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun handleSuccess() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun setPageLogin() {
        binding.apply {
            toolBar.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun applyAnimation() {
        binding.apply {

            val registerTitle = tvRegisterTitle
            val tv1 = tvRegisterSubtitle
            val name = edtName
            val email = edtEmail
            val pass = edtPassword
            val btnRegister = btnRegister
            val tv2 = tvNameLabel
            val tv3 = tvEmailLabel
            val tv4 = tvPasswordLabel

            val anim1 = ObjectAnimator.ofFloat(registerTitle, View.TRANSLATION_Y, -600f, 0f)
            val anim2 = ObjectAnimator.ofFloat(tv1, View.TRANSLATION_Y, -600f, 0f)
            val anim3 = ObjectAnimator.ofFloat(name, View.TRANSLATION_Y, -600f, 0f)
            val anim4 = ObjectAnimator.ofFloat(email, View.TRANSLATION_Y, -600f, 0f)
            val anim5 = ObjectAnimator.ofFloat(pass, View.TRANSLATION_Y, -600f, 0f)
            val anim6 = ObjectAnimator.ofFloat(btnRegister, View.TRANSLATION_Y, -600f, 0f)
            val anim7 = ObjectAnimator.ofFloat(tv2, View.TRANSLATION_Y, -600f, 0f)
            val anim8 = ObjectAnimator.ofFloat(tv3, View.TRANSLATION_Y, -600f, 0f)
            val anim9 = ObjectAnimator.ofFloat(tv4, View.TRANSLATION_Y, -600f, 0f)

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


            val set = AnimatorSet()
            set.playTogether(
                anim1, anim2, anim3, anim4, anim5, anim6, anim7, anim8, anim9
            )
            set.start()
        }
    }


}