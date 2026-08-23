package com.waterdelivery.app.core.platform

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.model.Invoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.io.FileOutputStream

actual interface PdfInvoiceGenerator {
    actual suspend fun generateInvoicePdf(
        invoice: Invoice,
        customer: Customer,
        profile: BusinessProfile,
        deliveries: List<Delivery>
    ): String
}

class AndroidPdfInvoiceGenerator(private val context: Context) : PdfInvoiceGenerator {
    override suspend fun generateInvoicePdf(
        invoice: Invoice,
        customer: Customer,
        profile: BusinessProfile,
        deliveries: List<Delivery>
    ): String = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var y = 50f

        // Business Header
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 24f
        paint.color = Color.BLACK
        canvas.drawText(profile.businessName, 50f, y, paint)
        y += 30f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f
        canvas.drawText(profile.address, 50f, y, paint)
        y += 20f
        canvas.drawText("Phone: ${profile.phone}", 50f, y, paint)
        y += 40f

        // Invoice Meta
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Invoice: ${invoice.invoiceNumber}", 50f, y, paint)
        y += 20f

        // Bill To
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("BILL TO:", 50f, y, paint)
        y += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(customer.name, 50f, y, paint)
        y += 15f
        canvas.drawText(customer.address, 50f, y, paint)
        y += 15f
        canvas.drawText(customer.phoneNumber, 50f, y, paint)
        y += 40f

        // Table Header
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Date", 50f, y, paint)
        canvas.drawText("Del", 200f, y, paint)
        canvas.drawText("Ret", 300f, y, paint)
        canvas.drawText("Amount", 450f, y, paint)
        y += 10f
        canvas.drawLine(50f, y, 550f, y, paint)
        y += 25f

        // Table Rows
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        deliveries.forEach { delivery ->
            val date = Instant.fromEpochMilliseconds(delivery.date)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val dateStr = "${date.dayOfMonth}/${date.monthNumber}"

            canvas.drawText(dateStr, 50f, y, paint)
            canvas.drawText("${delivery.deliveredQuantity}", 200f, y, paint)
            canvas.drawText("${delivery.returnedQuantity}", 300f, y, paint)
            canvas.drawText("PKR ${delivery.totalAmount.toInt()}", 450f, y, paint)
            y += 20f

            if (y > 750f) { // Simple overflow check
                // In a real app we'd start a new page
            }
        }

        canvas.drawLine(50f, y, 550f, y, paint)
        y += 30f

        // Summary
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Grand Total: PKR ${invoice.totalAmount.toInt()}", 350f, y, paint)

        pdfDocument.finishPage(page)

        val sanitizedInvoiceNumber = invoice.invoiceNumber.replace(Regex("[^a-zA-Z0-9]"), "_")
        val file = File(context.cacheDir, "invoice_$sanitizedInvoiceNumber.pdf")
        
        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } finally {
            pdfDocument.close()
        }

        file.absolutePath
    }
}
