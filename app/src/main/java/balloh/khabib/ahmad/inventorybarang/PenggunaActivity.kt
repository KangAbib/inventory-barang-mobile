package balloh.khabib.ahmad.inventorybarang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import balloh.khabib.ahmad.inventorybarang.Pengguna.HomeFragment
import com.google.android.material.navigation.NavigationView

class PenggunaActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

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

        // Menampilkan balloh.khabib.ahmad.inventorybarang.Pengguna.HomeFragment secara default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_home)
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

            R.id.nav_about -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment())
                .commit()

            R.id.nav_logout -> {
                // Logika logout, misalnya menghapus session atau kembali ke login screen
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
                // Misalnya, kembali ke Activity login
                startActivity(Intent(this, LoginActivity::class.java))
                finish()  // Menutup AdminActivity
            }
        }

        // Menutup drawer setelah item dipilih
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
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
