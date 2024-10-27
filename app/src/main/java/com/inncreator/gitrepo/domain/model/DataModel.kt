package com.inncreator.gitrepo.domain.model

import com.inncreator.gitrepo.data.database.RepositoryEntity
import com.inncreator.gitrepo.data.database.RepositoryStatus


data class DataModel(
    val id: Int,
    val name: String,
    val fullName: String,
    val login: String,
    val avatarUrl: String,
    val description: String?,
    val link: String,
    val downloadLink: String,
    val source: GitSources
) {
    fun toRepositoryEntity(status: RepositoryStatus) = RepositoryEntity (
        ownerName = login,
        repositoryName = fullName,
        description = description,
        ownerAvatarUrl = avatarUrl,
        source = source.toString(),
        fullName = fullName,
        downloadLink = downloadLink,
        status = status
    )
}