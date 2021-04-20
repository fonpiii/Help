package com.project.help.volunteer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.ConstValue
import com.project.help.OtherMenu
import com.project.help.R
import com.project.help.disabled.*
import com.project.help.disabled.model.PostAdapter
import com.project.help.disabled.model.PostDetailsResponse
import com.project.help.disabled.model.PostItem
import com.project.help.model.UserModel

class VolunteerMainActivity : AppCompatActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var user: UserModel
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
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
        shimmer = findViewById(R.id.shimmerFrameLayout)
        mSwipeRefreshLayout = findViewById(R.id.swipe_container)

        archiveOfPosts.setOnClickListener(this)
        postsHelped.setOnClickListener(this)
        otherMenu.setOnClickListener(this)

        //region On init
        setMenuBottomSheet()
        setToolbar()
        setSpinnerCategory()
        setFirebaseDatabase()
        setSwipeRefresh()

        setHeader()
        mSwipeRefreshLayout.isRefreshing = false
        getPosts(ConstValue.getByAll, "")
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

    private fun setHeader() {
        if ((intent.getParcelableExtra("User") as? UserModel) != null) {
            user = (intent.getParcelableExtra("User") as? UserModel)!!
            txtFullName.text = user.firstName + " " + user.lastName
            ratingReqForHelp.rating = user.scoreDisabled.toFloat()
            ratingVolunteerForHelp.rating = user.scoreVolunteer.toFloat()
        }
    }

    override fun onResume() {
        shimmer.startShimmerAnimation()
        setHeader()
        super.onResume()
    }

    override fun onPause() {
        shimmer.stopShimmerAnimation()
        setHeader()
        super.onPause()
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("PostDetails")
    }

    private fun getPosts(getBy: String, value: String) {
        shimmer.startShimmerAnimation()
        when (getBy) {
            ConstValue.getByAll -> {
                reference.get().addOnSuccessListener { result ->
                    setPostDetails(result)
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
            }
            ConstValue.getByCategory -> {
                reference.orderByChild("categorys").equalTo(value).get().addOnSuccessListener { result ->
                    setPostDetails(result)
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
            }
        }
    }

    private fun setPostDetails(result: DataSnapshot) {
        var postDetails = ArrayList<PostDetailsResponse>()
        for (data in result.children) {
            var postDetail: PostDetailsResponse = data.getValue(PostDetailsResponse::class.java)!!
            postDetail.id = data.key.toString()
            postDetails.add(postDetail)
        }

        // Sort postDetails by date
        postDetails.sortByDescending { it.createDate }

        if (postDetails.size != 0) {
            recyclerFeed.adapter =
                    PostAdapter(postDetails, user)
            recyclerFeed.layoutManager = LinearLayoutManager(this)
            recyclerFeed.setHasFixedSize(true)
            closeShimmer()
        } else {
            closeShimmer()
        }
    }

    private fun closeShimmer() {
        mSwipeRefreshLayout.isRefreshing = false
        shimmer.stopShimmerAnimation()
        shimmer.visibility = View.GONE
        recyclerFeed.visibility = View.VISIBLE
    }

    override fun onRefresh() {
        getPosts(ConstValue.getByAll, "")
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

    private fun setSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private fun setSpinnerCategory() {
        var categories = arrayOf("ทั้งหมด", "การมองเห็น", "การได้ยิน", "การเคลื่อนไหวร่างกาย", "สติปัญญา",
                "ออทิสติก", "ผู้สูงอายุ")

        var adapter = ArrayAdapter(this, R.layout.color_spinner_layout, categories)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    getPosts(ConstValue.getByAll, "")
                } else {
                    getPosts(ConstValue.getByCategory, categories[p2])
                }
            }
        }
    }
}