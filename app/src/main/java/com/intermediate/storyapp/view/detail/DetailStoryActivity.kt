package com.intermediate.storyapp.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.intermediate.storyapp.R
import com.intermediate.storyapp.databinding.ActivityStoryDetailBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val materialToolbar = findViewById<MaterialToolbar>(R.id.menubar)
        materialToolbar.setNavigationOnClickListener {
            finish()
        }

        fetchStory()
    }

    private fun fetchStory() {
        val id = intent.getStringExtra("ID")
        val name = intent.getStringExtra("NAME")
        val image = intent.getStringExtra("PHOTO")
        val description = intent.getStringExtra("DESCRIPTION")

        binding.judul.text = name
        binding.deskripsi.text = description

        Glide.with(this).load(image).into(binding.storyImage)
    }
}