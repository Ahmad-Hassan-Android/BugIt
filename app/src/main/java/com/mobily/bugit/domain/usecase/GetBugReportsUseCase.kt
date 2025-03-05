package com.mobily.bugit.domain.usecase

import com.mobily.bugit.domain.model.BugReport
import com.mobily.bugit.domain.repository.BugReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBugReportsUseCase @Inject constructor(
    private val repository: BugReportRepository
) {
    operator fun invoke(): Flow<List<BugReport>> {
        return repository.getBugReports()
    }
}