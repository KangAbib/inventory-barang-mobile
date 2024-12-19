package balloh.khabib.ahmad.inventorybarang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import android.widget.TextView
import balloh.khabib.ahmad.inventorybarang.Admin.BarangModel

class BarangFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var gridLayoutBarang: GridLayout
    private val barangList = mutableListOf<BarangModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.barang_pengguna, container, false)

        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Barang")

        // Inisialisasi GridLayout
        gridLayoutBarang = view.findViewById(R.id.gridLayoutBarang)

        // Baca data dari Firebase
        fetchBarangData()

        return view
    }

    private fun fetchBarangData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                barangList.clear()
                gridLayoutBarang.removeAllViews()

                for (data in snapshot.children) {
                    val barang = data.getValue(BarangModel::class.java)
                    if (barang != null) {
                        barangList.add(barang)
                        addBarangToGrid(barang)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat data barang", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addBarangToGrid(barang: BarangModel) {
        // Inflating layout item_barang_card.xml
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_barang_card, gridLayoutBarang, false)

        // Mengisi data ke item card
        val idBarangTextView = itemView.findViewById<TextView>(R.id.textIdBarang)
        val namaBarangTextView = itemView.findViewById<TextView>(R.id.textNamaBarang)
        val statusBarangTextView = itemView.findViewById<TextView>(R.id.textStatusBarang)

        idBarangTextView.text = "ID : ${barang.id}"
        namaBarangTextView.text = "Nama : ${barang.nama}"
        statusBarangTextView.text = "Status : ${barang.status}"

        // Menambahkan item ke GridLayout
        gridLayoutBarang.addView(itemView)
    }
}