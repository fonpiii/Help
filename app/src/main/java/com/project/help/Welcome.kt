package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.project.help.disabled.DisabledMainActivity
import com.project.help.disabled.model.PostDetails

class Welcome : AppCompatActivity() {

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
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        })
        //endregion Action
    }
}