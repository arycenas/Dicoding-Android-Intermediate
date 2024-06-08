package com.intermediate.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.intermediate.storyapp.R
import com.intermediate.storyapp.data.response.ListStoryItem
import com.intermediate.storyapp.databinding.ActivityMainBinding
import com.intermediate.storyapp.view.adapter.StoryAdapter
import com.intermediate.storyapp.view.detail.DetailStoryActivity
import com.intermediate.storyapp.view.maps.MapsActivity
import com.intermediate.storyapp.view.post.PostActivity
import com.intermediate.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private var previousCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSession()

        setupStoryDetails()

        setupError()

        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = linearLayoutManager

        setupView()
        setupPostStory()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_item -> {
                setLogout()
            }

            R.id.maps_item -> {
                setMaps()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setupError() {
        viewModel.error.observe(this) {
            if (it == true) {
                viewModel.message.observe(this) { message ->
                    binding.error.text = message.toString()
                }
            }
        }
    }

    private fun setLogout() {
        viewModel.logout()
        val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setMaps() {
        val intent = Intent(this@MainActivity, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun setupStoryDetails() {
        val adapter = StoryAdapter(object : StoryAdapter.OnItemClickListener {
            override fun onItemClick(item: ListStoryItem, component: List<Pair<View, String>>) {
                val intent = Intent(
                    this@MainActivity, DetailStoryActivity::class.java
                )
                intent.putExtra("ID", item.id)
                intent.putExtra("NAME", item.name)
                intent.putExtra("PHOTO", item.photoUrl)
                intent.putExtra("DESCRIPTION", item.description)
                startActivity(intent)
            }
        })
        binding.rvStories.adapter = adapter

        viewModel.story.observe(this) { stories ->
            val currentItem = adapter.itemCount
            adapter.submitData(lifecycle, stories)
            if (currentItem < previousCount) {
                binding.rvStories.scrollToPosition(0)
            }
            previousCount = adapter.itemCount
        }
    }

    private fun setupView() {
        setSupportActionBar(findViewById<MaterialToolbar>(R.id.menubar))
    }

    private fun setupPostStory() {
        binding.storyAdd.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }
    }
}