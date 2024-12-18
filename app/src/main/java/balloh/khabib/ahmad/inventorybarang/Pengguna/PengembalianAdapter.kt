package balloh.khabib.ahmad.inventorybarang.Pengguna

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R

class PengembalianAdapter(private val pengembalianList: List<Pengembalian>) :
    RecyclerView.Adapter<PengembalianAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTanggal: TextView = itemView.findViewById(R.id.textViewNamaBarang)
        val textBarang: TextView = itemView.findViewById(R.id.textViewTanggalPengembalian)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pengembalian, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pengembalian = pengembalianList[position]
        holder.textTanggal.text = pengembalian.tanggal_pengembalian
        holder.textBarang.text = pengembalian.barang_dikembalikan?.joinToString(", ")
    }

    override fun getItemCount(): Int {
        return pengembalianList.size
    }
}
