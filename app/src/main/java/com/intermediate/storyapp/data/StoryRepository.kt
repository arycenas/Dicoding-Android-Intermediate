package com.intermediate.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.intermediate.storyapp.data.database.StoryDatabase
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.response.ListStoryItem
import com.intermediate.storyapp.data.response.PostStoryResponse
import com.intermediate.storyapp.data.response.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class StoryRepository private constructor(
    private val userPreference: UserPreference, private val storyDatabase: StoryDatabase
) {

    fun getAllStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class) return Pager(config = PagingConfig(
            pageSize = 5
        ),
            remoteMediator = StoryMediator(storyDatabase, userPreference),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }).liveData
    }

    suspend fun postStory(img: File, description: String): PostStoryResponse {
        try {
            val requestDescription = description.toRequestBody(
                "text/plain".toMediaType()
            )
            val image = img.asRequestBody(
                "image/jpeg".toMediaType()
            )
            val multi = MultipartBody.Part.createFormData(
                "photo", img.name, image
            )
            val apiService = ApiConfig.getApiService(
                userPreference.getSession().first().token
            )
            val success = apiService.postStories(multi, requestDescription)
            return success
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorBody, PostStoryResponse::class.java)
            return errorMessage
        } catch (e: IOException) {
            return PostStoryResponse(error = true, message = "No internet connection")
        }
    }

    suspend fun getMapsStories(): StoryResponse {
        try {
            val apiService = ApiConfig.getApiService(
                userPreference.getSession().first().token
            )
            val success = apiService.getStoriesWithLocation()
            val mapStories = success.listStory.filter {
                it.lat != null && it.lon != null
            }
            return StoryResponse(
                listStory = mapStories, error = false, message = "Stories retrieved successfully"
            )
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorBody, StoryResponse::class.java)
            return errorMessage
        } catch (e: IOException) {
            return StoryResponse(error = true, message = "No internet connection")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null
        fun getInstance(
            userPreference: UserPreference, storyDatabase: StoryDatabase
        ): StoryRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: StoryRepository(userPreference, storyDatabase)
        }.also {
            INSTANCE = it
        }
    }
}