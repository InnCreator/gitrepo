package com.inncreator.gitrepo.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepository(repository: RepositoryEntity)

    @Query("SELECT * FROM download_repo")
    fun getAllRepositories(): Flow<List<RepositoryEntity>>

    @Delete
    suspend fun deleteRepository(repository: RepositoryEntity)

    @Query("SELECT * FROM download_repo WHERE status = :status LIMIT 1")
    suspend fun getQueuedRepository(status: RepositoryStatus = RepositoryStatus.QUEUED): RepositoryEntity?

}