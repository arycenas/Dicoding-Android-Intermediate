package com.dicoding.picodiploma.loginwithanimation.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.intermediate.storyapp.data.StoryRepository
import com.intermediate.storyapp.data.UserRepository
import com.intermediate.storyapp.di.Injection
import com.intermediate.storyapp.view.login.LoginViewModel
import com.intermediate.storyapp.view.main.MainViewModel
import com.intermediate.storyapp.view.maps.MapsViewModel
import com.intermediate.storyapp.view.post.PostViewModel
import com.intermediate.storyapp.view.signup.SignUpViewModel

class ViewModelFactory(
    private val repository: UserRepository, private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, storyRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }

            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideRepository(context),
                        Injection.provideStoryRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}