package balloh.khabib.ahmad.inventorybarang.Admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R

class RuangAdapter(
    private val ruangList: List<RuangModel>,
    private val onItemClick: (RuangModel) -> Unit
) : RecyclerView.Adapter<RuangAdapter.RuangViewHolder>() {

    inner class RuangViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idRuang: TextView = view.findViewById(R.id.textIdRuang)
        val nama: TextView = view.findViewById(R.id.textNamaRuang)
        val status: TextView = view.findViewById(R.id.textStatusRuang)

        fun bind(ruang: RuangModel) {
            // Mengisi data ke TextView
            idRuang.text = ruang.id
            nama.text = ruang.nama
            status.text = ruang.status

            // Klik item
            itemView.setOnClickListener { onItemClick(ruang) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ruang, parent, false)
        return RuangViewHolder(view)
    }

    override fun onBindViewHolder(holder: RuangViewHolder, position: Int) {
        holder.bind(ruangList[position])
    }

    override fun getItemCount(): Int = ruangList.size
}
