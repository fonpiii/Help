package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.project.help.disabled.model.PostDetails

class OtherMenu : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconLeft: ImageView
    private lateinit var contactUsMenu: CardView
    private lateinit var profileMenu: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_menu)

        contactUsMenu = findViewById(R.id.contactUs)
        profileMenu = findViewById(R.id.profile)

        contactUsMenu.setOnClickListener(this)
        profileMenu.setOnClickListener(this)

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
            R.id.contactUs -> {
                val intent = Intent(this, ContactUsActivity::class.java)
                startActivity(intent)
            }
            R.id.profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.iconLeft -> finish()
        }
    }
}