package com.inncreator.gitrepo.domain.repository

import com.inncreator.gitrepo.data.api.RepositoryService
import com.inncreator.gitrepo.domain.model.DataModel

class DataRepositoryImpl(private val repositoryService: RepositoryService) :
    DataRepository {
    override suspend fun getData(username: String): List<DataModel> {
        val repositoryItems = repositoryService.getRepositoriesByUser(username)
        println(repositoryItems)
        return repositoryItems.map { repositoryItem ->
            DataModel(
                id = repositoryItem.id,
                name = repositoryItem.name,
                login = repositoryItem.login,
                avatarUrl = repositoryItem.avatarUrl,
                description = repositoryItem.description,
                fullName = repositoryItem.fullName,
                downloadLink = repositoryItem.downloadLink,
                link = repositoryItem.link,
                source = repositoryItem.source
            )
        }
    }
}