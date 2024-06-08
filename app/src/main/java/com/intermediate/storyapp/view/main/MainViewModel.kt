package com.intermediate.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.intermediate.storyapp.data.StoryRepository
import com.intermediate.storyapp.data.UserRepository
import com.intermediate.storyapp.data.pref.UserModel
import com.intermediate.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: UserRepository, private val storyRepository: StoryRepository
) : ViewModel() {

    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _error = MutableLiveData<Boolean?>()
    val error: LiveData<Boolean?> = _error

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    var story: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getAllStories().cachedIn(viewModelScope)
}