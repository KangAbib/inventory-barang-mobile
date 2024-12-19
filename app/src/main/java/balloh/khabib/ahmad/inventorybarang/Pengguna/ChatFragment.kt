package balloh.khabib.ahmad.inventorybarang.Pengguna

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
import com.google.firebase.firestore.Query
import com.google.firebase.auth.FirebaseAuth

class ChatFragment : Fragment() {

    // Declaring Necessary variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var chatAdapter: ChatAdapter
    private val firestore = FirebaseFirestore.getInstance() // Getting Firestore instance.
    private val messagesCollection = firestore.collection("messages") // Our messages will be stored and fetched from "messages" collection
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var userName: String

    // Firebase Authentication instance to get the current user's ID
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = inflater.inflate(R.layout.activity_chat, container, false)

        recyclerView = binding.findViewById(R.id.recyclerView)
        editTextMessage = binding.findViewById(R.id.editTextMessage)
        buttonSend = binding.findViewById(R.id.buttonSend)

        // Get the current user ID
        val userId = auth.currentUser?.uid ?: ""

        // Fetch the user full name from Firestore
        if (userId.isNotEmpty()) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Get the fullName field from the user's document
                        userName = document.getString("fullName") ?: "Anonymous"
                        setupChat() // Proceed with chat setup after retrieving the user name
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Failed to get user info", Toast.LENGTH_SHORT).show()
                    userName = "Anonymous" // Set a fallback username if the fetch fails
                    setupChat() // Proceed with chat setup even if the user fetch failed
                }
        } else {
            userName = "Anonymous" // Set a fallback if no user is logged in
            setupChat() // Proceed with chat setup
        }

        return binding
    }

    private fun setupChat() {
        // Initialize the adapter and RecyclerView once the user name is fetched
        chatAdapter = ChatAdapter(messages)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        fetchExistingMessages()

        buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString()
            // Update the database with new messages.
            if (messageText.isNotEmpty()) {
                val message = ChatMessage(
                    id = messagesCollection.document().id,
                    message = messageText,
                    senderId = userName
                )
                messagesCollection.document(message.id).set(message)
                editTextMessage.text.clear()
            }
        }

        // Mock data for preview purposes
        messagesCollection.orderBy("timestamp", Query.Direction.ASCENDING)
            // Snapshot Listener listens to any changes inside the collection, If there is any change, Update the recyclerView. This helps chat update Realtime.
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(activity, "Unable to get new Messages", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    messages.clear()
                    for (document in snapshot.documents) {
                        // Convert the data directly into class variable
                        val message = document.toObject(ChatMessage::class.java)
                        if (message != null) {
                            // Add the new message into the list of messages
                            messages.add(message)
                        }
                    }
                    // Update the RecyclerView.
                    chatAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }
    }

    private fun fetchExistingMessages() {
        // Getting any previous messages and ordering them with respect to time they got sent.
        messagesCollection.orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    messages.clear()
                    for (document in snapshot.documents) {
                        // Add all the messages into the list 'messages'
                        val message = document.toObject(ChatMessage::class.java)
                        if (message != null) {
                            messages.add(message)
                        }
                    }
                    // Update the RecyclerView with the messages.
                    chatAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Unable to fetch Messages", Toast.LENGTH_SHORT).show()
            }
    }
}
