package com.inncreator.gitrepo.di.module


import android.content.Context
import androidx.room.Room
import com.inncreator.gitrepo.BuildConfig
import com.inncreator.gitrepo.data.api.RepositoryService
import com.inncreator.gitrepo.data.api.github.GitHubApi
import com.inncreator.gitrepo.data.api.github.GitHubRepositoryService
import com.inncreator.gitrepo.data.database.AppDatabase
import com.inncreator.gitrepo.data.database.DatabaseRepository
import com.inncreator.gitrepo.data.database.DatabaseRepositoryImpl
import com.inncreator.gitrepo.data.database.RepositoryDao
import com.inncreator.gitrepo.data.download.FileDownloader
import com.inncreator.gitrepo.domain.repository.DataRepository
import com.inncreator.gitrepo.domain.repository.DataRepositoryImpl
import com.inncreator.gitrepo.domain.repository.DownloadRepository
import com.inncreator.gitrepo.domain.repository.DownloadRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGitHubApi(): GitHubApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.GITHUB_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(GitHubApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubRepositoryService(api: GitHubApi): RepositoryService {
        return GitHubRepositoryService(api)
    }

    @Provides
    @Singleton
    fun provideDataRepository(repositoryService: RepositoryService): DataRepository {
        return DataRepositoryImpl(repositoryService)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "download_repo.db"
        ).build()
    }

    @Provides
    fun provideRepositoryDao(appDatabase: AppDatabase): RepositoryDao {
        return appDatabase.repositoryDao()
    }

    @Provides
    @Singleton
    fun provideDatabaseRepository(repositoryDao: RepositoryDao): DatabaseRepository {
        return DatabaseRepositoryImpl(repositoryDao)
    }
    @Provides
    @Singleton
    fun provideFileDownloader(@ApplicationContext context: Context): FileDownloader {
        return FileDownloader(context)
    }

    @Provides
    @Singleton
    fun provideDownloadRepository(repositoryDao: RepositoryDao, fileDownloader: FileDownloader): DownloadRepository {
        return DownloadRepositoryImpl(repositoryDao, fileDownloader)
    }
}