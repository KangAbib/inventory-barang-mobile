package balloh.khabib.ahmad.inventorybarang.Pengguna

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PeminjamanFragment : Fragment() {

    private lateinit var spinnerRuang: Spinner
    private lateinit var spinnerBarang: Spinner
    private lateinit var buttonTambahBarang: Button
    private lateinit var editTextTanggalPeminjaman: EditText
    private lateinit var editTextTanggalPengembalian: EditText
    private lateinit var buttonSubmitPeminjaman: Button
    private lateinit var recyclerViewOutputPeminjaman: RecyclerView
    private lateinit var recyclerViewBarangDipilih: RecyclerView

    private lateinit var ruangList: ArrayList<String>
    private lateinit var barangList: ArrayList<String>
    private lateinit var peminjamanList: ArrayList<Peminjaman>
    private lateinit var selectedBarangList: ArrayList<String>

    private lateinit var barangAdapter: BarangAdapter
    private lateinit var peminjamanAdapter: PeminjamanAdapter

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_peminjaman, container, false)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        // Inisialisasi UI
        spinnerRuang = view.findViewById(R.id.spinnerRuang)
        spinnerBarang = view.findViewById(R.id.spinnerBarang)
        buttonTambahBarang = view.findViewById(R.id.buttonTambahBarang)
        editTextTanggalPeminjaman = view.findViewById(R.id.editTextTanggalPeminjaman)
        editTextTanggalPengembalian = view.findViewById(R.id.editTextTanggalPengembalian)
        buttonSubmitPeminjaman = view.findViewById(R.id.buttonSubmitPeminjaman)
        recyclerViewOutputPeminjaman = view.findViewById(R.id.recyclerViewOutputPeminjaman)
        recyclerViewBarangDipilih = view.findViewById(R.id.recyclerViewBarangDipilih)

        // Set LayoutManager untuk RecyclerView
        recyclerViewOutputPeminjaman.layoutManager = LinearLayoutManager(context)
        recyclerViewBarangDipilih.layoutManager = LinearLayoutManager(context)

        // Inisialisasi daftar
        ruangList = ArrayList()
        barangList = ArrayList()
        peminjamanList = ArrayList()
        selectedBarangList = ArrayList()

        // Inisialisasi Adapter
        barangAdapter = BarangAdapter(selectedBarangList, object : BarangAdapter.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                selectedBarangList.removeAt(position)
                barangAdapter.notifyItemRemoved(position)
            }
        })
        recyclerViewBarangDipilih.adapter = barangAdapter

        peminjamanAdapter = PeminjamanAdapter(peminjamanList)
        recyclerViewOutputPeminjaman.adapter = peminjamanAdapter

        // Load data Ruang dan Barang dari Firebase
        loadRuangData()
        loadBarangData()

        // Panggil loadPeminjamanData untuk mendapatkan data peminjaman langsung
        loadPeminjamanData()

        // Button submit peminjaman
        buttonSubmitPeminjaman.setOnClickListener {
            submitPeminjaman()
        }

        // Button untuk menambah barang
        buttonTambahBarang.setOnClickListener {
            val selectedBarang = spinnerBarang.selectedItem.toString()
            if (selectedBarang.isNotEmpty() && !selectedBarangList.contains(selectedBarang)) {
                selectedBarangList.add(selectedBarang)
                barangAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Pilih barang yang valid", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadRuangData() {
        database.child("Ruang").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ruangList.clear()
                for (data in snapshot.children) {
                    val ruang = data.child("nama").getValue(String::class.java)
                    ruang?.let { ruangList.add(it) }
                }
                val adapterRuang = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ruangList)
                adapterRuang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerRuang.adapter = adapterRuang
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat data ruang", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadBarangData() {
        database.child("Barang").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                barangList.clear()
                for (data in snapshot.children) {
                    val barang = data.child("nama").getValue(String::class.java)
                    barang?.let { barangList.add(it) }
                }
                val adapterBarang = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, barangList)
                adapterBarang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerBarang.adapter = adapterBarang
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat data barang", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadPeminjamanData() {
        // Memuat data peminjaman saat fragment dibuka
        database.child("peminjaman").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                peminjamanList.clear()
                for (data in snapshot.children) {
                    val peminjaman = data.getValue(Peminjaman::class.java)
                    peminjaman?.let {
                        peminjamanList.add(it)
                    }
                }
                peminjamanAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat data peminjaman", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitPeminjaman() {
        val selectedRuang = spinnerRuang.selectedItem.toString()
        val tanggalPeminjaman = editTextTanggalPeminjaman.text.toString()
        val tanggalPengembalian = editTextTanggalPengembalian.text.toString()
        val userId = auth.currentUser?.uid ?: ""

        if (selectedRuang.isEmpty() || selectedBarangList.isEmpty() || tanggalPeminjaman.isEmpty() || tanggalPengembalian.isEmpty()) {
            Toast.makeText(requireContext(), "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        // Ambil nama pengguna dari Firestore berdasarkan userId
        firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val userName = document.getString("fullName") ?: "Nama Tidak Tersedia"

                val peminjamanId = generatePeminjamanId()

                val peminjaman = Peminjaman(
                    id = peminjamanId,
                    userName = userName,
                    ruangId = selectedRuang,
                    barangId = selectedBarangList.joinToString(", "),
                    tanggalPinjam = tanggalPeminjaman,
                    tanggalKembali = tanggalPengembalian,
                    status = "Sedang Pinjam"
                )

                database.child("peminjaman").child(peminjamanId).setValue(peminjaman).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "Peminjaman berhasil diajukan", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Gagal mengajukan peminjaman", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generatePeminjamanId(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val date = Date()
        return "PJM_" + dateFormat.format(date)
    }
}
