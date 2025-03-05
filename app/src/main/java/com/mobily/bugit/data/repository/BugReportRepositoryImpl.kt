package com.mobily.bugit.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.mobily.bugit.data.config.ImgBBConfig
import com.mobily.bugit.data.config.NotionConfig
import com.mobily.bugit.data.remote.*
import com.mobily.bugit.domain.model.BugReport
import com.mobily.bugit.domain.model.BugStatus
import com.mobily.bugit.domain.repository.BugReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class BugReportRepositoryImpl @Inject constructor(
    private val notionService: NotionService,
    private val imgBBService: ImgBBService,
    private val context: Context,
    private val notionConfig: NotionConfig,
    private val imgBBConfig: ImgBBConfig
) : BugReportRepository {

    private val bugReports = mutableListOf<BugReport>()
    private val dateFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    override suspend fun createBugReport(description: String, imageFiles: List<File>): Result<BugReport> {
        return try {
            val bugReport = BugReport(
                description = description,
                imageUrls = emptyList(),
                status = if (isOnline()) BugStatus.UPLOADED else BugStatus.PENDING
            )

            if (isOnline()) {
                // Upload all images first
                val imageUrls = imageFiles.mapNotNull { file ->
                    uploadImage(file).getOrNull()
                }

                val requestBody = NotionPageRequest(
                    parent = Parent(database_id = notionConfig.databaseId),
                    properties = Properties(
                        Description = TextProperty(
                            rich_text = listOf(TextContent(text = Text(content = description)))
                        ),
                        Image = FilesProperty(
                            files = imageUrls.map { url ->
                                FileContent(
                                    name = url.substringAfterLast("/"),
                                    external = ExternalFile(url = url)
                                )
                            }
                        ),
                        Status = StatusProperty(
                            status = Status(name = bugReport.status.name)
                        ),
                        Timestamp = TextProperty(
                            rich_text = listOf(
                                TextContent(
                                    text = Text(
                                        content = dateFormatter.format(Instant.ofEpochMilli(bugReport.timestamp))
                                    )
                                )
                            )
                        )
                    )
                )

                val response = notionService.createPage(
                    token = "Bearer ${notionConfig.apiKey}",
                    requestBody = requestBody
                )

                if (response.isSuccessful) {
                    val updatedBugReport = bugReport.copy(imageUrls = imageUrls)
                    bugReports.add(updatedBugReport)
                    Result.success(updatedBugReport)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Failed to create page in Notion: ${response.code()}, $errorBody"))
                }
            } else {
                bugReports.add(bugReport)
                Result.success(bugReport)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(file: File): Result<String> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaType())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = imgBBService.uploadImage(
                apiKey = imgBBConfig.apiKey,
                image = body
            )

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data?.url ?: throw Exception("No URL in response"))
            } else {
                Result.failure(Exception("Failed to upload image: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getBugReports(): Flow<List<BugReport>> = flow {
        emit(bugReports.toList())
    }

    override suspend fun syncPendingReports() {
        if (!isOnline()) return

        bugReports
            .filter { it.status == BugStatus.PENDING }
            .forEach { report ->
                createBugReport(report.description, emptyList())
            }
    }

    override fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}