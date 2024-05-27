package com.rifqi.sipalingstoryapp.ui.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.rifqi.sipalingstoryapp.R
import com.rifqi.sipalingstoryapp.data.adapter.getTimeAgo
import com.rifqi.sipalingstoryapp.data.model.Story
import com.rifqi.sipalingstoryapp.databinding.ActivityDetailBinding
import com.rifqi.sipalingstoryapp.preferences.ClientState
import com.rifqi.sipalingstoryapp.ui.home.HomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailVM: DetailViewModel by viewModel<DetailViewModel>()
    private lateinit var storyId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val story: Story? = intent.getParcelableExtra("user")
        if (story != null) {
            storyId = story.id
            handleDetail(story)
        }
        setPageHome()
        detailVM.detail(storyId)

        setView()

       applyTransition()

    }
    private fun setPageHome() {
        binding.apply {
            toolBar.setOnClickListener {
                val intent = Intent(this@DetailActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setView() {
        binding.apply {
            detailVM.detail.observe(this@DetailActivity) { resources ->
                when (resources) {
                    is ClientState.Success -> {
                        binding.loadingLayout.root.visibility = View.GONE
                        resources.data?.let { handleDetail(it) }
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

    private fun handleDetail(detailView: Story) {
        binding.apply {
            tvUserName.text = detailView.name
            tvDescription.text = detailView.description
            tvUploadAt.text = detailView.createdAt.getTimeAgo(root.context)

            Glide.with(root)
                .load(detailView.photoUrl)
                .into(imgStory)

        }
    }

    private fun applyTransition() {
        binding.apply {
            val imageStory = imgStory.setAlphaAnimation(500L)
            val imageUser = imgUser.setAlphaAnimation(300L)
            val textUsername = tvUserName.setAlphaAnimation(300L)
            val textUploadAt = tvUploadAt.setAlphaAnimation(300L)
            val textDescription = tvDescription.setAlphaAnimation(500L)

            val together = AnimatorSet().apply {
                playTogether( textDescription)
            }

            AnimatorSet().apply {
                playSequentially(imageUser, textUsername, textUploadAt, imageStory, together)
                start()
            }
        }
    }
    private fun View.setAlphaAnimation(animationSpeed: Long): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, View.ALPHA, 1f)
            .setDuration(animationSpeed)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@DetailActivity, message, Toast.LENGTH_LONG).show()
        Log.e("SA-DA Toast", message)
    }
}