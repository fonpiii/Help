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

class PostActivity : AppCompatActivity(), View.OnClickListener {

    //region Global variable
    private lateinit var iconLeft: ImageView
    private lateinit var editPost: EditText
    private lateinit var btnPost: TextView
    private lateinit var btnIsAdvice: LinearLayout
    private lateinit var itemAdvice: TextView
    private var isAdvice: Boolean = false
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        editPost = findViewById(R.id.editPost_Post)
        btnPost = findViewById(R.id.btnPost_Post)
        btnIsAdvice = findViewById(R.id.btnIsAdvice)
        itemAdvice = findViewById(R.id.itemAdvice)

        editPost.setOnClickListener(this)
        btnPost.setOnClickListener(this)
        btnIsAdvice.setOnClickListener(this)

        //region On init
        setToolbar()
        //endregion On init

        editPost.doAfterTextChanged {
            if (editPost.length() > 0) {
                btnPost.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                btnPost.setTextColor(ContextCompat.getColor(this, R.color.lightGray));
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editPost_Post -> {
                openSoftKeyboard(this, editPost)
            }
            R.id.btnPost_Post -> {
                if (editPost.length() > 0) {
                    val postDetail = PostDetails()
                    Log.d("Test", editPost.text.toString())
                    postDetail.txtPost = editPost.text.toString()
                    postDetail.isAdvice = isAdvice
                    val intent = launchNextScreen(this, postDetail)
                    startActivity(intent)
                }
            }
            R.id.btnIsAdvice -> {
                if (!isAdvice) {
                    itemAdvice.visibility = View.VISIBLE
                    isAdvice = true
                } else {
                    itemAdvice.visibility = View.GONE
                    isAdvice = false
                }
            }
            R.id.iconLeft -> finish()
        }
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(this)
    }

    private fun launchNextScreen(context: Context, postDetails: PostDetails): Intent {
        val intent = Intent(context, DisabledMainActivity::class.java)
        intent.putExtra("PostDetail", postDetails as Serializable)
        return intent
    }

    private fun openSoftKeyboard(context: Context, view: View) {
        view.requestFocus()
        // open the soft keyboard
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}