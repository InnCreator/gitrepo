package com.inncreator.gitrepo.presenter.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inncreator.gitrepo.domain.usecase.DownloadUseCase
import com.inncreator.gitrepo.presenter.mvi.DownloadRepositoryEffect
import com.inncreator.gitrepo.presenter.mvi.DownloadRepositoryIntent
import com.inncreator.gitrepo.presenter.mvi.DownloadRepositoryViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadRepositoryViewModel @Inject constructor(
    private val downloadUseCase: DownloadUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow<DownloadRepositoryViewState>(DownloadRepositoryViewState.Loading)
    val viewState: StateFlow<DownloadRepositoryViewState> = _viewState.asStateFlow()

    private val _effect = MutableSharedFlow<DownloadRepositoryEffect>()
    val effect: SharedFlow<DownloadRepositoryEffect> get() = _effect

    private val _intent = Channel<DownloadRepositoryIntent>(Channel.UNLIMITED)

    init {
        processIntents()
    }

    fun sendIntent(intent: DownloadRepositoryIntent) {
        viewModelScope.launch {
            _intent.send(intent)
        }
    }

    private fun processIntents() {
        viewModelScope.launch {
            _intent.consumeAsFlow().collect { intent ->
                when (intent) {
                    is DownloadRepositoryIntent.LoadRepositories -> {
                        loadDownloadedRepositories()
                    }
                }
            }
        }
    }

    private fun loadDownloadedRepositories() {
        viewModelScope.launch {
            downloadUseCase.getAllDownloadedRepositories()
                .collect { repositories ->
                    if (repositories.isEmpty()) {
                        _viewState.value = DownloadRepositoryViewState.Empty
                    } else {
                        _viewState.value = DownloadRepositoryViewState.Success(repositories)
                    }
                }
        }
    }
}
