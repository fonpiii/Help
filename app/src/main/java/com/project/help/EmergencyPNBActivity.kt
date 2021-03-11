package com.project.help

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class EmergencyPNBActivity : AppCompatActivity() {

    private lateinit var iconLeft: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_p_n_b)

        //region On init
        setToolbar()
        //endregion On init
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}