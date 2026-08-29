package com.waterdelivery.app.core.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
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

class AndroidPdfInvoiceGenerator(
    private val context: Context,
    // Day-of-month the bill is due. Wire this to a real profile/invoice field if you add one.
    private val dueDay: Int = 10
) : PdfInvoiceGenerator {

    // ---- Layout constants ---------------------------------------------------
    private val pageWidth = 595
    private val pageHeight = 842
    private val marginLeft = 50f
    private val marginRight = 545f
    private val contentWidth = marginRight - marginLeft
    private val centerX = (marginLeft + marginRight) / 2f
    private val rowBottomLimit = 760f // start a new page once rows pass this y

    // Column x-positions for the deliveries table
    private val colDate = marginLeft
    private val colDelivered = 220f
    private val colReturned = 330f
    private val colAmount = marginRight // right-aligned

    private val brandBlue = Color.rgb(30, 150, 195)
    private val headerFill = Color.rgb(235, 245, 249)
    private val black = Color.BLACK

    private val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    override suspend fun generateInvoicePdf(
        invoice: Invoice,
        customer: Customer,
        profile: BusinessProfile,
        deliveries: List<Delivery>
    ): String = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        var pageInfo =
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val tz = TimeZone.currentSystemDefault()
        var y = 60f

        // ---- Business Header (Logo + Info) -----------------------------------
        y = drawBusinessHeader(canvas, paint, profile, y)

        y += 10f
        // ---- Invoice Meta (Number + Date) ------------------------------------
        y = drawInvoiceMeta(canvas, paint, invoice, y)

        y += 20f
        // ---- Bill To Section -------------------------------------------------
        y = drawBillTo(canvas, paint, customer, y)

        y += 30f
        // ---- Deliveries Table ------------------------------------------------
        y = drawTableHeader(canvas, paint, y)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f
        paint.color = black

        deliveries.forEach { delivery ->
            if (y > rowBottomLimit) {
                pdfDocument.finishPage(page)
                pageInfo =
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1)
                        .create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 60f
                y = drawTableHeader(canvas, paint, y)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textSize = 11f
                paint.color = black
            }

            val date = Instant.fromEpochMilliseconds(delivery.date).toLocalDateTime(tz)
            val dateCell = "${date.dayOfMonth}/${date.monthNumber}"

            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(dateCell, colDate + 5f, y, paint)

            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("${delivery.deliveredQuantity}", colDelivered + 25f, y, paint)
            canvas.drawText("${delivery.returnedQuantity}", colReturned + 25f, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("${delivery.totalAmount.toInt()}", colAmount - 5f, y, paint)
            paint.textAlign = Paint.Align.LEFT

            y += 20f
        }

        // bottom rule under the last row
        canvas.drawLine(marginLeft, y, marginRight, y, paint)
        y += 20f

        // ---- Financial Summary ------------------------------------------------
        y = drawFinancialSummary(canvas, paint, invoice, y)

        y += 40f
        // ---- Footer -----------------------------------------------------------
        drawFooter(canvas, paint, profile, y)

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

    // ---- Drawing helpers ------------------------------------------------------

    private fun drawBusinessHeader(
        canvas: android.graphics.Canvas,
        paint: Paint,
        profile: BusinessProfile,
        startY: Float
    ): Float {
        var currentY = startY
        val logoSize = 60f

        // Logo on the left
        if (profile.logoPath != null && File(profile.logoPath).exists()) {
            try {
                val logoBitmap = BitmapFactory.decodeFile(profile.logoPath)
                if (logoBitmap != null) {
                    val scaled = Bitmap.createScaledBitmap(
                        logoBitmap,
                        logoSize.toInt(),
                        logoSize.toInt(),
                        true
                    )
                    canvas.drawBitmap(scaled, marginLeft, startY, paint)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Business details stacked on the right (or next to logo)
        val textX = if (profile.logoPath != null) marginLeft + logoSize + 15f else marginLeft

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 20f
        paint.color = brandBlue
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(profile.businessName.uppercase(), textX, currentY + 20f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        paint.color = Color.DKGRAY
        canvas.drawText(profile.address, textX, currentY + 38f, paint)
        canvas.drawText("Phone: ${profile.phone}", textX, currentY + 52f, paint)

        return startY + logoSize + 10f
    }

    private fun drawInvoiceMeta(
        canvas: android.graphics.Canvas,
        paint: Paint,
        invoice: Invoice,
        startY: Float
    ): Float {
        val tz = TimeZone.currentSystemDefault()
        // Left: Invoice Number
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        paint.color = Color.GRAY
        canvas.drawText("Invoice Number", marginLeft, startY, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = black
        canvas.drawText(invoice.invoiceNumber, marginLeft, startY + 18f, paint)

        // Period below Invoice Number
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 9f
        paint.color = Color.DKGRAY
        val startD = Instant.fromEpochMilliseconds(invoice.startDate).toLocalDateTime(tz)
        val endD = Instant.fromEpochMilliseconds(invoice.endDate).toLocalDateTime(tz)
        val periodStr = "Period: %02d %s %d – %02d %s %d".format(
            startD.dayOfMonth, monthNames[startD.monthNumber - 1].take(3), startD.year,
            endD.dayOfMonth, monthNames[endD.monthNumber - 1].take(3), endD.year
        )
        canvas.drawText(periodStr, marginLeft, startY + 32f, paint)

        // Right: Issue Date
        val date = Instant.fromEpochMilliseconds(invoice.createdAt).toLocalDateTime(tz)
        val dateStr = "${date.dayOfMonth} ${monthNames[date.monthNumber - 1].take(3)} ${date.year}"

        paint.textAlign = Paint.Align.RIGHT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.GRAY
        paint.textSize = 10f
        canvas.drawText("Issue Date", marginRight, startY, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = black
        paint.textSize = 12f
        canvas.drawText(dateStr, marginRight, startY + 18f, paint)

        paint.textAlign = Paint.Align.LEFT
        return startY + 45f
    }

    private fun drawBillTo(
        canvas: android.graphics.Canvas,
        paint: Paint,
        customer: Customer,
        startY: Float
    ): Float {
        val rect = RectF(marginLeft, startY, marginRight, startY + 75f)
        val fillPaint = Paint().apply {
            color = headerFill
            style = Paint.Style.FILL
        }
        canvas.drawRect(rect, fillPaint)

        val padding = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        paint.color = Color.GRAY
        canvas.drawText("BILL TO", marginLeft + padding, startY + 20f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        paint.color = black
        canvas.drawText(customer.name, marginLeft + padding, startY + 40f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        paint.color = Color.DKGRAY
        canvas.drawText(customer.address, marginLeft + padding, startY + 56f, paint)
        canvas.drawText(customer.phoneNumber, marginLeft + padding, startY + 70f, paint)

        return startY + 85f
    }

    private fun drawFinancialSummary(
        canvas: android.graphics.Canvas,
        paint: Paint,
        invoice: Invoice,
        startY: Float
    ): Float {
        var currentY = startY
        val labelX = marginRight - 180f

        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.DKGRAY

        fun drawRow(label: String, value: String) {
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(label, labelX, currentY, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(value, marginRight, currentY, paint)
            currentY += 18f
        }

        drawRow("Total Delivered:", "${invoice.totalDelivered} Bottles")
        drawRow("Total Returned:", "${invoice.totalReturned} Empties")
        drawRow("Net Balance:", "${invoice.totalDelivered - invoice.totalReturned} Bottles")

        currentY += 10f

        // Grand Total Box
        val boxRect = RectF(marginRight - 200f, currentY - 15f, marginRight, currentY + 30f)
        val boxPaint = Paint().apply {
            color = brandBlue
            style = Paint.Style.FILL
        }
        canvas.drawRect(boxRect, boxPaint)

        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 14f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Grand Total", marginRight - 190f, currentY + 12f, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            "Rs. ${invoice.totalAmount.toInt()}",
            marginRight - 10f,
            currentY + 12f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
        return currentY + 50f
    }

    /** Draws the shaded table header row and returns the y for the first data row. */
    private fun drawTableHeader(
        canvas: android.graphics.Canvas,
        paint: Paint,
        startY: Float
    ): Float {
        val headerTop = startY
        val headerBottom = startY + 24f

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(240, 240, 240)
            style = Paint.Style.FILL
        }
        canvas.drawRect(RectF(marginLeft, headerTop - 16f, marginRight, headerBottom), fillPaint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        paint.color = black
        val textY = headerTop

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Date", colDate + 5f, textY, paint)

        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Delivered", colDelivered + 25f, textY, paint)
        canvas.drawText("Returned", colReturned + 25f, textY, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Amount (Rs.)", colAmount - 5f, textY, paint)
        paint.textAlign = Paint.Align.LEFT

        paint.strokeWidth = 1f
        canvas.drawLine(marginLeft, headerTop - 16f, marginRight, headerTop - 16f, paint)
        canvas.drawLine(marginLeft, headerBottom, marginRight, headerBottom, paint)

        return headerBottom + 20f
    }

    /** Draws the "settle your bill" line, THANK YOU, dashed rule, and contact footer. */
    private fun drawFooter(
        canvas: android.graphics.Canvas,
        paint: Paint,
        profile: BusinessProfile,
        startY: Float
    ) {
        var y = startY

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = black
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            "Please settle your bill before the ${ordinal(dueDay)} of every month.",
            centerX, y, paint
        )
        y += 22f

        paint.textSize = 13f
        canvas.drawText("THANK YOU!", centerX, y, paint)
        y += 30f

        // Optional QR code slot: if/when you generate a payment QR (e.g. via ZXing),
        // draw it here with canvas.drawBitmap(qrBitmap, centerX - 30f, y, paint) and
        // advance y by the bitmap height + spacing. Left out since no QR content/lib
        // is wired up yet.

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f
        canvas.drawText("Phone: ${profile.phone}", centerX, y, paint)
        y += 25f

        // Dashed separator like the sample footer rule
        val dashPaint = Paint(paint).apply {
            pathEffect = DashPathEffect(floatArrayOf(6f, 4f), 0f)
            strokeWidth = 1f
        }
        canvas.drawLine(marginLeft, y, marginRight, y, dashPaint)
        y += 15f

        paint.textSize = 10f
        paint.color = Color.DKGRAY
        canvas.drawText("This invoice is electronically generated.", centerX, y, paint)
        paint.color = black
        paint.textAlign = Paint.Align.LEFT
    }

    private fun ordinal(n: Int): String {
        val suffix = when {
            n % 100 in 11..13 -> "th"
            n % 10 == 1 -> "st"
            n % 10 == 2 -> "nd"
            n % 10 == 3 -> "rd"
            else -> "th"
        }
        return "$n$suffix"
    }
}