package com.intermediate.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.intermediate.storyapp.data.pref.UserModel
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.response.LoginResponse
import com.intermediate.storyapp.data.response.SignUpResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class UserRepository private constructor(
    private val userPreference: UserPreference, private val apiService: ApiService
) {

    private val _userModel = MutableLiveData<UserModel>()
    val userModel: LiveData<UserModel> = _userModel

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun signUp(name: String, email: String, password: String): SignUpResponse {
        try {
            val success = apiService.register(name, email, password)
            return success
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val response = Gson().fromJson(errorBody, SignUpResponse::class.java)
            return response
        } catch (e: IOException) {
            return SignUpResponse(error = true, message = e.message)
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        try {
            val success = apiService.login(email, password)
            _userModel.value = success.loginResult?.token?.let {
                UserModel(
                    email, it, true
                )
            }
            return success
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val response = Gson().fromJson(errorBody, LoginResponse::class.java)
            return response
        } catch (e: IOException) {
            return LoginResponse(error = true, message = e.message)
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference, apiService: ApiService
        ): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(userPreference, apiService)
        }.also { instance = it }
    }
}