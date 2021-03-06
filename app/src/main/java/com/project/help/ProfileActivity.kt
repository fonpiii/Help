package com.project.help

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var iconLeft: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //region Back button
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(View.OnClickListener {
            finish()
        })
        //endregion Back button
    }
}