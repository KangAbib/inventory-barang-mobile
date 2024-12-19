package balloh.khabib.ahmad.inventorybarang.Admin

data class ChatMessageAdmin(
    val id: String = "",
    val message: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
