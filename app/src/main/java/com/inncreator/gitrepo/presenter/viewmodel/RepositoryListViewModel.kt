package com.inncreator.gitrepo.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inncreator.gitrepo.domain.model.DataModel
import com.inncreator.gitrepo.domain.usecase.DownloadUseCase
import com.inncreator.gitrepo.domain.usecase.GetDataUseCase
import com.inncreator.gitrepo.presenter.mvi.RepoError
import com.inncreator.gitrepo.presenter.mvi.RepositoryListIntent
import com.inncreator.gitrepo.presenter.mvi.RepositoryListViewState
import com.inncreator.gitrepo.presenter.mvi.RepositoryListEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryListViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase,
    private val downloadUseCase: DownloadUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(RepositoryListViewState())
    val viewState: StateFlow<RepositoryListViewState> get() = _viewState

    private val _effect = MutableSharedFlow<RepositoryListEffect>()
    val effect: SharedFlow<RepositoryListEffect> get() = _effect

    private var pendingDownloadDataModel: DataModel? = null

    fun sendIntent(intent: RepositoryListIntent) {
        when (intent) {
            is RepositoryListIntent.SearchRepositories -> {
                searchRepositories(intent.query)
            }

            is RepositoryListIntent.DownloadRepository -> {
                pendingDownloadDataModel = intent.dataModel
                viewModelScope.launch {
                    _effect.emit(RepositoryListEffect.RequestPermissions)
                }
            }

            is RepositoryListIntent.PermissionGranted -> {
                pendingDownloadDataModel?.let {
                    downloadRepository(it)
                    pendingDownloadDataModel = null
                }
            }

            is RepositoryListIntent.PermissionDenied -> {
                viewModelScope.launch {
                    _effect.emit(RepositoryListEffect.ShowError(RepoError.PermissionError))
                }
            }
        }
    }

    private fun downloadRepository(dataModel: DataModel) {
        viewModelScope.launch {
            _effect.emit(RepositoryListEffect.ShowDownloadStarted)
            downloadUseCase.enqueueRepositoryForDownload(dataModel)
        }
    }

    private fun searchRepositories(query: String) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true)
            val result = getDataUseCase(query)
            result.fold(
                onSuccess = {
                    _viewState.value = _viewState.value.copy(
                        isLoading = false,
                        repositories = it
                    )
                },
                onFailure = {
                    _viewState.value = _viewState.value.copy(isLoading = false)
                    _effect.emit(RepositoryListEffect.ShowError(RepoError.SearchError))
                }
            )
        }
    }
}