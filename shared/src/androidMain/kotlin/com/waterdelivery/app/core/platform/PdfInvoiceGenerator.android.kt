package com.waterdelivery.app.core.platform

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.model.Invoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
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
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        var y = 70f

        // ---- Title + logo mark ----------------------------------------------
        y = drawTitleAndLogo(canvas, paint, profile.businessName, y)

        // ---- Meta block: Date / Billing Month / Name -------------------------
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz)
        val dateStr = "%02d/%02d/%04d".format(today.dayOfMonth, today.monthNumber, today.year)

        val billingMonthStr = if (deliveries.isNotEmpty()) {
            val first = Instant.fromEpochMilliseconds(deliveries.first().date).toLocalDateTime(tz)
            "${monthNames[first.monthNumber - 1]}, ${first.year}"
        } else {
            "${monthNames[today.monthNumber - 1]}, ${today.year}"
        }

        y += 15f
        y = drawLabelValue(canvas, paint, "Date:", dateStr, marginLeft, y)
        y = drawLabelValue(canvas, paint, "Billing Month:", billingMonthStr, marginLeft, y)
        y = drawLabelValue(canvas, paint, "Name:", customer.name, marginLeft, y)

        y += 25f

        // ---- Deliveries table --------------------------------------------------
        y = drawTableHeader(canvas, paint, y)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f
        paint.color = black

        deliveries.forEach { delivery ->
            if (y > rowBottomLimit) {
                // finish current page, start a new one, redraw the header
                pdfDocument.finishPage(page)
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 60f
                y = drawTableHeader(canvas, paint, y)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textSize = 12f
                paint.color = black
            }

            val date = Instant.fromEpochMilliseconds(delivery.date).toLocalDateTime(tz)
            val dateCell = "${date.dayOfMonth}/${date.monthNumber}/${date.year}"

            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(dateCell, colDate, y, paint)
            canvas.drawText("${delivery.deliveredQuantity}", colDelivered, y, paint)
            canvas.drawText("${delivery.returnedQuantity}", colReturned, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("PKR ${delivery.totalAmount.toInt()}", colAmount, y, paint)
            paint.textAlign = Paint.Align.LEFT

            y += 22f
        }

        // bottom rule under the last row
        canvas.drawLine(marginLeft, y, marginRight, y, paint)
        y += 12f

        // ---- Grand total, boxed like the sample -------------------------------
        val totalRowTop = y
        y += 26f
        canvas.drawLine(marginLeft, totalRowTop, marginRight, totalRowTop, paint)
        canvas.drawLine(marginLeft, y, marginRight, y, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Grand Total:", 350f, totalRowTop + 18f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("PKR ${invoice.totalAmount.toInt()}", colAmount, totalRowTop + 18f, paint)
        paint.textAlign = Paint.Align.LEFT

        y += 45f

        // ---- Footer -------------------------------------------------------------
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

    /** Draws the "INVOICE" title (bold, underlined) and a simple vector logo mark top-right. */
    private fun drawTitleAndLogo(canvas: android.graphics.Canvas, paint: Paint, businessName: String, startY: Float): Float {
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 26f
        paint.color = black
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("INVOICE", marginLeft, startY, paint)
        val titleWidth = paint.measureText("INVOICE")
        paint.strokeWidth = 1.5f
        canvas.drawLine(marginLeft, startY + 5f, marginLeft + titleWidth, startY + 5f, paint)

        // Simple vector logo mark (circle + droplet) since no image asset is available.
        // Swap this block for canvas.drawBitmap(logoBitmap, ...) if you have a real logo PNG.
        val logoCx = marginRight - 20f
        val logoCy = startY - 15f
        val logoR = 20f

        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = brandBlue
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawCircle(logoCx, logoCy, logoR, ringPaint)

        val dropPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = brandBlue
            style = Paint.Style.FILL
        }
        val drop = Path().apply {
            moveTo(logoCx, logoCy - logoR * 0.55f)
            cubicTo(
                logoCx + logoR * 0.55f, logoCy - logoR * 0.05f,
                logoCx + logoR * 0.35f, logoCy + logoR * 0.55f,
                logoCx, logoCy + logoR * 0.55f
            )
            cubicTo(
                logoCx - logoR * 0.35f, logoCy + logoR * 0.55f,
                logoCx - logoR * 0.55f, logoCy - logoR * 0.05f,
                logoCx, logoCy - logoR * 0.55f
            )
            close()
        }
        canvas.drawPath(drop, dropPaint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        paint.color = brandBlue
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(businessName, logoCx, logoCy + logoR + 16f, paint)
        paint.textAlign = Paint.Align.LEFT

        return startY + 45f
    }

    /** Draws "Label: value" with the label bold and the value underlined, sample-style. */
    private fun drawLabelValue(canvas: android.graphics.Canvas, paint: Paint, label: String, value: String, x: Float, y: Float): Float {
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        paint.color = black
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(label, x, y, paint)
        val labelWidth = paint.measureText("$label ")

        val valueX = x + labelWidth
        canvas.drawText(value, valueX, y, paint)
        val valueWidth = paint.measureText(value)
        paint.strokeWidth = 1f
        canvas.drawLine(valueX, y + 3f, valueX + valueWidth, y + 3f, paint)

        return y + 20f
    }

    /** Draws the shaded table header row and returns the y for the first data row. */
    private fun drawTableHeader(canvas: android.graphics.Canvas, paint: Paint, startY: Float): Float {
        val headerTop = startY
        val headerBottom = startY + 24f

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = headerFill
            style = Paint.Style.FILL
        }
        canvas.drawRect(RectF(marginLeft, headerTop - 16f, marginRight, headerBottom), fillPaint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = black
        val textY = headerTop

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Date", colDate, textY, paint)
        canvas.drawText("Delivered", colDelivered, textY, paint)
        canvas.drawText("Returned", colReturned, textY, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Amount (Rs.)", colAmount, textY, paint)
        paint.textAlign = Paint.Align.LEFT

        paint.strokeWidth = 1f
        canvas.drawLine(marginLeft, headerTop - 16f, marginRight, headerTop - 16f, paint)
        canvas.drawLine(marginLeft, headerBottom, marginRight, headerBottom, paint)

        return headerBottom + 22f
    }

    /** Draws the "settle your bill" line, THANK YOU, dashed rule, and contact footer. */
    private fun drawFooter(canvas: android.graphics.Canvas, paint: Paint, profile: BusinessProfile, startY: Float) {
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