package balloh.khabib.ahmad.inventorybarang.Admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R

class ChatAdapterAdminMessages(
    private var messages: List<ChatMessageAdmin>
) : RecyclerView.Adapter<ChatAdapterAdminMessages.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSender: TextView = view.findViewById(R.id.textViewSender)
        val textViewMessage: TextView = view.findViewById(R.id.textViewMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val chatMessage = messages[position]
        holder.textViewSender.text = chatMessage.senderId
        holder.textViewMessage.text = chatMessage.message
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<ChatMessageAdmin>) {
        this.messages = newMessages
        notifyDataSetChanged()
    }
}
