package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.project.help.disabled.DisabledMainActivity
import com.project.help.volunteer.VolunteerMainActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnUser: Button
    lateinit var btnDisabled: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        btnUser = findViewById(R.id.btnUser)
        btnDisabled = findViewById(R.id.btnDisabled)

        btnDisabled.setOnClickListener(this)
        btnUser.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnDisabled -> {
                val intent = Intent(this, DisabledMainActivity::class.java)
                startActivity(intent)
            }
            R.id.btnUser -> {
                val intent = Intent(this, VolunteerMainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}