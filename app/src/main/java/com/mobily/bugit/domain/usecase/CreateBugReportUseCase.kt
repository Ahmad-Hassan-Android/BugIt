package com.mobily.bugit.domain.usecase

import com.mobily.bugit.domain.model.BugReport
import com.mobily.bugit.domain.repository.BugReportRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateBugReportUseCase @Inject constructor(
    private val repository: BugReportRepository
) {
    suspend operator fun invoke(description: String, imageFiles: List<File>): Result<BugReport> {
        return repository.createBugReport(description, imageFiles)
    }
}
