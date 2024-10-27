package com.inncreator.gitrepo.data.database

import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun insertRepository(repository: RepositoryEntity)
    fun getAllRepositories(): Flow<List<RepositoryEntity>>
    suspend fun deleteRepository(repository: RepositoryEntity)
}