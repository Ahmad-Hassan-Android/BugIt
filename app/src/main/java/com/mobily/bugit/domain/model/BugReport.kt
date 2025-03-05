package com.mobily.bugit.domain.model

data class BugReport(
    val id: String = "",
    val description: String,
    val imageUrls: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val status: BugStatus = BugStatus.PENDING
)

enum class BugStatus {
    PENDING,
    UPLOADED,
    FAILED
}