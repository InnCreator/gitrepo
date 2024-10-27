package com.inncreator.gitrepo.domain.usecase

import com.inncreator.gitrepo.domain.model.DataModel
import com.inncreator.gitrepo.domain.repository.DataRepository
import javax.inject.Inject

class GetDataUseCase @Inject constructor(private val repository: DataRepository) {

    suspend operator fun invoke(username: String): Result<List<DataModel>> {
        return try {
            val data = repository.getData(username)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}