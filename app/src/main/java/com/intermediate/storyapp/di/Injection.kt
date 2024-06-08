package com.intermediate.storyapp.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.intermediate.storyapp.data.StoryRepository
import com.intermediate.storyapp.data.UserRepository
import com.intermediate.storyapp.data.database.StoryDatabase
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val userModel = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(userModel.token)
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(pref, database)
    }
}