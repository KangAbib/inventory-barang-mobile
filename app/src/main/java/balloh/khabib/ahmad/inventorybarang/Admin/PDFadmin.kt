package balloh.khabib.ahmad.inventorybarang.Admin

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import java.io.File
import java.time.LocalDate
import balloh.khabib.ahmad.inventorybarang.R

class PDFadmin : Fragment() {

    private lateinit var database: DatabaseReference
    private val STORAGE_PERMISSION_CODE = 100
    private lateinit var tvStatus: TextView
    private val POST_NOTIFICATION_PERMISSION_CODE = 101

    data class Peminjaman(
        val peminjaman_id: String? = null,
        val nama_user: String? = null,
        val barang: String? = null,
        val ruang: String? = null,
        val tanggal_pinjam: String? = null,
        val tanggal_kembali: String? = null,
        val status: String? = null
    )

    data class Pengembalian(
        val barang_dikembalikan: String? = null,
        val tanggal_pengembalian: String? = null
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pdf_admin, container, false)
        database = FirebaseDatabase.getInstance().reference
        tvStatus = view.findViewById(R.id.tv_status)

        val btnExportPdf = view.findViewById<Button>(R.id.btn_export_pdf)
        btnExportPdf.setOnClickListener {
            Log.d("PDFadmin", "Tombol Export PDF Ditekan")

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("PDFadmin", "Izin WRITE_EXTERNAL_STORAGE tidak diberikan.")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                Log.d("PDFadmin", "Izin WRITE_EXTERNAL_STORAGE sudah diberikan.")
                val fileName =
                    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/LaporanPeminjamanPengembalian.pdf"
                tvStatus.text = "Mengekspor PDF... Mohon tunggu."
                fetchAndExportData(fileName)
            }
        }

        return view
    }

    private fun fetchAndExportData(fileName: String) {
        val combinedData = mutableListOf<List<String>>()

        // Ambil data peminjaman
        database.child("peminjaman").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val peminjaman = childSnapshot.getValue(Peminjaman::class.java)
                    if (peminjaman != null) {
                        combinedData.add(
                            listOf(
                                "Peminjaman",
                                peminjaman.nama_user ?: "-",
                                peminjaman.barang ?: "-",
                                peminjaman.ruang ?: "-",
                                peminjaman.tanggal_pinjam ?: "-",
                                peminjaman.tanggal_kembali ?: "-",
                                peminjaman.status ?: "-"
                            )
                        )
                    }
                }

                // Ambil data pengembalian
                database.child("Pengembalian").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val pengembalian = childSnapshot.getValue(Pengembalian::class.java)
                            if (pengembalian != null) {
                                combinedData.add(
                                    listOf(
                                        "Pengembalian",
                                        "-",
                                        pengembalian.barang_dikembalikan ?: "-",
                                        "-",
                                        "-",
                                        pengembalian.tanggal_pengembalian ?: "-",
                                        "-"
                                    )
                                )
                            }
                        }
                        try {
                            exportPdf(fileName, combinedData)
                            tvStatus.text = "PDF berhasil diekspor ke: $fileName"
                        } catch (e: Exception) {
                            tvStatus.text = "Gagal mengekspor PDF: ${e.message}"
                            Log.e("PDFadmin", "Gagal mengekspor PDF: ${e.message}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        tvStatus.text = "Error mengambil data Pengembalian: ${error.message}"
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                tvStatus.text = "Error mengambil data Peminjaman: ${error.message}"
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun exportPdf(fileName: String, data: List<List<String>>) {
        try {
            // Buat file di direktori internal aplikasi
            val downloadsDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDirectory, "LaporanPeminjamanPengembalian.pdf")
            Log.d("PDFadmin", "Membuat file di: ${file.absolutePath}")

            // Tulis PDF ke file
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            document.add(Paragraph("Laporan Peminjaman dan Pengembalian").setFontSize(18f).setBold())
            document.add(Paragraph("Data Peminjaman dan Pengembalian:").setFontSize(12f))

            val header = listOf(
                "Tipe Data", "Nama User", "Barang", "Ruang",
                "Tgl Pinjam", "Tgl Kembali", "Status"
            )
            val columnWidths = floatArrayOf(2f, 3f, 3f, 3f, 3f, 3f, 2f)
            val table = Table(columnWidths)

            for (heading in header) table.addCell(heading)
            for (row in data) {
                for (cell in row) table.addCell(cell)
            }

            document.add(table)
            document.add(Paragraph("\nGenerated on: ${LocalDate.now()}").setFontSize(10f).setItalic())
            document.close()

            // Pindahkan file ke folder Download
            val externalFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "LaporanPeminjamanPengembalian.pdf"
            )
            file.copyTo(externalFile, overwrite = true)
            Log.d("PDFadmin", "File dipindahkan ke: ${externalFile.absolutePath}")

            // Scan file agar terlihat di galeri
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(externalFile.absolutePath),
                arrayOf("application/pdf")
            ) { _, uri ->
                Log.d("PDFadmin", "File discan: $uri")
            }

            // Kirim notifikasi jika izin diberikan
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    sendNotification()
                } else {
                    // Minta izin jika belum diberikan
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        POST_NOTIFICATION_PERMISSION_CODE
                    )
                }
            }

            Toast.makeText(requireContext(), "PDF berhasil dibuat dan diunduh", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("PDFadmin", "Error saat membuat PDF: ${e.message}")
            tvStatus.text = "Gagal membuat PDF: ${e.message}"
            throw e
        }
    }

    private fun sendNotification() {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "pdf_export_channel"
        val notificationChannel = NotificationChannel(
            channelId,
            "PDF Export Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Ekspor PDF Selesai")
            .setContentText("Laporan Peminjaman dan Pengembalian berhasil diunduh.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}
