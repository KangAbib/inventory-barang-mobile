package balloh.khabib.ahmad.inventorybarang

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import balloh.khabib.ahmad.inventorybarang.databinding.ActivityPageBinding

class PageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivityPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menggunakan binding untuk setOnClickListener
        binding.btnStarted.setOnClickListener {
            startActivity(Intent(this@PageActivity, LoginActivity::class.java))
        }
    }
}
