package com.intermediate.storyapp.view.post

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.intermediate.storyapp.R
import com.intermediate.storyapp.databinding.ActivityStoryPostBinding
import com.intermediate.storyapp.utility.getImageUri
import com.intermediate.storyapp.utility.reduceFileImage
import com.intermediate.storyapp.utility.uriToFile
import com.intermediate.storyapp.view.main.MainActivity

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryPostBinding
    private var imageUri: Uri? = null
    private val viewModel by viewModels<PostViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                this, "Permission request granted", Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this, "Permission request denied", Toast.LENGTH_LONG
            ).show()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            imageUri?.let {
                Log.d("image URI", "showImage: $it")
                binding.previewImageView.setImageURI(it)
            }
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccessful ->
            if (isSuccessful) {
                imageUri?.let {
                    Log.d("image URI", "showImage: $it")
                    binding.previewImageView.setImageURI(it)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val materialToolbar = findViewById<MaterialToolbar>(R.id.menubar)
        materialToolbar.setNavigationOnClickListener {
            finish()
        }

        startGallery()
        setUploadButton()
        startCamera()
        setupError()
    }

    private fun setupError() {
        viewModel.error.observe(this) { error ->
            if (error == true) {
                val builder = AlertDialog.Builder(this)
                builder.apply {
                    setTitle("Gagal")
                    setMessage(viewModel.results.value)
                    setPositiveButton("Lanjut") { _, _ ->
                        viewModel.clearError()
                    }
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                val intent = Intent(this@PostActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun startGallery() {
        binding.galleryButton.setOnClickListener {
            launcherGallery.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setUploadButton() {
        binding.uploadButton.setOnClickListener {
            imageUri?.let { uri ->
                val img = uriToFile(uri, this@PostActivity)
                img.reduceFileImage()
                Log.d("image file", "onCreate: $img")
                val description = binding.descriptionEditText.text.toString()

                viewModel.postStory(img, description)
            }
        }
    }

    private fun startCamera() {
        binding.cammeraButton.setOnClickListener {
            if (!permissionGranted()) {
                requestPermission.launch(PERMISSION)
            } else {
                imageUri = getImageUri(this)
                launcherCamera.launch(imageUri!!)
            }
        }
    }

    private fun permissionGranted() = ContextCompat.checkSelfPermission(
        this, PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val PERMISSION = Manifest.permission.CAMERA
    }
}