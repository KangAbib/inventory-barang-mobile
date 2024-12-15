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
import balloh.khabib.ahmad.inventorybarang.Admin.RuangModel

class RuangFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var gridLayoutRuang: GridLayout
    private val ruangList = mutableListOf<RuangModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.ruang_pengguna, container, false)

        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Ruang")

        // Inisialisasi GridLayout
        gridLayoutRuang = view.findViewById(R.id.gridLayoutRuang)

        // Baca data dari Firebase
        fetchRuangData()

        return view
    }

    private fun fetchRuangData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ruangList.clear()
                gridLayoutRuang.removeAllViews()

                for (data in snapshot.children) {
                    val ruang = data.getValue(RuangModel::class.java)
                    if (ruang != null) {
                        ruangList.add(ruang)
                        addRuangToGrid(ruang)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat data ruang", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addRuangToGrid(ruang: RuangModel) {
        // Inflating layout item_ruang_card.xml
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_ruang_card, gridLayoutRuang, false)

        // Mengisi data ke item card
        val idRuangTextView = itemView.findViewById<TextView>(R.id.textIdRuang)
        val namaRuangTextView = itemView.findViewById<TextView>(R.id.textNamaRuang)
        val statusRuangTextView = itemView.findViewById<TextView>(R.id.textStatusRuang)

        idRuangTextView.text = "ID : ${ruang.id}"
        namaRuangTextView.text = "Nama : ${ruang.nama}"
        statusRuangTextView.text = "Status : ${ruang.status}"

        // Menambahkan item ke GridLayout
        gridLayoutRuang.addView(itemView)
    }
}
