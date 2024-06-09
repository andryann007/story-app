package com.andryan.storyapp.ui.activity.maps

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.R

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.andryan.storyapp.databinding.ActivityMapsBinding
import com.andryan.storyapp.utils.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalPagingApi
@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private val viewModel: MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBar(binding.toolbarMaps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
        getStoriesWithLocation()
    }

    private val requestPermissionLauncher =
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
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Timber.tag(TAG).d("Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Timber.tag(TAG).d("Map style: %s", exception.toString())
        }
    }

    private fun getStoriesWithLocation() {
        lifecycleScope.launch {
            val token = viewModel.getSession().first().token

            viewModel.getStoriesWithLocation(token).observe(this@MapsActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        setLoadingState(true)
                    }

                    is Result.Success -> {
                        setLoadingState(false)

                        val stories = result.data.listStory

                        stories.forEach { story ->
                            val lat: Double = story.lat ?: 0.0
                            val lon: Double = story.lon ?: 0.0
                            val latLng = LatLng(lat, lon)

                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(story.name)
                                    .snippet(story.description)
                                    .icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_BLUE
                                        )
                                    )
                            )
                            boundsBuilder.include(latLng)
                        }

                        val bounds: LatLngBounds = boundsBuilder.build()
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                resources.displayMetrics.widthPixels,
                                resources.displayMetrics.heightPixels,
                                300
                            )
                        )
                    }

                    is Result.Error -> {
                        setLoadingState(false)

                        binding.root.let {
                            Snackbar.make(
                                it,
                                result.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        Timber.tag(TAG).d("getStoriesWithLocation: %s", result.error)
                    }
                }
            }
        }
    }

    private fun setActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = resources.getString(R.string.title_activity_maps)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.progressbarMaps.visibility = View.VISIBLE
            binding.map.visibility = View.GONE
        } else {
            binding.progressbarMaps.visibility = View.GONE
            binding.map.visibility = View.VISIBLE
        }
    }
}