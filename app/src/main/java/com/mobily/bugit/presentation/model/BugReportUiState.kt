package com.mobily.bugit.presentation.model

import com.mobily.bugit.domain.model.BugReport
import java.io.File

data class BugReportUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val bugReports: List<BugReport> = emptyList(),
    val selectedImages: List<File> = emptyList(),
    val description: String = "",
    val isOnline: Boolean = true,
    val dialogMessage: String? = null,
    val showConfirmationDialog: Boolean = false
)

sealed class BugReportEvent {
    data class OnDescriptionChange(val description: String) : BugReportEvent()
    data class OnImageSelected(val file: File) : BugReportEvent()
    data class OnImageRemoved(val file: File) : BugReportEvent()
    object OnSubmit : BugReportEvent()
    object OnRetrySync : BugReportEvent()
    object DismissError : BugReportEvent()
    object DismissDialog : BugReportEvent()
    object ShowConfirmationDialog : BugReportEvent()
    object ConfirmSubmit : BugReportEvent()
    object CancelSubmit : BugReportEvent()
}