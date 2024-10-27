package com.inncreator.gitrepo.presenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.inncreator.gitrepo.R
import com.inncreator.gitrepo.databinding.FragmentDownloadRepositoryBinding
import com.inncreator.gitrepo.presenter.adapter.DownloadRepositoryAdapter
import com.inncreator.gitrepo.presenter.mvi.DownloadRepositoryEffect
import com.inncreator.gitrepo.presenter.mvi.DownloadRepositoryIntent
import com.inncreator.gitrepo.presenter.mvi.DownloadRepositoryViewState
import com.inncreator.gitrepo.presenter.mvi.RepositoryListEffect
import com.inncreator.gitrepo.presenter.viewmodel.DownloadRepositoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DownloadRepositoryFragment : Fragment() {

    private var _binding: FragmentDownloadRepositoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DownloadRepositoryViewModel by viewModels()
    private val adapter = DownloadRepositoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadRepositoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.sendIntent(DownloadRepositoryIntent.LoadRepositories)

        observeViewState()
        observeEffects()
    }

    private fun observeViewState() {
        lifecycleScope.launch {
            viewModel.viewState.collect { state ->
                when (state) {
                    is DownloadRepositoryViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is DownloadRepositoryViewState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(state.repositories)
                    }

                    is DownloadRepositoryViewState.Empty -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(emptyList())
                    }

                    is DownloadRepositoryViewState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun observeEffects() {
        lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is DownloadRepositoryEffect.ShowError -> {
                        Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}