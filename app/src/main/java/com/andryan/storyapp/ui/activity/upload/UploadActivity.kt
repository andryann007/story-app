package com.andryan.storyapp.ui.activity.upload

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.R
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.databinding.ActivityUploadBinding
import com.andryan.storyapp.ui.activity.main.MainActivity
import com.andryan.storyapp.utils.getImageUri
import com.andryan.storyapp.utils.reduceFileImage
import com.andryan.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

@ExperimentalPagingApi
@AndroidEntryPoint
class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val viewModel: UploadViewModel by viewModels()

    private var currentImageUri: Uri? = null
    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setActionBar(binding.toolbarUpload)

        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.edAddDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setButtonState()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setButtonState()
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        binding.buttonGallery.setOnClickListener {
            startGallery()
        }

        binding.buttonCamera.setOnClickListener {
            startCamera()
        }

        binding.buttonAdd.setOnClickListener {
            uploadStory()
        }
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getCurrentLocation()
            } else {
                this.location = null
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                this,
                resources.getString(R.string.permission_granted_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.permission_denied_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION,
        ) == PackageManager.PERMISSION_GRANTED

    private fun startGallery() {
        launcherIntentGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun descriptionValidation(editText: EditText): Boolean {
        val text = editText.text.toString().trim()

        return text.isNotEmpty()
    }

    private fun setButtonState() {
        val isValid: Boolean = descriptionValidation(binding.edAddDescription)

        if (!isValid) {
            binding.tilDescription.error = resources.getString(R.string.description_empty_error)
            binding.tilDescription.requestFocus()

            binding.buttonAdd.isEnabled = false
            binding.buttonAdd.background =
                ContextCompat.getDrawable(this, R.drawable.bg_button_disable)
            binding.buttonAdd.text = resources.getString(R.string.text_fill_data)
        } else {
            binding.tilDescription.error = null

            binding.buttonAdd.isEnabled = true
            binding.buttonAdd.background =
                ContextCompat.getDrawable(this, R.drawable.bg_button)
            binding.buttonAdd.text = resources.getString(R.string.text_upload)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Timber.tag("Photo Picker").d(resources.getString(R.string.gallery_error_message))
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let {
            launcherIntentCamera.launch(it)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        binding.lottieImageUpload.visibility = View.GONE

        currentImageUri?.let {
            Timber.tag("Image URI").d("showImage: $it")
            binding.ivPreviewItem.setImageURI(it)
        }
    }

    private fun uploadStory() {
        currentImageUri?.let {
            val photoFile = uriToFile(it, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString().trim()
            val isError: Boolean

            if (description.isEmpty()) {
                binding.edAddDescription.error =
                    resources.getString(R.string.description_empty_error)
                isError = true
            } else {
                isError = false
            }

            if (!isError) {
                val requestPhotoFile = photoFile.asRequestBody(
                    resources.getString(R.string.photo_format).toMediaType()
                )
                val storyPhoto: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    photoFile.name,
                    requestPhotoFile
                )
                val storyDescription = description.toRequestBody(
                    resources.getString(R.string.text_format).toMediaType()
                )

                var lat: RequestBody? = null
                var lon: RequestBody? = null

                if (location != null) {
                    lat = location?.latitude.toString()
                        .toRequestBody(resources.getString(R.string.text_format).toMediaType())
                    lon = location?.longitude.toString()
                        .toRequestBody(resources.getString(R.string.text_format).toMediaType())
                }

                lifecycleScope.launch {
                    val token = viewModel.getSession().first().token

                    viewModel.uploadStory(token, storyPhoto, storyDescription, lat, lon)
                        .observe(this@UploadActivity) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    setLoadingState(true)
                                }

                                is Result.Success -> {
                                    setLoadingState(false)

                                    Toast.makeText(
                                        this@UploadActivity,
                                        resources.getString(R.string.upload_success_message),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    Handler(Looper.getMainLooper()).postDelayed({
                                        startActivity(
                                            Intent(
                                                this@UploadActivity,
                                                MainActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                    }, DELAY_TIME)
                                }

                                is Result.Error -> {
                                    setLoadingState(false)

                                    Snackbar.make(
                                        binding.root,
                                        resources.getString(R.string.upload_error_message),
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                    Timber.tag(TAG).d("uploadStory: %s", result.error)
                                }
                            }
                        }
                }
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                    Timber.tag(TAG)
                        .d("getCurrentLocation: ${location.latitude}, ${location.longitude}")
                } else {
                    Snackbar.make(
                        binding.root,
                        resources.getString(R.string.text_enable_location),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.containerProgressbarUpload.visibility = View.VISIBLE
        } else {
            binding.containerProgressbarUpload.visibility = View.GONE
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val DELAY_TIME = 1000L
    }
}