package balloh.khabib.ahmad.inventorybarang

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import balloh.khabib.ahmad.inventorybarang.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var gotoRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi FirebaseAuth dan Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        email = findViewById(R.id.edtEmail)
        password = findViewById(R.id.edtPassword)
        loginBtn = findViewById(R.id.btnLogin)
        gotoRegister = findViewById(R.id.btnRegister)
        loginBtn.setOnClickListener {
            if (validateFields()) {
                val userEmail = email.text.toString().trim()
                val userPassword = password.text.toString().trim()

                // Proses login
                auth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login berhasil, ambil userId
                            val userId = auth.currentUser ?.uid
                            userId?.let {
                                // Cek jenis pengguna di Firestore
                                firestore.collection("users").document(it)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            val userType = document.getString("userType")
                                            when (userType) {
                                                "Admin" -> {
                                                    // Jika pengguna adalah guru
                                                    startActivity(Intent(this, AdminActivity::class.java))
                                                }
                                                "Mahasiswa" -> {
                                                    // Jika pengguna adalah Mahasiswa
                                                    startActivity(Intent(this, PenggunaActivity::class.java))
                                                }
                                                else -> {
                                                    Toast.makeText(this, "User  type not recognized", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error getting user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            // Jika login gagal, tampilkan pesan kesalahan
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        gotoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (email.text.toString().isEmpty()) {
            email.error = "Email is required"
            isValid = false
        }

        if (password.text.toString().isEmpty()) {
            password.error = "Password is required"
            isValid = false
        }

        return isValid
    }


}
