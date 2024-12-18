package balloh.khabib.ahmad.inventorybarang.Pengguna

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R

class BarangAdapter(
    private val barangList: MutableList<String>,
    private val onDeleteClickListener: OnDeleteClickListener
) : RecyclerView.Adapter<BarangAdapter.BarangViewHolder>() {

    // Interface untuk menangani klik tombol silang
    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    // ViewHolder untuk setiap item
    inner class BarangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNamaBarang: TextView = itemView.findViewById(R.id.textViewNamaBarang)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_barang_dipinjam, parent, false)
        return BarangViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val namaBarang = barangList[position]
        holder.textViewNamaBarang.text = namaBarang

        // Event untuk tombol delete
        holder.buttonDelete.setOnClickListener {
            onDeleteClickListener.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = barangList.size

    // Fungsi untuk menghapus item dari daftar
    fun removeItem(position: Int) {
        barangList.removeAt(position)
        notifyItemRemoved(position)
    }
}
