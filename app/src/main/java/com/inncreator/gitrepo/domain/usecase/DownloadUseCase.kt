package com.inncreator.gitrepo.domain.usecase

import com.inncreator.gitrepo.data.database.RepositoryEntity
import com.inncreator.gitrepo.domain.model.DataModel
import com.inncreator.gitrepo.domain.repository.DownloadRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadUseCase @Inject constructor(
    private val downloadRepository: DownloadRepositoryImpl
) {
    suspend fun enqueueRepositoryForDownload(dataModel: DataModel) = withContext(Dispatchers.IO) {
        downloadRepository.enqueueDownload(dataModel)
    }
    suspend fun cancelCurrentDownload() = withContext(Dispatchers.IO) {
        downloadRepository.cancelCurrentDownload()
    }
    suspend fun deleteRepository(repositoryEntity: RepositoryEntity) = withContext(Dispatchers.IO) {
        downloadRepository.deleteRepository(repositoryEntity)
    }
    fun getAllDownloadedRepositories(): Flow<List<RepositoryEntity>> {
        return downloadRepository.getAllDownloadedRepositories()
    }
}

