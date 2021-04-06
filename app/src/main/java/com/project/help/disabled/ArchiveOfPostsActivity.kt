package com.project.help.disabled

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

class ArchiveOfPostsActivity : AppCompatActivity(), View.OnClickListener {

    //region Global variable
    private lateinit var recyclerFeed: RecyclerView
    private lateinit var iconLeft: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var titleActivity: TextView
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_old_post)

        recyclerFeed = findViewById(R.id.recycler_feed)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        titleActivity = findViewById(R.id.titleActivity)

        //region On init
        setToolbar()
        setTitleName()
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
            R.id.iconLeft -> finish()
        }
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(this)
    }

    private fun getPosts() {
        var postList = generateDummyListPost(5)

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

            list += if (i == 2) {
                val item = PostItem(drawable, "User " + (i+1).toString(),
                        "ช่วยอ่านใบนัดหมอให้ทีครับ", (i+1), 3.0F, R.drawable.gmail)
                item
            } else {
                // Set content feed
                val item = PostItem(drawable, "User " + (i+1).toString(),
                        "ช่วยอ่านใบนัดหมอให้ทีครับ", (i+1), 3.0F, 0)
                item
            }
        }
        return list
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

    private fun setTitleName() {
        titleActivity.text = "คลังของโพสต์"
    }
}