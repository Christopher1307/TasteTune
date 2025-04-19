package com.example.tastetune

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.tastetune.Api_Music.SpotifyAuth
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Verificar token en SharedPreferences
        val token = SpotifyAuth.loadAccessToken(this)
        Log.d("SpotifyAuth", "Token cargado: $token")

        if (token == null) {
            Toast.makeText(this, "Token no encontrado, lanzando Login", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // ✅ Configurar navegación con BottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)


        // ✅ Botón temporal para cerrar sesión
        findViewById<FloatingActionButton>(R.id.fabLogout).setOnClickListener {
            SpotifyAuth.clearAccessToken(this)
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
}
