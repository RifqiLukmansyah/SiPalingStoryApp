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
import com.rifqi.sipalingstoryapp.databinding.ActivityRegisterBinding
import com.rifqi.sipalingstoryapp.preferences.ClientState
import com.rifqi.sipalingstoryapp.ui.register.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.rifqi.sipalingstoryapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel  by viewModel<RegisterViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                registerViewModel.performRegister(name, email, pass)
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
                        handleError(resources.message)
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

    private fun handleError(errorMSG: String?) {
        binding.apply {
            when {
                errorMSG?.contains("name") == true -> {
                    edtName.setNameError(errorMSG)
                }

                errorMSG?.contains("email") == true -> {
                    edtEmail.setEmailError(errorMSG)
                }

                errorMSG?.contains("password") == true -> {
                    edtPassword.setPassError(errorMSG)
                }

                else -> {
                    showToast("$errorMSG")
                }

            }
        }
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

            val imgView = ivRegister
            val tv1 = tv1Register
            val name = nameEdtRegister
            val email = emailEdtRegister
            val pass = passwordEdtRegister
            val btnRegister = btnSubmitRegister
            val tv2 = tv2Register
            val tvLogin = tvLoginHere

            val anim1 = ObjectAnimator.ofFloat(imgView, View.TRANSLATION_Y, -600f, 0f)
            val anim2 = ObjectAnimator.ofFloat(tv1, View.TRANSLATION_Y, -600f, 0f)
            val anim3 = ObjectAnimator.ofFloat(name, View.TRANSLATION_Y, -600f, 0f)
            val anim4 = ObjectAnimator.ofFloat(email, View.TRANSLATION_Y, -600f, 0f)
            val anim5 = ObjectAnimator.ofFloat(pass, View.TRANSLATION_Y, -600f, 0f)
            val anim6 = ObjectAnimator.ofFloat(btnRegister, View.TRANSLATION_Y, -600f, 0f)
            val anim7 = ObjectAnimator.ofFloat(tv2, View.TRANSLATION_Y, -600f, 0f)
            val anim8 = ObjectAnimator.ofFloat(tvLogin, View.TRANSLATION_Y, -600f, 0f)

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

            val set = AnimatorSet()
            set.playTogether(
                anim1, anim2, anim3, anim4, anim5, anim6, anim7, anim8
            )
            set.start()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}