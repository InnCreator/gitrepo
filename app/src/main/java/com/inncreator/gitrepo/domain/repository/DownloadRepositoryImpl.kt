package com.inncreator.gitrepo.domain.repository

import android.net.Uri
import com.inncreator.gitrepo.data.database.RepositoryDao
import com.inncreator.gitrepo.data.database.RepositoryEntity
import com.inncreator.gitrepo.data.database.RepositoryStatus
import com.inncreator.gitrepo.data.download.DownloadStatus
import com.inncreator.gitrepo.data.download.FileDownloader
import com.inncreator.gitrepo.domain.model.DataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    private val repositoryDao: RepositoryDao,
    private val fileDownloader: FileDownloader
) : DownloadRepository {
    private var currentDownloadId: Long? = null

    override suspend fun enqueueDownload(dataModel: DataModel) = withContext(Dispatchers.IO) {
        val repositoryEntity = dataModel.toRepositoryEntity(RepositoryStatus.QUEUED)
        repositoryDao.insertRepository(repositoryEntity)
        checkAndStartNextDownload()
    }

    private suspend fun checkAndStartNextDownload() {
        if (currentDownloadId == null) {
            val nextRepository = repositoryDao.getQueuedRepository()
            nextRepository?.let {
                startDownload(it)
            }
        }
    }

    private suspend fun startDownload(repositoryEntity: RepositoryEntity) =
        withContext(Dispatchers.IO) {
            val downloadId = fileDownloader.downloadFile(
                Uri.parse(repositoryEntity.downloadLink),
                repositoryEntity.repositoryName + ".zip"
            )
            currentDownloadId = downloadId

            repositoryDao.insertRepository(repositoryEntity.copy(status = RepositoryStatus.DOWNLOADING))

            var hasRunningStatusBeenUpdated = false

            fileDownloader.trackDownloadStatus(downloadId).collect { status ->
                when (status) {
                    is DownloadStatus.Successful -> {
                        currentDownloadId = null
                        repositoryDao.insertRepository(repositoryEntity.copy(status = RepositoryStatus.DOWNLOADED))
                        checkAndStartNextDownload()
                    }

                    is DownloadStatus.Failed -> {
                        currentDownloadId = null
                        repositoryDao.insertRepository(repositoryEntity.copy(status = RepositoryStatus.FAILED))
                        checkAndStartNextDownload()
                    }

                    is DownloadStatus.Running -> {
                        if (!hasRunningStatusBeenUpdated) {
                            repositoryDao.insertRepository(repositoryEntity.copy(status = RepositoryStatus.DOWNLOADING))
                            hasRunningStatusBeenUpdated = true
                        }
                    }

                    else -> {}
                }
            }
        }

    override suspend fun cancelCurrentDownload() {
        currentDownloadId?.let {
            fileDownloader.cancelDownload(it)
            currentDownloadId = null
        }
    }

    override suspend fun deleteRepository(repositoryEntity: RepositoryEntity) =
        withContext(Dispatchers.IO) {
            fileDownloader.deleteDownloadedFile(repositoryEntity.repositoryName + ".zip")
            repositoryDao.deleteRepository(repositoryEntity)
        }

    override fun getAllDownloadedRepositories(): Flow<List<RepositoryEntity>> {
        return repositoryDao.getAllRepositories()
    }
}
