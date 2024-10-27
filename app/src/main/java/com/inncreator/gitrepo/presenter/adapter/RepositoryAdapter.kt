package com.inncreator.gitrepo.presenter.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inncreator.gitrepo.databinding.ItemRepositoryBinding
import com.inncreator.gitrepo.domain.model.DataModel
import com.bumptech.glide.Glide
import com.inncreator.gitrepo.R

class RepositoryAdapter(private val startDownload: (DataModel) -> Unit) :
    ListAdapter<DataModel, RepositoryAdapter.RepositoryViewHolder>(RepositoryDiffCallback()) {

    inner class RepositoryViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, repository: DataModel) {
            binding.repositoryName.text = repository.name
            binding.repositoryOwner.text =
                context.getString(R.string.repository_owner, repository.login)
            binding.repositoryDescription.text = repository.description

            Glide.with(binding.ownerAvatar.context)
                .load(repository.avatarUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(binding.ownerAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val binding =
            ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepositoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        holder.bind(holder.itemView.context, getItem(position))
        holder.itemView.setOnClickListener { view ->
            showPopupMenu(view, getItem(position))
        }
    }

    private fun showPopupMenu(view: View, repository: DataModel) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.repository_menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_open_in_browser -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repository.link))
                    view.context.startActivity(intent)
                    true
                }

                R.id.action_download_zip -> {
                    startDownload.invoke(repository)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    class RepositoryDiffCallback : DiffUtil.ItemCallback<DataModel>() {
        override fun areItemsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
            return oldItem == newItem
        }
    }
}
