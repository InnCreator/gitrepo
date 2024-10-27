package com.inncreator.gitrepo.presenter.mvi

import com.inncreator.gitrepo.data.database.RepositoryEntity

sealed class DownloadRepositoryIntent {
    data object LoadRepositories : DownloadRepositoryIntent()
}

sealed class DownloadRepositoryViewState {
    data object Loading : DownloadRepositoryViewState()
    data class Success(val repositories: List<RepositoryEntity>) : DownloadRepositoryViewState()
    data object Empty : DownloadRepositoryViewState()
    data class Error(val errorMessage: String) : DownloadRepositoryViewState()
}

sealed class DownloadRepositoryEffect {
    data class ShowError(val message: String) : DownloadRepositoryEffect()
}
