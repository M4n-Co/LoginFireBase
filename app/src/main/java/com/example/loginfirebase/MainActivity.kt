package com.example.loginfirebase

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.loginfirebase.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    companion object{
        const val EMAIL_KEY = "EMAIL"
        const val PROVIDER_KEY = "PROVIDER"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.titulo)

        evento()

        session()

        binding.btRegistrar.setOnClickListener {
            registrar()
        }

        binding.btIniciar.setOnClickListener {
            iniciarSesion()
        }

        binding.btIniciarG.setOnClickListener {
            iniciarSesionGoogle()
        }
    }

    private fun iniciarSesionGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()

        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showLogeado(account.email ?: "", ProviderType.GOOGLE)
                        } else {
                            showError()
                        }
                    }
                }
            }catch (e: ApiException){
                showError()
            }
        }
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val emailS = prefs.getString(EMAIL_KEY, null)
        val providesS = prefs.getString(PROVIDER_KEY, null)

        if(emailS != null && providesS != null){
            showLogeado(emailS, ProviderType.valueOf(providesS))
        }
    }

    private fun evento() {
        val analytic = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion completa")
        analytic.logEvent("InitScreen",bundle)
    }

    private fun iniciarSesion() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                if (it.isSuccessful){
                    showLogeado(it.result.user?.email ?: "", ProviderType.BASIC)
                }else{
                    showError()
                }
            }
        }
    }

    private fun registrar() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()){
            if (pass.length>=6) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showLogeado(it.result.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showError()
                    }
                }
            }else{
                showErrorPass()
            }
        }
    }

    private fun showLogeado(email:String, provider:ProviderType) {

        val intent = Intent(this, Logeado::class.java).apply {
            putExtra(EMAIL_KEY,email)
            putExtra(PROVIDER_KEY,provider.name)
        }
        binding.etEmail.setText("")
        binding.etPass.setText("")

        startActivity(intent)
    }

    private fun showError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.error))
        builder.setMessage(getString(R.string.mensaje))
        builder.setPositiveButton(getString(R.string.aceptar),null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showErrorPass() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.error))
        builder.setMessage(getString(R.string.mensajePass))
        builder.setPositiveButton(getString(R.string.aceptar),null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}