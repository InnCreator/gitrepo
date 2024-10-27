package com.inncreator.gitrepo.data.api.github

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApi {
    @GET("users/{username}/repos")
    suspend fun getRepositoriesByUser(
        @Path("username") username: String
    ): Response<List<GitHubApiResponse>>
}