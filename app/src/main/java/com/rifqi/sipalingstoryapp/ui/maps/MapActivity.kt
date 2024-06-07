package com.rifqi.sipalingstoryapp.ui.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rifqi.sipalingstoryapp.R
import com.rifqi.sipalingstoryapp.data.model.Story
import com.rifqi.sipalingstoryapp.databinding.ActivityMapBinding
import com.rifqi.sipalingstoryapp.preferences.ClientState
import com.rifqi.sipalingstoryapp.ui.home.HomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding

    private val mapsviewmodel: MapsViewModel by viewModel<MapsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@MapActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)




        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setPageHome()

    }
    private fun setPageHome() {
        binding.apply {
            toolBar.setOnClickListener {
                val intent = Intent(this@MapActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        setView()
        getMyLocation()

    }

    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermission.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun setView() {
        binding.apply {
            mapsviewmodel.map.observe(this@MapActivity) { resources ->
                when (resources) {
                    is ClientState.Success -> {
                        binding.loadingLayout.root.visibility = View.GONE
                        resources.data?.forEach { handleSuccess(it) }

                    }

                    is ClientState.Error -> {
                        binding.loadingLayout.root.visibility = View.GONE
                        showToast("${resources.message}")
                    }

                    is ClientState.Loading -> {
                        binding.loadingLayout.root.visibility = View.VISIBLE
                    }

                    else -> {0}
                }
            }

            mapsviewmodel.mapStory()
        }
    }

    private fun handleSuccess(story: Story) {

        val location = LatLng(story.lat, story.lon)
        mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(story.name)
                .snippet(story.description)
        )

        mMap.moveCamera(
            CameraUpdateFactory.newLatLng(
                location
            )
        )

    }



    private fun showToast(message: String) {
        Toast.makeText(this@MapActivity, message, Toast.LENGTH_LONG).show()
    }

}