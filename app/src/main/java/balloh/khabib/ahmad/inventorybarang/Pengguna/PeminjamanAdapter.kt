package balloh.khabib.ahmad.inventorybarang.Pengguna

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R

class PeminjamanAdapter(private val peminjamanList: ArrayList<Peminjaman>) :
    RecyclerView.Adapter<PeminjamanAdapter.PeminjamanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeminjamanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_peminjaman, parent, false)
        return PeminjamanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeminjamanViewHolder, position: Int) {
        val peminjaman = peminjamanList[position]
        holder.bind(peminjaman)
    }

    override fun getItemCount(): Int {
        return peminjamanList.size
    }

    class PeminjamanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewId: TextView = itemView.findViewById(R.id.textViewPeminjamanId)
        private val textViewUserName: TextView = itemView.findViewById(R.id.textViewUserName)
        private val textViewRuang: TextView = itemView.findViewById(R.id.textViewRuang)
        private val textViewBarang: TextView = itemView.findViewById(R.id.textViewBarang)
        private val textViewTanggalPinjam: TextView = itemView.findViewById(R.id.textViewTanggalPinjam)
        private val textViewTanggalKembali: TextView = itemView.findViewById(R.id.textViewTanggalKembali)

        fun bind(peminjaman: Peminjaman) {
            textViewId.text = peminjaman.id
            textViewUserName.text = peminjaman.userName
            textViewRuang.text = peminjaman.ruangId
            textViewBarang.text = peminjaman.barangId
            textViewTanggalPinjam.text = peminjaman.tanggalPinjam
            textViewTanggalKembali.text = peminjaman.tanggalKembali
        }
    }
}
