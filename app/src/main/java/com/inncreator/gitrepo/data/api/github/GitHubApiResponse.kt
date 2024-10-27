package com.inncreator.gitrepo.data.api.github

import com.google.gson.annotations.SerializedName

data class GitHubApiResponse(
    val id: Int,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val owner: Owner,
    val description: String?,
    @SerializedName("html_url")
    val link: String,
    @SerializedName("downloads_url")
    val downloadLink: String
)