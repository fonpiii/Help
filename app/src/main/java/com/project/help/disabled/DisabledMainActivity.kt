package com.project.help.disabled

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.ConstValue
import com.project.help.OtherMenu
import com.project.help.R
import com.project.help.Utilities
import com.project.help.disabled.model.PostAdapter
import com.project.help.disabled.model.PostDetailsResponse
import com.project.help.model.UserDisabledModel
import com.project.help.model.UserVolunteerModel
import com.project.help.volunteer.VolunteerMainActivity


class DisabledMainActivity : AppCompatActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //region Global variable
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetDisabled: LinearLayout
    private lateinit var imgProfile: ImageView
    private lateinit var txtFullName: TextView
    private lateinit var txtUserType: TextView
    private lateinit var otherMenu: CardView
    private lateinit var oldPost: CardView
    private lateinit var thankYouNote: CardView
    private lateinit var archiveOfPosts: CardView
    private lateinit var switchVolunteer: CardView
    private lateinit var iconLeft: ImageView
    private lateinit var ratingReqForHelp: RatingBar
    private lateinit var ratingVolunteerForHelp: RatingBar
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnPost: Button
    private lateinit var btnSos: Button
    private lateinit var recyclerFeed: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var userDisabled: UserDisabledModel
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private val RQ_TELEPHONE = 100
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
        thankYouNote = findViewById(R.id.thankYouNote)
        archiveOfPosts = findViewById(R.id.archiveOfPosts)
        switchVolunteer = findViewById(R.id.switchVolunteer)
        ratingReqForHelp = findViewById(R.id.ratingReqForHelp)
        ratingVolunteerForHelp = findViewById(R.id.ratingVolunteerForHelp)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnPost = findViewById(R.id.btnPost)
        btnSos = findViewById(R.id.btnSos)
        recyclerFeed = findViewById(R.id.recycler_feed)
        shimmer = findViewById(R.id.shimmerFrameLayout)
        mSwipeRefreshLayout = findViewById(R.id.swipe_container)

        archiveOfPosts.setOnClickListener(this)
        oldPost.setOnClickListener(this)
        otherMenu.setOnClickListener(this)
        btnPost.setOnClickListener(this)
        btnSos.setOnClickListener(this)
        thankYouNote.setOnClickListener(this)
        switchVolunteer.setOnClickListener(this)

        //region On init
        setMenuBottomSheet()
        setToolbar()
        setSpinnerCategory()
        setFirebaseDatabase()
        setSwipeRefresh()

