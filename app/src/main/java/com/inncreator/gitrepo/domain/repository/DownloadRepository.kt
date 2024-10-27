package com.inncreator.gitrepo.domain.repository

import com.inncreator.gitrepo.data.database.RepositoryEntity
import com.inncreator.gitrepo.domain.model.DataModel
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    suspend fun enqueueDownload(dataModel: DataModel)
    suspend fun cancelCurrentDownload()
    suspend fun deleteRepository(repositoryEntity: RepositoryEntity)
    fun getAllDownloadedRepositories(): Flow<List<RepositoryEntity>>
}