package balloh.khabib.ahmad.inventorybarang.Pengguna

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R
import com.google.firebase.database.*

class PengembalianFragment : Fragment() {

    private lateinit var recyclerViewDipinjam: RecyclerView
    private lateinit var recyclerViewDikembalikan: RecyclerView
    private lateinit var buttonSubmit: Button
    private lateinit var buttonDenda: Button // Tambahkan Button Denda
    private lateinit var editTextTanggalPengembalian: EditText
    private lateinit var barangAdapter: BarangAdapter
    private lateinit var pengembalianAdapter: PengembalianAdapter

    private val barangList = mutableListOf<String>()
    private val pengembalianList = mutableListOf<Pengembalian>()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_pengembalian, container, false)

        // Inisialisasi RecyclerView dan Button
        recyclerViewDipinjam = view.findViewById(R.id.recyclerViewBarangDipinjam)
        recyclerViewDikembalikan = view.findViewById(R.id.recyclerViewBarangDikembalikan)
        buttonSubmit = view.findViewById(R.id.buttonSubmitPengembalian)
        buttonDenda = view.findViewById(R.id.buttonDenda) // Inisialisasi buttonDenda
        editTextTanggalPengembalian = view.findViewById(R.id.editTextTanggalPengembalian)

        // Setup RecyclerView untuk barang yang dipinjam
        recyclerViewDipinjam.layoutManager = LinearLayoutManager(context)
        barangAdapter = BarangAdapter(barangList, object : BarangAdapter.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                removeBarang(position)
            }
        })
        recyclerViewDipinjam.adapter = barangAdapter

        // Setup RecyclerView untuk barang yang dikembalikan
        recyclerViewDikembalikan.layoutManager = LinearLayoutManager(context)
        pengembalianAdapter = PengembalianAdapter(pengembalianList)
        recyclerViewDikembalikan.adapter = pengembalianAdapter

        // Memuat data barang yang sedang dipinjam
        loadPeminjamanData()

        // Memuat data barang yang dikembalikan
        loadPengembalianData()

        // Aksi tombol submit
        buttonSubmit.setOnClickListener {
            val tanggalPengembalian = editTextTanggalPengembalian.text.toString()
            if (tanggalPengembalian.isNotEmpty() && barangList.isNotEmpty()) {
                submitPengembalian(tanggalPengembalian)
            } else {
                Toast.makeText(context, "Harap isi tanggal pengembalian dan pilih barang", Toast.LENGTH_SHORT).show()
            }
        }

        // Aksi tombol denda
        buttonDenda.setOnClickListener {
            openDendaLink()
        }

        return view
    }

    // Membuka link denda menggunakan Intent
    private fun openDendaLink() {
        val dendaUrl = "https://app.sandbox.midtrans.com/payment-links/1734576671375"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dendaUrl))
        startActivity(intent)
    }

    // Mengambil data barang yang dipinjam dengan status "Sedang Pinjam"
    private fun loadPeminjamanData() {
        val peminjamanRef = database.child("peminjaman")

        peminjamanRef.orderByChild("status").equalTo("Sedang Pinjam")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    barangList.clear()

                    if (!snapshot.exists()) {
                        Toast.makeText(context, "Tidak ada barang yang sedang dipinjam", Toast.LENGTH_SHORT).show()
                        return
                    }

                    for (data in snapshot.children) {
                        val barangId = data.child("barangId").getValue(String::class.java)
                        if (barangId != null) {
                            barangList.add(barangId)
                        }
                    }

                    barangAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Mengambil data barang yang sudah dikembalikan dari Firebase
    private fun loadPengembalianData() {
        val pengembalianRef = database.child("Pengembalian")

        pengembalianRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pengembalianList.clear()

                for (data in snapshot.children) {
                    val pengembalian = data.getValue(Pengembalian::class.java)
                    if (pengembalian != null) {
                        pengembalianList.add(pengembalian)
                    }
                }

                pengembalianAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error loading data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Menghapus barang dari daftar barang yang dipinjam
    private fun removeBarang(position: Int) {
        barangList.removeAt(position)
        barangAdapter.notifyItemRemoved(position)
    }

    // Menyimpan data pengembalian ke Firebase Realtime Database
    private fun submitPengembalian(tanggalPengembalian: String) {
        val barangDikembalikan = mutableListOf<String>()
        barangDikembalikan.addAll(barangList)

        val pengembalianData = Pengembalian(
            tanggal_pengembalian = tanggalPengembalian,
            barang_dikembalikan = barangDikembalikan
        )

        database.child("Pengembalian").push().setValue(pengembalianData)
            .addOnSuccessListener {
                Toast.makeText(context, "Pengembalian berhasil disubmit!", Toast.LENGTH_SHORT).show()
                updateStatusBarang()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error adding data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Mengubah status barang yang dikembalikan menjadi "Sudah Kembali"
    private fun updateStatusBarang() {
        val peminjamanRef = database.child("peminjaman")

        peminjamanRef.orderByChild("status").equalTo("Sedang Pinjam")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val barangId = data.child("barangId").getValue(String::class.java)
                        if (barangId != null && barangList.contains(barangId)) {
                            data.ref.child("status").setValue("Sudah Kembali")
                        }
                    }

                    // Kosongkan daftar barang setelah status diperbarui
                    barangList.clear()
                    barangAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error updating status: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
