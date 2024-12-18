package balloh.khabib.ahmad.inventorybarang

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.material.chip.Chip

class MaplanjutActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var editText: EditText
    private val arrayPolyl = mutableListOf<LatLng>()
    private var isLiveUpdateEnabled = false
    private var currentPolyline: PolylineOptions? = null
    private var currentCircle: CircleOptions? = null
    private var currentPolygon: PolygonOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maplanjut)

        // Inisialisasi Map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inisialisasi EditText
        editText = findViewById(R.id.editText)

        // Menambahkan koordinat ke arrayPolyl (jika diperlukan untuk marker)
        arrayPolyl.add(LatLng(-7.800837, 111.979834)) // Titik pertama

        // Menangani aksi Live Update Chip
        val chip = findViewById<Chip>(R.id.chip)
        chip.setOnClickListener {
            isLiveUpdateEnabled = !isLiveUpdateEnabled
            if (isLiveUpdateEnabled) {
                chip.text = "Stop Live Update"
                startLocationUpdates()
            } else {
                chip.text = "Live Update"
                stopLocationUpdates()
            }
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabMapDrawPolygon).setOnClickListener {
            drawPolygon()
        }
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabMapDrawCircle).setOnClickListener {
            drawCircle()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Cek izin lokasi
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Minta izin lokasi
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
            return
        }

        // Aktifkan lokasi pengguna
        googleMap.isMyLocationEnabled = true

        val firstPoint = arrayPolyl[0]
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 15f))
    }

    private fun updateLocationToEditText() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Minta izin lokasi
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
            return
        }

        val fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                editText.setText("Lat: $latitude, Lng: $longitude")
            } else {
                Toast.makeText(this, "Tidak dapat mendapatkan lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startLocationUpdates() {
        googleMap.isMyLocationEnabled = true
        updateLocationToEditText()
        Toast.makeText(this, "Live Update Activated", Toast.LENGTH_SHORT).show()
    }

    private fun stopLocationUpdates() {
        googleMap.isMyLocationEnabled = false
        Toast.makeText(this, "Live Update Stopped", Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk menggambar polygon
    private fun drawPolygon() {
        if (currentCircle != null) {
            googleMap.clear()
            currentCircle = null
        }

        val polygonOptions = PolygonOptions()
            .add(LatLng(-7.800973, 111.980516))
            .add(LatLng(-7.803243, 111.980698))
            .add(LatLng(-7.802792, 111.979045))
            .add(LatLng(-7.800973, 111.980516))
            .fillColor(android.graphics.Color.argb(100, 255, 255, 0))
            .strokeColor(android.graphics.Color.YELLOW)
            .strokeWidth(5f)

        googleMap.addPolygon(polygonOptions)
        currentPolygon = polygonOptions
    }

    private fun drawCircle() {
        if (currentPolygon != null) {
            googleMap.clear()
            currentPolyline = null
        }

        val circleOptions = CircleOptions()
            .center(LatLng(-7.802273, 111.980061))
            .radius(150.0)
            .strokeColor(android.graphics.Color.RED)
            .fillColor(android.graphics.Color.argb(50, 150, 0, 0))

        googleMap.addCircle(circleOptions)
        currentCircle = circleOptions
    }
}