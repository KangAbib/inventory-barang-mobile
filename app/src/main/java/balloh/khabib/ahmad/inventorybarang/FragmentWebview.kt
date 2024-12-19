package balloh.khabib.ahmad.inventorybarang


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import balloh.khabib.ahmad.inventorybarang.databinding.FragmentWebviewBinding

class FragmentWebview : Fragment() {
    lateinit var b: FragmentWebviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentWebviewBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        b.webV.loadUrl(GlobalVariables.url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        b.webV.settings.apply {
            domStorageEnabled = true
            displayZoomControls = false
            useWideViewPort = true
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            setGeolocationEnabled(true)
            allowContentAccess = true
            loadsImagesAutomatically = true
        }
        b.webV.webViewClient = WebViewClient()
    }
}