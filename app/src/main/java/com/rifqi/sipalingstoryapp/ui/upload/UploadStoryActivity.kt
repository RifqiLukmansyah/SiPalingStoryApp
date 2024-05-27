package com.rifqi.sipalingstoryapp.ui.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rifqi.sipalingstoryapp.BuildConfig
import com.rifqi.sipalingstoryapp.R
import com.rifqi.sipalingstoryapp.databinding.ActivityUploadStoryBinding
import com.rifqi.sipalingstoryapp.preferences.ClientState
import com.rifqi.sipalingstoryapp.ui.home.HomeActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private val uploadVM: UploadViewModel by viewModel()
    private var currentImage: Uri? = null
    private val CAMERA_REQUEST_CODE = 100

    companion object {
        private const val MAX_IMAGE_SIZE = 1000000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInsets()
        setPageHome()
        setView()
        btnOpenpicture()
        btnUpload()
    }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun btnOpenpicture() {
        binding.imgStoryHolder.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            showGalleryCameraDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                showGalleryCameraDialog()
            } else {
                showToast(getString(R.string.permission_denied))
            }
        }
    }

    private fun showGalleryCameraDialog() {
        val options = arrayOf(getString(R.string.take_photo), getString(R.string.choose_from_gallery))
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.label_choose_image_picker_method))
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showCamera()
                    1 -> showGallery()
                }
                dialog.dismiss()
            }
            .show()
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentImage?.let { uri ->
                binding.imgStoryHolder.setImageURI(uri)
            }
        } else {
            showToast(getString(R.string.no_media_captured))
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentImage = it
            binding.imgStoryHolder.setImageURI(it)
        }
    }

    private lateinit var photoUri: Uri

    private fun showCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", photoFile)
        currentImage = photoUri
        takePictureLauncher.launch(photoUri)
    }

    private fun showGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    private fun setPageHome() {
        binding.toolbar.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }
    }

    private fun setView() {
        uploadVM.photoPath.observe(this) {
            binding.imgStoryHolder.setImageURI(it)
            currentImage = it
        }
        uploadVM.addStory.observe(this) { resources ->
            when (resources) {
                is ClientState.Success -> {
                    binding.loadingLayout.root.visibility = View.GONE
                    showToast(resources.data?.message.orEmpty())
                    directToHome()
                }
                is ClientState.Error -> {
                    binding.loadingLayout.root.visibility = View.GONE
                    handleError(resources.message)
                    showToast(resources.message.orEmpty())
                }
                is ClientState.Loading -> {
                    binding.loadingLayout.root.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun directToHome() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    private fun handleError(errorMsg: String?) {
        showToast(errorMsg ?: getString(R.string.unknown_error))
    }

    private fun btnUpload() {
        binding.btnUploadStory.setOnClickListener {
            currentImage?.let { uri ->
                val fileImage = File(getPathImage(uri))
                val desc = binding.edtDescription.text.toString()

                if (fileImage.exists()) {
                    val compressedFile = compressImageFile(fileImage)
                    val photoRB = compressedFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val descPart = desc.toRequestBody("text/plain".toMediaTypeOrNull())

                    val photoPart = MultipartBody.Part.createFormData(
                        "photo",
                        compressedFile.name,
                        photoRB
                    )

                    uploadVM.uploadStory(photoPart, descPart)
                } else {
                    showToast("Please take a picture")
                }
            } ?: showToast(getString(R.string.no_media_selected))
        }
    }

    private fun compressImageFile(imageFile: File): File {
        var compressQuality = 100
        var streamLength: Int
        val bitmap = BitmapFactory.decodeFile(imageFile.path)
        do {
            val bmpStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            bmpStream.flush()
            bmpStream.close()
            streamLength = imageFile.length().toInt()
            compressQuality -= 5
        } while (streamLength > MAX_IMAGE_SIZE)
        return imageFile
    }

    @SuppressLint("Recycle")
    private fun getPathImage(contentUri: Uri): String {
        val inputStream = contentResolver.openInputStream(contentUri)
        val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile.absolutePath
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
