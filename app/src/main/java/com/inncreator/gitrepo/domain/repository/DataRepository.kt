package com.inncreator.gitrepo.domain.repository

import com.inncreator.gitrepo.domain.model.DataModel

interface DataRepository {
    suspend fun getData(username: String): List<DataModel>
}