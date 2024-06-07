package com.rifqi.sipalingstoryapp.ui.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rifqi.sipalingstoryapp.BuildConfig
import com.rifqi.sipalingstoryapp.R
import com.rifqi.sipalingstoryapp.databinding.ActivityUploadStoryBinding
import com.rifqi.sipalingstoryapp.preferences.ClientState
import com.rifqi.sipalingstoryapp.ui.home.HomeActivity
import kotlinx.coroutines.launch
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private val uploadVM: UploadViewModel by viewModel()
    private var currentImage: Uri? = null
    private val cameracode = 100
    private val locationCode = 101
    private var allowLocation: Boolean = false
    private lateinit var fusedLP: FusedLocationProviderClient

    companion object {
        private const val MAX_IMAGE_SIZE = 1000000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLP = LocationServices.getFusedLocationProviderClient(this)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UploadStoryActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        requestPermissions()
        switchLocations()
        setInsets()
        btnLocation()
        setPageHome()
        setView()
        btnOpenpicture()
        btnUpload()
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), cameracode)
        }
    }

    private fun btnLocation() {
        binding.btnGetLocation.setOnClickListener {
            lifecycleScope.launch {
                checkLocationPermissionAndGetLocation()
            }
        }
    }

    private suspend fun checkLocationPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@UploadStoryActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationCode
            )
        } else {
            getMyLocation()?.let { location ->
                settextlocation(location)
            }
        }
    }

    private suspend fun getMyLocation(): Location? = suspendCoroutine { continuation ->
        if (ActivityCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationCode
            )
            continuation.resume(null)
        } else {
            fusedLP.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    continuation.resume(null)
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    private fun settextlocation(location: Location) {
        binding.apply {
            tvLatitude.text = location.latitude.toString()
            tvLongitude.text = location.longitude.toString()
        }
    }

    private fun switchLocations() {
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                allowLocation = true
                binding.layoutPosition.visibility = View.VISIBLE
            } else {
                allowLocation = false
                binding.layoutPosition.visibility = View.GONE
            }
        }
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
            showGalleryCameraDialog()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameracode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            cameracode -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                } else {
                    showToast(getString(R.string.permission_denied))
                }
            }
            locationCode -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    lifecycleScope.launch {
                        getMyLocation()?.let { location ->
                            settextlocation(location)
                        }
                    }
                } else {
                    showToast(getString(R.string.permission_denied))
                }
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
                lifecycleScope.launch {
                    val location = getMyLocation()
                    val fileImage = File(getPathImage(uri))
                    val desc = binding.edtDescription.text.toString()

                    if (fileImage.exists()) {
                        val compressedFile = compressImageFile(fileImage)
                        val photoRB =
                            compressedFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val descPart = desc.toRequestBody("text/plain".toMediaTypeOrNull())

                        val photoPart = MultipartBody.Part.createFormData(
                            "photo",
                            compressedFile.name,
                            photoRB
                        )
                        val lat = location?.latitude ?: 0.0
                        val lon = location?.longitude ?: 0.0

                        uploadVM.uploadStory(photoPart, descPart, lat, lon)
                    }
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
