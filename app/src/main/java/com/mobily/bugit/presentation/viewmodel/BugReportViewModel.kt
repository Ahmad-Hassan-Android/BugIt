package com.mobily.bugit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobily.bugit.domain.model.BugStatus
import com.mobily.bugit.domain.usecase.CreateBugReportUseCase
import com.mobily.bugit.domain.usecase.GetBugReportsUseCase
import com.mobily.bugit.presentation.model.BugReportEvent
import com.mobily.bugit.presentation.model.BugReportUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BugReportViewModel @Inject constructor(
    private val createBugReportUseCase: CreateBugReportUseCase,
    private val getBugReportsUseCase: GetBugReportsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BugReportUiState())
    val uiState: StateFlow<BugReportUiState> = _uiState.asStateFlow()

    init {
        loadBugReports()
    }

    private fun loadBugReports() {
        viewModelScope.launch {
            getBugReportsUseCase().collect { reports ->
                _uiState.update { it.copy(bugReports = reports) }
            }
        }
    }

    fun onEvent(event: BugReportEvent) {
        when (event) {
            is BugReportEvent.OnDescriptionChange -> {
                _uiState.update { it.copy(description = event.description) }
            }
            is BugReportEvent.OnImageSelected -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedImages = currentState.selectedImages + event.file,
                        error = null
                    )
                }
            }
            is BugReportEvent.OnImageRemoved -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedImages = currentState.selectedImages.filter { it != event.file }
                    )
                }
            }
            BugReportEvent.OnSubmit -> {
                val currentState = _uiState.value
                if (currentState.description.isBlank()) {
                    _uiState.update { it.copy(error = "Description cannot be empty") }
                    return
                }
                _uiState.update { it.copy(showConfirmationDialog = true) }
            }
            BugReportEvent.OnRetrySync -> submitBugReport()
            BugReportEvent.DismissError -> {
                _uiState.update { it.copy(error = null) }
            }
            BugReportEvent.DismissDialog -> {
                _uiState.update { it.copy(dialogMessage = null) }
            }
            BugReportEvent.ShowConfirmationDialog -> {
                _uiState.update { it.copy(showConfirmationDialog = true) }
            }
            BugReportEvent.ConfirmSubmit -> {
                _uiState.update { it.copy(showConfirmationDialog = false) }
                submitBugReport()
            }
            BugReportEvent.CancelSubmit -> {
                _uiState.update { it.copy(showConfirmationDialog = false) }
            }
        }
    }

    private fun submitBugReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            createBugReportUseCase(
                description = _uiState.value.description,
                imageFiles = _uiState.value.selectedImages
            ).fold(
                onSuccess = { bugReport ->
                    _uiState.update { state ->
                        if (bugReport.status == BugStatus.UPLOADED) {
                            // Clear form data only on successful upload
                            state.copy(
                                isLoading = false,
                                error = null,
                                dialogMessage = "Bug report submitted successfully!",
                                description = "",
                                selectedImages = emptyList()
                            )
                        } else {
                            // Keep form data when offline
                            state.copy(
                                isLoading = false,
                                error = null,
                                dialogMessage = "Please, Check internet connection."
                            )
                        }
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to create bug report"
                        )
                    }
                }
            )
        }
    }
}
