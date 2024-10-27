package com.inncreator.gitrepo.presenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.inncreator.gitrepo.R
import com.inncreator.gitrepo.databinding.FragmentRepositoryListBinding
import com.inncreator.gitrepo.presenter.adapter.RepositoryAdapter
import com.inncreator.gitrepo.presenter.viewmodel.RepositoryListViewModel
import com.inncreator.gitrepo.presenter.mvi.RepositoryListIntent
import com.inncreator.gitrepo.presenter.mvi.RepositoryListEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.Manifest
import android.os.Build
import com.inncreator.gitrepo.presenter.mvi.RepoError

@AndroidEntryPoint
class RepositoryListFragment : Fragment() {

    private var _binding: FragmentRepositoryListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RepositoryListViewModel by viewModels()
    private val adapter = RepositoryAdapter { dataModel ->
        sendIntent(RepositoryListIntent.DownloadRepository(dataModel))
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepositoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                sendIntent(RepositoryListIntent.PermissionGranted)
            } else {
                sendIntent(RepositoryListIntent.PermissionDenied)
            }
        }

        observeViewState()
        observeEffects()

        binding.buttonSearch.setOnClickListener {
            val query = binding.searchInput.text.toString()
            sendIntent(RepositoryListIntent.SearchRepositories(query))
        }
    }

    private fun sendIntent(repositoryListIntent: RepositoryListIntent) {
        viewModel.sendIntent(repositoryListIntent)
    }

    private fun observeViewState() {
        lifecycleScope.launch {
            viewModel.viewState.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                adapter.submitList(state.repositories)
            }
        }
    }

    private fun observeEffects() {
        lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is RepositoryListEffect.ShowError -> {
                        val message = when (effect.error) {
                            is RepoError.SearchError -> getString(R.string.search_failed)
                            is RepoError.PermissionError -> getString(R.string.permission_failed)
                        }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }

                    is RepositoryListEffect.ShowDownloadStarted -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.download_start),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is RepositoryListEffect.RequestPermissions -> {
                        requestPermissions()
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sendIntent(RepositoryListIntent.PermissionGranted)
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}