package org.example.util

import com.lowagie.text.Document
import com.lowagie.text.DocumentException
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import org.example.data.Product
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PdfBillGenerator {
    fun generateBill(
        cart: Map<Product, Int>,
        paymentMode: String,
        filePath: String?
    ): String {
        val documentsDir = System.getProperty("user.home") + java.io.File.separator + "Documents"
        val billFile = java.io.File(documentsDir, filePath ?: "bill_${System.currentTimeMillis()}.pdf")
        val document = Document()
        val now = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
        val formattedDate = now.format(dateFormatter)
        try {
            PdfWriter.getInstance(document, FileOutputStream(billFile))
            document.open()
            document.add(Paragraph("LACS Cinemas - Theatre Canteen Billing", com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18f, com.lowagie.text.Font.BOLD)))
            document.add(Paragraph(" "))
            document.add(Paragraph("Date: $formattedDate", com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12f)))
            document.add(Paragraph("Payment Mode: $paymentMode", com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12f)))
            document.add(Paragraph("----------------------------------------"))
            document.add(Paragraph("Item                Qty      Price", com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12f, com.lowagie.text.Font.BOLD)))
            cart.forEach { (product, qty) ->
                val line = String.format("%-18s %3d   ₹%.2f", product.name, qty, product.price * qty)
                document.add(Paragraph(line, com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12f)))
            }
            document.add(Paragraph("----------------------------------------"))
            val total = cart.entries.sumOf { it.key.price * it.value }
            document.add(Paragraph("Total: ₹%.2f".format(total), com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14f, com.lowagie.text.Font.BOLD)))
            document.add(Paragraph(" "))
            document.add(Paragraph("Thank you for your purchase!", com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12f, com.lowagie.text.Font.ITALIC)))
        } catch (e: DocumentException) {
            e.printStackTrace()
        } finally {
            document.close()
        }
        return billFile.absolutePath
    }
}
