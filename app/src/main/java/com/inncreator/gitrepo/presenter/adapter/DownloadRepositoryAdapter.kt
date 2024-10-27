package com.inncreator.gitrepo.presenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inncreator.gitrepo.R
import com.inncreator.gitrepo.data.database.RepositoryEntity
import com.inncreator.gitrepo.data.database.RepositoryStatus
import com.inncreator.gitrepo.databinding.ItemDownloadedRepositoryBinding

class DownloadRepositoryAdapter :
    ListAdapter<RepositoryEntity, DownloadRepositoryAdapter.DownloadRepositoryViewHolder>(
        RepositoryDiffCallback()
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownloadRepositoryViewHolder {
        val binding = ItemDownloadedRepositoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DownloadRepositoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadRepositoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DownloadRepositoryViewHolder(private val binding: ItemDownloadedRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repository: RepositoryEntity) {
            binding.repositoryName.text = repository.repositoryName
            binding.ownerName.text = repository.ownerName

            Glide.with(binding.root.context)
                .load(repository.ownerAvatarUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(binding.ownerAvatar)

            binding.downloadStatus.text = repository.status.toString()
            binding.progressBar.visibility =
                if (repository.status == RepositoryStatus.DOWNLOADING) View.VISIBLE else View.GONE

        }
    }

    class RepositoryDiffCallback : DiffUtil.ItemCallback<RepositoryEntity>() {
        override fun areItemsTheSame(
            oldItem: RepositoryEntity,
            newItem: RepositoryEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: RepositoryEntity,
            newItem: RepositoryEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}