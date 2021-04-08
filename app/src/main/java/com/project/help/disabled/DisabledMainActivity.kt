package com.project.help.disabled

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.OtherMenu
import com.project.help.R
import com.project.help.disabled.model.PostDetails
import com.project.help.disabled.model.PostDetailsResponse
import com.project.help.model.UserModel


class DisabledMainActivity : AppCompatActivity(), View.OnClickListener {

    //region Global variable
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetDisabled: LinearLayout
    private lateinit var imgProfile: ImageView
    private lateinit var txtFullName: TextView
    private lateinit var txtUserType: TextView
    private lateinit var otherMenu: CardView
    private lateinit var oldPost: CardView
    private lateinit var archiveOfPosts: CardView
    private lateinit var iconLeft: ImageView
    private lateinit var ratingReqForHelp: RatingBar
    private lateinit var ratingVolunteerForHelp: RatingBar
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnPost: Button
    private lateinit var recyclerFeed: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var user: UserModel
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disabled_main)
        supportActionBar?.hide()

        imgProfile = findViewById(R.id.imgProfile)
        txtFullName = findViewById(R.id.txtFullName)
        txtUserType = findViewById(R.id.txtUserType)
        otherMenu = findViewById(R.id.otherMenu)
        oldPost = findViewById(R.id.oldPost)
        archiveOfPosts = findViewById(R.id.archiveOfPosts)
        ratingReqForHelp = findViewById(R.id.ratingReqForHelp)
        ratingVolunteerForHelp = findViewById(R.id.ratingVolunteerForHelp)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnPost = findViewById(R.id.btnPost)
        recyclerFeed = findViewById(R.id.recycler_feed)

        archiveOfPosts.setOnClickListener(this)
        oldPost.setOnClickListener(this)
        otherMenu.setOnClickListener(this)
        btnPost.setOnClickListener(this)

        //region On init
        setMenuBottomSheet()
        setToolbar()
        setSpinnerCategory()
        setFirebaseDatabase()

//        val postDetail = intent.getSerializableExtra("PostDetail") as? PostDetailsModel
        setHeader()
        getPosts()
        //endregion On init
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("PostDetails")
    }

    private fun setHeader() {
        if ((intent.getParcelableExtra("User") as? UserModel) != null) {
            user = (intent.getParcelableExtra("User") as? UserModel)!!
            txtFullName.text = user.firstName + " " + user.lastName
            ratingReqForHelp.rating = user.scoreDisabled.toFloat()
            ratingVolunteerForHelp.rating = user.scoreVolunteer.toFloat()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.archiveOfPosts ->  {
                val intent = Intent(this, ArchiveOfPostsActivity::class.java)
                startActivity(intent)
            }
            R.id.oldPost -> {
                val intent = Intent(this, OldPostActivity::class.java)
                startActivity(intent)
            }
            R.id.otherMenu -> {
                val intent = Intent(this, OtherMenu::class.java)
                startActivity(intent)
            }
            R.id.btnPost -> {
                val intent = Intent(this, PostActivity::class.java)
                if (user != null) {
                    intent.putExtra("User", user)
                }
                startActivity(intent)
            }
        }
    }

    private fun setMenuBottomSheet() {
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
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.visibility = View.INVISIBLE
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
        reference.get().addOnSuccessListener { result ->
            var postDetails = ArrayList<PostDetailsResponse>()
            for (data in result.children) {
                var postDetail: PostDetailsResponse = data.getValue(PostDetailsResponse::class.java)!!
                postDetail.id = data.key.toString()
                postDetails.add(postDetail)
            }

            // Sort postDetails by date
            postDetails.sortByDescending { it.createDate }

            // Set for hide
            var postDetail = PostDetailsResponse()
            postDetails.add(postDetail)

            if (postDetails.size != 0) {
                recyclerFeed.adapter = PostAdapter(postDetails)
                recyclerFeed.layoutManager = LinearLayoutManager(this)
                recyclerFeed.setHasFixedSize(true)
            }
//            Toast.makeText(this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun addPostList(postList: ArrayList<PostItem>, postDetails: PostDetails): ArrayList<PostItem> {
        var index = postList.size - 1
        postList.add(index, PostItem(R.drawable.privacypolicy, "User " + (index+1).toString(),
            postDetails.txtPost.toString(), index+1, 5.0F, 0))
        return postList
    }
}