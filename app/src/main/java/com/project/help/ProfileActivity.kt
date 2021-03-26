package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.project.help.disabled.DisabledMainActivity
import com.project.help.disabled.model.PostDetails

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconLeft: ImageView
    private lateinit var signOut: TextView
    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        signOut = findViewById(R.id.signOut)
        mAuth = FirebaseAuth.getInstance()

        signOut.setOnClickListener(this)

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
            R.id.signOut -> {
                mAuth!!.signOut();
                startActivity(Intent(this, WelcomeActivity::class.java))
                finishAffinity()
            }
            R.id.iconLeft -> finish()
        }
    }
}