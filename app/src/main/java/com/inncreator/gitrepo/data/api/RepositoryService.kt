package com.inncreator.gitrepo.data.api

import com.inncreator.gitrepo.data.model.RepositoryItem
import okhttp3.ResponseBody

interface RepositoryService {
    suspend fun getRepositoriesByUser(username: String): List<RepositoryItem>
}