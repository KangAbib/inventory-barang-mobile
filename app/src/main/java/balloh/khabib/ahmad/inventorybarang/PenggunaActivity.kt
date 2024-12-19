package balloh.khabib.ahmad.inventorybarang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import balloh.khabib.ahmad.inventorybarang.Pengguna.ChatFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import balloh.khabib.ahmad.inventorybarang.Pengguna.HomeFragment
import balloh.khabib.ahmad.inventorybarang.Pengguna.PeminjamanFragment
import balloh.khabib.ahmad.inventorybarang.Pengguna.PengembalianFragment

class PenggunaActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengguna)

        // Mengatur Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Mengatur DrawerLayout dan NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Menambahkan toggle untuk membuka/menutup Navigation Drawer
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Menampilkan HomeFragment secara default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        // Menginisialisasi BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Menambahkan listener untuk BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chat -> {
                    // Ganti fragment ke ChatFragment
                    replaceFragment(ChatFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Menangani item yang dipilih di Navigation Drawer
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()

            R.id.nav_barang -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BarangFragment())
                .commit()

            R.id.nav_ruang -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RuangFragment())
                .commit()

            R.id.nav_peminjaman -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PeminjamanFragment())
                .commit()

            R.id.nav_pengembalian -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PengembalianFragment())
                .commit()

            R.id.nav_maps -> {
                val intent = Intent(this, MaplanjutActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_chart -> {
                val intent = Intent(this, ChartActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_about -> {
                GlobalVariables.url = "https://kangabib.github.io/Portofolio/"
                replaceFragment(FragmentWebview())
            }

            R.id.nav_logout -> {
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()  // Menutup AdminActivity
            }
        }

        // Menutup drawer setelah item dipilih
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        // Menutup drawer jika terbuka saat tombol kembali ditekan
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
