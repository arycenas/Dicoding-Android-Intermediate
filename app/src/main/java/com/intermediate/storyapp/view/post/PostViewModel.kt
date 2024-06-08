package com.intermediate.storyapp.view.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intermediate.storyapp.data.StoryRepository
import kotlinx.coroutines.launch
import java.io.File

class PostViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _results = MutableLiveData<String>()
    val results: LiveData<String> = _results

    private val _error = MutableLiveData<Boolean?>()
    val error: LiveData<Boolean?> = _error

    fun postStory(img: File, description: String) {
        viewModelScope.launch {
            val response = storyRepository.postStory(img, description)
            _results.value = response.message.toString()
            _error.value = response.error
        }
    }

    fun clearError() {
        _error.value = false
        _results.value = ""
    }
}