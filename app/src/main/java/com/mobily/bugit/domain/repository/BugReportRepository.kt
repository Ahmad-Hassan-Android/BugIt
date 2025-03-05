package com.mobily.bugit.domain.repository

import com.mobily.bugit.domain.model.BugReport
import kotlinx.coroutines.flow.Flow
import java.io.File

interface BugReportRepository {
    suspend fun createBugReport(description: String, imageFiles: List<File>): Result<BugReport>
    suspend fun uploadImage(file: File): Result<String>
    fun getBugReports(): Flow<List<BugReport>>
    suspend fun syncPendingReports()
    fun isOnline(): Boolean
}