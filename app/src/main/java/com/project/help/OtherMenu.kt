package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView

class OtherMenu : AppCompatActivity() {

    private lateinit var iconLeft: ImageView
    private lateinit var contactUsMenu: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_menu)

        //region Back button
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(View.OnClickListener {
            finish()
        })
        //endregion Back button

        //region Set variable
        contactUsMenu = findViewById(R.id.contactUs)
        //endregion

        //region Action
        contactUsMenu.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ContactUs::class.java)
            startActivity(intent)
        })
        //endregion
    }
}