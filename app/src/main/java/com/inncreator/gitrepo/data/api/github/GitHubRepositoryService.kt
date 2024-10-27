package com.inncreator.gitrepo.data.api.github

import com.inncreator.gitrepo.data.api.RepositoryService
import com.inncreator.gitrepo.data.model.RepositoryItem

class GitHubRepositoryService(private val api: GitHubApi) : RepositoryService {

    override suspend fun getRepositoriesByUser(username: String): List<RepositoryItem> {
        val response = api.getRepositoriesByUser(username)
        return if (response.isSuccessful) {
            GitHubMapper.mapFromGitHub(response.body())
        } else {
            throw Exception("Failed to fetch repositories")
        }
    }

}