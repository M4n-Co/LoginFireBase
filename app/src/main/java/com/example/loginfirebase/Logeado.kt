package com.example.loginfirebase

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.loginfirebase.databinding.ActivityLogeadoBinding
import com.example.loginfirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC,
    GOOGLE
}

class Logeado : AppCompatActivity() {
    lateinit var binding: ActivityLogeadoBinding

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogeadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Inicio"
        val bundle = intent.extras
        val emailR = bundle?.getString(MainActivity.EMAIL_KEY)
        val providerR = bundle?.getString(MainActivity.PROVIDER_KEY)

        //guardar datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString(MainActivity.EMAIL_KEY, emailR)
        prefs.putString(MainActivity.PROVIDER_KEY, providerR)
        prefs.apply()

        binding.tvEmail.text = emailR
        binding.tvProveedor.text = providerR

        binding.btCerrar.setOnClickListener {
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }
}