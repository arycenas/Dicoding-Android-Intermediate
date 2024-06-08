package com.intermediate.storyapp.view.maps

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.intermediate.storyapp.R
import com.intermediate.storyapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val materialToolbar = findViewById<MaterialToolbar>(R.id.menubar)
        materialToolbar.setNavigationOnClickListener {
            finish()
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
            if (!success) {
                Log.e("Maps", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("Maps", "Can't find style. Error: ", e)
        }

        setupViewModel()
    }

    private fun showError(error: String) {
        binding.error.visibility = View.VISIBLE
        binding.error.text = error
    }

    private fun setupViewModel() {
        viewModel.getMapsStories().observe(this) { listMap ->
            if (listMap != null) {
                viewModel.error.observe(this) {
                    if (it == false) {
                        listMap.forEach { data ->
                            val latLng = LatLng(data.lat!!, data.lon!!)
                            mMap.addMarker(
                                MarkerOptions().position(latLng).title(data.name)
                                    .snippet(data.description)
                            )
                            boundsBuilder.include(latLng)
                        }
                        val bounds: LatLngBounds = boundsBuilder.build()
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                resources.displayMetrics.widthPixels,
                                resources.displayMetrics.heightPixels,
                                0
                            )
                        )
                    } else {
                        val supportMapFragment =
                            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        supportMapFragment.view?.visibility = View.GONE
                        viewModel.message.observe(this) { message ->
                            showError(message.toString())
                        }
                    }
                }
            }
        }
    }
}