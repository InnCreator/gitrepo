package com.inncreator.gitrepo.data.database

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val repositoryDao: RepositoryDao
) : DatabaseRepository{

    override suspend fun insertRepository(repository: RepositoryEntity) {
        repositoryDao.insertRepository(repository)
    }

    override fun getAllRepositories(): Flow<List<RepositoryEntity>> {
        return repositoryDao.getAllRepositories()
    }

    override suspend fun deleteRepository(repository: RepositoryEntity) {
        repositoryDao.deleteRepository(repository)
    }

}