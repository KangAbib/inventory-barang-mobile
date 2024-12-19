package balloh.khabib.ahmad.inventorybarang.Admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R

class ChatAdapterAdmin(
    private val items: List<String>,
    private val onClick: (String) -> Unit = {}
) : RecyclerView.Adapter<ChatAdapterAdmin.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewUser: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val userName = items[position]
        holder.textViewUser.text = userName
        holder.itemView.setOnClickListener {
            onClick(userName)
        }
    }

    override fun getItemCount(): Int = items.size
}
