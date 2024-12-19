package balloh.khabib.ahmad.inventorybarang.Admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R
import com.google.firebase.database.*

class BarangAdmin : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var editTextNamaBarang: EditText
    private lateinit var spinnerStatusBarang: Spinner
    private lateinit var recyclerViewBarang: RecyclerView
    private lateinit var btnTambah: Button
    private lateinit var btnEdit: Button
    private lateinit var btnHapus: Button
    private val barangList = mutableListOf<BarangModel>()
    private var selectedBarang: BarangModel? = null
    private lateinit var btnSortNamaBarang: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Memuat layout barang_admin.xml
        val view = inflater.inflate(R.layout.barang_admin, container, false)

        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Barang")

        // Inisialisasi UI
        editTextNamaBarang = view.findViewById(R.id.editTextNamaBarang)
        spinnerStatusBarang = view.findViewById(R.id.spinnerStatusBarang)
        recyclerViewBarang = view.findViewById(R.id.recyclerViewBarang)
        btnTambah = view.findViewById(R.id.btnTambahBarang)
        btnEdit = view.findViewById(R.id.btnEditBarang)
        btnHapus = view.findViewById(R.id.btnHapusBarang)

        // Setup spinner dengan status
        val statusList = listOf("Tersedia", "Tidak Tersedia")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusBarang.adapter = spinnerAdapter

        // Setup RecyclerView
        recyclerViewBarang.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BarangAdapter(barangList) { barang ->
            onItemSelected(barang)
        }
        recyclerViewBarang.adapter = adapter

        // Baca data dari Firebase
        fetchBarangData(adapter)

        // Inisialisasi tombol sorting
        btnSortNamaBarang = view.findViewById(R.id.btnSortNamaBarang)

        // Tambahkan listener pada tombol sorting
        btnSortNamaBarang.setOnClickListener {
            val isAscending = btnSortNamaBarang.text == "Sort Ascending"
            if (isAscending) {
                barangList.sortBy { it.nama }
                btnSortNamaBarang.text = "Sort Descending"
                Toast.makeText(requireContext(), "Sorted ascending by Nama Barang", Toast.LENGTH_SHORT).show()
            } else {
                barangList.sortByDescending { it.nama }
                btnSortNamaBarang.text = "Sort Ascending"
                Toast.makeText(requireContext(), "Sorted descending by Nama Barang", Toast.LENGTH_SHORT).show()
            }
            adapter.notifyDataSetChanged()
        }

        // Tombol Tambah
        btnTambah.setOnClickListener {
            val nama = editTextNamaBarang.text.toString()
            val status = spinnerStatusBarang.selectedItem.toString()
            if (nama.isNotEmpty()) {
                addBarang(nama, status)
            } else {
                Toast.makeText(requireContext(), "Nama barang tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Edit
        btnEdit.setOnClickListener {
            val nama = editTextNamaBarang.text.toString()
            val status = spinnerStatusBarang.selectedItem.toString()
            if (selectedBarang != null) {
                updateBarang(selectedBarang!!.id, nama, status)
            } else {
                Toast.makeText(requireContext(), "Pilih barang untuk diubah", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Hapus
        btnHapus.setOnClickListener {
            if (selectedBarang != null) {
                deleteBarang(selectedBarang!!.id)
            } else {
                Toast.makeText(requireContext(), "Pilih barang untuk dihapus", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }

    private fun fetchBarangData(adapter: BarangAdapter) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                barangList.clear()
                for (data in snapshot.children) {
                    val barang = data.getValue(BarangModel::class.java)
                    if (barang != null) {
                        barangList.add(barang)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BarangAdmin", "Error fetching data", error.toException())
            }
        })
    }

    private fun addBarang(nama: String, status: String) {
        // Ambil ID terakhir dari Firebase
        database.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastId = snapshot.children.firstOrNull()?.key
                val nextId = generateNextId(lastId)
                if (nextId != null) {
                    val barang = BarangModel(nextId, nama, status)
                    database.child(nextId).setValue(barang).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            clearInputFields()
                        } else {
                            Toast.makeText(requireContext(), "Gagal menambahkan barang", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal menghasilkan ID baru", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BarangAdmin", "Gagal membaca ID terakhir", error.toException())
                Toast.makeText(requireContext(), "Gagal membaca ID terakhir", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBarang(id: String, nama: String, status: String) {
        val barang = mapOf(
            "id" to id,
            "nama" to nama,
            "status" to status
        )
        database.child(id).updateChildren(barang).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Barang berhasil diubah", Toast.LENGTH_SHORT).show()
                clearInputFields()
                selectedBarang = null
            } else {
                Toast.makeText(requireContext(), "Gagal mengubah barang", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteBarang(id: String) {
        database.child(id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Barang berhasil dihapus", Toast.LENGTH_SHORT).show()
                clearInputFields()
                selectedBarang = null
            } else {
                Toast.makeText(requireContext(), "Gagal menghapus barang", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onItemSelected(barang: BarangModel) {
        selectedBarang = barang
        editTextNamaBarang.setText(barang.nama)
        val spinnerPosition = (spinnerStatusBarang.adapter as ArrayAdapter<String>).getPosition(barang.status)
        spinnerStatusBarang.setSelection(spinnerPosition)
    }

    private fun clearInputFields() {
        editTextNamaBarang.text.clear()
        spinnerStatusBarang.setSelection(0)
    }

    private fun generateNextId(lastId: String?): String? {
        return if (lastId != null && lastId.startsWith("BRG_")) {
            val numericPart = lastId.substringAfter("BRG_").toIntOrNull()
            if (numericPart != null) {
                "BRG_${String.format("%03d", numericPart + 1)}"
            } else null
        } else {
            "BRG_001"
        }
    }
}
