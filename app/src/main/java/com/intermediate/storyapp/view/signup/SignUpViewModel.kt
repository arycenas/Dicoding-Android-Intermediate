package com.intermediate.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intermediate.storyapp.data.UserRepository
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: UserRepository) : ViewModel() {
    private val _result = MutableLiveData<String?>()
    val result: LiveData<String?> = _result

    private val _error = MutableLiveData<Boolean?>()
    val error: LiveData<Boolean?> = _error

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            val response = repository.signUp(name, email, password)
            _result.value = response.message
            _error.value = response.error
        }
    }
}