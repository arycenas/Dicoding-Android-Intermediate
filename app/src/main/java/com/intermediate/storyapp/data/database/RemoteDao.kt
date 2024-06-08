package com.intermediate.storyapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intermediate.storyapp.data.database.Remote

@Dao
interface RemoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remote: List<Remote>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeys(id: String): Remote

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()

}