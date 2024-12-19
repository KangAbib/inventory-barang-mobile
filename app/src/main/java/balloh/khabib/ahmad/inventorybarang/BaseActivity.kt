package balloh.khabib.ahmad.inventorybarang

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var currentLanguage: String = Locale.getDefault().language

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Firebase Remote Config
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Default values
        remoteConfig.setDefaultsAsync(mapOf(
            "font_color" to "#000000",
            "font_size" to 16
        ))

        fetchRemoteConfig()
    }

    override fun onResume() {
        super.onResume()

        // Deteksi perubahan bahasa
        val newLanguage = Locale.getDefault().language
        if (newLanguage != currentLanguage) {
            currentLanguage = newLanguage
            fetchRemoteConfig()
        }
    }

    private fun fetchRemoteConfig() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    applyConfigToAllTextViews()
                } else {
                    applyConfigToAllTextViews()
                }
            }
    }

    private fun applyConfigToAllTextViews() {
        val fontColor = remoteConfig.getString("font_color")
        val fontSize = remoteConfig.getLong("font_size").toFloat()

        // Iterasi semua TextView di layout
        val rootView = window.decorView.rootView
        applyConfigRecursively(rootView, fontColor, fontSize)
    }

    private fun applyConfigRecursively(view: android.view.View, fontColor: String, fontSize: Float) {
        if (view is TextView) {
            view.setTextColor(Color.parseColor(fontColor))
            view.textSize = fontSize
        } else if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                applyConfigRecursively(view.getChildAt(i), fontColor, fontSize)
            }
        }
    }
}