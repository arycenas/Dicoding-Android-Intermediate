package com.intermediate.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.intermediate.storyapp.data.database.Remote
import com.intermediate.storyapp.data.database.StoryDatabase
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class StoryMediator(
    private val storyDatabase: StoryDatabase, private val userPreference: UserPreference
) : RemoteMediator<Int, ListStoryItem>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val pages = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val previousKey = remoteKeys?.previousKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                previousKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            val apiService = ApiConfig.getApiService(
                userPreference.getSession().first().token
            )
            val storyResponse = apiService.getAllStories(
                page = pages, state.config.pageSize
            )
            val endOfPage = storyResponse.listStory.isEmpty()

            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDatabase.remoteDao().clearRemoteKeys()
                    storyDatabase.storyDao().deleteAll()
                }
                val previousKey = if (pages == 1) null else pages - 1
                val nextKey = if (endOfPage) null else pages + 1
                val keys = storyResponse.listStory.map {
                    Remote(id = it.id, previousKey = previousKey, nextKey = nextKey)
                }
                storyDatabase.remoteDao().insertAll(keys)
                storyDatabase.storyDao().insertStory(storyResponse.listStory)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPage)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyPosition(state: PagingState<Int, ListStoryItem>): Remote? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteDao().getRemoteKeys(id)
            }
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, ListStoryItem>): Remote? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteDao().getRemoteKeys(data.id)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, ListStoryItem>): Remote? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteDao().getRemoteKeys(data.id)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}