//        val postDetail = intent.getSerializableExtra("PostDetail") as? PostDetailsModel
        setHeader()
        mSwipeRefreshLayout.isRefreshing = false
        getPosts(ConstValue.getByAll, "")
        //endregion On init
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

    private fun setHeader() {
        if ((intent.getParcelableExtra("User") as? UserDisabledModel) != null) {
            userDisabled = (intent.getParcelableExtra("User") as? UserDisabledModel)!!
            if (userDisabled.firstName + " " + userDisabled.lastName == "ผู้พิการ 1") {
                imgProfile.setImageResource(R.drawable.user)
            }
            txtFullName.text = userDisabled.firstName + " " + userDisabled.lastName
            ratingReqForHelp.rating = userDisabled.scoreDisabled.toFloat()
            ratingVolunteerForHelp.rating = userDisabled.scoreVolunteer.toFloat()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.archiveOfPosts ->  {
                val intent = Intent(this, ArchiveOfPostsActivity::class.java)
                if (userDisabled != null) {
                    intent.putExtra("User", userDisabled)
                }
                startActivity(intent)
            }
            R.id.thankYouNote -> {
                val intent = Intent(this, ThankYouNoteActivity::class.java)
                if (userDisabled != null) {
                    intent.putExtra("User", userDisabled)
                }
                startActivity(intent)
            }
            R.id.switchVolunteer -> {
                switchVolunteer()
            }
            R.id.oldPost -> {
                val intent = Intent(this, OldPostActivity::class.java)
                if (userDisabled != null) {
                    intent.putExtra("User", userDisabled)
                }
                startActivity(intent)
            }
            R.id.otherMenu -> {
                val intent = Intent(this, OtherMenu::class.java)
                startActivity(intent)
            }
            R.id.btnPost -> {
                val intent = Intent(this, PostActivity::class.java)
                if (userDisabled != null) {
                    intent.putExtra("User", userDisabled)
                }
                startActivity(intent)
            }
            R.id.btnSos -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getPermissionToTelephone(RQ_TELEPHONE)
                }
            }
        }
    }

    private fun switchVolunteer() {
        val alertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        alertDialog.titleText = "คำเตือน ?"
        alertDialog.contentText = "ต้องการเปลี่ยนเป็นอาสา"
        alertDialog.cancelText = "ไม่"
        alertDialog.setCancelClickListener { sDialog -> sDialog.cancel() }
        alertDialog.confirmText = "ใช่"
        alertDialog.setConfirmClickListener { sDialog ->
                var databaseUser= FirebaseDatabase.getInstance().getReference("User")
                databaseUser.child(userDisabled.userId.toString()).child("userType").setValue(ConstValue.UserType_Volunteer).addOnSuccessListener {
                    userDisabled.userType = ConstValue.UserType_Volunteer
                    var intent = Intent(this, VolunteerMainActivity::class.java)
                    intent.putExtra("User", userDisabled)
                    sDialog.dismissWithAnimation()
                    startActivity(intent)
                    finishAffinity()
                }
            }
        alertDialog.show()
        val btnConfirm = alertDialog.findViewById(R.id.confirm_button) as Button
        btnConfirm.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed))
        val btnCancel = alertDialog.findViewById(R.id.cancel_button) as Button
        btnCancel.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlack))
    }

    private fun getPermissionToTelephone(telephoneRequestCode: Int) {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid checking the build version since Context.checkSelfPermission(...) is only available in Marshmallow
        // 2) Always check for permission (even if permission has already been granted) since the user can revoke permissions at any time through Settings
        if (telephoneRequestCode == RQ_TELEPHONE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), telephoneRequestCode)
            } else {
                onClickSos()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RQ_TELEPHONE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onClickSos()
//                Toast.makeText(this, "Call phone permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClickSos() {
        var database = FirebaseDatabase.getInstance().getReference("User")
        database.orderByChild("userType").equalTo(ConstValue.UserType_Volunteer).get().addOnSuccessListener { result ->
            var users = ArrayList<UserVolunteerModel>()
            for (data in result.children) {
                val user: UserVolunteerModel = data.getValue(UserVolunteerModel::class.java)!!
                users.add(user)
            }

            if (users.size > 0) {
                var country = users.filter { s -> s.country!!.toUpperCase() == userDisabled.country!!.toUpperCase() }.toList()

                if (country.isNotEmpty()) {
                    var zone = country.filter { s -> s.zipCode == userDisabled.zipCode }.toList()

                    if (zone.isNotEmpty()) {
                        randomUserAndTel(zone)
                    }
                } else {
                    randomUserAndTel(country)
                }
            } else {
                Toast.makeText(this, "ไม่มีข้อมูลอาสา", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun randomUserAndTel(list: List<UserVolunteerModel>) {
        val random = (list.indices).random()
        val alertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        alertDialog.titleText = "คำเตือน ?"
        alertDialog.contentText = "ต้องการโทรแบบ SOS ใช่หรือไม่"
        alertDialog.cancelText = "ไม่"
        alertDialog.setCancelClickListener { sDialog -> sDialog.cancel() }
        alertDialog.confirmText = "ใช่"
        alertDialog.setConfirmClickListener { sDialog ->
            var telRandom = list[random].telephone
            if (!telRandom.isNullOrEmpty()) {
                startActivity(Utilities.Other.callTelephone(telRandom))
                sDialog.dismissWithAnimation()
            }
        }
        alertDialog.show()
        val btnConfirm = alertDialog.findViewById(R.id.confirm_button) as Button
        btnConfirm.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed))
        val btnCancel = alertDialog.findViewById(R.id.cancel_button) as Button
        btnCancel.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlack))
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

        recyclerFeed.adapter =
                PostAdapter(postDetails, userDisabled)
        recyclerFeed.layoutManager = LinearLayoutManager(this)
        recyclerFeed.setHasFixedSize(true)
        closeShimmer()
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

    private fun setSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }
}