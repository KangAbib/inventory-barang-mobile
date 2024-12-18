package balloh.khabib.ahmad.inventorybarang.Admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R

class BarangAdapter(
    private val barangList: List<BarangModel>,
    private val onItemClick: (BarangModel) -> Unit
) : RecyclerView.Adapter<BarangAdapter.BarangViewHolder>() {

    inner class BarangViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idBarang: TextView = view.findViewById(R.id.textIdBarang)  // ID barang
        val nama: TextView = view.findViewById(R.id.textNamaBarang)   // Nama barang
        val status: TextView = view.findViewById(R.id.textStatusBarang)  // Status barang

        fun bind(barang: BarangModel) {
            // Mengisi data ke TextView
            idBarang.text = barang.id
            nama.text = barang.nama
            status.text = barang.status

            // Klik item
            itemView.setOnClickListener { onItemClick(barang) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang, parent, false)  // Ganti dengan layout item barang
        return BarangViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        holder.bind(barangList[position])
    }

    override fun getItemCount(): Int = barangList.size
}
