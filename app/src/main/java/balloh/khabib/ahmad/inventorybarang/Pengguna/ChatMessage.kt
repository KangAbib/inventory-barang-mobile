package balloh.khabib.ahmad.inventorybarang.Pengguna


data class ChatMessage(
    val id: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val senderId:String=""
)