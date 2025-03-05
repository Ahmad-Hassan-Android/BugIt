package com.mobily.bugit.presentation.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobily.bugit.presentation.model.BugReportEvent
import com.mobily.bugit.presentation.ui.components.BugReportForm
import com.mobily.bugit.presentation.ui.components.BugReportList
import com.mobily.bugit.presentation.viewmodel.BugReportViewModel
import com.mobily.bugit.util.FileUtil
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugReportScreen(
    viewModel: BugReportViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var currentPhotoFile: File? by remember { mutableStateOf(null) }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoFile?.let { file ->
                viewModel.onEvent(BugReportEvent.OnImageSelected(file))
            }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = FileUtil.copyUriToFile(context, it)
            viewModel.onEvent(BugReportEvent.OnImageSelected(file))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bug Reporter") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BugReportForm(
                state = state,
                onEvent = viewModel::onEvent,
                onTakePhoto = {
                    val (file, uri) = FileUtil.createTempImageFile(context)
                    currentPhotoFile = file
                    takePhotoLauncher.launch(uri)
                },
                onPickImage = {
                    pickImageLauncher.launch("image/*")
                }
            )

            if (state.bugReports.isNotEmpty()) {
                BugReportList(
                    bugReports = state.bugReports,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Show confirmation dialog
    if (state.showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(BugReportEvent.CancelSubmit) },
            title = { Text("Confirm Submission") },
            text = { Text("Are you sure you want to submit this bug report?") },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(BugReportEvent.ConfirmSubmit) }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(BugReportEvent.CancelSubmit) }) {
                    Text("No")
                }
            }
        )
    }

    // Show result dialog
    state.dialogMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(BugReportEvent.DismissDialog) },
            title = { Text("Message") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(BugReportEvent.DismissDialog) }) {
                    Text("OK")
                }
            }
        )
    }
}