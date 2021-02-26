package com.project.help.disabled

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.project.help.R
import com.project.help.disabled.model.PostDetails
import java.io.Serializable

class PostActivity : AppCompatActivity() {

    //region Global variable
    private lateinit var iconLeft: ImageView
    private lateinit var editPost: EditText
    private lateinit var btnPost: TextView
    private lateinit var btnIsAdvice: LinearLayout
    private lateinit var itemAdvice: TextView
    private var isAdvice: Boolean = false
    //endregion Global variable

    private fun launchNextScreen(context: Context, postDetails: PostDetails): Intent {
        val intent = Intent(context, DisabledMainActivity::class.java)
        intent.putExtra("PostDetail", postDetails as Serializable)
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        //region Back button
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(View.OnClickListener {
            finish()
        })
        //endregion Back buttonbtnPost_Post

        //region Set variable
        editPost = findViewById(R.id.editPost_Post)
        btnPost = findViewById(R.id.btnPost_Post)
        btnIsAdvice = findViewById(R.id.btnIsAdvice)
        itemAdvice = findViewById(R.id.itemAdvice)
        //endregion Set variable

        //region Action
        editPost.setOnClickListener(View.OnClickListener {
            openSoftKeyboard(this, editPost)
        })

        editPost.doAfterTextChanged {
            if (editPost.length() > 0) {
                btnPost.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                btnPost.setTextColor(ContextCompat.getColor(this, R.color.lightGray));
            }
        }

        btnPost.setOnClickListener(View.OnClickListener {
            if (editPost.length() > 0) {
                val postDetail = PostDetails()
                Log.d("Test", editPost.text.toString())
                postDetail.txtPost = editPost.text.toString()
                postDetail.isAdvice = isAdvice
                val intent = launchNextScreen(this, postDetail)
                startActivity(intent)
            }
        })

        btnIsAdvice.setOnClickListener(View.OnClickListener {
            if (!isAdvice) {
                itemAdvice.visibility = View.VISIBLE
                isAdvice = true
            } else {
                itemAdvice.visibility = View.GONE
                isAdvice = false
            }
        })
        //endregion Action
    }

    private fun openSoftKeyboard(context: Context, view: View) {
        view.requestFocus()
        // open the soft keyboard
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}