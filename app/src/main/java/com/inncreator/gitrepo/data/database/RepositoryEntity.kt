package com.inncreator.gitrepo.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_repo")
data class RepositoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val ownerName: String,
    val repositoryName: String,
    val description: String?,
    val ownerAvatarUrl: String,
    val source: String,
    val status: RepositoryStatus,
    val downloadLink: String
)

enum class RepositoryStatus {
    DOWNLOADED,
    DOWNLOADING,
    QUEUED,
    FAILED
}

