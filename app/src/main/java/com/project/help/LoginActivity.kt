package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.project.help.disabled.DisabledMainActivity

class LoginActivity : AppCompatActivity() {

    lateinit var btnUser: Button
    lateinit var btnDisabled: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        btnUser = findViewById(R.id.btnUser)
        btnDisabled = findViewById(R.id.btnDisabled)

        btnDisabled.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, DisabledMainActivity::class.java)
            startActivity(intent)
        })

        btnUser.setOnClickListener(View.OnClickListener {
            // Something
        })
    }
}