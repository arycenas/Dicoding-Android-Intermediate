package com.intermediate.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intermediate.storyapp.data.UserRepository
import com.intermediate.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _result = MutableLiveData<String?>()
    val result: LiveData<String?> = _result

    private val _error = MutableLiveData<Boolean?>()
    val error: LiveData<Boolean?> = _error

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val loginResponse = repository.login(email, password)
            _result.value = loginResponse.message.toString()
            _error.value = loginResponse.error
        }
    }

    fun userModel(): LiveData<UserModel> = repository.userModel
}