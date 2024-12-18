package balloh.khabib.ahmad.inventorybarang

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.database.*

class ChartActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        // Inisialisasi BarChart
        barChart = findViewById(R.id.bar_chart)
        barChart.setFitBars(true)

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("peminjaman")

        // Ambil data dari Firebase
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        // Mengambil data dari Firebase Realtime Database
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countMap = mutableMapOf<String, Int>()

                // Iterasi data untuk menghitung jumlah peminjaman per tanggal
                for (data in snapshot.children) {
                    val tanggalPinjam = data.child("tanggalPinjam").getValue(String::class.java)
                    if (tanggalPinjam != null) {
                        countMap[tanggalPinjam] = countMap.getOrDefault(tanggalPinjam, 0) + 1
                    }
                }

                // Log untuk memastikan data yang diambil
                Log.d("ChartActivity", "Data: ${countMap}")

                // Jika tidak ada data yang dihitung
                if (countMap.isEmpty()) {
                    return
                }

                // Konversi countMap menjadi BarEntries
                val entries = countMap.entries.mapIndexed { index, entry ->
                    BarEntry(index.toFloat(), entry.value.toFloat())
                }

                // Konversi tanggal untuk Label X
                val labels = countMap.keys.toList()

                // Update chart dengan data yang diambil
                updateBarChart(entries, labels)
            }

            override fun onCancelled(error: DatabaseError) {
                // Tangani error jika gagal mengambil data
                Log.e("ChartActivity", "Failed to read data.", error.toException())
            }
        })
    }

    private fun updateBarChart(entries: List<BarEntry>, labels: List<String>) {
        // Membuat dataset untuk chart
        val barDataSet = BarDataSet(entries, "Jumlah Peminjaman")
        barDataSet.color = Color.BLUE
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 12f

        // Mengatur data pada chart
        val barData = BarData(barDataSet)
        barChart.data = barData

        // Mengatur label sumbu X
        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return labels.getOrNull(value.toInt()) ?: ""
            }
        }

        // Mengatur sumbu Y
        barChart.axisLeft.textSize = 12f
        barChart.axisLeft.textColor = Color.BLACK
        barChart.axisRight.isEnabled = false

        // Menonaktifkan deskripsi dan legend
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false

        // Refresh chart
        barChart.invalidate()
    }
}
