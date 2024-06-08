package com.intermediate.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intermediate.storyapp.data.StoryRepository
import com.intermediate.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _error = MutableLiveData<Boolean?>()
    val error: LiveData<Boolean?> = _error

    fun getMapsStories(): LiveData<List<ListStoryItem>> {
        viewModelScope.launch {
            val mapsStories = storyRepository.getMapsStories()
            _listStories.value = mapsStories.listStory
            _error.value = mapsStories.error
            _message.value = mapsStories.message
        }
        return listStories
    }
}