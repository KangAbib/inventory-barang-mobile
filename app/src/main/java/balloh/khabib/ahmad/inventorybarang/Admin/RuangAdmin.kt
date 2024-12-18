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

class RuangAdmin : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var editTextNamaRuang: EditText
    private lateinit var spinnerStatusRuang: Spinner
    private lateinit var recyclerViewRuang: RecyclerView
    private lateinit var btnTambah: Button
    private lateinit var btnEdit: Button
    private lateinit var btnHapus: Button
    private val ruangList = mutableListOf<RuangModel>()
    private var selectedRuang: RuangModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Memuat layout ruang_admin.xml
        val view = inflater.inflate(R.layout.ruang_admin, container, false)

        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Ruang")

        // Inisialisasi UI
        editTextNamaRuang = view.findViewById(R.id.editTextNamaRuang)
        spinnerStatusRuang = view.findViewById(R.id.spinnerStatusRuang)
        recyclerViewRuang = view.findViewById(R.id.recyclerViewRuang)
        btnTambah = view.findViewById(R.id.btnTambah)
        btnEdit = view.findViewById(R.id.btnEdit)
        btnHapus = view.findViewById(R.id.btnHapus)

        // Setup spinner dengan status
        val statusList = listOf("Tersedia", "Tidak Tersedia")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusRuang.adapter = spinnerAdapter

        // Setup RecyclerView
        recyclerViewRuang.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RuangAdapter(ruangList) { ruang ->
            onItemSelected(ruang)
        }
        recyclerViewRuang.adapter = adapter

        // Baca data dari Firebase
        fetchRuangData(adapter)

        // Tombol Tambah
        btnTambah.setOnClickListener {
            val nama = editTextNamaRuang.text.toString()
            val status = spinnerStatusRuang.selectedItem.toString()
            if (nama.isNotEmpty()) {
                addRuang(nama, status)
            } else {
                Toast.makeText(requireContext(), "Nama ruang tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Edit
        btnEdit.setOnClickListener {
            val nama = editTextNamaRuang.text.toString()
            val status = spinnerStatusRuang.selectedItem.toString()
            if (selectedRuang != null) {
                updateRuang(selectedRuang!!.id, nama, status)
            } else {
                Toast.makeText(requireContext(), "Pilih ruang untuk diubah", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Hapus
        btnHapus.setOnClickListener {
            if (selectedRuang != null) {
                deleteRuang(selectedRuang!!.id)
            } else {
                Toast.makeText(requireContext(), "Pilih ruang untuk dihapus", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun fetchRuangData(adapter: RuangAdapter) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ruangList.clear()
                for (data in snapshot.children) {
                    val ruang = data.getValue(RuangModel::class.java)
                    if (ruang != null) {
                        ruangList.add(ruang)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RuangAdmin", "Error fetching data", error.toException())
            }
        })
    }

    private fun addRuang(nama: String, status: String) {
        // Ambil ID terakhir dari Firebase
        database.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastId = snapshot.children.firstOrNull()?.key
                val nextId = generateNextId(lastId)
                if (nextId != null) {
                    val ruang = RuangModel(nextId, nama, status)
                    database.child(nextId).setValue(ruang).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Ruang berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            clearInputFields()
                        } else {
                            Toast.makeText(requireContext(), "Gagal menambahkan ruang", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal menghasilkan ID baru", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RuangAdmin", "Gagal membaca ID terakhir", error.toException())
                Toast.makeText(requireContext(), "Gagal membaca ID terakhir", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRuang(id: String, nama: String, status: String) {
        val ruang = mapOf(
            "id" to id,
            "nama" to nama,
            "status" to status
        )
        database.child(id).updateChildren(ruang).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Ruang berhasil diubah", Toast.LENGTH_SHORT).show()
                clearInputFields()
                selectedRuang = null
            } else {
                Toast.makeText(requireContext(), "Gagal mengubah ruang", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteRuang(id: String) {
        database.child(id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Ruang berhasil dihapus", Toast.LENGTH_SHORT).show()
                clearInputFields()
                selectedRuang = null
            } else {
                Toast.makeText(requireContext(), "Gagal menghapus ruang", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onItemSelected(ruang: RuangModel) {
        selectedRuang = ruang
        editTextNamaRuang.setText(ruang.nama)
        val spinnerPosition = (spinnerStatusRuang.adapter as ArrayAdapter<String>).getPosition(ruang.status)
        spinnerStatusRuang.setSelection(spinnerPosition)
    }

    private fun clearInputFields() {
        editTextNamaRuang.text.clear()
        spinnerStatusRuang.setSelection(0)
    }

    private fun generateNextId(lastId: String?): String? {
        return if (lastId != null && lastId.startsWith("KLS_")) {
            val numericPart = lastId.substringAfter("KLS_").toIntOrNull()
            if (numericPart != null) {
                "KLS_${String.format("%03d", numericPart + 1)}"
            } else null
        } else {
            "KLS_001"
        }
    }
}
