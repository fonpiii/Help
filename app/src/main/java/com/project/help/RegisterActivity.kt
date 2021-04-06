package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.project.help.disabled.RegisterDisabledActivity
import com.project.help.volunteer.RegisterVolunteerActivity

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    //region Global variable
    private lateinit var iconLeft: ImageView
    private lateinit var btnVolunteer: MaterialButton
    private lateinit var btnDisabled: MaterialButton
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnVolunteer = findViewById(R.id.btnVolunteer)
        btnDisabled = findViewById(R.id.btnDisabled)

        btnVolunteer.setOnClickListener(this)
        btnDisabled.setOnClickListener(this)

        //region On init
        setToolbar()
        //endregion On init
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnDisabled -> {
                val intent = Intent(this, RegisterDisabledActivity::class.java)
                startActivity(intent)
            }
            R.id.btnVolunteer -> {
                val intent = Intent(this, RegisterVolunteerActivity::class.java)
                startActivity(intent)
            }
            R.id.iconLeft -> finish()
        }
    }
}