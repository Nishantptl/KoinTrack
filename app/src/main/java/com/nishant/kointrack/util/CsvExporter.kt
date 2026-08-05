package com.nishant.kointrack.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.nishant.kointrack.domain.model.Transaction
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    fun exportAndShareCsv(context: Context, transactions: List<Transaction>) {
        if (transactions.isEmpty()) return

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val csvHeader = "ID,Title,AmountEUR,Category,Type,Date,Note\n"

        val csvBody = transactions.joinToString("\n") { tx ->
            val formattedDate = dateFormat.format(Date(tx.timestamp))
            val safeTitle = escapeCsvField(tx.title)
            val safeNote = escapeCsvField(tx.note ?: "")

            "${tx.id},$safeTitle,${tx.amount},${tx.category.name},${tx.type.name},$formattedDate,$safeNote"
        }

        val csvContent = csvHeader + csvBody

        val fileName = "kointrack_export_${System.currentTimeMillis()}.csv"
        val exportFile = File(context.cacheDir, fileName)
        exportFile.writeText(csvContent)

        val authority = "${context.packageName}.fileprovider"
        val contentUri = FileProvider.getUriForFile(context, authority, exportFile)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Export Transactions CSV").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooserIntent)
    }

    private fun escapeCsvField(field: String): String {
        return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
    }
}
