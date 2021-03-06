package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class WelcomeActivity : AppCompatActivity() {

    //region Global variable
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        //region Set variable
        btnLogin = findViewById(R.id.btnLogin_Welcome)
        btnRegister = findViewById(R.id.btnRegister)
        //endregion Set variable

        //region Action
        btnLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })

        btnRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })
        //endregion Action
    }
}