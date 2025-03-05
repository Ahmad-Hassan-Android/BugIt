package com.mobily.bugit.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mobily.bugit.domain.model.BugReport
import com.mobily.bugit.domain.model.BugStatus

@Composable
fun BugReportList(
    bugReports: List<BugReport>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(bugReports) { report ->
            BugReportItem(report = report)
        }
    }
}

@Composable
fun BugReportItem(
    report: BugReport,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyLarge
            )

            if (report.imageUrls.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(120.dp)
                ) {
                    items(report.imageUrls) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Bug report image",
                            modifier = Modifier
                                .width(120.dp)
                                .fillMaxHeight(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status: ${report.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (report.status) {
                        BugStatus.UPLOADED -> MaterialTheme.colorScheme.primary
                        BugStatus.PENDING -> MaterialTheme.colorScheme.secondary
                        BugStatus.FAILED -> MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}