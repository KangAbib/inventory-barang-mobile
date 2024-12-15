package balloh.khabib.ahmad.inventorybarang

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import balloh.khabib.ahmad.inventorybarang.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var fullName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var phone: EditText
    private lateinit var registerBtn: Button
    private lateinit var goToLogin: Button
    private lateinit var isStudent: CheckBox
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi FirebaseAuth dan Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        fullName = findViewById(R.id.edtNama)
        email = findViewById(R.id.edtmail)
        password = findViewById(R.id.edtPass)
        phone = findViewById(R.id.edtPhone)
        registerBtn = findViewById(R.id.btnContinue)
        goToLogin = findViewById(R.id.btnSignUp)
        isStudent = findViewById(R.id.CbMhs)

        registerBtn.setOnClickListener {
            if (validateInput()) {
                val userEmail = email.text.toString().trim()
                val userPassword = password.text.toString().trim()
                val userFullName = fullName.text.toString().trim()
                val userPhone = phone.text.toString().trim()

                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Pendaftaran berhasil
                            val userId = auth.currentUser?.uid
                            val userType = when {
                                isStudent.isChecked -> "Mahasiswa"
                                else -> "unknown" // Jika tidak ada yang dipilih
                            }

                            val userData = hashMapOf(
                                "fullName" to userFullName,
                                "email" to userEmail,
                                "phone" to userPhone,
                                "userType" to userType // Menyimpan jenis pengguna
                            )
                            userId?.let {
                                firestore.collection("users").document(it)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "User  data saved to Firestore!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this, LoginActivity::class.java))
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Error saving user data: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            // Jika pendaftaran gagal, tampilkan pesan kesalahan
                            Toast.makeText(
                                this,
                                "Registration failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        goToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    private fun validateInput(): Boolean {
        var isValid = true

        if (fullName.text.toString().isEmpty()) {
            fullName.error = "Full name is required"
            isValid = false
        }

        if (email.text.toString().isEmpty()) {
            email.error = "Email is required"
            isValid = false
        }

        if (password.text.toString().isEmpty()) {
            password.error = "Password is required"
            isValid = false
        }

        if (phone.text.toString().isEmpty()) {
            phone.error = "Phone number is required"
            isValid = false
        }

        return isValid
    }
}


//        // Set onClickListener
//        binding.btnSignUp.setOnClickListener {
//            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
//        }
//    }
//}
