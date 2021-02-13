package com.project.help.disabled

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.help.OtherMenu
import com.project.help.R

class DisabledMainActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetDisabled: LinearLayout
    private lateinit var imgProfile: ImageView
    private lateinit var txtFullName: TextView
    private lateinit var txtUserType: TextView
    private lateinit var otherMenu: CardView
    private lateinit var iconLeft: ImageView
    private lateinit var ratingReqForHelp: RatingBar
    private lateinit var ratingVolunteerForHelp: RatingBar
    private lateinit var spinnerCategory: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disabled_main)
        supportActionBar?.hide()

        //region Set menu bottom sheet
        bottomSheetDisabled = findViewById(R.id.bottom_disabled_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetDisabled)

        bottomSheetBehavior.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, state: Int) {
                print(state)
                when (state) {

                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
        //endregion

        //region Back button
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.visibility = View.INVISIBLE
        //endregion Back button

        //region Set variable
        imgProfile = findViewById(R.id.imgProfile)
        txtFullName = findViewById(R.id.txtFullName)
        txtUserType = findViewById(R.id.txtUserType)
        otherMenu = findViewById(R.id.otherMenu)
        ratingReqForHelp = findViewById(R.id.ratingReqForHelp)
        ratingVolunteerForHelp = findViewById(R.id.ratingVolunteerForHelp)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        //endregion

        //region Action
        otherMenu.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, OtherMenu::class.java)
            startActivity(intent)
        })

        var categories = arrayOf("หมวดหมู่ผู้พิการ", "การมองเห็น", "การได้ยิน", "สติปัญญา")

        spinnerCategory.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories)

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }
        }
        //endregion

        //region On init
        setRating()
        //endregion On init
    }

    private fun setRating() {
//        ratingReqForHelp.rating = 1.0F
//        ratingVolunteerForHelp.rating = 5.0F
    }
}