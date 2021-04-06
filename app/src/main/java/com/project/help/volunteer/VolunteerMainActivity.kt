package com.project.help.volunteer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.help.OtherMenu
import com.project.help.R
import com.project.help.disabled.*
import com.project.help.disabled.model.PostDetails

class VolunteerMainActivity : AppCompatActivity(), View.OnClickListener {

    //region Global variable
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetDisabled: LinearLayout
    private lateinit var iconLeft: ImageView
    private lateinit var imgProfile: ImageView
    private lateinit var txtFullName: TextView
    private lateinit var txtUserType: TextView
    private lateinit var otherMenu: CardView
    private lateinit var rating: CardView
    private lateinit var postsHelped: CardView
    private lateinit var disabled: CardView
    private lateinit var archiveOfPosts: CardView
    private lateinit var ratingReqForHelp: RatingBar
    private lateinit var ratingVolunteerForHelp: RatingBar
    private lateinit var spinnerCategory: Spinner
    private lateinit var recyclerFeed: RecyclerView
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_main)

        imgProfile = findViewById(R.id.imgProfile)
        txtFullName = findViewById(R.id.txtFullName)
        txtUserType = findViewById(R.id.txtUserType)
        otherMenu = findViewById(R.id.otherMenu)
        rating = findViewById(R.id.rating)
        disabled = findViewById(R.id.disabled)
        postsHelped = findViewById(R.id.postsHelped)
        archiveOfPosts = findViewById(R.id.archiveOfPosts)
        ratingReqForHelp = findViewById(R.id.ratingReqForHelp)
        ratingVolunteerForHelp = findViewById(R.id.ratingVolunteerForHelp)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        recyclerFeed = findViewById(R.id.recycler_feed)

        archiveOfPosts.setOnClickListener(this)
        postsHelped.setOnClickListener(this)
        otherMenu.setOnClickListener(this)

        //region On init
        setMenuBottomSheet()
        setToolbar()
        setRatingUserHeader()
        setSpinnerCategory()

        getPosts()
        //endregion On init
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.archiveOfPosts ->  {
                val intent = Intent(this, ArchiveOfPostsActivity::class.java)
                startActivity(intent)
            }
            R.id.postsHelped -> {
                val intent = Intent(this, OldPostActivity::class.java)
                startActivity(intent)
            }
            R.id.otherMenu -> {
                val intent = Intent(this, OtherMenu::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setMenuBottomSheet() {
        bottomSheetDisabled = findViewById(R.id.bottom_volunteer_sheet)
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
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.visibility = View.INVISIBLE
    }

    private fun setRatingUserHeader() {
//        ratingReqForHelp.rating = 1.0F
//        ratingVolunteerForHelp.rating = 5.0F
    }

    private fun setSpinnerCategory() {
        var categories = arrayOf("หมวดหมู่ผู้พิการ", "การมองเห็น", "การได้ยิน", "การเคลื่อนไหวร่างกาย", "สติปัญญา",
                "ออทิสติก", "ผู้สูงอายุ")

        var adapter = ArrayAdapter(this, R.layout.color_spinner_layout, categories)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }
        }
    }

    private fun getPosts() {
        var postList = generateDummyListPost(10)

//        recyclerFeed.adapter = PostAdapter(postList)
//        recyclerFeed.layoutManager = LinearLayoutManager(this)
//        recyclerFeed.setHasFixedSize(true)
    }

    private fun generateDummyListPost(size: Int): ArrayList<PostItem> {
        val list = ArrayList<PostItem>()

        for (i in 0 until size) {
            val drawable = when (i % 3) {
                0 -> R.drawable.privacypolicy
                1 -> R.drawable.hospital
                else -> R.drawable.helplogo
            }

            // Set content feed
            val item = PostItem(drawable, "User " + (i+1).toString(),
                    "ช่วยอ่านใบนัดหมอให้ทีครับ", (i+1), 3.0F, 0)
            list += item
        }
        list += PostItem(R.drawable.privacypolicy, "",
                "", 999999, 3.0F, 0)
        return list
    }
}