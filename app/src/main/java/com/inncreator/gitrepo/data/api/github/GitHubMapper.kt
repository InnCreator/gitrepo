package com.inncreator.gitrepo.data.api.github

import com.inncreator.gitrepo.BuildConfig
import com.inncreator.gitrepo.data.model.RepositoryItem
import com.inncreator.gitrepo.domain.model.GitSources

class GitHubMapper {
    companion object {
        fun mapFromGitHub(response: List<GitHubApiResponse>?): List<RepositoryItem> {
            return response?.map { item ->
                RepositoryItem(
                    id = item.id,
                    name = item.name,
                    login = item.owner.login,
                    avatarUrl = item.owner.avatarUrl,
                    description = item.description,
                    downloadLink = BuildConfig.GITHUB_ENDPOINT+"repos/${item.owner.login}/${item.name}/zipball",
                    link = item.link,
                    fullName = item.fullName,
                    source = GitSources.GIT_HUB,
                )
            } ?: emptyList()
        }
    }

}