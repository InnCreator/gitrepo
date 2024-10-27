package com.inncreator.gitrepo.data.download

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class FileDownloader @Inject constructor(private val context: Context) {

    private var currentDownloadId: Long = -1

    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            val writePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val readPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED
        }
    }

    fun downloadFile(uri: Uri, fileName: String): Long {
        if (!hasPermissions()) {
            throw SecurityException("Permissions are not granted for downloading the file.")
        }

        val request = DownloadManager.Request(uri)
            .setTitle("Downloading $fileName")
            .setDescription("Please wait while the file is being downloaded")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        currentDownloadId = downloadManager.enqueue(request)
        return currentDownloadId
    }

    fun cancelDownload(downloadId: Long) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.remove(downloadId)
    }

    fun deleteDownloadedFile(fileName: String) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
            file.delete()
        }
    }

    fun trackDownloadStatus(downloadId: Long): Flow<DownloadStatus> = flow {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)

        while (true) {
            val cursor: Cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val reason =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))

                val downloadStatus = when (status) {
                    DownloadManager.STATUS_PENDING -> DownloadStatus.Pending
                    DownloadManager.STATUS_RUNNING -> {
                        val bytesDownloaded =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val bytesTotal =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        val progress = if (bytesTotal > 0) {
                            (bytesDownloaded * 1000 / bytesTotal).coerceIn(1, 1000)
                        } else {
                            0
                        }
                        DownloadStatus.Running(progress)
                    }

                    DownloadManager.STATUS_PAUSED -> DownloadStatus.Paused
                    DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.Successful
                    DownloadManager.STATUS_FAILED -> {
                        Log.e(
                            "FileDownloader",
                            "Download failed. Reason: ${getReasonString(reason)}"
                        )
                        DownloadStatus.Failed
                    }

                    else -> DownloadStatus.Unknown
                }
                emit(downloadStatus)
            }
            cursor.close()
            delay(1000)
        }
    }

    private fun getReasonString(reason: Int): String {
        return when (reason) {
            DownloadManager.ERROR_CANNOT_RESUME -> "ERROR_CANNOT_RESUME"
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "ERROR_DEVICE_NOT_FOUND"
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "ERROR_FILE_ALREADY_EXISTS"
            DownloadManager.ERROR_FILE_ERROR -> "ERROR_FILE_ERROR"
            DownloadManager.ERROR_HTTP_DATA_ERROR -> "ERROR_HTTP_DATA_ERROR"
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "ERROR_INSUFFICIENT_SPACE"
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "ERROR_TOO_MANY_REDIRECTS"
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "ERROR_UNHANDLED_HTTP_CODE"
            DownloadManager.ERROR_UNKNOWN -> "ERROR_UNKNOWN"
            DownloadManager.PAUSED_QUEUED_FOR_WIFI -> "PAUSED_QUEUED_FOR_WIFI"
            DownloadManager.PAUSED_UNKNOWN -> "PAUSED_UNKNOWN"
            DownloadManager.PAUSED_WAITING_FOR_NETWORK -> "PAUSED_WAITING_FOR_NETWORK"
            DownloadManager.PAUSED_WAITING_TO_RETRY -> "PAUSED_WAITING_TO_RETRY"
            else -> "UNKNOWN_REASON"
        }
    }
}

sealed class DownloadStatus {
    data object Pending : DownloadStatus()
    data class Running(val progress: Int) : DownloadStatus() // Progress from 1 to 1000
    data object Paused : DownloadStatus()
    data object Successful : DownloadStatus()
    data object Failed : DownloadStatus()
    data object Unknown : DownloadStatus()
}