package balloh.khabib.ahmad.inventorybarang.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import balloh.khabib.ahmad.inventorybarang.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.*

class ChatFragmentAdmin : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button

    private lateinit var messageAdapter: ChatAdapterAdminMessages
    private val firestore = FirebaseFirestore.getInstance()
    private val messagesCollection = firestore.collection("messages")

    private val messages = mutableListOf<ChatMessageAdmin>()
    private var messagesListener: ListenerRegistration? = null
    private var selectedUser: String? = "admin" // Default ke admin, bisa diubah ke user lain.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.activity_chat, container, false)

        recyclerView = binding.findViewById(R.id.recyclerView)
        editTextMessage = binding.findViewById(R.id.editTextMessage)
        buttonSend = binding.findViewById(R.id.buttonSend)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        buttonSend.setOnClickListener {
            sendMessage()
        }

        fetchMessagesRealtime()

        return binding
    }

    private fun fetchMessagesRealtime() {
        messagesListener?.remove() // Hapus listener sebelumnya jika ada

        messagesListener = messagesCollection
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(activity, "Gagal memuat pesan", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    messages.clear()
                    for (document in snapshot.documents) {
                        val message = document.toObject(ChatMessageAdmin::class.java)
                        if (message != null) {
                            messages.add(message)
                        }
                    }
                    displayMessages()
                }
            }
    }

    private fun displayMessages() {
        if (!::messageAdapter.isInitialized) {
            messageAdapter = ChatAdapterAdminMessages(messages)
            recyclerView.adapter = messageAdapter
        } else {
            messageAdapter.updateMessages(messages)
        }
    }

    private fun sendMessage() {
        val messageText = editTextMessage.text.toString()
        if (messageText.isEmpty()) {
            Toast.makeText(activity, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val message = ChatMessageAdmin(
            id = UUID.randomUUID().toString(),
            message = messageText,
            senderId = "admin", // Admin sebagai pengirim
            receiverId = selectedUser ?: "user", // Default user jika tidak dipilih
            timestamp = System.currentTimeMillis()
        )

        messagesCollection.add(message)
            .addOnSuccessListener {
                editTextMessage.text.clear()
                Toast.makeText(activity, "Pesan terkirim", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messagesListener?.remove() // Hapus listener untuk menghindari kebocoran memori
    }
}
