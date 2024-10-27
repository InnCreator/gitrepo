package com.inncreator.gitrepo.data.model

import com.inncreator.gitrepo.domain.model.GitSources

data class RepositoryItem(
    val id: Int,
    val name: String,
    val fullName: String,
    val login: String,
    val avatarUrl: String,
    val description: String?,
    val link: String,
    val downloadLink: String,
    val source: GitSources
)