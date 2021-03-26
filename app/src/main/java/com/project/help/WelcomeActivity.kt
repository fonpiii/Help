package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.project.help.disabled.DisabledMainActivity

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    //region Global variable
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    var mAuth: FirebaseAuth? = null
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        btnLogin = findViewById(R.id.btnLogin_Welcome)
        btnRegister = findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener(this)
        btnRegister.setOnClickListener(this)

        checkSessionLogin()
    }

    private fun checkSessionLogin() {
        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this, DisabledMainActivity::class.java))
            finish()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnLogin_Welcome -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.btnRegister -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }
}