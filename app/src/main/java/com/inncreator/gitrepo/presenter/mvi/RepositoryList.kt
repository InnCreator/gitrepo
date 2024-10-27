package com.inncreator.gitrepo.presenter.mvi

import com.inncreator.gitrepo.domain.model.DataModel


sealed class RepositoryListIntent {
    data class SearchRepositories(val query: String) : RepositoryListIntent()
    data class DownloadRepository(val dataModel: DataModel) : RepositoryListIntent()
    data object PermissionGranted : RepositoryListIntent()
    data object PermissionDenied : RepositoryListIntent()
}

data class RepositoryListViewState(
    val isLoading: Boolean = false,
    val repositories: List<DataModel> = emptyList(),
    val error: String? = null
)

sealed class RepositoryListEffect {
    data class ShowError(val error: RepoError) : RepositoryListEffect()
    data object ShowDownloadStarted : RepositoryListEffect()
    data object RequestPermissions : RepositoryListEffect()
}

sealed class RepoError{
    data object SearchError: RepoError()
    data object PermissionError: RepoError()
}
